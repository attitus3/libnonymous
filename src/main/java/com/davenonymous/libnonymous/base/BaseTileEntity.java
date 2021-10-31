package com.davenonymous.libnonymous.base;


import com.davenonymous.libnonymous.serialization.nbt.NBTFieldSerializationData;
import com.davenonymous.libnonymous.serialization.Store;
import com.davenonymous.libnonymous.serialization.nbt.NBTFieldUtils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BaseTileEntity extends TileEntity implements ITickableTileEntity {
    private boolean initialized = false;

    @Store(storeWithItem = true, sendInUpdatePackage = true)
    protected String customName;

    @Store(storeWithItem = true, sendInUpdatePackage = true)
    protected UUID owner;

    @Store(sendInUpdatePackage = true)
    private int incomingRedstonePower = 0;

    private List<NBTFieldSerializationData> NBTActions;

    public BaseTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);

        this.NBTActions = NBTFieldUtils.initSerializableStoreFields(this.getClass());
    }

    public void loadFromItem(ItemStack stack) {
        if(!stack.hasTag()) {
            return;
        }

        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, stack.getTag(), data -> data.storeWithItem);
        this.setChanged();
    }

    public void saveToItem(ItemStack stack) {
        CompoundNBT compound = createItemStackTagCompound();
        stack.setTag(compound);
    }

    protected CompoundNBT createItemStackTagCompound() {
        return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, new CompoundNBT(), data -> data.storeWithItem);
    }

    public void notifyClients() {
    	level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, super.getUpdateTag(), data -> data.sendInUpdatePackage);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, pkt.getTag(), data -> data.sendInUpdatePackage);

        /*
        // TODO: This should not be generalized in this way as it triggers on changes to blocks not belonging to this gui.
        if(world.isRemote && Minecraft.getMinecraft().currentScreen instanceof WidgetContainerScreen) {
            ((WidgetContainerScreen) Minecraft.getMinecraft().currentScreen).fireDataUpdateEvent();
        }
        */
    }

    public void read(BlockState state, CompoundNBT compound) {
        super.load(state, compound);

        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, compound, data -> true);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound = super.save(compound);
        compound = NBTFieldUtils.writeFieldsToNBT(NBTActions, this, compound, data -> true);

        return compound;
    }

    @Override
    public void tick() {
        if (!this.getLevel().isClientSide && !this.initialized) {
            initialize();
            this.initialized = true;
        }
    }

    protected void initialize() {
    }

    protected void spawnItem(ItemStack stack) {
        ItemEntity entityItem = new ItemEntity(level, getBlockPos().getX()+0.5f, getBlockPos().getY()+0.7f, getBlockPos().getZ()+0.5f, stack);
        entityItem.lifespan = 1200;
        entityItem.setPickUpDelay(5);

        entityItem.setDeltaMovement(0.0f, 0.10f, 0.0f);

        level.addFreshEntity(entityItem);
    }


    /**
     * Called when the block stops receiving a redstone signal.
     */
    public void redstonePulse() {

    }

    public void redstoneChanged(int previous, int now) {

    }


    public int getRedstonePowerFromNeighbors() {
        return this.level.getBestNeighborSignal(this.worldPosition);
    }

    public int getIncomingRedstonePower() {
        return incomingRedstonePower;
    }

    public BaseTileEntity setIncomingRedstonePower(int incomingRedstonePower) {
        this.incomingRedstonePower = incomingRedstonePower;
        return this;
    }

    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return level.getServer().getProfileCache().get(getOwner()).getName();
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setOwner(PlayerEntity player) {
        if(player == null) {
            return;
        }

        setOwner(player.getUUID());
    }

    public boolean isWaterlogged() {
        if(!this.level.getBlockState(this.getBlockPos()).hasProperty(BlockStateProperties.WATERLOGGED)) {
            return false;
        }

        return this.level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.WATERLOGGED);
    }
}
