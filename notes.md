# Decompiling
When decompiling, make sure you have java 8 or java 7 (pref 8) in your PATH (or equivalent).
If not, MCP will start trying to use whatever java it manages to find, which is REALLY bad.

# Recompiling
See above.

# Reobfuscation
If you recompiled with java 9 or above, when reobfuscating, SpecialSource with error out with "unsupported major class version".
This means MCP went ahead and used whatever java install it could.