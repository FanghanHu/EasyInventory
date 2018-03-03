package us.fihgu.easyinventory;

import java.util.LinkedList;

import org.bukkit.inventory.ItemStack;

/**
 * A group of items that's similar to each other.
 */
public class ItemCluster
{
	public LinkedList<ItemStack> list = new LinkedList<>();
	
	/**
	 * the first item 
	 */
	public ItemStack head = null;
	
	public ItemCluster(ItemStack item)
	{
		this.list.add(item);
		this.head = item;
	}
}
