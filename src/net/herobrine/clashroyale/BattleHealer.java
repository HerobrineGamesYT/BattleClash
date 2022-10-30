package net.herobrine.clashroyale;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;

public class BattleHealer extends Class {

	public BattleHealer(UUID uuid) {
		super(uuid, ClassTypes.HEALER);

	}

	@Override
	public void onStart(Player player) {
		player.getInventory().clear();

		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.YELLOW);
		helmetMeta.setDisplayName(ChatColor.GOLD + "Healer Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.YELLOW);
		chestplateMeta.setDisplayName(ChatColor.GOLD + "Healer Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.YELLOW);
		leggingsMeta.setDisplayName(ChatColor.GOLD + "Healer Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.YELLOW);
		bootsMeta.setDisplayName(ChatColor.GOLD + "Healer Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack bow = new ItemStack(Material.GOLD_SWORD);
		ItemMeta bowMeta = bow.getItemMeta();

		bowMeta.setDisplayName(ChatColor.GOLD + "Battle Healer Sword");
		bowMeta.spigot().setUnbreakable(true);
		bowMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		bowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bow.setItemMeta(bowMeta);

		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		player.getInventory().setItem(0, bow);
		player.updateInventory();
	}

}
