#!/bin/sh
# Builds the app into ./out. Needs a JDK 17 or newer.
cd "$(dirname "$0")"
JAVAC="javac"; command -v javac >/dev/null 2>&1 || JAVAC="java -m jdk.compiler/com.sun.tools.javac.Main"
rm -rf out out-test
$JAVAC -encoding UTF-8 -d out $(find src -name '*.java') || exit 1
$JAVAC -encoding UTF-8 -cp out -d out-test $(find test -name '*.java') || exit 1
echo "Built. Start the diary with ./run.sh"
