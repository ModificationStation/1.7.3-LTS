@echo off
title Initial 1.7.3-LTS Setup

echo Initial 1.7.3-LTS Setup
echo -------------------
echo.

::
:: Conirmation
::

set /P c=Are you sure you want to run the setup? [y/N]? 
if /I "%c%" EQU "Y" goto :start 
goto :end


::
:: Methods
::

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

:start

::
:: Copying scripts to the root folder
::

echo.
echo Copying scripts...
echo.

xcopy /Y runtime\windows_scripts\*.bat . >> nul


::
:: Create folders
::

echo.
echo Creating folders...
echo.

mkdir jars\bin\natives >> nul

::
:: Download runtimes
::

echo Downloading runtimes...

:: LWJGL 2.8.4
echo  ^> LWJGL
call :download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl/2.8.4/lwjgl-2.8.4.jar jars\bin\lwjgl.jar
call :download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl_util/2.8.4/lwjgl_util-2.8.4.jar jars\bin\lwjgl_util.jar
call :download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/2.8.4/lwjgl-platform-2.8.4-natives-windows.jar jars\bin\natives\lwjgl_platform.jar

:: jinput 2.0.5
echo  ^> jinput
call :download http://central.maven.org/maven2/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar jars\bin\jinput.jar
call :download http://central.maven.org/maven2/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar jars\bin\natives\jinput_platform.jar


::
:: Unzipping natives
::

echo.
echo Unzipping natives...

echo  ^> lwjgl_platform.jar
call :unzip jars\bin\natives\lwjgl_platform.jar jars\bin\natives
echo  ^> jinput_platform.jar
call :unzip jars\bin\natives\jinput_platform.jar jars\bin\natives
echo.

::
:: Setup Minecraft config and jars
::

echo.
echo Setting up Minecraft...

runtime\bin\python\python_mcp runtime\installminecraft.py

::
:: Clean up
::

echo Cleaning up...
echo.

rmdir /S /Q jars\bin\natives\META-INF >> nul
del /Q jars\bin\natives\lwjgl_platform.jar >> nul
del /Q jars\bin\natives\jinput_platform.jar >> nul

:end
echo Finished^!
echo.

pause
