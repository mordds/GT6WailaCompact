package com.mordd.gt6v.waila;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

public class NBTUtils {
	public static final int COMPOUND_ID = 10;
	public static final int LIST_ID = 9;
	public static FluidStack getFluidStackFromKey(NBTTagCompound nbt,String key) {
		if(!nbt.hasKey(key,COMPOUND_ID)) return null;
		NBTTagCompound fluidTag = nbt.getCompoundTag(key);
		FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);
		return stack;
	}
	public static SlotItemStack[] getInventoryFromKey(NBTTagCompound nbt,String key) {
		NBTBase base = nbt.getTag(key);
		if(!(base instanceof NBTTagList)) return null;
		NBTTagList list = (NBTTagList)base;
		SlotItemStack[] stacks = new SlotItemStack[list.tagCount()];
		for(int i = 0;i < list.tagCount();i++) {
			ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
			int s = list.getCompoundTagAt(i).getInteger("s");
			stacks[i] = new SlotItemStack(stack,s);
		}
		return stacks;
	}
	public static ItemStack getStackFromKey(NBTTagCompound nbt,String key) {
		if(!nbt.hasKey(key,COMPOUND_ID)) return null;
		NBTTagCompound itemTag = nbt.getCompoundTag(key);
		ItemStack stack = ItemStack.loadItemStackFromNBT(itemTag);
		return stack;
	}

}
