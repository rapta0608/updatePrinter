@echo off

REM 이전 프린터 이름을 변수로 받음
set "OLD_PRINTER_NAME=%~1"

REM 새로운 프린터 이름을 변수로 받음
set "NEW_PRINTER_NAME=%~2"

REM 프린터 이름 변경 명령어
rundll32 printui.dll,PrintUIEntry /Xs /n "%OLD_PRINTER_NAME%" PrinterName "%NEW_PRINTER_NAME%"