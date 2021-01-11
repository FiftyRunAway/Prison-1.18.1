package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;

public class FishFarm extends IMission implements Listener {

    @Override
    public String getDescription() {
        return "Наловите любых рыб в пруду";
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
