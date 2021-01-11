package org.runaway.events;

import com.warring.fishing.enums.FishingReward;
import com.warring.fishing.events.FishCatchEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.fishing.EFishType;
import org.runaway.fishing.Fish;
import org.runaway.fishing.fishes.Catfish;
import org.runaway.jobs.Job;
import org.runaway.jobs.JobRequriement;

public class FishCatch implements Listener {

    @EventHandler
    public void onPlayerFish(FishCatchEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        Fish fish = null;
        if (event.getReward().equals(FishingReward.LUCKY)) {
            fish = Fish.randomFish(EFishType.LEGENDARY);
            Job.addStatistics(gamer, JobRequriement.LEGENDARY_FISH);
            Achievement.FIRST_FISH.get(player, false);
        } else if (event.getReward().equals(FishingReward.AVERAGE)) {
            fish = Fish.randomFish(EFishType.EPIC);
        } else if (event.getReward().equals(FishingReward.FAIR)) {
            fish = Fish.randomFish(EFishType.RARE);
        } else if (event.getReward().equals(FishingReward.UNLUCKY)) {
            fish = Fish.randomFish(EFishType.ORDINARY);
        }
        if (fish == null) return;
        player.getInventory().addItem(fish.getIcon().item());
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {

    }
}
