package net.herobrine.clashroyale;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;

public class Knight extends Class {

	public Knight(UUID uuid) {
		super(uuid, ClassTypes.KNIGHT);
	}

	@Override
	public void onStart(Player player) {
		player.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.GOLD_HELMET);
		ItemMeta helmetMeta = helmet.getItemMeta();
		helmetMeta.setDisplayName(ChatColor.BLUE + "Knight Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.GOLD_CHESTPLATE);
		ItemMeta chestplateMeta = chestplate.getItemMeta();
		chestplateMeta.setDisplayName(ChatColor.BLUE + "Knight Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.GOLD_LEGGINGS);
		ItemMeta leggingsMeta = leggings.getItemMeta();
		leggingsMeta.setDisplayName(ChatColor.BLUE + "Knight Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
		ItemMeta bootsMeta = boots.getItemMeta();
		bootsMeta.setDisplayName(ChatColor.BLUE + "Knight Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack sword = new ItemStack(Material.GOLD_SWORD);
		ItemMeta swordMeta = sword.getItemMeta();
		swordMeta.setDisplayName(ChatColor.BLUE + "Knight Sword");
		swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
		swordMeta.spigot().setUnbreakable(true);
		swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		swordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		sword.setItemMeta(swordMeta);

		player.getInventory().setItem(0, sword);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		player.updateInventory();
	}
}
