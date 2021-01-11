package org.runaway.managers;

import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;

import java.util.UUID;

public class GamerManager {

    public static Gamer getGamer(UUID uuid) {
        return Main.gamers.get(uuid);
    }

    public static Gamer getGamer(Player player) {
        return Main.gamers.get(player.getUniqueId());
    }

    public static void createGamer(Player player) {
        Main.gamers.put(player.getUniqueId(), new Gamer(player.getUniqueId()));
    }
}
