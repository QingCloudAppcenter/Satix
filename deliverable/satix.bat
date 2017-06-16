@echo off
@SETLOCAL

set CLASSPATH="%CLASSPATH%";./lib/satix.jar;./lib/selenium-server-standalone-3.4.0.jar;./lib/mail.jar;./lib/JSON4J.jar;
java -cp %CLASSPATH% org.satix.SatixRunner %* -c ../example/googleSearch/global.properties -t ../example/googleSearch/googleSearch.xml

@ENDLOCAL
pause