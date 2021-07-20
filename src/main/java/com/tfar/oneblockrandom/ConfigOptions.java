// 
// Decompiled by Procyon v0.5.36
// 

package com.tfar.oneblockrandom;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.common.config.Config;

public class ConfigOptions
{
    @Config.Comment({ "Config Settings for the world generation" })
    public static WorldGenSettings worldGenSettings;
    @Config.Comment({ "Config Settings for the world generation" })
    public static IslandSettings islandSettings;
    
    static {
        ConfigOptions.worldGenSettings = new WorldGenSettings();
        ConfigOptions.islandSettings = new IslandSettings();
    }
    
    public static class WorldGenSettings
    {
        @Config.Comment({ "Overworld generation type" })
        public WorldGenType worldGenType;
        @Config.Comment({ "VOID-NOT USED, OVERWORLD-NOT USED, SUPERFLAT-Use the string as used for normal flat worlds, WORLDTYPE-world type to be used (set like server level-types), CUSTOMIZED-NOT USED" })
        public String worldGenSpecialParameters;
        
        public WorldGenSettings() {
            this.worldGenType = WorldGenType.VOID;
            this.worldGenSpecialParameters = "";
        }
        
        public enum WorldGenType
        {
            VOID, 
            OVERWORLD, 
            SUPERFLAT, 
            WORLDTYPE, 
            CUSTOMIZED;
        }
    }
    
    public static class IslandSettings
    {
        @Config.Comment({ "Y Level to spawn islands at (Set to 2 above where you want the ground block)" })
        public int islandYLevel;
        
        public IslandSettings() {
            this.islandYLevel = 88;
        }
        
        @SubscribeEvent
        public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals("oneblockrandom")) {
                ConfigManager.sync("oneblockrandom", Config.Type.INSTANCE);
            }
        }
    }
}
