package com.grimco.loadout.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityLoadout
{
	@CapabilityInject(ILoadout.class)
	public static Capability<ILoadout> LOADOUT = null;
	
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(ILoadout.class, new IStorage<ILoadout>()
		{
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<ILoadout> capability, ILoadout instance, EnumFacing side)
			{
				NBTTagList loadouts = new NBTTagList();
				instance.getLoadouts().forEach(loadout -> loadouts.appendTag(new NBTTagString(loadout)));
				return loadouts;
			}
			
			@Override
			public void readNBT(Capability<ILoadout> capability, ILoadout instance, EnumFacing side, NBTBase nbt)
			{
				((NBTTagList) nbt).forEach(tag -> ((LoadoutImp)instance).setLoadout(((NBTTagString)tag).getString()));
			}
		}, LoadoutImp::new);
	}
}
