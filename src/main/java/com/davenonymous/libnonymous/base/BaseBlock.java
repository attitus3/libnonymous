package com.davenonymous.libnonymous.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nullable;

public class BaseBlock extends Block {
    public BaseBlock(Properties properties) {
        super(properties);
    }

    public void renderEffectOnHeldItem(PlayerEntity player, Hand mainHand, float partialTicks, MatrixStack matrix) {

    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);

        if(world.isClientSide()) {
            return;
        }

        TileEntity tileEntity = world.getBlockEntity(pos);
        if(!(tileEntity instanceof BaseTileEntity)) {
            return;
        }

        BaseTileEntity base = (BaseTileEntity) tileEntity;
        int previous = base.getIncomingRedstonePower();
        int now = base.getRedstonePowerFromNeighbors();

        if(now == 0) {
            if(previous > 0) {
                base.redstonePulse();
            }
        } else {
            if(previous != now) {
                base.redstoneChanged(previous, now);
            }
        }

        base.setIncomingRedstonePower(now);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if(!(world.getBlockEntity(pos) instanceof BaseTileEntity)) {
            return;
        }

        BaseTileEntity baseTile = (BaseTileEntity) world.getBlockEntity(pos);
        baseTile.loadFromItem(stack);
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity, boolean horizontalOnly) {
        Direction result =   Direction.getNearest((float) (entity.getX() - clickedBlock.getX()), (float) (entity.getY() - clickedBlock.getY()), (float) (entity.getZ() - clickedBlock.getZ()));
        if(horizontalOnly && (result == Direction.UP || result == Direction.DOWN)) {
            return Direction.NORTH;
        }
        return result;
    }
}
