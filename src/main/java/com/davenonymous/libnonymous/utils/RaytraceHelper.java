package com.davenonymous.libnonymous.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RaytraceHelper {
    public static BlockRayTraceResult rayTrace(World world, Entity entity) {
        return rayTrace(world, entity, 16.0f);
    }

    public static BlockRayTraceResult rayTrace(World world, Entity entity, double blockReachDistance) {
        return rayTrace(world, entity, blockReachDistance, 0.0f);
    }

    @Nullable
    public static BlockRayTraceResult rayTrace(World world, Entity entity, double blockReachDistance, float partialTicks) {
        Vec3d vec3d = entity.getEyePosition(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }
}
