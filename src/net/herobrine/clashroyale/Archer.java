package net.herobrine.clashroyale;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;

public class Archer extends Class {
	public Archer(UUID uuid) {
		super(uuid, ClassTypes.ARCHER);
	}

	private ArrayList<Location> arrowLocations;

	@Override
	public void onStart(Player player) {
		player.getInventory().clear();

		arrowLocations = new ArrayList<>();
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.MAROON);
		helmetMeta.setDisplayName(ChatColor.RED + "Archer Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.MAROON);
		chestplateMeta.setDisplayName(ChatColor.RED + "Archer Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.MAROON);
		leggingsMeta.setDisplayName(ChatColor.RED + "Archer Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.MAROON);
		bootsMeta.setDisplayName(ChatColor.RED + "Archer Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();

		bowMeta.setDisplayName(ChatColor.RED + "Archer Bow");
		bowMeta.spigot().setUnbreakable(true);
		bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 9, true);
		bowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bow.setItemMeta(bowMeta);

		ItemStack arrow = new ItemStack(Material.ARROW);

		player.getInventory().setItem(0, bow);
		player.getInventory().setItem(27, arrow);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		player.updateInventory();

	}

	@EventHandler

	public void onLaunch(ProjectileLaunchEvent e) {

		if (e.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getEntity();

			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();

				if (player.getUniqueId().equals(this.getUUID())) {

					arrowLocations.add(player.getLocation());
					new BukkitRunnable() {

						@Override
						public void run() {

							if (arrowLocations.get(0).distance(arrow.getLocation()) > ClassTypes.ARCHER.getBaseDamage()
									|| arrow.isOnGround() || arrow.isDead()) {
								arrow.remove();
								arrowLocations.remove(0);

								cancel();
							}

						}

					}.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 1L);

				}
			}
		}

	}
}
