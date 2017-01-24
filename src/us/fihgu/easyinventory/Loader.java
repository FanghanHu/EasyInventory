package us.fihgu.easyinventory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import us.fihgu.easyinventory.commands.View;

public class Loader extends JavaPlugin
{
	public static Loader instance = null;
	
	@Override
	public void onEnable()
	{
		Loader.instance = this;
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		
		System.out.print("fihgu's EasyInventory is Enabled.");
		
		this.getCommand("view").setExecutor(new View());
	}
}
