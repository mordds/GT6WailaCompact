package com.mordd.gt6v.waila;

import java.awt.Dimension;
import java.awt.Point;

import com.mordd.gt6v.GT6Viewer;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackListRenderer implements IWailaTooltipRenderer {
	int cnt = 0;
	private Point getStackLocation(int index,int maxColumn,int maxCount,int tw) {
		if(maxCount < maxColumn) return new Point(index * 18 + tw,0);
		else if(maxCount < maxColumn * maxColumn) {
			int x = index % maxColumn;
			int y = index / maxColumn;
			return new Point(x * 18 + tw,y * 18);
		}
		else {
			int w = maxCount / maxColumn;
			int x = index % w;
			int y = index / w;
			return new Point(x * 18 + tw,y * 18);
		}
	}
	
	@Override
	public void draw(String[] arg0, IWailaCommonAccessor arg1) {
		if(arg0.length < 3) return;
		Dimension size = this.getSize(arg0, arg1);
		int height = (int) size.getHeight();
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int tw = fontRenderer.getStringWidth(arg0[0]);
		int maxColumn = Integer.parseInt(arg0[1]);
		int sLen = arg0.length - 2;
		RenderHelper.enableGUIStandardItemLighting();
		for(int i = 2;i < arg0.length;i++) {
			Point p = getStackLocation(i - 2,maxColumn,sLen,tw);
			DisplayUtil.renderStack(p.x, p.y, WailaUtils.decodeStack(arg0[i]));
		}
		RenderHelper.disableStandardItemLighting();
		fontRenderer.drawString(arg0[0], 0, (height - 8) / 2, 0xFFFFFF,false);
	}

	@Override
	public Dimension getSize(String[] arg0, IWailaCommonAccessor arg1) {
		if(arg0.length < 3) return new Dimension(1,1);
		int tw = Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0[0]);
		int maxColumn = Integer.parseInt(arg0[1]);
		int stacks = arg0.length - 2;
		
		if(stacks < maxColumn) {
			int height = 18;
			int width = stacks * 18 + tw;
			return new Dimension(width,height);
		}
		else if(stacks < maxColumn * maxColumn) {
			int height = stacks / maxColumn;
			if(stacks % maxColumn != 0) height++;
			height *= 18;
			int width = maxColumn * 18 + tw;
			return new Dimension(width,height);
		}
		else {
			int height = maxColumn * 18;
			int width = stacks / maxColumn;
			if(stacks % maxColumn != 0) width++;
			width *= 18;
			width += tw;
			return new Dimension(width,height);
		}
		
	}

}
