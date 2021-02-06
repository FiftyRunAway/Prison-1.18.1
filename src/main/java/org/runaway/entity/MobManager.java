package org.runaway.entity;

import com.nametagedit.plugin.utils.Utils;
import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.LootItem;
import org.runaway.entity.skills.DamageSkill;
import org.runaway.entity.skills.RepetitiveSkill;
import org.runaway.enums.MobType;
import org.runaway.items.ItemManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MobManager {
    public static Map<Integer, Entity> idEntityMap = new HashMap();
    public static Map<Integer, IMobController> mobControllerMap = new HashMap();
    public static Map<String, IMobController> uidMobControllerMap = new HashMap();
    public static Map<String, Attributable> attributableMap = new HashMap();

    public MobManager() {
        if(true) { //test
            MobLoot mobLoot = SimpleMobLoot.builder().minMoney(100).maxMoney(200)
                    .lootItems(Arrays.asList(
                            LootItem.builder().chance(0.2f).maxAmount(1).prisonItem(ItemManager.getPrisonItem("spickaxe3_7")).build(),
                            LootItem.builder().chance(0.3f).minAmount(1).maxAmount(2).prisonItem(ItemManager.getPrisonItem("shears0_1")).build()
                    )).build();
            Attributable attributable = PrisonMobPattern.builder()
                    .mobLevel(3).damage(2).boss(false).health(50).speed(0.3)
                    .regenerationDelay(15).regenerationValue(1)
                    .name("&aЗомби").techName("zombie")
                    .mobType(MobType.RAT)
                    .mobLoot(mobLoot)
                    .boss(true)
                    .onSpawnConsumer(livingEntity -> {
                        EntityEquipment entityEquipment = livingEntity.getEquipment();
                        entityEquipment.setHelmet(new ItemStack(Material.IRON_HELMET));
                        entityEquipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    })
                    .build();

            MobController mobController = MobController.builder()
                    .attributable(attributable)
                    .mobRandom(new Random())
                    .mobSkillList(Arrays.asList(
                            new DamageSkill((entity, player) -> {
                                LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
                                if(livingEntity.getHealth() < attributable.getHealth() * 0.1) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                                    player.damage(2);
                                }
                            }),
                            new RepetitiveSkill(entity -> {
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
                    .UID(Utils.generateUUID())
                    .build();
            mobController.init();
        }
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
