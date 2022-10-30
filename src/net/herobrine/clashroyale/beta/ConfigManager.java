package net.herobrine.clashroyale.beta;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import net.herobrine.clashroyale.ClashRoyaleGame;
import net.herobrine.gamecore.Manager;

public class ConfigManager {

	private static JavaPlugin plugin;

	public ConfigManager(JavaPlugin plugin) {
		this.plugin = plugin;

	}

	/*
	 * public Location getHitLocation(String a){
	 * 
	 * World b = Bukkit.getWorld( plugin.getConfig().getString("hitLocations." + a +
	 * ".world"));
	 * 
	 * double k = plugin.getConfig().getDouble("hitLocations." + a + ".x"); double g
	 * = plugin.getConfig().getDouble("hitLocations." + a + ".y"); double j =
	 * plugin.getConfig().getDouble("hitLocations." + a + ".z"); return new
	 * Location(b,k,g,j); } public Location getSpawnLocation(String a){
	 * 
	 * World b = Bukkit.getWorld(plugin.getConfig().getString("spawnLocations." + a
	 * + ".world"));
	 * 
	 * double k = plugin.getConfig().getDouble("spawnLocations." + a + ".x"); double
	 * g = plugin.getConfig().getDouble("spawnLocations." + a + ".y"); double j =
	 * plugin.getConfig().getDouble("spawnLocations." + a + ".z"); return new
	 * Location(b,k,g,j); }
	 */
	public void setRegion(String ab) {
		String a = ab.replace(" ", "_");

		System.out.println(a);
		String p = "towers." + a;

		System.out.println(p);

		World b = Bukkit.getWorld(plugin.getConfig().getString("towers.world"));

		ClashRoyaleGame game = Manager.getArena(b).getBattleClash();
		System.out.println(game);
		/* First location! */

		double k = plugin.getConfig().getDouble(p + ".region.0.x");
		System.out.println(k);
		double g = plugin.getConfig().getDouble(p + ".region.0.y");
		double j = plugin.getConfig().getDouble(p + ".region.0.z");

		Location f = new Location(b, k, g, j);

		/* Second location! */

		double k1 = plugin.getConfig().getDouble(p + ".region.1.x");
		double g1 = plugin.getConfig().getDouble(p + ".region.1.y");
		double j1 = plugin.getConfig().getDouble(p + ".region.1.z");

		Location s = new Location(b, k1, g1, j1);
		System.out.println(f);
		System.out.println(s);

		game.getTower().get(a).setRegion(new Location[] { f, s });

	}

	public static boolean doesTowerExist(String tower) {

		return plugin.getConfig().contains("towers." + tower);
	}
}
