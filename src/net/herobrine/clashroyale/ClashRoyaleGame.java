package net.herobrine.clashroyale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import net.herobrine.core.LevelRewards;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.herobrine.clashroyale.beta.Cannon;
import net.herobrine.clashroyale.beta.Cannon.CannonType;
import net.herobrine.clashroyale.beta.ConfigManager;
import net.herobrine.clashroyale.beta.Region;
import net.herobrine.clashroyale.beta.Tower;
import net.herobrine.clashroyale.beta.Tower.TowerTypes;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.SongPlayer;
import net.herobrine.core.Songs;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.GameCoreMain;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.GameType;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import net.herobrine.gamecore.Teams;

public class ClashRoyaleGame {
	private Arena arena;
	private ClashRoyaleMain main;
	private static ClashRoyaleGame instance;
	private int seconds;
	private int endSeconds;
	private Teams winner;
	public static final List<UUID> alivePlayers = new ArrayList<>();
	public HashMap<UUID, Integer> kills = new HashMap<>();
	public static int redKillCount;
	public static  int blueKillCount;
	public static int redCrowns;
	public static int blueCrowns;
	public static int redTowerHealth;

	public static int redLeftPrincessTowerHealth;
	public static int redRightPrincessTowerHealth;
	public static int blueTowerHealth;
	public static int blueLeftPrincessTowerHealth;
	public static int blueRightPrincessTowerHealth;
	public static boolean isCannonActivated;
	public static boolean isTowerActivated;
	public static GameType gameType;
	BukkitRunnable runnable;
	private Cannon redKing1;
	private Cannon redKing2;
	private Cannon redLeftPrincess;
	private Cannon redRightPrincess;
	private Cannon blueKing1;
	private Cannon blueKing2;
	private Cannon blueLeftPrincess;
	private Cannon blueRightPrincess;
	public HashMap<String, Tower> towerList = new HashMap<>();
	public HashMap<String, Cannon> cannonList = new HashMap<>();

	public HashMap<UUID, Long> lastHit = new HashMap<>();
	private Tower redKing;
	private Tower blueKing;
	private Tower redPrincess1;
	private Tower redPrincess2;
	private Tower bluePrincess1;
	private Tower bluePrincess2;
	private boolean suddenDeath;
	private boolean isSpecial;

	public ClashRoyaleGame(Arena arena, boolean isSpecial) {
		this.arena = arena;
		this.seconds = 300;
		this.isSpecial = isSpecial;
		instance = this;

		if (isSpecial) {
			initializeCannons();
			initializeTowers();
		}

	}

	public ClashRoyaleGame(ClashRoyaleMain main) {
		this.main = main;
	}

	public static void freezeEntity(Entity en) {
		net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) en).getHandle();
		NBTTagCompound compound = new NBTTagCompound();
		nmsEn.c(compound);
		compound.setByte("NoAI", (byte) 1);
		nmsEn.f(compound);
	}

	public static List<UUID> getAlivePlayers() {

		return alivePlayers;
	}

	public static ClashRoyaleGame getInstance() {
		return instance;
	}

	public int getSeconds() {
		return seconds;
	}

	public HashMap<UUID, Integer> getKills() {
		return kills;
	}

	public void initializeTowers() {

		this.redKing = new Tower("red_king", TowerTypes.KING, false, Teams.RED, ChatColor.RED + "Red King Tower");
		this.blueKing = new Tower("blue_king", TowerTypes.KING, false, Teams.BLUE, ChatColor.BLUE + "Blue King Tower");
		this.redPrincess1 = new Tower("left_red_princess", TowerTypes.PRINCESS, true, Teams.RED,
				ChatColor.RED + "Left Princess Tower");
		this.redPrincess2 = new Tower("right_red_princess", TowerTypes.PRINCESS, true, Teams.RED,
				ChatColor.RED + "Right Princess Tower");
		this.bluePrincess1 = new Tower("left_blue_princess", TowerTypes.PRINCESS, true, Teams.BLUE,
				ChatColor.BLUE + "Left Princess Tower");
		this.bluePrincess2 = new Tower("right_blue_princess", TowerTypes.PRINCESS, true, Teams.BLUE,
				ChatColor.BLUE + "Right Princess Tower");

		redKing.setCannon(new Cannon[] { redKing1, redKing2 });
		blueKing.setCannon(new Cannon[] { blueKing1, blueKing2 });
		redPrincess1.setCannon(new Cannon[] { redLeftPrincess });
		redPrincess2.setCannon(new Cannon[] { redRightPrincess });
		bluePrincess1.setCannon(new Cannon[] { blueLeftPrincess });
		bluePrincess2.setCannon(new Cannon[] { blueRightPrincess });

		towerList.put("red_king", redKing);
		towerList.put("blue_king", blueKing);
		towerList.put("left_red_princess", redPrincess1);
		towerList.put("right_red_princess", redPrincess2);
		towerList.put("left_blue_princess", bluePrincess1);
		towerList.put("right_blue_princess", bluePrincess2);

		for (String keys : towerList.keySet()) {

			System.out.println(keys);
			System.out.println(towerList.get(keys));

		}

	}

	public void resetTowers() {
		towerList.clear();

		towerList.put("red_king", redKing);
		towerList.put("blue_king", blueKing);
		towerList.put("left_red_princess", redPrincess1);
		towerList.put("right_red_princess", redPrincess2);
		towerList.put("left_blue_princess", bluePrincess1);
		towerList.put("right_blue_princess", bluePrincess2);
	}

	public HashMap<String, Tower> getTower() {

		return towerList;

	}

	public HashMap<UUID, Long> getTowerHits() {
		return lastHit;
	}

	public boolean getSuddenDeath() {
		return suddenDeath;
	}

	public void isGameOver() {

		if (gameType.equals(GameType.VANILLA)) {
			if (redKillCount > blueKillCount) {

				GameListener.customDeathCause.clear();
				GameListener.lastAbilityAttacker.clear();
				arena.setState(GameState.LIVE_ENDING);
				winner = Teams.RED;

				for (UUID uuid : arena.getPlayers()) {

					Player player = (Player) Bukkit.getPlayer(uuid);

					if (arena.getTeam(player) == winner) {

						SongPlayer.playSong(player, Songs.BCWIN);

					} else {
						SongPlayer.playSong(player, Songs.BCLOSE);
					}

				}

				runnable.cancel();
				runnable = null;
				startEnding(winner.getDisplay());

			} else if (blueKillCount > redKillCount) {

				GameListener.customDeathCause.clear();
				GameListener.lastAbilityAttacker.clear();
				arena.setState(GameState.LIVE_ENDING);
				winner = Teams.BLUE;

				for (UUID uuid : arena.getPlayers()) {

					Player player = (Player) Bukkit.getPlayer(uuid);

					if (arena.getTeam(player) == winner) {

						SongPlayer.playSong(player, Songs.BCWIN);

					} else {
						SongPlayer.playSong(player, Songs.BCLOSE);
					}

				}

				runnable.cancel();
				runnable = null;
				startEnding(winner.getDisplay());

			} else {

				GameListener.customDeathCause.clear();
				GameListener.lastAbilityAttacker.clear();
				arena.setState(GameState.LIVE_ENDING);
				startEnding(ChatColor.YELLOW + "DRAW!");

				for (UUID uuid : arena.getPlayers()) {

					Player player = (Player) Bukkit.getPlayer(uuid);

					SongPlayer.playSong(player, Songs.BCDRAW);

				}

			}
		}

		else {

			if (redCrowns > blueCrowns) {
				// red wins

				GameListener.customDeathCause.clear();
				GameListener.lastAbilityAttacker.clear();
				arena.setState(GameState.LIVE_ENDING);
				winner = Teams.RED;

				for (UUID uuid : arena.getPlayers()) {

					Player player = (Player) Bukkit.getPlayer(uuid);

					if (arena.getTeam(player) == winner) {

						SongPlayer.playSong(player, Songs.BCWIN);

					} else {
						SongPlayer.playSong(player, Songs.BCLOSE);
					}
				}
				runnable.cancel();
				runnable = null;
				startEnding(winner.getDisplay());
			}

			else if (blueCrowns > redCrowns) {
				// blue wins

				GameListener.customDeathCause.clear();
				GameListener.lastAbilityAttacker.clear();
				arena.setState(GameState.LIVE_ENDING);
				winner = Teams.BLUE;

				for (UUID uuid : arena.getPlayers()) {

					Player player = (Player) Bukkit.getPlayer(uuid);

					if (arena.getTeam(player) == winner) {

						SongPlayer.playSong(player, Songs.BCWIN);

					} else {
						SongPlayer.playSong(player, Songs.BCLOSE);
					}

				}
				runnable.cancel();
				runnable = null;
				startEnding(winner.getDisplay());

			}

			else {

				if (!suddenDeath) {
					suddenDeath = true;
					seconds = 120;

					arena.sendMessage(ChatColor.GREEN + "Sudden Death has begun! The first team to take a tower wins!");
					arena.playSound(Sound.ENDERDRAGON_GROWL);
					// sudden death

				} else {
					// draw
					GameListener.customDeathCause.clear();
					GameListener.lastAbilityAttacker.clear();
					arena.setState(GameState.LIVE_ENDING);
					startEnding(ChatColor.YELLOW + "DRAW!");

					for (UUID uuid : arena.getPlayers()) {

						Player player = (Player) Bukkit.getPlayer(uuid);

						SongPlayer.playSong(player, Songs.BCDRAW);

					}

				}

			}

		}

	}

	public void startEnding(String winningTeam) {

		for (UUID uuid : arena.getPlayers()) {

			arena.removeClass(uuid);
			Player player = Bukkit.getPlayer(uuid);

			if (arena.getTeam(player).getDisplay() == winningTeam
					&& !winningTeam.equalsIgnoreCase(ChatColor.YELLOW + "DRAW!")) {

				if (arena.getTeam(player) == Teams.RED) {

					if (isSpecial) {
						GameCoreMain.getInstance()
								.sendTitle(
										player, "&6&lVICTORY", "&7Your team got the most crowns! " + ChatColor.RED
												+ redCrowns + ChatColor.GRAY + "-" + ChatColor.BLUE + blueCrowns,
										0, 3, 0);


						HerobrinePVPCore.getFileManager().setGameStats(player.getUniqueId(), Games.CLASH_ROYALE, "wins",
								HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.CLASH_ROYALE,
										"wins") + 1);

					} else {
						GameCoreMain.getInstance()
								.sendTitle(
										player, "&6&lVICTORY", "&7Your team got the most kills! " + ChatColor.RED
												+ redKillCount + ChatColor.GRAY + "-" + ChatColor.BLUE + blueKillCount,
										0, 3, 0);
						HerobrinePVPCore.getFileManager().setGameStats(player.getUniqueId(), Games.CLASH_ROYALE, "wins",
								HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.CLASH_ROYALE,
										"wins") + 1);
					}

				} else {
					if (isSpecial) {
						GameCoreMain.getInstance()
								.sendTitle(
										player, "&6&lVICTORY", "&7Your team got the most crowns! " + ChatColor.BLUE
												+ blueCrowns + ChatColor.GRAY + "-" + ChatColor.RED + redCrowns,
										0, 3, 0);
						HerobrinePVPCore.getFileManager().setGameStats(player.getUniqueId(), Games.CLASH_ROYALE, "wins",
								HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.CLASH_ROYALE,
										"wins") + 1);
					} else {
						GameCoreMain.getInstance()
								.sendTitle(
										player, "&6&lVICTORY", "&7Your team got the most kills! " + ChatColor.BLUE
												+ blueKillCount + ChatColor.GRAY + "-" + ChatColor.RED + redKillCount,
										0, 3, 0);
						HerobrinePVPCore.getFileManager().setGameStats(player.getUniqueId(), Games.CLASH_ROYALE, "wins",
								HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.CLASH_ROYALE,
										"wins") + 1);
					}

				}

			} else if (arena.getTeam(player).getDisplay() != winningTeam
					&& !winningTeam.equalsIgnoreCase(ChatColor.YELLOW + "DRAW!")) {
				GameCoreMain.getInstance().sendTitle(player, "&c&lGAME OVER", "&7Your team didn't win this time.", 0, 3,
						0);
			} else {
				if (isSpecial) {
					GameCoreMain.getInstance().sendTitle(player, "&e&lDRAW", "The game ended in a draw! "
							+ ChatColor.RED + redCrowns + ChatColor.GRAY + "-" + ChatColor.BLUE + blueCrowns, 0, 3, 0);
				} else {
					GameCoreMain.getInstance().sendTitle(player, "&e&lDRAW", "The game ended in a draw! "
							+ ChatColor.RED + redKillCount + ChatColor.GRAY + "-" + ChatColor.BLUE + blueKillCount, 0,
							3, 0);
				}

			}

		}

		arena.sendMessage(
				ChatColor.translateAlternateColorCodes('&', "&a&m&l----------------------------------------"));

		arena.sendMessage(ChatColor.translateAlternateColorCodes('&', "                   &f&lBattle Clash"));
		arena.sendMessage("");
		arena.sendMessage(ChatColor.YELLOW + "Winner " + ChatColor.GRAY + "- " + winningTeam);
		arena.sendMessage("");
		List<UUID> keys = kills.entrySet().stream().sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
				.limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

		arena.sendMessage(ChatColor.GOLD + "                   Most Kills");

		if (keys.size() >= 1) {
			Player player1 = Bukkit.getPlayer(keys.get(0));

			if (player1 != null) {
				if (Manager.isPlaying(player1)) {

					if (Manager.getArena(player1).getID() == arena.getID()) {
						arena.sendMessage(arena.getTeam(player1).getColor() + player1.getName() + ChatColor.GRAY + " - "
								+ kills.get(player1.getUniqueId()));
					} else {
						arena.sendMessage(ChatColor.GRAY + player1.getName() + ChatColor.GRAY + " - "
								+ kills.get(player1.getUniqueId()));
					}
				} else {
					arena.sendMessage(ChatColor.GRAY + player1.getName() + ChatColor.GRAY + " - "
							+ kills.get(player1.getUniqueId()));
				}

			}

		} else {
			arena.sendMessage(ChatColor.RED + "No players found. Hello? Is anyone here?");
			System.out.println("No players.");
		}

		if (keys.size() >= 2) {
			Player player2 = Bukkit.getPlayer(keys.get(1));
			if (player2 != null) {
				if (Manager.isPlaying(player2)) {

					if (Manager.getArena(player2).getID() == arena.getID()) {
						arena.sendMessage(arena.getTeam(player2).getColor() + player2.getName() + ChatColor.GRAY + " - "
								+ kills.get(player2.getUniqueId()));
					} else {
						arena.sendMessage(ChatColor.GRAY + player2.getName() + ChatColor.GRAY + " - "
								+ kills.get(player2.getUniqueId()));
					}
				} else {
					arena.sendMessage(ChatColor.GRAY + player2.getName() + ChatColor.GRAY + " - "
							+ kills.get(player2.getUniqueId()));
				}

			}
		}

		if (keys.size() == 3) {
			Player player3 = Bukkit.getPlayer(keys.get(2));
			if (player3 != null) {
				if (Manager.isPlaying(player3)) {

					if (Manager.getArena(player3).getID() == arena.getID()) {
						arena.sendMessage(arena.getTeam(player3).getColor() + player3.getName() + ChatColor.GRAY + " - "
								+ kills.get(player3.getUniqueId()));
					} else {
						arena.sendMessage(ChatColor.GRAY + player3.getName() + ChatColor.GRAY + " - "
								+ kills.get(player3.getUniqueId()));
					}
				} else {
					arena.sendMessage(ChatColor.GRAY + player3.getName() + ChatColor.GRAY + " - "
							+ kills.get(player3.getUniqueId()));
				}
			}
		}


		arena.sendMessage("");

		arena.sendMessage(ChatColor.GREEN + "Rewards: ");

		if (winningTeam.equalsIgnoreCase(ChatColor.YELLOW + "DRAW!")) {
			arena.distributeRewards(Teams.PLACEHOLDER);
		}
		else {
			arena.distributeRewards(winner);
		}

		arena.sendMessage(
				ChatColor.translateAlternateColorCodes('&', "&a&m&l----------------------------------------"));

		kills.clear();
		endSeconds = 5;


		new BukkitRunnable() {

			@Override
			public void run() {

				if (endSeconds == 0) {
					cancel();
					arena.reset();
				}

				endSeconds--;
			}
		}.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 20L);
	}

	public ClassTypes randomClass() {
		int i = 0;
		do {
			int pick2 = new Random().nextInt(ClassTypes.values().length);
			if (ClassTypes.values()[pick2].getGame().equals(Games.CLASH_ROYALE)
					&& !ClassTypes.values()[pick2].isUnlockable()) {
				i = 1;
				return ClassTypes.values()[pick2];
			}
		} while (i != 1);
		return null;

	}

	public void initializeCannons() {

		// kings start as false because they require a princess to be destroyed first
		// cannontype, name, arenaID, spawnLocation, targetingTeam, isActive
		//TODO Make Cannon Location Configurable
		redKing1 = new Cannon(CannonType.KING, "red_king_1", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 123, 91 + 1, 195), Teams.BLUE, false);
		redKing2 = new Cannon(CannonType.KING, "red_king_2", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 106, 91 + 1, 195), Teams.BLUE, false);
		blueKing1 = new Cannon(CannonType.KING, "blue_king_1", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 106, 92, 96), Teams.RED, false);
		blueKing2 = new Cannon(CannonType.KING, "blue_king_2", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 123, 91, 97), Teams.RED, false);

		redLeftPrincess = new Cannon(CannonType.PRINCESS, "red_princess_1", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 93, 89 + 1, 179), Teams.BLUE, true);
		redRightPrincess = new Cannon(CannonType.PRINCESS, "red_princess_2", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 137, 88, 177), Teams.BLUE, true);

		blueLeftPrincess = new Cannon(CannonType.PRINCESS, "blue_princess_1", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 93, 88, 112), Teams.RED, true);

		blueRightPrincess = new Cannon(CannonType.PRINCESS, "blue_princess_2", arena.getID(),
				new Location(arena.getSpawn().getWorld(), 136, 88, 112), Teams.RED, true);

		cannonList.put(redKing1.getID(), redKing1);
		cannonList.put(redKing2.getID(), redKing2);
		cannonList.put(blueKing1.getID(), blueKing1);
		cannonList.put(blueKing2.getID(), blueKing2);
		cannonList.put(redLeftPrincess.getID(), redLeftPrincess);
		cannonList.put(redRightPrincess.getID(), redRightPrincess);
		cannonList.put(blueLeftPrincess.getID(), blueLeftPrincess);
		cannonList.put(blueRightPrincess.getID(), blueRightPrincess);

	}

	public void disableCannons() {

		redKing1.setActive(false);
		redKing2.setActive(false);
		blueKing1.setActive(false);
		blueKing2.setActive(false);

		redLeftPrincess.setActive(false);
		redRightPrincess.setActive(false);
		blueLeftPrincess.setActive(false);
		blueRightPrincess.setActive(false);

	}

	public void enableCannons() {

		redKing1.setActive(false);
		redKing2.setActive(false);
		blueKing1.setActive(false);
		blueKing2.setActive(false);

		redLeftPrincess.setActive(true);
		redRightPrincess.setActive(true);
		blueLeftPrincess.setActive(true);
		blueRightPrincess.setActive(true);

	}

	public void start(GameType type) {

		gameType = type;
		arena.setState(GameState.LIVE);
		redKillCount = 0;
		blueKillCount = 0;

		redCrowns = 0;
		blueCrowns = 0;
		suddenDeath = false;
		if (type == GameType.CLASH_ROYALE) {
			ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();
			ConfigManager configManager = new ConfigManager(plugin);
			for (String b : plugin.getConfig().getConfigurationSection("towers").getKeys(false)) {

				System.out.println(b);
				if (!b.equals("world") && !b.equals("arena")) {
					configManager.setRegion(b);
				}
			}
			for (Tower tower : towerList.values()) {
				if (tower.getType().equals(TowerTypes.KING)) {
					tower.setHealth(1000);
					tower.setEnabled(false);

				} else {
					tower.setHealth(500);
					tower.setEnabled(true);
				}

			}
			enableCannons();

			// TODO Spawn health progression holograms

		}

		for (UUID uuid : arena.getClasses().keySet()) {

			arena.getClasses().get(uuid).onStart(Bukkit.getPlayer(uuid));

		}
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		Date dateobj = new Date();
		System.out.println(df.format(dateobj));
		System.out.println("Battle Clash Game Starting In Arena " + arena.getID() + "!");
		for (UUID uuid : arena.getPlayers()) {
			System.out.println("WORKING ON UUID:" + uuid);
			Player player = Bukkit.getPlayer(uuid);
			alivePlayers.add(uuid);

			kills.put(uuid, 0);

			HerobrinePVPCore.getFileManager().setGameStats(player.getUniqueId(), Games.CLASH_ROYALE, "roundsPlayed",
					HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.CLASH_ROYALE,
							"roundsPlayed") + 1);
			if (!arena.getClasses().containsKey(player.getUniqueId())) {
				arena.setClass(player.getUniqueId(), randomClass());
				arena.getClasses().get(uuid).onStart(Bukkit.getPlayer(uuid));
				player.sendMessage(ChatColor.GREEN + "You didn't select a class, so we picked out the "
						+ arena.getClass(player).getDisplay() + ChatColor.GREEN + " class for you!");
			}

			if (arena.getTeam(player).equals(Teams.RED)) {
				player.teleport(Config.getRedTeamSpawn(arena.getID()));

				player.setDisplayName(ChatColor.RED + player.getName());
			} else if (arena.getTeam(player).equals(Teams.BLUE)) {
				player.teleport(Config.getBlueTeamSpawn(arena.getID()));

			} else {
				player.sendMessage(ChatColor.RED
						+ "Couldn't send you to your teams spawn point! Reason: You are not on a team. Please report this to staff, as you shouldn't be getting this error.");

			}

			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective obj = board.registerNewObjective("game", "dummy");
			obj.setDisplayName(ChatColor.AQUA + "Battle Clash");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);

			Team dateAndID = board.registerNewTeam("dateandid");
			dateAndID.addEntry(ChatColor.DARK_RED.toString());
			dateAndID.setPrefix(ChatColor.GRAY + df.format(dateobj) + ChatColor.DARK_GRAY + " cr" + arena.getID());
			obj.getScore(ChatColor.DARK_RED.toString()).setScore(10);

			Team timer = board.registerNewTeam("crtimer");
			timer.addEntry(ChatColor.LIGHT_PURPLE.toString());
			timer.setPrefix(ChatColor.AQUA + "Time Left: ");
			String time = String.format("%02d:%02d", seconds / 60, seconds % 60);
			timer.setSuffix(ChatColor.GREEN + time);
			obj.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(9);

			Score blank1 = obj.getScore(" ");
			blank1.setScore(8);

			Team rank = board.registerNewTeam("crteam");

			rank.addEntry(ChatColor.BLUE.toString());
			rank.setPrefix(ChatColor.AQUA + "Team: ");
			rank.setSuffix(ChatColor.GRAY + arena.getTeam(player).getDisplay());
			obj.getScore(ChatColor.BLUE.toString()).setScore(7);

			Team block = board.registerNewTeam("kills");
			block.addEntry(ChatColor.GOLD.toString());
			block.setPrefix(ChatColor.AQUA + "Kills: ");
			block.setSuffix(ChatColor.GREEN + kills.get(player.getUniqueId()).toString());
			obj.getScore(ChatColor.GOLD.toString()).setScore(6);

			Score blank2 = obj.getScore("  ");
			blank2.setScore(5);

			if (type.equals(GameType.VANILLA)) {
				Team yourTeamKills = board.registerNewTeam("yourKills");
				yourTeamKills.addEntry(ChatColor.RED.toString());
				yourTeamKills.setPrefix(ChatColor.AQUA + "Team Kills: ");

				if (arena.getTeam(player).equals(Teams.RED)) {
					yourTeamKills.setSuffix(ChatColor.GREEN + "" + redKillCount);
				} else {
					yourTeamKills.setSuffix(ChatColor.GREEN + "" + blueKillCount);
				}

				obj.getScore(ChatColor.RED.toString()).setScore(4);

				Team enemyTeamKills = board.registerNewTeam("enemyKills");
				enemyTeamKills.addEntry(ChatColor.YELLOW.toString());
				enemyTeamKills.setPrefix(ChatColor.AQUA + "Enemy Kills: ");

				if (arena.getTeam(player).equals(Teams.RED)) {
					enemyTeamKills.setSuffix(ChatColor.GREEN + "" + blueKillCount);
				} else {
					enemyTeamKills.setSuffix(ChatColor.GREEN + "" + redKillCount);
				}
				obj.getScore(ChatColor.YELLOW.toString()).setScore(3);

				Score blank3 = obj.getScore("   ");
				blank3.setScore(2);

				Score ip = obj.getScore(ChatColor.translateAlternateColorCodes('&', "&cherobrinepvp.beastmc.com"));
				ip.setScore(1);
			} else {

				Team redTower = board.registerNewTeam("redTower");
				redTower.addEntry(ChatColor.RED.toString());
				redTower.setPrefix(ChatColor.RED + "Main Tower: ");
				redTower.setSuffix(ChatColor.GREEN + "" + redKing.getHealth() + ChatColor.RED + "❤");

				obj.getScore(ChatColor.RED.toString()).setScore(4);

				Team blueTower = board.registerNewTeam("blueTower");
				blueTower.addEntry(ChatColor.YELLOW.toString());
				blueTower.setPrefix(ChatColor.BLUE + "Main Tower: ");
				blueTower.setSuffix(ChatColor.GREEN + "" + blueKing.getHealth() + ChatColor.RED + "❤");

				obj.getScore(ChatColor.YELLOW.toString()).setScore(3);

				Score blank3 = obj.getScore("   ");
				blank3.setScore(2);

				Score ip = obj.getScore(ChatColor.translateAlternateColorCodes('&', "&cherobrinepvp.beastmc.com"));
				ip.setScore(1);

				actionBar();

			}

			int nameCount = 0;
			Team redTeam = board.registerNewTeam("redTeam");
			redTeam.setDisplayName(ChatColor.RED + "RED");
			redTeam.setPrefix(ChatColor.RED + "RED ");

			Team blueTeam = board.registerNewTeam("blueTeam");

			blueTeam.setDisplayName(ChatColor.BLUE + "BLUE");
			blueTeam.setPrefix(ChatColor.BLUE + "BLUE ");
			for (UUID uuid1 : arena.getPlayers()) {

				Player player1 = Bukkit.getPlayer(uuid1);

				if (arena.getTeam(player1).equals(Teams.RED)) {
					redTeam.addPlayer(player1);
				} else {
					blueTeam.addPlayer(player1);

				}
				nameCount++;
			}

			player.setScoreboard(board);

		}

		if (type.equals(GameType.VANILLA)) {
			arena.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&a&m&l----------------------------------------"));
			arena.sendMessage(ChatColor.translateAlternateColorCodes('&', "                   &f&lBattle Clash"));
			arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&e&lUse your kit items and abilities to fight for your team. The team with the most kills after 5 minutes wins!\n&b&lClasses based on 'Clash Royale' by Supercell"));
			arena.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&a&m&l----------------------------------------"));

		} else {
			arena.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&a&m&l----------------------------------------"));
			arena.sendMessage(ChatColor.translateAlternateColorCodes('&', "                   &f&lBattle Clash"));
			arena.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&e&lDefend your towers and destroy the enemy towers! The person with more towers standing at the end wins! \nDestroy the king tower after taking out at least one princess for an instant victory!"));
			arena.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&a&m&l----------------------------------------"));

		}
		startFiring();
		startTimer();
	}

	public void startTimer() {
		seconds = 300;
		runnable = new BukkitRunnable() {

			@Override
			public void run() {
				if (arena.getState().equals(GameState.RECRUITING) || arena.getState().equals(GameState.COUNTDOWN)) {
					runnable.cancel();
					runnable = null;
				}

				for (UUID uuid : arena.getPlayers()) {
					Player player = Bukkit.getPlayer(uuid);
					String time = String.format("%02d:%02d", seconds / 60, seconds % 60);

					player.getScoreboard().getTeam("crtimer").setSuffix(ChatColor.GREEN + time);
					if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()
							.equals(Material.BARRIER)) {
						player.setHealth(0.0);
						player.sendMessage(ChatColor.RED + "Don't try to leave the playing area!");
					}
				}

				if (seconds <= 0 && suddenDeath) {
					runnable.cancel();
					isGameOver();
					runnable = null;
				}

				else if (seconds <= 0 && !suddenDeath && isSpecial) {
					isGameOver();
				}
				else if (seconds <= 0 && !isSpecial) {
					runnable.cancel();
					isGameOver();
					runnable = null;
				}
				if (seconds == 180) {
					arena.playSound(Sound.CLICK);
					arena.sendMessage(ChatColor.YELLOW + "The game" + " will end in " + ChatColor.GREEN + "3"
							+ ChatColor.YELLOW + " minutes!");
				}

				if (seconds == 120) {
					arena.playSound(Sound.CLICK);
					arena.sendMessage(ChatColor.YELLOW + "The game" + " will end in " + ChatColor.GREEN + "2"
							+ ChatColor.YELLOW + " minutes!");
				}
				if (seconds == 30) {
					arena.playSound(Sound.CLICK);
					arena.sendMessage(ChatColor.YELLOW + "The game" + " will end in " + ChatColor.GOLD + "30"
							+ ChatColor.YELLOW + " seconds!");
				}
				if (seconds <= 10) {

					if (seconds > 1) {
						arena.playSound(Sound.CLICK);
						arena.sendMessage(ChatColor.YELLOW + "The game" + " will end in " + ChatColor.RED + seconds
								+ ChatColor.YELLOW + " seconds!");
					}
					if (seconds == 1) {
						arena.playSound(Sound.CLICK);
						arena.sendMessage(ChatColor.YELLOW + "The game" + " will end in " + ChatColor.RED + "1"
								+ ChatColor.YELLOW + " second!");
					}

				}

				seconds--;
			}

		};
		runnable.runTaskTimer(ClashRoyaleMain.getInstance(), 0, 20);
	}

	public void actionBar() {
		HashMap<Double, Tower> distances = new HashMap<>();
		new BukkitRunnable() {

			@Override
			public void run() {
				if (arena.getState().equals(GameState.RECRUITING) || arena.getState().equals(GameState.COUNTDOWN)) {
					cancel();
				}

				for (UUID uuid : arena.getPlayers()) {

					Player player = Bukkit.getPlayer(uuid);

					for (Tower tower : towerList.values()) {

						distances.put(player.getLocation().distanceSquared(tower.getRegionLocations()[1]), tower);

					}

					Tower minDistance = distances.get(Collections.min(distances.keySet()));

					if (minDistance.getHealth() <= 0) {
						GameCoreMain.getInstance().sendActionBar(player, "&aNearest Tower Health: " + "&cDEAD");
					} else {
						GameCoreMain.getInstance().sendActionBar(player,
								"&aNearest Tower Health: " + minDistance.getHealth() + "&c❤");
					}

					distances.clear();
				}

			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 2L);

	}


	public void startFiring() {

		new BukkitRunnable() {

			@Override
			public void run() {
				if (arena.getState().equals(GameState.RECRUITING) || arena.getState().equals(GameState.COUNTDOWN)) {
					cancel();
					for (Cannon cannon : cannonList.values()) {
						cannon.setTarget(null);
					}
				}

				for (Cannon cannon : cannonList.values()) {

					for (UUID uuid : arena.getPlayers()) {

						Player player = Bukkit.getPlayer(uuid);

						if (cannon.isActive() && cannon.getTargetTeam().equals(arena.getTeam(player))
								&& !cannon.hasTarget()) {

							cannon.checkForTarget(player);

						}
					}

					if (cannon.isActive() && cannon.hasTarget()) {
						cannon.shootTarget();
					}

				}

			}
		}.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 20L);

	}

	public void setTime(int time) {

		seconds = time;
	}

	// needed to update kill count on scoreboards
	public void updateKillCounts(Player killer) {
		HerobrinePVPCore.getFileManager().setGameStats(killer.getUniqueId(), Games.CLASH_ROYALE, "kills",
				HerobrinePVPCore.getFileManager().getGameStats(killer.getUniqueId(), Games.CLASH_ROYALE, "kills") + 1);
		Teams killerTeam = Manager.getArena(killer).getTeam(killer);

		LevelRewards prestige = HerobrinePVPCore.getFileManager().getPrestige(HerobrinePVPCore.getFileManager().getPlayerLevel(killer.getUniqueId()));
		int baseKillCoins = 15;
		int earnedCoins = (int)Math.round(baseKillCoins * prestige.getGameCoinMultiplier());

		HerobrinePVPCore.getFileManager().addCoins(killer, earnedCoins);
		killer.sendMessage(ChatColor.YELLOW + "+" + earnedCoins + " coins! (Kill)");

		killer.getScoreboard().getTeam("kills").setSuffix(ChatColor.GREEN + "" + kills.get(killer.getUniqueId()));
		HerobrinePVPCore.getFileManager().setGameStats(killer.getUniqueId(), Games.CLASH_ROYALE, "kills",
				HerobrinePVPCore.getFileManager().getGameStats(killer.getUniqueId(), Games.CLASH_ROYALE,
						"kills") + 1);
		if (gameType.equals(GameType.VANILLA)) {
			if (killerTeam.equals(Teams.RED)) {
				killer.getScoreboard().getTeam("yourKills").setSuffix(ChatColor.GREEN + "" + redKillCount);
				killer.getScoreboard().getTeam("enemyKills").setSuffix(ChatColor.GREEN + "" + blueKillCount);
			} else {
				killer.getScoreboard().getTeam("yourKills").setSuffix(ChatColor.GREEN + "" + blueKillCount);
				killer.getScoreboard().getTeam("enemyKills").setSuffix(ChatColor.GREEN + "" + redKillCount);
			}

			for (UUID uuid : Manager.getArena(killer).getPlayers()) {

				Player player = Bukkit.getPlayer(uuid);

				Teams playerTeam = Manager.getArena(player).getTeam(player);

				if (player != killer) {
					if (playerTeam.equals(Teams.RED)) {
						player.getScoreboard().getTeam("yourKills").setSuffix(ChatColor.GREEN + "" + redKillCount);
						player.getScoreboard().getTeam("enemyKills").setSuffix(ChatColor.GREEN + "" + blueKillCount);

					} else {
						player.getScoreboard().getTeam("yourKills").setSuffix(ChatColor.GREEN + "" + blueKillCount);
						player.getScoreboard().getTeam("enemyKills").setSuffix(ChatColor.GREEN + "" + redKillCount);
					}
				}

			}
		}

	}

	public void updateTowerHealth(Arena arena) {

		for (UUID uuid : arena.getPlayers()) {

			Player player = Bukkit.getPlayer(uuid);

			if (redKing.getHealth() <= 0) {
				player.getScoreboard().getTeam("redTower").setSuffix(ChatColor.GREEN + "" + ChatColor.RED + "DEAD");
			} else {
				player.getScoreboard().getTeam("redTower")
						.setSuffix(ChatColor.GREEN + "" + redKing.getHealth() + ChatColor.RED + "❤");
			}

			if (blueKing.getHealth() <= 0) {
				player.getScoreboard().getTeam("blueTower").setSuffix(ChatColor.GREEN + "" + ChatColor.RED + "DEAD");
			} else {
				player.getScoreboard().getTeam("blueTower")
						.setSuffix(ChatColor.GREEN + "" + blueKing.getHealth() + ChatColor.RED + "❤");
			}

		}

	}

	public void deathRageCircle(Location loc, float radius, Player player) {
		for (double t = 0; t < 1000; t += 0.5) {
			float x = radius * (float) Math.sin(t);
			float z = radius * (float) Math.cos(t);

			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL, true,
					(float) loc.getX() + x, (float) loc.getY(), (float) loc.getZ() + z, 0, 0, 0, 0, 1, null);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}

	}

	public static void removeArmorStands(Player player) {

		for (Entity ent : Manager.getArena(player).getSpawn().getWorld().getEntities()) {

			if (ent instanceof ArmorStand) {

				if (ent.getCustomName() == player.getCustomName()) {

					ent.remove();
				}

			}

		}

	}

	public static void banditCooldown(Player player) {

		new BukkitRunnable() {
			int cooldown = 30;

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || Manager.getArena(player).getState().equals(GameState.RECRUITING)) {
					cancel();
				}

				if (!Manager.getArena(player).getState().equals(GameState.LIVE)) {
					cancel();
				}
				if (!Manager.getArena(player).getClass(player).equals(ClassTypes.BANDIT)) {
					cancel();

				}

				if (cooldown == 0) {
					cancel();

					ItemStack dasher = new ItemStack(Material.WOOL, 1, (byte) 13);
					ItemMeta dasherMeta = dasher.getItemMeta();
					dasherMeta.setDisplayName(ChatColor.DARK_GREEN + "Dash");
					dasher.setItemMeta(dasherMeta);
					player.getInventory().setItem(1, dasher);
					player.updateInventory();
				}
				if (player.getInventory().contains(Material.WOOL) || player.getInventory().contains(Material.SULPHUR)) {
					if (cooldown != 0) {
						ItemStack dasherCooldown = new ItemStack(Material.SULPHUR, cooldown);
						ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
						dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Dash (On Cooldown)");
						dasherCooldown.setItemMeta(dasherCooldownMeta);
						player.getInventory().setItem(1, dasherCooldown);
						player.updateInventory();

					} else {
						ItemStack dasher = new ItemStack(Material.WOOL, 1, (byte) 13);
						ItemMeta dasherMeta = dasher.getItemMeta();
						dasherMeta.setDisplayName(ChatColor.DARK_GREEN + "Dash");
						dasher.setItemMeta(dasherMeta);
						player.getInventory().setItem(1, dasher);
						player.updateInventory();
					}

				}
				cooldown--;
			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 20);
	}

	public static void collisionChecker(Player player) {
		new BukkitRunnable() {
			int cooldown = 10;
			ArrayList<UUID> hasHit = new ArrayList<>();

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || Manager.getArena(player).getState().equals(GameState.RECRUITING)) {
					cancel();
					hasHit.clear();
				}

				if (cooldown == 0) {
					cancel();
					hasHit.clear();

				} else {
					for (Entity en : player.getNearbyEntities(0.6, 1, 0.6)) {

						if (en.getType().equals(EntityType.PLAYER)) {
							Player pl1 = (Player) en;
							Arena arena = Manager.getArena(player);

							if (pl1 != player && arena.getTeam(pl1) != arena.getTeam(player)
									&& !hasHit.contains(player.getUniqueId())) {
								GameListener.lastAbilityAttacker.put(pl1.getUniqueId(), player.getUniqueId());
								GameListener.customDeathCause.put(pl1.getUniqueId(), CustomDeathCause.DASH);
								@SuppressWarnings("deprecation")
								EntityDamageEvent event = new EntityDamageEvent(pl1, DamageCause.CUSTOM, 10.0);

								pl1.setLastDamageCause(event);
								pl1.setLastDamageCause(event);
								hasHit.add(player.getUniqueId());
								pl1.damage(40.0);
								player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.DARK_GREEN + "Dash "
										+ ChatColor.GREEN + "hit " + arena.getTeam(pl1).getColor() + pl1.getName()
										+ ChatColor.GREEN + "!");
								pl1.sendMessage(arena.getTeam(player).getColor() + player.getName() + ChatColor.GREEN
										+ " hit you with their " + ChatColor.DARK_GREEN + "Dash" + ChatColor.GREEN
										+ " ability!");

							}
						}

					}

					if (hasHit.isEmpty() && Manager.getArena(player).getType().equals(GameType.CLASH_ROYALE)) {
						ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();
						ClashRoyaleGame game = null;

						game = Manager.getArena(player).getBattleClash();

						for (String b : plugin.getConfig().getConfigurationSection("towers").getKeys(false)) {

							if (!b.equals("world") && !b.equals("arena")) {

								Tower tower = game.getTower().get(b);

								Region region = new Region(tower.getRegionLocations()[0],
										tower.getRegionLocations()[1]);

								Arena arena = Manager.getArena(tower.getRegionLocations()[1].getWorld());

								if (region.blockInLocation(player.getLocation().getBlock().getRelative(BlockFace.NORTH))
										|| region.blockInLocation(
												player.getLocation().getBlock().getRelative(BlockFace.SOUTH))
										|| region.blockInLocation(
												player.getLocation().getBlock().getRelative(BlockFace.WEST))
										|| region.blockInLocation(
												player.getLocation().getBlock().getRelative(BlockFace.EAST))) {

									hasHit.add(player.getUniqueId());
									if (tower.isEnabled() && !tower.getTeam().equals(arena.getTeam(player))) {

										if (arena.getClass(player).equals(ClassTypes.BANDIT)) {

											tower.subtractHealth(16);

											player.sendMessage(ChatColor.GREEN + "You dashed into a tower!");
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

									} else if (!tower.isEnabled() && tower.getType().equals(TowerTypes.KING)) {
										player.sendMessage(ChatColor.GREEN
												+ "That tower is protected! Destroy at least one of the princess towers to be able to damage this one!");
									}

								}
							}

						}
					}
				}

				cooldown--;
			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 1);

	}

	public static void fishermanAbilityCooldown(Player player) {

		new BukkitRunnable() {
			int cooldown = 15;

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || Manager.getArena(player).getState().equals(GameState.RECRUITING)) {
					cancel();
				}

				if (!Manager.getArena(player).getClass(player).equals(ClassTypes.FISHERMAN)) {
					cancel();
				}

				if (cooldown == 0) {
					cancel();
					if (Manager.isPlaying(player)) {
						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {
							ItemStack dasher = new ItemStack(Material.FISHING_ROD, 1);
							ItemMeta dasherMeta = dasher.getItemMeta();
							dasherMeta.setDisplayName(ChatColor.AQUA + "Fisherman Hook");
							dasher.setItemMeta(dasherMeta);
							player.getInventory().setItem(1, dasher);
							player.updateInventory();
						} else {
							return;
						}
					}

				}
				if (cooldown != 0) {

					if (Manager.isPlaying(player)) {

						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {

							ItemStack dasherCooldown = new ItemStack(Material.REDSTONE, cooldown);
							ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
							dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Fisherman Hook (On Cooldown)");
							dasherCooldown.setItemMeta(dasherCooldownMeta);
							player.getInventory().setItem(1, dasherCooldown);
							player.updateInventory();

						}
					}

				} else {
					ItemStack dasher = new ItemStack(Material.FISHING_ROD, 1);
					ItemMeta dasherMeta = dasher.getItemMeta();
					dasherMeta.setDisplayName(ChatColor.AQUA + "Fisherman Hook");
					dasher.setItemMeta(dasherMeta);
					player.getInventory().setItem(1, dasher);
					player.updateInventory();
				}

				cooldown--;
			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 20);
	}

	public static void witchAbilityCooldown(Player player) {

		new BukkitRunnable() {
			int cooldown = 2;

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || Manager.getArena(player).getState().equals(GameState.RECRUITING)) {
					cancel();
				}

				if (!Manager.getArena(player).getClass(player).equals(ClassTypes.WITCH)) {
					cancel();
				}

				if (cooldown == 0) {
					cancel();
					if (Manager.isPlaying(player)) {
						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {
							ItemStack stick = new ItemStack(Material.STICK);
							ItemMeta stickMeta = stick.getItemMeta();
							stickMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch's Wand");
							stick.setItemMeta(stickMeta);
							player.getInventory().setItem(0, stick);
							player.updateInventory();
						} else {
							return;
						}
					}

				}
				if (cooldown != 0) {

					if (Manager.isPlaying(player)) {

						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {

							ItemStack dasherCooldown = new ItemStack(Material.REDSTONE, cooldown);
							ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
							dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Witch's Wand (On Cooldown)");
							dasherCooldown.setItemMeta(dasherCooldownMeta);
							player.getInventory().setItem(0, dasherCooldown);
							player.updateInventory();

						}
					}

				} else {
					ItemStack stick = new ItemStack(Material.STICK);
					ItemMeta stickMeta = stick.getItemMeta();
					stickMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch's Wand");
					stick.setItemMeta(stickMeta);
					player.getInventory().setItem(0, stick);
					player.updateInventory();
				}

				cooldown--;
			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 20);
	}

	public static void witchCooldown(Player player) {

		new BukkitRunnable() {
			int cooldown = 20;

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || Manager.getArena(player).getState().equals(GameState.RECRUITING)) {
					cancel();
				}

				if (!Manager.getArena(player).getClass(player).equals(ClassTypes.WITCH)) {
					cancel();
				}
				if (cooldown == 0) {
					cancel();
					if (Manager.isPlaying(player)) {
						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {
							ItemStack summoner = new ItemStack(Material.INK_SACK, 1, (byte) 15);
							ItemMeta summonerMeta = summoner.getItemMeta();
							summonerMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Summon Skeletons (Right Click)");
							summoner.setItemMeta(summonerMeta);
							player.getInventory().setItem(1, summoner);
							player.updateInventory();
						} else {
							return;
						}
					}

				}
				if (cooldown != 0) {

					if (Manager.isPlaying(player)) {

						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {

							ItemStack dasherCooldown = new ItemStack(Material.SULPHUR, cooldown);
							ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
							dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Summon Skeletons (On Cooldown)");
							dasherCooldown.setItemMeta(dasherCooldownMeta);
							player.getInventory().setItem(1, dasherCooldown);
							player.updateInventory();

						}
					}

				} else {
					ItemStack summoner = new ItemStack(Material.INK_SACK, 1, (byte) 15);
					ItemMeta summonerMeta = summoner.getItemMeta();
					summonerMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Summon Skeletons (Right Click)");
					summoner.setItemMeta(summonerMeta);
					player.getInventory().setItem(1, summoner);
					player.updateInventory();
				}

				cooldown--;
			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 20);
	}

	public static void wizardCooldown(Player player) {

		new BukkitRunnable() {
			int cooldown = 3;

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || Manager.getArena(player).getState().equals(GameState.RECRUITING)) {
					cancel();
				}

				if (!Manager.getArena(player).getClass(player).equals(ClassTypes.WIZARD)) {
					cancel();
				}

				if (cooldown == 0) {
					cancel();
					if (Manager.isPlaying(player)) {
						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {
							ItemStack fireball = new ItemStack(Material.FIREBALL);
							ItemMeta fireballMeta = fireball.getItemMeta();
							fireballMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Fireball (Right Click To Shoot!)");

							fireball.setItemMeta(fireballMeta);
							player.getInventory().setItem(0, fireball);
							player.updateInventory();
						} else {
							return;
						}
					}

				}
				if (cooldown != 0) {

					if (Manager.isPlaying(player)) {

						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {

							ItemStack dasherCooldown = new ItemStack(Material.SULPHUR, cooldown);
							ItemMeta dasherCooldownMeta = dasherCooldown.getItemMeta();
							dasherCooldownMeta.setDisplayName(ChatColor.GRAY + "Fireball (On Cooldown)");
							dasherCooldown.setItemMeta(dasherCooldownMeta);
							player.getInventory().setItem(0, dasherCooldown);
							player.updateInventory();

						}
					}

				} else {
					ItemStack fireball = new ItemStack(Material.FIREBALL);
					ItemMeta fireballMeta = fireball.getItemMeta();
					fireballMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Fireball (Right Click To Shoot!)");

					fireball.setItemMeta(fireballMeta);
					player.getInventory().setItem(0, fireball);
					player.updateInventory();
				}

				cooldown--;
			}
		}.runTaskTimerAsynchronously(ClashRoyaleMain.getInstance(), 0, 20);
	}

	public static void doubleJumpCooldown(Player player) {

		new BukkitRunnable() {
			int cooldown = 7;

			@Override
			public void run() {

				if (!Manager.isPlaying(player) || !Manager.getArena(player).getState().equals(GameState.LIVE)) {
					cancel();
				}

				if (!Manager.getArena(player).getClass(player).equals(ClassTypes.WIZARD)) {
					player.setLevel(0);
					cancel();
				}

				if (cooldown == 0) {
					cancel();

					if (Manager.isPlaying(player)) {
						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {

							player.setLevel(0);
							player.setAllowFlight(true);
							player.sendMessage(
									ChatColor.GOLD + "Double Jump" + ChatColor.GREEN + " ability recharged!");
							player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1f, 1f);
						} else {
							return;
						}
					}

				}
				if (cooldown != 0) {

					if (Manager.isPlaying(player)) {

						if (Manager.getArena(player).getState().equals(GameState.LIVE)) {

							player.setLevel(cooldown);
						} else {
							cancel();
							player.setExp(0.0F);
							player.setLevel(0);
							return;
						}
					} else {
						cancel();
						return;
					}

				}

				cooldown--;
			}
		}.runTaskTimer(ClashRoyaleMain.getInstance(), 0, 20);
	}

}
