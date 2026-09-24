#!/usr/bin/env bash
# Start 4 velum-client backends (ports 8081-8084) for testing the load balancer.
# Builds the velum-client jar first if it is missing. Ctrl+C stops all backends.
# Usage: ./start-backends.sh [basePort=8081]
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CLIENT_DIR="$SCRIPT_DIR/../../velum-client"
JAR="$CLIENT_DIR/target/velum-client.jar"
COUNT=4
BASE_PORT=${1:-8081}

if [ ! -f "$JAR" ]; then
  echo "Building velum-client..."
  mvn -q -f "$CLIENT_DIR/pom.xml" clean package
fi

pids=()
trap 'kill "${pids[@]}" 2>/dev/null; wait' INT TERM EXIT

for ((i = 0; i < COUNT; i++)); do
  port=$((BASE_PORT + i))
  java -jar "$JAR" "$port" "backend-$((i + 1))" &
  pids+=($!)
done

wait
