// 
// Decompiled by Procyon v0.5.36
// 

package com.tfar.oneblockrandom;

import java.util.Iterator;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.WorldServer;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;
import java.util.Map;
import net.minecraft.world.storage.WorldSavedData;

public class ModSavedData extends WorldSavedData
{
    private final Map<UUID, BlockPos> allPlayers;
    private BlockPos last;
    
    public ModSavedData(final String name) {
        super(name);
        this.allPlayers = new HashMap<UUID, BlockPos>();
    }
    
    public static ModSavedData getDefaultInstance(final WorldServer serverWorld) {
        return getInstance(serverWorld.getMinecraftServer().getWorld(0));
    }
    
    public static ModSavedData getInstance(final WorldServer serverWorld) {
        final MapStorage storage = serverWorld.getPerWorldStorage();
        final String name = "oneblockrandom:" + serverWorld.provider.getDimension();
        ModSavedData instance = (ModSavedData)storage.getOrLoadData((Class)ModSavedData.class, name);
        if (instance == null) {
            final ModSavedData wsd = new ModSavedData(name);
            storage.setData(name, (WorldSavedData)wsd);
            instance = (ModSavedData)storage.getOrLoadData((Class)ModSavedData.class, name);
        }
        return instance;
    }
    
    public int getPlayerCount() {
        return this.allPlayers.size();
    }
    
    public boolean hasPlayer(final UUID uuid) {
        return this.allPlayers.containsKey(uuid);
    }
    
    public void addDirtPos(final UUID uuid, final BlockPos pos) {
        this.allPlayers.put(uuid, pos);
        this.last = pos;
    }
    
    public BlockPos getLast() {
        return this.last;
    }
    
    public boolean isDirtPos(final BlockPos pos) {
        return this.allPlayers.containsValue(pos);
    }
    
    public BlockPos getPosForPlayer(final UUID uuid) {
        return this.allPlayers.get(uuid);
    }
    
    public void removePlayer(final UUID uuid) {
        this.allPlayers.remove(uuid);
        this.markDirty();
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        final NBTTagCompound lastCompound = nbt.getCompoundTag("last");
        this.last = new BlockPos(lastCompound.getInteger("x"), lastCompound.getInteger("y"), lastCompound.getInteger("z"));
        final NBTTagList listNBT = nbt.getTagList("players", 10);
        for (final NBTBase nbtBase : listNBT) {
            final NBTTagCompound compound = (NBTTagCompound)nbtBase;
            final UUID uuid = compound.getUniqueId("id");
            final BlockPos pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
            this.allPlayers.put(uuid, pos);
        }
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
        final NBTTagList listNBT = new NBTTagList();
        final NBTTagCompound lastCompound = new NBTTagCompound();
        lastCompound.setInteger("x", this.last.getX());
        lastCompound.setInteger("y", this.last.getY());
        lastCompound.setInteger("z", this.last.getZ());
        compound.setTag("last", (NBTBase)lastCompound);
        for (final Map.Entry<UUID, BlockPos> entry : this.allPlayers.entrySet()) {
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setUniqueId("id", (UUID)entry.getKey());
            final BlockPos pos1 = entry.getValue();
            nbtTagCompound.setInteger("x", pos1.getX());
            nbtTagCompound.setInteger("y", pos1.getY());
            nbtTagCompound.setInteger("z", pos1.getZ());
            listNBT.appendTag((NBTBase)nbtTagCompound);
        }
        compound.setTag("players", (NBTBase)listNBT);
        return compound;
    }
}
