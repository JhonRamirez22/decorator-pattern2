#!/usr/bin/env bash
# Builds and runs the app using only the JDK.
set -euo pipefail
cd "$(dirname "$0")"
./build.sh
exec java -jar target/decorator-pattern.jar
