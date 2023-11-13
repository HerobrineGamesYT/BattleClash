package net.herobrine.clashroyale.classes;

import java.util.UUID;

import net.herobrine.clashroyale.ClashRoyaleGame;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;

public class Bandit extends Class {

	public Bandit(UUID uuid) {
		super(uuid, ClassTypes.BANDIT);
	}

	@Override
	public void onStart(Player player) {
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.GREEN);
		helmetMeta.setDisplayName(ChatColor.GREEN + "Bandit Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.GREEN);
		chestplateMeta.setDisplayName(ChatColor.GREEN + "Bandit Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.GREEN);
		leggingsMeta.setDisplayName(ChatColor.GREEN + "Bandit Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.GREEN);
		bootsMeta.setDisplayName(ChatColor.GREEN + "Bandit Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack stick = new ItemStack(Material.STICK);
		ItemMeta stickMeta = stick.getItemMeta();
		stickMeta.setDisplayName(ChatColor.DARK_GREEN + "Bandit's Stick");
		stickMeta.addEnchant(Enchantment.DAMAGE_ALL, 9, true);
		stick.setItemMeta(stickMeta);

		ItemStack dasher = new ItemStack(Material.WOOL, 1, (byte) 13);
		ItemMeta dasherMeta = dasher.getItemMeta();
		dasherMeta.setDisplayName(ChatColor.DARK_GREEN + "Dash");
		dasher.setItemMeta(dasherMeta);

		ItemStack dasherCooldown = new ItemStack(Material.WOOL, 30);
		ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
		dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Dash (On Cooldown)");
		player.getInventory().clear();
		player.getInventory().setItem(0, stick);
		player.getInventory().setItem(1, dasher);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		PotionEffect banditSpeed = PotionEffectType.SPEED.createEffect(10000000, 1);
		player.addPotionEffect(banditSpeed);
		player.updateInventory();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = (Player) e.getPlayer();
		if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
			if (player.getItemInHand().getItemMeta() != null) {
				if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName()
						.equals(ChatColor.DARK_GREEN + "Dash")) {

					if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
							|| e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						Vector dir = player.getLocation().getDirection();

						dir.normalize();
						dir.multiply(2);
						PotionEffect banditResistance = PotionEffectType.DAMAGE_RESISTANCE.createEffect(5, 200);

						player.addPotionEffect(banditResistance);
						player.setVelocity(dir);
						ItemStack dasherCooldown = new ItemStack(Material.WOOL, 30);
						ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
						dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Dash (On Cooldown)");
						dasherCooldown.setItemMeta(dasherCooldownMeta);
						player.getInventory().setItem(1, dasherCooldown);

						ClashRoyaleGame.banditCooldown(player);
						ClashRoyaleGame.collisionChecker(player);
					}

				}

			}
		}
	}
}
