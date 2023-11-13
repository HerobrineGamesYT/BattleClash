package net.herobrine.clashroyale.beta;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import net.herobrine.clashroyale.classes.Monk;
import net.herobrine.gamecore.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import net.herobrine.clashroyale.ClashRoyaleMain;
import net.herobrine.clashroyale.CustomDeathCause;
import net.herobrine.clashroyale.GameListener;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class Cannon {

	private CannonType cannonType;

	private double damageRange = 1;
	private double projectileRange = 15.0;

	private double damage = 10;

	private Location hitLocation;

	private boolean goldenCannon = false;

	private double goldenCannonBallChance = .01;

	private boolean isCannonballGolden = false;

	private double attackRange;

	private int arenaID;
	private Location shootLocation;
	private Entity target;
	private Teams targetsTeam;
	private boolean isActive;

	private UUID uuid = UUID.randomUUID();
	private String id;

	public Cannon(CannonType cannonType, String id, int arenaID, Location shootLocation, Teams targetsTeam,
			boolean isActive) {
		this.cannonType = cannonType;
		this.id = id;
		this.arenaID = arenaID;
		this.shootLocation = shootLocation;
		this.targetsTeam = targetsTeam;
		this.isActive = isActive;

		if (cannonType == CannonType.KING) {
			setAttackRange(20.0);
		} else {
			setAttackRange(25.0);
		}
	}

	public void setProjectileRange(double i) {
		this.projectileRange = i;
	}

	public void setDamageRange(double i) {
		this.damageRange = i;
	}

	public void setGoldenCBChance(double i) {
		this.goldenCannonBallChance = i;
	}

	public void enableGoldenCannonball(boolean goldenCannon) {
		this.goldenCannon = goldenCannon;
	}

	public void setDamage(int i) {
		this.damage = i;
	}

	public void setAttackRange(double i) {
		this.attackRange = i;
	}

	public void setHitLocation(Location location) {
		this.hitLocation = location;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public Cannon setId(String id) {
		this.id = id;
		return this;
	}

	private ArmorStand stand;

	public Location getHitLocation() {
		return hitLocation;
	}

	public double getDamageRange() {
		return damageRange;
	}

	public UUID getUUID() {

		return this.uuid;
	}

	public String getID() {

		return this.id;
	}

	public boolean isActive() {

		return isActive;
	}

	public void setActive(boolean active) {

		isActive = active;
	}

	public Teams getTargetTeam() {
		return targetsTeam;
	}

	public void spawn() {

		// Spawning armorstand
		stand = shootLocation.getWorld().spawn(shootLocation, ArmorStand.class);

		stand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, (short) 3).setHeadTexture(
				"http://textures.minecraft.net/texture/22523e15e9986355a1f851f43f750ee3f23c89ae123631da241f872ba7a781")
				.build());
		stand.setMarker(true);
		goldenCannonBall(stand);
		stand.setVisible(false);
		stand.setSmall(true);
		stand.setHeadPose(new EulerAngle(Math.random(), Math.random(), Math.random()));

		Location hit = getHitLocation();

		Location s = stand.getLocation();

		// Calc the vector
		// Vector v = new Vector((hit.getX() - s.getX()), 1.2, (hit.getZ() - s.getZ()));

		// stand.setVelocity(v);

		if (isCannonballGolden) {
			for (UUID uuid : Manager.getArena(arenaID).getPlayers()) {
				Player player = Bukkit.getPlayer(uuid);
				player.sendMessage(Colorize.color("&e&l&k&&r&e A golden cannonball has been fired! &e&l&k&&r&e"));
			}
		}

		runThing(stand, getTarget());
		handleRunnable(stand, getTarget());

	}

	public void runThing(ArmorStand arrow, Entity target) {

		new BukkitRunnable() {
			int timesRan = 0;

			public void run() {
				// Vector newVelocity;
				// double speed = arrow.getVelocity().length();
				if (arrow.isDead() || target.isDead()) {
					cancel();

					arrow.remove();
					return;
				}
				HashMap<Double, Tower> distances = new HashMap<>();

				if (target instanceof Player) {
					Player player = Bukkit.getPlayer(target.getUniqueId());

					for (Tower tower : Manager.getArena(arenaID).getBattleClash().towerList.values()) {
						distances.put(player.getLocation().distanceSquared(tower.getRegionLocations()[1]), tower);
					}

					Tower minDistance = distances.get(Collections.min(distances.keySet()));

					if (minDistance.getTeam().equals(Manager.getArena(player).getTeam(player))) {

						cancel();
						arrow.remove();

						return;
					}

					distances.clear();
				}

				Location from = arrow.getLocation();
				Location to = target.getLocation();
				Vector vFrom = from.toVector();
				Vector vTo = to.toVector();
				Vector direction = vTo.subtract(vFrom).normalize();
				arrow.setVelocity(direction);

				if (timesRan == 20) {
					arrow.teleport(new Location(target.getLocation().getWorld(), target.getLocation().getX(),
							target.getLocation().getY() + 1, target.getLocation().getZ()));
				}
				timesRan++;

			}
		}.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 1L);
	}

	public void setTarget(Entity ent) {
		target = ent;

	}

	public boolean hasTarget() {
		if (target != null) {
			return true;
		}
		return false;
	}

	public Entity getTarget() {

		return target;
	}

	private void goldenCannonBall(ArmorStand stand) {
	//	if (!goldenCannon || isCannonballGolden)
	//		return;

		boolean isGolden = Math.random() <= goldenCannonBallChance;

		if (isGolden && cannonType == CannonType.KING) {
			stand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, (short) 3).setHeadTexture(
					"http://textures.minecraft.net/texture/c3d5d345075e85617c67a75475c8e199e1c2c9bc19a48234189f9a0c256bb891")
					.build());
			this.isCannonballGolden = true;
		}
		else {
			this.isCannonballGolden = false;
		}
	}

	public void shootTarget() {
		if (hasTarget()) {

			spawn();

			checkForTarget(getTarget());
		}
	}

	public void checkForTarget(Entity player) {
		if (player.getLocation().distance(shootLocation) <= attackRange) {
			setTarget(player);
			setHitLocation(player.getLocation());
			// Manager.getArena(arenaID)
			// .sendMessage(ChatColor.translateAlternateColorCodes('&',
			// "&2&lDEBUG! &r&aNew Target: " + target.getName() + "\n Hit Location: " +
			// getHitLocation())
			// + "\n Cannon: " + getID());
		} else {
			if (hasTarget()) {
				if (getTarget() == player) {
					setTarget(null);
				}
			}
		}
	}

	private final void handleRunnable(ArmorStand stand, Entity target) {
		new BukkitRunnable() {
			HashMap<Double, Tower> distances = new HashMap<>();

			@Override
			public void run() {

				if (stand == null) {
					cancel();
					return;
				}

				stand.setHeadPose(new EulerAngle(Math.random(), Math.random(), Math.random()));

				if (!Manager.getArena(arenaID).getState().equals(GameState.LIVE)) {
					cancel();
					stand.remove();
				}

				if (stand.isOnGround() || stand.getLocation().equals(hitLocation)) {

					if (getTarget() != null) {
						if (getTarget() instanceof Player) {
							Arena arena = Manager.getArena((Player)getTarget());
							if (arena.getClass(getTarget().getUniqueId()).equals(ClassTypes.MONK)) {
								Monk monkClass = (Monk) arena.getClasses().get(getTarget().getUniqueId());

								if (monkClass.isProtectionActive()) {return;}
							}
							stand.teleport(getTarget().getLocation());
						}
					}

					for (Entity i : hitLocation.getWorld().getNearbyEntities(stand.getLocation(), getDamageRange(),
							getDamageRange(), getDamageRange())) {
						stand.remove();
						if (i instanceof Player) {

							Player player = (Player) i;
							Arena arena = Manager.getArena(arenaID);
							if (arena.getTeam(player) != null && arena.getTeam(player).equals(getTargetTeam())) {
								GameListener.customDeathCause.put(player.getUniqueId(), CustomDeathCause.CANNON);
								@SuppressWarnings("deprecation")
								EntityDamageEvent event = new EntityDamageEvent(player, DamageCause.CUSTOM, 10.0);

								player.setLastDamageCause(event);
								player.damage(damage);

							}

							Location hit = player.getLocation();

							Manager.getArena(arenaID).getPlayers().forEach(uuid -> {
								Player players = Bukkit.getPlayer(uuid);
								if (!isCannonballGolden) {
									players.getLocation().getWorld().playSound(stand.getLocation(), Sound.ITEM_BREAK,
											0.2f, 0.5f);
									players.getLocation().getWorld().playSound(stand.getLocation(), Sound.WITHER_HURT,
											0.1f, 0.24f);
								} else {
									players.getLocation().getWorld().playSound(stand.getLocation(),
											Sound.SUCCESSFUL_HIT, 0.2f, 2f);
									players.getLocation().getWorld().playSound(stand.getLocation(), Sound.ITEM_BREAK,
											0.4f, 2.5f);
								}

								if (player.getGameMode() == GameMode.CREATIVE) {
									PacketPlayOutAnimation animation = new PacketPlayOutAnimation(
											((CraftPlayer) player).getHandle(), 1);
									((CraftPlayer) players).getHandle().playerConnection.sendPacket(animation);
								}
								if (!isCannonballGolden) {
									PacketPlayOutWorldParticles packetPlayOutWorldParticles = null;
									if (hit != null) {
										packetPlayOutWorldParticles = new PacketPlayOutWorldParticles(
												EnumParticle.SMOKE_LARGE, true, (float) hit.getX(), (float) hit.getY(),
												(float) hit.getZ(), .5f, .5f, .5f, 0, 1);
									} else {
										packetPlayOutWorldParticles = new PacketPlayOutWorldParticles(
												EnumParticle.SMOKE_LARGE, true, (float) stand.getLocation().getX(),
												(float) stand.getLocation().getY(), (float) stand.getLocation().getZ(),
												.5f, .5f, .5f, 0, 1);
									}

									((CraftPlayer) players).getHandle().playerConnection
											.sendPacket(packetPlayOutWorldParticles);
								} else {
									if (hit != null) {
										new FireworkUtil(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
												.withColor(Color.ORANGE).build(), hit);
									} else {
										new FireworkUtil(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
												.withColor(Color.ORANGE).build(), stand.getLocation());
									}
								}
							});
						} else if (i instanceof Skeleton) {
							((Skeleton) i).damage(damage);
						}
					}
					cancel();
				} else {

					if (target instanceof Player) {
						Player player = (Player) target;

						// player.sendMessage(ChatColor.RED + "You are being targeted");
						// player.sendMessage(ChatColor.GOLD + "[DEBUG] " + player.getLocation());
						for (Tower tower : Manager.getArena(arenaID).getBattleClash().towerList.values()) {

							distances.put(player.getLocation().distanceSquared(tower.getRegionLocations()[1]), tower);

						}

						Tower minDistance = distances.get(Collections.min(distances.keySet()));

						if (minDistance.getTeam().equals(Manager.getArena(player).getTeam(player))) {
							cancel();
							stand.remove();
							return;
						}

						distances.clear();
					}
				}
			}
		}.runTaskTimer(ClashRoyaleMain.getInstance(), 0, 1);
	}

	public enum CannonType {

		KING, PRINCESS,

	}
}
