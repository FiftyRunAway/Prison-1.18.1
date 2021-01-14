package org.runaway.events;

import com.google.common.collect.Maps;
import jdk.internal.net.http.common.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.runaway.Gamer;
import org.runaway.events.custom.PlayerFishingEvent;
import org.runaway.fishing.EFishType;
import org.runaway.managers.GamerManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerFishing implements Listener {

    private Map<UUID, Pair<Boolean, EFishType>> fishingList = Maps.newHashMap();
    private Map<UUID, Long> uuidLongMap = Maps.newHashMap();

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        EFishType reward;
        if (!player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (this.fishingList.containsKey(player.getUniqueId()) && !player.equals((Object)PlayerFishEvent.State.FISHING)) {
                reward = this.fishingList.get(player.getUniqueId()).second;
                Bukkit.getServer().getPluginManager().callEvent(new PlayerFishingEvent(player, reward));
                gamer.sendTitle(reward.getName(), reward.getRewardName());
                this.fishingList.get(player.getUniqueId());

            }
        }
    }

    public int sendTitle(Gamer gamer, int boldLine, boolean front) {
        List<Integer> integerList = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 10, 11, 12, 13, 15, 16, 17, 18, 20, 21, 22, 23, 25, 26, 27, 28, 30, 31, 32, 33, 35, 36, 37, 38, 40, 41, 42, 43, 45, 46, 47, 48, 50, 51, 52, 53, 55, 56, 57, 58, 60, 61, 62, 63, 65, 66, 67, 68, 70, 71, 72, 73, 75, 76, 77, 78, 80, 81, 82, 83, 85, 86, 87, 88, 90, 91, 92, 93, 95, 96, 97, 98, 100, 101, 102, 103, 105, 106, 107, 108, 110, 111, 112, 113, 115, 116, 117, 118, 120, 121, 122, 123, 125, 126, 127, 128, 130, 131, 132, 133, 135, 136, 137, 138, 140, 141, 142, 143, 145, 146, 147, 148, 150, 151, 152, 153);
        if (integerList.contains(boldLine)) {
            if (front) {
                while (integerList.contains(boldLine)) {
                    ++boldLine;
                }
            }
            else {
                while (integerList.contains(boldLine)) {
                    --boldLine;
                }
            }
        }
        final String main = "&r&4|&r&4|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&a|&r&a|&r&2|&r&e|&r&2|&r&a|&r&a|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&4|&r&4|&r";
        final String newMain = main.substring(0, boldLine) + "&f&l" + main.substring(boldLine);
        gamer.sendFishingTeleport(newMain);
        return boldLine;
    }

    public EFishType getReward(final int index) {
        if (index == 79) {
            return EFishType.LEGENDARY;
        }
        if (index >= 72 && index <= 82) {
            return EFishType.EPIC;
        }
        if (index >= 62 && index <= 92) {
            return EFishType.RARE;
        }
        if (index >= 12 && index <= 142) {
            return EFishType.ORDINARY;
        }
        return null;
    }


}
