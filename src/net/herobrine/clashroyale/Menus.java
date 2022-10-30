package net.herobrine.clashroyale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.ItemTypes;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.Games;

public class Menus {
	public static void applyClassSelector(Player player) {
		Inventory classSelector = Bukkit.createInventory(null, 27,
				ChatColor.translateAlternateColorCodes('&', "&bBattle Clash &7: &aClass Selector"));
		for (ClassTypes type : ClassTypes.values()) {
			if (type.getGame().equals(Games.CLASH_ROYALE)) {

				ItemStack is = new ItemStack(type.getMaterial());
				ItemMeta isMeta = is.getItemMeta();
				isMeta.setDisplayName(type.getDisplay());
				isMeta.setLore(Arrays.asList(type.getDescription()));
				is.setItemMeta(isMeta);

				if (type.isUnlockable() && !HerobrinePVPCore.getFileManager().isItemUnlocked(ItemTypes.CLASS,
						type.toString(), player.getUniqueId())) {
					List<String> lore = new ArrayList<>();

					lore.addAll(Arrays.asList(type.getDescription()));

					lore.add("");
					lore.add(ChatColor.RED + "You do not have this class unlocked!");
					isMeta.setLore(lore);
					is.setItemMeta(isMeta);
				}

				classSelector.addItem(is);
			}

		}
		player.openInventory(classSelector);
	}
}
