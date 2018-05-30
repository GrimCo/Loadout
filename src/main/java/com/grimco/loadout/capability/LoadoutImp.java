package com.grimco.loadout.capability;

import com.grimco.loadout.Loadout;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoadoutImp implements ILoadout
{
	
	
	protected Set<String> loadouts = new HashSet<>();
	
	public LoadoutImp()
	{
		loadouts = new HashSet<>();
	}
	
	@Override
	public boolean receivedLoadout(String loadout)
	{
		return loadouts.contains(loadout);
	}
	
	@Override
	public void setLoadout(String loadout)
	{
		loadouts.add(loadout);
	}
	
	@Override
	public Set<String> getLoadouts()
	{
		return loadouts;
	}
	
	@Override
	public void resetLoadouts()
	{
		loadouts.clear();
	}
	
	public static class Provider implements ICapabilitySerializable<NBTTagList>
	{
		public static final ResourceLocation NAME = new ResourceLocation(Loadout.MOD_ID, "loadouts");
		
		private final ILoadout cap = new LoadoutImp();
		
		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CapabilityLoadout.LOADOUT;
		}
		
		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
		{
			if(capability == CapabilityLoadout.LOADOUT)
				return (CapabilityLoadout.LOADOUT).cast(cap);
			
			return null;
		}
		
		@Override
		public NBTTagList serializeNBT()
		{
			NBTTagList tagList = new NBTTagList();
			
			cap.getLoadouts().forEach(loadout -> tagList.appendTag(new NBTTagString(loadout)));
			
			return tagList;
		}
		
		@Override
		public void deserializeNBT(NBTTagList nbt)
		{
			nbt.forEach(tag -> cap.setLoadout(((NBTTagString)tag).getString()));
		}
	}
}
