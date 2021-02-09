package org.runaway.entity;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.entity.skills.DamageSkill;
import org.runaway.entity.skills.MobSkill;
import org.runaway.entity.skills.RepetitiveSkill;
import org.runaway.enums.EConfig;
import org.runaway.managers.GamerManager;
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

    public MobController init() {
        setMobRare(MobRare.DEFAULT);
        setMobRandom(new Random());
        setMobTasks(new ArrayList());
        setDamageMap(new HashMap());
        initFirstSpawn();
        if (getRespawnTime() != 0) {
            new SyncRepeatTask(() -> {
                if (getTimeTextLine() != null && getRespawnTimeLeft() > 0) {
                    getTimeTextLine().setText(Utils.colored("&a" + TimeUtils.getDuration(getRespawnTimeLeft())));
                }
            }, 20);
        }
        MobManager.uidMobControllerMap.put(getUID(), this);
        MobManager.attributableMap.put(attributable.getTechName(), attributable);
        return this;
    }

    @Override
    public void spawn() {
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
    }

    @Override
    public boolean onDamage(DamageSource damageSource, float f) {
        net.minecraft.server.v1_12_R1.Entity damagerEntity = damageSource.i();
        if (damagerEntity == null) {
            return false;
        }
        if (damagerEntity.getBukkitEntity() instanceof Player) {
            Player player = (Player) damagerEntity.getBukkitEntity();
            if(getMobSkillList() != null) {
                getMobSkillList().forEach(mobSkill -> {
                    if(mobSkill instanceof DamageSkill) {
                        mobSkill.getConsumer().accept(getNmsEntity(), player);
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
        getBukkitEntity().setCustomName(Utils.colored("&7[" + attributable.getMobLevel() + "&7] " + attributable.getName() + "&4 ❤ " + (int) getBukkitEntity().getHealth()));
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
        if (getRespawnTime() == 0) return;

        if (this.bukkitEntity == null) {
            if (System.currentTimeMillis() / 1000L - this.lastDeathTime >= this.respawnTime) {
                this.spawn();
            } else {
                if (attributable.isBoss()) createHologram();
                setSpawnTask(new SyncTask(this::spawn, getRespawnTimeLeft() * 20));
            }
        }
    }

    private int getRespawnTimeLeft() {
        return (int) (getRespawnTime() - (System.currentTimeMillis() / 1000L - getLastDeathTime()));
    }

    private void createHologram() {
        if (getRespawnTime() == 0) return;
        setInfoHologram(HologramsAPI.createHologram(Prison.getInstance(), this.getSpawnLocation().clone().add(0.0, 2.0, 0.0)));
        getInfoHologram().appendTextLine(Utils.colored("&7[" + getAttributable().getMobLevel() + "&7] " + getAttributable().getName()));
        setTimeTextLine(getInfoHologram().appendTextLine(Utils.colored("&a" + TimeUtils.getDuration(getRespawnTimeLeft()))));
    }

    @Override
    public void die() {
        setLastDeathTime(System.currentTimeMillis() / 1000L);
        Map<Gamer, Double> damagePercentMap = new HashMap();
        if (getRespawnTime() == 0 && getUID() != null) {
            MobManager.uidMobControllerMap.remove(getUID());
            if (getSpawnTask() != null) getSpawnTask().stop();
        }
        getDamageMap().forEach((nickname, damage) -> {
            if (getTotalDamage() == 0) {
                setTotalDamage(1);
            }
            Gamer gamer = GamerManager.getGamer(nickname);
            if (Bukkit.getPlayerExact(nickname) == null || gamer == null) {
                this.totalDamage -= damage.getDamage();
                return;
            }
            gamer.increaseQuestValue(attributable.getTechName() + "Dmg", (int) damage.getDamage());
            if (System.currentTimeMillis() - damage.getLastDamageTime() > 2 * 60 * 1000) {
                gamer.sendMessage("&cВы нападали на моба и он был убит, но вы не получите награды, т.к. прошло более 2-ух минут с последнего удара.");
                this.totalDamage -= damage.getDamage();
                return;
            }
            damagePercentMap.put(gamer, damage.getDamage() / getTotalDamage());
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
        if (getRespawnTime() != 0) {
            setSpawnTask(new SyncTask(this::spawn, getRespawnTime() * 20));
        }
        if (getAttributable().isBoss()) {
            createHologram();
        }
        getMobTasks().forEach(Cancellable::stop);
        save();
    }

    public void save() {
        if (getRespawnTime() == 0) return;
        EConfig.MOBS.getConfig().set("mobs." + getUID() + ".lastDeathTime", getLastDeathTime());
        EConfig.MOBS.saveConfig();
    }

    @Override
    public boolean isAlive() {
        return nmsEntity != null;
    }
}
