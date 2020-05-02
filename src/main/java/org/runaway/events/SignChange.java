package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.runaway.Main;

/*
 * Created by _RunAway_ on 20.1.2019
 */


public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getLines() != null && event.getLines()[0] != null && event.getLines()[1] != null) {
            String[] mas = event.getLines();
            if (mas[0].equalsIgnoreCase("sell") && player.isOp()) {
                event.setCancelled(true);
                Sign sign = (Sign)event.getBlock().getState();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> refreshSign(sign), 20L);
            }
        }
    }

    private void refreshSign(Sign sign) {
        sign.setLine(0, ChatColor.DARK_GREEN + "|---|-*-|---|");
        sign.setLine(1, ChatColor.BLACK + "Нажми, чтобы");
        sign.setLine(2, ChatColor.BLACK + "всё продать");
        sign.setLine(3, ChatColor.DARK_GREEN + "|---|-*-|---|");
        sign.update();
    }
}
