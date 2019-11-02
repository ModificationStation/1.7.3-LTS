@echo off

if exist "runtime/command" (
    set /p command=< runtime\command
) else (
    set command=runtime\bin\python\python
)

"%command%" runtime\cleanup.py %*

if not exitcode == 1 (
    del /F cleanup.bat > nul
)
