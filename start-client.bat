@echo off
echo ===========================================
echo   HE THONG PHAN CONG CAN BO COI THI
echo   KHOI DONG CLIENT
echo ===========================================
set JAVA_HOME=C:\Program Files\Java\jdk-24
set MVN=C:\maven-399\bin\mvn.cmd

echo [*] Dang khoi dong Client...
"%MVN%" exec:java -Dexec.mainClass="client.ClientMain"
pause
