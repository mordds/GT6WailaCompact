package com.mordd.gt6v.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;


import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;


public class CoreContainer extends DummyModContainer{
	public static final String coreModVersion="0.1.0"; 
	public CoreContainer() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "gt6viewer_coremod";
		meta.name = "GT6 Waila Compact CoreMod";
		meta.version = coreModVersion;
		meta.authorList = Arrays.asList(new String[] {"mordd"});
		meta.description = "A CoreMod of GT6 Waila Compact";
		
	}
	@Override
	public boolean registerBus(EventBus bus,LoadController controller) {
		bus.register(this);
		return true;
	}
}
