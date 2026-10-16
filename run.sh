#!/bin/sh
cd "$(dirname "$0")"
[ -d out ] || ./build.sh || exit 1
exec java -Dfile.encoding=UTF-8 -cp out com.mooddiary.Main "$@"
