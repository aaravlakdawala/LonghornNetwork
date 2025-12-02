#!/usr/bin/env bash
set -euo pipefail

echo "One-shot setup for LonghornNetwork (bash)"
echo "This script: builds the Java backend with Maven and installs frontend packages in longhornnetwork-web."

check_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "  - Missing required command: $1"
    return 1
  fi
  return 0
}

missing=0
for cmd in git java mvn node npm; do
  check_cmd "$cmd" || missing=1
done

if [ $missing -ne 0 ]; then
  echo "\nOne or more prerequisites are missing. See README_SETUP.md for installation instructions." >&2
  exit 2
fi

echo "\nBuilding backend (Maven)..."
mvn clean package -DskipTests

if [ -d "longhornnetwork-web" ]; then
  echo "\nInstalling frontend dependencies (npm ci)..."
  (cd longhornnetwork-web && npm ci)
  echo "\nFrontend dependencies installed. To run the frontend dev server, run:"
  echo "  (cd longhornnetwork-web && npm run dev)"
else
  echo "\nWarning: directory 'longhornnetwork-web' not found; skipping frontend install."
fi

echo "\nOne-shot setup finished. See README_SETUP.md for run commands and troubleshooting."
