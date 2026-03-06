-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (aka
srgnames) - this means that you will not be able to read them directly against
normal code.

Source pack installation information:

Standalone source installation
==============================

To install this source code for development purposes, extract this zip file.
It ships with a demonstration mod. Run 'gradlew setupDevWorkspace' to create
a gradle environment primed with FML. Run 'gradlew eclipse' or 'gradlew idea' to
create an IDE workspace of your choice.
Refer to ForgeGradle for more information about the gradle environment
Note: On macs or linux you run the './gradlew.sh' instead of 'gradlew'

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

For reference this is version @MAJOR@.@MINOR@.@REV@.@BUILD@ of FML
for Minecraft version @MCVERSION@.

LexManos' Install Video
=======================
https://www.youtube.com/watch?v=8VEdtQLuLO0&feature=youtu.be

For more details update more often refer to the Forge Forums:
http://www.minecraftforge.net/forum/index.php/topic,14048.0.html

=========================
package com.mordd.gt6v;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.awt.Point;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gregapi.oredict.OreDictMaterial;
import gregapi.worldgen.StoneLayer;
import gregapi.worldgen.StoneLayerOres;
import gregtech.worldgen.NoiseGenerator;

class VeinData{
	int layerId;
	int minY;
	int maxY;
}
class BetweenVeinData{
	List<Integer> layers1;
	List<Integer> layers2;
	int minY;
	int maxY;
}

public class VeinCommand extends CommandBase {

	public static final String usage;
	public static final int VEIN_THRESHOLD = 3;
	public static Map<String,LocalDateTime> lastUsed;
	public static Map<String,List<VeinData>> oreList;
	public static Map<String,List<BetweenVeinData>> betweenList;
	public static Map<String,List<Integer>> materialLayers;
	static{
		StringBuilder builder = new StringBuilder();

		usage = "/g_vein help 查看具体用法";
		lastUsed = new HashMap<String,LocalDateTime>();
		oreList = new HashMap<String,List<VeinData>>();
		betweenList = new HashMap<String,List<BetweenVeinData>>();
		materialLayers = new HashMap<String,List<Integer>> ();
	}
	
	public static void init() {
		for(int i = 0;i < StoneLayer.LAYERS.size();i++) {
			StoneLayer layer = StoneLayer.LAYERS.get(i);
			for(StoneLayerOres ore : layer.mOres) {
				VeinData data = new VeinData();
				data.layerId = i;
				data.minY = ore.mMinY;
				data.maxY = ore.mMaxY;
				if(oreList.containsKey(ore.mMaterial.mNameInternal)) {
					oreList.get(ore.mMaterial.mNameInternal).add(data);
				}
				else {
					List<VeinData> data1 = new ArrayList<VeinData>();
					data1.add(data);
					oreList.put(ore.mMaterial.mNameInternal, data1);
				}
			}
			VeinData data = new VeinData();
			data.layerId = i;
			data.minY = -1;
			data.maxY = -1;
			if(oreList.containsKey(layer.mMaterial.mNameInternal)) {
				oreList.get(layer.mMaterial.mNameInternal).add(data);
			}
			else {
				List<VeinData> data1 = new ArrayList<VeinData>();
				data1.add(data);
				oreList.put(layer.mMaterial.mNameInternal, data1);
			}			
			if(materialLayers.containsKey(layer.mMaterial.mNameInternal)) {
				materialLayers.get(layer.mMaterial.mNameInternal).add(i);
			}
			else {
				List<Integer> data1 = new ArrayList<Integer>();
				data1.add(i);
				materialLayers.put(layer.mMaterial.mNameInternal, data1);
			}	
		}
		
		for(OreDictMaterial mat : StoneLayer.MAP.keySet()) {
			Map<OreDictMaterial,List<StoneLayerOres>> map1 = StoneLayer.MAP.get(mat);
			List<Integer> list1 = materialLayers.get(mat.mNameInternal);
			for(OreDictMaterial mat2 : map1.keySet()) {
				List<StoneLayerOres> list = map1.get(mat2); 
				List<Integer> list2 = materialLayers.get(mat.mNameInternal);
				for(StoneLayerOres ore : list) {
					BetweenVeinData data1 = new BetweenVeinData();
					data1.layers1 = list1;
					data1.layers2 = list2;
					data1.minY = ore.mMinY;
					data1.maxY = ore.mMaxY;
				}
			}
		}
		
	}
	
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "g_vein";
	}
    public int getRequiredPermissionLevel()
    {
        return 1;
    }

	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return usage;
		
	}
	
	private void find_vein(EntityPlayer player,int vein_id,int range,int minY,int maxY) {
		int x = ((int) player.posX / 16) * 16;
		int z = ((int) player.posZ / 16) * 16;
		long seed = player.worldObj.getSeed();
		NoiseGenerator aGen = new NoiseGenerator(seed);
		if(isGoodChunk(aGen,x,z,vein_id,minY,maxY)) {
			player.addChatMessage(new ChatComponentText("指定岩层就在你脚下哦~"));
			return;
		}
		for(int i = 1;i < range;i++) {
			for(int j = -1;j <= i;j++) {
				int k = i - Math.abs(j);
				if(isGoodChunk(aGen,x + j * 16,z + k * 16,vein_id,minY,maxY)) {
					player.addChatMessage(new ChatComponentText(String.format("离你最近的指定岩层位于 %d,%d,距你%d个区块",x + j *16,z + k * 16,i)));
					return;					
				}
				if(isGoodChunk(aGen,x + j * 16,z - k * 16,vein_id,minY,maxY)) {
					player.addChatMessage(new ChatComponentText(String.format("离你最近的指定岩层位于 %d,%d,距你%d个区块",x + j *16,z - k * 16,i)));
					return;					
				}
			}
		}
		player.addChatComponentMessage(new ChatComponentText(String.format("%d区块内没有你要找的矿脉哦~",range)));
	}
	private void find_between(EntityPlayer player,int layer1,int layer2,int range,int minY,int maxY) {
		int x = ((int) player.posX / 16) * 16;
		int z = ((int) player.posZ / 16) * 16;
		long seed = player.worldObj.getSeed();
		NoiseGenerator aGen = new NoiseGenerator(seed);
		if(isGoodChunk(aGen,x,z,layer1,layer2,minY,maxY)) {
			player.addChatMessage(new ChatComponentText("指定矿脉就在你脚下哦~"));
			return;
		}
		for(int i = 1;i < range;i++) {
			for(int j = -1;j <= i;j++) {
				int k = i - Math.abs(j);
				if(isGoodChunk(aGen,x + j * 16,z + k * 16,layer1,layer2,minY,maxY)) {
					player.addChatMessage(new ChatComponentText(String.format("离你最近的指定岩层位于 %d,%d,距你%d个区块",x + j *16,z + k * 16,i)));
					return;					
				}
				if(isGoodChunk(aGen,x + j * 16,z - k * 16,layer1,layer2,minY,maxY)) {
					player.addChatMessage(new ChatComponentText(String.format("离你最近的指定岩层位于 %d,%d,距你%d个区块",x + j *16,z - k * 16,i)));
					return;					
				}
			}
		}
		player.addChatMessage(new ChatComponentText(String.format("%d区块内没有你要找的矿脉哦~",range)));
	}
	private boolean isGoodChunk(NoiseGenerator aGen,int x,int z,int id,int minY,int maxY) {
		boolean goodChunk = true;
		goodChunk &= isGood(aGen,x + 4,z + 4,id,minY,maxY);
		goodChunk &= isGood(aGen,x + 12,z + 4,id,minY,maxY);
		goodChunk &= isGood(aGen,x + 4,z + 12,id,minY,maxY);
		goodChunk &= isGood(aGen,x + 12,z + 12,id,minY,maxY);
		return goodChunk;
	}
	private boolean isGoodChunk(NoiseGenerator aGen,int x,int z,int layer1,int layer2,int minY,int maxY) {
		boolean goodChunk = true;
		goodChunk &= isGood(aGen,x + 4,z + 4,layer1,layer2,minY,maxY);
		goodChunk &= isGood(aGen,x + 12,z + 4,layer1,layer2,minY,maxY);
		goodChunk &= isGood(aGen,x + 4,z + 12,layer1,layer2,minY,maxY);
		goodChunk &= isGood(aGen,x + 12,z + 12,layer1,layer2,minY,maxY);
		return goodChunk;
	}
	private boolean isGood(NoiseGenerator aGen,int x,int z,int id,int minY,int maxY) {
		int layers = VEIN_THRESHOLD;
		for(int i = minY;i <= maxY;i += 4) {
			if(aGen.get(x, i, z, StoneLayer.LAYERS.size()) == id) layers--;
			if(layers == 0) return true;
		}
		return false;
	}
	public static boolean isGood(NoiseGenerator aGen,int x,int z,int layer1,int layer2,int minY,int maxY) {
		int cnt1 = 0;
		
		for(int i = minY;i <= maxY - 4;i++) {
			if(aGen.get(x, i, z, 131) == layer1 && aGen.get(x, i + 4, z,131) == layer2) {
				cnt1++;
			}
		}
		return cnt1 >= VEIN_THRESHOLD;
	}
	public String displayList(List<Integer> list) {
		if(list == null) return "[]";
		else {
			StringBuilder builder = new StringBuilder("[");
			
			for(Integer i : list) {
				builder.append(i);
				builder.append(",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("]");
			return builder.toString();
		}
	}
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(sender instanceof EntityPlayer) {
			EntityPlayer player = CommandBase.getCommandSenderAsPlayer(sender);
			
			if(args.length < 2) throw new CommandException(usage);
			
			if(args[0].equals("find_vein")) {
				if(args.length < 4) new CommandException(usage);
				find_vein(player,Integer.parseInt(args[1]),32,Integer.parseInt(args[2]),Integer.parseInt(args[3]));
			}
			else if(args[0].equals("help")) {
				player.addChatMessage(new ChatComponentText("/g_vein find_vein <id> <yMin> <yMax>,寻找以当前玩家为中心32区块内距离其最近指定id的岩层\n"));
				player.addChatMessage(new ChatComponentText("/g_vein find_vein_wide <id> <yMin> <yMax>,寻找以当前玩家为中心128区块内距离其最近指定id的岩层,每个玩家每2分钟限用1次\n"));
				player.addChatMessage(new ChatComponentText("/g_vein find_between <id1> <id2> <yMin> <yMax>,寻找以当前玩家为中心32区块内距离其最近指定id的岩层间矿脉\n"));
				player.addChatMessage(new ChatComponentText("/g_vein find_between_wide <id1> <id2> <yMin> <yMax>,寻找以当前玩家为中心128区块内距离其最近指定id的岩层间矿脉,每个玩家每2分钟限用1次\n"));
				player.addChatMessage(new ChatComponentText("/g_vein get_id <ore> 查找包含指定ore矿石的岩层id\n"));
				player.addChatMessage(new ChatComponentText("/g_vein get_between <ore> 查找包含指定ore矿石的岩层间矿脉id\n"));
				player.addChatMessage(new ChatComponentText("/g_vein material_name <id> 查找指定metadata矿物的材料名称(用于get_id/get_between)\n"));
				
			}
			else if(args[0].equals("find_vein_wide")) {
				if(args.length < 4) new CommandException(usage);
				String name = player.getCommandSenderName();
				if(lastUsed.containsKey(name)) {
					LocalDateTime time = lastUsed.get(name);
					LocalDateTime excepted = time.plusMinutes(2);
					if(excepted.isAfter(LocalDateTime.now())) {
						player.addChatMessage(new ChatComponentText("每两分钟只能用一次wide类型的命令哦~"));
						return;
					}
				}
				find_vein(player,Integer.parseInt(args[1]),128,Integer.parseInt(args[2]),Integer.parseInt(args[3]));
				lastUsed.put(name,LocalDateTime.now());
			}
			else if(args[0].equals("find_between")) {
				if(args.length < 5) new CommandException(usage);
				find_between(player,Integer.parseInt(args[1]),32,Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));
			}
			else if(args[0].equals("find_between_wide")) {
				if(args.length < 4) new CommandException(usage);
				String name = player.getCommandSenderName();
				if(lastUsed.containsKey(name)) {
					LocalDateTime time = lastUsed.get(name);
					LocalDateTime excepted = time.plusMinutes(2);
					if(excepted.isAfter(LocalDateTime.now())) {
						player.addChatMessage(new ChatComponentText("每两分钟只能用一次wide类型的命令哦~"));
						return;
					}
				}
				find_between(player,Integer.parseInt(args[1]),128,Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));
				lastUsed.put(name,LocalDateTime.now());
			}
			else if(args[0].equals("get_id")) {
				List<VeinData> datas = oreList.get(args[1]);
				if(datas == null) player.addChatComponentMessage(new ChatComponentText("这个材料没有对应任何岩层哦~"));
				else {
					player.addChatMessage(new ChatComponentText("这个材料没有对应下列岩层:"));
					for(VeinData data : datas) {
						player.addChatMessage(new ChatComponentText(String.format("岩层id:%d 最小高度:%d 最大高度:%d",
								data.layerId,data.minY,data.maxY)));
					}
				}
			}
			else if(args[0].equals("get_between")) {
				List<BetweenVeinData> datas = betweenList.get(args[1]);
				if(datas == null) player.addChatComponentMessage(new ChatComponentText("这个材料没有对应任何岩层间矿脉哦~"));
				else {
					player.addChatMessage(new ChatComponentText("这个材料没有对应下列岩层间矿脉:"));
					for(BetweenVeinData data : datas) {
						player.addChatMessage(new ChatComponentText( String.format("顶部岩层id列表:%s", displayList(data.layers1)) ));
						player.addChatMessage(new ChatComponentText( String.format("底部岩层id列表:%s", displayList(data.layers2)) ));
						player.addChatMessage(new ChatComponentText( String.format("最小高度:%d 最大高度:%d", data.minY,data.maxY) ));
					}
				}
			}
			else if(args[0].equals("material_name")) {
				OreDictMaterial mat = OreDictMaterial.get(Long.parseLong(args[1]));
				
				player.addChatMessage(new ChatComponentText(mat.mNameInternal));

			}
			
		}
		else throw new CommandException("This Command can only invoked by player.");

	}

}

