package org.runaway.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.TypeMessage;
import org.runaway.managers.GamerManager;

/*
 * Created by _RunAway_ on 11.2.2019
 */

public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Gamer.tp.contains(player.getUniqueId())) {
            if (!event.getFrom().getBlock().getLocation().equals(event.getTo().getBlock().getLocation())) {
                Gamer.tp.remove(player.getUniqueId());
                GamerManager.getGamer(player).sendTitle(TypeMessage.ERROR.getColor() + "" + TypeMessage.ERROR, ChatColor.RED + "* Вы сдвинулись *");
                Gamer gamer = GamerManager.getGamer(player);
                if (gamer.isEffected(PotionEffectType.BLINDNESS)) event.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
    }
}
