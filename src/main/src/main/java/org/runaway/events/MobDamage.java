package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.runaway.Gamer;
import org.runaway.entity.DamageInfo;
import org.runaway.entity.IMobController;
import org.runaway.entity.MobManager;
import org.runaway.events.custom.BossDamageEvent;
import org.runaway.managers.GamerManager;

public class MobDamage implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(EntityDamageByEntityEvent event) {
        org.bukkit.entity.Entity damager = event.getDamager();
        if (!(damager instanceof Player) && !(damager instanceof Projectile)) return;
        Player player = null;
        if (damager instanceof Player) player = (Player) damager;
        if (damager instanceof Projectile) player = (Player) ((CraftArrow)damager).getShooter();
        org.bukkit.entity.Entity target = event.getEntity();
        if (!(target instanceof LivingEntity livingEntity)) return;
        Gamer gamer = GamerManager.getGamer(player);
        if (event.isCancelled()) return;
        String nickname = player.getName();
        IMobController mobController = MobManager.getMobController(target.getEntityId());
        if (mobController == null) return;
        if (!mobController.getDamageMap().containsKey(nickname)) {
            mobController.getDamageMap().put(nickname, new DamageInfo(0, System.currentTimeMillis()));
        }
        double damage = event.getDamage();
        mobController.getDamageMap().get(nickname).addDamage(damage);
        mobController.getDamageMap().get(nickname).setLastDamageTime(System.currentTimeMillis());
        gamer.debug("&aВы нанесли " + damage + " урона.");
        if (mobController.getAttributable().isBoss())
            Bukkit.getServer().getPluginManager().callEvent(new BossDamageEvent(gamer.getPlayer(), mobController, damage));
        mobController.setTotalDamage(mobController.getTotalDamage() + damage);
        double sum = livingEntity.getHealth() - damage;
        if (!gamer.isEndedCooldown("bossND")) {
            event.setCancelled(true);
            return;
        }
        if (sum > 0) {
            gamer.sendActionbar(mobController.getAttributable().getName() + "&4 " + getPercentLine((livingEntity.getHealth() - damage) / livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        }
    }

    private String getPercentLine(double percent) {
        int fillAmount = (int) (percent * 10);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < (fillAmount + 1); i++) {
            stringBuilder.append("█");
        }
        return stringBuilder.toString();
    }
}
