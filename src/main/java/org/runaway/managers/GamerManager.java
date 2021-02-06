package org.runaway.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamerManager {
    public static Map<UUID, Gamer> gamers = new HashMap<>();

    public static Gamer getGamer(UUID uuid) {
        return gamers.get(uuid);
    }

    public static Gamer getGamer(Player player) {
        return getGamer(player.getUniqueId());
    }

    public static Gamer getGamer(String player) {
        return getGamer(Bukkit.getPlayer(player));
    }

    public static void createGamer(Player player) {
        gamers.put(player.getUniqueId(), new Gamer(player));
    }

    public static Map<UUID, Gamer> getGamers() {
        return gamers;
    }
}
