@echo off
title Initial MCP Setup

echo Initial MCP Setup
echo -------------------
echo.

REM TODO ADD TYPE YES
::
:: Create folders
::

mkdir jars\bin\natives

::
:: Download runtimes
::

:: Client
runtime\bin\wget.exe -O jars\bin\minecraft.jar https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar

::LWJGL 2.8.4
runtime\bin\wget.exe -O jars\bin\lwjgl.jar http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl/2.8.4/lwjgl-2.8.4.jar
runtime\bin\wget.exe -O jars\bin\lwjgl_util.jar http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl_util/2.8.4/lwjgl_util-2.8.4.jar
runtime\bin\wget.exe -O jars\bin\natives\lwjgl_platform.jar http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/2.8.4/lwjgl-platform-2.8.4-natives-windows.jar

::jinput 2.0.5
runtime\bin\wget.exe -O jars\bin\jinput.jar http://central.maven.org/maven2/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar
runtime\bin\wget.exe -O jars\bin\natives\jinput_platform.jar http://central.maven.org/maven2/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar

::
:: Unzipping natives
::

cd jars\bin\natives
echo Extracting lwjgl_platform.jar..
..\..\..\runtime\bin\7z.exe x lwjgl_platform.jar >> nul
echo Extracting jinput_platform.jar..
..\..\..\runtime\bin\7z.exe x jinput_platform.jar >> nul

::
:: Clean up
::

rmdir /S /Q META-INF >> nul
del /Q lwjgl_platform.jar >> nul
del /Q jinput_platform.jar >> nul
cd ..\..\..

:: Server (find an official link!
REM runtime\bin\wget.exe -O jars\minecraft_server.jar

pause
