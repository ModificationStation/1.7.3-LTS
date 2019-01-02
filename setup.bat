@echo off
title Initial MCP Setup

echo Initial MCP Setup
echo -------------------
echo.

::
:: Conirmation
::

set /P c=Are you sure you want to run the setup? [Y/N]? 
if /I "%c%" EQU "Y" goto :start 
if /I "%c%" EQU "N" goto :end

:start

::
:: Create folders
::

echo.
echo Creating folders...
echo.

mkdir jars\bin\natives

::
:: Download runtimes
::

echo Downloading runtimes...

:: Client
runtime\bin\wget.exe -O jars\bin\minecraft.jar https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar 2>>nul

echo  ^> Client

:: Server (find an official link!
REM runtime\bin\wget.exe -O jars\minecraft_server.jar

:: LWJGL 2.8.4
runtime\bin\wget.exe -O jars\bin\lwjgl.jar http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl/2.8.4/lwjgl-2.8.4.jar 2>>nul
runtime\bin\wget.exe -O jars\bin\lwjgl_util.jar http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl_util/2.8.4/lwjgl_util-2.8.4.jar 2>>nul
runtime\bin\wget.exe -O jars\bin\natives\lwjgl_platform.jar http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/2.8.4/lwjgl-platform-2.8.4-natives-windows.jar 2>>nul

echo  ^> LWJGL

:: jinput 2.0.5
runtime\bin\wget.exe -O jars\bin\jinput.jar http://central.maven.org/maven2/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar 2>>nul
runtime\bin\wget.exe -O jars\bin\natives\jinput_platform.jar http://central.maven.org/maven2/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar 2>>nul

echo  ^> jinput
echo.

::
:: Unzipping natives
::

echo Unzipping natives...

cd jars\bin\natives
..\..\..\runtime\bin\7z.exe x lwjgl_platform.jar >> nul
echo  ^> lwjgl_platform.
..\..\..\runtime\bin\7z.exe x jinput_platform.jar >> nul
echo  ^> jinput_platform.jar
echo.

::
:: Clean up
::

echo Cleaning up...
echo.

rmdir /S /Q META-INF >> nul
del /Q lwjgl_platform.jar >> nul
del /Q jinput_platform.jar >> nul
cd ..\..\..

:end
echo Finished^!
echo.

pause