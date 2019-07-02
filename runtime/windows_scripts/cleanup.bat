@echo off
runtime\bin\python\python runtime\cleanup.py %*

del  /F/Q/S runtime\bin\python > nul
del /F cleanup.bat
