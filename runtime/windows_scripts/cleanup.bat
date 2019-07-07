@echo off
runtime\bin\python\python runtime\cleanup.py %*

if not exitcode == 1 (
    rmdir /Q/S runtime\bin\python > nul
    del /F cleanup.bat > nul
)