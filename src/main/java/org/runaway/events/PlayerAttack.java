package org.runaway.events;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.utils.Utils;

public class PlayerAttack implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getDamager() instanceof Player) {
            Player p = (Player)event.getDamager();
            Gamer gamer = Main.gamers.get(p.getUniqueId());
            if (p.getInventory().getItemInMainHand().getType().toString().endsWith("SWORD")) {
                if ((int)gamer.getStatistics(EStat.LEVEL) < gamer.getLevelItem()) {
                    event.setCancelled(true);
                    p.sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replaceAll("%level%", gamer.getLevelItem() + ""));
                    return;
                }
            }
            Gamer attacker = Main.gamers.get(p.getUniqueId());
            event.setDamage(event.getDamage() + (int)attacker.getStatistics(EStat.GYM_TRAINER) * 4 / 100);
            if (event.getEntity() instanceof Player) {
                if (!canAttack((Player)event.getDamager(), (Player)event.getEntity())) {
                    attacker.sendMessage(EMessage.FRIENDATTACK);
                    event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
            if (((Projectile) event.getDamager()).getShooter().equals(event.getEntity())) return;

            if (event.getEntity() instanceof Player) {
                Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
                if (!canAttack(attacker, (Player)event.getEntity())) {
                    Main.gamers.get(attacker.getUniqueId()).sendMessage(EMessage.FRIENDATTACK);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean canAttack(Player attacker, Player getDamager) {
        Gamer at = Main.gamers.get(attacker.getUniqueId()); Gamer get = Main.gamers.get(getDamager.getUniqueId());
        if (at.getFaction().equals(get.getFaction())) {
            if (!at.getFaction().equals(FactionType.DEFAULT)) {
                return false;
            }
        }
        return true;
    }
}
