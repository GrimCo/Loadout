package com.grimco.loadout.events;

import com.grimco.loadout.LoadoutManager;
import com.grimco.loadout.capability.CapabilityLoadout;
import com.grimco.loadout.capability.ILoadout;
import com.grimco.loadout.capability.LoadoutImp;
import com.grimco.loadout.capability.LoadoutImp.Provider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Set;

@EventBusSubscriber
public class EventHandler
{
	
	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof EntityPlayer)
		{
			event.addCapability(Provider.NAME, new LoadoutImp.Provider());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event)
	{
		if(event.getEntity().hasCapability(CapabilityLoadout.LOADOUT, null))
		{
			ILoadout capOld = event.getOriginal().getCapability(CapabilityLoadout.LOADOUT, null);
			ILoadout capNew = event.getEntity().getCapability(CapabilityLoadout.LOADOUT, null);
			
			capOld.getLoadouts().forEach(loadout -> capNew.setLoadout(loadout));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoinedWorld(PlayerLoggedInEvent event)
	{
		if(event.player.hasCapability(CapabilityLoadout.LOADOUT,null))
		{
			EntityPlayer player = event.player;
			ILoadout loadoutCap = player.getCapability(CapabilityLoadout.LOADOUT, null);
			LoadoutManager.loadouts.forEach((key,list) -> {
				if(!loadoutCap.receivedLoadout(key))
				{
					list.forEach(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack.copy()));
					loadoutCap.setLoadout(key);
				}
			});
		}
	}
}
