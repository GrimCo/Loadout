package com.grimco.loadout;

import com.grimco.loadout.command.CommandLoadout;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Loadout.MOD_ID, name = Loadout.MOD_NAME, version = Loadout.MOD_VERSION, acceptableRemoteVersions = "*")
public class Loadout
{
	public final static String MOD_ID = "loadout";
	public final static String MOD_NAME = "Loadout";
	public final static String MOD_VERSION = "1.0.2";
	
	@Instance
	public Loadout instance;
	
	protected static File GRIMCO_DIR = null;
	
	private static Logger logger;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		createGrimcoDir(event.getModConfigurationDirectory());
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent event)
	{
	
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		LoadoutManager.Parser.loadLoadouts();
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandLoadout());
	}
	
	public static Logger getLogger()
	{
		return logger;
	}
	
	private void createGrimcoDir(File configDir)
	{
		GRIMCO_DIR = configDir.toPath().resolve("grimco/").toFile();
		
		if(!GRIMCO_DIR.exists())
		{
			GRIMCO_DIR.mkdir();
		}
	}
	
	@Config(modid = MOD_ID, name = "grimco/loadout")
	public static class ModConfig
	{
		@Config.Comment("Should Loadouts with Armor auto equip them if possible")
		public static boolean equipArmor = false;
		
		
	}
}
