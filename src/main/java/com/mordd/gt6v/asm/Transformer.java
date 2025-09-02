package com.mordd.gt6v.asm;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.BasicVerifier;
import org.objectweb.asm.util.CheckClassAdapter;

import com.mordd.gt6v.GT6Viewer;

import net.minecraft.launchwrapper.IClassTransformer;


public class Transformer implements IClassTransformer {
	public static List<String> supportedClassHashes = new ArrayList<String>();
	static {
		supportedClassHashes.add("6154366160c9bf6955100b3f7a7b0b7f");
	}
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name.equals("gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam")) {
			//byte[] basicClassCopy = basicClass.clone();
			
			String hash = DigestUtils.md5Hex(basicClass);
			boolean shouldLoad = false; 
			for(String targetHash : supportedClassHashes) {
				if(targetHash.equals(hash)) {
					shouldLoad = true;
					break;
				}
			}
			if(!shouldLoad) return basicClass;
			
			ClassReader cr = new ClassReader(basicClass);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
			org.objectweb.asm.tree.FieldNode f = new org.objectweb.asm.tree.FieldNode(Opcodes.ASM5, Opcodes.ACC_PUBLIC, "mGenerated", Type.LONG_TYPE.getDescriptor(), null, null);
			cn.fields.add(f);
			
			for(MethodNode mn: cn.methods) {
				 if(mn.name.equals("doConversion")) {
					 AbstractInsnNode node = mn.instructions.getFirst();
					 while(node.getNext() != null) {
						 node = node.getNext();
						 if(node.getOpcode() == Opcodes.LSTORE && node.getType() == AbstractInsnNode.VAR_INSN) {
							 VarInsnNode node1 = (VarInsnNode)node;
							 if(node1.var == 3) break;
						 }
					 }		 
					 
					 mn.instructions.insert(node, new FieldInsnNode(Opcodes.PUTFIELD,"gregtech/tileentity/energy/converters/MultiTileEntityTurbineSteam","mGenerated",Type.LONG_TYPE.getDescriptor()));
					 mn.instructions.insert(node, new InsnNode(Opcodes.LDIV));
					 mn.instructions.insert(node, new InsnNode(Opcodes.I2L));
					 mn.instructions.insert(node, new FieldInsnNode(Opcodes.GETSTATIC,"gregapi/data/CS","STEAM_PER_EU",Type.INT_TYPE.getDescriptor()));
					 mn.instructions.insert(node, new VarInsnNode(Opcodes.LLOAD,3));
					 mn.instructions.insert(node, new VarInsnNode(Opcodes.ALOAD,0));
					 
					AbstractInsnNode node1 = mn.instructions.getLast();
					int r = 1;
					while(node1.getPrevious() != null) {
						node1 = node1.getPrevious();
						if(node1.getType() == AbstractInsnNode.LABEL) {
							if(r == 0) break;
							r--;
						}
					}
					
					AbstractInsnNode node2 = mn.instructions.getFirst();
					 while(node2.getNext() != null) {
						 node2 = node2.getNext();
						 if(node2.getOpcode() == Opcodes.IFEQ) {
							 break;
						 }
					 }	
					 
					JumpInsnNode jmpNode = (JumpInsnNode)node2;
					LabelNode elseNode = new LabelNode();
					mn.instructions.insertBefore(node1, new JumpInsnNode(Opcodes.GOTO,jmpNode.label));
					mn.instructions.insertBefore(node1, elseNode);
					mn.instructions.insertBefore(node1, new VarInsnNode(Opcodes.ALOAD,0));
					mn.instructions.insertBefore(node1, new InsnNode(Opcodes.LCONST_0));
					mn.instructions.insertBefore(node1, new FieldInsnNode(Opcodes.PUTFIELD,"gregtech/tileentity/energy/converters/MultiTileEntityTurbineSteam","mGenerated",Type.LONG_TYPE.getDescriptor()));
					jmpNode.label = elseNode;
					mn.instructions.resetLabels();	
				 }
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			try {
				
				cn.accept(cw);	
			}
			catch(Exception e) {
				return basicClass;
			}
			
			return cw.toByteArray();
		}
		return basicClass;
	}
	
}
