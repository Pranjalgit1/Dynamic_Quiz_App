@echo off
echo Starting Quiz Application...
set JAVA_HOME=D:\java_coding
call .\mvnw.cmd clean javafx:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo The application exited with an error.
    pause
)
