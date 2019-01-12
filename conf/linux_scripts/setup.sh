#!/bin/bash

echo Initial MCP Setup
echo -------------------
echo 

read -p 'Are you sure you want to run the setup? [Y/N]? ' answer

#
# Methods
#

# Download (url, path)
download() {
    wget -q -O $2 $1 >/dev/null
}

# Unzip (zip, output)
unzip() {
    7z x -y -o$2 $1 >/dev/null
}

start() {
    
    #
    # Create folders
    #
    
    echo 
    echo Creating folders...
    echo 
    
    mkdir -p jars/bin/natives/
    
    #
    # Download runtimes
    #
    
    echo Downloading runtimes...
    
    # Client
    download https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar jars/bin/minecraft.jar
    
    echo ' > Client'
    
    # Server (From BetaCraft, official would be better)
    download https://betacraft.ovh/server-archive/minecraft/b1.7.3.jar jars/minecraft_server.jar
    
    echo ' > Server'
    
    # LWJGL 2.8.4
    download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl/2.8.4/lwjgl-2.8.4.jar jars/bin/lwjgl.jar
    download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl_util/2.8.4/lwjgl_util-2.8.4.jar jars/bin/lwjgl_util.jar
    download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/2.8.4/lwjgl-platform-2.8.4-natives-linux.jar jars/bin/natives/lwjgl_platform.jar
    
    echo ' > LWJGL'
    
    # jinput 2.05
    download http://central.maven.org/maven2/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar jars/bin/jinput.jar
    download http://central.maven.org/maven2/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar jars/bin/natives/jinput_platform.jar
    
    echo ' > jinput'
    
    # PyPy 2.7.13
    download https://bitbucket.org/squeaky/portable-pypy/downloads/pypy-6.0.0-linux_x86_64-portable.tar.bz2 runtime/bin/pypy.tar.bz2
    
    echo ' > PyPy 6.0.0'
    
    #
    # Unzipping natives
    #
    
    echo
    echo Unzipping natives
    
    unzip jars/bin/natives/lwjgl_platform.jar jars/bin/natives
    echo ' > lwjgl_platform.jar'
    unzip jars/bin/natives/jinput_platform.jar jars/bin/natives
    echo ' > jinput_platform.jar'
    mkdir -p runtime/bin/pypy_linux
    tar -xjf runtime/bin/pypy.tar.bz2 -C runtime/bin pypy-6.0.0-linux_x86_64-portable/
    mv runtime/bin/pypy-6.0.0-linux_x86_64-portable/ runtime/bin/pypy_linux/
    echo ' > PyPy 6.0.0'
    echo
    
    #
    # Cleanup
    #
    
    echo  Cleaning up...
    echo 

    rm -R jars/bin/natives/META-INF>/dev/null
    rm jars/bin/natives/lwjgl_platform.jar
    rm jars/bin/natives/jinput_platform.jar
    rm runtime/bin/pypy.tar.bz2
    
    echo Finished!
    exit
}

case "$answer" in
    [yY])
        start ;;
    *)
        exit ;;
esac