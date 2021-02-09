package org.runaway.entity;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.runaway.entity.skills.DamageSkill;
import org.runaway.entity.skills.MobSkill;
import org.runaway.entity.skills.RepetitiveSkill;
import org.runaway.enums.EConfig;
import org.runaway.items.ItemManager;
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
        spider();
        initAllControllers();
        if(false) { //test
            MobLoot mobLoot = SimpleMobLoot.builder().minMoney(100).maxMoney(200)
                    .lootItems(Arrays.asList(
                            LootItem.builder().chance(0.2f).maxAmount(1).prisonItem(ItemManager.getPrisonItem("spickaxe3_7")).build(),
                            LootItem.builder().chance(0.3f).minAmount(1).maxAmount(2).prisonItem(ItemManager.getPrisonItem("shears0_1")).build()
                    )).build();
            Attributable attributable = PrisonMobPattern.builder()
                    .mobLevel(3).damage(2).boss(false).health(50).speed(0.3)
                    .regenerationDelay(15).regenerationValue(3)
                    .name("&aЗомби").techName("zombie")
                    .mobType(MobType.SILVERFISH)
                    .mobLoot(mobLoot)
                    .boss(true)
                    .onSpawnConsumer(livingEntity -> {
                        EntityEquipment entityEquipment = livingEntity.getEquipment();
                        entityEquipment.setHelmet(new ItemStack(Material.IRON_HELMET));
                        entityEquipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    })
                    .build();

            MobController mobController = MobController.builder()
                    .attributable(attributable) //тут паттерн моба
                    .mobRandom(new Random())
                    .mobSkillList(Arrays.asList(
                            new DamageSkill((entity, player) -> { //умение при атаке со стороны игрока
                                LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
                                if(livingEntity.getHealth() < attributable.getHealth() * 0.1) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                                    player.damage(2);
                                }
                            }),
                            new RepetitiveSkill(entity -> { //повторяющееся умение
                                entity.getBukkitEntity().getNearbyEntities(10, 10, 10).stream()
                                        .filter(entity1 -> entity1 instanceof LivingEntity)
                                        .map(entity1 -> (LivingEntity) entity1)
                                        .forEach(livingEntity -> {
                                            livingEntity.damage(4);
                                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10));
                                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 2));
                                        });
                            }, 20 * 30)
                    ))
                    .spawnLocation(new Location(Bukkit.getWorld("Prison"), 680, 75, -540))
                    .respawnTime(30) //sec
                    .lastDeathTime(-1)
                    .UID(Utils.generateUID())
                    .build();
            mobController.init();
        }
    }

    private void spider() {
        ConfigurationSection section = getInfoSection(MobType.SPIDER);
        int money = section.getInt("money");
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(money - 25).maxMoney(money).lootItems(
                Arrays.asList(
                        LootItem.builder().chance(0.6f).amount(1).prisonItem(ItemManager.getPrisonItem("star")).build(),
                        LootItem.builder().chance(0.8f).minAmount(4).maxAmount(10).prisonItem(ItemManager.getPrisonItem("default_key")).build()
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
            if (ThreadLocalRandom.current().nextFloat() < 0.2) {
                entity.getBukkitEntity().getNearbyEntities(10, 10, 10).stream()
                        .filter(entity1 -> entity1 instanceof LivingEntity)
                        .map(entity1 -> (LivingEntity) entity1)
                        .forEach(le -> {
                            le.damage(4);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                        });
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
        MobLoot mobLoot = SimpleMobLoot.builder().minMoney(0).maxMoney(1).build();
        Attributable attributable = PrisonMobPattern.builder()
                .mobLevel(1).damage(2).boss(false).health(12).speed(0.3)
                .regenerationDelay(15).regenerationValue(1)
                .name("&aТюремная крыса").techName("rat")
                .mobType(MobType.SILVERFISH)
                .mobLoot(mobLoot)
                .build();
        addController(attributable);
    }

    private static void initAllControllers() {
        if (!EConfig.MOBS.getConfig().contains("mobs")) return;
        EConfig.MOBS.getConfig().getConfigurationSection("mobs").getKeys(false).forEach(s -> {
            ConfigurationSection section = EConfig.MOBS.getConfig().getConfigurationSection("mobs." + s);
            Attributable attributable = getAttributable(section.getString("type"));
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

    public static void addController(Attributable attributable) {
        if(!attributableMap.containsKey(attributable.getTechName())) {
            attributableMap.put(attributable.getTechName(), attributable);
        }
    }

    private static ConfigurationSection getInfoSection(MobType type) {
        return EConfig.MOBS.getConfig().getConfigurationSection(type.name().toLowerCase());
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