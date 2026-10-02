#!/usr/bin/env bash
# Compiles and runs the app using only the JDK (no Maven required).
set -e
rm -rf out
javac --release 17 -d out $(find src/main/java -name '*.java')
cp -r src/main/resources/* out/
java -cp out co.ceiba.transfers.Application
