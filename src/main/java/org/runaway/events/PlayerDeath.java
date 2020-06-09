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
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.board.Board;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.events.custom.PlayerKillEvent;
import org.runaway.utils.Utils;

/*
 * Created by _RunAway_ on 27.1.2019
 */

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        event.setKeepInventory(true); event.setKeepLevel(true);

        Location loc = player.getLocation();
        event.getDrops().forEach(itemStack -> {
            if (itemStack.getItemMeta().getLore() == null) {
                loc.getWorld().dropItemNaturally(loc, itemStack);
                player.getInventory().remove(itemStack);
            }
        });
        boolean givenot = false;
        int money = (int)gamer.getStatistics(EStat.LEVEL);
        if ((double)gamer.getStatistics(EStat.MONEY) >= money) {
            gamer.withdrawMoney(money);
        } else {
            givenot = true;
            gamer.setStatistics(EStat.MONEY, 0);
        }

        player.sendMessage(Utils.colored(EMessage.DIEDPLAYER.getMessage()).replace("%money%", Board.FormatMoney(money)));
        gamer.setStatistics(EStat.DEATHES, (int)gamer.getStatistics(EStat.DEATHES) + 1);
        if ((int)gamer.getStatistics(EStat.DEATHES) >= 5) Achievement.DEAD_5.get(player, false);
        if ((int)gamer.getStatistics(EStat.DEATHES) >= 100) Achievement.DEAD_100.get(player, false);
        if (event.getEntity().getKiller() != null) {
            Gamer gamerKiller = Main.gamers.get(event.getEntity().getKiller().getUniqueId());
            gamerKiller.setStatistics(EStat.KILLS, (int)gamerKiller.getStatistics(EStat.KILLS) + 1);
            if ((int)gamerKiller.getStatistics(EStat.KILLS) >= 5) Achievement.KILL_5.get(gamer.getPlayer(), false);
            if ((int)gamerKiller.getStatistics(EStat.KILLS) >= 100) Achievement.KILL_100.get(gamer.getPlayer(), false);
            if (givenot) return;

            Bukkit.getServer().getPluginManager().callEvent(new PlayerKillEvent(player));

            gamerKiller.depositMoney(money);
            gamerKiller.getPlayer().sendMessage(Utils.colored(EMessage.KILLPLAYER.getMessage()).replace("%player%", gamer.getGamer()).replace("%money%", Board.FormatMoney(money)));
            if (gamerKiller.getPlayer().hasPermission("*")) Achievement.KILL_ADMIN.get(gamerKiller.getPlayer(), false);
        } else if (event.getEntity() instanceof Projectile && ((Projectile)event.getEntity()).getShooter() instanceof Player) {
            if (event.getEntity().getKiller().getName().equals(event.getEntity().getName())) Achievement.KILL_ARROW.get(player, false);
            Gamer gamerKiller = Main.gamers.get(event.getEntity().getKiller().getUniqueId());
            gamerKiller.setStatistics(EStat.BOW_KILL, (int)gamerKiller.getStatistics(EStat.BOW_KILL) + 1);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Main.SPAWN);
        Player player = event.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 220, 3));
    }
}
