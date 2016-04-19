package us.fihgu.easyinventory.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class View implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length == 1 && sender instanceof Player)
		{
			Player player = Bukkit.getPlayer(args[0]);
			Player commandSender = (Player)sender;
			if(player != null)
			{
				Inventory inv = player.getInventory();
				commandSender.openInventory(inv);
				return true;
			}
		}
		
		return false;
	}

}
