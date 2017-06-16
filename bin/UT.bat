@echo off
@SETLOCAL

cd ..\deliverable
satix.bat -c ..\UT\UT.properties -t ..\UT\Case
cd ..\..\

@ENDLOCAL
