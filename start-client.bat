@echo off
echo ===========================================
echo   HE THONG PHAN CONG CAN BO COI THI
echo   KHOI DONG CLIENT
echo ===========================================
set JAVA_HOME=C:\Program Files\Java\jdk-23
set MVN=%~dp0mvnw.cmd

echo [*] Dang khoi dong Client...
call "%MVN%" exec:java -Dexec.mainClass="client.ClientMain"
pause
