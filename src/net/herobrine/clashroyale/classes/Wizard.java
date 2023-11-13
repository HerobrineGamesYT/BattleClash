package net.herobrine.clashroyale.classes;

import java.util.ArrayList;
import java.util.UUID;

import net.herobrine.clashroyale.ClashRoyaleGame;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;

public class Wizard extends Class {
	public ArrayList<UUID> hasShot = new ArrayList<>();

	public Wizard(UUID uuid) {
		super(uuid, ClassTypes.WIZARD);

	}

	@Override
	public void onStart(Player player) {
		player.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.ORANGE);
		helmetMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wizard Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.ORANGE);
		chestplateMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wizard Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.ORANGE);
		leggingsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wizard Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.ORANGE);
		bootsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wizard Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack fireball = new ItemStack(Material.FIREBALL);
		ItemMeta fireballMeta = fireball.getItemMeta();
		fireballMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Fireball (Right Click To Shoot!)");

		fireball.setItemMeta(fireballMeta);

		player.getInventory().setItem(0, fireball);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		player.updateInventory();
		player.setAllowFlight(true);

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = (Player) e.getPlayer();

		if (player.getUniqueId() == this.getUUID() && !hasShot.contains(player.getUniqueId())) {

			if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
				if (player.getItemInHand().getItemMeta() != null) {
					if (player.getItemInHand().getItemMeta().getDisplayName()
							.equals(ChatColor.LIGHT_PURPLE + "Fireball (Right Click To Shoot!)")) {

						if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
								|| e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
										&& !hasShot.contains(player.getUniqueId())) {
							hasShot.add(player.getUniqueId());
							ClashRoyaleGame.wizardCooldown(player);
							Fireball fireball = player.launchProjectile(Fireball.class);
							fireball.setShooter(player);
							fireball.setYield(3F);
							fireball.setCustomName(player.getName());
							fireball.setCustomNameVisible(false);
							fireball.setFireTicks(0);

							fireball.setIsIncendiary(false);
							fireball.setBounce(false);
							fireball.playEffect(EntityEffect.WITCH_MAGIC);
							player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1f, 1f);

							player.sendMessage(ChatColor.LIGHT_PURPLE + "You shot a fireball!");
							hasShot.remove(player.getUniqueId());
						}

					}

				}
			}
		} else {
			return;
		}

	}

	@EventHandler
	public void onDoubleJump(PlayerToggleFlightEvent e) {

		Player player = e.getPlayer();

		if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)
				|| player.isFlying()) {

			e.setCancelled(false);
			return;

		} else {

			if (player.getUniqueId() == this.getUUID()) {
				e.setCancelled(true);
				player.setAllowFlight(false);
				player.setFlying(false);
				player.setVelocity(player.getLocation().getDirection().multiply(2).setY(1));
				player.sendMessage(ChatColor.GREEN + "Used " + ChatColor.GOLD + "Double Jump" + ChatColor.GREEN + "!");
				ClashRoyaleGame.doubleJumpCooldown(player);
			}

		}

	}

}
