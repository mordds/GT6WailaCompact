package com.mordd.gt6v.waila;

import java.awt.Dimension;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TooltipStackRenderer implements IWailaTooltipRenderer {

	@Override
	public Dimension getSize(String[] params, IWailaCommonAccessor arg1) {
			int tw = Minecraft.getMinecraft().fontRenderer.getStringWidth(params[4]);
			return new Dimension(tw + 20,16);
	}

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
    	FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    	int tw = fontRenderer.getStringWidth(params[4]);
        int type = Integer.valueOf(params[0]).intValue();
        String name = params[1];
        int amount = Integer.valueOf(params[2]).intValue();
        int meta = Integer.valueOf(params[3]).intValue();
        ItemStack stack = null;
        if (type == 0) {
            stack = new ItemStack((Block) Block.blockRegistry.getObject(name), amount, meta);
        }
        if (type == 1) {
            stack = new ItemStack((Item) Item.itemRegistry.getObject(name), amount, meta);
        }
        RenderHelper.enableGUIStandardItemLighting();
        DisplayUtil.renderStack(tw + 4, 0, stack);
        RenderHelper.disableStandardItemLighting();
        fontRenderer.drawString(params[4], 0, 4, 0xFFFFFF,false);
    }


}
