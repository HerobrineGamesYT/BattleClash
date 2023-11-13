package net.herobrine.clashroyale.classes;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import net.herobrine.clashroyale.ClashRoyaleGame;
import net.herobrine.clashroyale.ClashRoyaleMain;
import net.herobrine.clashroyale.CustomDeathCause;
import net.herobrine.clashroyale.GameListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.herobrine.clashroyale.beta.Cannon;
import net.herobrine.clashroyale.beta.Colorize;
import net.herobrine.clashroyale.beta.Region;
import net.herobrine.clashroyale.beta.Tower;
import net.herobrine.clashroyale.beta.Tower.TowerTypes;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.GameType;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import net.herobrine.gamecore.Teams;

public class Witch extends Class {
	HashMap<UUID, Location> skeletonTargets = new HashMap<>();
	HashMap<UUID, Double> skeletonTargetDistance = new HashMap<>();
	HashMap<UUID, Boolean> hasHit = new HashMap<>();

	public Witch(UUID uuid) {
		super(uuid, ClassTypes.WITCH);
	}

	@Override
	public void onStart(Player player) {
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.PURPLE);
		helmetMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch Helmet");
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateMeta.setColor(Color.PURPLE);
		chestplateMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch Chestplate");
		chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		chestplateMeta.spigot().setUnbreakable(true);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		chestplate.setItemMeta(chestplateMeta);

		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsMeta.setColor(Color.PURPLE);
		leggingsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch Leggings");
		leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		leggingsMeta.spigot().setUnbreakable(true);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		leggings.setItemMeta(leggingsMeta);

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(Color.PURPLE);
		bootsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch Boots");
		bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		bootsMeta.spigot().setUnbreakable(true);
		bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		boots.setItemMeta(bootsMeta);

		ItemStack stick = new ItemStack(Material.STICK);
		ItemMeta stickMeta = stick.getItemMeta();
		stickMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch's Wand");
		stick.setItemMeta(stickMeta);

		ItemStack dasher = new ItemStack(Material.INK_SACK, 1, (byte) 15);
		ItemMeta dasherMeta = dasher.getItemMeta();
		dasherMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Summon Skeletons (Right Click)");
		dasher.setItemMeta(dasherMeta);

		ItemStack dasherCooldown = new ItemStack(Material.SULPHUR, 30);
		ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
		dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Summon Skeletons (On Cooldown)");
		player.getInventory().clear();
		player.getInventory().setItem(0, stick);
		player.getInventory().setItem(1, dasher);
		player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });

		player.updateInventory();
	}

	public void setSkeletonTarget(Skeleton skeleton, Player target) {skeleton.setTarget(target);}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = (Player) e.getPlayer();
		if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
			if (player.getItemInHand().getItemMeta() != null) {
				if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName()
						.equals(ChatColor.LIGHT_PURPLE + "Summon Skeletons (Right Click)")) {

					if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
							|| e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

						// first set cooldown item to prevent further clicks
						ItemStack dasherCooldown = new ItemStack(Material.SULPHUR, 10);
						ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
						dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Summon Skeletons (On Cooldown)");
						dasherCooldown.setItemMeta(dasherCooldownMeta);
						player.getInventory().setItem(1, dasherCooldown);
						player.updateInventory();

						// witch cooldown
						ClashRoyaleGame.witchCooldown(player);

						// summon skeletons
						Location spawnLoc1 = new Location(player.getLocation().getWorld(), player.getLocation().getX(),
								player.getLocation().getY(), player.getLocation().getZ() + 1);
						Location spawnLoc2 = new Location(player.getLocation().getWorld(), player.getLocation().getX(),
								player.getLocation().getY(), player.getLocation().getZ() + -1);
						Location spawnLoc3 = new Location(player.getLocation().getWorld(),
								player.getLocation().getX() - 1, player.getLocation().getY(),
								player.getLocation().getZ() + 1);
						Location spawnLoc4 = new Location(player.getLocation().getWorld(),
								player.getLocation().getX() + 1, player.getLocation().getY(),
								player.getLocation().getZ() + -1);
						ItemStack skeletonSword = new ItemStack(Material.IRON_SWORD);

						ItemMeta swordMeta = skeletonSword.getItemMeta();

						swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
						skeletonSword.setItemMeta(swordMeta);
						Skeleton skeleton = (Skeleton) player.getWorld().spawnEntity(spawnLoc1, EntityType.SKELETON);
						skeleton.setCustomName(player.getName());
						skeleton.setCustomNameVisible(false);
						skeleton.addPotionEffect(PotionEffectType.SPEED.createEffect(10000000, 1));
						skeleton.getEquipment().setItemInHand(skeletonSword);
						skeleton.getEquipment().setItemInHandDropChance(0.0F);
						skeleton.setHealth(1.0);
						Skeleton skeleton2 = (Skeleton) player.getWorld().spawnEntity(spawnLoc2, EntityType.SKELETON);
						skeleton2.setCustomName(player.getName());
						skeleton2.setCustomNameVisible(false);
						skeleton2.addPotionEffect(PotionEffectType.SPEED.createEffect(10000000, 1));
						skeleton2.getEquipment().setItemInHand(skeletonSword);
						skeleton2.getEquipment().setItemInHandDropChance(0.0F);
						skeleton2.setHealth(1.0);
						Skeleton skeleton3 = (Skeleton) player.getWorld().spawnEntity(spawnLoc3, EntityType.SKELETON);
						skeleton3.setCustomName(player.getName());
						skeleton3.setCustomNameVisible(false);
						skeleton3.addPotionEffect(PotionEffectType.SPEED.createEffect(10000000, 1));
						skeleton3.getEquipment().setItemInHand(skeletonSword);
						skeleton3.getEquipment().setItemInHandDropChance(0.0F);
						skeleton3.setHealth(1.0);
						Skeleton skeleton4 = (Skeleton) player.getWorld().spawnEntity(spawnLoc4, EntityType.SKELETON);
						skeleton4.setCustomName(player.getName());
						skeleton4.setCustomNameVisible(false);
						skeleton4.setHealth(1.0);
						skeleton4.addPotionEffect(PotionEffectType.SPEED.createEffect(10000000, 1));
						skeleton4.getEquipment().setItemInHand(skeletonSword);
						skeleton4.getEquipment().setItemInHandDropChance(0.0F);
						int i = 0;
						Arena arena = Manager.getArena(player);
						Teams playerTeam = arena.getTeam(player);

						// targeting logic--more in gamelistener EntityTargetEvent
						for (UUID uuid : arena.getPlayers()) {

							if (i == arena.getPlayers().size()) {
								break;
							} else {
								Player player2 = Bukkit.getPlayer(uuid);
								Teams player2Team = arena.getTeam(player2);
								if (playerTeam != player2Team) skeletonTargets.put(player2.getUniqueId(), player2.getLocation());
							}

							i++;

						}

						for (UUID uuid : skeletonTargets.keySet()) {
							if (skeletonTargets.containsKey(uuid)) {
								Player player3 = Bukkit.getPlayer(uuid);
								if (uuid != player.getUniqueId()) {
									if (arena.getTeam(player3) != arena.getTeam(player)) skeletonTargetDistance.put(player3.getUniqueId(), player.getLocation().distanceSquared(player3.getLocation()));
								}
							}
						}

						Double minDistance = Collections.min(skeletonTargetDistance.values());

						for (UUID entry : skeletonTargetDistance.keySet()) {

							if (skeletonTargetDistance.get(entry).equals(minDistance)) {

								Player target = Bukkit.getPlayer(entry);

								setSkeletonTarget(skeleton, target);
								setSkeletonTarget(skeleton2, target);
								setSkeletonTarget(skeleton3, target);
								setSkeletonTarget(skeleton4, target);
								skeletonTargetDistance.clear();
								skeletonTargets.clear();
							}

						}

					}

				}

				else if (player.getItemInHand().getItemMeta().getDisplayName()
						.equals(ChatColor.LIGHT_PURPLE + "Witch's Wand")) {

					if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
						{

							Arena arena = Manager.getArena(player);
							ClashRoyaleGame.witchAbilityCooldown(player);
							new BukkitRunnable() {
								double t = 0;
								Location loc = player.getLocation();
								Vector direction = loc.getDirection().normalize();

								public void run() {
									t = t + 1;
									double x = direction.getX() * t;
									double y = direction.getY() * t + 1.5;
									double z = direction.getZ() * t;
									loc.add(x, y, z);
									if (loc.getBlock().getType() != Material.AIR) {
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

																if (tower.isEnabled() && !tower.getTeam()
																		.equals(arena.getTeam(player))) {

																	if (game.getTowerHits()
																			.containsKey(player.getUniqueId())) {

																		if (System.currentTimeMillis()
																				- game.getTowerHits().get(
																						player.getUniqueId()) >= arena
																								.getClass(player)
																								.getHitSpeed()) {

																			game.getTowerHits()
																					.remove(player.getUniqueId());

																			tower.subtractHealth(arena.getClass(player)
																					.getBaseDamage());
																			game.getTowerHits().put(
																					player.getUniqueId(),
																					System.currentTimeMillis());

																			player.playSound(player.getLocation(),
																					Sound.DIG_STONE, 1, 0.5f);
																			player.playSound(player.getLocation(),
																					Sound.WITHER_HURT, 0.23f, 0.2f);
																			game.updateTowerHealth(Manager.getArena(tower.getRegionLocations()[1].getWorld()));
																			game.checkIfDead(tower, player);
																		}

																		else {
																			double timeToAttack = (arena
																					.getClass(player).getHitSpeed()
																					- (System.currentTimeMillis() - game
																							.getTowerHits()
																							.get(player.getUniqueId())))
																					/ 1000.0;
																			player.sendMessage(ChatColor.RED
																					+ "You can attack this tower in "
																					+ ChatColor.BOLD + timeToAttack
																					+ "s");
																		}
																	}

																	else {

																		tower.subtractHealth(
																				arena.getClass(player).getBaseDamage());
																		game.getTowerHits().put(player.getUniqueId(),
																				System.currentTimeMillis());

																		player.playSound(player.getLocation(),
																				Sound.DIG_STONE, 1, 0.5f);
																		player.playSound(player.getLocation(),
																				Sound.WITHER_HURT, 0.23f, 0.2f);

																		game.updateTowerHealth(Manager
																				.getArena(tower.getRegionLocations()[1]
																						.getWorld()));
																		game.checkIfDead(tower, player);

																	}
																} else if (!tower.isEnabled()
																		&& tower.getType().equals(TowerTypes.KING)) {
																	player.sendMessage(ChatColor.GREEN
																			+ "That tower is protected! Destroy at least one of the princess towers to be able to damage this one!");
																}

															}
														}

													}

												}

											}

										}
										hasHit.clear();
										ClashRoyaleGame.removeArmorStands(player);
										this.cancel();

									} else {
										ArmorStand bat = (ArmorStand) player.getWorld().spawnEntity(loc,
												EntityType.ARMOR_STAND);
										bat.setCustomName(player.getName());
										bat.setCustomNameVisible(false);
										bat.setVisible(false);
										bat.setMaxHealth(100);
										bat.setHealth(100);
										bat.setMarker(true);

										player.getWorld().playEffect(loc, Effect.WITCH_MAGIC, 5);

										for (Entity ent : bat.getNearbyEntities(.6, 1, .6)) {
											if (ent instanceof Player) {
												Player targetPlayer = (Player) ent;
												if (arena.getTeam(player) != arena.getTeam(targetPlayer)) {

													if (!hasHit.containsKey(player.getUniqueId())) {

														GameListener.customDeathCause.put(targetPlayer.getUniqueId(),
																CustomDeathCause.WITCH_MAGIC);
														@SuppressWarnings("deprecation")
														EntityDamageEvent event = new EntityDamageEvent(targetPlayer,
																DamageCause.CUSTOM, 10.0);

														targetPlayer.setLastDamageCause(event);
														targetPlayer.damage(10);
														targetPlayer.setLastDamageCause(event);
														GameListener.lastAbilityAttacker.put(targetPlayer.getUniqueId(),
																player.getUniqueId());

														if (!GameListener.customDeathCause
																.containsKey(targetPlayer.getUniqueId())
																|| !GameListener.customDeathCause
																		.get(targetPlayer.getUniqueId())
																		.equals(CustomDeathCause.WITCH_MAGIC)) {
															GameListener.customDeathCause.put(
																	targetPlayer.getUniqueId(),
																	CustomDeathCause.WITCH_MAGIC);
														}
														GameListener.customDeathCause.put(targetPlayer.getUniqueId(),
																CustomDeathCause.WITCH_MAGIC);
													} else return;


												}

											}

											else if (ent instanceof Skeleton) {

												Skeleton skeleton = (Skeleton) ent;
												Player skeletonOwner = Bukkit.getPlayerExact(skeleton.getCustomName());

												if (arena.getTeam(player) != arena.getTeam(skeletonOwner)) {skeleton.damage(20);}

											}
											bat.remove();
										}
										bat.remove();
									}

									loc.subtract(x, y, z);

									if (t > 10) {
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
}
