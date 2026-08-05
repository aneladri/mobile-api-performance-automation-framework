#!/usr/bin/env python3
from __future__ import annotations
import argparse, hashlib, json, os, shutil, subprocess, sys, tempfile, zipfile
from datetime import datetime, timezone
from pathlib import Path

VERSION = "2.9.0"
RELEASE_NAME = f"MAPAF-Enterprise-v{VERSION}-GA"
EXCLUDED_PARTS = {
    ".git", ".gradle", ".idea", ".mapaf-backups", ".mapaf-patches",
    "node_modules", "__pycache__", ".DS_Store"
}
EXCLUDED_TOP = {"releases"}


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def run(cmd: list[str], cwd: Path) -> None:
    subprocess.run(cmd, cwd=cwd, check=True)


def include_path(rel: Path) -> bool:
    if not rel.parts:
        return True
    if rel.parts[0] in EXCLUDED_TOP:
        return False
    return not any(part in EXCLUDED_PARTS for part in rel.parts)


def copy_framework(root: Path, dest: Path) -> list[dict]:
    manifest = []
    for src in sorted(root.rglob("*")):
        rel = src.relative_to(root)
        if not include_path(rel):
            continue
        if src.is_symlink():
            continue
        target = dest / rel
        if src.is_dir():
            target.mkdir(parents=True, exist_ok=True)
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, target)
        manifest.append({"path": str(Path("framework") / rel), "size": src.stat().st_size, "sha256": sha256(src)})
    return manifest


def copy_if_exists(src: Path, dst: Path) -> bool:
    if src.is_file():
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, dst)
        return True
    return False


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=".")
    parser.add_argument("--skip-validation", action="store_true")
    args = parser.parse_args()
    root = Path(args.root).resolve()
    version_file = root / "MAPAF_VERSION"
    if not version_file.is_file() or version_file.read_text().strip() != VERSION:
        raise SystemExit(f"Expected MAPAF_VERSION={VERSION}")

    if not args.skip_validation:
        run(["./gradlew", "gaReleaseValidation"], root)

    out_dir = root / "releases" / f"v{VERSION}"
    out_dir.mkdir(parents=True, exist_ok=True)
    zip_path = out_dir / f"{RELEASE_NAME}.zip"
    checksum_path = out_dir / f"{RELEASE_NAME}.zip.sha256"
    external_manifest = out_dir / f"MAPAF-v{VERSION}-Release-Manifest.json"

    with tempfile.TemporaryDirectory(prefix="mapaf-ga-") as td:
        stage = Path(td) / RELEASE_NAME
        framework = stage / "framework"
        docs = stage / "docs"
        samples = stage / "samples"
        manifests = stage / "manifests"
        installers = stage / "installers"
        for p in [framework, docs, samples, manifests, installers]:
            p.mkdir(parents=True, exist_ok=True)

        files = copy_framework(root, framework)
        release_script_root = root / "scripts" / "release" / "installers"
        for name in ["install.sh", "upgrade-from-v2.8.sh", "rollback.sh", "validate.sh"]:
            copy_if_exists(release_script_root / name, installers / name)
            if (installers / name).exists():
                (installers / name).chmod(0o755)

        guide_root = root / "docs" / "guides"
        release_root = root / "docs" / "releases"
        for source in sorted(guide_root.glob("MAPAF-v2.9-*")) + sorted(release_root.glob("MAPAF-v2.9-*")):
            if source.is_file():
                shutil.copy2(source, docs / source.name)

        sample_map = {
            root / "dashboard/reports/doctor/doctor-report.html": samples / "doctor-report.html",
            root / "dashboard/reports/doctor/doctor-overview.html": samples / "doctor-overview.html",
            root / "dashboard/reports/doctor/doctor-diagnosis.html": samples / "doctor-diagnosis.html",
            root / "dashboard/reports/doctor/executive-report.pdf": samples / "executive-report.pdf",
            root / "dashboard/reports/release-readiness.html": samples / "release-readiness.html",
            root / "dashboard/reports/index.html": samples / "command-center.html",
        }
        samples_included = [str(dst.relative_to(stage)) for src, dst in sample_map.items() if copy_if_exists(src, dst)]

        generated_at = datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
        release_manifest = {
            "schemaVersion": "mapaf.release-package/v1",
            "product": "MAPAF Enterprise Quality Engineering Platform",
            "version": VERSION,
            "release": "GA",
            "releaseName": RELEASE_NAME,
            "generatedAt": generated_at,
            "frameworkFiles": len(files),
            "samples": samples_included,
            "contracts": [
                "mapaf.release-readiness/v3", "mapaf.executive-decision/v1",
                "mapaf.doctor/v1", "mapaf.doctor.executive/v1",
                "mapaf.doctor.diagnosis/v1", "mapaf.doctor.executive-report/v1"
            ],
            "capabilities": [
                "Mobile Quality Engineering", "Web Quality Engineering", "API Quality Engineering",
                "Performance Engineering", "Release Readiness", "MAPAF Doctor",
                "Executive Dashboard", "Diagnosis Center", "Doctor CLI", "Executive Reports"
            ]
        }
        (manifests / "release-manifest.json").write_text(json.dumps(release_manifest, indent=2) + "\n")
        (manifests / "component-manifest.json").write_text(json.dumps({"schemaVersion":"mapaf.component-manifest/v1","components":files}, indent=2) + "\n")
        (manifests / "version.txt").write_text(VERSION + "\n")
        (stage / "README.md").write_text(
            f"# {RELEASE_NAME}\n\nOfficial MAPAF Enterprise v{VERSION} General Availability package.\n\n"
            "Start with `docs/MAPAF-v2.9-Installation-Guide.md`.\n"
        )
        checks = []
        for p in sorted(stage.rglob("*")):
            if p.is_file() and p.name != "checksums.sha256":
                checks.append(f"{sha256(p)}  {p.relative_to(stage)}")
        (manifests / "checksums.sha256").write_text("\n".join(checks) + "\n")

        if zip_path.exists(): zip_path.unlink()
        with zipfile.ZipFile(zip_path, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as zf:
            for p in sorted(stage.rglob("*")):
                if p.is_file():
                    zf.write(p, Path(RELEASE_NAME) / p.relative_to(stage))

    digest = sha256(zip_path)
    checksum_path.write_text(f"{digest}  {zip_path.name}\n")
    release_manifest["archive"] = zip_path.name
    release_manifest["archiveSha256"] = digest
    release_manifest["archiveSizeBytes"] = zip_path.stat().st_size
    external_manifest.write_text(json.dumps(release_manifest, indent=2) + "\n")
    print("=" * 72)
    print("MAPAF ENTERPRISE v2.9.0 GA PACKAGE CREATED")
    print("=" * 72)
    print(f"Archive   : {zip_path}")
    print(f"Checksum  : {checksum_path}")
    print(f"Manifest  : {external_manifest}")
    print(f"SHA-256   : {digest}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
