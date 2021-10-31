package com.davenonymous.libnonymous.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/*
This class is basically taken as is from
https://github.com/Darkhax-Minecraft/Bookshelf/blob/1.16.5/src/main/java/net/darkhax/bookshelf/crafting/RecipeDataBase.java

Thanks @Darkhax
 */
public abstract class RecipeDataBase implements IRecipe<IInventory> {
    
    private final ResourceLocation identifier;
    
    public RecipeDataBase(ResourceLocation identifier) {
        
        this.identifier = identifier;
        
        if (this.getSerializer() == null) {
            
            throw new IllegalStateException("No serializer found for " + this.getClass().getName());
        }
        
        if (this.getType() == null) {
            
            throw new IllegalStateException("No recipe type found for " + this.getClass().getName());
        }
    }
    
    @Override
    public ResourceLocation getId () {
        
        return this.identifier;
    }
    
    @Override
    public boolean matches (IInventory inv, World worldIn) {
        
        // Not used
        return false;
    }
    
    @Override
    public ItemStack assemble (IInventory inv) {
        
        // Not used
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canCraftInDimensions (int width, int height) {
        
        // Not used
        return false;
    }
    
    @Override
    public ItemStack getResultItem () {
        
        // Not used
        return ItemStack.EMPTY;
    }
}