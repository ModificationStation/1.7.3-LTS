#!/bin/bash
./runtime/bin/python/bin/pypy3 runtime/cleanup.py "$@"

if [ $? -ne 1 ]; then
    rm -f ./cleanup.sh
fi