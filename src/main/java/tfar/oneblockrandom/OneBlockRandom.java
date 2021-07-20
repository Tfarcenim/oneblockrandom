// 
// Decompiled by Procyon v0.5.36
// 

package tfar.oneblockrandom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(OneBlockRandom.MODID)
@Mod.EventBusSubscriber
public class OneBlockRandom {
    public static final String MODID = "oneblockrandom";
    private static Logger logger;
    public static final List<ItemStack> allItems;
    private static final Random rand;
    
    public OneBlockRandom() {
        IEventBus bus  = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        bus.addGenericListener(ForgeWorldType.class,this::worldType);
        MinecraftForge.EVENT_BUS.addListener(this::breakBlock);
        MinecraftForge.EVENT_BUS.addListener(this::respawn);
        MinecraftForge.EVENT_BUS.addListener(this::playerJoin);
    }

    public static ForgeWorldType type;

    public void init(final FMLCommonSetupEvent event) {
        for (final Item item : ForgeRegistries.ITEMS) {
            final NonNullList<ItemStack> nonNullList = NonNullList.create();
            final Collection<ItemGroup> creativeTabs2 = item.getCreativeTabs();
            for (final ItemGroup creativeTab : creativeTabs2) {
                if (creativeTab != null) {
                    item.fillItemGroup(creativeTab, nonNullList);
                }
            }
            item.fillItemGroup(ItemGroup.SEARCH, nonNullList);
            for (final ItemStack stack : nonNullList) {
                OneBlockRandom.allItems.add(stack.copy());
            }
        }
    }

    private void worldType(RegistryEvent.Register<ForgeWorldType> e) {
        type = new ForgeWorldType(new ChunkFactory());
        e.getRegistry().register(type.setRegistryName(MODID));
    }
    
    public void breakBlock(final BlockEvent.BreakEvent e) {
        final BlockPos pos = e.getPos();
        final ModSavedData modSavedData = ModSavedData.getDefaultInstance((ServerWorld)e.getWorld());
        if (modSavedData.isDirtPos(pos)) {
            e.setCanceled(true);
            InventoryHelper.spawnItemStack((World) e.getWorld(), pos.getX(), pos.getY(), pos.getZ(), OneBlockRandom.allItems.get(OneBlockRandom.rand.nextInt(OneBlockRandom.allItems.size())).copy());
        }
    }
    
    public void respawn(final PlayerEvent.PlayerRespawnEvent e) {
        final PlayerEntity player = e.getPlayer();
        if (player.world.getDimensionKey() == World.OVERWORLD) {
            final ModSavedData modSavedData = ModSavedData.getDefaultInstance((ServerWorld)player.world);
            if (!player.getBedPosition().isPresent()) {
                final BlockPos pos = modSavedData.getPosForPlayer(player.getGameProfile().getId());
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 2.6, pos.getZ() + 0.5);
            }
        }
    }
    
    public void playerJoin(final PlayerEvent.PlayerLoggedInEvent e) {
        final ServerPlayerEntity player = (ServerPlayerEntity)e.getPlayer();
        final ModSavedData modSavedData = ModSavedData.getDefaultInstance((ServerWorld)player.world);
        if (!modSavedData.hasPlayer(player.getUniqueID())) {
            final UUID uuid = player.getGameProfile().getId();
            final BlockPos pos = (modSavedData.getPlayerCount() == 0) ? new BlockPos(0, 64, 0) : getRandomPointFrom(modSavedData.getLast(), 32);
            //player.setSpawnPoint(pos, true);
            player.world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 2.6, pos.getZ() + 0.5);
            modSavedData.addDirtPos(uuid, pos);
        }
    }
    
    public static BlockPos getRandomPointFrom(final BlockPos pos, final int distance) {
        final double rot = OneBlockRandom.rand.nextInt(360);
        final int x = (int)(pos.getX() + distance * Math.cos(0.017453292519943295 * rot));
        final int z = (int)(pos.getZ() + distance * Math.sin(0.017453292519943295 * rot));
        return new BlockPos(x, pos.getY(), z);
    }
    
    static {
        allItems = new ArrayList<>();
        rand = new Random();
    }
}
