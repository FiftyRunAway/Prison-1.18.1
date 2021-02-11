package org.runaway.entity;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntitySlime;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.runaway.Gamer;
import org.runaway.entity.skills.DamageSkill;
import org.runaway.entity.skills.MobSkill;
import org.runaway.entity.skills.RepetitiveSkill;
import org.runaway.enums.EConfig;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.rewards.LootItem;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MobManager {
    public static Map<Integer, Entity> idEntityMap = new HashMap();
    public static Map<Integer, IMobController> mobControllerMap = new HashMap();
    public static Map<String, IMobController> uidMobControllerMap = new HashMap();
    public static Map<String, Attributable> attributableMap = new HashMap();

    public MobManager() {
        rats();
        zombie();
        spider();
        slime();
        golem();
        blaze();

        initAllControllers();
    }

    private void blaze() {
        ConfigurationSection section = getInfoSection("magma");
        int money = section.getInt("money");
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(getMinMoney(money)).maxMoney(money).lootItems(
                Arrays.asList(
                        LootItem.builder().chance(0.6f).minAmount(1).maxAmount(3).prisonItem(ItemManager.getPrisonItem("star")).build(),
                        LootItem.builder().chance(0.9f).minAmount(8).maxAmount(12).prisonItem(ItemManager.getPrisonItem("defaultKey")).build()
                )).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(4).damage(section.getInt("damage")).boss(true).health(section.getInt("health")).speed(section.getDouble("speed"))
                .regenerationDelay(30).regenerationValue(2)
                .name(section.getString("name")).techName("blaze")
                .mobType(MobType.BLAZE)
                .mobLoot(mobLoot)
                .build();
        List<MobSkill> skills = new ArrayList<>();
        skills.add(new DamageSkill((entity, player) -> {
            if (ThreadLocalRandom.current().nextFloat() < 0.15) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
                player.setFireTicks(85);
            }
        }));
        skills.add(new RepetitiveSkill(entity -> {
            LivingEntity e = (LivingEntity) entity.getBukkitEntity();
            e.getWorld().playEffect(e.getLocation(), Effect.MOBSPAWNER_FLAMES, 10);
            e.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 120, 0));
            e.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 1));
            e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0));
            entity.getBukkitEntity().getNearbyEntities(10, 10, 10).stream()
                    .filter(entity1 -> entity1 instanceof Player)
                    .map(entity1 -> (Player) entity1)
                    .forEach(livingEntity -> {
                        livingEntity.damage(2);
                        e.getWorld().playSound(e.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.4f, 1f);
                        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0f, 1.0f);
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 2));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 2));
                        Gamer gamer = GamerManager.getGamer(livingEntity);
                        if(gamer == null) return;
                        gamer.sendMessage("&eЯ РАЗЪЯРЁЁЁН!");
                    });
        }, 20 * 30));
        attributable.setMobSkills(skills);
        addController(attributable);
    }

    private void golem() {
        ConfigurationSection section = getInfoSection("golem");
        int money = section.getInt("money");
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(getMinMoney(money)).maxMoney(money).lootItems(
                Arrays.asList(
                        LootItem.builder().chance(0.55f).minAmount(1).maxAmount(3).prisonItem(ItemManager.getPrisonItem("star")).build(),
                        LootItem.builder().chance(0.8f).minAmount(6).maxAmount(12).prisonItem(ItemManager.getPrisonItem("defaultKey")).build()
                )).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(5).damage(section.getInt("damage")).boss(true).health(section.getInt("health")).speed(section.getDouble("speed"))
                .regenerationDelay(20).regenerationValue(1)
                .name(section.getString("name")).techName("golem")
                .mobType(MobType.GOLEM)
                .mobLoot(mobLoot)
                .build();
        List<MobSkill> skills = new ArrayList<>();
        skills.add(new DamageSkill((entity, player) -> {
            LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
            if(livingEntity.getHealth() < attributable.getHealth() * 0.1) {
                player.damage(2);
                return;
            }
            if (ThreadLocalRandom.current().nextFloat() < 0.12) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                livingEntity.getLocation().getWorld().playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
                Gamer gamer = GamerManager.getGamer(player);
                if(gamer == null) return;
                gamer.sendMessage("&eВы попали в капкан");
            }
        }));
        skills.add(new RepetitiveSkill(entity -> {
            entity.getBukkitEntity().getWorld().playEffect(entity.getBukkitEntity().getLocation(), Effect.SMOKE, 10);
            entity.getBukkitEntity().setVelocity(new Vector(0, 1.05, 0).multiply(1));

            entity.getBukkitEntity().getNearbyEntities(10, 10, 10).stream()
                    .filter(entity1 -> entity1 instanceof Player)
                    .map(entity1 -> (Player) entity1)
                    .forEach(livingEntity -> {
                        livingEntity.damage(4);
                        new SyncTask(() -> {
                            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                            livingEntity.setVelocity(livingEntity.getVelocity().add(livingEntity.getLocation().getDirection()).multiply(-1));
                            Gamer gamer = GamerManager.getGamer(livingEntity);
                            if(gamer == null) return;
                            gamer.sendMessage("&eПока!");
                        }, 25);
                    });
        }, 20 * 45));
        attributable.setMobSkills(skills);
        addController(attributable);
    }

    private void slime() {
        ConfigurationSection section = getInfoSection("slime");
        int money = section.getInt("money");
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(getMinMoney(money)).maxMoney(money).lootItems(
                Arrays.asList(
                        LootItem.builder().chance(0.6f).minAmount(1).maxAmount(2).prisonItem(ItemManager.getPrisonItem("star")).build(),
                        LootItem.builder().chance(0.8f).minAmount(6).maxAmount(12).prisonItem(ItemManager.getPrisonItem("defaultKey")).build()
                )).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(3).damage(section.getInt("damage")).boss(true).health(section.getInt("health")).speed(section.getDouble("speed"))
                .regenerationDelay(25).regenerationValue(1)
                .name(section.getString("name")).techName("slime")
                .mobType(MobType.SLIME)
                .mobLoot(mobLoot)
                .build();
        List<MobSkill> skills = new ArrayList<>();
        skills.add(new DamageSkill(((entity, player) -> {
            LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
            if(livingEntity.getHealth() < attributable.getHealth() * 0.2) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 220, 1));
                player.damage(3);
            }
        })));
        skills.add(new RepetitiveSkill(entity ->
                entity.getBukkitEntity().getNearbyEntities(10, 10, 10).stream()
                .filter(entity1 -> entity1 instanceof Player)
                .map(entity1 -> (Player) entity1)
                .forEach(livingEntity -> {
                    livingEntity.damage(2);
                    new SyncTask(() -> {
                        Gamer gamer = GamerManager.getGamer(livingEntity);
                        if(gamer == null) return;
                        gamer.sendMessage("&eНе ожидали от меня такого?");
                        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f, 1.0f);
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 140, 2));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 2));
                    }, 25);
                }), 20 * 55));
        attributable.setMobSkills(skills);
        addController(attributable);
    }

    private void spider() {
        ConfigurationSection section = getInfoSection("spider");
        int money = section.getInt("money");
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(getMinMoney(money)).maxMoney(money).lootItems(
                Arrays.asList(
                        LootItem.builder().chance(0.6f).amount(1).prisonItem(ItemManager.getPrisonItem("star")).build(),
                        LootItem.builder().chance(0.8f).minAmount(4).maxAmount(10).prisonItem(ItemManager.getPrisonItem("defaultKey")).build()
                )).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(2).damage(section.getInt("damage")).boss(true).health(section.getInt("health")).speed(section.getDouble("speed"))
                .regenerationDelay(30).regenerationValue(1)
                .name(section.getString("name")).techName("spider")
                .mobType(MobType.SPIDER)
                .mobLoot(mobLoot)
                .build();
        List<MobSkill> skills = new ArrayList<>();
        skills.add(new DamageSkill((entity, player) -> {
            LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
            if(livingEntity.getHealth() < attributable.getHealth() * 0.1) {
                player.damage(2);
                return;
            }
            if (ThreadLocalRandom.current().nextFloat() < 0.3) {
                player.damage(4);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
            }
        }));
        skills.add(new RepetitiveSkill((entity) -> {
            entity.getBukkitEntity().getWorld().playEffect(entity.getBukkitEntity().getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
            entity.getBukkitEntity().setVelocity(new Vector(0, 1, 0).multiply(1));

            entity.getBukkitEntity().getNearbyEntities(10, 10, 10).stream()
                    .filter(entity1 -> entity1 instanceof LivingEntity)
                    .map(entity1 -> (LivingEntity) entity1)
                    .forEach(livingEntity -> {
                        livingEntity.damage(2);
                        new SyncTask(() -> {
                            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                            livingEntity.setVelocity(livingEntity.getVelocity().add(livingEntity.getLocation().getDirection()).multiply(-2));
                        }, 25);
                    });
        }, 20 * 40));
        attributable.setMobSkills(skills);
        addController(attributable);
    }

    private void rats() {
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(0.1).maxMoney(0.50).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(1).damage(2).boss(false).health(12).speed(0.025)
                .regenerationDelay(15).regenerationValue(1)
                .name("&aТюремная крыса").techName("rat")
                .mobType(MobType.SILVERFISH)
                .mobLoot(mobLoot)
                .build();
        addController(attributable);
    }

    private void zombie() {
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(0.8).maxMoney(1.25)
                .lootItems(Arrays.asList(
                        LootItem.builder().chance(0.08f).amount(1).prisonItem(ItemManager.getPrisonItem("gapple")).build(),
                        LootItem.builder().chance(0.12f).minAmount(1).maxAmount(4).prisonItem(ItemManager.getPrisonItem("arrow")).build()
                )).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(1).damage(4).boss(false).health(50).speed(0.025)
                .regenerationDelay(30).regenerationValue(1)
                .name("&aЗомби").techName("zombie")
                .mobType(MobType.ZOMBIE)
                .mobLoot(mobLoot)
                .build();
        addController(attributable);
    }

    private static void initAllControllers() {
        if (!EConfig.MOBS.getConfig().contains("mobs")) return;
        EConfig.MOBS.getConfig().getConfigurationSection("mobs").getKeys(false).forEach(s -> {
            ConfigurationSection section = EConfig.MOBS.getConfig().getConfigurationSection("mobs." + s);
            Attributable attributable = getAttributable(section.getString("type"));
            if(attributable == null) return;
            MobController.builder()
                    .attributable(attributable) //тут паттерн моба
                    .spawnLocation(Utils.unserializeLocation(section.getString("location")))
                    .respawnTime(section.getInt("respawnTime")) //sec
                    .lastDeathTime(section.getLong("lastDeathTime"))
                    .UID(s)
                    .mobSkillList(attributable.getMobSkills())
                    .build()
                    .init();
        });
    }

    private static int getMinMoney(int maximum) {
        return maximum - Math.round((float) maximum / 10);
    }

    public static void addController(Attributable attributable) {
        if(!attributableMap.containsKey(attributable.getTechName())) {
            attributableMap.put(attributable.getTechName(), attributable);
        }
    }

    private static ConfigurationSection getInfoSection(String mobName) {
        return EConfig.MOBS.getConfig().getConfigurationSection(mobName);
    }

    public static Attributable getAttributable(String techName) {
        return attributableMap.get(techName);
    }

    public static IMobController getMobController(org.bukkit.entity.Entity entity) {
        return getMobController(entity.getEntityId());
    }

    public static IMobController getMobController(int entityId) {
        return mobControllerMap.get(entityId);
    }
}