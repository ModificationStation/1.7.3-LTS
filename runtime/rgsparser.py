import json


class RGSParser(dict):
    def __init__(self, side):
        super().__init__()
        self["client"] = {}
        self["server"] = {}
        if side == 0:
            self.side = "client"
            self.filename = "minecraft.rgs"
        else:
            self.side = "server"
            self.filename = "minecraft_server.rgs"

        with open("conf/" + self.filename, "r") as rgs:
            lines = rgs.readlines()
            for line in lines:
                line = line.replace("\n", "").replace("\r", "")
                parts = line.split(" ")
                if line.startswith(".class_map"):
                    self[self.side][parts[1]] = {"£name": "net/minecraft/src/" + parts[2]}

            for line in lines:
                line = line.replace("\n", "").replace("\r", "")
                parts = line.split(" ")
                if line.startswith(".field_map"):
                    clas = parts[1].split("/")
                    name = clas.pop(len(clas) - 1)
                    clas = "/".join(clas)
                    if not self[self.side].__contains__(clas):
                        self[self.side][clas] = {"£name": clas}
                    self[self.side][clas][name] = {"name": self[self.side][clas]["£name"] + "/" + parts[2], "parentclass": clas, "type": "f"}

    def export_json(self, filename):
        with open(filename, "w") as file:
            file.write(json.dumps(self, indent=4, sort_keys=True))

    def export_srgs(self):
        lines = []
        with open("temp/" + self.side + "_rg.srg", "w") as file:
            for classname, classobj in self[self.side].items():
                lines.append("CL: " + classname + " " + classobj["£name"] + "\n")

            for classname, classobj in self[self.side].items():
                for mfname, mfobj in classobj.items():
                    if mfname != "£name" and mfobj["type"] == "f":
                        lines.append("FD: " + mfobj["parentclass"] + "/" + mfname + " " + mfobj["name"] + "\n")

            file.writelines(lines)

        lines = []
        with open("temp/" + self.side + "_ro.srg", "w") as file:
            for classname, classobj in self[self.side].items():
                lines.append("CL: " + classobj["£name"] + " " + classname + "\n")

            for classname, classobj in self[self.side].items():
                for mfname, mfobj in classobj.items():
                    if mfname != "£name" and mfobj["type"] == "f":
                        lines.append("FD: " + mfobj["name"] + " " + mfobj["parentclass"] + "/" + mfname + "\n")

            file.writelines(lines)
