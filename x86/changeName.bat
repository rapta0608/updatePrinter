@echo off

REM ���� ������ �̸��� ������ ����
set "OLD_PRINTER_NAME=%~1"

REM ���ο� ������ �̸��� ������ ����
set "NEW_PRINTER_NAME=%~2"

REM ������ �̸� ���� ��ɾ�
rundll32 printui.dll,PrintUIEntry /Xs /n "%OLD_PRINTER_NAME%" PrinterName "%NEW_PRINTER_NAME%"