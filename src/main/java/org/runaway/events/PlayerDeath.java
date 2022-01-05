package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.achievements.Achievement;
import org.runaway.board.Board;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.events.custom.PlayerKillEvent;
import org.runaway.managers.GamerManager;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Utils;

/*
 * Created by _RunAway_ on 27.1.2019
 */

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Gamer gamer = GamerManager.getGamer(player);
        event.setKeepInventory(true); event.setKeepLevel(true);
        Location loc = player.getLocation();
        event.getDrops().forEach(itemStack -> {
            if (!itemStack.hasItemMeta()) {
                loc.getWorld().dropItemNaturally(loc, itemStack);
                player.getInventory().remove(itemStack);
            }
        });
        boolean givenot = false;
        double money = gamer.getIntStatistics(EStat.LEVEL);
        if (gamer.getMoney() >= money) {
            gamer.withdrawMoney(money);
        } else {
            givenot = true;
            gamer.setStatistics(EStat.MONEY, 0);
        }

        player.sendMessage(Utils.colored(EMessage.DIEDPLAYER.getMessage()).replace("%money%", Board.FormatMoney(money)));
        gamer.increaseIntStatistics(EStat.DEATHES);
        if (gamer.getIntStatistics(EStat.DEATHES) >= 5) Achievement.DEAD_5.get(player);
        if (gamer.getIntStatistics(EStat.DEATHES) >= 100) Achievement.DEAD_100.get(player);
        gamer.addEffect(PotionEffectType.WEAKNESS, 400, 1);
        if (event.getEntity().getKiller() != null) {
            Gamer gamerKiller = GamerManager.getGamer(event.getEntity().getKiller().getUniqueId());
            gamerKiller.increaseDoubleStatistics(EStat.KILLS);
            if (gamerKiller.getIntStatistics(EStat.KILLS) >= 5) Achievement.KILL_5.get(gamer.getPlayer());
            if (gamerKiller.getIntStatistics(EStat.KILLS) >= 100) Achievement.KILL_100.get(gamer.getPlayer());
            if (givenot) return;

            Bukkit.getServer().getPluginManager().callEvent(new PlayerKillEvent(player));

            gamerKiller.depositMoney(money, true);
            gamerKiller.getPlayer().sendMessage(Utils.colored(EMessage.KILLPLAYER.getMessage()).replace("%player%", gamer.getGamer()).replace("%money%", Board.FormatMoney(money)));
            if (gamerKiller.getPlayer().hasPermission("*")) Achievement.KILL_ADMIN.get(gamerKiller.getPlayer());
        } else if (event.getEntity() instanceof Projectile && ((Projectile)event.getEntity()).getShooter() instanceof Player) {
            if (event.getEntity().getKiller().getName().equals(event.getEntity().getName())) Achievement.KILL_ARROW.get(player);
            Gamer gamerKiller = GamerManager.getGamer(event.getEntity().getKiller().getUniqueId());
            gamerKiller.increaseIntStatistics(EStat.BOW_KILL);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Prison.SPAWN);
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        new SyncTask(() -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 220, 3));
            gamer.getPassivePerks().forEach(passive -> {
                if (!passive.isEffectAction()) return;
                passive.getPerkAction(gamer);
            });
        }, 10);
    }
}
