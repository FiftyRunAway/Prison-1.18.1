package org.runaway.entity;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityTippedArrow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArrow;
import org.bukkit.entity.*;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.achievements.Achievement;
import org.runaway.entity.skills.DamageSkill;
import org.runaway.entity.skills.MobSkill;
import org.runaway.enums.EConfig;
import org.runaway.events.custom.BossSpawnEvent;
import org.runaway.events.custom.KillRatsEvent;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.tasks.Cancellable;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.TimeUtils;
import org.runaway.utils.Utils;

import java.util.*;

@Builder @Getter @Setter
public class MobController implements IMobController {
    private Attributable attributable;
    private Location spawnLocation;
    private String UID;
    private LivingEntity bukkitEntity;
    private net.minecraft.server.v1_12_R1.Entity nmsEntity;
    private int respawnTime;
    private long lastDeathTime, lastDamageTime;
    private Hologram infoHologram;
    private TextLine timeTextLine;
    private Map<String, DamageInfo> damageMap;
    private double totalDamage;
    private Random mobRandom;
    private Cancellable spawnTask;
    private List<MobSkill> mobSkillList, damageSkillList;
    private List<Cancellable> mobTasks;
    private MobRare mobRare;
    private boolean canRare;
    private boolean forceSpawn;

    private StandardMenu adminMenu;

    public MobController init() {
        setMobRare(MobRare.DEFAULT);
        setMobRandom(new Random());
        setMobTasks(new ArrayList<>());
        setDamageMap(new HashMap<>());
        if (getRespawnTimeLeft() <= 0) {
            setLastDeathTime(System.currentTimeMillis() / 1000L);
            if (!forceSpawn) save();
        }
        initFirstSpawn();
        if (!forceSpawn && getRespawnTime() != 0) {
            new SyncRepeatTask(() -> {
                if (getTimeTextLine() != null && getRespawnTimeLeft() > 0) {
                    getTimeTextLine().setText(Utils.colored("&a" + TimeUtils.getDuration(getRespawnTimeLeft())));
                }
            }, 20);
        }
        MobManager.uidMobControllerMap.put(getUID(), this);
        MobManager.attributableMap.put(attributable.getTechName(), attributable);

        this.adminMenu = StandardMenu.create(1, "&eАдмин-панель босса &7• &e" + this.getAttributable().getName());

        IMenuButton reset = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aЗаспавнить босса через 10 секунд").build().item()).setSlot(0);
        reset.setClickEvent(event -> {
            spawn();
            event.getWhoClicked().closeInventory();
        });
        this.adminMenu.addButton(reset);

        return this;
    }

    @Override
    public void spawn() {
        if(canRare && !attributable.isBoss() && getMobRandom().nextFloat() < 0.2) {
            setMobRare(MobRare.RARE);
        }
        try {
            if (!getSpawnLocation().getChunk().isLoaded()) getSpawnLocation().getChunk().load();
        } catch (Exception e) {
        }
        if (attributable.isBoss())
            Bukkit.getServer().getPluginManager().callEvent(new BossSpawnEvent(getAttributable().getName()));

        net.minecraft.server.v1_12_R1.Entity entity = CustomEntity.spawnEntity(getAttributable().getMobType(), getSpawnLocation(), this);
        if (getRespawnTime() != 0) {
            if (getInfoHologram() != null) {
                getInfoHologram().delete();
                setTimeTextLine(null);
            }
        }
        setBukkitEntity((LivingEntity) entity.getBukkitEntity());
        setNmsEntity(entity);
        MobManager.mobControllerMap.put(entity.getId(), this);
        initSkills();
        if(getAttributable().getOnSpawnConsumer() != null) {
            getAttributable().getOnSpawnConsumer().accept(getBukkitEntity());
        }
        if (getSpawnTask() != null) getSpawnTask().stop();
    }

    public int getRespawnTimeLeft() {
        return (int) (getRespawnTime() - (System.currentTimeMillis() / 1000L - getLastDeathTime()));
    }

    @Override
    public boolean onDamage(DamageSource damageSource, float f) {
        net.minecraft.server.v1_12_R1.Entity damagerEntity = damageSource.i();
        if (damagerEntity == null) {
            return false;
        }
        if (damagerEntity.getBukkitEntity() instanceof Player ||
                damagerEntity.getBukkitEntity() instanceof Projectile) {
            Player player = null;
            if (damagerEntity.getBukkitEntity() instanceof Player) player = (Player) damagerEntity.getBukkitEntity();
            if (damagerEntity.getBukkitEntity() instanceof Projectile) player = (Player) ((CraftArrow)damagerEntity.getBukkitEntity()).getShooter();
            if (getMobSkillList() != null) {
                Player finalPlayer = player;
                getMobSkillList().forEach(mobSkill -> {
                    if (mobSkill instanceof DamageSkill) {
                        mobSkill.getConsumer().accept(getNmsEntity(), finalPlayer);
                    }
                });
            }
            updateCustomName();
        } else {
            return false;
        }
        return true;
    }

    public void updateCustomName() {
        if(!isAlive()) return;
        String mobName = getMobRare() == MobRare.DEFAULT ? attributable.getName() : getMobRare().getNamePrefix() + ChatColor.stripColor(attributable.getName()) + " &4&l☠";
        getBukkitEntity().setCustomName(Utils.colored("&7[" + attributable.getMobLevel() + "&7] " + mobName + "&4 ❤ " + (int) getBukkitEntity().getHealth()));
    }

    public void initSkills() {
        if(getMobSkillList() != null) {
            getMobSkillList().forEach(mobSkill -> {
                mobSkill.apply(this);
            });
        }
        getMobTasks().add(new SyncRepeatTask(() -> {
            if (!isAlive()) {
                return;
            }
            if (getBukkitEntity().getHealth() <= (double) attributable.getRegenerationValue()) {
                return;
            }
            double sum = getBukkitEntity().getHealth() + (double) attributable.getRegenerationValue();
            if (sum > getBukkitEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                getBukkitEntity().setHealth(getBukkitEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            } else {
                getBukkitEntity().setHealth(sum);
            }
            updateCustomName();
        }, getAttributable().getRegenerationDelay()));
        getMobTasks().add(new SyncRepeatTask(() -> {
            if (!isAlive()) {
                return;
            }
            double distance = getSpawnLocation().distance(getBukkitEntity().getLocation());
            if(distance > 20) {
                getNmsEntity().setPosition(getSpawnLocation().getX(), getSpawnLocation().getY(),getSpawnLocation().getZ());
            }
        }, 20 * 10));
    }

    public void kill() {
        if (getBukkitEntity() != null) {
            getBukkitEntity().remove();
        }
        if (getSpawnTask() != null) getSpawnTask().stop();
        if (getInfoHologram() != null) getInfoHologram().delete();
    }

    public void remove() {
        MobManager.uidMobControllerMap.remove(getUID());
        getMobTasks().forEach(Cancellable::stop);
        if(isAlive()) {
            MobManager.mobControllerMap.remove(getBukkitEntity().getEntityId());
        }
    }

    public void initFirstSpawn() {
        if (!forceSpawn && getRespawnTime() == 0) return;

        if (this.bukkitEntity == null) {
            if (forceSpawn || System.currentTimeMillis() / 1000L - this.lastDeathTime >= this.respawnTime) {
                this.spawn();
            } else {
                if (attributable.isBoss()) createHologram();
                setSpawnTask(new SyncTask(this::spawn, getRespawnTimeLeft() * 20));
            }
        }
    }

    /*public int getRespawnTimeLeft() {
        return (int) (getRespawnTime() - (System.currentTimeMillis() / 1000L - getLastDeathTime()));
    }*/

    private void createHologram() {
        if (getRespawnTime() == 0) return;
        setInfoHologram(HologramsAPI.createHologram(Prison.getInstance(), this.getSpawnLocation().clone().add(0.0, 2.5, 0.0)));
        getInfoHologram().appendTextLine(Utils.colored("&7[" + getAttributable().getMobLevel() + "&7] " + getAttributable().getName()))
                .setTouchHandler(player -> {
                    if (player.isOp())
                        this.adminMenu.open(GamerManager.getGamer(player));
                });
        setTimeTextLine(getInfoHologram().appendTextLine(Utils.colored("&a" + TimeUtils.getDuration(getRespawnTimeLeft()))));
    }

    @Override
    public void die() {
        setLastDeathTime(System.currentTimeMillis() / 1000L);
        save();
        Map<Gamer, Double> damagePercentMap = new HashMap<>();
        if (getRespawnTime() == 0 && getUID() != null) {
            MobManager.uidMobControllerMap.remove(getUID());
            if (getSpawnTask() != null) getSpawnTask().stop();
        }

        if (getBukkitEntity().getKiller() != null) {
            Gamer killer = GamerManager.getGamer(getBukkitEntity().getKiller());

            boolean isRat = getAttributable().getMobType().equals(MobType.SILVERFISH);
            boolean isZombie = getAttributable().getMobType().equals(MobType.ZOMBIE);
            boolean isSkeleton = getAttributable().getMobType().equals(MobType.SKELETON);

            //Achievements
            if (isRat && getMobRare().equals(MobRare.RARE))
                Achievement.RARE_RAT.get(GamerManager.getGamer(getBukkitEntity().getKiller()).getPlayer());
            if (isRat && killer.getMobKills().getOrDefault("rat", 0) >= 15)
                Achievement.FIFTEEN_RATS.get(killer.getPlayer());
            if (isZombie && killer.getMobKills().getOrDefault("zombie", 0) >= 15)
                Achievement.FIFTEEN_ZOMBIES.get(killer.getPlayer());
            if (isSkeleton && killer.getMobKills().getOrDefault("skeleton", 0) >= 50)
                Achievement.FIFTY_SKELETONS.get(killer.getPlayer());

            if (isRat) {
                Bukkit.getServer().getPluginManager().callEvent(new KillRatsEvent(getBukkitEntity().getKiller(), getMobRare().equals(MobRare.RARE)));
            }
        }

        setMobRare(MobRare.DEFAULT);
        getDamageMap().forEach((nickname, damage) -> {
            if (getTotalDamage() == 0) {
                setTotalDamage(1);
            }
            if (Bukkit.getPlayerExact(nickname) == null) {
                this.totalDamage -= damage.getDamage();
                return;
            }
            Gamer gamer = GamerManager.getGamer(nickname);
            gamer.increaseQuestValue(attributable.getTechName() + "Dmg", (int) damage.getDamage());
            if (System.currentTimeMillis() - damage.getLastDamageTime() > 2 * 60 * 1000) {
                gamer.sendMessage("&cВы нападали на моба и он был убит, но вы не получите награды, т.к. прошло более 2-ух минут с последнего удара.");
                this.totalDamage -= damage.getDamage();
                return;
            }
            damagePercentMap.put(gamer, damage.getDamage() / getTotalDamage());
            if (getAttributable().getMobType().equals(MobType.SPIDER)) Achievement.SPIDER_KILL.get(gamer.getPlayer());
            if (getAttributable().getMobType().equals(MobType.BLAZE)) Achievement.BLAZE_KILL.get(gamer.getPlayer());
            if (getAttributable().getMobType().equals(MobType.GOLEM)) Achievement.GOLEM_KILL.get(gamer.getPlayer());
            if (getAttributable().getMobType().equals(MobType.SLIME)) Achievement.SLIME_KILL.get(gamer.getPlayer());
        });
        try {
            getAttributable().getMobLoot().drop(damagePercentMap, getBukkitEntity().getLocation(), getAttributable());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTotalDamage(0);
        getDamageMap().clear();
        MobManager.mobControllerMap.remove(getBukkitEntity().getEntityId());
        setBukkitEntity(null);
        setNmsEntity(null);
        if (!forceSpawn && getRespawnTime() != 0 && !Prison.isDisabling) {
            setSpawnTask(new SyncTask(this::spawn, getRespawnTime() * 20));
        }
        if (getAttributable().isBoss()) {
            createHologram();
        }
        getMobTasks().forEach(Cancellable::stop);
    }

    public void save() {
        if (getRespawnTime() == 0 || forceSpawn) return;
        EConfig.MOBS.getConfig().set("mobs." + getUID() + ".lastDeathTime", getLastDeathTime());
        EConfig.MOBS.saveConfig();
    }

    @Override
    public boolean isAlive() {
        return nmsEntity != null;
    }
}
