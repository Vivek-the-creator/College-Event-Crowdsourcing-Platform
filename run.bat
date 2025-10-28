@echo off
echo Starting College Event Crowdsourcing Platform...
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven is not installed or not in PATH.
    echo Please install Maven and try again.
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH.
    echo Please install Java 17 or higher and try again.
    pause
    exit /b 1
)

echo Building the project...
call mvn clean compile

if %errorlevel% neq 0 (
    echo Build failed. Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Starting the application...
call mvn javafx:run

if %errorlevel% neq 0 (
    echo Application failed to start. Please check the error messages above.
    pause
    exit /b 1
)

pause


