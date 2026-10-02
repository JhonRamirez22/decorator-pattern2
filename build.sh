#!/usr/bin/env bash
# Builds an executable JAR using only the JDK.
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out target
find src/main/java -name '*.java' -print > out/sources.txt
javac --release 17 -encoding UTF-8 -d out @out/sources.txt
cp -R src/main/resources/. out/
jar --create --file target/decorator-pattern.jar --main-class co.ceiba.transfers.Application -C out .
echo "Built target/decorator-pattern.jar"
