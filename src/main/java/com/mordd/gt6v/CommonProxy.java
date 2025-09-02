package com.mordd.gt6v;

import cpw.mods.fml.common.event.FMLInterModComms;

public class CommonProxy {
	public void init() {
		FMLInterModComms.sendMessage("Waila", "register", "com.mordd.gt6v.waila.GT6WailaViewer.callbackRegister"); 
	}
}
