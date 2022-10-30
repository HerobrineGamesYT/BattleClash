package net.herobrine.clashroyale;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.herobrine.clashroyale.beta.Region;
import net.herobrine.clashroyale.beta.Tower;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.GameType;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import net.herobrine.gamecore.Teams;

public class Fisherman extends Class {
	HashMap<UUID, Boolean> hasHit = new HashMap<>();

	public Fisherman(UUID uuid) {
		super(uuid, ClassTypes.FISHERMAN);
	}

	@Override
	public void onStart(Player player) {
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.AQUA);
		helmetMeta.setDisplayName(ChatColor.AQUA + "Fisherman Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.AQUA);
		chestplateMeta.setDisplayName(ChatColor.AQUA + "Fisherman Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.AQUA);
		leggingsMeta.setDisplayName(ChatColor.AQUA + "Fisherman Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.AQUA);
		bootsMeta.setDisplayName(ChatColor.AQUA + "Fisherman Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack stick = new ItemStack(Material.COOKED_FISH);
		ItemMeta stickMeta = stick.getItemMeta();
		stickMeta.setDisplayName(ChatColor.AQUA + "Fish");
		stickMeta.addEnchant(Enchantment.DAMAGE_ALL, 9, true);
		stick.setItemMeta(stickMeta);

		ItemStack dasher = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta dasherMeta = dasher.getItemMeta();
		dasherMeta.setDisplayName(ChatColor.AQUA + "Fisherman Hook");
		dasher.setItemMeta(dasherMeta);

		player.getInventory().clear();
		player.getInventory().setItem(0, stick);
		player.getInventory().setItem(1, dasher);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
		player.updateInventory();
	}

	public void removeHooked(UUID uuid) {

		new BukkitRunnable() {
			int timer = 5;

			@Override
			public void run() {

				if (timer == 0) {
					GameListener.wasHooked.remove(uuid, GameListener.wasHooked.get(uuid));
					this.cancel();

				}

				timer--;
			}

		}.runTaskTimer(ClashRoyaleMain.getInstance(), 0, 20L);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = (Player) e.getPlayer();
		if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
			if (player.getItemInHand().getItemMeta() != null) {
				if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName()
						.equals(ChatColor.AQUA + "Fisherman Hook")) {
					e.setCancelled(true);
					if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
						Arena arena = Manager.getArena(player);
						ClashRoyaleGame.fishermanAbilityCooldown(player);
						new BukkitRunnable() {
							double t = 0;
							Location loc = player.getLocation();
							Vector direction = loc.getDirection().normalize();

							@SuppressWarnings("deprecation")
							public void run() {
								t = t + 1;

								double x = direction.getX() * t;
								double y = direction.getY() * t + 1.5;
								double z = direction.getZ() * t;
								loc.add(x, y, z);
								if (loc.getBlock().getType() != Material.AIR) {
									hasHit.clear();
									ClashRoyaleGame.removeArmorStands(player);

									if (arena.getType().equals(GameType.CLASH_ROYALE)) {
										Block block = loc.getBlock();
										World world = block.getWorld();
										if (Manager.isArenaWorld(world)) {

											if (Manager.getArena(world).getGame(Manager.getArena(world).getID())
													.equals(Games.CLASH_ROYALE)) {

												ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();
												ClashRoyaleGame game = null;

												game = Manager.getArena(world).getBattleClash();

												for (String b : plugin.getConfig().getConfigurationSection("towers")
														.getKeys(false)) {

													if (!b.equals("world") && !b.equals("arena")) {

														Tower tower = game.getTower().get(b);

														Region region = new Region(tower.getRegionLocations()[0],
																tower.getRegionLocations()[1]);

														Arena arena = Manager
																.getArena(tower.getRegionLocations()[1].getWorld());

														if (region.blockInLocation(block)) {

															player.sendMessage(
																	ChatColor.BLUE + "You have hooked onto a tower!");

															Vector direction = block.getLocation().toVector()
																	.subtract(player.getLocation().toVector())
																	.normalize();

															if (tower.getTeam().equals(Teams.BLUE)) {
																direction.setX(direction.getX() * 1);
																direction.setZ(direction.getZ() * 1);
															} else {
																direction.setX(direction.getX() * 1);
																direction.setZ(direction.getZ() * 1);
															}

															direction.multiply(2);
															player.setVelocity(direction);
														}

													}
												}
											}
										}
									}

									this.cancel();

								}

								else {
									ArmorStand bat = (ArmorStand) player.getWorld().spawnEntity(loc,
											EntityType.ARMOR_STAND);
									bat.setCustomName(player.getName());
									bat.setCustomNameVisible(false);
									bat.setVisible(false);
									bat.setMaxHealth(100);
									bat.setHealth(100);
									bat.setMarker(true);

									player.getWorld().playEffect(loc, Effect.HAPPY_VILLAGER, 5);

									for (Entity ent : bat.getNearbyEntities(.6, 1, .6)) {
										if (ent instanceof Player) {

											Player targetPlayer = (Player) ent;

											if (arena.getTeam(player) != arena.getTeam(targetPlayer)) {

												if (!hasHit.containsKey(player.getUniqueId())) {

													hasHit.put(player.getUniqueId(), true);

													// hook player
													Vector direction = targetPlayer.getLocation().toVector()
															.subtract(player.getLocation().toVector()).normalize();
													direction.setX(direction.getX() * -1);
													direction.setZ(direction.getZ() * -1);
													direction.multiply(2);
													targetPlayer.setVelocity(direction);
													PotionEffect banditSpeed = PotionEffectType.SLOW.createEffect(120,
															4);
													targetPlayer.addPotionEffect(banditSpeed);
													player.sendMessage(ChatColor.AQUA + "You pulled "
															+ targetPlayer.getName() + "!");
													GameListener.customDeathCause.put(targetPlayer.getUniqueId(),
															CustomDeathCause.FISHERMAN_HOOK);

													EntityDamageEvent event = new EntityDamageEvent(targetPlayer,
															DamageCause.CUSTOM, 1.0);

													targetPlayer.setLastDamageCause(event);
													targetPlayer.damage(1);
													targetPlayer.setLastDamageCause(event);
													GameListener.lastAbilityAttacker.put(targetPlayer.getUniqueId(),
															player.getUniqueId());

													if (!GameListener.customDeathCause
															.containsKey(targetPlayer.getUniqueId())
															|| !GameListener.customDeathCause
																	.get(targetPlayer.getUniqueId())
																	.equals(CustomDeathCause.FISHERMAN_HOOK)) {
														GameListener.customDeathCause.put(targetPlayer.getUniqueId(),
																CustomDeathCause.FISHERMAN_HOOK);
													}
													GameListener.customDeathCause.put(targetPlayer.getUniqueId(),
															CustomDeathCause.FISHERMAN_HOOK);
													GameListener.wasHooked.put(targetPlayer.getUniqueId(),
															player.getUniqueId());

													removeHooked(targetPlayer.getUniqueId());

												} else {
													return;
												}

											}

										}

										else if (ent instanceof Skeleton) {

											Skeleton skeleton = (Skeleton) ent;
											Player skeletonOwner = Bukkit.getPlayerExact(skeleton.getCustomName());

											if (arena.getTeam(player) != arena.getTeam(skeletonOwner)) {
												// hook skeleton
												hasHit.put(player.getUniqueId(), true);
												Vector direction = skeleton.getLocation().toVector()
														.subtract(player.getLocation().toVector()).normalize();
												direction.setX(direction.getX() * -1);
												direction.setY(direction.getY() * -1);
												direction.setZ(direction.getZ() * -1);
												direction.multiply(2);
												skeleton.setVelocity(direction);
												skeleton.damage(1.0);
												PotionEffect banditSpeed = PotionEffectType.SLOW.createEffect(120, 4);
												skeleton.addPotionEffect(banditSpeed);
												player.sendMessage(ChatColor.AQUA + "You pulled a skeleton!");

											}

										}

										bat.remove();
									}

									bat.remove();
								}

								loc.subtract(x, y, z);

								if (t > 15) {
									hasHit.clear();
									ClashRoyaleGame.removeArmorStands(player);
									this.cancel();

								}

							}
						}.runTaskTimer(ClashRoyaleMain.getInstance(), 0, 1L);
					}

				}

			}
		}
	}
}
