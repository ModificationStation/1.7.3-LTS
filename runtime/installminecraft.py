import sys
import shutil
import os.path
import ConfigParser
import logging
import time
import minecraftversions
import urllib2

class InstallMC:
    _default_config = 'conf/mcp.cfg'

    def __init__(self, conffile=None):
        self.conffile = conffile
        self.readconf()
        self.confdir = self.config.get("DEFAULT", "DirConf")
        self.jardir = self.config.get("DEFAULT", "DirJars")
        self.mcplogfile = self.config.get('MCP', 'LogFile')
        self.mcperrlogfile = self.config.get('MCP', 'LogFileErr')
        self.startlogger()

    def startlogger(self):
        """
        Basically sets up a logger and different logger handlers for different levels and such.
        Code copied from commands.py:92
        :return:
        """
        self.logger = logging.getLogger('MCPLog')
        self.logger.setLevel(logging.DEBUG)
        # create file handler which logs even debug messages
        fh = logging.FileHandler(filename=self.mcplogfile, mode='w')
        fh.setLevel(logging.DEBUG)
        # create console handler with a higher log level
        ch = logging.StreamHandler()
        ch.setLevel(logging.INFO)
        # File output of everything Warning or above
        eh = logging.FileHandler(filename=self.mcperrlogfile, mode='w')
        eh.setLevel(logging.WARNING)
        # create formatter and add it to the handlers
        formatterconsole = logging.Formatter('%(message)s')
        ch.setFormatter(formatterconsole)
        formatterfile = logging.Formatter('%(asctime)s - %(module)11s.%(funcName)s - %(levelname)s - %(message)s', datefmt='%Y-%m-%d %H:%M')
        fh.setFormatter(formatterfile)
        eh.setFormatter(formatterfile)
        # add the handlers to logger
        self.logger.addHandler(ch)
        self.logger.addHandler(fh)
        self.logger.addHandler(eh)

    def start(self):
        """
        Main entry function.
        :return:
        """
        print(sys.version)
        self.logger.info("> Welcome to the LTS version selector!")
        self.logger.info("> If you wish to supply your own configuration, type \"none\".")
        self.logger.info("> What version would you like to install?")

        versions = []
        versionsstring = ""
        for version in os.listdir(self.confdir):
            if os.path.isdir(os.path.join(self.confdir, version)):
                versionsstring += version.replace(",", ":") + ", "
                versions.append(version)
        versionsstring = versionsstring.strip(", ")

        inp = ""

        while inp not in versions:
            self.logger.info("> Current versions are: " + versionsstring)
            self.logger.info("> What version would you like?")

            inp = str(raw_input(": "))

            if inp == "none":
                sys.exit()

        self.logger.info("> Copying config.")
        copytime = time.time()
        self.copydir(os.path.join(self.confdir, inp), self.confdir)
        self.logger.info('> Done in %.2f seconds' % (time.time() - copytime))

        self.logger.info("> Downloading Minecraft client...")
        clientdltime = time.time()
        self.download(minecraftversions.versions["client"][inp]["url"], os.path.join(self.jardir, "bin", "minecraft.jar"))
        self.logger.info('> Done in %.2f seconds' % (time.time() - clientdltime))

        serverdltime = time.time()
        self.logger.info("> Downloading Minecraft server...")
        self.download(minecraftversions.versions["server"][inp]["url"], os.path.join(self.jardir, "minecraft_server.jar"))
        self.logger.info('> Done in %.2f seconds' % (time.time() - serverdltime))

    def download(self, url, dst):
        # Because legacy code is stupid.
        try:
            f = urllib2.urlopen(url)
            print("> Downloading \"" + url + "\"...")

            path = os.path.abspath(os.path.dirname(dst))
            if not os.path.exists(path):
                os.makedirs(path)

            # Open our local file for writing
            with open(dst, "wb") as local_file:
                local_file.write(f.read())
            self.logger.info("> Done!")
        except Exception:
            print("Unable to download \"" + url + "\"")

    def copydir(self, src, dst, replace=True):
        """
        Shutil's built in copytree function raises an exception if src exists.
        This is basically copytree minus the exceptions and added logging.
        :param dir:
        :return:
        """
        for file in os.listdir(src):
            if os.path.isfile(os.path.join(src, file)):
                if os.path.exists(os.path.join(dst, file)) and not replace:
                    self.logger.debug("> Skipped file \"" + os.path.join(src, file) + "\": Already exists.")
                elif os.path.exists(os.path.join(dst, file)):
                    os.unlink(os.path.join(dst, file))
                    shutil.copy2(os.path.join(src, file), dst)
                    self.logger.debug("> Replaced file \"" + os.path.join(src, file) + "\".")
                else:
                    shutil.copy2(os.path.join(src, file), dst)
                    self.logger.debug("> Copied file \"" + os.path.join(src, file) + "\".")
            else:
                try:
                    os.makedirs(os.path.join(dst, file))
                except:
                    pass
                self.copydir(os.path.join(src, file), os.path.join(dst, file))

    def readconf(self):
        """
        Reads config and creates a class from it.
        Code copied from commands.py:126
        :return:
        """
        config = ConfigParser.SafeConfigParser()
        with open(self._default_config) as config_file:
            config.readfp(config_file)
        if self.conffile is not None:
            config.read(self.conffile)
        self.config = config

if __name__ == '__main__':
    instmc = InstallMC()
    instmc.start()
