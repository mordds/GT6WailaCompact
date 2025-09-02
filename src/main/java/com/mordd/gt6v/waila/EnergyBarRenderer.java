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
import net.minecraft.client.renderer.texture.TextureMap;


public class EnergyBarRenderer implements IWailaTooltipRenderer {

	RenderItem item = new RenderItem();
    Minecraft mc = Minecraft.getMinecraft();
    ResourceLocation texture = new ResourceLocation("gt6viewer", "textures/sprites.png");
    static int r = 259;
    @Override // mcp.mobius.waila.api.IWailaTooltipRenderer
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(130, 10);
    }

    @Override // mcp.mobius.waila.api.IWailaTooltipRenderer
    public void draw(String[] params, IWailaCommonAccessor accessor) {
    	int off = params[0].equals("a") ? 72 : 96;
        int currentValue = Integer.valueOf(params[1]).intValue(); 
        int maxValue = Integer.valueOf(params[2]).intValue();
        int progress = (currentValue * 126) / maxValue;
        this.mc.getTextureManager().bindTexture(this.texture);
        DisplayUtil.drawTexturedModalRect(0, 0, 0, 48, 129, 9, 129, 9);
        if(progress != 0) DisplayUtil.drawTexturedModalRect(0, 0, 0, off, progress+1, 10, progress+1, 10);
        DisplayUtil.drawString(String.format("%.2f%%", currentValue * 10000L / maxValue / 100.0f), 4, 0, (progress > 25 && off == 72) ? 0x0 : 0xFFFFFF, false);

        
    }


}
