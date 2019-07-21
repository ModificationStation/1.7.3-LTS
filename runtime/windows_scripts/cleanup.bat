@echo off
runtime\bin\python\python runtime\cleanup.py %*

if not exitcode == 1 (
    del /F cleanup.bat > nul
)
