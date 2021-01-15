package org.runaway.mines;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EMessage;

import java.util.ArrayList;

public class Location {

    public static ArrayList<Location> locations = new ArrayList<>();

    private String name;
    private String loc_name;

    public Location(String name, String loc_name) {
        this.name = name;
        this.loc_name = loc_name;
    }

    public void getLocation(Gamer gamer) {
        Player player = gamer.getPlayer();
        try {
            if (player.getInventory().getItemInMainHand() != null &&
                    player.getInventory().getItemInMainHand().hasItemMeta() &&
                    ChatColor.stripColor(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains(name)) {
                if (!gamer.getLocations().contains(getLocName())) {
                    gamer.getLocations().add(getLocName());
                    gamer.sendMessage(EMessage.ACTIVATELOCATION);
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    Achievement.FIRST_LOCATION.get(player);
                } else {
                    gamer.sendMessage(EMessage.ALREADYHAVE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getLocName() {
        return loc_name;
    }
}
