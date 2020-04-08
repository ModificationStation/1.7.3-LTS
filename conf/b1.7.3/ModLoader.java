// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 
// Source File Name:   ModLoader.java

package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

// Referenced classes of package net.minecraft.src:
//            Achievement, StatBase, StringTranslate, BaseMod, 
//            TextureFX, Item, Block, ItemStack, 
//            CraftingManager, FurnaceRecipes, BiomeGenBase, EnumCreatureType, 
//            SpawnListEntry, EntityLiving, EntityRendererProxy, EntityList, 
//            Session, TileEntityRenderer, RenderPlayer, RenderEngine, 
//            BiomeGenHell, BiomeGenSky, TileEntity, RenderBlocks, 
//            GameSettings, StatList, StatCrafting, IRecipe, 
//            TexturePackList, TexturePackBase, EntityPlayer, World, 
//            KeyBinding, IChunkProvider, ModTextureStatic, ItemBlock, 
//            TileEntitySpecialRenderer, MLProp, UnexpectedThrowable, GuiScreen, 
//            IBlockAccess

public final class ModLoader
{

    public static void AddAchievementDesc(Achievement achievement, String name, String description)
    {
        try
        {
            if(achievement.statName.contains("."))
            {
                String split[] = achievement.statName.split("\\.");
                if(split.length == 2)
                {
                    String key = split[1];
                    AddLocalization((new StringBuilder("achievement.")).append(key).toString(), name);
                    AddLocalization((new StringBuilder("achievement.")).append(key).append(".desc").toString(), description);
                    setPrivateValue(net.minecraft.src.StatBase.class, achievement, 1, StringTranslate.getInstance().translateKey((new StringBuilder("achievement.")).append(key).toString()));
                    setPrivateValue(net.minecraft.src.Achievement.class, achievement, 3, StringTranslate.getInstance().translateKey((new StringBuilder("achievement.")).append(key).append(".desc").toString()));
                } else
                {
                    setPrivateValue(net.minecraft.src.StatBase.class, achievement, 1, name);
                    setPrivateValue(net.minecraft.src.Achievement.class, achievement, 3, description);
                }
            } else
            {
                setPrivateValue(net.minecraft.src.StatBase.class, achievement, 1, name);
                setPrivateValue(net.minecraft.src.Achievement.class, achievement, 3, description);
            }
        }
        catch(IllegalArgumentException e)
        {
            logger.throwing("ModLoader", "AddAchievementDesc", e);
            ThrowException(e);
        }
        catch(SecurityException e)
        {
            logger.throwing("ModLoader", "AddAchievementDesc", e);
            ThrowException(e);
        }
        catch(NoSuchFieldException e)
        {
            logger.throwing("ModLoader", "AddAchievementDesc", e);
            ThrowException(e);
        }
    }

    public static int AddAllFuel(int id)
    {
        logger.finest((new StringBuilder("Finding fuel for ")).append(id).toString());
        int result = 0;
        for(Iterator iter = modList.iterator(); iter.hasNext() && result == 0; result = ((BaseMod)iter.next()).AddFuel(id)) { }
        if(result != 0)
        {
            logger.finest((new StringBuilder("Returned ")).append(result).toString());
        }
        return result;
    }

    public static void AddAllRenderers(Map o)
    {
        if(!hasInit)
        {
            init();
            logger.fine("Initialized");
        }
        BaseMod mod;
        for(Iterator iterator = modList.iterator(); iterator.hasNext(); mod.AddRenderer(o))
        {
            mod = (BaseMod)iterator.next();
        }

    }

    public static void addAnimation(TextureFX anim)
    {
        logger.finest((new StringBuilder("Adding animation ")).append(anim.toString()).toString());
        for(Iterator iterator = animList.iterator(); iterator.hasNext();)
        {
            TextureFX oldAnim = (TextureFX)iterator.next();
            if(oldAnim.tileImage == anim.tileImage && oldAnim.iconIndex == anim.iconIndex)
            {
                animList.remove(anim);
                break;
            }
        }

        animList.add(anim);
    }

    public static int AddArmor(String armor)
    {
        try
        {
            String existingArmor[] = (String[])field_armorList.get(null);
            List existingArmorList = Arrays.asList(existingArmor);
            List combinedList = new ArrayList();
            combinedList.addAll(existingArmorList);
            if(!combinedList.contains(armor))
            {
                combinedList.add(armor);
            }
            int index = combinedList.indexOf(armor);
            field_armorList.set(null, ((Object) (combinedList.toArray(new String[0]))));
            return index;
        }
        catch(IllegalArgumentException e)
        {
            logger.throwing("ModLoader", "AddArmor", e);
            ThrowException("An impossible error has occured!", e);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "AddArmor", e);
            ThrowException("An impossible error has occured!", e);
        }
        return -1;
    }

    public static void AddLocalization(String key, String value)
    {
        Properties props = null;
        try
        {
            props = (Properties)getPrivateValue(net.minecraft.src.StringTranslate.class, StringTranslate.getInstance(), 1);
        }
        catch(SecurityException e)
        {
            logger.throwing("ModLoader", "AddLocalization", e);
            ThrowException(e);
        }
        catch(NoSuchFieldException e)
        {
            logger.throwing("ModLoader", "AddLocalization", e);
            ThrowException(e);
        }
        if(props != null)
        {
            props.put(key, value);
        }
    }

    private static void addMod(ClassLoader loader, String filename)
    {
        try
        {
            String name = filename.split("\\.")[0];
            if(name.contains("$"))
            {
                return;
            }
            if(props.containsKey(name) && (props.getProperty(name).equalsIgnoreCase("no") || props.getProperty(name).equalsIgnoreCase("off")))
            {
                return;
            }
            Package pack = (net.minecraft.src.ModLoader.class).getPackage();
            if(pack != null)
            {
                name = (new StringBuilder(String.valueOf(pack.getName()))).append(".").append(name).toString();
            }
            Class instclass = loader.loadClass(name);
            if(!(net.minecraft.src.BaseMod.class).isAssignableFrom(instclass))
            {
                return;
            }
            setupProperties(instclass);
            BaseMod mod = (BaseMod)instclass.newInstance();
            if(mod != null)
            {
                modList.add(mod);
                logger.fine((new StringBuilder("Mod Loaded: \"")).append(mod.toString()).append("\" from ").append(filename).toString());
                System.out.println((new StringBuilder("Mod Loaded: ")).append(mod.toString()).toString());
            }
        }
        catch(Throwable e)
        {
            logger.fine((new StringBuilder("Failed to load mod from \"")).append(filename).append("\"").toString());
            System.out.println((new StringBuilder("Failed to load mod from \"")).append(filename).append("\"").toString());
            logger.throwing("ModLoader", "addMod", e);
            ThrowException(e);
        }
    }

    public static void AddName(Object instance, String name)
    {
        String tag = null;
        if(instance instanceof Item)
        {
            Item item = (Item)instance;
            if(item.getItemName() != null)
            {
                tag = (new StringBuilder(String.valueOf(item.getItemName()))).append(".name").toString();
            }
        } else
        if(instance instanceof Block)
        {
            Block block = (Block)instance;
            if(block.getBlockName() != null)
            {
                tag = (new StringBuilder(String.valueOf(block.getBlockName()))).append(".name").toString();
            }
        } else
        if(instance instanceof ItemStack)
        {
            ItemStack stack = (ItemStack)instance;
            if(stack.getItemName() != null)
            {
                tag = (new StringBuilder(String.valueOf(stack.getItemName()))).append(".name").toString();
            }
        } else
        {
            Exception e = new Exception((new StringBuilder(String.valueOf(instance.getClass().getName()))).append(" cannot have name attached to it!").toString());
            logger.throwing("ModLoader", "AddName", e);
            ThrowException(e);
        }
        if(tag != null)
        {
            AddLocalization(tag, name);
        } else
        {
            Exception e = new Exception((new StringBuilder()).append(instance).append(" is missing name tag!").toString());
            logger.throwing("ModLoader", "AddName", e);
            ThrowException(e);
        }
    }

    public static int addOverride(String fileToOverride, String fileToAdd)
    {
        try
        {
            int i = getUniqueSpriteIndex(fileToOverride);
            addOverride(fileToOverride, fileToAdd, i);
            return i;
        }
        catch(Throwable e)
        {
            logger.throwing("ModLoader", "addOverride", e);
            ThrowException(e);
            throw new RuntimeException(e);
        }
    }

    public static void addOverride(String path, String overlayPath, int index)
    {
        int dst = -1;
        int left = 0;
        if(path.equals("/terrain.png"))
        {
            dst = 0;
            left = terrainSpritesLeft;
        } else
        if(path.equals("/gui/items.png"))
        {
            dst = 1;
            left = itemSpritesLeft;
        } else
        {
            return;
        }
        System.out.println((new StringBuilder("Overriding ")).append(path).append(" with ").append(overlayPath).append(" @ ").append(index).append(". ").append(left).append(" left.").toString());
        logger.finer((new StringBuilder("addOverride(")).append(path).append(",").append(overlayPath).append(",").append(index).append("). ").append(left).append(" left.").toString());
        Map overlays = (Map)overrides.get(Integer.valueOf(dst));
        if(overlays == null)
        {
            overlays = new HashMap();
            overrides.put(Integer.valueOf(dst), overlays);
        }
        overlays.put(overlayPath, Integer.valueOf(index));
    }

    public static void AddRecipe(ItemStack output, Object params[])
    {
        CraftingManager.getInstance().addRecipe(output, params);
    }

    public static void AddShapelessRecipe(ItemStack output, Object params[])
    {
        CraftingManager.getInstance().addShapelessRecipe(output, params);
    }

    public static void AddSmelting(int input, ItemStack output)
    {
        FurnaceRecipes.smelting().addSmelting(input, output);
    }

    public static void AddSpawn(Class entityClass, int weightedProb, EnumCreatureType spawnList)
    {
        AddSpawn(entityClass, weightedProb, spawnList, null);
    }

    public static void AddSpawn(Class entityClass, int weightedProb, EnumCreatureType spawnList, BiomeGenBase biomes[])
    {
        if(entityClass == null)
        {
            throw new IllegalArgumentException("entityClass cannot be null");
        }
        if(spawnList == null)
        {
            throw new IllegalArgumentException("spawnList cannot be null");
        }
        if(biomes == null)
        {
            biomes = standardBiomes;
        }
        for(int i = 0; i < biomes.length; i++)
        {
            List list = biomes[i].getSpawnableList(spawnList);
            if(list != null)
            {
                boolean exists = false;
                for(Iterator iterator = list.iterator(); iterator.hasNext();)
                {
                    SpawnListEntry entry = (SpawnListEntry)iterator.next();
                    if(entry.entityClass == entityClass)
                    {
                        entry.spawnRarityRate = weightedProb;
                        exists = true;
                        break;
                    }
                }

                if(!exists)
                {
                    list.add(new SpawnListEntry(entityClass, weightedProb));
                }
            }
        }

    }

    public static void AddSpawn(String entityName, int weightedProb, EnumCreatureType spawnList)
    {
        AddSpawn(entityName, weightedProb, spawnList, null);
    }

    public static void AddSpawn(String entityName, int weightedProb, EnumCreatureType spawnList, BiomeGenBase biomes[])
    {
        Class entityClass = (Class)classMap.get(entityName);
        if(entityClass != null && (net.minecraft.src.EntityLiving.class).isAssignableFrom(entityClass))
        {
            AddSpawn(entityClass, weightedProb, spawnList, biomes);
        }
    }

    public static boolean DispenseEntity(World world, double x, double y, double z, int xVel, 
            int zVel, ItemStack item)
    {
        boolean result = false;
        for(Iterator iter = modList.iterator(); iter.hasNext() && !result; result = ((BaseMod)iter.next()).DispenseEntity(world, x, y, z, xVel, zVel, item)) { }
        return result;
    }

    public static List getLoadedMods()
    {
        return Collections.unmodifiableList(modList);
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public static Minecraft getMinecraftInstance()
    {
        if(instance == null)
        {
            try
            {
                ThreadGroup group = Thread.currentThread().getThreadGroup();
                int count = group.activeCount();
                Thread threads[] = new Thread[count];
                group.enumerate(threads);
                for(int i = 0; i < threads.length; i++)
                {
                    if(!threads[i].getName().equals("Minecraft main thread"))
                    {
                        continue;
                    }
                    instance = (Minecraft)getPrivateValue(java.lang.Thread.class, threads[i], "target");
                    break;
                }

            }
            catch(SecurityException e)
            {
                logger.throwing("ModLoader", "getMinecraftInstance", e);
                throw new RuntimeException(e);
            }
            catch(NoSuchFieldException e)
            {
                logger.throwing("ModLoader", "getMinecraftInstance", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static Object getPrivateValue(Class instanceclass, Object instance, int fieldindex)
        throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field f = instanceclass.getDeclaredFields()[fieldindex];
            f.setAccessible(true);
            return f.get(instance);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "getPrivateValue", e);
            ThrowException("An impossible error has occured!", e);
            return null;
        }
    }

    public static Object getPrivateValue(Class instanceclass, Object instance, String field)
        throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field f = instanceclass.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(instance);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "getPrivateValue", e);
            ThrowException("An impossible error has occured!", e);
            return null;
        }
    }

    public static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem)
    {
        int id = nextBlockModelID++;
        blockModels.put(Integer.valueOf(id), mod);
        blockSpecialInv.put(Integer.valueOf(id), Boolean.valueOf(full3DItem));
        return id;
    }

    public static int getUniqueEntityId()
    {
        return highestEntityId++;
    }

    private static int getUniqueItemSpriteIndex()
    {
        for(; itemSpriteIndex < usedItemSprites.length; itemSpriteIndex++)
        {
            if(!usedItemSprites[itemSpriteIndex])
            {
                usedItemSprites[itemSpriteIndex] = true;
                itemSpritesLeft--;
                return itemSpriteIndex++;
            }
        }

        Exception e = new Exception("No more empty item sprite indices left!");
        logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
        ThrowException(e);
        return 0;
    }

    public static int getUniqueSpriteIndex(String path)
    {
        if(path.equals("/gui/items.png"))
        {
            return getUniqueItemSpriteIndex();
        }
        if(path.equals("/terrain.png"))
        {
            return getUniqueTerrainSpriteIndex();
        } else
        {
            Exception e = new Exception((new StringBuilder("No registry for this texture: ")).append(path).toString());
            logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
            ThrowException(e);
            return 0;
        }
    }

    private static int getUniqueTerrainSpriteIndex()
    {
        for(; terrainSpriteIndex < usedTerrainSprites.length; terrainSpriteIndex++)
        {
            if(!usedTerrainSprites[terrainSpriteIndex])
            {
                usedTerrainSprites[terrainSpriteIndex] = true;
                terrainSpritesLeft--;
                return terrainSpriteIndex++;
            }
        }

        Exception e = new Exception("No more empty terrain sprite indices left!");
        logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
        ThrowException(e);
        return 0;
    }

    private static void init()
    {
        hasInit = true;
        String usedItemSpritesString = "1111111111111111111111111111111111111101111111011111111111111001111111111111111111111111111011111111100110000011111110000000001111111001100000110000000100000011000000010000001100000000000000110000000000000000000000000000000000000000000000001100000000000000";
        String usedTerrainSpritesString = "1111111111111111111111111111110111111111111111111111110111111111111111111111000111111011111111111111001111111110111111111111100011111111000010001111011110000000111111000000000011111100000000001111000000000111111000000000001101000000000001111111111111000011";
        for(int i = 0; i < 256; i++)
        {
            usedItemSprites[i] = usedItemSpritesString.charAt(i) == '1';
            if(!usedItemSprites[i])
            {
                itemSpritesLeft++;
            }
            usedTerrainSprites[i] = usedTerrainSpritesString.charAt(i) == '1';
            if(!usedTerrainSprites[i])
            {
                terrainSpritesLeft++;
            }
        }

        try
        {
            instance = (Minecraft)getPrivateValue(net.minecraft.client.Minecraft.class, null, 1);
            instance.entityRenderer = new EntityRendererProxy(instance);
            classMap = (Map)getPrivateValue(net.minecraft.src.EntityList.class, null, 0);
            field_modifiers = (java.lang.reflect.Field.class).getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            field_blockList = (net.minecraft.src.Session.class).getDeclaredFields()[0];
            field_blockList.setAccessible(true);
            field_TileEntityRenderers = (net.minecraft.src.TileEntityRenderer.class).getDeclaredFields()[0];
            field_TileEntityRenderers.setAccessible(true);
            field_armorList = (net.minecraft.src.RenderPlayer.class).getDeclaredFields()[3];
            field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & 0xffffffef);
            field_armorList.setAccessible(true);
            field_animList = (net.minecraft.src.RenderEngine.class).getDeclaredFields()[6];
            field_animList.setAccessible(true);
            Field fieldArray[] = (net.minecraft.src.BiomeGenBase.class).getDeclaredFields();
            List biomes = new LinkedList();
            for(int i = 0; i < fieldArray.length; i++)
            {
                Class fieldType = fieldArray[i].getType();
                if((fieldArray[i].getModifiers() & 8) != 0 && fieldType.isAssignableFrom(net.minecraft.src.BiomeGenBase.class))
                {
                    BiomeGenBase biome = (BiomeGenBase)fieldArray[i].get(null);
                    if(!(biome instanceof BiomeGenHell) && !(biome instanceof BiomeGenSky))
                    {
                        biomes.add(biome);
                    }
                }
            }

            standardBiomes = (BiomeGenBase[])biomes.toArray(new BiomeGenBase[0]);
            try
            {
                method_RegisterTileEntity = (net.minecraft.src.TileEntity.class).getDeclaredMethod("a", new Class[] {
                    java.lang.Class.class, java.lang.String.class
                });
            }
            catch(NoSuchMethodException e)
            {
                method_RegisterTileEntity = (net.minecraft.src.TileEntity.class).getDeclaredMethod("addMapping", new Class[] {
                    java.lang.Class.class, java.lang.String.class
                });
            }
            method_RegisterTileEntity.setAccessible(true);
            try
            {
                method_RegisterEntityID = (net.minecraft.src.EntityList.class).getDeclaredMethod("a", new Class[] {
                    java.lang.Class.class, java.lang.String.class, Integer.TYPE
                });
            }
            catch(NoSuchMethodException e)
            {
                method_RegisterEntityID = (net.minecraft.src.EntityList.class).getDeclaredMethod("addMapping", new Class[] {
                    java.lang.Class.class, java.lang.String.class, Integer.TYPE
                });
            }
            method_RegisterEntityID.setAccessible(true);
        }
        catch(SecurityException e)
        {
            logger.throwing("ModLoader", "init", e);
            ThrowException(e);
            throw new RuntimeException(e);
        }
        catch(NoSuchFieldException e)
        {
            logger.throwing("ModLoader", "init", e);
            ThrowException(e);
            throw new RuntimeException(e);
        }
        catch(NoSuchMethodException e)
        {
            logger.throwing("ModLoader", "init", e);
            ThrowException(e);
            throw new RuntimeException(e);
        }
        catch(IllegalArgumentException e)
        {
            logger.throwing("ModLoader", "init", e);
            ThrowException(e);
            throw new RuntimeException(e);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "init", e);
            ThrowException(e);
            throw new RuntimeException(e);
        }
        try
        {
            loadConfig();
            if(props.containsKey("loggingLevel"))
            {
                cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
            }
            if(props.containsKey("grassFix"))
            {
                RenderBlocks.cfgGrassFix = Boolean.parseBoolean(props.getProperty("grassFix"));
            }
            logger.setLevel(cfgLoggingLevel);
            if((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null)
            {
                logHandler = new FileHandler(logfile.getPath());
                logHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(logHandler);
            }
            logger.fine("ModLoader Beta 1.7.3 Initializing...");
            System.out.println("ModLoader Beta 1.7.3 Initializing...");
            File source = new File((net.minecraft.src.ModLoader.class).getProtectionDomain().getCodeSource().getLocation().toURI());
            modDir.mkdirs();
            readFromModFolder(modDir);
            readFromClassPath(source);
            System.out.println("Done.");
            props.setProperty("loggingLevel", cfgLoggingLevel.getName());
            props.setProperty("grassFix", Boolean.toString(RenderBlocks.cfgGrassFix));
            for(Iterator iterator = modList.iterator(); iterator.hasNext();)
            {
                BaseMod mod = (BaseMod)iterator.next();
                mod.ModsLoaded();
                if(!props.containsKey(mod.getClass().getName()))
                {
                    props.setProperty(mod.getClass().getName(), "on");
                }
            }

            instance.gameSettings.keyBindings = RegisterAllKeys(instance.gameSettings.keyBindings);
            instance.gameSettings.loadOptions();
            initStats();
            saveConfig();
        }
        catch(Throwable e)
        {
            logger.throwing("ModLoader", "init", e);
            ThrowException("ModLoader has failed to initialize.", e);
            if(logHandler != null)
            {
                logHandler.close();
            }
            throw new RuntimeException(e);
        }
    }

    private static void initStats()
    {
        for(int id = 0; id < Block.blocksList.length; id++)
        {
            if(!StatList.oneShotStats.containsKey(Integer.valueOf(0x1000000 + id)) && Block.blocksList[id] != null && Block.blocksList[id].getEnableStats())
            {
                String str = StringTranslate.getInstance().translateKeyFormat("stat.mineBlock", new Object[] {
                    Block.blocksList[id].translateBlockName()
                });
                StatList.mineBlockStatArray[id] = (new StatCrafting(0x1000000 + id, str, id)).registerStat();
                StatList.objectMineStats.add(StatList.mineBlockStatArray[id]);
            }
        }

        for(int id = 0; id < Item.itemsList.length; id++)
        {
            if(!StatList.oneShotStats.containsKey(Integer.valueOf(0x1020000 + id)) && Item.itemsList[id] != null)
            {
                String str = StringTranslate.getInstance().translateKeyFormat("stat.useItem", new Object[] {
                    Item.itemsList[id].getStatName()
                });
                StatList.objectUseStats[id] = (new StatCrafting(0x1020000 + id, str, id)).registerStat();
                if(id >= Block.blocksList.length)
                {
                    StatList.itemStats.add(StatList.objectUseStats[id]);
                }
            }
            if(!StatList.oneShotStats.containsKey(Integer.valueOf(0x1030000 + id)) && Item.itemsList[id] != null && Item.itemsList[id].isDamagable())
            {
                String str = StringTranslate.getInstance().translateKeyFormat("stat.breakItem", new Object[] {
                    Item.itemsList[id].getStatName()
                });
                StatList.objectBreakStats[id] = (new StatCrafting(0x1030000 + id, str, id)).registerStat();
            }
        }

        HashSet idHashSet = new HashSet();
        Object result;
        for(Iterator iterator = CraftingManager.getInstance().getRecipeList().iterator(); iterator.hasNext(); idHashSet.add(Integer.valueOf(((IRecipe)result).getRecipeOutput().itemID)))
        {
            result = iterator.next();
        }

        for(Iterator iterator1 = FurnaceRecipes.smelting().getSmeltingList().values().iterator(); iterator1.hasNext(); idHashSet.add(Integer.valueOf(((ItemStack)result).itemID)))
        {
            result = iterator1.next();
        }

        for(Iterator iterator2 = idHashSet.iterator(); iterator2.hasNext();)
        {
            int id = ((Integer)iterator2.next()).intValue();
            if(!StatList.oneShotStats.containsKey(Integer.valueOf(0x1010000 + id)) && Item.itemsList[id] != null)
            {
                String str = StringTranslate.getInstance().translateKeyFormat("stat.craftItem", new Object[] {
                    Item.itemsList[id].getStatName()
                });
                StatList.objectCraftStats[id] = (new StatCrafting(0x1010000 + id, str, id)).registerStat();
            }
        }

    }

    public static boolean isGUIOpen(Class gui)
    {
        Minecraft game = getMinecraftInstance();
        if(gui == null)
        {
            return game.currentScreen == null;
        }
        if(game.currentScreen == null && gui != null)
        {
            return false;
        } else
        {
            return gui.isInstance(game.currentScreen);
        }
    }

    public static boolean isModLoaded(String modname)
    {
        Class chk = null;
        try
        {
            chk = Class.forName(modname);
        }
        catch(ClassNotFoundException e)
        {
            return false;
        }
        if(chk != null)
        {
            for(Iterator iterator = modList.iterator(); iterator.hasNext();)
            {
                BaseMod mod = (BaseMod)iterator.next();
                if(chk.isInstance(mod))
                {
                    return true;
                }
            }

        }
        return false;
    }

    public static void loadConfig()
        throws IOException
    {
        cfgdir.mkdir();
        if(!cfgfile.exists() && !cfgfile.createNewFile())
        {
            return;
        }
        if(cfgfile.canRead())
        {
            InputStream in = new FileInputStream(cfgfile);
            props.load(in);
            in.close();
        }
    }

    public static java.awt.image.BufferedImage loadImage(RenderEngine texCache, String path)
        throws Exception
    {
        TexturePackList pack = (TexturePackList)getPrivateValue(net.minecraft.src.RenderEngine.class, texCache, 11);
        InputStream input = pack.selectedTexturePack.getResourceAsStream(path);
        if(input == null)
        {
            throw new Exception((new StringBuilder("Image not found: ")).append(path).toString());
        }
        java.awt.image.BufferedImage image = ImageIO.read(input);
        if(image == null)
        {
            throw new Exception((new StringBuilder("Image corrupted: ")).append(path).toString());
        } else
        {
            return image;
        }
    }

    public static void OnItemPickup(EntityPlayer player, ItemStack item)
    {
        BaseMod mod;
        for(Iterator iterator = modList.iterator(); iterator.hasNext(); mod.OnItemPickup(player, item))
        {
            mod = (BaseMod)iterator.next();
        }

    }

    public static void OnTick(Minecraft game)
    {
        if(!hasInit)
        {
            init();
            logger.fine("Initialized");
        }
        if(texPack == null || game.gameSettings.skin != texPack)
        {
            texturesAdded = false;
            texPack = game.gameSettings.skin;
        }
        if(!texturesAdded && game.renderEngine != null)
        {
            RegisterAllTextureOverrides(game.renderEngine);
            texturesAdded = true;
        }
        long newclock = 0L;
        if(game.theWorld != null)
        {
            newclock = game.theWorld.getWorldTime();
            for(Iterator iter = inGameHooks.entrySet().iterator(); iter.hasNext();)
            {
                java.util.Map.Entry modSet = (java.util.Map.Entry)iter.next();
                if((clock != newclock || !((Boolean)modSet.getValue()).booleanValue()) && !((BaseMod)modSet.getKey()).OnTickInGame(game))
                {
                    iter.remove();
                }
            }

        }
        if(game.currentScreen != null)
        {
            for(Iterator iter = inGUIHooks.entrySet().iterator(); iter.hasNext();)
            {
                java.util.Map.Entry modSet = (java.util.Map.Entry)iter.next();
                if((clock != newclock || !(((Boolean)modSet.getValue()).booleanValue() & (game.theWorld != null))) && !((BaseMod)modSet.getKey()).OnTickInGUI(game, game.currentScreen))
                {
                    iter.remove();
                }
            }

        }
        if(clock != newclock)
        {
            for(Iterator iterator = keyList.entrySet().iterator(); iterator.hasNext();)
            {
                java.util.Map.Entry modSet = (java.util.Map.Entry)iterator.next();
                for(Iterator iterator1 = ((Map)modSet.getValue()).entrySet().iterator(); iterator1.hasNext();)
                {
                    java.util.Map.Entry keySet = (java.util.Map.Entry)iterator1.next();
                    boolean state = Keyboard.isKeyDown(((KeyBinding)keySet.getKey()).keyCode);
                    boolean keyInfo[] = (boolean[])keySet.getValue();
                    boolean oldState = keyInfo[1];
                    keyInfo[1] = state;
                    if(state && (!oldState || keyInfo[0]))
                    {
                        ((BaseMod)modSet.getKey()).KeyboardEvent((KeyBinding)keySet.getKey());
                    }
                }

            }

        }
        clock = newclock;
    }

    public static void OpenGUI(EntityPlayer player, GuiScreen gui)
    {
        if(!hasInit)
        {
            init();
            logger.fine("Initialized");
        }
        Minecraft game = getMinecraftInstance();
        if(game.thePlayer != player)
        {
            return;
        }
        if(gui != null)
        {
            game.displayGuiScreen(gui);
        }
    }

    public static void PopulateChunk(IChunkProvider generator, int chunkX, int chunkZ, World world)
    {
        if(!hasInit)
        {
            init();
            logger.fine("Initialized");
        }
        Random rnd = new Random(world.getRandomSeed());
        long xSeed = (rnd.nextLong() / 2L) * 2L + 1L;
        long zSeed = (rnd.nextLong() / 2L) * 2L + 1L;
        rnd.setSeed((long)chunkX * xSeed + (long)chunkZ * zSeed ^ world.getRandomSeed());
        for(Iterator iterator = modList.iterator(); iterator.hasNext();)
        {
            BaseMod mod = (BaseMod)iterator.next();
            if(generator.makeString().equals("RandomLevelSource"))
            {
                mod.GenerateSurface(world, rnd, chunkX << 4, chunkZ << 4);
            } else
            if(generator.makeString().equals("HellRandomLevelSource"))
            {
                mod.GenerateNether(world, rnd, chunkX << 4, chunkZ << 4);
            }
        }

    }

    private static void readFromClassPath(File source)
        throws FileNotFoundException, IOException
    {
        logger.finer((new StringBuilder("Adding mods from ")).append(source.getCanonicalPath()).toString());
        ClassLoader loader = (net.minecraft.src.ModLoader.class).getClassLoader();
        if(source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip")))
        {
            logger.finer("Zip found.");
            InputStream input = new FileInputStream(source);
            ZipInputStream zip = new ZipInputStream(input);
            ZipEntry entry = null;
            do
            {
                entry = zip.getNextEntry();
                if(entry == null)
                {
                    break;
                }
                String name = entry.getName();
                if(!entry.isDirectory() && name.startsWith("mod_") && name.endsWith(".class"))
                {
                    addMod(loader, name);
                }
            } while(true);
            input.close();
        } else
        if(source.isDirectory())
        {
            Package pkg = (net.minecraft.src.ModLoader.class).getPackage();
            if(pkg != null)
            {
                String pkgdir = pkg.getName().replace('.', File.separatorChar);
                source = new File(source, pkgdir);
            }
            logger.finer("Directory found.");
            File files[] = source.listFiles();
            if(files != null)
            {
                for(int i = 0; i < files.length; i++)
                {
                    String name = files[i].getName();
                    if(files[i].isFile() && name.startsWith("mod_") && name.endsWith(".class"))
                    {
                        addMod(loader, name);
                    }
                }

            }
        }
    }

    private static void readFromModFolder(File folder)
        throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
    {
        ClassLoader loader = (net.minecraft.client.Minecraft.class).getClassLoader();
        Method addURL = (java.net.URLClassLoader.class).getDeclaredMethod("addURL", new Class[] {
            java.net.URL.class
        });
        addURL.setAccessible(true);
        if(!folder.isDirectory())
        {
            throw new IllegalArgumentException("folder must be a Directory.");
        }
        File sourcefiles[] = folder.listFiles();
        if(loader instanceof URLClassLoader)
        {
            for(int file = 0; file < sourcefiles.length; file++)
            {
                File source = sourcefiles[file];
                if(source.isDirectory() || source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip")))
                {
                    addURL.invoke(loader, new Object[] {
                        source.toURI().toURL()
                    });
                }
            }

        }
        for(int file = 0; file < sourcefiles.length; file++)
        {
            File source = sourcefiles[file];
            if(source.isDirectory() || source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip")))
            {
                logger.finer((new StringBuilder("Adding mods from ")).append(source.getCanonicalPath()).toString());
                if(source.isFile())
                {
                    logger.finer("Zip found.");
                    InputStream input = new FileInputStream(source);
                    ZipInputStream zip = new ZipInputStream(input);
                    ZipEntry entry = null;
                    do
                    {
                        entry = zip.getNextEntry();
                        if(entry == null)
                        {
                            break;
                        }
                        String name = entry.getName();
                        if(!entry.isDirectory() && name.startsWith("mod_") && name.endsWith(".class"))
                        {
                            addMod(loader, name);
                        }
                    } while(true);
                    zip.close();
                    input.close();
                } else
                if(source.isDirectory())
                {
                    Package pkg = (net.minecraft.src.ModLoader.class).getPackage();
                    if(pkg != null)
                    {
                        String pkgdir = pkg.getName().replace('.', File.separatorChar);
                        source = new File(source, pkgdir);
                    }
                    logger.finer("Directory found.");
                    File dirfiles[] = source.listFiles();
                    if(dirfiles != null)
                    {
                        for(int j = 0; j < dirfiles.length; j++)
                        {
                            String name = dirfiles[j].getName();
                            if(dirfiles[j].isFile() && name.startsWith("mod_") && name.endsWith(".class"))
                            {
                                addMod(loader, name);
                            }
                        }

                    }
                }
            }
        }

    }

    public static KeyBinding[] RegisterAllKeys(KeyBinding w[])
    {
        List combinedList = new LinkedList();
        combinedList.addAll(Arrays.asList(w));
        Map keyMap;
        for(Iterator iterator = keyList.values().iterator(); iterator.hasNext(); combinedList.addAll(keyMap.keySet()))
        {
            keyMap = (Map)iterator.next();
        }

        return (KeyBinding[])combinedList.toArray(new KeyBinding[0]);
    }

    public static void RegisterAllTextureOverrides(RenderEngine texCache)
    {
        animList.clear();
        Minecraft game = getMinecraftInstance();
        BaseMod mod;
        for(Iterator iterator = modList.iterator(); iterator.hasNext(); mod.RegisterAnimation(game))
        {
            mod = (BaseMod)iterator.next();
        }

        TextureFX anim;
        for(Iterator iterator1 = animList.iterator(); iterator1.hasNext(); texCache.registerTextureFX(anim))
        {
            anim = (TextureFX)iterator1.next();
        }

        for(Iterator iterator2 = overrides.entrySet().iterator(); iterator2.hasNext();)
        {
            java.util.Map.Entry overlay = (java.util.Map.Entry)iterator2.next();
            for(Iterator iterator3 = ((Map)overlay.getValue()).entrySet().iterator(); iterator3.hasNext();)
            {
                java.util.Map.Entry overlayEntry = (java.util.Map.Entry)iterator3.next();
                String overlayPath = (String)overlayEntry.getKey();
                int index = ((Integer)overlayEntry.getValue()).intValue();
                int dst = ((Integer)overlay.getKey()).intValue();
                try
                {
                    java.awt.image.BufferedImage im = loadImage(texCache, overlayPath);
                    anim = new ModTextureStatic(index, dst, im);
                    texCache.registerTextureFX(anim);
                }
                catch(Exception e)
                {
                    logger.throwing("ModLoader", "RegisterAllTextureOverrides", e);
                    ThrowException(e);
                    throw new RuntimeException(e);
                }
            }

        }

    }

    public static void RegisterBlock(Block block)
    {
        RegisterBlock(block, null);
    }

    public static void RegisterBlock(Block block, Class itemclass)
    {
        try
        {
            if(block == null)
            {
                throw new IllegalArgumentException("block parameter cannot be null.");
            }
            List list = (List)field_blockList.get(null);
            list.add(block);
            int id = block.blockID;
            ItemBlock item = null;
            if(itemclass != null)
            {
                item = (ItemBlock)itemclass.getConstructor(new Class[] {
                    Integer.TYPE
                }).newInstance(new Object[] {
                    Integer.valueOf(id - 256)
                });
            } else
            {
                item = new ItemBlock(id - 256);
            }
            if(Block.blocksList[id] != null && Item.itemsList[id] == null)
            {
                Item.itemsList[id] = item;
            }
        }
        catch(IllegalArgumentException e)
        {
            logger.throwing("ModLoader", "RegisterBlock", e);
            ThrowException(e);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "RegisterBlock", e);
            ThrowException(e);
        }
        catch(SecurityException e)
        {
            logger.throwing("ModLoader", "RegisterBlock", e);
            ThrowException(e);
        }
        catch(InstantiationException e)
        {
            logger.throwing("ModLoader", "RegisterBlock", e);
            ThrowException(e);
        }
        catch(InvocationTargetException e)
        {
            logger.throwing("ModLoader", "RegisterBlock", e);
            ThrowException(e);
        }
        catch(NoSuchMethodException e)
        {
            logger.throwing("ModLoader", "RegisterBlock", e);
            ThrowException(e);
        }
    }

    public static void RegisterEntityID(Class entityClass, String entityName, int id)
    {
        try
        {
            method_RegisterEntityID.invoke(null, new Object[] {
                entityClass, entityName, Integer.valueOf(id)
            });
        }
        catch(IllegalArgumentException e)
        {
            logger.throwing("ModLoader", "RegisterEntityID", e);
            ThrowException(e);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "RegisterEntityID", e);
            ThrowException(e);
        }
        catch(InvocationTargetException e)
        {
            logger.throwing("ModLoader", "RegisterEntityID", e);
            ThrowException(e);
        }
    }

    public static void RegisterKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat)
    {
        Map keyMap = (Map)keyList.get(mod);
        if(keyMap == null)
        {
            keyMap = new HashMap();
        }
        boolean aflag[] = new boolean[2];
        aflag[0] = allowRepeat;
        keyMap.put(keyHandler, aflag);
        keyList.put(mod, keyMap);
    }

    public static void RegisterTileEntity(Class tileEntityClass, String id)
    {
        RegisterTileEntity(tileEntityClass, id, null);
    }

    public static void RegisterTileEntity(Class tileEntityClass, String id, TileEntitySpecialRenderer renderer)
    {
        try
        {
            method_RegisterTileEntity.invoke(null, new Object[] {
                tileEntityClass, id
            });
            if(renderer != null)
            {
                TileEntityRenderer ref = TileEntityRenderer.instance;
                Map renderers = (Map)field_TileEntityRenderers.get(ref);
                renderers.put(tileEntityClass, renderer);
                renderer.setTileEntityRenderer(ref);
            }
        }
        catch(IllegalArgumentException e)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", e);
            ThrowException(e);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", e);
            ThrowException(e);
        }
        catch(InvocationTargetException e)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", e);
            ThrowException(e);
        }
    }

    public static void RemoveSpawn(Class entityClass, EnumCreatureType spawnList)
    {
        RemoveSpawn(entityClass, spawnList, null);
    }

    public static void RemoveSpawn(Class entityClass, EnumCreatureType spawnList, BiomeGenBase biomes[])
    {
        if(entityClass == null)
        {
            throw new IllegalArgumentException("entityClass cannot be null");
        }
        if(spawnList == null)
        {
            throw new IllegalArgumentException("spawnList cannot be null");
        }
        if(biomes == null)
        {
            biomes = standardBiomes;
        }
        for(int i = 0; i < biomes.length; i++)
        {
            List list = biomes[i].getSpawnableList(spawnList);
            if(list != null)
            {
                for(Iterator iter = list.iterator(); iter.hasNext();)
                {
                    SpawnListEntry entry = (SpawnListEntry)iter.next();
                    if(entry.entityClass == entityClass)
                    {
                        iter.remove();
                    }
                }

            }
        }

    }

    public static void RemoveSpawn(String entityName, EnumCreatureType spawnList)
    {
        RemoveSpawn(entityName, spawnList, null);
    }

    public static void RemoveSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase biomes[])
    {
        Class entityClass = (Class)classMap.get(entityName);
        if(entityClass != null && (net.minecraft.src.EntityLiving.class).isAssignableFrom(entityClass))
        {
            RemoveSpawn(entityClass, spawnList, biomes);
        }
    }

    public static boolean RenderBlockIsItemFull3D(int modelID)
    {
        if(!blockSpecialInv.containsKey(Integer.valueOf(modelID)))
        {
            return modelID == 16;
        } else
        {
            return ((Boolean)blockSpecialInv.get(Integer.valueOf(modelID))).booleanValue();
        }
    }

    public static void RenderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        BaseMod mod = (BaseMod)blockModels.get(Integer.valueOf(modelID));
        if(mod == null)
        {
            return;
        } else
        {
            mod.RenderInvBlock(renderer, block, metadata, modelID);
            return;
        }
    }

    public static boolean RenderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID)
    {
        BaseMod mod = (BaseMod)blockModels.get(Integer.valueOf(modelID));
        if(mod == null)
        {
            return false;
        } else
        {
            return mod.RenderWorldBlock(renderer, world, x, y, z, block, modelID);
        }
    }

    public static void saveConfig()
        throws IOException
    {
        cfgdir.mkdir();
        if(!cfgfile.exists() && !cfgfile.createNewFile())
        {
            return;
        }
        if(cfgfile.canWrite())
        {
            OutputStream out = new FileOutputStream(cfgfile);
            props.store(out, "ModLoader Config");
            out.close();
        }
    }

    public static void SetInGameHook(BaseMod mod, boolean enable, boolean useClock)
    {
        if(enable)
        {
            inGameHooks.put(mod, Boolean.valueOf(useClock));
        } else
        {
            inGameHooks.remove(mod);
        }
    }

    public static void SetInGUIHook(BaseMod mod, boolean enable, boolean useClock)
    {
        if(enable)
        {
            inGUIHooks.put(mod, Boolean.valueOf(useClock));
        } else
        {
            inGUIHooks.remove(mod);
        }
    }

    public static void setPrivateValue(Class instanceclass, Object instance, int fieldindex, Object value)
        throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field f = instanceclass.getDeclaredFields()[fieldindex];
            f.setAccessible(true);
            int modifiers = field_modifiers.getInt(f);
            if((modifiers & 0x10) != 0)
            {
                field_modifiers.setInt(f, modifiers & 0xffffffef);
            }
            f.set(instance, value);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "setPrivateValue", e);
            ThrowException("An impossible error has occured!", e);
        }
    }

    public static void setPrivateValue(Class instanceclass, Object instance, String field, Object value)
        throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field f = instanceclass.getDeclaredField(field);
            int modifiers = field_modifiers.getInt(f);
            if((modifiers & 0x10) != 0)
            {
                field_modifiers.setInt(f, modifiers & 0xffffffef);
            }
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch(IllegalAccessException e)
        {
            logger.throwing("ModLoader", "setPrivateValue", e);
            ThrowException("An impossible error has occured!", e);
        }
    }

    private static void setupProperties(Class mod)
        throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException, NoSuchFieldException
    {
        Properties modprops = new Properties();
        File modcfgfile = new File(cfgdir, (new StringBuilder(String.valueOf(mod.getName()))).append(".cfg").toString());
        if(modcfgfile.exists() && modcfgfile.canRead())
        {
            modprops.load(new FileInputStream(modcfgfile));
        }
        StringBuilder helptext = new StringBuilder();
        Field afield[];
        int j = (afield = mod.getFields()).length;
        for(int i = 0; i < j; i++)
        {
            Field field = afield[i];
            if((field.getModifiers() & 8) == 0 || !field.isAnnotationPresent(net.minecraft.src.MLProp.class))
            {
                continue;
            }
            Class type = field.getType();
            MLProp annotation = (MLProp)field.getAnnotation(net.minecraft.src.MLProp.class);
            String key = annotation.name().length() != 0 ? annotation.name() : field.getName();
            Object currentvalue = field.get(null);
            StringBuilder range = new StringBuilder();
            if(annotation.min() != (-1.0D / 0.0D))
            {
                range.append(String.format(",>=%.1f", new Object[] {
                    Double.valueOf(annotation.min())
                }));
            }
            if(annotation.max() != (1.0D / 0.0D))
            {
                range.append(String.format(",<=%.1f", new Object[] {
                    Double.valueOf(annotation.max())
                }));
            }
            StringBuilder info = new StringBuilder();
            if(annotation.info().length() > 0)
            {
                info.append(" -- ");
                info.append(annotation.info());
            }
            helptext.append(String.format("%s (%s:%s%s)%s\n", new Object[] {
                key, type.getName(), currentvalue, range, info
            }));
            if(modprops.containsKey(key))
            {
                String strvalue = modprops.getProperty(key);
                Object value = null;
                if(type.isAssignableFrom(java.lang.String.class))
                {
                    value = strvalue;
                } else
                if(type.isAssignableFrom(Integer.TYPE))
                {
                    value = Integer.valueOf(Integer.parseInt(strvalue));
                } else
                if(type.isAssignableFrom(Short.TYPE))
                {
                    value = Short.valueOf(Short.parseShort(strvalue));
                } else
                if(type.isAssignableFrom(Byte.TYPE))
                {
                    value = Byte.valueOf(Byte.parseByte(strvalue));
                } else
                if(type.isAssignableFrom(Boolean.TYPE))
                {
                    value = Boolean.valueOf(Boolean.parseBoolean(strvalue));
                } else
                if(type.isAssignableFrom(Float.TYPE))
                {
                    value = Float.valueOf(Float.parseFloat(strvalue));
                } else
                if(type.isAssignableFrom(Double.TYPE))
                {
                    value = Double.valueOf(Double.parseDouble(strvalue));
                }
                if(value == null)
                {
                    continue;
                }
                if(value instanceof Number)
                {
                    double num = ((Number)value).doubleValue();
                    if(annotation.min() != (-1.0D / 0.0D) && num < annotation.min() || annotation.max() != (1.0D / 0.0D) && num > annotation.max())
                    {
                        continue;
                    }
                }
                logger.finer((new StringBuilder(String.valueOf(key))).append(" set to ").append(value).toString());
                if(!value.equals(currentvalue))
                {
                    field.set(null, value);
                }
            } else
            {
                logger.finer((new StringBuilder(String.valueOf(key))).append(" not in config, using default: ").append(currentvalue).toString());
                modprops.setProperty(key, currentvalue.toString());
            }
        }

        if(!modprops.isEmpty() && (modcfgfile.exists() || modcfgfile.createNewFile()) && modcfgfile.canWrite())
        {
            modprops.store(new FileOutputStream(modcfgfile), helptext.toString());
        }
    }

    public static void TakenFromCrafting(EntityPlayer player, ItemStack item)
    {
        BaseMod mod;
        for(Iterator iterator = modList.iterator(); iterator.hasNext(); mod.TakenFromCrafting(player, item))
        {
            mod = (BaseMod)iterator.next();
        }

    }

    public static void TakenFromFurnace(EntityPlayer player, ItemStack item)
    {
        BaseMod mod;
        for(Iterator iterator = modList.iterator(); iterator.hasNext(); mod.TakenFromFurnace(player, item))
        {
            mod = (BaseMod)iterator.next();
        }

    }

    public static void ThrowException(String message, Throwable e)
    {
        Minecraft game = getMinecraftInstance();
        if(game != null)
        {
            game.displayUnexpectedThrowable(new UnexpectedThrowable(message, e));
        } else
        {
            throw new RuntimeException(e);
        }
    }

    private static void ThrowException(Throwable e)
    {
        ThrowException("Exception occured in ModLoader", e);
    }

    private ModLoader()
    {
    }

    private static final List animList = new LinkedList();
    private static final Map blockModels = new HashMap();
    private static final Map blockSpecialInv = new HashMap();
    private static final File cfgdir;
    private static final File cfgfile;
    public static Level cfgLoggingLevel;
    private static Map classMap = null;
    private static long clock = 0L;
    public static final boolean DEBUG = false;
    private static Field field_animList = null;
    private static Field field_armorList = null;
    private static Field field_blockList = null;
    private static Field field_modifiers = null;
    private static Field field_TileEntityRenderers = null;
    private static boolean hasInit = false;
    private static int highestEntityId = 3000;
    private static final Map inGameHooks = new HashMap();
    private static final Map inGUIHooks = new HashMap();
    private static Minecraft instance = null;
    private static int itemSpriteIndex = 0;
    private static int itemSpritesLeft = 0;
    private static final Map keyList = new HashMap();
    private static final File logfile = new File(Minecraft.getMinecraftDir(), "ModLoader.txt");
    private static final Logger logger = Logger.getLogger("ModLoader");
    private static FileHandler logHandler = null;
    private static Method method_RegisterEntityID = null;
    private static Method method_RegisterTileEntity = null;
    private static final File modDir = new File(Minecraft.getMinecraftDir(), "/mods/");
    private static final LinkedList modList = new LinkedList();
    private static int nextBlockModelID = 1000;
    private static final Map overrides = new HashMap();
    public static final Properties props = new Properties();
    private static BiomeGenBase standardBiomes[];
    private static int terrainSpriteIndex = 0;
    private static int terrainSpritesLeft = 0;
    private static String texPack = null;
    private static boolean texturesAdded = false;
    private static final boolean usedItemSprites[] = new boolean[256];
    private static final boolean usedTerrainSprites[] = new boolean[256];
    public static final String VERSION = "ModLoader Beta 1.7.3";

    static 
    {
        cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
        cfgfile = new File(cfgdir, "ModLoader.cfg");
        cfgLoggingLevel = Level.FINER;
    }
}
