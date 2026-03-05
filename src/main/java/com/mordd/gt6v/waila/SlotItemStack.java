package com.mordd.gt6v.waila;

import net.minecraft.item.ItemStack;

public class SlotItemStack {
	public ItemStack stack;
	public int slot;
	public SlotItemStack(ItemStack stack,int slot) {
		this.stack = stack;
		this.slot = slot;
	}
}
