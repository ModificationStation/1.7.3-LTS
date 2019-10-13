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
	./runtime/bin/python/bin/pypy3 runtime/setuplts.py scriptsonly "$@"
}

start() {
    #
    # Downloading natives
    #

    # PyPy 3.6.1
    if [ ! -d runtime/bin/python/ ]; then
        echo "> Downloading PyPy 7.1.1 on Python 3.6.1"

        pypy_url=""
        os=$(uname)
        if [ "$os" == 'Darwin' ]; then
            pypy_url="https://bitbucket.org/pypy/pypy/downloads/pypy3.6-v7.1.1-osx64.tar.bz2"
            download $pypy_url runtime/bin/pypy.tar.bz2
        elif [ "$os" == 'Linux' ]; then
            pypy_url="https://bitbucket.org/pypy/pypy/downloads/pypy3.6-v7.1.1-linux64.tar.bz2"
            download $pypy_url runtime/bin/pypy.tar.bz2
        else
            echo "! You are not on a supported OS, unfortunately. Sorry about that :("
            exit 1
        fi
    fi

    #
    # Unzipping natives
    #

    if [ ! -d runtime/bin/python/ ]; then
        echo
        echo Unzipping natives

        echo "> PyPy 7.1.1 on Python 3.6.1"
        tar -xjf runtime/bin/pypy.tar.bz2 -C runtime/bin/
        mv runtime/bin/pypy3.6-v7.1.1* runtime/bin/python
        rm runtime/bin/pypy.tar.bz2
    fi

	#
	# Setup LTS workspace
	#

	echo
	echo Setting up LTS workspace...

	./runtime/bin/python/bin/pypy3 runtime/setuplts.py "$@"

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
