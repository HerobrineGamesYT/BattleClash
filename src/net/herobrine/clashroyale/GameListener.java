package net.herobrine.clashroyale;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.herobrine.clashroyale.beta.Cannon;
import net.herobrine.clashroyale.beta.Region;
import net.herobrine.clashroyale.beta.Tower;
import net.herobrine.clashroyale.beta.Tower.TowerTypes;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.ItemTypes;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.GameType;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import net.herobrine.gamecore.Teams;

public class GameListener implements Listener {

	public static HashMap<UUID, CustomDeathCause> customDeathCause = new HashMap<>();
	// UUID KEY IS THE VICTIM, UUID VALUE IS THE ATTACKER!
	public static HashMap<UUID, UUID> lastAbilityAttacker = new HashMap<>();
	public HashMap<UUID, Entity> lastAttacker = new HashMap<>();
	public static HashMap<UUID, UUID> wasHooked = new HashMap<>();

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();

		if (e.getView().getTitle()
				.contains(ChatColor.translateAlternateColorCodes('&', "&bBattle Clash &7: &aClass Selector"))
				&& e.getRawSlot() <= 27 && e.getCurrentItem() != null) {

			String classString = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toUpperCase());

			classString = classString.replaceAll("\\s", "");
			classString = classString.replaceAll("BATTLE", "");

			ClassTypes type = ClassTypes.valueOf(classString);

			if (Manager.hasKit(player) && Manager.getKit(player).equals(type)) {
				player.sendMessage(ChatColor.RED + "You already have this class selected!");
			} else {

				if (type.isUnlockable() && !HerobrinePVPCore.getFileManager().isItemUnlocked(ItemTypes.CLASS,
						type.toString(), player.getUniqueId())) {

					player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
					player.sendMessage(ChatColor.RED + "You do not have this class unlocked!");

				} else {
					player.sendMessage(ChatColor.GREEN + "You have selected the " + type.getDisplay() + ChatColor.GREEN
							+ " class!");

					Manager.getArena(player).setClass(player.getUniqueId(), type);
				}

			}

			e.setCancelled(true);
			player.closeInventory();
		} else {
			if (Manager.isPlaying(player)) {
				Arena arena = Manager.getArena(player);

				if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)) {
					e.setCancelled(true);

				}
			}
		}
	}

	@EventHandler

	public void onTarget(EntityTargetEvent e) {

		if (Manager.isGameWorld(e.getEntity().getWorld())) {

			Arena arena = Manager.getArena(e.getEntity().getWorld());

			if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE) && arena.getState().equals(GameState.LIVE)) {
				if (e.getEntityType() == EntityType.SKELETON) {

					if (e.getTarget() instanceof Player) {
						Player targetPlayer = (Player) e.getTarget();
						Player skeletonOwner = Bukkit.getPlayer(e.getEntity().getCustomName());

						if (arena.getTeam(targetPlayer) == arena.getTeam(skeletonOwner)) {
							e.setCancelled(true);

						}
					}
				}

			}
		}
	}

	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {

			Arrow arrow = (Arrow) e.getEntity();
			World world = arrow.getWorld();
			Location loc = arrow.getLocation();
			Vector vec = arrow.getVelocity();
			Location loc2 = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(),
					loc.getZ() + vec.getZ());

			Block block = loc2.getBlock();
			if (Manager.isArenaWorld(world)) {

				if (Manager.getArena(world).getGame(Manager.getArena(world).getID()).equals(Games.CLASH_ROYALE)) {

					ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();
					ClashRoyaleGame game = null;

					game = Manager.getArena(world).getBattleClash();

					for (String b : plugin.getConfig().getConfigurationSection("towers").getKeys(false)) {

						if (!b.equals("world") && !b.equals("arena")) {

							Tower tower = game.getTower().get(b);

							Region region = new Region(tower.getRegionLocations()[0], tower.getRegionLocations()[1]);

							Arena arena = Manager.getArena(tower.getRegionLocations()[1].getWorld());
							Player player = (Player) arrow.getShooter();
							if (region.blockInLocation(block)) {

								if (tower.isEnabled() && !tower.getTeam().equals(arena.getTeam(player))) {

									if (game.getTowerHits().containsKey(player.getUniqueId())) {

										if (System.currentTimeMillis() - game.getTowerHits()
												.get(player.getUniqueId()) >= arena.getClass(player).getHitSpeed()) {
											if (arena.getClass(player).equals(ClassTypes.ARCHER)) {

												game.getTowerHits().remove(player.getUniqueId());

												tower.subtractHealth(arena.getClass(player).getBaseDamage());
												game.getTowerHits().put(player.getUniqueId(),
														System.currentTimeMillis());

											}

											player.playSound(player.getLocation(), Sound.DIG_STONE, 1, 0.5f);
											player.playSound(player.getLocation(), Sound.WITHER_HURT, 0.23f, 0.2f);

											game.updateTowerHealth(
													Manager.getArena(tower.getRegionLocations()[1].getWorld()));
											if (tower.getHealth() <= 0) {

												tower.setEnabled(false);

												for (Cannon cannon : tower.getCannons()) {
													cannon.setActive(false);
												}
												if (tower.getType().equals(TowerTypes.PRINCESS)) {

													for (Tower tower2 : game.getTower().values()) {
														if (tower2.getTeam().equals(tower.getTeam())
																&& tower2.getType().equals(TowerTypes.KING)) {
															if (!tower2.isEnabled()) {
																tower2.setEnabled(true);
																for (Cannon cannon : tower2.getCannons()) {
																	cannon.setActive(true);
																}

															}
														}
													}
												}
												arena.playSound(Sound.WITHER_DEATH);
												arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
														"&a&lTOWER DESTRUCTION > " + tower.getFriendlyName()
																+ ChatColor.GREEN + " has been destroyed by "
																+ arena.getTeam(player).getColor() + player.getName()
																+ ChatColor.GREEN + "!"));

											}

										}

										else {
											double timeToAttack = (arena.getClass(player).getHitSpeed() - (System.currentTimeMillis() - game.getTowerHits().get(player.getUniqueId()))) / 1000.0;
											player.sendMessage(ChatColor.RED + "You can attack this tower in "
													+ ChatColor.BOLD + timeToAttack + "s");
										}
									}

									else {
										if (arena.getClass(player).equals(ClassTypes.ARCHER)) {

											tower.subtractHealth(arena.getClass(player).getBaseDamage());
											game.getTowerHits().put(player.getUniqueId(), System.currentTimeMillis());

										}

										player.playSound(player.getLocation(), Sound.DIG_STONE, 1, 0.5f);
										player.playSound(player.getLocation(), Sound.WITHER_HURT, 0.23f, 0.2f);

										game.updateTowerHealth(
												Manager.getArena(tower.getRegionLocations()[1].getWorld()));
										if (tower.getHealth() <= 0) {

											tower.setEnabled(false);

											for (Cannon cannon : tower.getCannons()) {
												cannon.setActive(false);
											}
											if (tower.getType().equals(TowerTypes.PRINCESS)) {

												for (Tower tower2 : game.getTower().values()) {
													if (tower2.getTeam().equals(tower.getTeam())
															&& tower2.getType().equals(TowerTypes.KING)) {
														if (!tower2.isEnabled()) {
															tower2.setEnabled(true);
															for (Cannon cannon : tower2.getCannons()) {
																cannon.setActive(true);
															}

														}
													}
												}
											}
											arena.playSound(Sound.WITHER_DEATH);
											arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
													"&a&lTOWER DESTRUCTION > " + tower.getFriendlyName()
															+ ChatColor.GREEN + " has been destroyed by "
															+ arena.getTeam(player).getColor() + player.getName()
															+ ChatColor.GREEN + "!"));

											if (tower.getType().equals(TowerTypes.KING)) {
												if (arena.getTeam(player).equals(Teams.RED)) {
													ClashRoyaleGame.redCrowns = 3;

													game.isGameOver();
												} else {

													ClashRoyaleGame.blueCrowns = 3;

													game.isGameOver();
												}
											} else {
												if (arena.getTeam(player).equals(Teams.RED)) {
													ClashRoyaleGame.redCrowns = ClashRoyaleGame.redCrowns + 1;

													if (game.getSuddenDeath()) {
														game.isGameOver();

													}
												} else {

													ClashRoyaleGame.blueCrowns = ClashRoyaleGame.blueCrowns + 1;

													if (game.getSuddenDeath()) {
														game.isGameOver();
													}
												}
											}
										}

									}
								} else if (!tower.isEnabled() && tower.getType().equals(TowerTypes.KING)) {
									player.sendMessage(ChatColor.GREEN
											+ "That tower is protected! Destroy at least one of the princess towers to be able to damage this one!");
								}

							}
						}

					}

				}

			}

			arrow.remove();

		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
			if (player.getItemInHand().getItemMeta() != null
					&& player.getItemInHand().getItemMeta().getDisplayName() != null) {
				if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Spectate")
						&& Manager.getArena(player).getGame(Manager.getArena(player).getID())
								.equals(Games.CLASH_ROYALE)) {
					Random rand = new Random();
					int alivePlayersSize = ClashRoyaleGame.getAlivePlayers().size();
					int randomIndex = rand.nextInt(alivePlayersSize);
					Player randPlayer = Bukkit.getPlayer(ClashRoyaleGame.getAlivePlayers().get(randomIndex));
					player.teleport(randPlayer);
					player.sendMessage(
							ChatColor.GREEN + "You are now spectating " + ChatColor.GOLD + randPlayer.getName());

				} else if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Leave")) {

					if (Manager.isPlaying(player)) {
						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {
							player.sendMessage(ChatColor.RED
									+ "You can only use this item when the game is in the countdown or recruiting phase. Use /leave instead!");
						} else {
							Manager.getArena(player)
									.sendMessage(HerobrinePVPCore.getFileManager().getRank(player).getColor()
											+ player.getName() + ChatColor.YELLOW + " has left!");
							Manager.getArena(player).removePlayer(player);

						}
					} else {
						player.sendMessage(ChatColor.RED + "You are not in a game!");
					}

				} else if (player.getItemInHand().getItemMeta().getDisplayName()
						.equals(ChatColor.AQUA + "Class Selector")) {
					Menus.applyClassSelector(player);
				}

			}

		}
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR)
				return;

			if (Manager.isArenaWorld(e.getClickedBlock().getWorld())) {

				if (Manager.getArena(e.getClickedBlock().getWorld())
						.getGame(Manager.getArena(e.getClickedBlock().getWorld()).getID()).equals(Games.CLASH_ROYALE)
						&& Manager.getArena(e.getClickedBlock().getWorld()).getType().equals(GameType.CLASH_ROYALE)) {

					ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();
					ClashRoyaleGame game = null;

					game = Manager.getArena(e.getClickedBlock().getWorld()).getBattleClash();

					for (String b : plugin.getConfig().getConfigurationSection("towers").getKeys(false)) {

						if (!b.equals("world") && !b.equals("arena")) {

							Tower tower = game.getTower().get(b);

							Region region = new Region(tower.getRegionLocations()[0], tower.getRegionLocations()[1]);

							Block block = e.getClickedBlock();
							Arena arena = Manager.getArena(tower.getRegionLocations()[1].getWorld());
							if (region.blockInLocation(block)) {
								// player.sendMessage(
								// Colorize.color("&2&lDEBUG!&r&a You have hit " + tower.getName() + " !"));

								if (tower.isEnabled() && !tower.getTeam().equals(arena.getTeam(player))) {

									if (player.getItemInHand() != null) {

										if (game.getTowerHits().containsKey(player.getUniqueId())) {

											if (System.currentTimeMillis()
													- game.getTowerHits().get(player.getUniqueId()) >= arena
															.getClass(player).getHitSpeed()) {

												if (player.getInventory().getHeldItemSlot() == 0) {

													if (arena.getClass(player) != ClassTypes.WITCH
															&& arena.getClass(player) != ClassTypes.WIZARD
															&& arena.getClass(player) != ClassTypes.ARCHER) {

														game.getTowerHits().remove(player.getUniqueId());

														tower.subtractHealth(arena.getClass(player).getBaseDamage());
														game.getTowerHits().put(player.getUniqueId(),
																System.currentTimeMillis());
													}

													if (arena.getClass(player).equals(ClassTypes.HEALER)) {
														for (Entity ent : player.getNearbyEntities(3f, 1f, 3f)) {

															if (ent instanceof Player) {
																Player pl = (Player) ent;

																if (arena.getTeam(pl).equals(arena.getTeam(player))
																		&& pl != player) {

																	try {
																		if (!(pl.getHealth() >= 20)
																				&& !(pl.getHealth() <= 0)) {

																			if (pl.getHealth() + 1.0 > 20) {

																				if (pl.getHealth() + 0.5 > 20) {

																					if (!(pl.getHealth() + 0.25 > 20)) {
																						pl.setHealth(
																								pl.getHealth() + 0.25);
																						pl.sendMessage(ChatColor.GOLD
																								+ player.getName()
																								+ ChatColor.AQUA
																								+ " healed you for 0.25 health!");
																					}

																				} else {
																					pl.setHealth(pl.getHealth() + 0.5);
																					pl.sendMessage(ChatColor.GOLD
																							+ player.getName()
																							+ ChatColor.AQUA
																							+ " healed you for 0.5 health!");
																				}

																			} else {
																				pl.setHealth(pl.getHealth() + 1);
																				pl.sendMessage(ChatColor.GOLD
																						+ player.getName()
																						+ ChatColor.AQUA
																						+ " healed you for 1 health!");
																			}

																		}
																	}

																	catch (Exception exception) {
																		return;
																	}

																}

															}

														}

														if (!(player.getHealth() >= 20) && !(player.getHealth() <= 0)) {

															try {
																if (player.getHealth() + 1.0 > 20) {

																	if (player.getHealth() + 0.5 > 20) {

																		if (!(player.getHealth() + 0.25 > 20)) {
																			player.setHealth(player.getHealth() + 0.5);

																		}

																	} else {
																		player.setHealth(player.getHealth() + 0.5);

																	}

																} else {
																	player.setHealth(player.getHealth() + 1);

																}
															} catch (Exception exception) {
																return;
															}

														}
													}

												}

												player.playSound(player.getLocation(), Sound.DIG_STONE, 1, 0.5f);
												player.playSound(player.getLocation(), Sound.WITHER_HURT, 0.23f, 0.2f);

												game.updateTowerHealth(
														Manager.getArena(tower.getRegionLocations()[1].getWorld()));
												if (tower.getHealth() <= 0) {

													tower.setEnabled(false);

													for (Cannon cannon : tower.getCannons()) {
														cannon.setActive(false);
													}
													if (tower.getType().equals(TowerTypes.PRINCESS)) {

														for (Tower tower2 : game.getTower().values()) {
															if (tower2.getTeam().equals(tower.getTeam())
																	&& tower2.getType().equals(TowerTypes.KING)) {
																if (!tower2.isEnabled()) {
																	tower2.setEnabled(true);
																	for (Cannon cannon : tower2.getCannons()) {
																		cannon.setActive(true);
																	}

																}
															}
														}
													}
													arena.playSound(Sound.WITHER_DEATH);
													arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
															"&a&lTOWER DESTRUCTION > " + tower.getFriendlyName()
																	+ ChatColor.GREEN + " has been destroyed by "
																	+ arena.getTeam(player).getColor()
																	+ player.getName() + ChatColor.GREEN + "!"));

													if (tower.getType().equals(TowerTypes.KING)) {
														if (arena.getTeam(player).equals(Teams.RED)) {
															ClashRoyaleGame.redCrowns = 3;

															game.isGameOver();
														} else {

															ClashRoyaleGame.blueCrowns = 3;

															game.isGameOver();
														}
													} else {
														if (arena.getTeam(player).equals(Teams.RED)) {
															ClashRoyaleGame.redCrowns = ClashRoyaleGame.redCrowns + 1;

															if (game.getSuddenDeath()) {
																game.isGameOver();

															}
														} else {

															ClashRoyaleGame.blueCrowns = ClashRoyaleGame.blueCrowns + 1;

															if (game.getSuddenDeath()) {
																game.isGameOver();
															}
														}
													}
												}
											} else {
												double timeToAttack = (arena.getClass(player).getHitSpeed()
														- (System.currentTimeMillis()
																- game.getTowerHits().get(player.getUniqueId())))
														/ 1000.0;
												player.sendMessage(ChatColor.RED + "You can attack this tower in "
														+ ChatColor.BOLD + timeToAttack + "s");

											}

										} else {

											if (player.getInventory().getHeldItemSlot() == 0) {

												if (arena.getClass(player) != ClassTypes.WITCH
														&& arena.getClass(player) != ClassTypes.WIZARD
														&& arena.getClass(player) != ClassTypes.ARCHER) {

													tower.subtractHealth(arena.getClass(player).getBaseDamage());
													game.getTowerHits().put(player.getUniqueId(),
															System.currentTimeMillis());
												}

											}

											player.playSound(player.getLocation(), Sound.DIG_STONE, 1, 0.5f);
											player.playSound(player.getLocation(), Sound.WITHER_HURT, 0.23f, 0.2f);

											game.updateTowerHealth(
													Manager.getArena(tower.getRegionLocations()[1].getWorld()));
											if (tower.getHealth() <= 0) {

												tower.setEnabled(false);

												for (Cannon cannon : tower.getCannons()) {
													cannon.setActive(false);
												}
												if (tower.getType().equals(TowerTypes.PRINCESS)) {

													for (Tower tower2 : game.getTower().values()) {
														if (tower2.getTeam().equals(tower.getTeam())
																&& tower2.getType().equals(TowerTypes.KING)) {
															if (!tower2.isEnabled()) {
																tower2.setEnabled(true);
																for (Cannon cannon : tower2.getCannons()) {
																	cannon.setActive(true);
																}

															}
														}
													}
												}
												arena.playSound(Sound.WITHER_DEATH);
												arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
														"&a&lTOWER DESTRUCTION > " + tower.getFriendlyName()
																+ ChatColor.GREEN + " has been destroyed by "
																+ arena.getTeam(player).getColor() + player.getName()
																+ ChatColor.GREEN + "!"));

											}
										}
									}
								} else if (!tower.isEnabled() && tower.getType().equals(TowerTypes.KING)) {
									player.sendMessage(ChatColor.GREEN
											+ "That tower is protected! Destroy at least one of the princess towers to be able to damage this one!");
								}

							}
						}

					}

				}

			}

		}

	}

	@EventHandler

	public void onExplosion(EntityExplodeEvent e) {
		if (e.getEntity() instanceof Fireball) {
			e.setCancelled(true);
			Fireball fireball = (Fireball) e.getEntity();

			Player player = Bukkit.getPlayer(fireball.getCustomName());

			if (Manager.isPlaying(player)) {
				Arena arena = Manager.getArena(player);
				if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)) {
					player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1f, .5f);

					if (arena.getType().equals(GameType.CLASH_ROYALE)) {
						ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();
						ClashRoyaleGame game = null;

						game = arena.getBattleClash();
						for (String b : plugin.getConfig().getConfigurationSection("towers").getKeys(false)) {

							if (!b.equals("world") && !b.equals("arena")) {

								Tower tower = game.getTower().get(b);

								Region region = new Region(tower.getRegionLocations()[0],
										tower.getRegionLocations()[1]);

								boolean hasHit = false;

								for (Block block : e.blockList()) {

									if (region.blockInLocation(block) && !hasHit) {

										hasHit = true;
										if (tower.isEnabled() && !tower.getTeam().equals(arena.getTeam(player))) {

											if (game.getTowerHits().containsKey(player.getUniqueId())) {

												if (System.currentTimeMillis()
														- game.getTowerHits().get(player.getUniqueId()) >= arena
																.getClass(player).getHitSpeed()) {

													if (arena.getClass(player).equals(ClassTypes.WIZARD)) {
														game.getTowerHits().remove(player.getUniqueId());

														tower.subtractHealth(arena.getClass(player).getBaseDamage());
														game.getTowerHits().put(player.getUniqueId(),
																System.currentTimeMillis());

													}
													player.playSound(player.getLocation(), Sound.DIG_STONE, 1, 0.5f);
													player.playSound(player.getLocation(), Sound.WITHER_HURT, 0.23f,
															0.2f);

													game.updateTowerHealth(
															Manager.getArena(tower.getRegionLocations()[1].getWorld()));
													if (tower.getHealth() <= 0) {

														tower.setEnabled(false);

														for (Cannon cannon : tower.getCannons()) {
															cannon.setActive(false);
														}
														if (tower.getType().equals(TowerTypes.PRINCESS)) {

															for (Tower tower2 : game.getTower().values()) {
																if (tower2.getTeam().equals(tower.getTeam())
																		&& tower2.getType().equals(TowerTypes.KING)) {
																	if (!tower2.isEnabled()) {
																		tower2.setEnabled(true);
																		for (Cannon cannon : tower2.getCannons()) {
																			cannon.setActive(true);
																		}

																	}
																}
															}
														}
														arena.playSound(Sound.WITHER_DEATH);
														arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
																"&a&lTOWER DESTRUCTION > " + tower.getFriendlyName()
																		+ ChatColor.GREEN + " has been destroyed by "
																		+ arena.getTeam(player).getColor()
																		+ player.getName() + ChatColor.GREEN + "!"));

														if (tower.getType().equals(TowerTypes.KING)) {
															if (arena.getTeam(player).equals(Teams.RED)) {
																ClashRoyaleGame.redCrowns = 3;

																game.isGameOver();
															} else {

																ClashRoyaleGame.blueCrowns = 3;

																game.isGameOver();
															}
														} else {
															if (arena.getTeam(player).equals(Teams.RED)) {
																ClashRoyaleGame.redCrowns = ClashRoyaleGame.redCrowns
																		+ 1;

																if (game.getSuddenDeath()) {
																	game.isGameOver();

																}
															} else {

																ClashRoyaleGame.blueCrowns = ClashRoyaleGame.blueCrowns
																		+ 1;

																if (game.getSuddenDeath()) {
																	game.isGameOver();
																}
															}
														}
													}
												} else {
													double timeToAttack = (arena.getClass(player).getHitSpeed()
															- (System.currentTimeMillis()
																	- game.getTowerHits().get(player.getUniqueId())))
															/ 1000.0;
													player.sendMessage(ChatColor.RED + "You can attack this tower in "
															+ ChatColor.BOLD + timeToAttack + "s");

												}

											} else {

												if (arena.getClass(player).equals(ClassTypes.WIZARD)) {
													game.getTowerHits().remove(player.getUniqueId());

													tower.subtractHealth(arena.getClass(player).getBaseDamage());
													game.getTowerHits().put(player.getUniqueId(),
															System.currentTimeMillis());

												}

												player.playSound(player.getLocation(), Sound.DIG_STONE, 1, 0.5f);
												player.playSound(player.getLocation(), Sound.WITHER_HURT, 0.23f, 0.2f);

												game.updateTowerHealth(
														Manager.getArena(tower.getRegionLocations()[1].getWorld()));
												if (tower.getHealth() <= 0) {

													tower.setEnabled(false);

													for (Cannon cannon : tower.getCannons()) {
														cannon.setActive(false);
													}
													if (tower.getType().equals(TowerTypes.PRINCESS)) {

														for (Tower tower2 : game.getTower().values()) {
															if (tower2.getTeam().equals(tower.getTeam())
																	&& tower2.getType().equals(TowerTypes.KING)) {
																if (!tower2.isEnabled()) {
																	tower2.setEnabled(true);
																	for (Cannon cannon : tower2.getCannons()) {
																		cannon.setActive(true);
																	}

																}
															}
														}
													}
													arena.playSound(Sound.WITHER_DEATH);
													arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
															"&a&lTOWER DESTRUCTION > " + tower.getFriendlyName()
																	+ ChatColor.GREEN + " has been destroyed by "
																	+ arena.getTeam(player).getColor()
																	+ player.getName() + ChatColor.GREEN + "!"));

												}
											}

										} else if (!tower.isEnabled() && tower.getType().equals(TowerTypes.KING)) {
											player.sendMessage(ChatColor.GREEN
													+ "That tower is protected! Destroy at least one of the princess towers to be able to damage this one!");
										}

									}
								}
							}

						}
					}

				}
			}

		} else {
			return;
		}

	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (Manager.isPlaying(player)) {
				Arena arena = Manager.getArena(player);

				if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)) {

					if (e.getCause().equals(DamageCause.FALL)) {
						e.setCancelled(true);
					}

					else if (e.getCause().equals(DamageCause.VOID)) {
						player.setHealth(0.5);
					}

				}

			}

		} else {
			return;
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {

		if (e.getEntityType() == EntityType.SKELETON) {

			Skeleton skeleton = (Skeleton) e.getEntity();

			Arena arena = Manager.getArena(skeleton.getLocation().getWorld());

			if (skeleton.getCustomName() != null && arena != null) {

				if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)) {

					e.getDrops().clear();
					e.setDroppedExp(0);

				}

			}

			else {
				return;
			}

		} else {
			return;
		}

	}

	@EventHandler
	public void onHit(EntityDamageEvent e) {

		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();

			if (Manager.isPlaying(player)) {

				Arena arena = Manager.getArena(player);

				if (arena.getState() != GameState.LIVE) {
					e.setCancelled(true);
				}

			}

		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();

			if (Manager.isPlaying(player)) {
				Arena arena = Manager.getArena(player);

				if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)
						&& arena.getState().equals(GameState.LIVE)) {

					if (!e.getCause().equals(DamageCause.CUSTOM)
							&& customDeathCause.containsKey(player.getUniqueId())) {

						customDeathCause.remove(player.getUniqueId());
					}

					if (e.getDamager() instanceof Fireball) {
						Fireball fireball = (Fireball) e.getDamager();

						Player fireballOwner = Bukkit.getPlayerExact(fireball.getCustomName());

						if (fireballOwner != null) {

							if (arena.getTeam(fireballOwner).equals(arena.getTeam(player))
									|| arena.getState() != GameState.LIVE) {
								e.setCancelled(true);
							} else {

								e.setCancelled(false);
							}

						} else {
							// this should never occur but is a failsafe
							return;
						}

					}

					else if (e.getDamager() instanceof Arrow) {

						Arrow arrow = (Arrow) e.getDamager();

						if (arrow.getShooter() instanceof Player) {
							Player shooter = (Player) arrow.getShooter();
							if (arena.getTeam(shooter) == arena.getTeam(player) || arena.getState() != GameState.LIVE
									|| arena.getSpectators().contains(shooter.getUniqueId())) {
								e.setCancelled(true);
							} else {
								e.setCancelled(false);
							}
						}

					}

					else if (e.getDamager() instanceof Player) {

						Player damager = (Player) e.getDamager();

						if (arena.getSpectators().contains(damager.getUniqueId())) {
							e.setCancelled(true);
						}

						if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {

							lastAttacker.put(player.getUniqueId(), damager);

						}

						if (e.getCause().equals(DamageCause.ENTITY_ATTACK)
								&& arena.getClass(player).equals(ClassTypes.WIZARD)) {

							double dmg1 = e.getDamage() * .25;

							double dmg2 = e.getDamage() - dmg1;

							e.setDamage(dmg2);

						}

						else if (e.getCause().equals(DamageCause.ENTITY_ATTACK)
								&& arena.getClass(damager).equals(ClassTypes.HEALER)) {
							if (damager.getItemInHand().getType().equals(Material.GOLD_SWORD)
									&& arena.getTeam(player) != arena.getTeam(damager)) {

								for (Entity ent : damager.getNearbyEntities(3f, 1f, 3f)) {

									if (ent instanceof Player) {
										Player pl = (Player) ent;

										if (arena.getTeam(pl).equals(arena.getTeam(damager)) && pl != player) {

											try {
												if (!(pl.getHealth() >= 20) && !(pl.getHealth() <= 0)) {

													if (pl.getHealth() + 1.0 > 20) {

														if (pl.getHealth() + 0.5 > 20) {

															if (!(pl.getHealth() + 0.25 > 20)) {
																pl.setHealth(pl.getHealth() + 0.25);
																pl.sendMessage(ChatColor.GOLD + damager.getName()
																		+ ChatColor.AQUA
																		+ " healed you for 0.25 health!");
															}

														} else {
															pl.setHealth(pl.getHealth() + 0.5);
															pl.sendMessage(ChatColor.GOLD + damager.getName()
																	+ ChatColor.AQUA + " healed you for 0.5 health!");
														}

													} else {
														pl.setHealth(pl.getHealth() + 1);
														pl.sendMessage(ChatColor.GOLD + damager.getName()
																+ ChatColor.AQUA + " healed you for 1 health!");
													}

												}
											}

											catch (Exception exception) {
												return;
											}

										}

									}

								}

								if (!(damager.getHealth() >= 20) && !(damager.getHealth() <= 0)) {

									try {
										if (damager.getHealth() + 1.0 > 20) {

											if (damager.getHealth() + 0.5 > 20) {

												if (!(damager.getHealth() + 0.25 > 20)) {
													damager.setHealth(damager.getHealth() + 0.5);

												}

											} else {
												damager.setHealth(damager.getHealth() + 0.5);

											}

										} else {
											damager.setHealth(damager.getHealth() + 1);

										}
									} catch (Exception exception) {
										return;
									}

								}
							}
						}

					}

				}
			} else {
				e.setCancelled(true);
			}
		}

		if (e.getEntity() instanceof Skeleton) {

			Skeleton skeleton = (Skeleton) e.getEntity();

			if (skeleton.getCustomName() != null) {

				Player skeletonPlayer = Bukkit.getPlayer(skeleton.getCustomName());
				if (skeletonPlayer != null) {
					Arena arena = Manager.getArena(skeletonPlayer);
					if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)) {
						if (e.getDamager() instanceof Fireball) {
							Fireball fireball = (Fireball) e.getDamager();

							Player fireballOwner = Bukkit.getPlayerExact(fireball.getCustomName());

							if (fireballOwner != null) {

								if (arena.getTeam(fireballOwner).equals(arena.getTeam(skeletonPlayer))) {
									e.setCancelled(true);
								} else {

									e.setCancelled(false);
								}

							} else {
								// this should never occur but is a failsafe
								return;
							}

						}

						else if (e.getDamager() instanceof Arrow) {

							Arrow arrow = (Arrow) e.getDamager();

							if (arrow.getShooter() instanceof Player) {
								Player shooter = (Player) arrow.getShooter();
								if (arena.getTeam(shooter) == arena.getTeam(skeletonPlayer)) {
									e.setCancelled(true);
								} else {
									e.setCancelled(false);
								}
							}

						}

						else if (e.getDamager() instanceof Player) {

							Player damager = (Player) e.getDamager();

							if (arena.getTeam(skeletonPlayer) == arena.getTeam(damager)) {
								e.setCancelled(true);
							}

						}

						else {
							return;
						}
					}
				}
			} else {
				return;
			}

		}
		if (e.getDamager() instanceof Skeleton && e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			Skeleton skeleton = (Skeleton) e.getDamager();
			Arena arena = Manager.getArena(player);

			if (skeleton.getCustomName() != null) {

				Player skeletonOwner = Bukkit.getPlayerExact(skeleton.getCustomName());

				if (arena.getTeam(player) == arena.getTeam(skeletonOwner)) {
					e.setCancelled(true);
				} else {
					lastAttacker.put(player.getUniqueId(), skeleton);

				}
			}
		}

	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();

		if (Manager.isPlaying(player)) {

			if (Manager.getArena(player).getState().equals(GameState.LIVE)
					&& Manager.getArena(player).getGame(Manager.getArena(player).getID()).equals(Games.CLASH_ROYALE)) {
				Arena arena = Manager.getArena(player);

				player.setMaximumNoDamageTicks(20);

				if (arena.getTeam(player).equals(Teams.RED)) {

					e.setRespawnLocation(Config.getRedTeamSpawn(arena.getID()));

					if (arena.getClasses().get(player.getUniqueId()).getClassType().equals(ClassTypes.BANDIT) || arena
							.getClasses().get(player.getUniqueId()).getClassType().equals(ClassTypes.LUMBERJACK)) {
						new BukkitRunnable() {
							@Override
							public void run() {
								PotionEffect banditSpeed = PotionEffectType.SPEED.createEffect(10000000, 1);
								player.addPotionEffect(banditSpeed);

								cancel();
							}
						}.runTaskLater(ClashRoyaleMain.getInstance(), 2L);

					}

				} else {

					e.setRespawnLocation(Config.getBlueTeamSpawn(arena.getID()));

					if (arena.getClasses().get(player.getUniqueId()).getClassType().equals(ClassTypes.BANDIT) || arena
							.getClasses().get(player.getUniqueId()).getClassType().equals(ClassTypes.LUMBERJACK)) {
						new BukkitRunnable() {

							@Override
							public void run() {
								PotionEffect banditSpeed = PotionEffectType.SPEED.createEffect(10000000, 1);
								player.addPotionEffect(banditSpeed);

								cancel();
							}

						}.runTaskLater(ClashRoyaleMain.getInstance(), 2L);

					}

				}

			}
		}

	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {

			Player player = (Player) e.getEntity();

			Arena arena = Manager.getArena(player);
			DamageCause deathCause = player.getLastDamageCause().getCause();

			if (arena.getGame(arena.getID()) != Games.CLASH_ROYALE) {

				System.out.println("death in another pvp game");
			}

			else if (ClashRoyaleGame.getAlivePlayers().contains(player.getUniqueId())) {

				if (arena.getClasses().get(player.getUniqueId()).getClassType().equals(ClassTypes.LUMBERJACK)) {

					int i = 0;

					for (UUID players : arena.getPlayers()) {
						Player actualPlayer = Bukkit.getPlayer(players);

						arena.getBattleClash().deathRageCircle(player.getLocation(), 3f, actualPlayer);
					}

					for (Entity ent : player.getNearbyEntities(3f, 1f, 3f)) {

						if (ent instanceof Player) {
							Player player2 = (Player) ent;

							if (arena.getTeam(player2) == arena.getTeam(player)) {
								for (PotionEffect effect : player2.getActivePotionEffects()) {

									player2.removePotionEffect(effect.getType());
								}
								PotionEffect lumberjackSpeed = PotionEffectType.SPEED.createEffect(120, 2);
								PotionEffect lumberjackHaste = PotionEffectType.FAST_DIGGING.createEffect(80, 1);
								player2.addPotionEffect(lumberjackSpeed);
								player2.addPotionEffect(lumberjackHaste);
								player2.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.AQUA
										+ " has granted you Speed and Haste for 5 seconds!");
								i = i + 1;
								if (arena.getClasses().get(player2.getUniqueId()).getClassType()
										.equals(ClassTypes.BANDIT)
										|| arena.getClasses().get(player2.getUniqueId()).getClassType()
												.equals(ClassTypes.LUMBERJACK)) {

									new BukkitRunnable() {
										@Override
										public void run() {
											player.sendMessage(ChatColor.GOLD + "[DEBUG] Runnable started!");
											player2.sendMessage(ChatColor.GOLD + "[DEBUG] Runnable started!");

											PotionEffect banditSpeed = PotionEffectType.SPEED.createEffect(10000000, 1);
											player2.addPotionEffect(banditSpeed);

										}

									}.runTaskLater(ClashRoyaleMain.getInstance(), 120L);

								}

							}

						}

					}
					if (i == 1) {
						player.sendMessage(ChatColor.AQUA + "Your death rage has given effects to "
								+ ChatColor.LIGHT_PURPLE + i + ChatColor.AQUA + " player!");
					} else {
						player.sendMessage(ChatColor.AQUA + "Your death rage has given effects to "
								+ ChatColor.LIGHT_PURPLE + i + ChatColor.AQUA + " players!");
					}

				}

				if (player.getKiller() != null && deathCause != DamageCause.CUSTOM) {

					Player killer = player.getKiller();

					HashMap<UUID, Integer> kills = arena.getBattleClash().getKills();

					if (deathCause.equals(DamageCause.ENTITY_ATTACK) && killer != player) {
						arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY
								+ " has killed " + arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
								+ " with their epic PVP skills.");

						killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
						player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
						kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
						if (arena.getTeam(killer).equals(Teams.RED)) {
							ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
						} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
							ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
						}
					} else if (deathCause.equals(DamageCause.FALL) && killer != player) {
						arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY
								+ " just made " + arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
								+ " fall to their death.");
						killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
						player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
						kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
						if (arena.getTeam(killer).equals(Teams.RED)) {
							ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
						} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
							ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
						}
					} else if (deathCause.equals(DamageCause.FIRE)) {
						arena.sendMessage(
								arena.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY + " burned "
										+ arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + ".");
						killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
						player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
						if (arena.getTeam(killer).equals(Teams.RED)) {
							ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
						} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
							ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
						}
					} else if (deathCause.equals(DamageCause.PROJECTILE) && killer != player) {
						arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY
								+ " bowspammed " + arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
								+ " to death." + ChatColor.GOLD + ChatColor.BOLD + " EPIC SKILLS!");
						killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
						player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
						kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
						if (arena.getTeam(killer).equals(Teams.RED)) {
							ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
						} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
							ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
						}
					}

					else if (deathCause.equals(DamageCause.ENTITY_EXPLOSION) && killer != player) {
						arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY
								+ " fireballed " + arena.getTeam(player).getColor() + player.getName());
						killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
						player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
						kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
						if (arena.getTeam(killer).equals(Teams.RED)) {
							ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
						} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
							ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
						}
					}

					else if (deathCause.equals(DamageCause.VOID) && killer != player) {
						arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName() + ChatColor.GRAY
								+ " knocked " + arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
								+ " into the void.");
						killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
						player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
						kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
						if (arena.getTeam(killer).equals(Teams.RED)) {
							ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
						} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
							ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
						}
					}
					arena.getBattleClash().updateKillCounts(killer);
				} else {

					HashMap<UUID, Integer> kills = arena.getBattleClash().getKills();
					player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);

					if (deathCause.equals(DamageCause.VOID)) {

						if (lastAttacker.get(player.getUniqueId()) instanceof Skeleton) {
							Skeleton skeleton = (Skeleton) lastAttacker.get(player.getUniqueId());
							Player skeletonOwner = Bukkit.getPlayerExact(skeleton.getCustomName());
							arena.sendMessage(arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
									+ " was knocked into the void by one of " + arena.getTeam(skeletonOwner).getColor()
									+ skeletonOwner.getName() + ChatColor.GRAY + "'s skeletons.");

							skeletonOwner.playSound(skeletonOwner.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
							player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
							kills.put(skeletonOwner.getUniqueId(), kills.get(skeletonOwner.getUniqueId()) + 1);
							if (arena.getTeam(skeletonOwner).equals(Teams.RED)) {
								ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
							} else if (arena.getTeam(skeletonOwner).equals(Teams.BLUE)) {
								ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
							}

							arena.getBattleClash().updateKillCounts(skeletonOwner);

						}

						else if (wasHooked.containsKey(player.getUniqueId())) {

							if (wasHooked.get(player.getUniqueId()) != null) {

								Player killer = Bukkit.getPlayer(wasHooked.get(player.getUniqueId()));

								if (lastAbilityAttacker.containsKey(player.getUniqueId())) {

									if (arena.getClass(lastAbilityAttacker.get(player.getUniqueId()))
											.equals(ClassTypes.FISHERMAN)) {

										arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName()
												+ ChatColor.GRAY + " hooked " + arena.getTeam(player).getColor()
												+ player.getName() + ChatColor.GRAY + "."
												+ ChatColor.translateAlternateColorCodes('&', " &b&lFISHY!"));
										killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
										player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
										kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
										if (arena.getTeam(killer).equals(Teams.RED)) {
											ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
										} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
											ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
										}
										arena.getBattleClash().updateKillCounts(killer);
									} else {
										arena.sendMessage(arena.getTeam(player).getColor() + player.getName()
												+ ChatColor.GRAY + " fell into the void.");
									}

								} else {
									arena.sendMessage(arena.getTeam(player).getColor() + player.getName()
											+ ChatColor.GRAY + " fell into the void.");
								}

							}
						}

						else {
							arena.sendMessage(arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
									+ " fell into the void.");
						}
					} else {

						if (lastAttacker.get(player.getUniqueId()) instanceof Skeleton
								&& deathCause.equals(DamageCause.ENTITY_ATTACK)) {

							Skeleton skeleton = (Skeleton) lastAttacker.get(player.getUniqueId());
							Player skeletonOwner = Bukkit.getPlayer(skeleton.getCustomName());
							arena.sendMessage(arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
									+ " was killed by one of " + arena.getTeam(skeletonOwner).getColor()
									+ skeletonOwner.getName() + ChatColor.GRAY + "'s skeletons.");
							skeletonOwner.playSound(skeletonOwner.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
							player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
							kills.put(skeletonOwner.getUniqueId(), kills.get(skeletonOwner.getUniqueId()) + 1);
							if (arena.getTeam(skeletonOwner).equals(Teams.RED)) {
								ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
							} else if (arena.getTeam(skeletonOwner).equals(Teams.BLUE)) {
								ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
							}

							arena.getBattleClash().updateKillCounts(skeletonOwner);

						} else {
							if (deathCause.equals(DamageCause.CUSTOM)) {

								if (customDeathCause.containsKey(player.getUniqueId())) {

									if (customDeathCause.get(player.getUniqueId()).equals(CustomDeathCause.DASH)) {

										if (lastAbilityAttacker.containsKey(player.getUniqueId())) {

											if (arena.getClass(lastAbilityAttacker.get(player.getUniqueId()))
													.equals(ClassTypes.BANDIT)) {

												Player killer = Bukkit
														.getPlayer(lastAbilityAttacker.get(player.getUniqueId()));

												arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName()
														+ ChatColor.GRAY + " killed " + arena.getTeam(player).getColor()
														+ player.getName() + ChatColor.GRAY
														+ " with their dash ability."
														+ ChatColor.translateAlternateColorCodes('&', " &6&lSNEAKY!"));
												killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
												player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
												kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
												if (arena.getTeam(killer).equals(Teams.RED)) {
													ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
												} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
													ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
												}
												arena.getBattleClash().updateKillCounts(killer);

											} else {
												player.sendMessage(ChatColor.GOLD
														+ "The game believes you died because of a dash ability, however the last ability attack you recieved was from someone who isn't playing Bandit! \n Please report this message to staff so this can be fixed.");
											}

										}

									} else if (customDeathCause.get(player.getUniqueId())
											.equals(CustomDeathCause.WITCH_MAGIC)) {

										if (lastAbilityAttacker.containsKey(player.getUniqueId())) {

											if (arena.getClass(lastAbilityAttacker.get(player.getUniqueId()))
													.equals(ClassTypes.WITCH)) {

												Player killer = Bukkit
														.getPlayer(lastAbilityAttacker.get(player.getUniqueId()));

												arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName()
														+ ChatColor.GRAY + " killed " + arena.getTeam(player).getColor()
														+ player.getName() + ChatColor.GRAY
														+ " with their magic ability."
														+ ChatColor.translateAlternateColorCodes('&', " &6&lSPOOKY!"));
												killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
												player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
												kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
												if (arena.getTeam(killer).equals(Teams.RED)) {
													ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
												} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
													ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
												}
												arena.getBattleClash().updateKillCounts(killer);
											}

										} else {
											player.sendMessage(ChatColor.GOLD
													+ "The game thinks you died to a Witch, but the last person who hit you with an ability was not playing the witch. \n Please report this message to staff so this error can be fixed.");
										}

									}

								}

								else if (customDeathCause.get(player.getUniqueId())
										.equals(CustomDeathCause.FISHERMAN_HOOK)) {

									if (lastAbilityAttacker.containsKey(player.getUniqueId())) {

										if (arena.getClass(lastAbilityAttacker.get(player.getUniqueId()))
												.equals(ClassTypes.FISHERMAN)) {

											Player killer = Bukkit
													.getPlayer(lastAbilityAttacker.get(player.getUniqueId()));

											arena.sendMessage(arena.getTeam(killer).getColor() + killer.getName()
													+ ChatColor.GRAY + " hooked " + arena.getTeam(player).getColor()
													+ player.getName() + ChatColor.GRAY + "."
													+ ChatColor.translateAlternateColorCodes('&', " &b&lFISHY!"));
											killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
											player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
											kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
											if (arena.getTeam(killer).equals(Teams.RED)) {
												ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
											} else if (arena.getTeam(killer).equals(Teams.BLUE)) {
												ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
											}
											arena.getBattleClash().updateKillCounts(killer);
										}

									} else {
										player.sendMessage(ChatColor.GOLD
												+ "The game thinks you died to a Fisherman, but the last person who hit you with an ability was not playing the Fisherman. \n Please report this message to staff so this error can be fixed.");
									}

								}

								else if (customDeathCause.get(player.getUniqueId()).equals(CustomDeathCause.CANNON)) {

									arena.sendMessage(arena.getTeam(player).getColor() + player.getName()
											+ ChatColor.GRAY + " died to a tower cannonball.");

								}

								else if (customDeathCause.get(player.getUniqueId()).equals(CustomDeathCause.SKELETON)) {
									player.sendMessage(ChatColor.GOLD + "you were killed by a skeleton");

									if (lastAttacker.containsKey(player.getUniqueId())) {
										player.sendMessage(ChatColor.GOLD + "you have a last mob attacker");

										if (lastAttacker.get(player.getUniqueId()) instanceof Skeleton) {
											Skeleton skeleton = (Skeleton) lastAttacker.get(player.getUniqueId());
											if (skeleton.getCustomName() != null) {

												player.sendMessage(
														ChatColor.GOLD + "you have a skeleton with a custom name");
												Player skeletonOwner = Bukkit.getPlayerExact(skeleton.getCustomName());

												skeletonOwner.playSound(skeletonOwner.getLocation(), Sound.ORB_PICKUP,
														1f, 1f);
												player.playSound(player.getLocation(), Sound.BAT_DEATH, 1f, 1f);
												arena.sendMessage(arena.getTeam(player).getColor() + player.getName()
														+ ChatColor.GRAY + " was killed by one of "
														+ arena.getTeam(skeletonOwner).getColor()
														+ skeletonOwner.getName() + ChatColor.GRAY + "'s skeletons."
														+ ChatColor.translateAlternateColorCodes('&', " &6&lSPOOKY!"));
												if (arena.getTeam(skeletonOwner).equals(Teams.RED)) {
													ClashRoyaleGame.redKillCount = ClashRoyaleGame.redKillCount + 1;
												} else if (arena.getTeam(skeletonOwner).equals(Teams.BLUE)) {
													ClashRoyaleGame.blueKillCount = ClashRoyaleGame.blueKillCount + 1;
												}

												arena.getBattleClash().updateKillCounts(skeletonOwner);

											} else {
												player.sendMessage(ChatColor.GOLD + "Skeleton's custom name is null");
											}

										} else {
											player.sendMessage(ChatColor.GOLD
													+ "The game does not believe you were attacked by a skeleton. Please report this message to staff so this error can be fixed.");
										}

									} else {
										player.sendMessage(ChatColor.GOLD
												+ "The game does not believe you were attacked by a mob. Please report this message to staff so this error can be fixed.");
									}
								} else {
									arena.sendMessage(arena.getTeam(player).getColor() + player.getName()
											+ ChatColor.GRAY + " has died.");
									player.sendMessage(
											ChatColor.GOLD + "[DEBUG] Death Cause: " + deathCause.toString());
									player.sendMessage(ChatColor.GOLD + "Custom Death Cause: "
											+ customDeathCause.get(player.getUniqueId()));
									player.sendMessage(ChatColor.GOLD + "Last Ability Attacker: " + Bukkit
											.getPlayer(lastAbilityAttacker.get(player.getUniqueId())).getName());
									player.sendMessage(ChatColor.GOLD + "Last Mob Attacker: "
											+ lastAttacker.get(player.getUniqueId()));
									player.sendMessage(ChatColor.GOLD + "[DEBUG] The Game Defines The Killer As: "
											+ player.getKiller());

									player.sendMessage(ChatColor.GOLD
											+ "There is a chance you were meant to be killed by an ability. Please report this message to staff and include the death cause above.");
								}

							}

							else {
								arena.sendMessage(arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY
										+ " has died.");
								player.sendMessage(ChatColor.GOLD + "[DEBUG] Death Cause: " + deathCause.toString());
								player.sendMessage(ChatColor.GOLD + "Custom Death Cause: "
										+ customDeathCause.get(player.getUniqueId()));
								player.sendMessage(ChatColor.GOLD + "Last Ability Attacker: "
										+ Bukkit.getPlayer(lastAbilityAttacker.get(player.getUniqueId())).getName());
								player.sendMessage(ChatColor.GOLD + "Last Mob Attacker: "
										+ lastAttacker.get(player.getUniqueId()));
								player.sendMessage(ChatColor.GOLD + "[DEBUG] The Game Defines The Killer As: "
										+ player.getKiller());

								player.sendMessage(ChatColor.GOLD
										+ "There is a chance you were meant to be killed by an ability. Please report this message to staff and include the death cause above.");
							}

						}
					}
				}

			} else {
				player.sendMessage(ChatColor.GOLD + "[DEBUG] Death Cause: " + deathCause.toString());
				player.sendMessage(ChatColor.GOLD + "[DEBUG] The Game Defines The Killer As: " + player.getKiller());
				player.sendMessage(ChatColor.GOLD
						+ "There is a chance you were meant to be killed by an ability. Please report this message to staff and include the death cause above.");

				arena.sendMessage(arena.getTeam(player).getColor() + player.getName() + ChatColor.GRAY + " has died.");
			}

		}

		Player player = (Player) e.getEntity();
		Arena arena = Manager.getArena(player);
		if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE))

		{
			new BukkitRunnable() {
				@Override
				public void run() {
					player.spigot().respawn();

					cancel();
				}
			}.runTaskLater(ClashRoyaleMain.getInstance(), 2L);
		}
	}

}
