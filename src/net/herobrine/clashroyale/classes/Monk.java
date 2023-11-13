package net.herobrine.clashroyale.classes;

import net.herobrine.clashroyale.ClashRoyaleGame;
import net.herobrine.clashroyale.ClashRoyaleMain;
import net.herobrine.clashroyale.beta.Cannon;
import net.herobrine.clashroyale.beta.Tower;
import net.herobrine.gamecore.*;
import net.herobrine.gamecore.Class;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class Monk extends Class {

    private boolean isProtectionActive;
    private int fistDamage = 5;
    private int comboDamage = 7;
    private int hits = 0;
    private int cooldown = 20;
    private int protectionTime = 5;
    private int protectionTicks = 100;
    int addToY = 0;



    public Monk(UUID uuid) {super(uuid, ClassTypes.MONK);}


    @Override
    public void onStart(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(Color.LIME);
        helmetMeta.setDisplayName(ChatColor.DARK_GREEN + "Monk Helmet");
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 7, true);
        helmetMeta.spigot().setUnbreakable(true);
        helmetMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        helmetMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        helmet.setItemMeta(helmetMeta);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(Color.LIME);
        chestplateMeta.setDisplayName(ChatColor.DARK_GREEN + "Monk Chestplate");
        chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 7, true);
        chestplateMeta.spigot().setUnbreakable(true);
        chestplateMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        chestplateMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        chestplate.setItemMeta(chestplateMeta);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(Color.LIME);
        leggingsMeta.setDisplayName(ChatColor.DARK_GREEN + "Monk Leggings");
        leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 7, true);
        leggingsMeta.spigot().setUnbreakable(true);
        leggingsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leggingsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        leggings.setItemMeta(leggingsMeta);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.LIME);
        bootsMeta.setDisplayName(ChatColor.DARK_GREEN + "Monk Boots");
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 7, true);
        bootsMeta.spigot().setUnbreakable(true);
        bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bootsMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        boots.setItemMeta(bootsMeta);

        ItemBuilder monkAbility = new ItemBuilder(Material.NETHER_STAR);
        monkAbility.setDisplayName(ChatColor.DARK_GREEN + "Pensive Protection " +  ChatColor.GRAY + "(Right Click)");

        player.getInventory().clear();

        player.getEquipment().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
        player.getInventory().setItem(1, monkAbility.build());
    }

    public boolean isProtectionActive() {

        return isProtectionActive;
    }


    public void doParticles(Player player, float radius) {
            Location loc = player.getLocation();
            Arena arena = Manager.getArena(player);

            new BukkitRunnable() {
                public void run() {
                    if(!isProtectionActive || !arena.getState().equals(GameState.LIVE)) {
                        cancel();
                        return;
                    }
                    if (addToY > 3) addToY = 0;
                    for(UUID uuid : arena.getPlayers()) {

                        Player showFor = Bukkit.getPlayer(uuid);
                        for (double t = 0; t < 1000; t += 0.5) {
                            float x = radius * (float) Math.sin(t);
                            float z = radius * (float) Math.cos(t);

                            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true,
                                    (float) loc.getX() + x, (float) loc.getY() + addToY, (float) loc.getZ() + z, 0, 0, 0, 0, 1, null);
                            ((CraftPlayer) showFor).getHandle().playerConnection.sendPacket(packet);

                        }
                    }


                    addToY++;
                }
            }.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 5L);


    }

    public void startCooldown(Player player) {
        cooldown = 20;
        ItemBuilder cooldownTimer = new ItemBuilder(Material.SULPHUR);

        cooldownTimer.setDisplayName(ChatColor.GRAY + "Pensive Protection (On Cooldown)");
        cooldownTimer.setAmount(cooldown);

        new BukkitRunnable() {
            public void run() {
                if (player == null) {
                    cancel();
                    return;
                }

                if (!Manager.isPlaying(player)) {
                    cancel();
                    return;
                }

                if (Manager.getArena(player).getState() != GameState.LIVE) {
                    cancel();
                    return;
                }

                if (cooldown == 0) {
                    cancel();

                    ItemBuilder monkAbility = new ItemBuilder(Material.NETHER_STAR);
                    monkAbility.setDisplayName(ChatColor.DARK_GREEN + "Pensive Protection " +  ChatColor.GRAY + "(Right Click)");
                    player.getInventory().setItem(1, monkAbility.build());
                }
                else {
                    cooldownTimer.setAmount(cooldown);
                    player.getInventory().setItem(1, cooldownTimer.build());
                    if (isProtectionActive) isProtectionActive = false;
                }


                cooldown--;
            }
        }.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 20L);
    }

    public void checkForCollision(Player player) {
        HashMap<Double, Tower> distances = new HashMap<>();
        ArrayList<Integer> hitStands = new ArrayList<>();
        new BukkitRunnable() {
            public void run() {

                if (!Manager.isPlaying(player)) {
                    cancel();
                    hitStands.clear();
                    return;
                }

                if (Manager.getArena(player).getState() != GameState.LIVE) {
                    cancel();
                    hitStands.clear();
                    return;
                }

                if (!isProtectionActive) {
                    cancel();
                    hitStands.clear();
                    return;
                }

                for (org.bukkit.entity.Entity ent : player.getNearbyEntities(3.0F, 3.0F, 3.0F)) {

                    if (ent instanceof ArmorStand) {
                        ArmorStand stand = (ArmorStand) ent;
                        if (stand.getHelmet() != null) {

                            if (!hitStands.contains(stand.getEntityId())) {
                                hitStands.add(stand.getEntityId());
                                player.sendMessage(ChatColor.GRAY + "" + hitStands.get(hitStands.size() - 1));
                                distances.clear();
                                for (Tower tower : Manager.getArena(player).getBattleClash().getTowerList().values()) {
                                    distances.put(stand.getLocation().distanceSquared(tower.getRegionLocations()[1]), tower);
                                }

                                Tower minDistance = distances.get(Collections.min(distances.keySet()));
                                Location from = stand.getLocation();
                                Location to = minDistance.getRegionLocations()[1];
                                Vector vFrom = from.toVector();
                                Vector vTo = to.toVector();
                                Vector direction = vTo.subtract(vFrom).normalize();

                                deflectCannonBall(player, stand, minDistance);
                            }


                        }
                        else stand.remove();
                    }

                }

            }


        }.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 1L);
    }


    public void startProtection(Player player) {
        isProtectionActive = true;
        protectionTime = 5;

        ItemBuilder abilityTimer = new ItemBuilder(Material.REDSTONE);
        abilityTimer.setDisplayName(ChatColor.RED + "Pensive Protection Active!");
        abilityTimer.setAmount(5);
        player.getInventory().setItem(1, abilityTimer.build());

        EntityArmorStand stand = new EntityArmorStand(((CraftWorld)player.getLocation().getWorld()).getHandle(),
                player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        stand.setInvisible(true);
        stand.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        Entity ent = stand;


        PacketPlayOutSpawnEntityLiving pac = new PacketPlayOutSpawnEntityLiving((EntityLiving) ent);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pac);
        PacketPlayOutCamera packet = new PacketPlayOutCamera(ent);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        player.setGameMode(GameMode.SPECTATOR);

        new BukkitRunnable(){
            public void run() {

               for (Chunk chunk : player.getLocation().getWorld().getLoadedChunks()) {
                    int viewDistance = 64;


                    double distanceX = Math.abs(player.getLocation().getX() - (chunk.getX() << 4));
                  double distanceZ = Math.abs(player.getLocation().getZ() - (chunk.getZ() << 4));



                   if (distanceX <= viewDistance && distanceZ <= viewDistance) {
                    player.getWorld().loadChunk(chunk);

                   }

               }
            }
        }.runTaskLater(ClashRoyaleMain.getInstance(), 2L);



        checkForCollision(player);
        doParticles(player, 2.5F);

        new BukkitRunnable() {
            public void run() {
                if (player == null) {
                    cancel();
                    return;
                }

                if (!Manager.isPlaying(player)) {
                    cancel();
                    return;
                }

                if (!Manager.getArena(player).getState().equals(GameState.LIVE) || !Manager.getArena(player).getClass(player).equals(ClassTypes.MONK)) {
                    cancel();
                    PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    player.setGameMode(GameMode.SURVIVAL);
                    return;
                }



                if (protectionTime == 0) {
                    cancel();

                    PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    player.setGameMode(GameMode.SURVIVAL);

                    startCooldown(player);
                }
                else {
                    GameCoreMain.getInstance().sendTitle(player, "", "&6Protection Expires In: &c" + protectionTime + "&cs", 0, 1,0);
                }
                protectionTime--;
            }
        }.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 20L);
    }


    public void deflectCannonBall(Player player, ArmorStand stand, Tower tower) {


        new BukkitRunnable() {
            int timesRan = 0;
            boolean isTowerDestroyed = false;
            public void run() {

                if (!Manager.isPlaying(player)) {
                    cancel();
                    return;
                }

                if (Manager.getArena(player).getState() != GameState.LIVE) {
                    cancel();
                    return;
                }

                if (!isProtectionActive) {
                    cancel();
                    return;
                }

                if (tower.getHealth() <= 0) {
                    if (tower.getType().equals(Tower.TowerTypes.PRINCESS)) {
                     Tower tower2 = Manager.getArena(player).getBattleClash().getTowerList().get(tower.getTeam().name().toLowerCase() + "_king");
                     deflectCannonBall(player, stand, tower2);
                     cancel();
                     return;
                    }
                }

                if (timesRan > 25) {
                    stand.teleport(tower.getRegionLocations()[1]);
                    stand.remove();
                    tower.setHealth(tower.getHealth() - 5);
                    if(tower.getType().equals(Tower.TowerTypes.KING)) Manager.getArena(player).getBattleClash().updateTowerHealth(Manager.getArena(player));
                    player.getLocation().getWorld().playSound(player.getLocation(),
                            Sound.SUCCESSFUL_HIT, 0.2f, 2f);
                    player.getLocation().getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK,
                            0.4f, 2.5f);
                    player.sendMessage(ChatColor.GREEN + "You deflected a cannonball onto a tower!");


                    if (tower.getHealth() <= 0 && !isTowerDestroyed) {
                        isTowerDestroyed = true;
                        Manager.getArena(player).playSound(Sound.WITHER_DEATH);
                        Manager.getArena(player).sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&a&lTOWER DESTRUCTION > "
                                                + tower.getFriendlyName()
                                                + ChatColor.GREEN
                                                + " has been destroyed by "
                                                + Manager.getArena(player).getTeam(
                                                        player)
                                                .getColor()
                                                + player.getName()
                                                + ChatColor.GREEN
                                                + "!"));

                        tower.setEnabled(false);
                        for (Cannon cannon : tower.getCannons()) {
                            cannon.setActive(false);
                        }

                        Arena arena = Manager.getArena(player);
                        ClashRoyaleGame game = arena.getBattleClash();
                        if (tower.getType().equals(Tower.TowerTypes.KING)) {
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

                            for (Tower twr: arena.getBattleClash().getTowerList().values()) {
                                if (twr.getType().equals(Tower.TowerTypes.KING) && twr.getTeam().equals(tower.getTeam()) && !twr.isEnabled()) {
                                    twr.setEnabled(true);
                                    for (Cannon cannon : twr.getCannons()) {
                                        cannon.setActive(true);
                                    }
                                }
                            }

                        }

                    }
                    cancel();
                    return;
                }

               else if (stand.getLocation().equals(tower.getRegionLocations()[1])) {
                    stand.remove();
                    tower.setHealth(tower.getHealth() - 5);
                    player.getLocation().getWorld().playSound(player.getLocation(),
                            Sound.SUCCESSFUL_HIT, 0.2f, 2f);
                    player.getLocation().getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK,
                            0.4f, 2.5f);
                    player.sendMessage(ChatColor.GREEN + "You deflected a cannonball onto a tower!");
                    cancel();
                    return;
                }

                Location from = stand.getLocation();
                Location to = tower.getRegionLocations()[1];
                Vector vFrom = from.toVector();
                Vector vTo = to.toVector();
                Vector direction = vTo.subtract(vFrom).normalize();
                stand.setHeadPose(new EulerAngle(Math.random(), Math.random(), Math.random()));
                stand.setVelocity(direction);

                timesRan++;
            }
        }.runTaskTimer(ClashRoyaleMain.getInstance(), 0L, 1L);

    }



    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getUniqueId() != getUUID()) return;

        if (e.getPlayer().getItemInHand() != null) {
            if (e.getPlayer().getItemInHand().getType().equals(Material.NETHER_STAR) && !isProtectionActive) startProtection(e.getPlayer());
        }
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager().getUniqueId() != getUUID() && e.getEntity().getUniqueId() != getUUID()) return;

        Player player = Bukkit.getPlayer(getUUID());
        if (e.getDamager().getUniqueId() == getUUID()) {

        }


        else if (e.getEntity().getUniqueId() == getUUID()) {

        }


    }



    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e ) {

    }


}
