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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;

public class Lumberjack extends Class {
	public Lumberjack(UUID uuid) {
		super(uuid, ClassTypes.LUMBERJACK);
	}

	@Override
	public void onStart(Player player) {
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.FUCHSIA);
		helmetMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Lumberjack Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.FUCHSIA);
		chestplateMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Lumberjack Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.FUCHSIA);
		leggingsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Lumberjack Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.FUCHSIA);
		bootsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Lumberjack Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack stick = new ItemStack(Material.IRON_AXE);
		ItemMeta stickMeta = stick.getItemMeta();
		stickMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Lumberjack Axe");
		stickMeta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
		stickMeta.spigot().setUnbreakable(true);
		stickMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stickMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		stick.setItemMeta(stickMeta);

		player.getInventory().clear();
		player.getInventory().setItem(0, stick);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		PotionEffect banditSpeed = PotionEffectType.SPEED.createEffect(10000000, 1);
		player.addPotionEffect(banditSpeed);
		player.updateInventory();

	}

}
