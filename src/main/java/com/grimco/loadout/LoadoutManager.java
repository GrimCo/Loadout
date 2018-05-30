package com.grimco.loadout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.ChunkEvent.Load;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadoutManager
{
	private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
	
	public static HashMap<String,NonNullList<ItemStack>> loadouts = new HashMap<>();
	
	private static Map<String, Object> initializeJSON()
	{
		Map<String, Object> json=new HashMap<>();
		
		File f=new File(Loadout.GRIMCO_DIR, "loadout.json");
		JsonParser parser=new JsonParser();
		JsonObject jsonObject;
		
		try
		{
			jsonObject=(JsonObject)parser.parse(new FileReader(f));
			jsonObject.entrySet().iterator().forEachRemaining(stringJsonElementEntry->json.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue()));
		} catch(IOException e)
		{
		
		}
		
		return json;
	}
	
	public static class Parser
	{
		public static void loadLoadouts()
		{
			Map<String,Object> json = new HashMap<>();
			
			json = initializeJSON();
			
			Set<String> keys  = json.keySet();
			
			for(String key: keys)
			{
				NonNullList<ItemStack> loadout_items = NonNullList.create();
				((JsonArray)json.get(key)).forEach(jsonElement -> loadout_items.add(deserializeItems((JsonObject)jsonElement)));
				
				System.out.println("Loadout: " + key + " Items: " + loadout_items);
				
				loadouts.put(key, loadout_items);
			}
		}
		
		private static ItemStack deserializeItems(JsonObject entry)
		{
			Item placeholder = null;
			int data = 0;
			int count = 1;
			NBTTagCompound tagCompound = null;
			
			if(entry.has("item"))
			{
				ResourceLocation itemRL=new ResourceLocation(entry.get("item").getAsString());
				placeholder = Item.REGISTRY.getObject(itemRL);
			}
			
			if(entry.has("data"))
				data =entry.get("data").getAsInt();
			
			if(entry.has("count"))
				count =entry.get("count").getAsInt();
			
			if(entry.has("nbt"))
				try
				{
					tagCompound = new NBTTagCompound();
					tagCompound.merge(JsonToNBT.getTagFromJson(entry.get("nbt").getAsString()));
				}
				catch(NBTException e)
				{
					Loadout.getLogger().error("Bad NBT for " + placeholder);
				}
				
				ItemStack temp = new ItemStack(placeholder, count, data);
			
				if(tagCompound!=null)
					temp.setTagCompound(tagCompound);
			
			return placeholder!=null ? temp : ItemStack.EMPTY;
		}
	}
	
	public static boolean deleteJSON()
	{
		File f=new File(Loadout.GRIMCO_DIR, "loadout.json");
		return f.delete();
	}

	
	public static class Generator
	{
		public static void addLoadout (String loadout_name, NonNullList<ItemStack>items)
		{
			//Loadout.GRIMCO_DIR
			Map<String, Object> json=new HashMap<>();
			
			json=initializeJSON();
			
			List<Map<String, Object>> loadout_itemstacks=new ArrayList<>();
			
			for(ItemStack stack : items)
				loadout_itemstacks.add(serializeItem(stack));
			
			json.put(loadout_name, loadout_itemstacks);
			
			File f=new File(Loadout.GRIMCO_DIR, "loadout.json");
			
			try(FileWriter w=new FileWriter(f))
			{
				GSON.toJson(json, w);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		private static Map<String, Object> serializeItem (ItemStack stack)
		{
			Map<String, Object> ret=new HashMap<>();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if(stack.getItem().getHasSubtypes()||stack.getItemDamage()!=0)
				ret.put("data", stack.getItemDamage());
			if(stack.getCount()>1)
				ret.put("count", stack.getCount());
			if(stack.hasTagCompound())
			{
				ret.put("type", "minecraft:item_nbt");
				ret.put("nbt", stack.getTagCompound().toString());
			}
			
			return ret;
		}
	}
}
