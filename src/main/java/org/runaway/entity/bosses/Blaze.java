package org.runaway.entity.bosses;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.donate.features.BossMoney;
import org.runaway.entity.CustomEntity;
import org.runaway.entity.Spawner;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.MoneyType;
import org.runaway.events.custom.BossSpawnEvent;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Blaze extends EntityMonster {

    private String name;
    private int health;
    private double speed;
    private double money;
    private Spawner spawner;
    private CraftEntity bukkitEntity;
    private HashMap<String, Integer> attackers;
    private int totalDamage;
    private int hpDelay;

    private static int dopDamage;
    private boolean event;

    private Location cotel;
    private Location cotel_boss;

    public Blaze(Spawner spawner) {
        super(((CraftWorld)spawner.getSpawnLocation().getWorld()).getHandle());
        ConfigurationSection section = EConfig.MOBS.getConfig().getConfigurationSection("blaze");
        this.name = section.getString("name");
        this.health = section.getInt("health"); //350
        double damage = section.getDouble("damage"); //10
        double followRange = 128.0;
        double knobackResistence = -1.0;
        this.speed = section.getDouble("speed"); //0.03
        this.money = section.getDouble("money"); //400
        this.cotel = Utils.unserializeLocation(section.getString("cotel"));
        this.cotel_boss = Utils.unserializeLocation(section.getString("cotel_boss"));
        this.hpDelay = 20;
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(this.health);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(followRange);
        this.getAttributeInstance(GenericAttributes.c).setValue(knobackResistence);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.speed);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
        this.setHealth((float)this.health);
        ((LivingEntity)this.getBukkitEntity()).setRemoveWhenFarAway(false);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, (float) followRange));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 10.0));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this,  EntityHuman.class, (float)(followRange / 2.0)));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, damage, true));
        (this.spawner = spawner).register(this);
        this.setCustomName(Utils.colored(this.name));
        this.setCustomNameVisible(true);
        this.canPickUpLoot = false;
        this.fireProof = true;
        this.persistent = true;
        this.bukkitEntity = this.getBukkitEntity();
        this.attackers = new HashMap<>();
        this.totalDamage = 0;
        this.event = false;

        Bukkit.getServer().getPluginManager().callEvent(new BossSpawnEvent(this.name, this));
    }

    public boolean damageEntity(DamageSource source, float a) {
        if ((!this.passengers.isEmpty() && source.getEntity() == this.passengers.get(0)) || source == DamageSource.STUCK) {
            return false;
        }
        if (a == 0) return false;
        if (source.getEntity() != null && source.getEntity().getBukkitEntity().getType() == EntityType.PLAYER) {
            Player pAttacker = Bukkit.getPlayer(Objects.requireNonNull(source.getEntity().getBukkitEntity()).getName());
            if (pAttacker == null) return false;

            Main.gamers.get(Bukkit.getPlayer(source.getEntity().getBukkitEntity().getName()).getUniqueId()).sendTitle("&c" + Math.round(this.getHealth() - a) + "♥",  "&c" + this.name);
            if (!this.attackers.containsKey(pAttacker.getName())) {
                this.attackers.put(pAttacker.getName(), (int)a);
            } else {
                this.attackers.put(pAttacker.getName(), (int)(this.attackers.get(pAttacker.getName()) + a));
            }
            this.totalDamage += (int)a;

            if (ThreadLocalRandom.current().nextFloat() < 0.05) {
                a += 3;
                pAttacker.sendMessage(Utils.colored("&7Критический урон"));
            }
        }
        if (ThreadLocalRandom.current().nextFloat() < 0.15 && !event) {
            for (Entity e : this.getBukkitEntity().getNearbyEntities(10.0, 5.0, 10.0)) {
                if (e.getType() == EntityType.PLAYER) {
                    LivingEntity p = (LivingEntity)e;
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1, true, false));
                    p.setFireTicks(100);
                }
            }
        }

        if (ThreadLocalRandom.current().nextFloat() < 0.03 && !event && this.getHealth() > 80) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
            dopDamage = 0;
            event = true;
            for (Entity e : this.getBukkitEntity().getNearbyEntities(10.0, 8.0, 10.0)) {
                if (e.getType() == EntityType.PLAYER) {
                    LivingEntity ent = (LivingEntity)e;
                    ent.teleport(cotel);
                    ent.sendMessage(Utils.colored(this.name + " &7>> &cВы все сгорите в аду!"));
                    ent.sendMessage(Utils.colored("&eЗадание &7>> Нанесите все вместе 70 урона Огненному стражу"));
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (event) {
                        for (Entity e : Blaze.this.getBukkitEntity().getNearbyEntities(10.0, 8.0, 10.0)) {
                            if (e.getType() == EntityType.PLAYER) {
                                LivingEntity p = (LivingEntity)e;
                                p.teleport(spawner.getSpawnLocation());
                                p.sendMessage(Utils.colored(Blaze.this.name + " &7>> &cЛадненько... вернусь сам!"));
                            }
                        }
                        Blaze.this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(Blaze.this.speed);
                        Blaze.this.getBukkitEntity().teleport(Blaze.this.spawner.getSpawnLocation());
                        event = false;
                        cancel();
                    }
                }
            }.runTaskLater(Main.getInstance(), 600);

            this.getBukkitEntity().teleport(cotel_boss);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (dopDamage >= 70) {
                        if (!event) {
                            cancel();
                            return;
                        }
                        for (Entity e : Blaze.this.getBukkitEntity().getNearbyEntities(10.0, 8.0, 10.0)) {
                            if (e.getType() == EntityType.PLAYER) {
                                LivingEntity p = (LivingEntity)e;
                                p.teleport(spawner.getSpawnLocation());
                            }
                        }
                        Blaze.this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(Blaze.this.speed);
                        Blaze.this.getBukkitEntity().teleport(Blaze.this.spawner.getSpawnLocation());
                        event = false;
                        cancel();
                    } else {
                        for (Entity e : Blaze.this.getBukkitEntity().getNearbyEntities(10.0, 8.0, 10.0)) {
                            if (e.getType() == EntityType.PLAYER) {
                                LivingEntity p = (LivingEntity)e;
                                p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                            }
                        }
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0L, 15L);
        }
        if (event) dopDamage += a;
        return super.damageEntity(source, a);
    }

    public void n() {
        if (this.spawner != null && this.spawner.getCurrent() != null && this.spawner.getSpawnLocation().distance(this.spawner.getCurrent().getBukkitEntity().getLocation()) > 40.0) {
            this.spawner.getCurrent().setLocation(this.spawner.getSpawnLocation().getX(), this.spawner.getSpawnLocation().getY(), this.spawner.getSpawnLocation().getZ(), 0.0f, 0.0f);
        }
        if (this.hpDelay-- <= 0) {
            if (this.getHealth() < this.health) {
                this.heal(2.0f, EntityRegainHealthEvent.RegainReason.REGEN);
            }
            this.hpDelay = 20;
        }
        if (this.getGoalTarget() != null) {
            boolean isSameWorld = this.getBukkitEntity().getLocation().getWorld() == this.getGoalTarget().getBukkitEntity().getLocation().getWorld();
            double distance = isSameWorld ? this.getBukkitEntity().getLocation().distance(this.getGoalTarget().getBukkitEntity().getLocation()) : 32.0;
            if (distance <= 16.0) {
                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.speed);
            } else {
                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
            }
        }
        super.n();
    }

    public void die() {
        if (!Main.bosses.contains(this.getUniqueID())) return;
        if (this.getBukkitEntity().getCustomName().equals("toDelete")) {
            super.die();
            return;
        }
        if (this.spawner != null) {
            if (!CustomEntity.monsters.contains(this.name)) {
                CustomEntity.monsters.add(this.name);
            }
            this.spawner.dead();
        }
        if (this.killer != null) {
            Bukkit.broadcastMessage(Utils.colored(EMessage.BLAZEDEAD.getMessage()
                    .replaceAll("%player%", ChatColor.RESET + this.killer.getName())
            ));
            HashMap<String, Double> percents = Utils.calculatePercents(this.attackers, this.totalDamage);

            World world = this.spawner.getSpawnLocation().getWorld();
            world.dropItemNaturally(getBukkitEntity().getLocation(), ExampleItems.getNetherStarBuilder().amount(ThreadLocalRandom.current().nextInt(2) + 1).build().item());
            world.dropItemNaturally(getBukkitEntity().getLocation(), ExampleItems.getKeyBuilder().amount(12).build().item());

            for (String key : percents.keySet()) {
                double money = new BigDecimal(percents.get(key) * this.money).setScale(2, RoundingMode.UP).doubleValue();
                if (money < 0) money = 0;
                if (!Utils.getPlayers().contains(key)) {
                    EStat.MONEY.setInConfig(key, (double)EStat.MONEY.getFromConfig(key) + money);
                    continue;
                }
                Gamer gamer = Main.gamers.get(Bukkit.getPlayer(key).getUniqueId());

                gamer.depositMoney(money);
                Achievement.BLAZE_KILL.get(gamer.getPlayer(), false);
                gamer.setStatistics(EStat.BOSSES, gamer.getIntStatistics(EStat.BOSSES) + 1);

                Object obj = gamer.getPrivilege().getValue(new BossMoney());
                int sale = 0;
                if (obj != null) sale = Integer.parseInt(obj.toString());

                gamer.getPlayer().sendMessage(Utils.colored(EMessage.BOSSREWARD.getMessage()
                        .replaceAll("%boss%", ChatColor.RESET + name)
                        .replaceAll("%money%", Math.round(money) + " " + MoneyType.RUBLES.getShortName() + (sale > 0 ? (" &7(&b+" + sale + "% за донат&7)") : ("")))
                ));
            }
        }
        super.die();
    }
}
