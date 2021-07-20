package tfar.oneblockrandom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.common.world.ForgeWorldType;

import java.util.Optional;

public class ChunkFactory implements ForgeWorldType.IBasicChunkGeneratorFactory {
    @Override
    public ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed) {
        if (ConfigOptions.worldGenSettings.worldGenType != ConfigOptions.WorldGenSettings.WorldGenType.OVERWORLD && ConfigOptions.worldGenSettings.worldGenType != ConfigOptions.WorldGenSettings.WorldGenType.CUSTOMIZED) {
            return new FlatChunkGenerator(buildSettings(biomeRegistry));
        }
        return new NoiseChunkGenerator(new OverworldBiomeProvider(seed, false, false, biomeRegistry),
                seed,() -> dimensionSettingsRegistry.getOrThrow(DimensionSettings.OVERWORLD));
    }

    public static FlatGenerationSettings buildSettings(Registry<Biome> biomeRegistry) {
        DimensionStructuresSettings dimensionstructuressettings = new DimensionStructuresSettings(Optional.of(DimensionStructuresSettings.field_236192_c_), Maps.newHashMap());
        FlatGenerationSettings flatgenerationsettings = new FlatGenerationSettings(dimensionstructuressettings, biomeRegistry);
        flatgenerationsettings.biomeToUse = () -> biomeRegistry.getOrThrow(Biomes.PLAINS);
        flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.AIR));
        flatgenerationsettings.updateLayers();
        return flatgenerationsettings;
    }
}
