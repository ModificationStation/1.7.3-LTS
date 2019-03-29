#!/bin/bash

echo 'Initial 1.7.3-LTS Setup'
echo -------------------
echo 

read -p 'Are you sure you want to run the setup? [Y/N]? ' answer

#
# Methods
#

# Download (url, path)
download() {
    wget -q -O $2 $1
}

# Unpack (zip, output)
unpack() {
    unzip -qqod $2 $1
}

start() {
    #
    # Copying scripts to the root folder
    #
    
    echo 
    echo Copying scripts...
    echo 
    
    # https://www.shellscript.sh/tips/cp-t/  :^)
    find runtime/linux_scripts -name "*.sh" -print0 | xargs -0 cp -t ./
    find . -maxdepth 1 -name "*.sh" | xargs -I{} chmod -v 755 {}
    
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
    echo ' > Client'
    download https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar jars/bin/minecraft.jar
    
    # Server (From BetaCraft, official would be better)
    echo ' > Server'
    download https://betacraft.ovh/server-archive/minecraft/b1.7.3.jar jars/minecraft_server.jar
    
    # LWJGL 2.8.4
    echo ' > LWJGL'
    download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl/2.8.4/lwjgl-2.8.4.jar jars/bin/lwjgl.jar
    download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl_util/2.8.4/lwjgl_util-2.8.4.jar jars/bin/lwjgl_util.jar
    download http://central.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/2.8.4/lwjgl-platform-2.8.4-natives-linux.jar jars/bin/natives/lwjgl_platform.jar
    
    # jinput 2.05
    echo ' > jinput'
    download http://central.maven.org/maven2/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar jars/bin/jinput.jar
    download http://central.maven.org/maven2/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar jars/bin/natives/jinput_platform.jar
    
    # PyPy 2.7.13
    if [ ! -d runtime/bin/pypy_linux/ ]; then
        echo ' > PyPy 6.0.0'
        download https://bitbucket.org/squeaky/portable-pypy/downloads/pypy-6.0.0-linux_x86_64-portable.tar.bz2 runtime/bin/pypy.tar.bz2
    fi
    
    #
    # Unzipping natives
    #
    
    echo
    echo Unzipping natives
    
    echo ' > lwjgl_platform.jar'
    unpack jars/bin/natives/lwjgl_platform.jar jars/bin/natives
    echo ' > jinput_platform.jar'
    unpack jars/bin/natives/jinput_platform.jar jars/bin/natives
    if [ ! -d runtime/bin/pypy_linux/ ]; then
        echo ' > PyPy 6.0.0'
        tar -xjf runtime/bin/pypy.tar.bz2 -C runtime/bin/ pypy-6.0.0-linux_x86_64-portable/
        mv runtime/bin/pypy-6.0.0-linux_x86_64-portable/ runtime/bin/pypy_linux
    fi
    echo
    
    #
    # Cleanup
    #
    
    echo  Cleaning up...
    echo 

    rm -R jars/bin/natives/META-INF>/dev/null
    rm jars/bin/natives/lwjgl_platform.jar
    rm jars/bin/natives/jinput_platform.jar
    if [ -d runtime/bin/pypy.tar.bz2 ]; then
        rm runtime/bin/pypy.tar.bz2
    fi
    
    echo Finished!
    exit
}

case "$answer" in
    [yY])
        start ;;
    *)
        exit ;;
esac
