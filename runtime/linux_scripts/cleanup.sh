#!/bin/bash

echo MCP Cleanup
echo -------------
echo 

read -p 'Are you sure you want to run the cleanup? [y/N]? ' answer

#
# Methods
#

# removedir (directory)
removedir() {
    rm -R $1 >/dev/null
}

# copy (file, destination)
copy() {
    cp $1 $2 >/dev/null
}

start() {
    echo
    echo Deleting...
    echo
    
    # Delete jars/, but not the server properties
    
    # Because that can happen
    if [ ! -d temp/ ]; then
        mkdir temp/;
    fi
    
    copy jars/server.properties temp/server.properties
    removedir jars/
    mkdir jars/
    copy temp/server.properties jars/server.properties
    
    # Delete everything else
    removedir logs/
    removedir reobf/
    removedir temp/
    removedir src/
    removedir bin/    
    
    echo 'Finished!'
}

case "$answer" in
    [yY])
        start ;;
    *)
        exit ;;
esac