#!/bin/bash
./runtime/bin/pypy_linux/bin/pypy runtime/cleanup.py "$@"

rm -rf runtime/bin/python
rm -f ./cleanup.sh
