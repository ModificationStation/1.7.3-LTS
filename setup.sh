#!/bin/bash

echo "Initial LTS Setup"
echo -------------------
echo 

echo "Input 's' if you want to only copy the .sh files."
read -p "Are you sure you want to run the setup? [y/N/s]? " answer

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

scriptsonly() {
	./runtime/bin/python/bin/pypy runtime/setuplts.py scriptsonly "$@"
}

start() {
    #
    # Downloading natives
    #

    # PyPy 3.6.1
    if [ ! -d runtime/bin/python/ ]; then
        echo "> Downloading PyPy 7.1.1 on Python 3.6.1"
        download https://bitbucket.org/squeaky/portable-pypy/downloads/pypy3.6-7.1.1-beta-linux_x86_64-portable.tar.bz2 runtime/bin/pypy.tar.bz2
    fi

    #
    # Unzipping natives
    #

    echo
    echo Unzipping natives

    if [ ! -d runtime/bin/python/ ]; then
        echo "> PyPy 7.1.1 on Python 3.6.1"
        tar -xjf runtime/bin/pypy.tar.bz2 -C runtime/bin/ pypy3.6-7.1.1-beta-linux_x86_64-portable/
        mv runtime/bin/pypy3.6-7.1.1-beta-linux_x86_64-portable/ runtime/bin/python
        rm runtime/bin/pypy.tar.bz2
    fi

	#
	# Setup LTS workspace
	#

	echo
	echo Setting up LTS workspace...

	./runtime/bin/python/bin/pypy runtime/setuplts.py "$@"

    echo
    echo Finished!
    exit
}

case "$answer" in
    [yY])
        start ;;
    [sS])
        scriptsonly ;;
    *)
        exit ;;
esac
