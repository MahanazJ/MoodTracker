@echo off
cd /d "%~dp0"
if not exist out call build.bat || exit /b 1
start "" javaw -Dfile.encoding=UTF-8 -cp out com.mooddiary.Main %*
