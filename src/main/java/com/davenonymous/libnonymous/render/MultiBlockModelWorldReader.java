package com.davenonymous.libnonymous.render;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nullable;

public class MultiBlockModelWorldReader implements IBlockReader, BiomeManager.IBiomeReader, IBlockDisplayReader {
    private MultiblockBlockModel model;

    private IWorldReader blockWorld;
    private BlockPos blockPos;

    public MultiBlockModelWorldReader(MultiblockBlockModel model) {
        this.model = model;
    }

    public MultiBlockModelWorldReader(MultiblockBlockModel model, IWorldReader blockWorld, BlockPos blockPos) {
        this.model = model;
        this.blockWorld = blockWorld;
        this.blockPos = blockPos;
    }

    public Biome getBiome(BlockPos pos) {
        return /* blockWorld == null ? Biomes.FOREST : */blockWorld.getBiome(blockPos);
    }


    public IWorldReader getContextWorld() {
        return blockWorld;
    }

    public BlockPos getContextPos() {
        return blockPos;
    }

    @Override
    public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
        return 0;
    }

    @Override
    public WorldLightManager getLightEngine() {
        // TODO: blockworld might be null, what lightmanager do we use then?
        return blockWorld.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        return 0;
    }

    @Override
    public int getBrightness(LightType type, BlockPos pos) {
        return blockWorld == null ? type.surrounding : blockWorld.getBrightness(type, blockPos);
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if(model.blocks.get(pos) != null) {
            return model.blocks.get(pos);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        // TODO: ???
        return null;
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return null;
    }
}
