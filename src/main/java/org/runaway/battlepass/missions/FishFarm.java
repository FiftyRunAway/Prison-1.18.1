package org.runaway.battlepass.missions;

import com.warring.fishing.events.FishCatchEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;

public class FishFarm extends IMission implements Listener {

    @EventHandler
    public void onPlayerFish(FishCatchEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        addAllValues(gamer);
    }

    @Override
    public String getDescription() {
        return "Поймайте несколько рыбёшек в пруду";
    }

    @Override
    public String getArgumentsString() {
        return "fish_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
