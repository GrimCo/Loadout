package com.grimco.loadout.events;

import com.grimco.loadout.Loadout;
import com.grimco.loadout.Loadout.ModConfig;
import com.grimco.loadout.LoadoutManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import net.minecraftforge.items.ItemHandlerHelper;

@EventBusSubscriber()
public class EventHandler
{
	public static final String LOUDOUT_TAG = "LOADOUT_KEYS";
	
	@SubscribeEvent
	public static void onPlayerJoinedWorld(PlayerLoggedInEvent event)
	{
		if(event.player.world.isRemote)
			return;

		EntityPlayer player = event.player;
		
		
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		
		NBTTagCompound keys = new NBTTagCompound();
		
		if(data.hasKey(LOUDOUT_TAG))
			keys = data.getCompoundTag(LOUDOUT_TAG);
		
		NBTTagCompound finalKeys=keys;
		LoadoutManager.loadouts.forEach((key,list) -> {
			if(!finalKeys.hasKey(key))
			{
				list.forEach(stack -> {
					if(ModConfig.equipArmor && stack.getItem()instanceof ItemArmor)
					{
						if(player.getItemStackFromSlot(EntityLiving.getSlotForItemStack(stack)).isEmpty())
						{
							player.setItemStackToSlot(EntityLiving.getSlotForItemStack(stack),stack.copy());
						}
						else
						{
							ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
						}
					}
					else
					{
						ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
					}
				});
				
				finalKeys.setByte(key,(byte)1);
			}
		});
		
		NBTTagCompound tempKeyList = finalKeys;
		
		tempKeyList.getKeySet().forEach(tag -> {
			if(!LoadoutManager.loadouts.keySet().contains(tag))
				finalKeys.removeTag(tag);
		});
		
		data.setTag(LOUDOUT_TAG,finalKeys);
		playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
		
	}
}

