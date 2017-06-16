@echo off
@SETLOCAL

set CLASSPATH="%CLASSPATH%";./lib/satix.jar;./lib/selenium-server-standalone-2.41.0.jar;./lib/mail.jar;./lib/JSON4J.jar;
java -cp %CLASSPATH% org.satix.SatixRunner %*

@ENDLOCAL
