@echo off
REM Run WebSocket Server with all dependencies

echo 🚀 Starting WebSocket Server...
echo.

cd /d "%~dp0"

set CLASSPATH=target\classes

for /r target\lib %%f in (*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

echo Starting Java process...
echo ---
echo.

java -cp "%CLASSPATH%" WebSocketServer

echo.
echo ---
echo Server stopped
pause
