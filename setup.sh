#!/bin/bash

echo "Initial LTS Setup"
echo -------------------
echo

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
    echo "> Setting up LTS workspace..."
    if [ -z "$1" ]; then
	    ./runtime/bin/python/bin/pypy3 runtime/setuplts.py scriptsonly
	else
	    echo "! Running custom python command! Some things may not work correctly!"
	    "$1" runtime/setuplts.py scriptsonly "$1"
	fi
}

start() {
    #
    # Downloading natives
    #

    # PyPy 3.6.1
    if [ -z "$1" ] && [ ! -d runtime/bin/python/ ]; then
        echo "> Downloading PyPy 7.1.1 on Python 3.6.1"

        os=$(uname)
        if [ "$os" == "Darwin" ]; then
            download "https://downloads.python.org/pypy/pypy3.6-v7.3.2-osx64.tar.bz2" runtime/bin/pypy.tar.bz2
        elif [ "$os" == "Linux" ]; then
            download "https://downloads.python.org/pypy/pypy3.6-v7.3.2-linux32.tar.bz2" runtime/bin/pypy.tar.bz2
        else
            echo "! You are not on a supported OS listed in the autoinstaller, unfortunately. Sorry about that :("
            echo "! You may try using your own python3 install by using 'c' in setup."
            exit 1
        fi
    fi

    #
    # Unzipping natives
    #

    if [ -z "$1" ] && [ ! -d runtime/bin/python/ ]; then
        echo
        echo Unzipping natives

        echo "> PyPy 7.3.2 on Python 3.6.1"
        tar -xjf runtime/bin/pypy.tar.bz2 -C runtime/bin/
        mv runtime/bin/pypy3.6* runtime/bin/python
        rm runtime/bin/pypy.tar.bz2
    fi

	#
	# Setup LTS workspace
	#

	echo
	echo Setting up LTS workspace...

    if [ -z "$1" ]; then
        ./runtime/bin/python/bin/pypy3 runtime/setuplts.py
    else
	    echo "! Running custom python command! Some things may not work correctly!"
        "$1" runtime/setuplts.py "$1"
    fi

    echo
    echo Finished!
    exit
}

setpython() {
    echo "> Enter the path to your desired python install."
    read -p ": " answer

    start ${answer}
}

setpythonscriptsonly() {
    echo "> Enter the path to your desired python install."
    read -p ": " answer

    scriptsonly ${answer}
}

if [ -d runtime/bin/python/ ]; then
    echo "> Input 's' if you want to only copy the .sh files."
    echo "> Input 'c' if you want to use a custom python command."
    echo "> Input 'i' if you want to use a custom python command and only copy the .sh files."
    echo "> Are you sure you want to run the setup? [y/N/s/c/i]?"
    read -p ": " answer

    case "$answer" in
        [yY])
            start ;;
        [sS])
            scriptsonly ;;
        [cC])
            setpython ;;
        [iI])
            setpythonscriptsonly ;;
        *)
            exit ;;
    esac
else
    echo "> Input 'c' if you want to use a custom python command."
    echo "> Are you sure you want to run the setup? [y/N/c]?"
    read -p ": " answer

    case "$answer" in
        [yY])
            start ;;
        [cC])
            setpython ;;
        *)
            exit ;;
    esac
fi
