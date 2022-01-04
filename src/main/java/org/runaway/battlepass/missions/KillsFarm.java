package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.PlayerKillEvent;
import org.runaway.managers.GamerManager;

public class KillsFarm extends IMission implements Listener {

    @EventHandler
    public void onKillPlayer(PlayerKillEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);

        addAllValues(gamer);
    }

    @Override
    public String getDescription() {
        return ChatColor.GRAY + "Убивайте игроков";
    }

    @Override
    public String getArgumentsString() {
        return "kills_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
