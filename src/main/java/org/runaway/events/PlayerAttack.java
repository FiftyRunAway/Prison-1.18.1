package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.events.custom.PlayerDamageEvent;
import org.runaway.managers.GamerManager;
import org.runaway.passiveperks.perks.Killer;
import org.runaway.utils.Utils;

public class PlayerAttack implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player))) {
            Player damager = null;
            if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
            if (event.getDamager() instanceof Projectile) damager = (Player) ((Projectile) event.getDamager()).getShooter();
            if (!canAttack(damager, (Player)event.getEntity())) {
                Main.gamers.get(damager.getUniqueId()).sendMessage(EMessage.FRIENDATTACK);
                event.setCancelled(true);
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerDamageEvent(damager, (Player)event.getEntity()));
            }
        }
        if (event.getDamager() instanceof Player) {
            Player p = (Player)event.getDamager();
            Gamer gamer = GamerManager.getGamer(p);
            if (p.getInventory().getItemInMainHand().getType().toString().endsWith("SWORD")) {
                if (gamer.getIntStatistics(EStat.LEVEL) < gamer.getLevelItem()) {
                    event.setCancelled(true);
                    p.sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replaceAll("%level%", gamer.getLevelItem() + ""));
                    return;
                }
            }
            Gamer attacker = GamerManager.getGamer(p);
            int boost = 0;
            if (attacker.hasPassivePerk(new Killer())) boost += 1;
            event.setDamage(event.getDamage() + (double) (attacker.getIntStatistics(EStat.GYM_TRAINER) + boost) * 4 / 100);
        }
    }

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getCaught() instanceof Player) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD)) {
                if (!canAttack(player, (Player) event.getCaught())) {
                    GamerManager.getGamer(player).sendMessage(EMessage.FRIENDATTACK);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean canAttack(Player attacker, Player getDamager) {
        Gamer at = Main.gamers.get(attacker.getUniqueId());
        Gamer get = Main.gamers.get(getDamager.getUniqueId());
        if (at.getFaction().equals(get.getFaction())) {
            if (!at.getFaction().equals(FactionType.DEFAULT)) {
                return false;
            }
        }
        return true;
    }
}
