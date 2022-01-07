package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.donate.features.BossNotify;
import org.runaway.enums.EMessage;
import org.runaway.events.custom.BossSpawnEvent;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Objects;

public class BossSpawn implements Listener {

    @EventHandler
    public void onBossSpawn(BossSpawnEvent event) {
        Utils.getPlayers().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            Gamer gamer = GamerManager.getGamer(player);
            if (gamer.hasPermission("admin") ||
                    Objects.equals(gamer.getPrivilege().getValue(new BossNotify()), true)) {
                gamer.sendMessage(EMessage.BOSSNOTIFY.getMessage().replace("%name%", event.getName()));
            }
        });
    }
}
