#!/bin/bash
./runtime/bin/python/bin/pypy runtime/cleanup.py "$@"

if [ $? -ne 1 ]; then
    rm -f ./cleanup.sh
fi