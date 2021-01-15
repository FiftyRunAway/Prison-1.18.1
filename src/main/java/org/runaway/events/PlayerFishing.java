package org.runaway.events;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.events.custom.PlayerFishingEvent;
import org.runaway.fishing.EFishType;
import org.runaway.fishing.Fish;
import org.runaway.fishing.pair.Pair;
import org.runaway.jobs.Job;
import org.runaway.jobs.JobRequriement;
import org.runaway.managers.GamerManager;
import org.runaway.tasks.SyncTask;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerFishing implements Listener {

    private final Map<UUID, Pair<Boolean, EFishType>> fishingList = Maps.newHashMap();
    private final Map<UUID, Long> uuidLongMap = Maps.newHashMap();
    private Map<UUID, UUID> fisherman = Maps.newHashMap();

    @EventHandler
    public void onFishReward(PlayerFishingEvent event) {
        EFishType fish = event.getReward();
        Fish f = Fish.randomFish(fish);
        if (f != null) {
            if (fish == EFishType.LEGENDARY) Job.addStatistics(GamerManager.getGamer(event.getPlayer()), JobRequriement.LEGENDARY_FISH);
            event.getPlayer().getInventory().addItem(f.getIcon().item());
        }
    }

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        event.setExpToDrop(0);
        if (!BlockBreak.isLocation(player.getLocation(), "fisherman")) return;
        FishHook fh = event.getHook();
        Gamer gamer = GamerManager.getGamer(player);
        EFishType reward;
        if (!player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (event.getState() != PlayerFishEvent.State.FISHING) {
                fisherman.remove(player.getUniqueId());
            }
            if (this.fishingList.containsKey(player.getUniqueId()) &&
                    !player.equals((Object)PlayerFishEvent.State.FISHING)) {
                reward = this.fishingList.get(player.getUniqueId()).getValue();
                Bukkit.getServer().getPluginManager().callEvent(new PlayerFishingEvent(player, reward));
                gamer.sendTitle(reward.getName(), reward.getRewardName());
                this.fishingList.get(player.getUniqueId()).setKey(false);
            } else if (this.uuidLongMap.containsKey(player.getUniqueId())) {
                this.uuidLongMap.remove(player.getUniqueId());
            } else if (event.getState() == PlayerFishEvent.State.FISHING) {
                UUID un = UUID.randomUUID();
                fisherman.put(player.getUniqueId(), un);
                new SyncTask(() -> {
                    if (fisherman.containsKey(player.getUniqueId()) && fisherman.containsValue(un)) {
                        if (fh != null) {
                            fisherman.remove(player.getUniqueId());
                            Location location = fh.getLocation();
                            if (location.getBlock().getType() == Material.STATIONARY_WATER ||
                                    location.getBlock().getType() == Material.WATER) {
                                if (ThreadLocalRandom.current().nextDouble() * 100 <= 80) {
                                    this.procFishing(player);
                                }
                            }
                        }
                    }
                }, 35);
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
            } else {
                while (integerList.contains(boldLine)) {
                    --boldLine;
                }
            }
        }
        String main = "&r&4|&r&4|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&a|&r&a|&r&2|&r&e|&r&2|&r&a|&r&a|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&c|&r&4|&r&4|&r";
        String newMain = main.substring(0, boldLine) + "&f&l" + main.substring(boldLine);
        gamer.sendFishingTitle(newMain);
        return boldLine;
    }

    public EFishType getReward(int index) {
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
        return EFishType.NONE_REWARD;
    }

    public EFishType procFishing(Player p) {
        EFishType[] reward = { null };
        int[] count = { 0 };
        int[] boldLine = { 2 };
        boolean[] forwards = { true };
        this.uuidLongMap.put(p.getUniqueId(), System.currentTimeMillis() + 4000L);
        Gamer gamer = GamerManager.getGamer(p);
        new BukkitRunnable() {
            public void run() {
                this.cancel();
                if (!uuidLongMap.containsKey(p.getUniqueId())) {
                    return;
                }
                if (uuidLongMap.get(p.getUniqueId()) > System.currentTimeMillis()) {
                    return;
                }
                uuidLongMap.remove(p.getUniqueId());
                fishingList.put(p.getUniqueId(), new Pair<>(true, EFishType.NONE_REWARD));
                new BukkitRunnable() {
                    public void run() {
                        if (!fishingList.get(p.getUniqueId()).getKey()) {
                            this.cancel();
                            fishingList.remove(p.getUniqueId());
                            return;
                        }
                        if (count[0] == 4) {
                            this.cancel();
                            //FishNoCatchEvent event = new FishNoCatchEvent(p);
                            //Bukkit.getPluginManager().callEvent((Event)event);
                            gamer.sendFishRewardTitle(EFishType.TRY_AGAIN);
                            fishingList.get(p.getUniqueId()).setValue(getReward(boldLine[0]));
                            fishingList.remove(p.getUniqueId());
                            return;
                        }
                        if (forwards[0]) {
                            if (boldLine[0] >= 150) {
                                forwards[0] = false;
                            }
                            boldLine[0] = sendTitle(gamer, boldLine[0], true);
                            fishingList.get(p.getUniqueId()).setValue(getReward(boldLine[0]));
                            int n = 0;
                            ++boldLine[n];
                        } else {
                            if (boldLine[0] <= 8) {
                                forwards[0] = true;
                                int n2 = 0;
                                ++count[n2];
                            }
                            boldLine[0] = sendTitle(gamer, boldLine[0], false);
                            fishingList.get(p.getUniqueId()).setValue(getReward(boldLine[0]));
                            int n3 = 0;
                            --boldLine[n3];
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 1L, 1L);
            }
        }.runTaskLater(Main.getInstance(), 90L);
        return reward[0];
    }
}
