package com.mordd.gt6v.waila;

import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import gregtech.tileentity.energy.converters.MultiTileEntityBoilerTank;
import gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class WailaData {
	public static Field BOILER_TIMER = null;
	public static Field TURBINE_OUTPUT = null;
	


	public static void init() {
		
		try {
			BOILER_TIMER = MultiTileEntityBoilerTank.class.getDeclaredField("mCoolDownResetTimer");
			BOILER_TIMER.setAccessible(true);
			TURBINE_OUTPUT = MultiTileEntityTurbineSteam.class.getField("mGenerated");
		} catch (Exception e) {
			
		} 
		assert BOILER_TIMER != null;
	}
			
}
