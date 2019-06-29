import sys
import shutil
import os.path
from commands import Commands
import ConfigParser

class InstallMC:
    _default_config = 'conf/mcp.cfg'

    def __init__(self, conffile=None):
        self.conffile = conffile
        self.readconf()
        self.confdir = self.config.get("DEFAULT", "DirConf")
        self.start()

    def start(self):
        self.readconf()
        print("Welcome to the LTS version selector!")
        print("If you wish to supply your own versions and configuration, close this window.")
        print("What version would you like to install?")

        versions = ""
        for version in os.listdir(self.confdir):
            if os.path.isdir(os.path.join(self.confdir, version)):
                versions += version.replace(",", ":") + ", "
        versions = versions.strip(", ")

        print("Current versions are: " + versions)

    def readconf(self):
        config = ConfigParser.SafeConfigParser()
        with open(self._default_config) as config_file:
            config.readfp(config_file)
        if self.conffile is not None:
            config.read(self.conffile)
        self.config = config

if __name__ == '__main__':
    InstallMC()
