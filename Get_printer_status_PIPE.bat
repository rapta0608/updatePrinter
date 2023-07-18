@echo off

echo Get "Evolis Kc Prime" status  (PIPE) :

EspfRequestTool.exe -i "Request1.txt" -p \\.\pipe\EspfServer00 -o "Result1.txt"
IF ERRORLEVEL 1 (
	ECHO ERROR - PIPE connection : Client failed to send request
) ELSE (
	ECHO.
	MORE Result1.txt
)

echo.

pause
