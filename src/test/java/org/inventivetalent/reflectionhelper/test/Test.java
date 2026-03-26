package org.inventivetalent.reflectionhelper.test;

import com.avaje.ebean.config.ServerConfig;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.minecraft.MinecraftVersion;
import org.inventivetalent.reflection.resolver.wrapper.MethodWrapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class Test {

	public double primitiveDummyMethod(String aString, double returnValue) {
		return returnValue;
	}

	public Method genericDummyMethod(Thread aThread, Exception aException) {
		return null;
	}

	public void voidDummyMethod(Character aCharacter, Class aClass, String aString) {
	}

	public String[] complexDummyMethod(Boolean[][] booleans, Object[] objects) {
		return null;
	}

	public int wildcardMethod1(String string) {
		return 0;
	}

	public double wildcardMethod2(String string) {
		return 0;
	}

	public double wildcardMethod3(boolean b) {
		return 0;
	}

	public int wildCardMethod4(String string) {
		return 0;
	}

	@org.junit.Test
	public void primitiveSignatureTest() throws ReflectiveOperationException {
		String signature = MethodWrapper.getMethodSignature(Test.class.getMethod("primitiveDummyMethod", String.class, double.class));
		assertEquals("double primitiveDummyMethod(String,double)", signature);
	}

	@org.junit.Test
	public void genericSignatureTest() throws ReflectiveOperationException {
		String signature = MethodWrapper.getMethodSignature(Test.class.getMethod("genericDummyMethod", Thread.class, Exception.class));
		assertEquals("Method genericDummyMethod(Thread,Exception)", signature);
	}

	@org.junit.Test
	public void voidSignatureTest() throws ReflectiveOperationException {
		String signature = MethodWrapper.getMethodSignature(Test.class.getMethod("voidDummyMethod", Character.class, Class.class, String.class));
		assertEquals("void voidDummyMethod(Character,Class,String)", signature);
	}

	@org.junit.Test
	public void fullNameSignatureTest() throws ReflectiveOperationException {
		String signature = MethodWrapper.getMethodSignature(Test.class.getMethod("genericDummyMethod", Thread.class, Exception.class), true);
		assertEquals("java.lang.reflect.Method genericDummyMethod(java.lang.Thread,java.lang.Exception)", signature);
	}

	@org.junit.Test
	public void complexSignatureTest() throws ReflectiveOperationException {
		String signature = MethodWrapper.getMethodSignature(Test.class.getMethod("complexDummyMethod", Boolean[][].class, Object[].class), true);
		assertEquals("[Ljava.lang.String; complexDummyMethod([[Ljava.lang.Boolean;,[Ljava.lang.Object;)", signature);
	}

	@org.junit.Test
	public void signatureObjectTest() throws ReflectiveOperationException {
		MethodWrapper.MethodSignature signature = MethodWrapper.MethodSignature.of(Test.class.getMethod("genericDummyMethod", Thread.class, Exception.class), false);
		assertEquals("Method", signature.getReturnType());
		assertEquals("genericDummyMethod", signature.getName());
		assertArrayEquals(new String[] {
				"Thread",
				"Exception"
		}, signature.getParameterTypes());
	}

	@org.junit.Test
	public void signatureFromStringTest() {
		MethodWrapper.MethodSignature signature = MethodWrapper.MethodSignature.fromString("java.lang.reflect.Method genericDummyMethod(java.lang.Thread,java.lang.Exception)");
		assertEquals("java.lang.reflect.Method", signature.getReturnType());
		assertEquals("genericDummyMethod", signature.getName());
		assertEquals("java.lang.Thread", signature.getParameterType(0));
		assertEquals("java.lang.Exception", signature.getParameterType(1));
	}

	@org.junit.Test
	public void wildcardTest() throws ReflectiveOperationException {
		MethodWrapper.MethodSignature wildcardSignature = MethodWrapper.MethodSignature.fromString("* wildcardMethod*(String)");
		MethodWrapper.MethodSignature testSignature1 = MethodWrapper.MethodSignature.of(Test.class.getMethod("wildcardMethod1", String.class), false);
		MethodWrapper.MethodSignature testSignature2 = MethodWrapper.MethodSignature.of(Test.class.getMethod("wildcardMethod2", String.class), false);
		MethodWrapper.MethodSignature testSignature3 = MethodWrapper.MethodSignature.of(Test.class.getMethod("wildcardMethod3", boolean.class), false);
		MethodWrapper.MethodSignature testSignature4 = MethodWrapper.MethodSignature.of(Test.class.getMethod("wildCardMethod4", String.class), false);

		assertTrue(wildcardSignature.matches(testSignature1));
		assertTrue(wildcardSignature.matches(testSignature2));
		assertFalse(wildcardSignature.matches(testSignature3));
		assertFalse(wildcardSignature.matches(testSignature4));
	}

	@org.junit.Test
	public void versionTest() {
		Bukkit.setServer(new CraftServer());
		assertNotNull(MinecraftVersion.VERSION);
		assertNotNull(Minecraft.Version.v1_14_R1);
		assertEquals("net.minecraft.server.v1_16_R3", Minecraft.Version.v1_16_R3.minecraft().getNmsPackage());
		assertEquals("net.minecraft", Minecraft.Version.v1_17_R1.minecraft().getNmsPackage());
		assertEquals("org.bukkit.craftbukkit.v1_21_R7", Minecraft.Version.v1_21_R7.minecraft().getObcPackage());
		assertEquals("org.bukkit.craftbukkit", Minecraft.Version.v26_1.minecraft().getObcPackage());
	}

}
