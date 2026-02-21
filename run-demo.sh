#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$ROOT_DIR"

echo "Compiling..."
javac -cp 'lib/*' -d out src/util/*.java

echo "\n=== Running CtxMapDemo ==="
java -cp 'out:lib/*' util.CtxMapDemo

echo "\n=== Running JsonUtilDemo ==="
java -cp 'out:lib/*' util.JsonUtilDemo

echo "\nAll demos finished."