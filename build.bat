@echo off
rem Builds the app into .\out. Needs a JDK 17 or newer.
cd /d "%~dp0"
if exist out rmdir /s /q out
if exist out-test rmdir /s /q out-test
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt || goto :fail
dir /s /b test\*.java > tests.txt
javac -encoding UTF-8 -cp out -d out-test @tests.txt || goto :fail
del sources.txt tests.txt
echo Built. Start the diary with run.bat
goto :eof
:fail
del sources.txt tests.txt 2>nul
echo Build failed.
exit /b 1
