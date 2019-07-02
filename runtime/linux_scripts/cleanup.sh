#!/bin/bash
./runtime/bin/python/bin/pypy runtime/cleanup.py "$@"

rm -rf runtime/bin/python
rm -f ./cleanup.sh
