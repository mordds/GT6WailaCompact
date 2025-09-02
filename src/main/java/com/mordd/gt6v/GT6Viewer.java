package com.mordd.gt6v;

import org.apache.logging.log4j.Logger;

import com.mordd.gt6v.waila.WailaData;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregapi.worldgen.StoneLayer;
import net.minecraft.item.ItemStack;


@Mod(modid=GT6Viewer.modid,name="GT6 Waila Compact",version="0.5.0",acceptableRemoteVersions = "*",dependencies="required-after:gregtech;required-after:gregapi_post;required-after:gregapi")
public class GT6Viewer {
	public static final String version = "0.5.0";
	public static final String modid = "gt6viewer";
	@Mod.Instance(GT6Viewer.modid)
	public static GT6Viewer instance;
	public static Logger logger;
	
	
	@SidedProxy(serverSide = "com.mordd.gt6v.CommonProxy", clientSide = "com.mordd.gt6v.ClientProxy")
	public static CommonProxy myProxy;
	
	public GT6Viewer() {
		instance = this;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		
	}
	@EventHandler
	public void init(FMLInitializationEvent e) {
		myProxy.init();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		WailaData.init();
	}
	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {
		//logger.fatal(StoneLayer.LAYERS.size());
	}
}
