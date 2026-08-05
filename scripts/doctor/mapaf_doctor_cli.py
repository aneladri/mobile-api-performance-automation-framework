#!/usr/bin/env python3
from __future__ import annotations

import argparse
import html
import json
import os
import subprocess
import sys
import textwrap
import webbrowser
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

SCHEMA = "mapaf.doctor.executive-report/v1"


def load_json(path: Path) -> dict[str, Any]:
    if not path.is_file():
        raise FileNotFoundError(f"Required Doctor artifact is missing: {path}")
    with path.open(encoding="utf-8") as stream:
        return json.load(stream)


def run_gradle(root: Path) -> None:
    command = [str(root / "gradlew"), "mapafDoctor"]
    result = subprocess.run(command, cwd=root)
    if result.returncode != 0:
        raise RuntimeError("MAPAF Doctor execution failed.")


def status_symbol(status: str) -> str:
    return {
        "HEALTHY": "PASS",
        "DEGRADED": "WARN",
        "UNHEALTHY": "FAIL",
        "NOT_CONFIGURED": "N/A",
    }.get(status.upper(), status.upper() or "UNKNOWN")


def build_report(root: Path) -> dict[str, Any]:
    report_dir = root / "dashboard/reports/doctor"
    detail = load_json(report_dir / "doctor-report.json")
    overview = load_json(report_dir / "doctor-overview.json")
    diagnosis_path = report_dir / "doctor-diagnosis.json"
    diagnosis = load_json(diagnosis_path) if diagnosis_path.is_file() else {
        "schemaVersion": "not-installed",
        "totalIssues": 0,
        "blockingIssues": 0,
        "warnings": 0,
        "openActions": 0,
        "executiveSummary": "Diagnosis Center is not installed.",
        "diagnoses": [],
        "immediateActions": [],
        "capabilityImpact": {},
    }

    probes = []
    for item in overview.get("probes", []):
        probes.append({
            "id": item.get("probeId", ""),
            "name": item.get("name", ""),
            "status": item.get("status", "UNKNOWN"),
            "severity": item.get("severity", "INFO"),
            "score": item.get("score", 0),
            "operational": item.get("operational", False),
            "blocking": item.get("blocking", False),
            "summary": item.get("summary", ""),
        })

    generated = datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
    return {
        "schemaVersion": SCHEMA,
        "generatedAt": generated,
        "product": overview.get("product", detail.get("product", "MAPAF Enterprise")),
        "platformVersion": overview.get("platformVersion", detail.get("platformVersion", "")),
        "environment": overview.get("environment", detail.get("environment", "")),
        "overallStatus": overview.get("overallStatus", detail.get("overallStatus", "UNKNOWN")),
        "weightedHealthScore": overview.get("weightedHealthScore", detail.get("healthScore", 0)),
        "platformReady": overview.get("platformReady", detail.get("platformReady", False)),
        "blockingIssues": diagnosis.get("blockingIssues", overview.get("blockingIssues", 0)),
        "warnings": diagnosis.get("warnings", overview.get("warnings", 0)),
        "openActions": diagnosis.get("openActions", 0),
        "executiveSummary": overview.get("executiveSummary", detail.get("executiveSummary", "")),
        "diagnosisSummary": diagnosis.get("executiveSummary", ""),
        "readiness": overview.get("readiness", {}),
        "probes": probes,
        "priorityIssues": diagnosis.get("diagnoses", []),
        "immediateActions": diagnosis.get("immediateActions", overview.get("recommendedActions", [])),
        "capabilityImpact": diagnosis.get("capabilityImpact", {}),
        "artifacts": {
            "overview": "doctor-overview.html",
            "diagnosis": "doctor-diagnosis.html",
            "details": "doctor-report.html",
            "json": "executive-report.json",
            "markdown": "executive-report.md",
            "html": "executive-report.html",
            "pdf": "executive-report.pdf",
        },
    }


def markdown_report(data: dict[str, Any]) -> str:
    lines = [
        "# MAPAF Doctor Executive Report",
        "",
        f"- **Generated:** {data['generatedAt']}",
        f"- **Platform version:** {data['platformVersion']}",
        f"- **Environment:** {data['environment']}",
        f"- **Platform health:** {data['overallStatus']}",
        f"- **Weighted health score:** {data['weightedHealthScore']}%",
        f"- **Platform ready:** {'YES' if data['platformReady'] else 'NO'}",
        f"- **Blocking issues:** {data['blockingIssues']}",
        f"- **Warnings:** {data['warnings']}",
        "",
        "## Executive Summary",
        "",
        data.get("executiveSummary", ""),
        "",
        "## Capability Health",
        "",
        "| Capability | Status | Severity | Score | Operational |",
        "|---|---:|---:|---:|---:|",
    ]
    for probe in data["probes"]:
        lines.append(
            f"| {probe['name']} | {probe['status']} | {probe['severity']} | "
            f"{probe['score']}% | {'YES' if probe['operational'] else 'NO'} |"
        )
    lines.extend(["", "## Priority Issues", ""])
    issues = data.get("priorityIssues", [])
    if not issues:
        lines.append("No active diagnosis items.")
    else:
        for issue in issues:
            lines.extend([
                f"### {issue.get('capability', 'Platform')} — {issue.get('issue', 'Issue')}",
                "",
                f"- **Priority:** {issue.get('priority', '')}",
                f"- **Severity:** {issue.get('severity', '')}",
                f"- **Blocking:** {'YES' if issue.get('blocking') else 'NO'}",
                f"- **Owner:** {issue.get('owner', '')}",
                f"- **Estimated effort:** {issue.get('estimatedEffort', '')}",
                f"- **Root cause:** {issue.get('rootCause', '')}",
                f"- **Business impact:** {issue.get('businessImpact', '')}",
                f"- **Recommended action:** {issue.get('recommendedAction', '')}",
                "",
            ])
    lines.extend(["## Immediate Actions", ""])
    actions = data.get("immediateActions", [])
    if actions:
        lines.extend(f"- {action}" for action in actions)
    else:
        lines.append("No immediate actions required.")
    lines.append("")
    return "\n".join(lines)


def html_report(data: dict[str, Any]) -> str:
    probe_cards = "".join(
        f"<article class='card'><span class='tag'>{html.escape(str(p['severity']))}</span>"
        f"<h3>{html.escape(str(p['name']))}</h3>"
        f"<div class='status {str(p['status']).lower().replace('_','-')}'>{html.escape(str(p['status']))}</div>"
        f"<p>{html.escape(str(p['summary']))}</p></article>" for p in data["probes"]
    )
    issues = data.get("priorityIssues", [])
    issue_html = "<p>No active diagnosis items.</p>" if not issues else "".join(
        f"<article class='issue'><h3>{html.escape(str(i.get('capability','Platform')))} — {html.escape(str(i.get('issue','Issue')))}</h3>"
        f"<p><b>Priority:</b> {html.escape(str(i.get('priority','')))} · <b>Owner:</b> {html.escape(str(i.get('owner','')))} · "
        f"<b>Effort:</b> {html.escape(str(i.get('estimatedEffort','')))}</p>"
        f"<p><b>Business impact:</b> {html.escape(str(i.get('businessImpact','')))}</p>"
        f"<p><b>Action:</b> {html.escape(str(i.get('recommendedAction','')))}</p></article>" for i in issues
    )
    actions = data.get("immediateActions", [])
    action_html = "<li>No immediate actions required.</li>" if not actions else "".join(
        f"<li>{html.escape(str(action))}</li>" for action in actions
    )
    return f"""<!doctype html><html lang='en'><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'>
<title>MAPAF Doctor Executive Report</title><style>
:root{{--orange:#d04a02;--dark:#1d252d;--muted:#667085;--bg:#f4f6f8;--card:#fff;--green:#147d64;--amber:#a15c00;--red:#c62828;--blue:#175cd3}}
*{{box-sizing:border-box}}body{{margin:0;font-family:Arial,sans-serif;background:var(--bg);color:var(--dark)}}header{{padding:34px 5vw;background:linear-gradient(120deg,#1d252d,#46545f);color:#fff}}header small{{color:#ff9859;letter-spacing:1.5px;font-weight:700}}nav{{padding:11px 5vw;background:#fff;border-bottom:1px solid #dce1e6}}nav a{{margin-right:12px;color:var(--dark);text-decoration:none;font-weight:700}}main{{max-width:1450px;margin:auto;padding:28px 5vw 60px}}.hero,.grid{{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:14px}}.card,.issue{{background:var(--card);border:1px solid #dce1e6;border-radius:12px;padding:18px;box-shadow:0 3px 12px rgba(0,0,0,.04)}}.metric{{font-size:30px;font-weight:800;margin-top:8px}}.status{{font-size:24px;font-weight:800}}.healthy{{color:var(--green)}}.degraded{{color:var(--amber)}}.unhealthy{{color:var(--red)}}.not-configured{{color:var(--blue)}}.tag{{font-size:11px;color:var(--muted);font-weight:700}}section{{margin-top:28px}}p,li{{line-height:1.5}}footer{{margin-top:30px;color:var(--muted);font-size:12px}}</style></head>
<body><header><small>MAPAF ENTERPRISE QUALITY ENGINEERING PLATFORM</small><h1>Doctor Executive Report</h1><p>Operational health, diagnosis, and action intelligence</p></header>
<nav><a href='doctor-overview.html'>Overview</a><a href='doctor-diagnosis.html'>Diagnosis</a><a href='doctor-report.html'>Details</a><a href='executive-report.md'>Markdown</a><a href='executive-report.pdf'>PDF</a></nav>
<main><section class='hero'><article class='card'><div>Platform Health</div><div class='metric {str(data['overallStatus']).lower()}'>{html.escape(str(data['overallStatus']))}</div></article>
<article class='card'><div>Weighted Score</div><div class='metric'>{data['weightedHealthScore']}%</div></article><article class='card'><div>Platform Ready</div><div class='metric {'healthy' if data['platformReady'] else 'unhealthy'}'>{'YES' if data['platformReady'] else 'NO'}</div></article><article class='card'><div>Blocking Issues</div><div class='metric'>{data['blockingIssues']}</div></article></section>
<section><h2>Executive Summary</h2><article class='card'><p>{html.escape(str(data.get('executiveSummary','')))}</p><p>{html.escape(str(data.get('diagnosisSummary','')))}</p></article></section>
<section><h2>Capability Health</h2><div class='grid'>{probe_cards}</div></section><section><h2>Priority Issues</h2>{issue_html}</section><section><h2>Immediate Actions</h2><article class='card'><ul>{action_html}</ul></article></section>
<footer>Contract {SCHEMA} · Generated {html.escape(str(data['generatedAt']))}</footer></main></body></html>"""


def pdf_escape(value: str) -> str:
    return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")


def write_simple_pdf(path: Path, lines: list[str]) -> None:
    wrapped: list[str] = []
    for line in lines:
        if not line:
            wrapped.append("")
        else:
            wrapped.extend(textwrap.wrap(line, width=94, replace_whitespace=False) or [""])
    pages = [wrapped[i:i + 48] for i in range(0, len(wrapped), 48)] or [["MAPAF Doctor Executive Report"]]
    objects: list[bytes] = []
    objects.append(b"<< /Type /Catalog /Pages 2 0 R >>")
    page_ids = [4 + i * 2 for i in range(len(pages))]
    kids = " ".join(f"{pid} 0 R" for pid in page_ids)
    objects.append(f"<< /Type /Pages /Kids [{kids}] /Count {len(pages)} >>".encode())
    objects.append(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>")
    for index, page_lines in enumerate(pages):
        page_id = page_ids[index]
        content_id = page_id + 1
        objects.append(f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 3 0 R >> >> /Contents {content_id} 0 R >>".encode())
        commands = ["BT", "/F1 10 Tf", "50 750 Td", "13 TL"]
        for line in page_lines:
            commands.append(f"({pdf_escape(line)}) Tj")
            commands.append("T*")
        commands.append("ET")
        stream = "\n".join(commands).encode("latin-1", errors="replace")
        objects.append(b"<< /Length " + str(len(stream)).encode() + b" >>\nstream\n" + stream + b"\nendstream")
    output = bytearray(b"%PDF-1.4\n%\xe2\xe3\xcf\xd3\n")
    offsets = [0]
    for number, obj in enumerate(objects, 1):
        offsets.append(len(output))
        output.extend(f"{number} 0 obj\n".encode())
        output.extend(obj)
        output.extend(b"\nendobj\n")
    xref = len(output)
    output.extend(f"xref\n0 {len(objects)+1}\n".encode())
    output.extend(b"0000000000 65535 f \n")
    for offset in offsets[1:]:
        output.extend(f"{offset:010d} 00000 n \n".encode())
    output.extend(f"trailer\n<< /Size {len(objects)+1} /Root 1 0 R >>\nstartxref\n{xref}\n%%EOF\n".encode())
    path.write_bytes(output)


def publish(root: Path, quiet: bool = False) -> dict[str, Any]:
    data = build_report(root)
    output = root / "dashboard/reports/doctor"
    output.mkdir(parents=True, exist_ok=True)
    json_path = output / "executive-report.json"
    md_path = output / "executive-report.md"
    html_path = output / "executive-report.html"
    pdf_path = output / "executive-report.pdf"
    json_path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    markdown = markdown_report(data)
    md_path.write_text(markdown, encoding="utf-8")
    html_path.write_text(html_report(data), encoding="utf-8")
    write_simple_pdf(pdf_path, markdown.splitlines())
    if not quiet:
        print("MAPAF Doctor Executive Report generated:")
        for path in (html_path, pdf_path, md_path, json_path):
            print(f"  {path}")
    return data


def print_console(data: dict[str, Any]) -> None:
    width = 72
    print("=" * width)
    print("                 MAPAF DOCTOR — EXECUTIVE STATUS")
    print("=" * width)
    print(f"Platform Health      : {data['overallStatus']}")
    print(f"Weighted Score       : {data['weightedHealthScore']}%")
    print(f"Platform Ready       : {'YES' if data['platformReady'] else 'NO'}")
    print(f"Blocking Issues      : {data['blockingIssues']}")
    print(f"Warnings             : {data['warnings']}")
    print("-" * width)
    for probe in data["probes"]:
        print(f"{probe['name'][:38]:38} : {status_symbol(str(probe['status'])):5}  {probe['score']:3}%")
    print("-" * width)
    actions = data.get("immediateActions", [])
    if actions:
        print("Immediate Actions")
        for index, action in enumerate(actions[:5], 1):
            print(f"  {index}. {action}")
    else:
        print("Immediate Actions    : None")
    print("=" * width)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="MAPAF Doctor command-line experience")
    parser.add_argument("command", nargs="?", default="run", choices=["run", "status", "report", "json", "open", "help"])
    parser.add_argument("--root", default=os.getcwd(), help="MAPAF repository root")
    parser.add_argument("--no-refresh", action="store_true", help="Use existing Doctor artifacts")
    parser.add_argument("--strict", action="store_true", help="Return non-zero when platform is not ready")
    parser.add_argument("--quiet", action="store_true", help="Suppress artifact publication messages")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = Path(args.root).resolve()
    if args.command == "help":
        print("Use: ./mapaf-doctor [run|status|report|json|open] [--no-refresh] [--strict]")
        return 0
    if not (root / "MAPAF_VERSION").is_file():
        print(f"ERROR: MAPAF_VERSION not found under {root}", file=sys.stderr)
        return 2
    try:
        if args.command == "run" and not args.no_refresh:
            run_gradle(root)
        data = publish(root, quiet=args.quiet)
        if args.command in ("run", "status", "report"):
            print_console(data)
        elif args.command == "json":
            print(json.dumps(data, indent=2))
        elif args.command == "open":
            target = root / "dashboard/reports/doctor/executive-report.html"
            webbrowser.open(target.as_uri())
            print(target)
        if args.strict and not data.get("platformReady", False):
            return 1
        return 0
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
