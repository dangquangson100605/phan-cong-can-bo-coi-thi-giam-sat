@REM Maven Wrapper script for Windows
@REM Downloads Maven if not present and runs the build

@echo off
set MAVEN_VERSION=3.9.6
set MAVEN_DIR=%~dp0.mvn\wrapper
set MAVEN_ZIP=%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_HOME=%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%
set MVN=%MAVEN_HOME%\bin\mvn.cmd

if exist "%MVN%" goto run

echo Downloading Apache Maven %MAVEN_VERSION%...
mkdir "%MAVEN_DIR%" 2>nul
powershell -Command "Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%'"
echo Extracting...
powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"
del "%MAVEN_ZIP%"
echo Maven ready!

:run
"%MVN%" %*
