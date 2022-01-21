package org.runaway.entity.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.runaway.entity.Armor;
import org.runaway.entity.Attributable;
import org.runaway.entity.IMobController;
import org.runaway.entity.MobRare;

import java.util.Locale;

public class CustomMonsterEntity extends Monster {
    private IMobController iMobController;
    private Attributable attributable;

    public CustomMonsterEntity(IMobController mobController) {
        super(mobController.getAttributable().getMobType().getType(),
                ((CraftWorld) mobController.getSpawnLocation().getWorld()).getHandle());
        this.iMobController = mobController;
        this.attributable = iMobController.getAttributable();
        double health = attributable.getHealth();
        double damage = attributable.getDamage();
        double speed = attributable.getSpeed();
        MobRare mobRare = iMobController.getMobRare();
        if (mobRare != MobRare.DEFAULT && mobRare != null) {
            damage += mobRare.getAdditionalDamage();
            health *= mobRare.getMultiplier();
            speed *= mobRare.getMultiplier();
        }
        this.getAttributes().getInstance(Attributes.FOLLOW_RANGE).setBaseValue(128.0);
        this.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttributes().getInstance(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(-1);
        this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        ((LivingEntity)this.getBukkitEntity()).setRemoveWhenFarAway(false);
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, ServerPlayer.class, (float)128.0));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 10.0));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, ServerPlayer.class, true));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, damage, true));
        this.setHealth((float) health);
        this.setCustomNameVisible(true);
        this.setCanPickUpLoot(false);
        this.setSecondsOnFire(0);
        //this.setPersistenceRequired();

        Armor armor = mobController.getAttributable().getArmor();
        if (armor == null) return;
        EntityEquipment equipment = ((LivingEntity) this.getBukkitEntity()).getEquipment();
        if (armor.getBoots() != null) equipment.setBoots(armor.getBoots());
        if (armor.getLeggings() != null) equipment.setLeggings(armor.getLeggings());
        if (armor.getChestplate() != null) equipment.setChestplate(armor.getChestplate());
        if (armor.getHelmet() != null) equipment.setHelmet(armor.getHelmet());
        if (armor.getItemInHand() != null) equipment.setItemInMainHand(armor.getItemInHand());
    }

    protected boolean damageEntity0(DamageSource damagesource, float f) {
        if (!iMobController.onDamage(damagesource, f)) {
            return false;
        }
        return super.damageEntity0(damagesource, f);
    }

    public void baseTick() {
        try {
            super.baseTick();
            if (this.getTarget() != null) {
                if (this.getTarget() != null && this.getTarget().getBukkitEntity().getLocation().getWorld() != this.getBukkitEntity().getWorld()) {
                    this.setTarget(null);
                } else if (!(this.getTarget() instanceof ServerPlayer)) {
                    this.setTarget(null);
                } else if (this.getTarget() != null && this.passengers.isEmpty()) {
                    Location currentLocation = this.getBukkitEntity().getLocation();
                    Location targetLocation = this.getTarget().getBukkitEntity().getLocation();
                    boolean isSameWorld = currentLocation.getWorld() == targetLocation.getWorld();
                    double distance = isSameWorld ? currentLocation.distance(targetLocation) : 32.0;
                    if (distance <= 16.0) {
                        this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(attributable.getSpeed());
                    } else {
                        this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void die(DamageSource damagesource) {
        iMobController.die();
        super.die(damagesource);
    }
}
