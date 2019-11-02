@echo off
title Initial LTS Setup

echo Initial LTS Setup
echo -------------------
echo.

::
:: Confirmation
::
if exist "runtime\bin\python\python.exe" (
    call :areyousurepython
) else (
    call :areyousure
)

goto :end


::
:: Methods
::

rem Ask user confirmation.
:areyousure
echo ^> Are you sure you want to run the setup? [y/N]?
set /p answer=": "
if /I "%answer%" EQU "Y" goto :start
exit /b

:areyousurepython
echo ^> Input 's' if you want to only copy the .bat files.
echo ^> Input 'c' if you want to use a custom python command.
echo ^> Input 'i' if you want to use a custom python command and only copy the .bat files.
echo ^> Are you sure you want to run the setup? [y/N/s/c/i]?
set /p answer=": "
if /I "%answer%" EQU "Y" call :start
if /I "%answer%" EQU "S" call :scriptsonly
if /I "%answer%" EQU "C" call :setpython
if /I "%answer%" EQU "I" call :setpythonscriptsonly
exit /b

rem Download function
rem Arguments: 
rem         URL, Output file
rem     URL: a http(s) link 
rem     Output file: A path that will be output for the downloaded file
:download
runtime\bin\wget.exe -q -O %2 %1
exit /b

rem Unzip function
rem Arguments: 
rem         archive path, output path
rem     archive path: Path to the archive
rem     output path: Output folder 
:unzip
runtime\bin\7z.exe x -y -o%2 %1 >>nul
exit /b

:scriptsonly

echo.
echo ^> Setting up LTS workspace...
if "%~1" == "" (
    runtime\bin\python\python runtime\setuplts.py scriptsonly
} else (
    echo ! Running custom python command! Some things may not work correctly!
    "%~1" runtime\setuplts.py scriptsonly "%~1"
)
exit /b

:start

::
:: Download runtimes
::
echo.
echo ^> Downloading runtimes...

:: Python 3.6.1
if "%~1" == "" (
    echo ^> Python 3.6.1 embeddable zip.
    if exist "runtime\bin\python\python.exe" (
        echo ^> Skipping Python. Already exists.
    ) else (
        call :download https://www.python.org/ftp/python/3.6.1/python-3.6.1-embed-win32.zip runtime\bin\python.zip
    )
)

::
:: Unzipping natives
::

echo.
echo ^> Unzipping runtimes...

:: Python 3.6.1
if "%~1" == "" (
    echo ^> python.zip
    if exist "runtime\bin\python\python.exe" (
        echo ^> Skipping Python. Already exists.
    ) else (
        call :unzip runtime\bin\python.zip runtime\bin\python
        del runtime\bin\python.zip
    )
)

::
:: Setup LTS workspace
::

echo.
echo ^> Setting up LTS workspace...

if "%~1" == "" (
    runtime\bin\python\python runtime\setuplts.py
) else (
    echo ! Running custom python command! Some things may not work correctly!
    "%~1" runtime\setuplts.py "%~1"
)
exit /b

:setpython
echo Enter the path to your desired python install.
set /p answer=": "
call :start "%answer%"
exit /b

:setpythonscriptsonly
echo Enter the path to your desired python install.
set /p answer=": "
call :scriptsonly "%answer%"
exit /b

:end
echo.
echo Finished!

pause
