@echo off
title MCP Cleanup

echo MCP Cleanup
echo --------------
echo.

::
:: Confirmation
::

set /P c=Are you sure you want to run the cleanup? [Y/N]? 
if /I "%c%" EQU "Y" goto :start 
if /I "%c%" EQU "N" goto :end

::
:: Methods
::

rem Remove directory function
rem Arguments: 
rem         Directory
rem     Directory: A directory
:rmdir
rmdir %1 /s /q >nul
echo  ^> %1
exit /b

rem Copy file function
rem Arguments: 
rem         File path, Destination path
rem     File: Path to the file
rem		Destination: Path to the destination
:copy
copy %1 %2 >nul
exit /b

:start

echo.
echo Deleting...

:: Delete jars\, but not the server.properties
call :copy "jars\server.properties" "temp\server.properties"
call :rmdir jars\
mkdir jars
call :copy "temp\server.properties" "jars\server.properties"

:: Delete everything else
call :rmdir logs\
call :rmdir reobf\
call :rmdir temp\
call :rmdir src\
call :rmdir bin\

:end

echo.
echo Finished^!
echo.

pause