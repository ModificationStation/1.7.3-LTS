@echo off
title Initial LTS Setup

echo Initial LTS Setup
echo -------------------
echo.

::
:: Confirmation
::
if exist "runtime\bin\python\python.exe" (
    goto :areyousurepython
) else (
    goto :areyousure
)

goto :end


::
:: Methods
::

rem Ask user confirmation.
:areyousure
echo Are you sure you want to run the setup? [y/N]?
set /p c=": "
if /I "%c%" EQU "Y" goto :start
goto :end

:areyousurepython
echo Input 's' if you want to only copy the .bat files.
echo Are you sure you want to run the setup? [y/N/s]?
set /p c=": "
if /I "%c%" EQU "Y" goto :start
if /I "%c%" EQU "S" goto :scriptsonly
goto :end

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
runtime\bin\python\python runtime\setuplts.py scriptsonly %*
goto :end

:start

::
:: Download runtimes
::
echo.
echo Downloading runtimes...

:: Python 3.6.1
echo ^> Python 3.6.1 embeddable zip.
if exist "runtime\bin\python\python.exe" (
    echo ^> Skipping Python. Already exists.
) else (
    call :download https://www.python.org/ftp/python/3.6.1/python-3.6.1-embed-win32.zip runtime\bin\python.zip
)

::
:: Unzipping natives
::

echo.
echo ^Unzipping runtimes...

:: Python 3.6.1
echo ^> python.zip
if exist "runtime\bin\python\python.exe" (
    echo ^> Skipping Python. Already exists.
) else (
    call :unzip runtime\bin\python.zip runtime\bin\python
    del runtime\bin\python.zip
)

::
:: Setup LTS workspace
::

echo.
echo ^> Setting up LTS workspace...

runtime\bin\python\python runtime\setuplts.py %*

:end
echo.
echo Finished!

pause
