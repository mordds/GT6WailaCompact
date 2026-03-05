package com.mordd.gt6v.waila;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.mordd.gt6v.GT6Viewer;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.CS;
import gregapi.data.FM;
import gregapi.data.MT;
import gregapi.fluid.FluidTankGT;
import gregapi.recipes.Recipe;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.connectors.MultiTileEntityAxle;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.connectors.MultiTileEntityWireElectric;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.tank.TileEntityBase08Barrel;
import gregapi.tileentity.tank.TileEntityBase08FluidContainer;
import gregtech.tileentity.energy.converters.MultiTileEntityBoilerTank;
import gregtech.tileentity.energy.converters.MultiTileEntityEngineSteam;
import gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorFluidBed;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorGas;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorLiquid;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorSolid;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorCore;
import gregtech.tileentity.misc.MultiTileEntityFluidSpring;
import gregtech.tileentity.misc.MultiTileEntityRock;
import gregtech.tileentity.multiblocks.MultiTileEntityCrucible;
import gregtech.tileentity.multiblocks.MultiTileEntityTank;
import gregtech.tileentity.plants.MultiTileEntityBush;
import gregtech.tileentity.tanks.MultiTileEntityBarometerGasCylinder;
import gregtech.tileentity.tanks.MultiTileEntityBarrelMetal;
import gregtech.tileentity.tanks.MultiTileEntityCell;
import gregtech.tileentity.tanks.MultiTileEntityJug;
import gregtech.tileentity.tanks.MultiTileEntityMeasuringPot;
import gregtech.tileentity.tools.MultiTileEntityAnvil;
import gregtech.tileentity.tools.MultiTileEntityMixingBowl;
import gregtech.tileentity.tools.MultiTileEntitySiftingTable;
import gregtech.tileentity.tools.MultiTileEntitySmeltery;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static com.mordd.gt6v.waila.WailaData.*;
import static com.mordd.gt6v.waila.WailaUtils.*;

public class GT6WailaViewer implements IWailaDataProvider {
	public static DataAccessorCommon targetAccessor = new DataAccessorCommon();
	private static MethodType bodyMethodType = null;
	private static HashSet<Class> forbidClass = new HashSet<Class>();
	private static HashMap<Class,WailaBodyFunction> bodyMethods = new HashMap<>(64);
	private static MethodHandles.Lookup lookup = null;
	static {
		bodyMethodType = MethodType.methodType(List.class,new Class[] {ItemStack.class,List.class,IWailaDataAccessor.class,IWailaConfigHandler.class}); 
		lookup = MethodHandles.lookup();
		try {
			registerBodyMethod(MultiTileEntitySmeltery.class,GT6WailaViewer::CrucibleBody);
			registerBodyMethod(MultiTileEntityCrucible.class,GT6WailaViewer::LargeCrucibleBody);
			registerBodyMethod(MultiTileEntityBoilerTank.class,GT6WailaViewer::BoilerTankBody);
			registerBodyMethod(MultiTileEntityRock.class,GT6WailaViewer::RockBody);
			registerBodyMethod(TileEntityBase08FluidContainer.class,GT6WailaViewer::SmallFluidContainerBody);
			registerBodyMethod(MultiTileEntityGeneratorSolid.class,GT6WailaViewer::SoildBurningBoxBody);
			registerBodyMethod(MultiTileEntityGeneratorLiquid.class,GT6WailaViewer::LiquidBurningBoxBody);
			registerBodyMethod(MultiTileEntityTank.class,GT6WailaViewer::MultiBlockTankBody);
			registerBodyMethod(MultiTileEntityMultiBlockPart.class,GT6WailaViewer::MultiBlockBody);
			registerBodyMethod(MultiTileEntityAnvil.class,GT6WailaViewer::AnvilBody);
			registerBodyMethod(MultiTileEntityEngineSteam.class,GT6WailaViewer::SteamEngineBody);
			registerBodyMethod(MultiTileEntityTurbineSteam.class,GT6WailaViewer::SteamTurbineBody);
			registerBodyMethod(MultiTileEntityAxle.class,GT6WailaViewer::AxleBody);
			registerBodyMethod(MultiTileEntityWireElectric.class,GT6WailaViewer::ElectricWireBody);
			registerBodyMethod(MultiTileEntityFluidSpring.class,GT6WailaViewer::FluidSpringBody);
			registerBodyMethod(MultiTileEntityMixingBowl.class,GT6WailaViewer::MixingBowlBody);
			registerBodyMethod(TileEntityBase08Barrel.class,GT6WailaViewer::BarrelBody);
			registerBodyMethod(MultiTileEntityPipeFluid.class,GT6WailaViewer::FluidPipeBody);
			registerBodyMethod(MultiTileEntitySiftingTable.class,GT6WailaViewer::SiftingTableBody);
			registerBodyMethod(MultiTileEntityMotorLiquid.class,GT6WailaViewer::LiquidMotorBody);
			registerBodyMethod(MultiTileEntityReactorCore.class,GT6WailaViewer::ReactorBody);
			registerBodyMethod(MultiTileEntityBush.class,GT6WailaViewer::BushBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void registerBodyMethod(Class teClass,WailaBodyFunction func) {
		bodyMethods.put(teClass, func);
	}
	
	
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
	{
		if (te != null)
			te.writeToNBT(tag);
		if(te instanceof MultiTileEntityBoilerTank) {
			try {
				int cd = (Short) BOILER_TIMER.get(te);
				tag.setInteger("gt.cooldown", cd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(te instanceof MultiTileEntityAxle) {
			long mTransferred = ((MultiTileEntityAxle)te).mTransferredLast;
			tag.setLong("gt.transfer.ru",mTransferred);
		}
		else if(te instanceof MultiTileEntityWireElectric) {
			long mTransferred = ((MultiTileEntityWireElectric)te).mWattageLast;
			tag.setLong("gt.transfer.eu",mTransferred);
		}
		else if(te instanceof MultiTileEntityMultiBlockPart) {
			if(tag.hasKey("gt.target")) {
				int x1 = tag.getInteger("gt.target.x");
				int y1 = tag.getInteger("gt.target.y");
				int z1 = tag.getInteger("gt.target.z");
				TileEntity target = world.getTileEntity(x1, y1, z1);
				NBTTagCompound target_nbt = new NBTTagCompound();
				target_nbt.setInteger("WailaX", x1);
				target_nbt.setInteger("WailaY", y1);
				target_nbt.setInteger("WailaZ", z1);
				target.writeToNBT(target_nbt);
				tag.setTag("target", target_nbt);
			}
		}
		return tag;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		TileEntity te = accessor.getTileEntity();
		if(te == null) return currenttip;
		Class teClass = te.getClass();
		Class originClass = teClass;
		if(forbidClass.contains(originClass)) return currenttip;
		WailaBodyFunction handle = bodyMethods.get(teClass);
		while(handle == null && teClass != TileEntity.class) {
			teClass = teClass.getSuperclass();
			handle = bodyMethods.get(teClass);
		}
		if(handle != null) bodyMethods.put(originClass, handle);
		else {
			forbidClass.add(originClass);
			return currenttip;
		}
		try {
			return handle.getBody(itemStack, currenttip, accessor, config);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return currenttip;
		
	}
	
	public static List<String> SoildBurningBoxBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		TileEntity te = accessor.getTileEntity();
		MultiTileEntityGeneratorSolid burningbox = (MultiTileEntityGeneratorSolid)te;
		int energy = 0,output = 0,time1 = 0;
		boolean active = false;
		ItemStack fuelStack = null,ashStack = null;
		if(nbt.hasKey("gt.energy")) energy = nbt.getInteger("gt.energy");
		if(nbt.hasKey("gt.active")) active = true;
		if(nbt.hasKey("gt.invlist")) {
			NBTTagList inv = nbt.getTagList("gt.invlist",10);
			if(inv.tagCount() >= 1) fuelStack = ItemStack.loadItemStackFromNBT(inv.getCompoundTagAt(0));
			if(inv.tagCount() >= 2) ashStack = ItemStack.loadItemStackFromNBT(inv.getCompoundTagAt(1));
			if(ashStack == null) {
				if(FM.Furnace.mRecipeItemMap.get(fuelStack) == null){
					ashStack = fuelStack;
					fuelStack = null;
				}
			}
		}
		if(nbt.hasKey("gt.mte.id")) {
			output = getDefaultNBT(nbt.getInteger("gt.mte.id")).getInteger("gt.output");
		}
		if(output == 0) time1 = energy;
		else time1 = energy / output;
		String invString = "";
		if(fuelStack != null) {
			boolean isBlock = fuelStack.getItem() instanceof ItemBlock;
			int mode = isBlock ? 0 : 1;
			
			invString +=  SpecialChars.RENDER + String.format("{gt6v.stack,%d,%s,%d,%d,%s}",mode,getRegistryName(fuelStack),fuelStack.stackSize,fuelStack.getItemDamage(),I18n.format("gt6v.info.fuel"));
	
		}
		if(ashStack != null) {
			boolean isBlock = ashStack.getItem() instanceof ItemBlock;
			int mode = isBlock ? 0 : 1;
			if(invString.length() != 0) invString += "   ";
			invString += SpecialChars.RENDER + String.format("{gt6v.stack,%d,%s,%d,%d,%s}",mode,getRegistryName(ashStack),ashStack.stackSize,ashStack.getItemDamage(),I18n.format("gt6v.info.ash"));	
		}
		currenttip.add(invString);
		if(active) {
			currenttip.add(I18n.format("gt6v.countdown.burn")+getTimeString(time1));
			currenttip.add(I18n.format("gt6v.out.hu", output));
		}
		return currenttip;
	}
	
	public static List<String> FluidiBedBurningBoxBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		
		TileEntity te = accessor.getTileEntity();
		FluidStack calciteStack = null;
		MultiTileEntityGeneratorFluidBed burningbox = (MultiTileEntityGeneratorFluidBed)te;
		int energy = 0,output = 0,time1 = 0;
		boolean active = false;
		ItemStack fuelStack = null,ashStack = null;
		if(nbt.hasKey("gt.energy")) energy = nbt.getInteger("gt.energy");
		if(nbt.hasKey("gt.active")) active = true;
		if(nbt.hasKey("gt.invlist")) {
			NBTTagList inv = nbt.getTagList("gt.invlist",10);
			if(inv.tagCount() >= 1) fuelStack = ItemStack.loadItemStackFromNBT(inv.getCompoundTagAt(0));
			if(inv.tagCount() >= 2) ashStack = ItemStack.loadItemStackFromNBT(inv.getCompoundTagAt(1));
			if(ashStack == null) {
				if(FM.FluidBed.mRecipeItemMap.get(fuelStack) == null){
					ashStack = fuelStack;
					fuelStack = null;
				}
			}
		}
		if(nbt.hasKey("gt.tank")) calciteStack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank"));
		if(nbt.hasKey("gt.mte.id")) {
			output = getDefaultNBT(nbt.getInteger("gt.mte.id")).getInteger("gt.output");
		}
		if(output == 0) time1 = energy;
		else time1 = energy / output;
		if(calciteStack == null) {
			 currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar_small,%d,%s,%d}",0,"null",1000));
		}
		else currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar_small,%d,%s,%d}",calciteStack.amount,calciteStack.getFluid().getName(),1000));

		String invString = "";
		if(fuelStack != null) {
			boolean isBlock = fuelStack.getItem() instanceof ItemBlock;
			int mode = isBlock ? 0 : 1;
			
			invString +=  SpecialChars.RENDER + String.format("{gt6v.stack,%d,%s,%d,%d,%s}",mode,getRegistryName(fuelStack),fuelStack.stackSize,fuelStack.getItemDamage(),I18n.format("gt6v.info.fuel"));
			
		}
		if(ashStack != null) {
			boolean isBlock = ashStack.getItem() instanceof ItemBlock;
			int mode = isBlock ? 0 : 1;
			if(invString.length() != 0) invString += "   ";
			invString += SpecialChars.RENDER + String.format("{gt6v.stack,%d,%s,%d,%d,%s}",mode,getRegistryName(ashStack),ashStack.stackSize,ashStack.getItemDamage(),I18n.format("gt6v.info.ash"));	
		}
		currenttip.add(invString);
		if(active) {
			currenttip.add(I18n.format("gt6v.countdown.burn")+getTimeString(time1));
			currenttip.add(I18n.format("gt6v.out.hu", output));
		}
		return currenttip;
	}
	
	public static List<String> BoilerTankBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		TileEntity te = accessor.getTileEntity();
		NBTTagCompound nbt = accessor.getNBTData();
		MultiTileEntityBoilerTank boiler = (MultiTileEntityBoilerTank)te;
		NBTTagCompound nbt2 = getDefaultNBT(nbt.getInteger("gt.mte.id"));
		
		FluidStack stack = null;
		int steamSize = 0;
		int steamCap = nbt2.getInteger("gt.capacity.su");
		if(steamCap == 0) steamCap = 1;
		int hu = nbt.getInteger("gt.energy");
		if(nbt.hasKey("gt.tank.0")) {
			stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.0"));
		} 
		if(nbt.hasKey("gt.tank.1")) {
			steamSize = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.1")).amount;
		}
		
		if(stack == null) {
			 currenttip.add("      "+String.format(SpecialChars.RENDER+"{gt6v.tankbar_small,%d,%s,%d}",0,"null",4000));
		}
		else currenttip.add("      "+String.format(SpecialChars.RENDER+"{gt6v.tankbar_small,%d,%s,%d}",stack.amount,stack.getFluid().getName(),4000));
		currenttip.add(String.format("Steam "+SpecialChars.RENDER+"{gt6v.energybar,%s,%d,%d}","a",steamSize,steamCap));
		currenttip.add(String.format("HU    "+SpecialChars.RENDER+"{gt6v.energybar,%s,%d,%d}","b",hu,nbt2.getInteger("gt.capacity") == 0 ? 1 : nbt2.getInteger("gt.capacity")));
		int eff = 10000;
		int outFactor = 4 * steamSize / steamCap;
		if(outFactor > 0) outFactor--;
		int state = outFactor;
		if(nbt.getInteger("gt.cooldown") < 120 && state == 0 && hu == 0) state = 3;
		if(hu != 0 && (stack == null || stack.amount == 0)) state = 4;
		if(nbt.hasKey("gt.eff")) eff = nbt.getInteger("gt.eff");
		currenttip.add(I18n.format("gt6v.info.eff",eff/100.0)+"    "+I18n.format("gt6v.out.su",nbt2.getInteger("gt.output.su") * outFactor) + String.format("(%s)", I18n.format("gt6v.boiler.state."+state)));
		return currenttip;
	}
	
	public static List<String> LiquidMotorBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		int output = 0;
		boolean active = false;
		if(nbt.hasKey("gt.energy")) active = true;
		double consumeRate = 0.0;
		if(nbt.hasKey("gt.mte.id")) {
			NBTTagCompound nbt2 = getDefaultNBT(nbt.getInteger("gt.mte.id"));
			output = nbt2.getInteger("gt.output");
		}
		int cap = output == 0 ? 1 : output * 10;
		
		if(nbt.hasKey("gt.tank.0")) {
			FluidStack fuel = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.0"));
			if(fuel != null && active) {
				Iterator<Recipe> recipes = FM.Engine.mRecipeFluidMap.get(fuel.getFluid().getName()).iterator();
				long fuelHeat = 0;
				if(recipes != null && recipes.hasNext()){
					Recipe recipe = recipes.next();
					fuelHeat = recipe.mEUt * recipe.mDuration * -1;
					FluidStack[] fluids = recipe.mFluidInputs;
					for(int i = 0;i < fluids.length;i++){
						String fName = FluidRegistry.getFluidName(fluids[i].getFluid());
						if(fName == fuel.getFluid().getName()){
							fuelHeat /= fluids[i].amount;
							break;
						}
					}
				}
				consumeRate = output * 100 / fuelHeat / 100.0;
			}
			currenttip.add(I18n.format("gt6v.fluid.visual.fuel", WailaUtils.getSmallTankBarRenderString(fuel, cap)));
			
		}
		
		if(nbt.hasKey("gt.tank.1")) {
			FluidStack stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.1"));
			currenttip.add(I18n.format("gt6v.fluid.visual.garbage", WailaUtils.getSmallTankBarRenderString(stack, cap)));
		}
		if(active) {
			currenttip.add(I18n.format("gt6v.consume.burn", consumeRate));
			currenttip.add(I18n.format("gt6v.out.ru", output));
		}
		return currenttip;
	}
	
	public static List<String> LiquidBurningBoxBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		int output = 1;
		double consumeRate = 0;
		FluidStack fuel = null;
		boolean active = false;
		if(nbt.hasKey("gt.active")) active = true;
		if(nbt.hasKey("gt.tank")) fuel = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank"));
		if(nbt.hasKey("gt.mte.id")) {
			output = getDefaultNBT(nbt.getInteger("gt.mte.id")).getInteger("gt.output");
			if(output == 0) output = 1;
		}
		if(fuel != null && active) {
			Iterator<Recipe> recipes = FM.Burn.mRecipeFluidMap.get(fuel.getFluid().getName()).iterator();
			long fuelHeat = 0;
			if(recipes != null && recipes.hasNext()){
				Recipe recipe = recipes.next();
				fuelHeat = recipe.mEUt * recipe.mDuration * -1;
				FluidStack[] fluids = recipe.mFluidInputs;
				for(int i = 0;i < fluids.length;i++){
					String fName = FluidRegistry.getFluidName(fluids[i].getFluid());
					if(fName == fuel.getFluid().getName()){
						fuelHeat /= fluids[i].amount;
						break;
					}
				}
			}
			consumeRate = output * 100 / fuelHeat / 100.0;
		}
		if(fuel == null) {
			 currenttip.add(I18n.format("gt6v.info.fuel")+"  "+String.format(SpecialChars.RENDER+"{gt6v.tankbar_small,%d,%s,%d}",0,"null",output * 10));
		}
		else currenttip.add(I18n.format("gt6v.info.fuel") +"  "+String.format(SpecialChars.RENDER+"{gt6v.tankbar_small,%d,%s,%d}",fuel.amount,fuel.getFluid().getName(),output * 10));
		if(active) {
			currenttip.add(I18n.format("gt6v.consume.burn", consumeRate));
			currenttip.add(I18n.format("gt6v.out.hu", output));
		}
		return currenttip;
	}
	
	public static List<String> RockBody(ItemStack itemStack,List<String> currenttip,IWailaDataAccessor accessor,IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		if(nbt.hasKey("gt.value")) {
			ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("gt.value"));
			currenttip.add(String.format("%s",I18n.format(stack.getItem().getUnlocalizedName(stack) + ".name")));
		}
		return currenttip;
	}
	
	public static List<String> SiftingTableBody(ItemStack itemStack,List<String> currenttip,IWailaDataAccessor accessor,IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		if(nbt.hasKey("gt.invlist")) {
			NBTTagList list = (NBTTagList) nbt.getTag("gt.invlist");
			if(list.tagCount() == 0) return currenttip;
			int inSlot = -1;
			StringBuilder builder = new StringBuilder();
			builder.append(SpecialChars.RENDER);
			builder.append("{gt6v.stacklist,");
			builder.append(I18n.format("gt6v.item.out"));
			builder.append(",3");
			for(int i = 0;i < list.tagCount();i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				if(tag.getInteger("s") == 0) {
					inSlot = i;
				}
				else {
					ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
					builder.append(",");
					builder.append(WailaUtils.encodeStack(stack));
				}
			}
			builder.append("}");
			if(inSlot != -1) {
				ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(inSlot));
				boolean isBlock = stack.getItem() instanceof ItemBlock;
				int mode = isBlock ? 0 : 1;
				String invString = SpecialChars.RENDER + String.format("{gt6v.stack,%d,%s,%d,%d,%s}",mode,getRegistryName(stack),stack.stackSize,stack.getItemDamage(),I18n.format("gt6v.item.in"));
				currenttip.add(invString);
			}
			if(inSlot == -1 || list.tagCount() > 1) {
				currenttip.add(builder.toString());
			}
		}
		return currenttip;
	}
	
	
	
	public static List<String> BarrelBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		TileEntity te = accessor.getTileEntity();
		NBTTagCompound nbt = accessor.getNBTData();
		TileEntityBase08Barrel barrel = (TileEntityBase08Barrel)te;
		int capacity = barrel.mTank.getCapacity();
		FluidStack stack = null;
		if(nbt.hasKey("gt.tank")) stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank"));
		if(stack == null) {
			 currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar,%d,%s,%d}",0,"null",capacity));
		}
		else currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar,%d,%s,%d}",stack.amount,stack.getFluid().getName(),capacity));
		return currenttip;
	}
	
	public static List<String> AnvilBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		long dur = nbt.getLong("gt.durability") / 1000;
		currenttip.add(I18n.format("gt6v.anvil.dura",dur / 10,dur % 10));
		int side = getCurrentSide();
		int anvilFacing = nbt.getInteger("gt.facing");

		switch(anvilFacing){
		case 0:
		case 1:
			{
				currenttip.add("Invalid Anvil Facing!");
				break;
			}
		case 2:
		case 3:
			{
				if(side >= 4){
					int isBig = (side - anvilFacing) % 2;
					if(isBig == 1) currenttip.add(I18n.format("gt6v.anvil.big"));
					else currenttip.add(I18n.format("gt6v.anvil.small"));
				}
				break;
			}
		case 4:
		case 5:
			{
				if(side < 4 && side > 1){
					int isBig = (anvilFacing - side) % 2;
					if(isBig == 1) currenttip.add(I18n.format("gt6v.anvil.big"));
					else currenttip.add(I18n.format("gt6v.anvil.small"));
				}
				break;
			}
			
		}
		return currenttip;
	}
	
	public static List<String> SteamEngineBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		long state = nbt.getLong(CS.NBT_VISUAL);
		int output = getDefaultNBT(nbt.getInteger("gt.mte.id")).getInteger("gt.output");
		int eCap = getDefaultNBT(nbt.getInteger("gt.mte.id")).getInteger(CS.NBT_CAPACITY);
		int wState = 0;
		long energy = nbt.getLong(CS.NBT_ENERGY);
		double eff = nbt.getLong(CS.NBT_EFFICIENCY);
		if(nbt.hasKey(CS.NBT_ACTIVE)) wState = 1;
		else if(nbt.hasKey(CS.NBT_STOPPED)) wState = 2;
		FluidStack stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.0"));
		
		currenttip.add(String.format("KU    "+SpecialChars.RENDER+"{gt6v.energybar,%s,%d,%d}","b",energy,eCap == 0 ? 1 : eCap));
		long mOut = output * (state + 1) / 16;
		long sIn = (long) (mOut * 2 * 10000.0/ eff);
		if(wState != 1) {
			mOut = 0;
			sIn = 0;
		}
		currenttip.add(I18n.format("gt6v.out.ku", mOut));
		currenttip.add(I18n.format("gt6v.in.su", sIn));
		currenttip.add(I18n.format("gt6v.state")+I18n.format("gt6v.engine.state."+wState));
		return currenttip;
	}
	
	private static long getTurbineOutput(NBTTagCompound nbt) {
		if(nbt.hasKey("gt.output.su")) {
			return nbt.getLong("gt.output.su");
		}
		FluidStack stack = NBTUtils.getFluidStackFromKey(nbt, "gt.tank.0");
		return stack.amount;
	}
	
	public static List<String> SteamTurbineBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		long out = getTurbineOutput(nbt);
		NBTTagCompound nbt2 = getDefaultNBT(nbt.getInteger("gt.mte.id"));
		long stdIn = nbt2.getInteger("gt.input");
		if(out > stdIn * 2) {
			currenttip.add(I18n.format("gt6v.overpowered"));
		} 
		else {
			currenttip.add(I18n.format("gt6v.in.su", out));
			currenttip.add(I18n.format("gt6v.out.ru", out / 3));
		}
		return currenttip;
	}
	
	
	public static List<String> AxleBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		long transfer = nbt.getLong("gt.transfer.ru");
		currenttip.add(I18n.format("gt6v.transfer.ru", transfer));
		return currenttip;
	}
	
	public static List<String> ElectricWireBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		long transfer = nbt.getLong("gt.transfer.eu");
		currenttip.add(I18n.format("gt6v.transfer.eu", transfer));
		return currenttip;
	}
	
	public static List<String> FluidSpringBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		FluidStack stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.spring"));
		if(stack != null) {
			currenttip.add(I18n.format("gt6v.spring.a", I18n.format(stack.getUnlocalizedName())));
			currenttip.add(I18n.format("gt6v.spring.b", getTimeString2(stack.amount)));
		}
		return currenttip;
	}
	
	
	private static int getRodType(int meta) {
		if(meta > 9400) return 1;
		if(meta > 9300) return 0;
		if(meta > 9209) return 3;
		if(meta == 9202) return 2;
		if(meta == 9203) return 0;
		if(meta == 9204) return 0;
		return 0;
	}
	
	public static List<String> ReactorBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound nbt = accessor.getNBTData();
		
		FluidStack coolantStack = NBTUtils.getFluidStackFromKey(nbt, "gt.tank.0");

		StringBuilder builder = new StringBuilder(I18n.format("gt6v.reactor.coolant"));
		builder.append(getSmallTankBarRenderString(coolantStack,64000));
		currenttip.add(builder.toString());
		
		FluidStack hotFluidStack = NBTUtils.getFluidStackFromKey(nbt, "gt.tank.1");
	
		builder = new StringBuilder(I18n.format("gt6v.reactor.hotfluid"));
		builder.append(getSmallTankBarRenderString(hotFluidStack,64000));
		currenttip.add(builder.toString());
		
		byte stopped = nbt.getByte("gt.stopped");
		currenttip.add(I18n.format("gt6v.reactor.state", I18n.format("gt6v.reactor.state."+stopped)));
		
		MultiTileEntityReactorCore core = (MultiTileEntityReactorCore)accessor.getTileEntity();
		long heat = 0;
		SlotItemStack[] inv = NBTUtils.getInventoryFromKey(nbt, "gt.invlist");
		if(inv == null) return currenttip;
		for(int i = 0;i < inv.length;i++) {
			int type = getRodType(inv[i].stack.getItemDamage());
			long neutron = nbt.getLong(String.format("gt.value.o.%d", inv[i].slot));
			switch(type) {
				case 1:
					heat += neutron * 2;
					break;
				case 2:
					heat += neutron / 2;
					break;
				case 3:
					heat += neutron;
					break;
				default: 
			}
		}
		if(MT.Sn.mLiquid.isFluidEqual(coolantStack)) {
			heat = (long) Math.ceil(heat / 3.0);
		}
		else if(MT.Na.mLiquid.isFluidEqual(coolantStack)) {
			heat = (long) Math.ceil(heat / 6.0);
		}	
		if(heat != 0) currenttip.add(I18n.format("gt6v.reactor.out.hu", heat));
		for(int i = 0;i < inv.length;i++) {
			for(int j = 0;j < inv.length;j++) {
				if(inv[j].slot == i) { 
					if(inv[j].stack.hasTagCompound()) {
						String duraString = I18n.format("gt6v.reactor.durable", GT6WailaViewer.getTimeString(inv[j].stack.stackTagCompound.getLong(CS.NBT_DURABILITY) / 100));
						currenttip.add(I18n.format("gt6v.reactor.rods", i,inv[j].stack.getDisplayName() + duraString));
					} 
					else {
						currenttip.add(I18n.format("gt6v.reactor.rods", i,inv[j].stack.getDisplayName()));
					}
				}
			}
		}	
		builder = new StringBuilder();
		for(int i = 0;i < 4;i++) {
			if(!nbt.hasKey("gt.value.o."+i)) continue;
			if(builder.length() != 0) builder.append(" | ");
			builder.append(String.format("%d: %d", i, nbt.getLong("gt.value.o."+i)));
		}
		if(builder.length() != 0) {
			currenttip.add(I18n.format("gt6v.reactor.neutron", builder.toString()));
		}
		return currenttip;
	}
	
	public static List<String> MixingBowlBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound nbt = accessor.getNBTData();
		
		NBTTagList invlist = (NBTTagList) nbt.getTag("gt.invlist");
		if(invlist == null) return currenttip;
		int count = invlist.tagCount();
		int outIndex = -1;
		if(count != 0) {
			StringBuilder builder = new StringBuilder();
			builder.append(SpecialChars.RENDER);
			builder.append("{gt6v.stacklist,");
			builder.append(I18n.format("gt6v.item.in"));
			builder.append(",3");
			for(int i = 0;i < count;i++) {
				NBTTagCompound itemCompound = invlist.getCompoundTagAt(i);
				if(itemCompound.getLong("s") == 6L) {
					outIndex = i;
					continue;
				}
				builder.append(",");
				builder.append(encodeStack(ItemStack.loadItemStackFromNBT(itemCompound)));
			}
			builder.append("}");


			if(!(count == 1 && outIndex != -1)) currenttip.add(builder.toString());
		}
		if(outIndex != -1) {
			ItemStack stack = ItemStack.loadItemStackFromNBT(invlist.getCompoundTagAt(outIndex));
			boolean isBlock = stack.getItem() instanceof ItemBlock;
			int mode = isBlock ? 0 : 1;
			String invString = SpecialChars.RENDER + String.format("{gt6v.stack,%d,%s,%d,%d,%s}",mode,getRegistryName(stack),stack.stackSize,stack.getItemDamage(),I18n.format("gt6v.item.out"));
			currenttip.add(invString);
		}
		for(int i = 0;i < 6;i++) {
			if(nbt.hasKey("gt.tank.in."+i)) {
				FluidStack stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.in."+i));
				currenttip.add(I18n.format("gt6v.fluid.in.slots", i + 1,getFluidStackString(stack)));
				//FluidStack stack = null;
				stack.writeToNBT(nbt);
			}
		}
		for(int i = 0;i < 2;i++) {
			if(nbt.hasKey("gt.tank.out."+i)) {
				FluidStack stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank.out."+i));
				currenttip.add(I18n.format("gt6v.fluid.out.slots", i + 1,getFluidStackString(stack)));
			}
		}
		return currenttip;
	}
	
	public static List<String> FluidPipeBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound tag = accessor.getNBTData();
		if(tag.hasKey("gt.mlast.8")) {
			boolean shouldExit = false;
			for(int i = 0;i < 3;i++) {
				StringBuilder builder = new StringBuilder();
				int j;
				for(j = 0;j < 3;j++) {
					if(tag.hasKey("gt.tank."+(i * 3 + j))) {
						FluidStack stack = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("gt.tank."+(i * 3 + j)));
						builder.append(getFluidStackString(stack));
						builder.append(" | ");
					}
					else {
						shouldExit = true;
						break;
					}
				}
				if(builder.length() != 0) {
					builder.deleteCharAt(builder.length() - 2);
					if(j == 1) currenttip.add(I18n.format("gt6v.fluid.pipe.b", i * 3 + 1,builder.toString()));
					else currenttip.add(I18n.format("gt6v.fluid.pipe.a", i * 3 + 1,i * 3 + j,builder.toString()));
				}
				if(shouldExit) break;
			}
		}
		else {
			boolean shouldExit = false;
			for(int i = 0;i < 2;i++) {
				StringBuilder builder = new StringBuilder();
				int j;
				for(j = 0;j < 2;j++) {
					if(tag.hasKey("gt.tank."+(i * 2 + j))) {
						FluidStack stack = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("gt.tank."+(i * 2 + j)));
						builder.append(getFluidStackString(stack));
						builder.append(" | ");
					}
					else {
						shouldExit = true;
						break;
					}
				}
				if(builder.length() != 0) {
					builder.deleteCharAt(builder.length() - 2);
					if(j == 1) currenttip.add(I18n.format("gt6v.fluid.pipe.b", i * 2 + 1,builder.toString()));
					else currenttip.add(I18n.format("gt6v.fluid.pipe.a", i * 2 + 1,i * 2 + 2,builder.toString()));
				}
				if(shouldExit) break;
			}
		}
		return currenttip;
	}
	
	public static List<String> SmallFluidContainerBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound nbt = accessor.getNBTData();
		TileEntityBase08FluidContainer entity = (TileEntityBase08FluidContainer)accessor.getTileEntity();
		int capacity = entity.mTank.getCapacity();
		FluidStack stack = null;
		if(nbt.hasKey("gt.tank")) stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank"));
		if(stack == null) {
			 currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar,%d,%s,%d}",0,"null",capacity));
		}
		else currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar,%d,%s,%d}",stack.amount,stack.getFluid().getName(),capacity));
		return currenttip;
	}

	public static int getCurrentSide() {
		return Minecraft.getMinecraft().objectMouseOver.sideHit;
	}
	
	public static List<String> CrucibleBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		TileEntity te = accessor.getTileEntity();
		long maxTemp = 0;
		NBTTagCompound nbt = accessor.getNBTData();
		MultiTileEntitySmeltery smeltery = (MultiTileEntitySmeltery)te;	
		maxTemp = smeltery.getTemperatureMax((byte)0);
		currenttip.add(String.format("Temperature:  %d K / %d K", nbt.getShort("gt.temperature"),maxTemp));
		NBTTagCompound nbt2 = nbt.getCompoundTag("gt.materials");
		String s = "";
		if(nbt2 != null) {
			for(int i = 0;i < 32;i++) {
				if(!nbt2.hasKey(""+i)) {
					if(s != "") currenttip.add(String.format("Material %d-%d ",(i / 4 * 4 + 1),i) + s);
					break;
				}
				NBTTagCompound nbt3 = nbt2.getCompoundTag(""+i);
				
				short id = nbt3.getShort("i");
				long amount = nbt3.getLong("a") / 648648;
				
				String name = I18n.format("gt.material." + gregapi.oredict.OreDictMaterial.get(id).mNameInternal);
				if(s != "") s += " | ";
				s = s + amount / 1000.0 + " Unit " + name;
				if(i % 4 == 3) {
					currenttip.add(String.format("Material %d-%d ",i - 2,i + 1) + s);
					s = "";
				}
			}
		}
		return currenttip;
	}
	
	public static List<String> LargeCrucibleBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		TileEntity te = accessor.getTileEntity();
		long maxTemp = 0;
		NBTTagCompound nbt = accessor.getNBTData();

			MultiTileEntityCrucible crucible = (MultiTileEntityCrucible)te;
			maxTemp = crucible.getTemperatureMax((byte)0);

		
		currenttip.add(String.format("Temperature:  %d K / %d K", nbt.getShort("gt.temperature"),maxTemp));
		NBTTagCompound nbt2 = nbt.getCompoundTag("gt.materials");
		String s = "";
		if(nbt2 != null) {
			for(int i = 0;i < 32;i++) {
				if(!nbt2.hasKey(""+i)) {
					if(s != "") currenttip.add(String.format("Material %d-%d ",(i / 4 * 4 + 1),i) + s);
					break;
				}
				NBTTagCompound nbt3 = nbt2.getCompoundTag(""+i);
				
				short id = nbt3.getShort("i");
				long amount = nbt3.getLong("a") / 648648;
				
				String name = I18n.format("gt.material." + gregapi.oredict.OreDictMaterial.get(id).mNameInternal);
				if(s != "") s += " | ";
				s = s + amount / 1000.0 + " Unit " + name;
				if(i % 4 == 3) {
					currenttip.add(String.format("Material %d-%d ",i - 2,i + 1) + s);
					s = "";
				}
			}
		}
		return currenttip;
	}
	
	public static List<String> MultiBlockTankBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		TileEntity te = accessor.getTileEntity();
		MultiTileEntityTank barrel = (MultiTileEntityTank)te;
		int capacity = barrel.mTank.getCapacity();
		FluidStack stack = null;
		if(nbt.hasKey("gt.tank")) stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("gt.tank"));
		if(stack == null) {
			 currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar,%d,%s,%d}",0,"null",capacity));
		}
		else currenttip.add(String.format(SpecialChars.RENDER+"{gt6v.tankbar,%d,%s,%d}",stack.amount,stack.getFluid().getName(),capacity));
		
		return currenttip;
	}
	
	public static List<String> BushBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		/*
		<line displayname = "生长进度">
			if(!showProgress) return null;
			if(nbt['gt.state'] != 3){
				var t = nbt['gt.state'] * 256;
				if(nbt['gt.progress'] < 0) t = t + 256;
				t = t + nbt['gt.progress'];
				var progress = parseInt(t * 1000 / (256 * 3)) / 10;
				return progress + " %";
			}
			return AQUA + "已成熟，请收获";
		</line>
		<line displayname = "产出作物">
			return nbt['gt.value']['Count'] + Unit("个 ") + name(nbt['gt.value']);
		</line>		  
		  */
		NBTTagCompound nbt = accessor.getNBTData();
		long state = nbt.getLong("gt.state");
		long progress = nbt.getLong("gt.progress");
		if(state == 3) currenttip.add(I18n.format("gt6v.bush.progress",I18n.format("gt6v.bush.mature")));
		else {
			double prog = state * 256;
			if(progress < 0) prog += 256;
			prog += progress;
			prog = prog * 1000 / (256 * 3 * 10);
			String progString = String.format("%.1f%%", prog);
			currenttip.add(I18n.format("gt6v.bush.progress", progString));
		}
		ItemStack stack = NBTUtils.getStackFromKey(nbt, "gt.value");
		
		return currenttip;
	}

	public static List<String> MultiBlockBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config){
		NBTTagCompound nbt = accessor.getNBTData();
		if(nbt.hasKey("target")) {
			targetAccessor.remoteNbt = nbt.getCompoundTag("target");
			targetAccessor.tileEntity = accessor.getWorld().getTileEntity(nbt.getInteger("gt.target.x"), nbt.getInteger("gt.target.y"), nbt.getInteger("gt.target.z"));
			targetAccessor.mop = new MovingObjectPosition(targetAccessor.tileEntity.xCoord,targetAccessor.tileEntity.yCoord,targetAccessor.tileEntity.zCoord,(byte)0,Vec3.createVectorHelper(0, 0, 0),true);
			NBTTagCompound nbt2 = targetAccessor.getNBTData();
			String id = nbt2.getString("id");
			if(targetAccessor.tileEntity == null) return currenttip;
			Class teClass = targetAccessor.tileEntity.getClass();
			Class originClass = teClass;
			if(forbidClass.contains(originClass)) return currenttip;
			WailaBodyFunction handle = bodyMethods.get(teClass);
			while(handle == null && teClass != TileEntity.class) {
				teClass = teClass.getSuperclass();
				handle = bodyMethods.get(teClass);
			}
			if(handle != null) bodyMethods.put(originClass, handle);
			else {
				forbidClass.add(originClass);
				return currenttip;
			}
			try {
				return handle.getBody(itemStack, currenttip, accessor, config);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return currenttip;
	}

	public static String getFluidStackString(FluidStack stack) {
		if(stack == null) return "";
		else return String.format("%d L %s", stack.amount,I18n.format(stack.getUnlocalizedName()));
	}
	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}
	

	public static String getTimeString(long ticks) {
		if(ticks < 20) return I18n.format("gt6v.time.1",ticks);
		else if(ticks < 1200) return I18n.format("gt6v.time.2",ticks / 20);
		else if(ticks < 72000) return I18n.format("gt6v.time.3",ticks / 1200, (ticks % 1200) / 20);
		else if(ticks < 1728000) return I18n.format("gt6v.time.4",ticks / 72000, (ticks % 72000) / 1200);
		else return I18n.format("gt6v.time.5", ticks / 1728000,(ticks % 1728000) / 72000);
	}
	public static String getTimeString2(long ticks) {
		if(ticks < 20) return I18n.format("gt6v.time.6",ticks);
		else if(ticks < 1200) return I18n.format("gt6v.time.7",ticks / 20);
		else if(ticks < 72000) return I18n.format("gt6v.time.8",ticks / 1200);
		else if(ticks < 1728000) return I18n.format("gt6v.time.9",ticks / 72000);
		else return I18n.format("gt6v.time.10", ticks / 1728000);
	}
	public static NBTTagCompound getDefaultNBT(int id) {
		return MultiTileEntityRegistry.getRegistry("gt.multitileentity").getClassContainer(id).mParameters;
	}
	
	public static void callbackRegisterClient(IWailaRegistrar reg) {
		reg.registerTooltipRenderer("gt6v.tankbar", new TankBarRenderer());
		reg.registerTooltipRenderer("gt6v.tankbar_small", new SmallTankBarRenderer());
		reg.registerTooltipRenderer("gt6v.energybar", new EnergyBarRenderer());
		reg.registerTooltipRenderer("gt6v.stack", new TooltipStackRenderer());
		reg.registerTooltipRenderer("gt6v.stacklist", new ItemStackListRenderer());
	}
	
	public static void callbackRegister(IWailaRegistrar reg)
	{
		GT6WailaViewer viewer = new GT6WailaViewer();		
		for(Class clazz : bodyMethods.keySet()) {
			reg.registerBodyProvider(viewer, clazz);
			reg.registerNBTProvider(viewer, clazz);
		}
	}
}
