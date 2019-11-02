# -*- coding: utf-8 -*-
"""
Created on Fri Apr  8 16:54:36 2011

@author: ProfMobius
@version: v1.0
"""
from optparse import OptionParser
import sys
import os
sys.path.append(os.path.dirname(os.path.realpath(__file__)))  # Workaround for python 3.6's obtuse import system.
from commands import Commands


def main(conffile=None):
    commands = Commands(conffile)

    commands.startserver()


if __name__ == '__main__':
    parser = OptionParser(version='MCP %s' % Commands.MCPVersion)
    parser.add_option('-c', '--config', dest='config', help='additional configuration file')
    (options, args) = parser.parse_args()
    main(options.config)
