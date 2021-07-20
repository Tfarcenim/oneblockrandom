// 
// Decompiled by Procyon v0.5.36
// 

package tfar.oneblockrandom;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModSavedData extends WorldSavedData
{
    private final Map<UUID, BlockPos> allPlayers;
    private BlockPos last;
    
    public ModSavedData(final String name) {
        super(name);
        this.allPlayers = new HashMap<>();
    }
    
    public static ModSavedData getDefaultInstance(final ServerWorld serverWorld) {
        return getInstance(serverWorld.getServer().getWorld(World.OVERWORLD));
    }
    
    public static ModSavedData getInstance(final ServerWorld serverWorld) {
        final DimensionSavedDataManager storage = serverWorld.getSavedData();
        final String name = "oneblockrandom:" + serverWorld.getDimensionKey().getLocation();
        return storage.getOrCreate(() -> new ModSavedData(name), name);
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
    
    public void read(final CompoundNBT nbt) {
        final CompoundNBT lastCompound = nbt.getCompound("last");
        this.last = new BlockPos(lastCompound.getInt("x"), lastCompound.getInt("y"), lastCompound.getInt("z"));
        final ListNBT listNBT = nbt.getList("players", 10);
        for (final INBT nbtBase : listNBT) {
            final CompoundNBT compound = (CompoundNBT)nbtBase;
            final UUID uuid = compound.getUniqueId("id");
            final BlockPos pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
            this.allPlayers.put(uuid, pos);
        }
    }
    
    public CompoundNBT write(final CompoundNBT compound) {
        final ListNBT listNBT = new ListNBT();
        final CompoundNBT lastCompound = new CompoundNBT();
        lastCompound.putInt("x", this.last.getX());
        lastCompound.putInt("y", this.last.getY());
        lastCompound.putInt("z", this.last.getZ());
        compound.put("last", lastCompound);
        for (final Map.Entry<UUID, BlockPos> entry : this.allPlayers.entrySet()) {
            final CompoundNBT nbtTagCompound = new CompoundNBT();
            nbtTagCompound.putUniqueId("id", entry.getKey());
            final BlockPos pos1 = entry.getValue();
            nbtTagCompound.putInt("x", pos1.getX());
            nbtTagCompound.putInt("y", pos1.getY());
            nbtTagCompound.putInt("z", pos1.getZ());
            listNBT.add(nbtTagCompound);
        }
        compound.put("players", listNBT);
        return compound;
    }
}
