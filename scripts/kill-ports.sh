#!/usr/bin/env bash
# Kill whatever is listening on the given TCP ports.
#
# Usage: kill-ports.sh [-9] [-n] PORT [PORT...]
#   -9  send SIGKILL immediately instead of SIGTERM-then-SIGKILL
#   -n  dry run: show what would be killed, kill nothing
#
# Ports can be single values or ranges: kill-ports.sh 8080 9000-9005

set -euo pipefail

force=false
dry_run=false

usage() {
  sed -n '2,8p' "$0" | sed 's/^# \{0,1\}//'
  exit "${1:-0}"
}

while getopts ":9nh" opt; do
  case "$opt" in
    9) force=true ;;
    n) dry_run=true ;;
    h) usage 0 ;;
    *) usage 1 ;;
  esac
done
shift $((OPTIND - 1))

[[ $# -eq 0 ]] && usage 1

# Expand ranges like 9000-9005 into individual ports.
ports=()
for arg in "$@"; do
  if [[ "$arg" =~ ^([0-9]+)-([0-9]+)$ ]]; then
    for ((p = BASH_REMATCH[1]; p <= BASH_REMATCH[2]; p++)); do ports+=("$p"); done
  elif [[ "$arg" =~ ^[0-9]+$ ]]; then
    ports+=("$arg")
  else
    echo "invalid port: $arg" >&2
    exit 1
  fi
done

pids_on_port() {
  lsof -t -iTCP:"$1" -sTCP:LISTEN 2>/dev/null || true
}

status=0
for port in "${ports[@]}"; do
  pids=$(pids_on_port "$port")
  if [[ -z "$pids" ]]; then
    echo "port $port: free"
    continue
  fi

  for pid in $pids; do
    cmd=$(ps -o comm= -p "$pid" 2>/dev/null || echo "?")
    if $dry_run; then
      echo "port $port: would kill $pid ($cmd)"
      continue
    fi

    if $force; then
      kill -9 "$pid" 2>/dev/null || true
    else
      kill "$pid" 2>/dev/null || true
      # Give it up to 3s to shut down cleanly, then SIGKILL.
      for _ in {1..30}; do
        kill -0 "$pid" 2>/dev/null || break
        sleep 0.1
      done
      kill -0 "$pid" 2>/dev/null && kill -9 "$pid" 2>/dev/null || true
    fi

    if kill -0 "$pid" 2>/dev/null; then
      echo "port $port: failed to kill $pid ($cmd) — try sudo" >&2
      status=1
    else
      echo "port $port: killed $pid ($cmd)"
    fi
  done
done

exit "$status"
