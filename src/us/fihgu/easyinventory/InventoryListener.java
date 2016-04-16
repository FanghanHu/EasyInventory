package us.fihgu.easyinventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class InventoryListener implements Listener
{
	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		ClickType click = event.getClick();
		
		if(click == ClickType.MIDDLE)
		{
			System.out.println("Clicked");
		}
	}
	
	public void onDrag(InventoryDragEvent event)
	{
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{		
		this.handleEvent(event);
	}
	
	@EventHandler
	public void onInteract(PlayerItemConsumeEvent event)
	{		
		this.handleEvent(event);
	}
	
	@EventHandler
	public void onInteract(PlayerItemDamageEvent event)
	{		
		this.handleEvent(event);
	}
	
	private void handleEvent(PlayerEvent event)
	{
		Player player = event.getPlayer();
		ItemStack old_main = player.getInventory().getItemInMainHand().clone();
		ItemStack old_off = player.getInventory().getItemInOffHand().clone();
		
		if(this.hasItem(old_main) || this.hasItem(old_off))
		{
			BukkitRunnable task = new BukkitRunnable(){

				@Override
				public void run()
				{
					if(player.isOnline())
					{
						ItemStack main = player.getInventory().getItemInMainHand();
						ItemStack off = player.getInventory().getItemInOffHand();
						
						if(hasItem(old_main) && !hasItem(main))
						{
							ItemStack temp = popStack(player.getInventory(), old_main);
							
							if(temp != null)
								player.getInventory().setItemInMainHand(temp);
						}
						
						if(hasItem(old_off) && !hasItem(off))
						{
							ItemStack temp = popStack(player.getInventory(), old_off);
							
							if(temp != null)
								player.getInventory().setItemInOffHand(temp);
						}
					}
				}
				
			};
			task.runTaskLater(Loader.instance, 1);
		}
	}
	
	private boolean hasItem(ItemStack item)
	{
		return item != null && item.getType() != Material.AIR;
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack popStack(Inventory inv, ItemStack item)
	{
		for(int i = 0; i < inv.getSize(); i++)
		{
			ItemStack temp = inv.getItem(i);
			if(temp != null && temp.getTypeId() == item.getTypeId())
			{
				inv.setItem(i, null);
				return temp;
			}
		}
		
		return null;
	}
}
