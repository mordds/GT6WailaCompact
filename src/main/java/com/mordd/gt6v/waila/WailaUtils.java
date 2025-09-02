package com.mordd.gt6v.waila;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mordd.gt6v.GT6Viewer;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class WailaUtils {
	public static Map<String,Integer> fluidColorBuffer = new HashMap<String,Integer>();
	
	public static String getStackListRenderString(String tooltip,int rows,List<ItemStack> stacks) {
		StringBuilder builder = new StringBuilder();
		builder.append(SpecialChars.RENDER);
		builder.append("{gt6v.stacklist,");
		builder.append(tooltip);
		builder.append(",");
		builder.append(rows);
		builder.append(",");
		for(ItemStack stack : stacks) {
			builder.append(encodeStack(stack));
		}
		builder.append("}");
		return builder.toString();
	}
	public static String getStackRenderString(String tooltip,int rows,ItemStack stack) {
		StringBuilder builder = new StringBuilder();
		builder.append(SpecialChars.RENDER);
		builder.append("{gt6v.stack,");
		builder.append(stack.getItem() instanceof ItemBlock ? 1 : 0);
		builder.append(",");
		builder.append(getRegistryName(stack));
		builder.append(",");
		builder.append(stack.stackSize);
		builder.append(",");
		builder.append(stack.getItemDamage());
		builder.append("}");
		return builder.toString();
	}
	
	public static String getTankBarRenderString(FluidStack stack,int capacity) {
		StringBuilder builder = new StringBuilder();
		builder.append(SpecialChars.RENDER);
		builder.append("{gt6v.tankbar,");
		if(capacity < 1) capacity = 1;
		if(stack != null) {
			builder.append(stack.amount);
			builder.append(",");
			builder.append(stack.getFluid().getName());
			
			
		}
		else {
			builder.append(0);
			builder.append(",");
			builder.append("null");
		}
		builder.append(",");
		builder.append(capacity);
		builder.append("}");
		return builder.toString();
	}
	public static String getSmallTankBarRenderString(FluidStack stack,int capacity) {
		StringBuilder builder = new StringBuilder();
		builder.append(SpecialChars.RENDER);
		builder.append("{gt6v.tankbar_small,");
		if(capacity < 1) capacity = 1;
		if(stack != null) {
			builder.append(stack.amount);
			builder.append(",");
			builder.append(stack.getFluid().getName());
			
			
		}
		else {
			builder.append(0);
			builder.append(",");
			builder.append("null");
		}
		builder.append(",");
		builder.append(capacity);
		builder.append("}");
		return builder.toString();
	}
	
	public static String encodeStack(ItemStack stack) {
		if(stack == null) return null;
		StringBuilder builder = new StringBuilder();
		boolean isBlock = stack.getItem() instanceof ItemBlock;
		builder.append(getRegistryName(stack));
		builder.append("@");
		builder.append(stack.getItemDamage());
		builder.append("@");
		builder.append(stack.stackSize);
		builder.append("@");
		builder.append(isBlock ? 1 : 0);
		return builder.toString();
	}
	public static ItemStack decodeStack(String s) {
		String[] params = s.split("@");
		if(params.length < 4) return null;
		try {
			int damage = Integer.parseInt(params[1]);
			int count = Integer.parseInt(params[2]);
			int mode = Integer.parseInt(params[3]);
			ItemStack stack = null;
	        if (mode == 0) {
	            stack = new ItemStack((Item) Item.itemRegistry.getObject(params[0]), count,damage);
	        }
	        if (mode == 1) {
	            stack = new ItemStack((Block) Block.blockRegistry.getObject(params[0]), count, damage);
	        } 
	        return stack;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String getRegistryName(ItemStack stack) {
		boolean isBlock = stack.getItem() instanceof ItemBlock;
		UniqueIdentifier ui = isBlock ? GameRegistry.findUniqueIdentifierFor(Block.getBlockFromItem((ItemBlock)stack.getItem())) : GameRegistry.findUniqueIdentifierFor(stack.getItem());
		return ui.toString();
	}
	
	public static Integer getFluidColorByIcon(TextureAtlasSprite sprite,Fluid fluid) {
		if(fluidColorBuffer.containsKey(fluid.getName())) return fluidColorBuffer.get(fluid.getName());
		if(sprite.getFrameCount() == 0) return null;
		int[][] data = sprite.getFrameTextureData(0);
		int rTot = 0;
		int gTot = 0;
		int bTot = 0;
		int cnt = 0;
		for(int i = 0;i < Math.min(data.length,4);i++) {
			for(int j = 0;j < Math.min(data[i].length,16);j++) {
				rTot += data[i][j] & 0xFF;
				gTot += ((data[i][j] & 0xFF00) >> 8);
				bTot += ((data[i][j] & 0xFF0000) >> 16);

				cnt++;
			}
		}
		rTot /= cnt;
		gTot /= cnt;
		bTot /= cnt;

		int color = rTot + (gTot << 8) + (bTot << 16);
		fluidColorBuffer.put(fluid.getName(), color);
		return color;
		
	}
	public static double getRelativeLight(int r,int g,int b) {
		double rf = r / 255.0;
		double gf = g / 255.0;
		double bf = b / 255.0;
		rf = rf < 0.03928 ? rf / 12.92 : Math.pow((rf + 0.055) / 1.055,2.2);
		gf = gf < 0.03928 ? gf / 12.92 : Math.pow((gf + 0.055) / 1.055,2.2);
		bf = bf < 0.03928 ? bf / 12.92 : Math.pow((bf + 0.055) / 1.055,2.2);
		double absLight = 0.2126 * rf + 0.7152 * gf + 0.0722 * bf;
		return 1.05 /( absLight + 0.05);
		
	}
	
}
