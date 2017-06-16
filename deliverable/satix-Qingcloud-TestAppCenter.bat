set JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
@echo off
@SETLOCAL

set CLASSPATH="%CLASSPATH%";./lib/satix.jar;./lib/selenium-server-standalone-3.4.0.jar;./lib/mail.jar;./lib/JSON4J.jar;
java -cp %CLASSPATH% org.satix.SatixRunner %* -c  Qingcloud/Qingcloud.properties -t  Qingcloud/Case/TestAppCenter

@ENDLOCAL

pause