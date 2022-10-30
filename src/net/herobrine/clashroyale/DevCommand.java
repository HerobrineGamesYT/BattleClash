package net.herobrine.clashroyale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;

public class DevCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (HerobrinePVPCore.getFileManager().getRank(player).getPermLevel() >= 9) {
				if (Manager.isPlaying(player)) {

					Arena arena = Manager.getArena(player);

					if (arena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)
							&& arena.getState().equals(GameState.LIVE)) {

						if (args.length == 0) {

							player.sendMessage(ChatColor.AQUA + "Battle Clash Dev Tools");
							// player.sendMessage(ChatColor.AQUA
							// + "- /bcdev addkills <number> <player/team>: Add kills to your kill count. To
							// add kills to another player or team, use the third argument.");
							// player.sendMessage(ChatColor.GOLD
							// + "- /bcdev setkills <player/team> <number> - Set the kill count of a team or
							// player.");
							player.sendMessage(
									ChatColor.AQUA + "- /bcdev changekit <kit>: Change your class mid-game.");
							player.sendMessage(ChatColor.AQUA
									+ "- /bcdev getdamagecause <player>: Get the last known damage cause of the player.");
							player.sendMessage(ChatColor.AQUA
									+ "- /bcdev settime <time>: Set the timer to a certain time. The timer cannot be set higher than 30 minutes. Time is in minutes or seconds.");
							player.sendMessage(ChatColor.AQUA + "");

						}

						else if (args.length == 1) {
							if (args[0].equalsIgnoreCase("help")) {
								player.sendMessage(ChatColor.AQUA + "Battle Clash Dev Tools");
								// player.sendMessage(ChatColor.AQUA
								// + "- /bcdev addkills <number> <player/team>: Add kills to a player.");
								// player.sendMessage(ChatColor.GOLD
								// + "- /bcdev setkills <player/team> <number> - Set the kill count of a team or
								// player.");
								player.sendMessage(
										ChatColor.AQUA + "- /bcdev changekit <kit>: Change your class mid-game.");
								player.sendMessage(ChatColor.AQUA
										+ "- /bcdev getdamagecause <player>: Get the last known damage cause of the player.");
								player.sendMessage(ChatColor.AQUA
										+ "- /bcdev settime <time>: Set the timer to a certain time. The timer cannot be set higher than 30 minutes. Time is in minutes or seconds.");
								player.sendMessage(ChatColor.AQUA + "");

							} else {
								player.sendMessage(
										ChatColor.RED + "Invalid usage! Use /bcdev help to see all of your options.");
							}

						}

						else if (args.length == 2) {

							if (args[0].equalsIgnoreCase("getdamagecause")) {

								Player target = Bukkit.getPlayer(args[1]);

								if (target != null) {

									if (Manager.isPlaying(target)) {

										Arena targetArena = Manager.getArena(target);
										if (targetArena.getGame(arena.getID()).equals(Games.CLASH_ROYALE)) {

											player.sendMessage(
													ChatColor.GREEN + "Last Damage Cause Info For " + target.getName());
											player.sendMessage(ChatColor.GREEN + "Last Minecraft Damage Cause: "
													+ ChatColor.AQUA + target.getLastDamageCause().getCause());
											if (GameListener.customDeathCause.containsKey(target.getUniqueId())) {
												player.sendMessage(ChatColor.GREEN + "Last Custom Damage Cause: "
														+ ChatColor.AQUA
														+ GameListener.customDeathCause.get(target.getUniqueId()));
											} else {
												player.sendMessage(ChatColor.GREEN + "Last Custom Damage Cause: "
														+ ChatColor.RED + "None!");
											}
											if (GameListener.lastAbilityAttacker.containsKey(target.getUniqueId())) {

												player.sendMessage(
														ChatColor.GREEN + "Last Ability Attacker: " + ChatColor.AQUA
																+ Bukkit.getPlayer(GameListener.lastAbilityAttacker
																		.get(target.getUniqueId())).getName());

											} else {
												player.sendMessage(ChatColor.GREEN + "Last Ability Attacker: "
														+ ChatColor.RED + "None!");
											}

										}

										else {
											player.sendMessage(
													ChatColor.RED + "That player is not playing Battle Clash!");
										}

									} else {
										player.sendMessage(ChatColor.RED + "That player is not playing any minigames.");
									}

								} else {
									player.sendMessage(ChatColor.RED + "That is not a valid player!");
								}

							} else if (args[0].equalsIgnoreCase("settime")) {

								if (args[1] != null) {

									try {

										if (args[1].endsWith("s") || args[1].endsWith("seconds")) {

											if (args[1].endsWith("s")) {
												args[1] = args[1].replaceAll("s", "");
											} else {
												args[1] = args[1].replaceAll("seconds", "");
											}

											int time = Integer.parseInt(args[1]);
											if (time / 60 > 30) {
												player.sendMessage(ChatColor.RED
														+ "You can't set the timer higher than 30 minutes!");
											} else {

												arena.getBattleClash().setTime(time);
												String newTime = String.format("%02d:%02d",
														arena.getBattleClash().getSeconds() / 60,
														arena.getBattleClash().getSeconds() % 60);
												arena.sendMessage(HerobrinePVPCore.translateString(
														"&a&lTIME! &eThe timer has been set to " + newTime + " by &c")
														+ player.getName());
											}

										}

										else if (args[1].endsWith("m") || args[1].endsWith("minutes")) {
											if (args[1].endsWith("m")) {
												args[1] = args[1].replaceAll("m", "");
											} else {
												args[1] = args[1].replaceAll("minutes", "");
											}

											int time = Integer.parseInt(args[1]);
											if (time > 30) {
												player.sendMessage(ChatColor.RED
														+ "You can't set the timer higher than 30 minutes!");

											} else {
											arena.getBattleClash().setTime(time * 60);
												String newTime = String.format("%02d:%02d",
														arena.getBattleClash().getSeconds() / 60,
														arena.getBattleClash().getSeconds() % 60);
												arena.sendMessage(HerobrinePVPCore.translateString(
														"&a&lTIME! &eThe timer has been set to " + newTime + " by &c")
														+ player.getName());

											}

										}

										else {
											player.sendMessage(ChatColor.RED
													+ "Invalid time provided. Note that you can only put the time in units of minutes(m/minutes) and seconds (s/seconds)");
										}

									} catch (NumberFormatException e) {
										player.sendMessage(
												ChatColor.RED + "Invalid time provided. Did you put a number?");
									}

								}

							} else if (args[0].equalsIgnoreCase("changekit")) {

								try {
									if (ClassTypes.valueOf(args[1].toUpperCase()) != null) {

										ClassTypes newClass = ClassTypes.valueOf(args[1].toUpperCase());

										if (arena.getClass(player) == newClass) {
											player.sendMessage(ChatColor.RED + "You are already using this class!");
										} else if(newClass.getGame().equals(Games.CLASH_ROYALE)) {
											arena.setClass(player.getUniqueId(), newClass);
											for (PotionEffect effect : player.getActivePotionEffects()) {

												player.removePotionEffect(effect.getType());
											}
											player.getEquipment().setHelmet(null);
											player.getEquipment().setChestplate(null);
											player.getEquipment().setLeggings(null);
											player.getEquipment().setBoots(null);
											arena.getClasses().get(player.getUniqueId())
													.onStart(Bukkit.getPlayer(player.getUniqueId()));
											player.sendMessage(ChatColor.AQUA + "You switched to the "
													+ arena.getClass(player).getDisplay() + ChatColor.AQUA + " class!");
										}
										else {
											player.sendMessage(ChatColor.RED + "You can only switch to classes from Battle Clash!");
										}

									} else {
										player.sendMessage(ChatColor.RED + "Invalid class type!");
									}

								}

								catch (IllegalArgumentException e) {
									player.sendMessage(ChatColor.RED + "Invalid class type!");
								}

							}

							else {
								player.sendMessage(
										ChatColor.RED + "Invalid usage! Use /bcdev help to see all of your options.");
							}

						}

						else if (args.length == 3) {
							player.sendMessage(
									ChatColor.RED + "Invalid usage! Use /bcdev help to see all of your options.");
						}

						else {
							player.sendMessage(
									ChatColor.RED + "Invalid usage! Use /bcdev help to see all of your options.");
						}

					} else {
						player.sendMessage(
								ChatColor.GREEN + "You must be in a live game of Battle Clash to use this command!");
					}

				}

			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
			}

		}

		else {
			sender.sendMessage(ChatColor.RED + "You must use this command in-game.");
		}

		return false;
	}

}
