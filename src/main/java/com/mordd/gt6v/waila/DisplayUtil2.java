package com.mordd.gt6v.waila;

import org.lwjgl.opengl.GL11;

import com.mordd.gt6v.GT6Viewer;

import gregapi.fluid.FluidGT;
import gregapi.util.UT;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class DisplayUtil2 {
    public static void drawTexturedModelFluid(int x, int y, Fluid f, int w, int h)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);

        IIcon icon = f.getIcon();
        
    	if(f instanceof FluidGT) {
    		short[] aRGBa = UT.Code.getRGBaArray(((FluidGT)f).getColor());
    		//GT6Viewer.logger.fatal(aRGBa[0]+" "+aRGBa[1]+" "+aRGBa[2]);
    		Tessellator.instance.setColorRGBA((int)(UT.Code.bind8(aRGBa[0])), (int)(UT.Code.bind8(aRGBa[1])), (int)(UT.Code.bind8(aRGBa[2])), 255);
    	}
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + h), 1.0f, (double)icon.getMinU(), (double)icon.getMaxV());
        tessellator.addVertexWithUV((double)(x + w), (double)(y + h), 1.0f, (double)icon.getMaxU(), (double)icon.getMaxV());
        tessellator.addVertexWithUV((double)(x + w), (double)(y + 0), 1.0f, (double)icon.getMaxU(), (double)icon.getMinV());
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), 1.0f, (double)icon.getMinU(), (double)icon.getMinV());
        tessellator.draw();
    }
    public static void draw2ColorText(String str,int x,int y,boolean shadow,int colorA,int colorB,int cutPixel) {
    	if(colorA == colorB) {
    		DisplayUtil.drawString(str,x,y,colorA,shadow);
    	 	return;
    	}
    	FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    	int width = fontRenderer.getStringWidth(str);
    	if(width < cutPixel) {
    		DisplayUtil.drawString(str,x,y,colorA,shadow);
    	 	return;
    	}
    	else if(cutPixel * 20 < width) {
    		DisplayUtil.drawString(str,x,y,colorB,shadow);
    	 	return;
    	}

    	int estimateLen = (int) Math.round(cutPixel * 1.0 / width * str.length());
    	String builder = str.substring(0,estimateLen);
    	int width2 = fontRenderer.getStringWidth(builder);
    	if(width2 < cutPixel) {
    		while(width2 < cutPixel) {
    			int charWidth = fontRenderer.getCharWidth(str.charAt(estimateLen));
        		width2 += charWidth;
        		estimateLen++;
 
    		}
    		estimateLen--;
    	}
    	else while(width2 > cutPixel) {
    		int charWidth = fontRenderer.getCharWidth(str.charAt(estimateLen - 1));
    		width2 -= charWidth;
    		estimateLen--;
    	}
    	String prev = str.substring(0,estimateLen);
    	String next = str.substring(estimateLen);
    	DisplayUtil.drawString(prev,x,y,colorA,shadow);
    	DisplayUtil.drawString(next,x + fontRenderer.getStringWidth(prev),y,colorB,shadow);
    }

}
