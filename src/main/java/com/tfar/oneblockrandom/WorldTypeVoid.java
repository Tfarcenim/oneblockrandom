// 
// Decompiled by Procyon v0.5.36
// 

package com.tfar.oneblockrandom;

import java.util.Random;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiCustomizeWorldScreen;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;

public class WorldTypeVoid extends WorldType
{
    private WorldType overridenWorldType;
    
    public WorldTypeVoid() {
        super("oneblockrandom");
        if (ConfigOptions.worldGenSettings.worldGenType == ConfigOptions.WorldGenSettings.WorldGenType.WORLDTYPE) {
            this.overridenWorldType = WorldType.byName(ConfigOptions.worldGenSettings.worldGenSpecialParameters);
        }
    }
    
    public boolean hasInfoNotice() {
        return true;
    }
    
    public int getMinimumSpawnHeight(final World world) {
        return ConfigOptions.islandSettings.islandYLevel;
    }
    
    public int getSpawnFuzz() {
        return 2;
    }
    
    public float getCloudHeight() {
        return 128.0f;
    }
    
    public double getHorizon(final World world) {
        return 64.0;
    }
    
    public BiomeProvider getBiomeProvider(final World world) {
        if (this.overridenWorldType != null) {
            return this.overridenWorldType.getBiomeProvider(world);
        }
        return new BiomeProvider(world.getWorldInfo());
    }
    
    @SideOnly(Side.CLIENT)
    public void onCustomizeButton(final Minecraft mc, final GuiCreateWorld guiCreateWorld) {
        if (ConfigOptions.worldGenSettings.worldGenType == ConfigOptions.WorldGenSettings.WorldGenType.CUSTOMIZED) {
            mc.displayGuiScreen((GuiScreen)new GuiCustomizeWorldScreen((GuiScreen)guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
        }
        else if (ConfigOptions.WorldGenSettings.WorldGenType.WORLDTYPE == ConfigOptions.worldGenSettings.worldGenType && this.overridenWorldType != null) {
            this.overridenWorldType.onCustomizeButton(mc, guiCreateWorld);
        }
    }
    
    public boolean isCustomizable() {
        return ConfigOptions.worldGenSettings.worldGenType == ConfigOptions.WorldGenSettings.WorldGenType.CUSTOMIZED;
    }
    
    public IChunkGenerator getChunkGenerator(final World world, final String generatorOptions) {
        if (this.overridenWorldType != null) {
            return this.overridenWorldType.getChunkGenerator(world, generatorOptions);
        }
        if (ConfigOptions.worldGenSettings.worldGenType != ConfigOptions.WorldGenSettings.WorldGenType.OVERWORLD && ConfigOptions.worldGenSettings.worldGenType != ConfigOptions.WorldGenSettings.WorldGenType.CUSTOMIZED) {
            String genSettings = "3;1*minecraft:air";
            if (ConfigOptions.worldGenSettings.worldGenType == ConfigOptions.WorldGenSettings.WorldGenType.SUPERFLAT) {
                genSettings = ConfigOptions.worldGenSettings.worldGenSpecialParameters;
            }
            final ChunkGeneratorFlat provider = new ChunkGeneratorFlat(world, world.getSeed(), false, genSettings);
            world.setSeaLevel(63);
            return (IChunkGenerator)provider;
        }
        return (IChunkGenerator)new ChunkGeneratorOverworld(world, world.getSeed(), true, generatorOptions);
    }
    
    public boolean handleSlimeSpawnReduction(final Random random, final World world) {
        if (this.overridenWorldType != null) {
            return this.overridenWorldType.handleSlimeSpawnReduction(random, world);
        }
        return super.handleSlimeSpawnReduction(random, world);
    }
}
