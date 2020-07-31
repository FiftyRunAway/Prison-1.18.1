package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.donate.features.BossNotify;
import org.runaway.enums.EMessage;
import org.runaway.events.custom.BossSpawnEvent;
import org.runaway.utils.Utils;

import java.util.Objects;

public class BossSpawn implements Listener {

    @EventHandler
    public void onBossSpawn(BossSpawnEvent event) {
        Utils.getPlayers().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            Gamer gamer = Main.gamers.get(player.getUniqueId());
            if (Objects.equals(gamer.getPrivilege().getValue(new BossNotify()), true)) {
                player.sendMessage(Utils.colored(EMessage.BOSSNOTIFY.getMessage()).replaceAll("%name%", event.getName()));
            }
        });
    }
}
