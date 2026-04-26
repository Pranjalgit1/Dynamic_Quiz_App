@echo off
echo Starting Quiz Application...

REM Keep JAVA_HOME only if it points to a valid JDK
if not "%JAVA_HOME%"=="" (
    if not exist "%JAVA_HOME%\bin\java.exe" set "JAVA_HOME="
)

REM Strategy 1: Parse java.home from java command output
if "%JAVA_HOME%"=="" (
    for /f "tokens=2,* delims==" %%a in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /c:"java.home"') do (
        for /f "tokens=*" %%i in ("%%b") do set "JAVA_HOME=%%i"
    )
)

REM Strategy 2: Infer from javac path (works when javac is in JDK\bin)
if "%JAVA_HOME%"=="" (
    for /f "delims=" %%i in ('where javac 2^>nul') do (
        set "_JAVAC=%%i"
        goto :foundJavac
    )
)

:foundJavac
if not "%_JAVAC%"=="" (
    for %%i in ("%_JAVAC%") do set "JAVA_HOME=%%~dpi.."
)

REM Strategy 3: Scan standard JDK install locations on Windows
if not exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_HOME="
    for /d %%D in ("C:\Program Files\Java\jdk*") do set "JAVA_HOME=%%~fD"
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_HOME="
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*") do set "JAVA_HOME=%%~fD"
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo.
    echo JAVA_HOME is not set correctly.
    echo Please set JAVA_HOME to your JDK folder and run again.
    echo Example: set JAVA_HOME=C:\Program Files\Java\jdk-25
    pause
    exit /b 1
)

call .\mvnw.cmd clean javafx:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo The application exited with an error.
    pause
)
