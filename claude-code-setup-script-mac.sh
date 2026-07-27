#!/usr/bin/env bash
# claude-code-setup-script-mac.sh — Unified Claude Code installer for macOS
#
# Install modes:
#   bash claude-code-setup-script-mac.sh --enterprise                        Full install, enterprise auth
#   bash claude-code-setup-script-mac.sh --shared-services --api-key "sk-..."  Full install + shared services config
#
# Switch modes (config-only, no install):
#   bash claude-code-setup-script-mac.sh --switch-to-enterprise              Remove shared services config, restore enterprise auth
#   bash claude-code-setup-script-mac.sh --switch-to-shared-services --api-key "sk-..."  Apply shared services config (no install)
#
# Shared services parameters (used with --shared-services or --switch-to-shared-services):
#   --api-key KEY       (required) Your shared services API key
#   --base-url URL      (optional) Default: americas endpoint
#   --sonnet-model M    (optional) Default: bedrock.anthropic.claude-sonnet-4-6
#   --opus-model M      (optional) Default: bedrock.anthropic.claude-opus-4-6
#   --haiku-model M     (optional) Default: bedrock.anthropic.claude-haiku-4-5
#
# If macOS says "permission denied" or shows a Gatekeeper warning, run:
#   xattr -d com.apple.quarantine claude-code-setup-script-mac.sh
# then re-run: bash claude-code-setup-script-mac.sh --enterprise
#
# Prerequisites:
#   - You need a Premium Claude license OR a shared-services API key.

# Must be run with bash, not sh
if [ -z "${BASH_VERSION:-}" ]; then
    echo "Error: This script must be run with bash, not sh."
    echo "Usage: bash claude-code-setup-script-mac.sh"
    exit 1
fi

set -euo pipefail

SCRIPT_VERSION="1.8"  # Last updated: 2026-04-24

# ── Parse Arguments ──────────────────────────────────────────────────────────

MODE_ENTERPRISE=false
MODE_SHARED_SERVICES=false
MODE_SWITCH_TO_ENTERPRISE=false
MODE_SWITCH_TO_SHARED_SERVICES=false
CLI_API_KEY=""
CLI_BASE_URL=""
CLI_SONNET_MODEL=""
CLI_OPUS_MODEL=""
CLI_HAIKU_MODEL=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --enterprise)                MODE_ENTERPRISE=true ;;
        --shared-services)           MODE_SHARED_SERVICES=true ;;
        --switch-to-enterprise)      MODE_SWITCH_TO_ENTERPRISE=true ;;
        --switch-to-shared-services) MODE_SWITCH_TO_SHARED_SERVICES=true ;;
        --api-key)                   CLI_API_KEY="$2"; shift ;;
        --base-url)                  CLI_BASE_URL="$2"; shift ;;
        --sonnet-model)              CLI_SONNET_MODEL="$2"; shift ;;
        --opus-model)                CLI_OPUS_MODEL="$2"; shift ;;
        --haiku-model)               CLI_HAIKU_MODEL="$2"; shift ;;
        -h|--help)
            sed -n '2,/^$/p' "$0" | sed 's/^# \?//'
            exit 0 ;;
        *) echo "Unknown option: $1"; exit 1 ;;
    esac
    shift
done

# ── Validate mode selection ──────────────────────────────────────────────────
mode_count=0
[[ "$MODE_ENTERPRISE" == "true" ]] && ((++mode_count))
[[ "$MODE_SHARED_SERVICES" == "true" ]] && ((++mode_count))
[[ "$MODE_SWITCH_TO_ENTERPRISE" == "true" ]] && ((++mode_count))
[[ "$MODE_SWITCH_TO_SHARED_SERVICES" == "true" ]] && ((++mode_count))

if [[ "$mode_count" -eq 0 ]]; then
    echo "Error: A mode flag is required."
    echo ""
    echo "  Install modes:"
    echo "    bash claude-code-setup-script-mac.sh --enterprise                              Full install, enterprise auth"
    echo "    bash claude-code-setup-script-mac.sh --shared-services --api-key \"sk-...\"      Full install + shared services"
    echo ""
    echo "  Switch modes (config-only, no install):"
    echo "    bash claude-code-setup-script-mac.sh --switch-to-enterprise                    Restore enterprise auth"
    echo "    bash claude-code-setup-script-mac.sh --switch-to-shared-services --api-key \"sk-...\"  Apply shared services config"
    echo ""
    echo "  Run 'bash claude-code-setup-script-mac.sh -h' for full help."
    exit 1
fi

if [[ "$mode_count" -gt 1 ]]; then
    echo "Error: Only one mode flag can be used at a time."
    echo "  Choose one of: --enterprise, --shared-services, --switch-to-enterprise, --switch-to-shared-services"
    exit 1
fi

# ── Helpers ──────────────────────────────────────────────────────────────────

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
LOG_DIR="$HOME/.claude/logs"
mkdir -p "$LOG_DIR" 2>/dev/null || true
LOG_FILE="$LOG_DIR/install-${TIMESTAMP}.log"

# Log all output for troubleshooting while still showing it on screen
if touch "$LOG_FILE" 2>/dev/null; then
    exec > >(tee -a "$LOG_FILE") 2>&1
else
    # Fallback: try script directory
    LOG_FILE="$SCRIPT_DIR/install-${TIMESTAMP}.log"
    if touch "$LOG_FILE" 2>/dev/null; then
        exec > >(tee -a "$LOG_FILE") 2>&1
    else
        echo "  [INFO] Could not create log file (continuing without logging)."
        LOG_FILE=""
    fi
fi

# Output helpers
step()  { printf '\n\033[36m[%s] %s\033[0m\n' "$1" "$2"; }
ok()    { printf '  \033[32m[OK]  \033[0m %s\n' "$1"; }
skip()  { printf '  \033[90m[SKIP]\033[0m %s\n' "$1"; }
fail()  { printf '  \033[31m[FAIL]\033[0m %s\n' "$1"; }
info()  { printf '  \033[36m[INFO]\033[0m %s\n' "$1"; }
warn()  { printf '  \033[33m[WARN]\033[0m %s\n' "$1"; }
fix()   { printf '  \033[35m[FIX] \033[0m %s\n' "$1"; }

# Detect the user's shell and choose the right rc file
detect_rcfile() {
    case "$SHELL" in
        */bash)  echo "$HOME/.bash_profile" ;;
        */zsh)   echo "$HOME/.zshrc" ;;
        *)       echo "$HOME/.zshrc" ;;  # macOS default
    esac
}

# Append lines to RCFILE if a guard string is not already present
ensure_in_rcfile() {
    local guard="$1"; shift
    if ! grep -Fq "$guard" "$RCFILE" 2>/dev/null; then
        [[ -s "$RCFILE" ]] && [[ "$(tail -c 1 "$RCFILE")" != "" ]] && echo '' >> "$RCFILE"
        printf '%s\n' "" "$@" >> "$RCFILE"
    fi
}

# Compare version strings: version_gte "14.2" "13.0" -> true
version_gte() {
    local v1="$1" v2="$2"
    if [[ "$v1" == "$v2" ]]; then return 0; fi
    local IFS=.
    local i v1_parts=($v1) v2_parts=($v2)
    for ((i = 0; i < ${#v2_parts[@]}; i++)); do
        local a="${v1_parts[$i]:-0}"
        local b="${v2_parts[$i]:-0}"
        a="${a%%[!0-9]*}"; a="${a:-0}"
        b="${b%%[!0-9]*}"; b="${b:-0}"
        if (( a > b )); then return 0; fi
        if (( a < b )); then return 1; fi
    done
    return 0
}

# Detect proxy configuration for better error messages
detect_proxy() {
    local proxy=""
    if [[ -n "${HTTP_PROXY:-}" ]]; then proxy="$HTTP_PROXY"
    elif [[ -n "${HTTPS_PROXY:-}" ]]; then proxy="$HTTPS_PROXY"
    elif [[ -n "${http_proxy:-}" ]]; then proxy="$http_proxy"
    elif [[ -n "${https_proxy:-}" ]]; then proxy="$https_proxy"
    fi
    echo "$proxy"
}

# Provide guidance if the user interrupts with Ctrl+C
trap 'echo ""; echo "Installation interrupted. Re-run this script to resume."; exit 130' INT TERM

# ERR trap — report the line number on unexpected failures
_error_handler() {
    local exit_code=$?
    local line_number=$1
    fail "Unexpected error on line $line_number: '${BASH_COMMAND}' (exit code: $exit_code)."
    info "Check the log file for details. Re-run this script to resume."
}
trap '_error_handler $LINENO' ERR

# ── Globals ──────────────────────────────────────────────────────────────────
warnings=false
RCFILE="$(detect_rcfile)"
CORP_CERT_BUNDLE="$HOME/.claude/certs/corporate-ca-bundle.pem"
MERGED_CERT_BUNDLE="$HOME/.claude/certs/merged-ca-bundle.pem"
TOTAL_STEPS=12

# Adjust step count based on mode
if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
    TOTAL_STEPS=13
fi

# ── Homebrew cask install helper ──────────────────────────────────────────────
# Wraps `brew install --cask` with HOMEBREW_CURLRC set so Homebrew's internal
# curl uses the corporate CA bundle. Without this, cask downloads through the
# PwC SSL-intercepting proxy fail with certificate errors.
brew_cask_install() {
    local cask_name="$1"
    local _curlrc=""
    local _old_curlrc="${HOMEBREW_CURLRC:-}"

    if [[ -f "$MERGED_CERT_BUNDLE" ]]; then
        _curlrc=$(mktemp /tmp/pwc-brew-curlrc-XXXXXX)
        echo "cacert = $MERGED_CERT_BUNDLE" > "$_curlrc"
        export HOMEBREW_CURLRC="$_curlrc"
    elif [[ -f "$CORP_CERT_BUNDLE" ]]; then
        _curlrc=$(mktemp /tmp/pwc-brew-curlrc-XXXXXX)
        echo "cacert = $CORP_CERT_BUNDLE" > "$_curlrc"
        export HOMEBREW_CURLRC="$_curlrc"
    fi

    local rc=0
    brew install --cask "$cask_name" || rc=$?

    # Clean up temp curlrc
    if [[ -n "$_curlrc" ]]; then
        rm -f "$_curlrc"
        if [[ -n "$_old_curlrc" ]]; then
            export HOMEBREW_CURLRC="$_old_curlrc"
        else
            unset HOMEBREW_CURLRC
        fi
    fi

    return $rc
}

# ── Validate and set shared services params (used by --shared-services and --switch-to-shared-services) ──
validate_shared_services_params() {
    if [[ -z "$CLI_API_KEY" ]]; then
        fail "API key is required for shared services mode."
        echo ""
        echo "  Usage:"
        echo "    bash claude-code-setup-script-mac.sh --shared-services --api-key 'sk-...'"
        echo "    bash claude-code-setup-script-mac.sh --switch-to-shared-services --api-key 'sk-...'"
        echo ""
        echo "  All parameters:"
        echo "    --api-key KEY        (required) Your shared services API key"
        echo "    --base-url URL       (optional) Default: americas endpoint"
        echo "    --sonnet-model M     (optional) Default: bedrock.anthropic.claude-sonnet-4-6"
        echo "    --opus-model M       (optional) Default: bedrock.anthropic.claude-opus-4-6"
        echo "    --haiku-model M      (optional) Default: bedrock.anthropic.claude-haiku-4-5"
        echo ""
        exit 1
    fi

    ss_api_key="$CLI_API_KEY"
    ss_base_url="${CLI_BASE_URL:-https://genai-sharedservice-americas.pwcinternal.com}"
    ss_sonnet="${CLI_SONNET_MODEL:-bedrock.anthropic.claude-sonnet-4-6}"
    ss_opus="${CLI_OPUS_MODEL:-bedrock.anthropic.claude-opus-4-6}"
    ss_haiku="${CLI_HAIKU_MODEL:-bedrock.anthropic.claude-haiku-4-5}"
}

# ── Apply shared services configuration (used by --shared-services step and --switch-to-shared-services) ──
apply_shared_services_config() {
    # Determine cert path for NODE_EXTRA_CA_CERTS
    local ss_cert_path=""
    if [[ -f "$CORP_CERT_BUNDLE" ]]; then
        ss_cert_path="$CORP_CERT_BUNDLE"
    fi

    if ! command -v python3 &>/dev/null; then
        fail "python3 is required for shared services configuration but not found."
        exit 1
    fi

    # ── Claude Code settings.json (~/.claude/settings.json) ──
    local CLAUDE_SETTINGS_FILE="$HOME/.claude/settings.json"

    if [[ -f "$CLAUDE_SETTINGS_FILE" ]]; then
        if ! python3 -c "import json, sys; json.load(open(sys.argv[1]))" "$CLAUDE_SETTINGS_FILE" 2>/dev/null; then
            local BACKUP="${CLAUDE_SETTINGS_FILE}.bak.$(date +%Y%m%d-%H%M%S)"
            warn "Invalid ~/.claude/settings.json -- backing up to: $BACKUP"
            cp "$CLAUDE_SETTINGS_FILE" "$BACKUP"
            echo "{}" > "$CLAUDE_SETTINGS_FILE"
        fi
    else
        mkdir -p "$HOME/.claude"
        echo "{}" > "$CLAUDE_SETTINGS_FILE"
    fi

    # Update ~/.claude/settings.json with env block
    SS_BASE_URL="$ss_base_url" \
    SS_API_KEY="$ss_api_key" \
    SS_SONNET="$ss_sonnet" \
    SS_OPUS="$ss_opus" \
    SS_HAIKU="$ss_haiku" \
    SS_CERT_PATH="$ss_cert_path" \
    SS_CLAUDE_SETTINGS="$CLAUDE_SETTINGS_FILE" \
    python3 << 'PYEOF'
import json, os

settings_file = os.environ["SS_CLAUDE_SETTINGS"]

with open(settings_file, "r") as f:
    settings = json.load(f)

env_block = {
    "ANTHROPIC_BASE_URL":             os.environ["SS_BASE_URL"],
    "ANTHROPIC_AUTH_TOKEN":           os.environ["SS_API_KEY"],
    "ANTHROPIC_DEFAULT_SONNET_MODEL": os.environ["SS_SONNET"],
    "ANTHROPIC_DEFAULT_OPUS_MODEL":   os.environ["SS_OPUS"],
    "ANTHROPIC_DEFAULT_HAIKU_MODEL":  os.environ["SS_HAIKU"],
}

ca_cert = os.environ.get("SS_CERT_PATH", "")
if ca_cert:
    env_block["NODE_EXTRA_CA_CERTS"] = ca_cert

# Merge: preserve any existing env entries the user added manually
existing_env = settings.get("env", {})
for key, value in existing_env.items():
    if key not in env_block:
        env_block[key] = value

settings["env"] = env_block

with open(settings_file, "w") as f:
    json.dump(settings, f, indent=4)
PYEOF

    ok "~/.claude/settings.json updated with env block."

    # ── VS Code settings.json (disableLoginPrompt only) ──
    local SETTINGS_DIR="$HOME/Library/Application Support/Code/User"
    local SETTINGS_FILE="$SETTINGS_DIR/settings.json"

    if [[ ! -d "$SETTINGS_DIR" ]]; then
        mkdir -p "$SETTINGS_DIR"
    fi

    if [[ -f "$SETTINGS_FILE" ]]; then
        if ! python3 -c "import json, sys; json.load(open(sys.argv[1]))" "$SETTINGS_FILE" 2>/dev/null; then
            local BACKUP="${SETTINGS_FILE}.bak.$(date +%Y%m%d-%H%M%S)"
            warn "Existing settings.json has invalid JSON. Backing up to: $BACKUP"
            cp "$SETTINGS_FILE" "$BACKUP"
            echo "{}" > "$SETTINGS_FILE"
        fi
    else
        echo "{}" > "$SETTINGS_FILE"
    fi

    # Set disableLoginPrompt and remove legacy environmentVariables
    SS_VSCODE_SETTINGS="$SETTINGS_FILE" \
    python3 << 'PYEOF'
import json, os, re

settings_file = os.environ["SS_VSCODE_SETTINGS"]

with open(settings_file, "r") as f:
    raw = f.read()

try:
    settings = json.loads(raw)
except json.JSONDecodeError:
    cleaned = re.sub(r'(?m)^\s*//.*$', '', raw)
    cleaned = re.sub(r'(?<=,)\s*//.*$', '', cleaned)
    settings = json.loads(cleaned)

# Set disableLoginPrompt (both key formats for compatibility)
settings["claude-code.disableLoginPrompt"] = True
settings["claudeCode.disableLoginPrompt"] = True

# Remove legacy environmentVariables entries (config now lives in ~/.claude/settings.json)
if "claude-code.environmentVariables" in settings:
    del settings["claude-code.environmentVariables"]
    print("  [INFO] Removed legacy claude-code.environmentVariables from VS Code settings (now in ~/.claude/settings.json).")

with open(settings_file, "w") as f:
    json.dump(settings, f, indent=4)

print("  [OK]   VS Code settings.json updated (disableLoginPrompt enabled).")
PYEOF

    ok "Shared services configuration complete."
}

# ============================================================================
# SWITCH TO ENTERPRISE -- config-only, remove shared services, restore login
# ============================================================================

if [[ "$MODE_SWITCH_TO_ENTERPRISE" == "true" ]]; then
    echo ""
    echo "============================================="
    echo "  Switching to Enterprise Authentication"
    echo "============================================="
    echo ""

    changed=false

    # ── 1. Remove shared services env vars from ~/.claude/settings.json ──
    CLAUDE_SETTINGS="$HOME/.claude/settings.json"
    if [[ -f "$CLAUDE_SETTINGS" ]] && command -v python3 &>/dev/null; then
        result=$(python3 << 'PYEOF'
import json, os, sys

settings_file = os.path.expanduser("~/.claude/settings.json")
try:
    with open(settings_file, "r") as f:
        settings = json.load(f)
except (json.JSONDecodeError, FileNotFoundError):
    print("SKIP:Could not parse ~/.claude/settings.json")
    sys.exit(0)

env_block = settings.get("env", {})
ss_keys = [
    "ANTHROPIC_BASE_URL",
    "ANTHROPIC_AUTH_TOKEN",
    "ANTHROPIC_DEFAULT_SONNET_MODEL",
    "ANTHROPIC_DEFAULT_OPUS_MODEL",
    "ANTHROPIC_DEFAULT_HAIKU_MODEL",
    "DISABLE_PROMPT_CACHING",
]

removed = []
for key in ss_keys:
    if key in env_block:
        del env_block[key]
        removed.append(key)

if removed:
    settings["env"] = env_block
    with open(settings_file, "w") as f:
        json.dump(settings, f, indent=4)
    print("OK:Removed shared services env vars from ~/.claude/settings.json")
    for k in removed:
        print(f"  - {k}")
    # Verify keys are actually gone after write
    try:
        with open(settings_file, "r") as f:
            verify = json.load(f)
        env_after = verify.get("env", {})
        for k in removed:
            if k in env_after:
                print(f"WARN:Verification failed: {k} still present after write -- please edit ~/.claude/settings.json manually.")
    except Exception as e:
        print(f"WARN:Could not verify settings.json after write: {e}")
else:
    print("SKIP:No shared services env vars found in ~/.claude/settings.json")
PYEOF
        )
        while IFS= read -r line; do
            if [[ "$line" == OK:* ]]; then
                echo "  [OK]   ${line#OK:}"
                changed=true
            elif [[ "$line" == SKIP:* ]]; then
                echo "  [SKIP] ${line#SKIP:}"
            elif [[ "$line" == WARN:* ]]; then
                echo "  [WARN] ${line#WARN:}"
            else
                echo "  $line"
            fi
        done <<< "$result"
    elif [[ ! -f "$CLAUDE_SETTINGS" ]]; then
        echo "  [SKIP] ~/.claude/settings.json not found"
    else
        echo "  [WARN] python3 not available -- cannot update settings"
    fi

    # ── 2. Disable disableLoginPrompt in VS Code settings ──
    VSCODE_SETTINGS="$HOME/Library/Application Support/Code/User/settings.json"
    if [[ -f "$VSCODE_SETTINGS" ]] && command -v python3 &>/dev/null; then
        result=$(SS_VSCODE_SETTINGS="$VSCODE_SETTINGS" python3 << 'PYEOF'
import json, os, re, sys

settings_file = os.environ["SS_VSCODE_SETTINGS"]
try:
    with open(settings_file, "r") as f:
        raw = f.read()
    try:
        settings = json.loads(raw)
    except json.JSONDecodeError:
        cleaned = re.sub(r'(?m)^\s*//.*$', '', raw)
        cleaned = re.sub(r'(?<=,)\s*//.*$', '', cleaned)
        settings = json.loads(cleaned)
except Exception:
    print("SKIP:Could not parse VS Code settings.json")
    sys.exit(0)

dlp_changed = False
for key in ["claude-code.disableLoginPrompt", "claudeCode.disableLoginPrompt"]:
    if key in settings:
        settings[key] = False
        dlp_changed = True

if dlp_changed:
    with open(settings_file, "w") as f:
        json.dump(settings, f, indent=4)
    print("OK:Set disableLoginPrompt = false in VS Code settings.")
else:
    print("SKIP:disableLoginPrompt not found in VS Code settings")
PYEOF
        )
        while IFS= read -r line; do
            if [[ "$line" == OK:* ]]; then
                echo "  [OK]   ${line#OK:}"
                changed=true
            elif [[ "$line" == SKIP:* ]]; then
                echo "  [SKIP] ${line#SKIP:}"
            else
                echo "  $line"
            fi
        done <<< "$result"
    elif [[ ! -f "$VSCODE_SETTINGS" ]]; then
        echo "  [SKIP] VS Code settings.json not found"
    else
        echo "  [WARN] python3 not available — cannot update VS Code settings"
    fi

    # ── 3. Check for missing OAuth credentials ──
    needs_login=false
    cred_file="$HOME/.claude/.credentials.json"
    if [[ ! -f "$cred_file" ]]; then
        needs_login=true
    elif command -v python3 &>/dev/null; then
        if ! python3 -c "
import json, sys
try:
    with open(sys.argv[1]) as f:
        creds = json.load(f)
    sys.exit(0 if 'claudeAiOauth' in creds else 1)
except Exception:
    sys.exit(1)
" "$cred_file" 2>/dev/null; then
            needs_login=true
        fi
    fi

    # ── 4. Summary ──
    echo ""
    if [[ "$changed" == "true" ]]; then
        echo "============================================="
        echo "  Switched to enterprise authentication."
        echo "============================================="
    else
        echo "============================================="
        echo "  No shared services config found to remove."
        echo "============================================="
    fi
    echo ""

    if [[ "$needs_login" == "true" ]]; then
        echo "  [INFO] No enterprise credentials found -- log in to activate your enterprise license."
        echo ""
        echo "  To log in:"
        echo "    CLI:    Open a new Terminal and run: claude login"
        echo "    VS Code: Click the Claude icon > Claude.ai Subscription > Authorize"
        echo ""
    else
        echo "  Next steps:"
        echo ""
        echo "  1. Close and reopen VS Code"
        echo "  2. Claude Code will prompt you to log in with your Enterprise subscription"
        echo "  3. Click 'Claude.ai Subscription' -> 'Open' -> 'Authorize'"
        echo ""
        echo "  CLI: Open a new Terminal and run 'claude' -- it will prompt for login."
        echo ""
    fi
    exit 0
fi

# ============================================================================
# SWITCH TO SHARED SERVICES -- config-only, apply shared services, no install
# ============================================================================

if [[ "$MODE_SWITCH_TO_SHARED_SERVICES" == "true" ]]; then
    validate_shared_services_params

    echo ""
    echo "============================================="
    echo "  Switching to Shared Services"
    echo "============================================="
    echo ""

    info "API key: sk-...${ss_api_key: -4}"
    info "Base URL: $ss_base_url"
    info "Sonnet model: $ss_sonnet"
    info "Opus model: $ss_opus"
    info "Haiku model: $ss_haiku"
    echo ""

    apply_shared_services_config

    echo ""
    echo "============================================="
    echo "  Switched to shared services."
    echo "============================================="
    echo ""
    echo "  Next steps:"
    echo ""
    echo "  1. Close and reopen VS Code (if it was already open)"
    echo "  2. Click the Claude icon (upper-right corner)"
    echo "  3. Start coding -- no login needed, routing via shared service"
    echo ""
    echo "  CLI usage:"
    echo "    1. Open a new Terminal window"
    echo "    2. Run: claude"
    echo "    (env vars are configured in ~/.claude/settings.json -- no export needed)"
    echo ""
    exit 0
fi

# ============================================================================
# FULL INSTALLATION
# ============================================================================

# ── Header ───────────────────────────────────────────────────────────────────

mode_label="Enterprise"
[[ "$MODE_SHARED_SERVICES" == "true" ]] && mode_label="Shared Services"

echo ""
echo "============================================="
echo "  Claude Code — macOS Unified Install"
echo "============================================="
echo ""
printf '  \033[90mScript version: %s\033[0m\n' "$SCRIPT_VERSION"
printf '  \033[90mMode: %s\033[0m\n' "$mode_label"
printf '  \033[90mEstimated time: 5-10 minutes on a fresh machine.\033[0m\n'
printf '  \033[90mThis script is safe to re-run — it skips anything already done.\033[0m\n'
echo ""

# Don't run as root
if [[ "$(id -u)" -eq 0 ]]; then
    fail "Do not run this script with sudo. Run as your normal user."
    exit 1
fi


# ── Validate Shared Services parameters ──────────────────────────────────────
if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
    validate_shared_services_params
    ok "Shared services configuration accepted."
    info "API key: sk-...${ss_api_key: -4}"
    info "Base URL: $ss_base_url"
    info "Sonnet model: $ss_sonnet"
    info "Opus model: $ss_opus"
    info "Haiku model: $ss_haiku"
    info "Configuration will be applied after all tools are installed."
    echo ""
fi

# ── Step 1: Pre-flight Checks ───────────────────────────────────────────────

step "1/$TOTAL_STEPS" "Pre-flight checks"

# macOS version
macos_version="$(sw_vers -productVersion 2>/dev/null || echo '0.0')"
if version_gte "$macos_version" "13.0"; then
    ok "macOS $macos_version (>= 13.0 Ventura required)."
else
    fail "macOS $macos_version — version 13.0 (Ventura) or later is required."
    exit 1
fi

# RAM
ram_bytes=$(sysctl -n hw.memsize 2>/dev/null || echo 0)
ram_gb=$((ram_bytes / 1073741824))
if [[ "$ram_gb" -ge 16 ]]; then
    ok "RAM: ${ram_gb} GB."
elif [[ "$ram_gb" -ge 8 ]]; then
    warn "RAM: ${ram_gb} GB — 16 GB recommended for best experience."
    warnings=true
else
    fail "RAM: ${ram_gb} GB — at least 8 GB required (16 GB recommended)."
    exit 1
fi

# Disk space
disk_free_gb=$(df -g / 2>/dev/null | awk 'NR==2 {print $4}')
if [[ -n "$disk_free_gb" ]] && [[ "$disk_free_gb" -ge 2 ]]; then
    ok "Disk space: ${disk_free_gb} GB free."
elif [[ -n "$disk_free_gb" ]]; then
    warn "Disk space: ${disk_free_gb} GB free — recommend at least 2 GB."
    warnings=true
fi

# Normalise PATH for tools that may be installed but not yet on the current
# shell's PATH (common on re-runs before the user has opened a new terminal).
# Homebrew: Apple Silicon installs to /opt/homebrew, not in default PATH.
if ! command -v brew &>/dev/null; then
    if [[ -f /opt/homebrew/bin/brew ]]; then
        eval "$(/opt/homebrew/bin/brew shellenv)"
    elif [[ -f /usr/local/bin/brew ]]; then
        eval "$(/usr/local/bin/brew shellenv)"
    fi
fi
# Claude Code: installer places binary in ~/.local/bin or ~/.claude/bin.
for _p in "$HOME/.local/bin" "$HOME/.claude/bin"; do
    if [[ -d "$_p" ]] && [[ ":$PATH:" != *":$_p:"* ]]; then
        export PATH="$_p:$PATH"
    fi
done
unset _p

# ── Sudo credential cache ─────────────────────────────────────────────────────
# Only needed for install modes, and only if Homebrew isn't already installed.
# Runs after pre-flight so users with Homebrew already present skip it entirely.
if { [[ "$MODE_ENTERPRISE" == "true" ]] || [[ "$MODE_SHARED_SERVICES" == "true" ]]; } \
        && ! command -v brew &>/dev/null \
        && ! sudo -n true 2>/dev/null; then

    # Read password char-by-char, printing * for each keystroke
    _read_pwd_stars() {
        local password="" char
        while IFS= read -r -s -n1 char 2>/dev/null; do
            case "$char" in
                '')            break ;;
                $'\177'|$'\b') [[ ${#password} -gt 0 ]] && { password="${password%?}"; printf '\b \b' >&2; } ;;
                *)             password+="$char"; printf '*' >&2 ;;
            esac
        done
        printf '\n' >&2
        printf '%s' "$password"
    }

    echo ""
    info "Homebrew installation requires your Mac login password (entered once, used throughout)."
    _pwd_attempt=0
    while true; do
        printf '      Password: ' >&2
        _pwd=$(_read_pwd_stars)
        if printf '%s\n' "$_pwd" | sudo -S -v 2>/dev/null; then
            unset _pwd
            echo ""
            break
        fi
        ((_pwd_attempt++)) || true
        if [[ $_pwd_attempt -ge 3 ]]; then
            fail "Too many incorrect password attempts."
            exit 1
        fi
        warn "  Incorrect password — please try again."
    done
    unset _pwd_attempt

    # Keep sudo alive in the background for the duration of the script
    ( while true; do sudo -n true; sleep 60; kill -0 "$$" 2>/dev/null || exit; done ) 2>/dev/null &
    SUDO_KEEPALIVE_PID=$!
fi

# ── Step 2: Network Connectivity ────────────────────────────────────────────

step "2/$TOTAL_STEPS" "Network connectivity"

cacert_args=()
[[ -f "$CORP_CERT_BUNDLE" ]] && cacert_args=(--cacert "$CORP_CERT_BUNDLE")

if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
    # ── Shared Services network check ──
    # SS users may not have claude.ai whitelisted (no Premium license / GUM group).
    # Test the shared services endpoint instead -- that is what they actually need.
    ss_net_ok=false
    if curl -sS "${cacert_args[@]+"${cacert_args[@]}"}" --max-time 10 "$ss_base_url/health" -o /dev/null 2>/dev/null; then
        ss_net_ok=true
    elif curl -sS --max-time 10 "$ss_base_url/health" -o /dev/null 2>/dev/null; then
        ss_net_ok=true
    fi

    if [[ "$ss_net_ok" == "true" ]]; then
        ok "Shared services endpoint is reachable ($ss_base_url)."
    else
        ssl_bypass_ok=false
        if curl -ksf --max-time 10 "$ss_base_url/health" -o /dev/null 2>/dev/null; then
            ssl_bypass_ok=true
        fi

        if [[ "$ssl_bypass_ok" == "true" ]]; then
            fail "SSL certificate error reaching shared services endpoint."
            info "The auto-detected certificate bundle may resolve this for Claude Code."
            info "Continuing with installation."
            warnings=true
        else
            fail "Cannot reach shared services endpoint: $ss_base_url"
            proxy="$(detect_proxy)"
            if [[ -n "$proxy" ]]; then
                info "Proxy detected ($proxy) but connection still failing."
            else
                info "No proxy detected. You may need HTTP_PROXY / HTTPS_PROXY environment variables."
            fi
            info "Verify you are on the PwC corporate network and the base URL is correct."
            exit 1
        fi
    fi

    # Also check general internet connectivity (needed for Homebrew, npm, VS Code marketplace)
    general_net_ok=false
    if curl -sS "${cacert_args[@]+"${cacert_args[@]}"}" --max-time 10 "https://registry.npmjs.org" -o /dev/null 2>/dev/null; then
        general_net_ok=true
    elif curl -sS --max-time 10 "https://registry.npmjs.org" -o /dev/null 2>/dev/null; then
        general_net_ok=true
    fi
    if [[ "$general_net_ok" == "true" ]]; then
        ok "General internet connectivity verified."
    else
        warn "Could not reach registry.npmjs.org -- some tool downloads may fail."
        info "Homebrew downloads may still work via different CDN routes."
        warnings=true
    fi

else
    # ── Enterprise network check ──
    net_ok=false
    # Use -sS (not -f) so that HTTP 403 etc. are still treated as "reachable".
    # A 403 is normal -- claude.ai returns it for unauthenticated requests.
    # What matters is that the TLS handshake + DNS resolution succeeded.
    if curl -sS "${cacert_args[@]+"${cacert_args[@]}"}" --max-time 10 "https://claude.ai" -o /dev/null 2>/dev/null; then
        net_ok=true
    elif curl -sS "${cacert_args[@]+"${cacert_args[@]}"}" --max-time 10 "https://api.anthropic.com" -o /dev/null 2>/dev/null; then
        net_ok=true
    elif curl -sS --max-time 10 "https://claude.ai" -o /dev/null 2>/dev/null; then
        net_ok=true
    fi

    if [[ "$net_ok" == "true" ]]; then
        ok "Claude.ai is reachable."

    else
        ssl_bypass_ok=false
        if curl -ksf --max-time 10 "https://claude.ai" -o /dev/null 2>/dev/null; then
            ssl_bypass_ok=true
        fi

        if [[ "$ssl_bypass_ok" == "true" ]]; then
            fail "SSL certificate error -- the network is reachable but the connection is not trusted."
            info "curl uses the system Keychain, which may not have the PwC proxy CA."
            info "Claude Code uses the auto-detected certificate bundle and may still work."
            info "Continuing with installation -- if Claude Code fails later, re-run this script."
            warnings=true
        else
            fail "Cannot reach claude.ai or api.anthropic.com."
            proxy="$(detect_proxy)"
            if [[ -n "$proxy" ]]; then
                info "Proxy detected ($proxy) but connection still failing."
                info "Check that your proxy is correctly configured and allows access to claude.ai."
            else
                info "No proxy detected. If you are on a corporate network, you may need proxy"
                info "settings configured (HTTP_PROXY / HTTPS_PROXY environment variables)."
            fi
            info ""
            info "You need a Premium Claude license to be unblocked from the firewall."
            info "Request one here (select 'Grant new license - with Claude Code premium'):"
            info "  https://pwc.sharepoint.com/sites/US-xLoS-AIinnovationCOE/SitePages/Claude.ai.aspx"
            info "Once provisioned, re-run this script."
            exit 1
        fi
    fi
fi

# ── Step 3: SSL Certificates ────────────────────────────────────────────────

step "3/$TOTAL_STEPS" "SSL certificates"

cert_installed=false
certs_already_configured=false
mkdir -p "$HOME/.claude/certs" 2>/dev/null || true

# Skip auto-detection if cert bundles already exist and env vars are set (idempotent)
if [[ -f "$CORP_CERT_BUNDLE" ]] && [[ -f "$MERGED_CERT_BUNDLE" ]]; then
    corp_cert_count=$(grep -c 'BEGIN CERTIFICATE' "$CORP_CERT_BUNDLE" 2>/dev/null || echo 0)
    # Check if shell rc already has the env vars configured
    if [[ "$corp_cert_count" -gt 0 ]] && grep -q 'corporate-ca-bundle.pem' "$RCFILE" 2>/dev/null; then
        skip "Certificate bundles already exist ($corp_cert_count corporate CA(s))."
        ok "Env vars and tool configs already set (dual-bundle approach)."
        # Deduplicate existing merged bundle in case it was created before dedup logic was added.
        # LibreSSL 3.3+ rejects bundles with duplicate certs ("cert already in hash table").
        _dedup_inline() {
            local bundle="$1"; [[ -f "$bundle" ]] || return 0
            local orig_count tmp in_cert=false cert_block="" fp seen_fps=""
            orig_count=$(grep -c 'BEGIN CERTIFICATE' "$bundle" 2>/dev/null || echo 0)
            tmp="${bundle}.dedup"
            : > "$tmp"
            while IFS= read -r line; do
                if [[ "$line" == *"BEGIN CERTIFICATE"* ]]; then in_cert=true; cert_block="$line"$'\n'
                elif [[ "$line" == *"END CERTIFICATE"* ]]; then
                    cert_block+="$line"$'\n'
                    # Hash the raw cert block — simpler and more reliable than openssl x509 parsing.
                    # Fail-open: if hashing fails, include the cert to avoid silent data loss.
                    fp=$(printf '%s' "$cert_block" | shasum -a 1 2>/dev/null | cut -d' ' -f1)
                    if [[ -z "$fp" ]] || ! printf '%s\n' $seen_fps | grep -qxF "$fp"; then
                        [[ -n "$fp" ]] && seen_fps="$seen_fps $fp"
                        printf '%s' "$cert_block" >> "$tmp"
                    fi
                    in_cert=false; cert_block=""
                elif [[ "$in_cert" == true ]]; then cert_block+="$line"$'\n'
                else echo "$line" >> "$tmp"; fi
            done < "$bundle"
            # Safety check: if dedup produced fewer certs than expected, keep the original.
            local new_count
            new_count=$(grep -c 'BEGIN CERTIFICATE' "$tmp" 2>/dev/null || echo 0)
            if [[ "$new_count" -gt 0 ]]; then
                mv "$tmp" "$bundle"
            else
                rm -f "$tmp"
            fi
        }
        _dedup_inline "$MERGED_CERT_BUNDLE"
        unset -f _dedup_inline
        # Re-export env vars for the current session (not persisted yet in this shell).
        export NODE_EXTRA_CA_CERTS="$CORP_CERT_BUNDLE"
        export SSL_CERT_FILE="$MERGED_CERT_BUNDLE"
        export REQUESTS_CA_BUNDLE="$MERGED_CERT_BUNDLE"
        export CURL_CA_BUNDLE="$MERGED_CERT_BUNDLE"
        export GIT_SSL_CAINFO="$MERGED_CERT_BUNDLE"
        export UV_NATIVE_TLS=1
        # macOS /usr/bin/git uses SecureTransport (LibreSSL) which relies on the
        # macOS Keychain for trust -- PEM env vars and gitconfig are ignored.
        # Import corporate CAs into the login keychain so SecureTransport trusts
        # connections through the corporate proxy (needed before Homebrew install).
        _keychain_db="$HOME/Library/Keychains/login.keychain-db"
        if [[ -f "$CORP_CERT_BUNDLE" ]] && [[ -f "$_keychain_db" ]]; then
            security import "$CORP_CERT_BUNDLE" -k "$_keychain_db" -T /usr/bin/git 2>/dev/null || true
        fi
        # Also set http.sslCAInfo for any git that uses OpenSSL (e.g. Homebrew-installed git).
        xcode-select -p &>/dev/null && /usr/bin/git config --global http.sslCAInfo "$MERGED_CERT_BUNDLE" 2>/dev/null || true
        cert_installed=true
        certs_already_configured=true
    fi
fi

if [[ "$certs_already_configured" != "true" ]] && command -v openssl &>/dev/null; then
    corp_certs=""

    # Deduplicate a PEM cert bundle in-place by SHA-1 fingerprint.
    # Removes duplicate certs that cause LibreSSL "cert already in hash table" errors.
    # Uses shasum-based dedup (no associative arrays, no openssl x509 parsing) for reliability
    # and bash 3.2 compatibility (macOS default shell). Fail-open: if hashing fails, the cert
    # is included rather than silently dropped. Safety check: if dedup empties the bundle, keep original.
    _dedup_cert_bundle() {
        local bundle="$1"
        [[ -f "$bundle" ]] || return 0
        local tmp="${bundle}.dedup"
        local in_cert=false cert_block="" fp seen_fps=""
        : > "$tmp"
        while IFS= read -r line; do
            if [[ "$line" == *"BEGIN CERTIFICATE"* ]]; then
                in_cert=true
                cert_block="$line"$'\n'
            elif [[ "$line" == *"END CERTIFICATE"* ]]; then
                cert_block+="$line"$'\n'
                fp=$(printf '%s' "$cert_block" | shasum -a 1 2>/dev/null | cut -d' ' -f1)
                if [[ -z "$fp" ]] || ! printf '%s\n' $seen_fps | grep -qxF "$fp"; then
                    [[ -n "$fp" ]] && seen_fps="$seen_fps $fp"
                    printf '%s' "$cert_block" >> "$tmp"
                fi
                in_cert=false
                cert_block=""
            elif [[ "$in_cert" == true ]]; then
                cert_block+="$line"$'\n'
            else
                echo "$line" >> "$tmp"
            fi
        done < "$bundle"
        local new_count
        new_count=$(grep -c 'BEGIN CERTIFICATE' "$tmp" 2>/dev/null || echo 0)
        if [[ "$new_count" -gt 0 ]]; then
            mv "$tmp" "$bundle"
        else
            rm -f "$tmp"
        fi
    }

    # Parse a block of PEM text and append any CA certs found to corp_certs.
    _parse_ca_certs() {
        local pem_input="$1"
        local current_cert="" in_cert=false cert_issuer cert_subject is_ca
        while IFS= read -r line; do
            if [[ "$line" == *"BEGIN CERTIFICATE"* ]]; then
                in_cert=true
                current_cert="$line"$'\n'
            elif [[ "$line" == *"END CERTIFICATE"* ]]; then
                current_cert+="$line"$'\n'
                in_cert=false
                cert_issuer=$(echo "$current_cert" | openssl x509 -noout -issuer 2>/dev/null || true)
                cert_subject=$(echo "$current_cert" | openssl x509 -noout -subject 2>/dev/null || true)
                is_ca=$(echo "$current_cert" | openssl x509 -noout -text 2>/dev/null | grep -c "CA:TRUE" || true)
                if [[ "$is_ca" -gt 0 ]]; then
                    corp_certs+="# Issuer: ${cert_issuer#*=}"$'\n'
                    corp_certs+="# Subject: ${cert_subject#*=}"$'\n'
                    corp_certs+="$current_cert"$'\n'
                fi
                current_cert=""
            elif [[ "$in_cert" == "true" ]]; then
                current_cert+="$line"$'\n'
            fi
        done <<< "$pem_input"
    }

    # Tier 1: macOS System keychain — PwC IT deploys the corporate proxy CA here
    # via MDM profile. More reliable than TLS sniffing because it doesn't depend
    # on the server including intermediates in the TLS handshake.
    info "Checking macOS System keychain for corporate CA certificates..."
    keychain_pem=$(security find-certificate -a -p /Library/Keychains/System.keychain 2>/dev/null || true)
    if [[ -n "$keychain_pem" ]]; then
        _parse_ca_certs "$keychain_pem"
    fi

    # Tier 2: TLS chain sniffing — fallback if keychain had no CA certs.
    if [[ -z "$corp_certs" ]]; then
        info "No CA certs found in System keychain. Trying TLS chain detection..."
        chain_output=$(echo | openssl s_client -connect engagementhub.pwcinternal.com:443 -showcerts 2>/dev/null) || true
        if [[ -n "$chain_output" ]] && echo "$chain_output" | grep -q "BEGIN CERTIFICATE"; then
            _parse_ca_certs "$chain_output"
            if [[ -z "$corp_certs" ]]; then
                info "TLS chain connected but contained no CA certs (server may not send intermediates)."
            fi
        else
            info "Could not reach engagementhub.pwcinternal.com — may not be on PwC network."
        fi
    fi

    if [[ -n "$corp_certs" ]]; then
            # Create corporate-only bundle
            {
                echo "# Corporate CA Bundle - Auto-detected $(date +%Y-%m-%d)"
                echo "# Use with NODE_EXTRA_CA_CERTS (augments existing trust store)"
                echo ""
                echo "$corp_certs"
            } > "$CORP_CERT_BUNDLE"

            # Create merged bundle (system CAs + corporate)
            # SSL_CERT_FILE *replaces* the trust store, so merged must include system CAs
            system_cas=""
            if [[ -f "/etc/ssl/cert.pem" ]]; then
                system_cas="/etc/ssl/cert.pem"
            elif security find-certificate -a -p /System/Library/Keychains/SystemRootCertificates.keychain > /dev/null 2>&1; then
                # Fallback: export from macOS system keychain
                system_cas_tmp="$HOME/.claude/certs/system-ca-export.pem"
                security find-certificate -a -p /System/Library/Keychains/SystemRootCertificates.keychain > "$system_cas_tmp" 2>/dev/null
                system_cas="$system_cas_tmp"
            fi

            system_cas_merged=false
            if [[ -n "$system_cas" ]]; then
                {
                    echo "# Merged CA Bundle - System + Corporate CAs"
                    echo "# Auto-detected $(date +%Y-%m-%d)"
                    echo ""
                    echo "# ===== SYSTEM CA CERTIFICATES ====="
                    cat "$system_cas"
                    echo ""
                    echo "# ===== CORPORATE CA CERTIFICATES ====="
                    echo "$corp_certs"
                } > "$MERGED_CERT_BUNDLE"
                # Clean up temp system CA export if one was created
                [[ -n "${system_cas_tmp:-}" ]] && rm -f "$system_cas_tmp"
                # Deduplicate merged bundle — LibreSSL 3.3+ rejects bundles with duplicate certs
                _dedup_cert_bundle "$MERGED_CERT_BUNDLE"
                system_cas_merged=true
            else
                # Cannot get system CAs — only create corporate-only bundle.
                # Do NOT set SSL_CERT_FILE: it replaces the trust store, so a
                # corp-only bundle would break public HTTPS.
                warn "Could not locate system CA certificates — SSL_CERT_FILE will not be set."
                cp "$CORP_CERT_BUNDLE" "$MERGED_CERT_BUNDLE"
            fi

            # Validate merged bundle has enough certs (system + corporate)
            merged_cert_count=$(grep -c 'BEGIN CERTIFICATE' "$MERGED_CERT_BUNDLE" 2>/dev/null || echo 0)
            if [[ "$system_cas_merged" == "true" ]] && [[ "$merged_cert_count" -lt 10 ]]; then
                warn "Merged bundle has only $merged_cert_count certificates (expected 10+)."
                info "System CAs may not have been included. Public HTTPS may fail for some tools."
                info "If you see SSL errors, contact your IT support for the corporate CA bundle."
            fi

            cert_installed=true
            ok "Certificate auto-detection successful — corporate CAs captured ($merged_cert_count certs in merged bundle)."

            # Set env vars with dual-bundle approach.
            # SSL_CERT_FILE replaces the trust store, so only set it when the merged
            # bundle actually contains system CAs — otherwise public HTTPS would break.
            if [[ "$system_cas_merged" == "true" ]]; then
                ensure_in_rcfile 'corporate-ca-bundle.pem' \
                    '# Corporate certificates for Claude Code (auto-detected by installer)' \
                    "export NODE_EXTRA_CA_CERTS=\"\$HOME/.claude/certs/corporate-ca-bundle.pem\"" \
                    "export SSL_CERT_FILE=\"\$HOME/.claude/certs/merged-ca-bundle.pem\"" \
                    "export REQUESTS_CA_BUNDLE=\"\$HOME/.claude/certs/merged-ca-bundle.pem\"" \
                    "export CURL_CA_BUNDLE=\"\$HOME/.claude/certs/merged-ca-bundle.pem\"" \
                    "export GIT_SSL_CAINFO=\"\$HOME/.claude/certs/merged-ca-bundle.pem\"" \
                    "export UV_NATIVE_TLS=1"
                export SSL_CERT_FILE="$MERGED_CERT_BUNDLE"
                export REQUESTS_CA_BUNDLE="$MERGED_CERT_BUNDLE"
                export CURL_CA_BUNDLE="$MERGED_CERT_BUNDLE"
                export GIT_SSL_CAINFO="$MERGED_CERT_BUNDLE"
                # macOS /usr/bin/git uses SecureTransport (LibreSSL) which relies on the
                # macOS Keychain -- PEM env vars are ignored.  Import to login keychain.
                _keychain_db="$HOME/Library/Keychains/login.keychain-db"
                [[ -f "$_keychain_db" ]] && security import "$CORP_CERT_BUNDLE" -k "$_keychain_db" -T /usr/bin/git 2>/dev/null || true
                # Also set http.sslCAInfo for any OpenSSL-backed git (e.g. Homebrew git).
                xcode-select -p &>/dev/null && /usr/bin/git config --global http.sslCAInfo "$MERGED_CERT_BUNDLE" 2>/dev/null || true
            else
                ensure_in_rcfile 'corporate-ca-bundle.pem' \
                    '# Corporate certificates for Claude Code (auto-detected by installer)' \
                    "export NODE_EXTRA_CA_CERTS=\"\$HOME/.claude/certs/corporate-ca-bundle.pem\"" \
                    "export UV_NATIVE_TLS=1"
            fi

            export NODE_EXTRA_CA_CERTS="$CORP_CERT_BUNDLE"
            export UV_NATIVE_TLS=1

            # Configure tools (npm only — git cert config runs in the git step after git is installed)
            if command -v npm &>/dev/null && [[ "$system_cas_merged" == "true" ]]; then
                npm config set cafile "$MERGED_CERT_BUNDLE" 2>/dev/null || true
            fi

            ok "Env vars and tool configs set (dual-bundle approach)."
    fi
fi

# If auto-detection didn't find certs, warn and continue
if [[ "$cert_installed" == "false" ]]; then
    warn "Certificate auto-detection did not find PwC corporate CA certificates."
    info "This is normal if you're not behind the PwC corporate proxy."
    info "If you encounter SSL errors later, contact your IT support for the corporate CA bundle."
    warnings=true
fi

# ── Step 4: Xcode Command Line Tools ────────────────────────────────────────

step "4/$TOTAL_STEPS" "Xcode Command Line Tools"

if xcode-select -p &>/dev/null; then
    skip "Xcode CLT already installed."
else
    info "Installing Xcode Command Line Tools (this may take several minutes)..."
    # Non-interactive install: create the sentinel file so softwareupdate can
    # find and install the CLT package without triggering the GUI dialog.
    touch /tmp/.com.apple.dt.CommandLineTools.installondemand.in-progress
    clt_label=$(softwareupdate -l 2>/dev/null \
        | awk '/\* Label: Command Line Tools/ {sub(/.*Label: /, ""); print}' \
        | sort -V | tail -1)
    if [[ -n "$clt_label" ]]; then
        info "Found package: $clt_label"
        clt_log=$(mktemp)
        softwareupdate -i "$clt_label" --agree-to-license >"$clt_log" 2>&1 &
        clt_pid=$!
        clt_elapsed=0
        while kill -0 "$clt_pid" 2>/dev/null; do
            printf "\r  [INFO] Installing... %ds elapsed" "$clt_elapsed" >&2
            sleep 5
            clt_elapsed=$((clt_elapsed + 5))
        done
        printf "\r%50s\r" "" >&2  # clear the progress line
        wait "$clt_pid" || { cat "$clt_log"; rm -f "$clt_log"; false; }
        rm -f "$clt_log"
    else
        # Fallback: trigger the dialog-based installer if softwareupdate can't find it
        info "softwareupdate package not found — falling back to dialog installer."
        info "A system dialog will appear — click 'Install' to proceed."
        xcode-select --install 2>/dev/null || true
        info "Waiting for Xcode CLT installation to finish (this can take 5-20 minutes)..."
        xcode_wait=0
        until xcode-select -p &>/dev/null; do
            sleep 5
            xcode_wait=$((xcode_wait + 5))
            if [[ "$xcode_wait" -ge 1800 ]]; then
                fail "Xcode CLT installation timed out after 30 minutes."
                info "Try installing manually: xcode-select --install"
                rm -f /tmp/.com.apple.dt.CommandLineTools.installondemand.in-progress
                exit 1
            fi
        done
    fi
    rm -f /tmp/.com.apple.dt.CommandLineTools.installondemand.in-progress

    if xcode-select -p &>/dev/null; then
        ok "Xcode CLT installed."
    else
        fail "Xcode CLT installation failed. Try running: xcode-select --install"
        exit 1
    fi
fi

# ── Step 5: Homebrew ─────────────────────────────────────────────────────────

step "5/$TOTAL_STEPS" "Homebrew"

if command -v brew &>/dev/null; then
    skip "Homebrew already installed."
else
    info "Installing Homebrew..."

    # curl args for corporate cert bundle (curl works fine through the proxy).
    brew_cacert_args=()
    [[ -f "$CORP_CERT_BUNDLE" ]] && brew_cacert_args=(--cacert "$CORP_CERT_BUNDLE")

    # ── Attempt 1: Official Homebrew installer ───────────────────────────────
    # This uses git internally, which may fail on corporate networks where
    # LibreSSL cannot negotiate TLS through the SSL-intercepting proxy
    # (SSL_ERROR_SYSCALL). We try it first since it's the canonical method.
    _brew_installed=false

    brew_install_script=$(curl -fsSL "${brew_cacert_args[@]+"${brew_cacert_args[@]}"}" \
        https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh 2>/dev/null) || true

    if [[ -n "$brew_install_script" ]] && ! echo "$brew_install_script" | grep -qi "<html"; then
        # Set HOMEBREW_CURLRC so Homebrew's internal curl calls use the corporate CA.
        _brew_curlrc=""
        if [[ -f "$MERGED_CERT_BUNDLE" ]]; then
            _brew_curlrc=$(mktemp /tmp/pwc-brew-curlrc-XXXXXX)
            echo "cacert = $MERGED_CERT_BUNDLE" > "$_brew_curlrc"
            export HOMEBREW_CURLRC="$_brew_curlrc"
        elif [[ -f "$CORP_CERT_BUNDLE" ]]; then
            _brew_curlrc=$(mktemp /tmp/pwc-brew-curlrc-XXXXXX)
            echo "cacert = $CORP_CERT_BUNDLE" > "$_brew_curlrc"
            export HOMEBREW_CURLRC="$_brew_curlrc"
        fi

        # macOS /usr/bin/git uses SecureTransport (LibreSSL) which often fails
        # through corporate proxies with SSL_ERROR_SYSCALL.  Temporarily set
        # git config to disable SSL verification and force HTTP/1.1.
        _git_ssl_was_set=false
        _git_http_ver_was_set=false
        if [[ -f "$CORP_CERT_BUNDLE" ]]; then
            info "Corporate proxy detected -- configuring git for Homebrew install."
            _old_ssl_verify=$(/usr/bin/git config --global --get http.sslVerify 2>/dev/null) && _git_ssl_was_set=true || true
            _old_http_ver=$(/usr/bin/git config --global --get http.version 2>/dev/null) && _git_http_ver_was_set=true || true
            /usr/bin/git config --global http.sslVerify false
            /usr/bin/git config --global http.version HTTP/1.1
            export GIT_SSL_NO_VERIFY=true
        fi

        if NONINTERACTIVE=1 /bin/bash -c "$brew_install_script"; then
            _brew_installed=true
        else
            warn "Standard Homebrew installer failed (likely git SSL issue on corporate network)."
        fi

        # Restore git SSL settings.
        if [[ "$_git_ssl_was_set" == "true" ]]; then
            /usr/bin/git config --global http.sslVerify "$_old_ssl_verify"
        else
            /usr/bin/git config --global --unset http.sslVerify 2>/dev/null || true
        fi
        if [[ "$_git_http_ver_was_set" == "true" ]]; then
            /usr/bin/git config --global http.version "$_old_http_ver"
        else
            /usr/bin/git config --global --unset http.version 2>/dev/null || true
        fi
        unset GIT_SSL_NO_VERIFY 2>/dev/null || true
        [[ -n "$_brew_curlrc" ]] && rm -f "$_brew_curlrc" && unset HOMEBREW_CURLRC || true
    fi

    # ── Attempt 2: Tarball-based install (bypasses git entirely) ─────────────
    # curl works fine through the corporate proxy, so we download the Homebrew
    # tarball directly and extract it.  This avoids the LibreSSL git issue.
    if [[ "$_brew_installed" != "true" ]]; then
        info "Trying tarball-based Homebrew install (bypasses git)..."

        # Determine install prefix (Apple Silicon vs Intel).
        if [[ "$(uname -m)" == "arm64" ]]; then
            _brew_prefix="/opt/homebrew"
        else
            _brew_prefix="/usr/local/Homebrew"
        fi

        _brew_tarball=$(mktemp /tmp/brew-tarball-XXXXXX.tar.gz)

        if curl -fsSL "${brew_cacert_args[@]+"${brew_cacert_args[@]}"}" \
            -o "$_brew_tarball" \
            "https://github.com/Homebrew/brew/tarball/master" 2>/dev/null; then

            # Create the prefix directory (needs sudo on Apple Silicon).
            sudo mkdir -p "$_brew_prefix" 2>/dev/null || mkdir -p "$_brew_prefix" 2>/dev/null
            sudo chown -R "$(whoami):admin" "$_brew_prefix" 2>/dev/null || true

            # Extract tarball (strip the top-level directory name).
            if tar xzf "$_brew_tarball" --strip-components=1 -C "$_brew_prefix" 2>/dev/null; then
                # Create the standard Homebrew directory structure.
                mkdir -p "$_brew_prefix"/{bin,etc,include,lib,sbin,share,var,opt,Cellar,Caskroom,Frameworks} 2>/dev/null || true
                mkdir -p "$_brew_prefix"/share/{doc,man,zsh} 2>/dev/null || true
                mkdir -p "$_brew_prefix"/share/zsh/site-functions 2>/dev/null || true
                mkdir -p "$_brew_prefix"/share/man/man1 2>/dev/null || true
                mkdir -p "$_brew_prefix"/var/homebrew/linked 2>/dev/null || true
                chmod go-w "$_brew_prefix"/share/zsh "$_brew_prefix"/share/zsh/site-functions 2>/dev/null || true

                # Add to /etc/paths.d so brew is in PATH system-wide.
                echo "$_brew_prefix/bin" | sudo tee /etc/paths.d/homebrew >/dev/null 2>&1 || true

                _brew_installed=true
                ok "Homebrew installed via tarball."
            else
                warn "Failed to extract Homebrew tarball."
            fi
        else
            warn "Failed to download Homebrew tarball."
        fi
        rm -f "$_brew_tarball"
    fi

    if [[ "$_brew_installed" != "true" ]]; then
        fail "Homebrew installation failed."
        if [[ "$cert_installed" == "true" ]]; then
            info "SSL certificates were detected. Try opening a new terminal and re-running this script."
        else
            info "This may be an SSL certificate issue. Re-run this script while on the PwC network."
        fi
        exit 1
    fi

    # Apple Silicon: add brew to PATH for current session.
    if [[ "$(uname -m)" == "arm64" ]] && [[ -f /opt/homebrew/bin/brew ]]; then
        eval "$(/opt/homebrew/bin/brew shellenv)"
        ensure_in_rcfile '/opt/homebrew/bin/brew shellenv' \
            '# Homebrew (added by Claude Code installer)' \
            'eval "$(/opt/homebrew/bin/brew shellenv)"'
    fi

    if command -v brew &>/dev/null; then
        ok "Homebrew ready."
    else
        fail "Homebrew installation failed."
        exit 1
    fi
fi

# ── Step 6: Git ──────────────────────────────────────────────────────────────

step "6/$TOTAL_STEPS" "Git"

if command -v git &>/dev/null; then
    skip "Git already installed: $(git --version 2>/dev/null)."
else
    info "Installing Git via Homebrew..."
    if ! brew install git; then
        fail "Git installation failed."
        exit 1
    fi
    if command -v git &>/dev/null; then
        ok "Git installed: $(git --version 2>/dev/null)."
    else
        fail "Git installation failed."
        exit 1
    fi
fi

# Set default branch to main
current_default=$(git config --global --get init.defaultBranch 2>/dev/null || true)
if [[ -z "$current_default" ]]; then
    git config --global init.defaultBranch main 2>/dev/null || true
    ok "Default branch set to 'main'."
else
    skip "Default branch already set to '$current_default'."
fi

# Configure git to use cert bundle if auto-detected
if [[ -f "$MERGED_CERT_BUNDLE" ]]; then
    git config --global http.sslCAInfo "$MERGED_CERT_BUNDLE" 2>/dev/null || true
elif [[ -f "$CORP_CERT_BUNDLE" ]]; then
    git config --global http.sslCAInfo "$CORP_CERT_BUNDLE" 2>/dev/null || true
fi

# ── Step 7: Python 3 ────────────────────────────────────────────────────────

step "7/$TOTAL_STEPS" "Python 3"

if command -v python3 &>/dev/null; then
    skip "Python 3 already installed: $(python3 --version 2>&1)."
else
    info "Installing Python 3 via Homebrew..."
    if brew install python; then
        ok "Python 3 installed: $(python3 --version 2>&1)."
    else
        warn "Python 3 installation failed — shared services config may not work."
        warnings=true
    fi
fi

# ── Step 8: Claude Code CLI ─────────────────────────────────────────────────

step "8/$TOTAL_STEPS" "Claude Code CLI"

# Check for stale npm installation
if command -v npm &>/dev/null; then
    npm_claude="$(npm list -g @anthropic-ai/claude-code 2>/dev/null || true)"
    if echo "$npm_claude" | grep -q "claude-code"; then
        info "Found deprecated npm-based Claude Code — removing..."
        npm uninstall -g @anthropic-ai/claude-code 2>/dev/null || true
        ok "Removed stale npm Claude Code."
    fi
fi

# Check if existing claude is an npm ghost
claude_needs_install=true
if command -v claude &>/dev/null; then
    claude_path="$(command -v claude)"
    if [[ "$claude_path" == *"node_modules"* ]] || [[ "$claude_path" == *"npm"* ]]; then
        warn "Claude Code found at $claude_path — this is a deprecated npm installation."
        info "Installing the native version..."
    else
        skip "Claude Code already installed: $(claude --version 2>/dev/null || echo 'unknown')."
        info "To update later, run: claude update."
        claude_needs_install=false
    fi
fi

if [[ "$claude_needs_install" == "true" ]]; then
    info "Installing Claude Code..."
    install_ok=false

    if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
        # SS users cannot reach claude.ai -- skip the official installer and go straight to Homebrew.
        info "Using Homebrew for shared services install..."
        if command -v brew &>/dev/null && brew_cask_install claude-code 2>/dev/null; then
            install_ok=true
            ok "Claude Code installed via Homebrew."
        fi
    else
        # Enterprise: try official installer first, then Homebrew fallback
        dl_cacert_args=()
        [[ -f "$CORP_CERT_BUNDLE" ]] && dl_cacert_args=(--cacert "$CORP_CERT_BUNDLE")
        installer_script=$(curl -fsSL "${dl_cacert_args[@]+"${dl_cacert_args[@]}"}" https://claude.ai/install.sh 2>/dev/null) || true

        if [[ -n "$installer_script" ]] && ! echo "$installer_script" | grep -qi "<html" && echo "$installer_script" | grep -q "claude"; then
            if echo "$installer_script" | bash; then
                install_ok=true
            fi
        fi

        if [[ "$install_ok" == "false" ]]; then
            info "Primary installer failed -- trying Homebrew fallback..."
            if command -v brew &>/dev/null && brew_cask_install claude-code 2>/dev/null; then
                install_ok=true
                ok "Claude Code installed via Homebrew."
            fi
        fi
    fi

    if [[ "$install_ok" == "true" ]]; then
        # The official installer may place claude in ~/.local/bin or ~/.claude/bin.
        # Add both to the current session PATH so the command -v check below works.
        for _bin_dir in "$HOME/.local/bin" "$HOME/.claude/bin"; do
            if [[ -d "$_bin_dir" ]] && [[ ":$PATH:" != *":$_bin_dir:"* ]]; then
                export PATH="$_bin_dir:$PATH"
            fi
        done
        # Persist whichever directory the binary actually landed in
        [[ -f "$HOME/.local/bin/claude" ]] && ensure_in_rcfile '.local/bin' \
            '# Claude Code (added by installer)' \
            'export PATH="$HOME/.local/bin:$PATH"'
        [[ -f "$HOME/.claude/bin/claude" ]] && ensure_in_rcfile '.claude/bin' \
            '# Claude Code (added by installer)' \
            'export PATH="$HOME/.claude/bin:$PATH"'
        if command -v claude &>/dev/null; then
            ok "Claude Code installed: $(claude --version 2>/dev/null || echo 'unknown version')."
        else
            fail "Claude Code installed but 'claude' not found on PATH."
            info "Try opening a new terminal and re-running this script."
            exit 1
        fi
    else
        fail "Claude Code installation failed."
        if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
            info "Homebrew is the primary install method for shared services users."
            info "Ensure Homebrew is installed (the script should have installed it in Step 5)."
        else
            info "Both official installer and Homebrew methods failed."
            if [[ "$warnings" == "true" ]]; then
                info "This may be related to the SSL certificate warning in Step 3."
            fi
        fi
        exit 1
    fi
fi

# NOTE: Skipping 'claude /terminal-setup' — it's a slash command that launches a full
# interactive Claude session (with workspace trust prompt), stalling the install script.
# Terminal integration (Shift+Enter keybinding) is set up automatically when the user
# first launches Claude Code interactively.

# ── Step 9: VS Code + Claude Code Extension ─────────────────────────────────

step "9/$TOTAL_STEPS" "Visual Studio Code + Claude Code extension"

vscode_app="/Applications/Visual Studio Code.app"

# Helper: verify VS Code app bundle is intact (codesign check).
vscode_is_healthy() {
    [[ -d "$vscode_app" ]] && codesign --verify --deep --strict "$vscode_app" 2>/dev/null
}

# Helper: install VS Code via direct curl download (fallback when Homebrew fails).
# Downloads the .zip from Microsoft, extracts to /Applications.
install_vscode_curl() {
    local arch_suffix="darwin-arm64"
    [[ "$(uname -m)" != "arm64" ]] && arch_suffix="darwin"
    local dl_url="https://update.code.visualstudio.com/latest/${arch_suffix}/stable"
    local zip_path="/tmp/vscode-download-$$.zip"

    local _dl_cert_args=()
    [[ -f "$MERGED_CERT_BUNDLE" ]] && _dl_cert_args=(--cacert "$MERGED_CERT_BUNDLE")
    [[ ${#_dl_cert_args[@]} -eq 0 && -f "$CORP_CERT_BUNDLE" ]] && _dl_cert_args=(--cacert "$CORP_CERT_BUNDLE")

    info "Downloading VS Code directly..."
    if ! curl -fsSL "${_dl_cert_args[@]+"${_dl_cert_args[@]}"}" \
        --max-time 300 -o "$zip_path" -L "$dl_url" 2>/dev/null; then
        rm -f "$zip_path"
        return 1
    fi

    # Verify we got a zip file, not an HTML redirect page
    if ! file "$zip_path" 2>/dev/null | grep -qi "zip"; then
        rm -f "$zip_path"
        return 1
    fi

    info "Extracting VS Code to /Applications..."
    local tmp_extract="/tmp/vscode-extract-$$"
    rm -rf "$tmp_extract"
    mkdir -p "$tmp_extract"
    if ! unzip -q "$zip_path" -d "$tmp_extract" 2>/dev/null; then
        rm -f "$zip_path"
        rm -rf "$tmp_extract"
        return 1
    fi
    rm -f "$zip_path"

    # Move the .app bundle to /Applications
    local extracted_app
    extracted_app=$(find "$tmp_extract" -maxdepth 2 -name "Visual Studio Code*.app" -type d 2>/dev/null | head -1)
    if [[ -z "$extracted_app" ]]; then
        rm -rf "$tmp_extract"
        return 1
    fi

    if ! mv "$extracted_app" "$vscode_app" 2>/dev/null; then
        sudo mv "$extracted_app" "$vscode_app" 2>/dev/null || { rm -rf "$tmp_extract"; return 1; }
    fi
    rm -rf "$tmp_extract"
    return 0
}

vscode_install_ok=false

if [[ -d "$vscode_app" ]] || command -v code &>/dev/null; then
    if vscode_is_healthy; then
        skip "VS Code already installed."
        vscode_install_ok=true
    else
        warn "VS Code found but app bundle is corrupted or incomplete."
        info "Removing broken installation and reinstalling..."
        sudo rm -rf "$vscode_app" 2>/dev/null || rm -rf "$vscode_app" 2>/dev/null || true
        if [[ -d "$vscode_app" ]]; then
            fail "Could not remove corrupted VS Code. Please manually move it to Trash and re-run."
            exit 1
        fi
    fi
fi

if [[ "$vscode_install_ok" == "false" ]]; then
    # Attempt 1: Homebrew (with corporate cert support)
    info "Installing VS Code via Homebrew..."
    if brew_cask_install visual-studio-code 2>/dev/null && vscode_is_healthy; then
        ok "VS Code installed via Homebrew."
        vscode_install_ok=true
    else
        # Attempt 2: Direct curl download
        warn "Homebrew download failed -- trying direct download..."
        if install_vscode_curl && vscode_is_healthy; then
            ok "VS Code installed via direct download."
            vscode_install_ok=true
        else
            # Attempt 3: Manual instructions (don't exit -- VS Code is not strictly required for CLI usage)
            warn "VS Code automatic installation failed."
            warn "The download domain (update.code.visualstudio.com) may be blocked by the firewall."
            echo ""
            echo "  To install VS Code manually:"
            echo "    1. Open Safari and go to: https://code.visualstudio.com/download"
            echo "    2. Click the Mac download button (Apple Silicon or Intel)"
            echo "    3. Open the downloaded .zip file"
            echo "    4. Drag 'Visual Studio Code.app' to your Applications folder"
            echo "    5. Re-run this script -- it will detect VS Code and continue"
            echo ""
            warnings=true
        fi
    fi
fi

# Remove macOS quarantine attribute if present -- VS Code downloaded outside Homebrew
# (e.g. from the website .zip) carries com.apple.quarantine, which causes the
# "is damaged and can't be opened" Gatekeeper popup.
if [[ -d "$vscode_app" ]] && xattr "$vscode_app" 2>/dev/null | grep -q com.apple.quarantine; then
    info "Removing macOS quarantine flag from VS Code..."
    if ! xattr -cr "$vscode_app" 2>/dev/null; then
        info "Needs elevated permissions -- you may be prompted for your password."
        sudo xattr -cr "$vscode_app" 2>/dev/null || true
    fi
    if xattr "$vscode_app" 2>/dev/null | grep -q com.apple.quarantine; then
        warn "Could not remove quarantine flag. If VS Code shows a 'damaged' popup,"
        warn "run manually: sudo xattr -cr \"$vscode_app\""
    else
        ok "Quarantine flag removed."
    fi
fi

# Ensure 'code' CLI is available
if ! command -v code &>/dev/null; then
    vscode_bin="/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code"
    if [[ -f "$vscode_bin" ]]; then
        info "Setting up 'code' CLI..."
        # Try user-local path first to avoid a sudo password prompt.
        mkdir -p "$HOME/.local/bin"
        if ln -sf "$vscode_bin" "$HOME/.local/bin/code" 2>/dev/null; then
            ensure_in_rcfile '.local/bin' \
                '# Local bin (added by Claude Code installer)' \
                'export PATH="$HOME/.local/bin:$PATH"'
            export PATH="$HOME/.local/bin:$PATH"
            ok "Linked 'code' CLI to ~/.local/bin/code."
        elif sudo mkdir -p /usr/local/bin 2>/dev/null && sudo ln -sf "$vscode_bin" /usr/local/bin/code 2>/dev/null; then
            ok "Linked 'code' CLI to /usr/local/bin/code."
        else
            ok "Linked 'code' CLI to ~/.local/bin/code."
        fi
    else
        info "Could not set up 'code' CLI. Open VS Code manually, then:"
        info "  Cmd+Shift+P > 'Shell Command: Install code command in PATH'"
    fi
fi

# VS Code version check
if command -v code &>/dev/null; then
    vscode_version="$(code --version 2>/dev/null | head -n1 || echo 'unknown')"
    if [[ "$vscode_version" != "unknown" ]]; then
        if version_gte "$vscode_version" "1.98.0"; then
            ok "VS Code version: $vscode_version (>= 1.98.0)."
        else
            warn "VS Code version: $vscode_version — recommend updating to >= 1.98.0."
            warnings=true
        fi
    fi
fi

# Set VS Code as Git editor
if command -v git &>/dev/null && command -v code &>/dev/null; then
    current_editor=$(git config --global --get core.editor 2>/dev/null || true)
    if [[ -z "$current_editor" ]]; then
        git config --global core.editor "code --wait" 2>/dev/null || true
        ok "Git default editor set to VS Code."
    else
        skip "Git editor already set to '$current_editor'."
    fi
fi

# Install Claude Code extension
if command -v code &>/dev/null; then
    if code --list-extensions 2>/dev/null | grep -qi "anthropic.claude-code"; then
        skip "Claude Code extension already installed."
    else
        info "Installing extension..."
        # Pre-create the extensions directory — on a freshly installed VS Code the
        # directory may not exist yet, which causes --install-extension to fail silently.
        mkdir -p "$HOME/.vscode/extensions"
        # Explicitly pass the corporate CA cert to VS Code's Node.js process so it
        # can reach the marketplace through the PwC SSL-intercepting proxy.
        _ext_cert="${NODE_EXTRA_CA_CERTS:-$HOME/.claude/certs/corporate-ca-bundle.pem}"
        NODE_EXTRA_CA_CERTS="$_ext_cert" code --install-extension anthropic.claude-code --force || true
        unset _ext_cert

        if code --list-extensions 2>/dev/null | grep -qi "anthropic.claude-code"; then
            ok "Claude Code extension installed."
        else
            # Fallback: try downloading VSIX directly from VS Code Marketplace API.
            # The API endpoint (marketplace.visualstudio.com) is a different domain from the
            # CDN (anthropic.gallery.vsassets.io) and may be accessible even when the CDN is blocked.
            info "Marketplace install failed. Trying direct VSIX download..."
            _vsix_path="/tmp/claude-code-latest.vsix"
            _vsix_ok=false
            _dl_cert="${NODE_EXTRA_CA_CERTS:-$HOME/.claude/certs/corporate-ca-bundle.pem}"
            _dl_cacert_args=()
            [[ -f "$_dl_cert" ]] && _dl_cacert_args=(--cacert "$_dl_cert")
            if curl -fsSL "${_dl_cacert_args[@]+"${_dl_cacert_args[@]}"}" \
                --max-time 120 \
                -o "$_vsix_path" \
                "https://marketplace.visualstudio.com/_apis/public/gallery/publishers/anthropic/vsextensions/claude-code/latest/vspackage" 2>/dev/null; then
                # Validate it is a real VSIX (ZIP format) and not an HTML error/redirect page
                if [[ -f "$_vsix_path" ]] && file "$_vsix_path" 2>/dev/null | grep -qi "zip"; then
                    _vsix_ok=true
                else
                    info "Download returned non-VSIX content (possibly a login/block page)."
                fi
            fi

            if [[ "$_vsix_ok" == "true" ]]; then
                _ext_cert2="${NODE_EXTRA_CA_CERTS:-$HOME/.claude/certs/corporate-ca-bundle.pem}"
                NODE_EXTRA_CA_CERTS="$_ext_cert2" code --install-extension "$_vsix_path" --force || true
                if code --list-extensions 2>/dev/null | grep -qi "anthropic.claude-code"; then
                    ok "Claude Code extension installed (via VSIX download)."
                fi
            fi
            rm -f "$_vsix_path" 2>/dev/null

            if ! code --list-extensions 2>/dev/null | grep -qi "anthropic.claude-code"; then
                fail "Extension install failed -- the VS Code marketplace may be blocked by the firewall."
                info "To install manually:"
                info "  1. On an unblocked machine, visit:"
                info "     https://marketplace.visualstudio.com/items?itemName=anthropic.claude-code"
                info "  2. Click 'Download Extension' on the right side to get the .vsix file"
                info "  3. Copy the .vsix to this machine and run:"
                info "     code --install-extension <path-to-file>.vsix"
                info "Or ask a colleague who has it installed to share the .vsix file."
                warnings=true
            fi
        fi
    fi
else
    info "Cannot install extension — 'code' CLI not found."
    warnings=true
fi

# ── Step 10: Plugins ────────────────────────────────────────────────────────

step "10/$TOTAL_STEPS" "Plugin installation"

if command -v claude &>/dev/null; then
    # NOTE: Use 'claude plugin' (CLI subcommand, non-interactive), NOT 'claude /plugin'
    # (slash command that launches the interactive TUI).
    # Marketplaces and plugins are recorded in ~/.claude/settings.json.
    # Check there before adding/installing to avoid redundant operations.
    _settings="$HOME/.claude/settings.json"
    info "Adding plugin marketplaces..."
    if [[ -f "$_settings" ]] && grep -q '"knowledge-work-plugins"' "$_settings"; then
        skip "Marketplace already registered: knowledge-work-plugins."
    else
        claude plugin marketplace add anthropics/knowledge-work-plugins 2>/dev/null || true
    fi
    if [[ -f "$_settings" ]] && grep -q '"anthropic-agent-skills"' "$_settings"; then
        skip "Marketplace already registered: anthropic-agent-skills."
    else
        claude plugin marketplace add anthropics/skills 2>/dev/null || true
    fi

    # anthropics/skills repo registers as marketplace name "anthropic-agent-skills"
    plugins=("productivity@knowledge-work-plugins" "data@knowledge-work-plugins" "enterprise-search@knowledge-work-plugins" "document-skills@anthropic-agent-skills")
    for plugin in "${plugins[@]}"; do
        plugin_name="${plugin%%@*}"
        if [[ -f "$_settings" ]] && grep -q "\"$plugin\"" "$_settings"; then
            skip "Plugin already installed: $plugin_name."
        else
            info "Installing plugin: $plugin_name..."
            if claude plugin install "$plugin" 2>/dev/null; then
                ok "Plugin installed: $plugin_name."
            else
                warn "Could not install plugin: $plugin_name."
            fi
        fi
    done
    unset _settings
else
    warn "Claude Code not available — skipping plugin installation."
fi

# ── Step 11: Verification ───────────────────────────────────────────────────

step "11/$TOTAL_STEPS" "Verification"

# Quick claude version check
if command -v claude &>/dev/null; then
    ver="$(claude --version 2>/dev/null || true)"
    if [[ -n "$ver" ]]; then
        ok "Claude Code version: $ver"
    else
        warn "Claude Code installed but could not get version."
    fi
fi

# Summary table
echo ""
echo "  Verification:"
for item in "cert:$CORP_CERT_BUNDLE" "merged-cert:$MERGED_CERT_BUNDLE" "git:git" "python3:python3" "claude:claude" "code:code"; do
    name="${item%%:*}"
    check="${item#*:}"
    if [[ "$name" == "cert" ]] || [[ "$name" == "merged-cert" ]]; then
        if [[ -f "$check" ]]; then
            printf '    %s \033[32m[ok]\033[0m\n' "$name"
        else
            printf '    %s \033[90m[n/a]\033[0m\n' "$name"
        fi
    else
        if command -v "$check" &>/dev/null; then
            printf '    %s \033[32m[ok]\033[0m\n' "$name"
        else
            printf '    %s \033[31m[MISSING]\033[0m\n' "$name"
            warnings=true
        fi
    fi
done

# ── Step 12: Shared Services Configuration (conditional) ────────────────────

if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
    step "12/$TOTAL_STEPS" "Shared services configuration"

    ok "Using previously collected shared services configuration."
    info "API key: sk-...${ss_api_key: -4}"
    info "Base URL: $ss_base_url"

    apply_shared_services_config
fi

# ── Final Step: Git Identity ────────────────────────────────────────────────

step "$TOTAL_STEPS/$TOTAL_STEPS" "Git identity"

if command -v git &>/dev/null; then
    git_name="$(git config --global user.name 2>/dev/null || true)"
    git_email="$(git config --global user.email 2>/dev/null || true)"
    if [[ -n "$git_name" && -n "$git_email" ]]; then
        skip "Git identity already configured: $git_name <$git_email>"
    else
        echo ""
        echo "  ─────────────────────────────────────────────"
        echo "  Git needs your name and email for commits."
        echo "  (This is optional — press Enter to skip.)"
        echo "  ─────────────────────────────────────────────"
        if [[ -z "$git_name" ]]; then
            printf '  Your full name: '
            read -r git_name
            if [[ -n "$git_name" ]]; then
                git config --global user.name "$git_name"
            fi
        fi
        if [[ -z "$git_email" ]]; then
            printf '  Your PwC email: '
            read -r git_email
            if [[ -n "$git_email" ]]; then
                git config --global user.email "$git_email"
            fi
        fi
        if [[ -n "$git_name" || -n "$git_email" ]]; then
            ok "Git identity configured."
        else
            info "Skipped — you can set this later with:"
            info "  git config --global user.name 'Your Name'"
            info "  git config --global user.email 'you@pwc.com'"
        fi
    fi
fi

# ── Done ─────────────────────────────────────────────────────────────────────

echo ""
if [[ "$warnings" == "true" ]]; then
    echo "============================================="
    echo "  Installation complete (with warnings)."
    echo "============================================="
    echo ""
    echo "  Review the warnings above before continuing."
else
    echo "============================================="
    echo "  Installation complete!"
    echo "============================================="
fi
echo ""

if [[ "$MODE_SHARED_SERVICES" == "true" ]]; then
    echo "  Shared services are configured. Next steps:"
    echo ""
    echo "  1. Close and reopen VS Code (if it was already open)"
    echo "  2. Click the Claude icon (upper-right corner)"
    echo "  3. Start coding — no login needed, routing via shared service"
    echo ""
    echo "  CLI usage:"
    echo "    1. Open a new Terminal window"
    echo "    2. Run: claude"
    echo "    (env vars are configured in ~/.claude/settings.json — no export needed)"
    echo ""
else
    echo "  Next steps to finish setup:"
    echo ""
    echo "  CLI Login:"
    echo "    1. Open a new Terminal window"
    echo "    2. Type: claude"
    echo "    3. Press 1 to log in with your Enterprise subscription"
    echo "    4. Click 'Authorize' in the browser"
    echo ""
    echo "  VS Code Login:"
    echo "    1. Close and reopen VS Code (if it was already open)"
    echo "    2. Click the Claude icon (upper-right corner)"
    echo "    3. Click 'Claude.ai Subscription'"
    echo "    4. Click 'Open' then 'Authorize' in the browser"
fi
echo ""
if [[ -n "$LOG_FILE" ]]; then
    echo "  Log saved to: $LOG_FILE"
fi

# Clean up sudo keepalive if it was started
[[ -n "${SUDO_KEEPALIVE_PID:-}" ]] && kill "$SUDO_KEEPALIVE_PID" 2>/dev/null || true
echo ""
