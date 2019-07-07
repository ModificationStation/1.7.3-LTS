import shutil
import os
import sys
import time
import configparser
import platform
import traceback


class Cleanup:
    _default_config = 'conf/mcp.cfg'

    def __init__(self, conffile=None):
        print(os.getcwd())
        self.conffile = conffile
        self.readconf()
        self.confdir = self.config.get("DEFAULT", "DirConf")
        self.tempdir = self.config.get("DEFAULT", "DirTemp")
        self.logdir = self.config.get("DEFAULT", "DirLogs")
        self.srcdir = self.config.get("DEFAULT", "DirSrc")
        self.bindir = self.config.get("DEFAULT", "DirBin")
        self.reobfdir = self.config.get("DEFAULT", "DirReobf")
        self.jardir = self.config.get("DEFAULT", "DirJars")
        self.mcplogfile = self.config.get('MCP', 'LogFile')
        self.mcperrlogfile = self.config.get('MCP', 'LogFileErr')
        if platform.system() == "Windows":
            self.systemext = "bat"
        else:
            self.systemext = "sh"

    def start(self):
        """
        ARE YOU *REALLY* SURE?!
        :return:
        """
        print("> Welcome to the LTS cleanup script!")
        print("> This script will delete your workspace and set most of it to factory defaults.")
        print("> Are you sure you want to clean up your workspace? [y/N]")
        inp = input(": ")
        if inp != "y":
            sys.exit(0)
        print("> Are you *REALLY* sure you want to clean up your workspace? [y/N]")
        print("> This deletes ALL your source files and jars! This is NOT recoverable!")
        inp = input(": ")
        if inp != "y":
            sys.exit(0)

        print("> Commencing the purge of the universe...")

        print("> Deleting \"" + self.jardir + "\"...")
        jartime = time.time()
        # Delete jars while keeping server.properties.
        if os.path.exists(self.jardir):
            if not os.path.exists(self.tempdir):
                os.makedirs(self.tempdir)
            shutil.copy2(os.path.join(self.jardir, "server.properties"), self.tempdir)
            shutil.rmtree(self.jardir)
            os.makedirs(self.jardir)
            if os.path.exists(os.path.join(self.tempdir, "server.properties")):
                shutil.copy2(os.path.join(self.tempdir, "server.properties"), self.jardir)
        print('> Done in %.2f seconds' % (time.time() - jartime))

        print("> Deleting \"" + self.reobfdir + "\"...")
        reobftime = time.time()
        if os.path.exists(self.reobfdir):
            shutil.rmtree(self.reobfdir)
        print('> Done in %.2f seconds' % (time.time() - reobftime))

        print("> Deleting \"" + self.logdir + "\"...")
        logtime = time.time()
        if os.path.exists(self.logdir):
            shutil.rmtree(self.logdir)
        print('> Done in %.2f seconds' % (time.time() - logtime))

        print("> Deleting \"" + self.bindir + "\"...")
        bintime = time.time()
        if os.path.exists(self.bindir):
            shutil.rmtree(self.bindir)
        print('> Done in %.2f seconds' % (time.time() - bintime))

        print("> Deleting \"" + self.srcdir + "\"...")
        srctime = time.time()
        if os.path.exists(self.srcdir):
            shutil.rmtree(self.srcdir)
        print('> Done in %.2f seconds' % (time.time() - srctime))

        print("> Deleting \"" + self.tempdir + "\"...")
        temptime = time.time()
        if os.path.exists(self.tempdir):
            shutil.rmtree(self.tempdir)
        print('> Done in %.2f seconds' % (time.time() - temptime))

        print("> Deleting non-default config...")
        conftime = time.time()
        if os.path.exists(self.confdir):
            if os.path.exists(os.path.join(self.confdir, "patches")) and os.path.isdir(os.path.join(self.confdir, "patches")):
                shutil.rmtree(os.path.join(self.confdir, "patches"))
            for file in os.listdir(self.confdir):
                if os.path.isfile(os.path.join(self.confdir, file)) and file not in ["mcp.cfg", "version.cfg"]:
                    os.unlink(os.path.join(self.confdir, file))
        print('> Done in %.2f seconds' % (time.time() - conftime))

        print("> Deleting system specific files from root...")
        systime = time.time()
        for file in ["decompile", "recompile", "reobfuscate", "startclient", "startserver", "updatemcp", "updatemd5"]:
            if os.path.exists(file + "." + self.systemext) and os.path.isfile(file + "." + self.systemext):
                os.unlink(file + "." + self.systemext)
        print('> Done in %.2f seconds' % (time.time() - systime))

        print("> Done!")
        print("> Press enter to continue...")
        input()

    def readconf(self):
        """
        Reads config and creates a class from it.
        Code copied from commands.py:126
        :return:
        """
        config = configparser.ConfigParser()
        with open(self._default_config) as config_file:
            config.read_file(config_file)
        if self.conffile is not None:
            config.read(self.conffile)
        self.config = config

if __name__ == '__main__':
    cleanup = Cleanup()
    try:
        cleanup.start()
    except:
        traceback.print_exc()
        sys.exit(1)
