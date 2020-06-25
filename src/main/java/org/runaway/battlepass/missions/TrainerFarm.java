package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.TrainerUpEvent;

public class TrainerFarm extends IMission implements Listener {

    @EventHandler
    public void onTrainerUpgrade(TrainerUpEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        addAllValues(gamer);
    }

    @Override
    public String getDescription() {
        return "Получите любые прокачки у тренера";
    }

    @Override
    public String getArgumentsString() {
        return "trainings_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
