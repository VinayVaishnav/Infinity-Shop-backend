#!/usr/bin/env bash
# Copy product PNGs from repo products/ to UI public + Spring static.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/products"
UI="$ROOT/../myProjUI/public/products"
STATIC="$ROOT/src/main/resources/static/products"
mkdir -p "$UI" "$STATIC"
cp "$SRC"/*.png "$UI/"
cp "$SRC"/*.png "$STATIC/"
echo "Synced $(ls -1 "$SRC"/*.png | wc -l | tr -d ' ') images to UI and backend static."
