package net.herobrine.clashroyale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.gamecore.GameCoreMain;

public class ClashRoyaleMain extends JavaPlugin {
	private static ClashRoyaleMain instance;

	@Override
	public void onEnable() {
		instance = this;
		new Config(this);

		Bukkit.getPluginManager().registerEvents(new GameListener(), this);
		getCommand("bcdev").setExecutor(new DevCommand());

	}

	public static ClashRoyaleMain getInstance() {
		return instance;
	}

	public HerobrinePVPCore getCustomAPI() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("HBPVP-Core");
		if (plugin instanceof HerobrinePVPCore) {
			return (HerobrinePVPCore) plugin;
		} else {
			return null;
		}
	}

	public GameCoreMain getGameCore() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GameCore");
		if (plugin instanceof GameCoreMain) {

			return (GameCoreMain) plugin;
		} else {
			return null;
		}
	}

	public static String translateText(String string) {

		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
