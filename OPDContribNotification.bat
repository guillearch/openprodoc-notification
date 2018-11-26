set PATH2=%PATH%
if "%JAVA_HOME%" == "" goto Start
set PATH=%PATH%;%JAVA_HOME%\bin;
:Start

set OPDL=.\dist\lib\

rem Send notification to contributors (0) or OpenProdoc users (1)
set CASE=0

rem Date of the last check in milliseconds (e.g., 3600000 ms to check new contributions in the last 1 hour).
rem 1 hour
set MS=3600000
rem 1 day
rem MS=86400000
rem 1 week
rem MS=604800000

java -Dfile.encoding=UTF-8 -classpath .;%OPDL%Prodoc.jar;%OPDL%ProdocSwing.jar;%OPDL%javax.mail.jar;%OPDL%tika-app-1.18.jar;.\dist\OPDContribNotification.jar; opdcontribnotification.OPDContribNotification %CASE% %MS%