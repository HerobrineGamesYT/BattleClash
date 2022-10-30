package net.herobrine.clashroyale.beta;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.herobrine.clashroyale.ClashRoyaleMain;
import net.herobrine.gamecore.Teams;

public class Tower {

	private String name;

	private TowerTypes x;

	private ClashRoyaleMain plugin = ClashRoyaleMain.getInstance();

	private Location[] cr;

	private boolean e = false;
	private Teams team;
	private Cannon[] c;
	private int health;
	private String friendlyName;

// name, type, isActive
	public Tower(String name, TowerTypes x, boolean e, Teams team, String friendlyName) {
		this.name = name;
		this.x = x;
		this.e = e;
		this.cr = new Location[] { new Location(Bukkit.getWorld("clashMap"), 0, 0, 0),
				new Location(Bukkit.getWorld("clashMap"), 1, 1, 1) };
		this.team = team;
		this.friendlyName = friendlyName;

		if (getType() == TowerTypes.KING) {
			this.health = 1000;
		} else {
			this.health = 500;
		}

	}

	public Tower setRegion(Location[] l) {
		this.cr = l;
		return this;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public Teams getTeam() {
		return team;
	}

	public String getName() {
		return name;
	}

	public Tower setType(TowerTypes t) {
		this.x = t;
		return this;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int setHealth) {
		health = setHealth;
	}

	public void subtractHealth(int subtract) {
		health = health - subtract;
	}

	public void addHealth(int add) {
		health = health + add;
	}

	public void setCannon(Cannon[] c) {
		this.c = c;

	}

	public Cannon[] getCannons() {

		return c;
	}

	public TowerTypes getType() {
		return x;
	}

	public Tower setEnabled(boolean e) {
		this.e = e;
		return this;
	}

	public boolean isEnabled() {
		return e;
	}

	public Location[] getRegionLocations() {
		return cr;
	}

	public static enum TowerTypes {

		KING, PRINCESS

	}
}
