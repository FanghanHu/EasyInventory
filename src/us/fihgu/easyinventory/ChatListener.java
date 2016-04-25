package us.fihgu.easyinventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import us.fihgu.toolbox.item.ItemUtils;
import us.fihgu.toolbox.json.event.HoverEventAction;
import us.fihgu.toolbox.json.event.JsonHoverEvent;
import us.fihgu.toolbox.json.text.JsonText;
import us.fihgu.toolbox.json.text.JsonTextBuilder;
import us.fihgu.toolbox.packet.PacketUtils;

public class ChatListener implements Listener
{
	final public static String KEY_WORD = "[item]";
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		
		if(player.hasPermission("us.fihgu.easyinventory.show"))
		{
			String message = event.getMessage();
			String temp = message.toLowerCase();
			if(temp.contains(KEY_WORD))
			{
				int index;
				JsonTextBuilder builder = new JsonTextBuilder();
				builder.append(new JsonText("<" + player.getDisplayName() + "> "));
				do
				{
					index = temp.indexOf(KEY_WORD, 0);
					if(index != -1)
					{
						String sub = temp.substring(0, index);
						builder.append(new JsonText(sub));
						
						ItemStack item = player.getInventory().getItemInMainHand();
						JsonText itemText;
						if(ItemUtils.notNullorAir(item))
						{
							String itemName = ItemUtils.getVisibleName(item);
							
							if(item.getAmount() > 1)
							{
								itemName += " X " + item.getAmount(); 
							}
							
							itemText = new JsonText("[" + itemName + "]");
							itemText.hoverEvent = new JsonHoverEvent(HoverEventAction.show_item, ItemUtils.toNBTCompoound(item).toString());
						}
						else
						{
							itemText = new JsonText("[No Item]");
							itemText.hoverEvent = new JsonHoverEvent(HoverEventAction.show_text, player.getDisplayName() + " is not holding an iteam.");
						}
						
						builder.append(itemText);
						
						index += KEY_WORD.length();
						temp = temp.substring(index);
					}
					
				}
				while(index >= 0 && index < temp.length());
				
				if(temp.length() > 0)
				{
					builder.append(new JsonText(temp));
				}
							
				JsonText product = builder.toJsonText();
				PacketUtils.broadcastJsonMessage(product);
				
				event.setCancelled(true);
			}
		}
		
	}
}
