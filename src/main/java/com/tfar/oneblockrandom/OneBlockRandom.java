// 
// Decompiled by Procyon v0.5.36
// 

package com.tfar.oneblockrandom;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import java.util.Iterator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.NonNullList;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.util.Random;
import net.minecraft.item.ItemStack;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "oneblockrandom", name = "Example Mod", version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
@Mod.EventBusSubscriber
public class OneBlockRandom
{
    public static final String MODID = "oneblockrandom";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";
    private static Logger logger;
    public static final List<ItemStack> allItems;
    private static final Random rand;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        OneBlockRandom.logger = event.getModLog();
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        new WorldTypeVoid();
        for (final Item item : ForgeRegistries.ITEMS) {
            final NonNullList<ItemStack> nonNullList = (NonNullList<ItemStack>)NonNullList.create();
            final CreativeTabs[] creativeTabs2;
            final CreativeTabs[] creativeTabs = creativeTabs2 = item.getCreativeTabs();
            for (final CreativeTabs creativeTab : creativeTabs2) {
                if (creativeTab != null) {
                    item.getSubItems(creativeTab, (NonNullList)nonNullList);
                }
            }
            item.getSubItems(CreativeTabs.SEARCH, (NonNullList)nonNullList);
            for (final ItemStack stack : nonNullList) {
                OneBlockRandom.allItems.add(stack.copy());
            }
        }
    }
    
    @SubscribeEvent
    public static void breakBlock(final BlockEvent.BreakEvent e) {
        final BlockPos pos = e.getPos();
        final ModSavedData modSavedData = ModSavedData.getDefaultInstance((WorldServer)e.getWorld());
        if (modSavedData.isDirtPos(pos)) {
            e.setCanceled(true);
            InventoryHelper.spawnItemStack(e.getWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), OneBlockRandom.allItems.get(OneBlockRandom.rand.nextInt(OneBlockRandom.allItems.size())).copy());
        }
    }
    
    @SubscribeEvent
    public static void respawn(final PlayerEvent.PlayerRespawnEvent e) {
        final EntityPlayer player = e.player;
        if (player.getEntityWorld().getWorldInfo().getTerrainType() instanceof WorldTypeVoid) {
            final ModSavedData modSavedData = ModSavedData.getDefaultInstance((WorldServer)player.world);
            if (player.getBedLocation() == null || EntityPlayer.getBedSpawnLocation(player.getEntityWorld(), player.getBedLocation(), true) == null) {
                final BlockPos pos = modSavedData.getPosForPlayer(player.getGameProfile().getId());
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 2.6, pos.getZ() + 0.5);
            }
        }
    }
    
    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent e) {
        final EntityPlayerMP player = (EntityPlayerMP)e.player;
        final ModSavedData modSavedData = ModSavedData.getDefaultInstance((WorldServer)e.player.world);
        if (!modSavedData.hasPlayer(e.player.getUniqueID())) {
            final UUID uuid = player.getGameProfile().getId();
            final BlockPos pos = (modSavedData.getPlayerCount() == 0) ? new BlockPos(0, 64, 0) : getRandomPointFrom(modSavedData.getLast(), 32);
            e.player.setSpawnPoint(pos, true);
            e.player.world.setBlockState(pos, Blocks.DIRT.getDefaultState());
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
        allItems = new ArrayList<ItemStack>();
        rand = new Random();
    }
}
