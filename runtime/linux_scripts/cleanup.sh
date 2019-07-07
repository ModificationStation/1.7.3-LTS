#!/bin/bash
./runtime/bin/python/bin/pypy runtime/cleanup.py "$@"

if [ $? -ne 1 ]; then
    rm -rf runtime/bin/python
    rm -f ./cleanup.sh
fi