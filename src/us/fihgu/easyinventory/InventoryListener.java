package us.fihgu.easyinventory;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListener implements Listener
{
	private final static LinkedList<String> allowedNames = new LinkedList<String>();
	
	static
	{
		allowedNames.add("container.chest");
		allowedNames.add("container.chestDouble");
		allowedNames.add("container.inventory");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(InventoryClickEvent event)
	{
		
		if (!event.isCancelled())
		{			
			final HumanEntity player = event.getWhoClicked();
			if(player.hasPermission("us.fihgu.easyinventory.sort"))
			{
				ClickType click = event.getClick();

				if (click == ClickType.MIDDLE && event.getClickedInventory() != null)
				{
					InventoryType type = event.getClickedInventory().getType();
					
					boolean allow = false;
					if(type == InventoryType.PLAYER)
					{
						String title = event.getClickedInventory().getTitle();
						for(String temp : allowedNames)
						{
							if(temp.equals(title))
							{
								allow = true;
								break;
							}
						}
					}
					
					if(type == InventoryType.CHEST)
					{
						String title = event.getClickedInventory().getTitle();
						
						for(String temp : allowedNames)
						{
							if(temp.equals(title))
							{
								allow = true;
								break;
							}
						}
					}
					
					if(type == InventoryType.ENDER_CHEST)
					{
						allow = true;
					}
					
					if(!allow)
					{
						return;
					}
					
					final Inventory inv = event.getClickedInventory();

					BukkitRunnable task = new BukkitRunnable()
					{
						@Override
						public void run()
						{
							player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							sortInventory(inv);
						}
					};
					task.runTaskLater(Loader.instance, 1);
				}
			}		
		}
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
		final Player player = event.getPlayer();
		if (!player.hasPermission("us.fihgu.easyinventory.refill"))
		{
			return;
		}
		final ItemStack old_main = player.getInventory().getItemInMainHand().clone();
		final ItemStack old_off = player.getInventory().getItemInOffHand().clone();

		if (this.hasItem(old_main) || this.hasItem(old_off))
		{
			BukkitRunnable task = new BukkitRunnable()
			{

				@Override
				public void run()
				{
					if (player.isOnline())
					{
						ItemStack main = player.getInventory().getItemInMainHand();
						ItemStack off = player.getInventory().getItemInOffHand();

						if (hasItem(old_main) && !hasItem(main))
						{
							ItemStack temp = popStack(player.getInventory(), old_main);

							if (temp != null)
								player.getInventory().setItemInMainHand(temp);
						}

						if (hasItem(old_off) && !hasItem(off))
						{
							ItemStack temp = popStack(player.getInventory(), old_off);

							if (temp != null)
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
		for (int i = 0; i < inv.getSize(); i++)
		{
			ItemStack temp = inv.getItem(i);
			if (temp != null && temp.getTypeId() == item.getTypeId())
			{
				inv.setItem(i, null);
				return temp;
			}
		}

		return null;
	}

	/**
	 * merge itemStacks, sort items by type
	 */
	private void sortInventory(Inventory inv)
	{
		LinkedList<ItemCluster> sortBySimilarity = new LinkedList<ItemCluster>();

		outer: for (ItemStack item : inv.getStorageContents())
		{
			if (this.hasItem(item))
			{
				for (ItemCluster cluster : sortBySimilarity)
				{
					// search for similar item to merge
					if (cluster.head.isSimilar(item))
					{
						// attempt to merge
						for (ItemStack temp : cluster.list)
						{
							int roomLeft = temp.getMaxStackSize() - temp.getAmount();
							if (roomLeft > 0)
							{
								int moveAmount = Math.min(roomLeft, item.getAmount());
								int AmountLeft = item.getAmount() - moveAmount;
								temp.setAmount(temp.getAmount() + moveAmount);

								if (AmountLeft > 0)
								{
									item.setAmount(AmountLeft);
								} else
								{
									continue outer;
								}
							}
						}
						// not fully merged into other stacks
						cluster.list.addFirst(item);
						continue outer;
					}
				}

				// can not find similar ItemCluster
				sortBySimilarity.add(new ItemCluster(item));
			}
		}

		ArrayList<ItemCluster> sortByType = new ArrayList<ItemCluster>();

		outer: for (ItemCluster cluster : sortBySimilarity)
		{
			boolean found = false;
			for (int i = 0; i < sortByType.size(); i++)
			{
				ItemCluster temp = sortByType.get(i);
				if (temp.head.getType() == cluster.head.getType())
				{
					found = true;
				}
				else if(found == true)
				{
					sortByType.add(i, cluster);
					continue outer;
				}
			}
			sortByType.add(cluster);
		}

		LinkedList<ItemStack> product = new LinkedList<ItemStack>();
		for (ItemCluster cluster : sortByType)
		{
			product.addAll(cluster.list);
		}

		if (product.size() > inv.getSize())
		{
			System.err.println("EasyInventory: can not sort inventory, product inventory size is bigger than original");
		} else
		{
			ItemStack[] newContent = new ItemStack[inv.getStorageContents().length];
			inv.setStorageContents(newContent);
			for (int i = 0; i < product.size(); i++)
			{
				ItemStack item = product.get(i);
				inv.setItem(i, item);
			}
		}
	}
}
