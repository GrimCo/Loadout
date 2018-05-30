package com.grimco.loadout.command;

import com.google.common.collect.Lists;
import com.grimco.loadout.Loadout;
import com.grimco.loadout.LoadoutManager;

import com.grimco.loadout.LoadoutManager.Generator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandLoadout extends CommandBase
{
	private final List<String> aliases;
	
	private final List<String> commands = Lists.newArrayList("create","delete", "list", "view", "reload");
	
	public CommandLoadout()
	{
		aliases = Lists.newArrayList(Loadout.MOD_ID, "LO", "lo");
	}
	
	@Override
	public String getName()
	{
		return "lo";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		sender.sendMessage(new TextComponentTranslation("loadout.command.help.create","/lo create <name>"));
		sender.sendMessage(new TextComponentTranslation("loadout.command.help.delete", "/lo delete <name>"));
		sender.sendMessage(new TextComponentTranslation("loadout.command.help.list", "/lo list"));
		sender.sendMessage(new TextComponentTranslation("loadout.command.help.view","/lo view <name>"));
		sender.sendMessage(new TextComponentTranslation("loadout.command.help.reload", "/lo reload"));
		
		return "Requires OP to use";
	}
	
	
	
	@Override
	public List<String> getAliases()
	{
		return aliases;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		
		if (args.length < 1)
		{
			sender.sendMessage(new TextComponentTranslation("loadout.command.invalid"));
			return;
		}
		else if (args.length > 2)
		{
			sender.sendMessage(new TextComponentTranslation("loadout.command.invalid"));
			return;
		}
		
		if(args[0].equals("create"))
		{
			
			NonNullList<ItemStack> loadout=NonNullList.create();
			
			loadout.addAll(((EntityPlayer)sender).inventory.mainInventory);
			if(!loadout.isEmpty())
				loadout.removeIf(ItemStack::isEmpty);
			
			if(loadout.isEmpty())
			{
				sender.sendMessage(new TextComponentTranslation("loadout.command.no_itemstacks", args[1]));
				return;
			}
			
			LoadoutManager.Generator.addLoadout(args[1], loadout);
			sender.sendMessage(new TextComponentTranslation("loadout.command.success", args[0], loadout.size()));
		}
		
		if(args[0].equals("delete"))
		{
			if(LoadoutManager.loadouts.containsKey(args[1]))
			{
				LoadoutManager.loadouts.remove(args[1]);
				
				if(LoadoutManager.deleteJSON())
				{
					sender.sendMessage(new TextComponentTranslation("loadout.command.delete.success", args[1]));
					LoadoutManager.loadouts.forEach(Generator::addLoadout);
				}
			}
			else
			{
				sender.sendMessage(new TextComponentTranslation("loadout.command.delete.fail",args[1]));
			}
		}
		
		if(args[0].equals("list"))
		{
			if(LoadoutManager.loadouts.keySet().size()>0)
			{
				sender.sendMessage(new TextComponentTranslation("loadout.command.list.success", LoadoutManager.loadouts.keySet()));
			}
			else
				sender.sendMessage(new TextComponentTranslation("loadout.command.list.fail"));
		}
		
		if(args[0].equals("view"))
		{
			if(LoadoutManager.loadouts.containsKey(args[1]))
			{
				sender.sendMessage(new TextComponentTranslation("loadout.command.view.success", args[1]));
				LoadoutManager.loadouts.get(args[1]).forEach(stack -> sender.sendMessage(new TextComponentTranslation("loadout.command.view.item",stack.getDisplayName(), stack.getMetadata(), stack.getCount(), (stack.hasTagCompound() ? stack.getTagCompound():""))));
			}
			else
			{
				sender.sendMessage(new TextComponentTranslation("loadout.command.view.fail", args[1]));
			}
		}
		
		if(args[0].equals("reload"))
		{
			LoadoutManager.loadouts.clear();
			LoadoutManager.Parser.loadLoadouts();
			
			sender.sendMessage(new TextComponentTranslation("loadout.command.reload",LoadoutManager.loadouts.size()));
		}
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 4;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		return args.length <2 ? commands : Collections.emptyList();
	}
	
}
