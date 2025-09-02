package com.mordd.gt6v.waila;

import java.awt.Dimension;

import com.mordd.gt6v.GT6Viewer;

import gregapi.fluid.FluidGT;
import gregapi.util.UT;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;


public class TankBarRenderer implements IWailaTooltipRenderer {
	int cnt = 100;
	int cnt2 = 100;
	RenderItem item = new RenderItem();
    Minecraft mc = Minecraft.getMinecraft();
    ResourceLocation texture = new ResourceLocation("gt6viewer", "textures/sprites.png");
    @Override // mcp.mobius.waila.api.IWailaTooltipRenderer
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(130, 18);
    }

    @Override // mcp.mobius.waila.api.IWailaTooltipRenderer
    public void draw(String[] params, IWailaCommonAccessor accessor) {

        int currentValue = Integer.valueOf(params[0]).intValue();
        Fluid f = null;
        if(currentValue != 0) f = FluidRegistry.getFluid(params[1]);
        int maxValue = Integer.valueOf(params[2]).intValue();

        int progress = (currentValue * 126) / maxValue;
        if(progress > 126) progress = 126;
        int Textcolor = 0xFFFFFF;
        this.mc.getTextureManager().bindTexture(this.texture);
        DisplayUtil.drawTexturedModalRect(0, 0, 0, 0, 129, 17, 129, 17);
        if(f != null) {
        	if(f instanceof FluidGT) {
        		short[] aRGBa = UT.Code.getRGBaArray(((FluidGT)f).getColor());
        		if(WailaUtils.getRelativeLight(aRGBa[0], aRGBa[1], aRGBa[2]) < 2.15) {
        			Textcolor = 0;
        		}
        		
        	}
        	if(f.getIcon() != null && f.getIcon() instanceof TextureAtlasSprite) {
        		Integer color = WailaUtils.getFluidColorByIcon((TextureAtlasSprite)f.getIcon(), f);
        		if(color != null) {
        			int r = color & 0xFF;
            		int g = ((color & 0xFF00) >> 8);
            		int b = ((color & 0xFF0000) >> 16);
            		//TextureBlack
            		if(WailaUtils.getRelativeLight(r,g,b) < 2.15) {
            			if(!(f instanceof FluidGT)) Textcolor = 0;
            			
            		}
            		else Textcolor = 0xFFFFFF;
        		} 
        	}
        	this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        	DisplayUtil2.drawTexturedModelFluid(1, 1, f, progress+1, 15);
        } 
        
        DisplayUtil2.draw2ColorText(String.format("%d / %d L %s", currentValue,maxValue,f == null?"":I18n.format(f.getUnlocalizedName())), 4, 6, false,Textcolor,0xFFFFFF,progress+1);
        this.mc.getTextureManager().bindTexture(this.texture);
        DisplayUtil.drawTexturedModalRect(0, 0, 0, 17, 129, 10, 129, 10);
        
    }


}
