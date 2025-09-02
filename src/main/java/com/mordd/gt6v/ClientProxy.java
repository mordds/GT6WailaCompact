package com.mordd.gt6v;

import cpw.mods.fml.common.event.FMLInterModComms;

public class ClientProxy extends CommonProxy {
	public void init() {
		super.init();
		FMLInterModComms.sendMessage("Waila", "register", "com.mordd.gt6v.waila.GT6WailaViewer.callbackRegisterClient"); 
	}
}
