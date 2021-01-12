package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.enums.SaveType;
import org.runaway.enums.ServerStatus;
import org.runaway.inventories.BattlePassMenu;
import org.runaway.managers.GamerManager;
import org.runaway.sqlite.DoVoid;
import org.runaway.sqlite.PreparedRequests;
import org.runaway.utils.Utils;

import java.util.Arrays;

/*
 * Created by _RunAway_ on 20.1.2019
 */

public class PlayerQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        removeMissions(player);
        SavePlayer(player.getName());
        Utils.getPlayers().remove(player.getName());
        removeBar(player);
        if (BlockBreak.treasure_holo.containsKey(player.getName())) {
            Bukkit.getWorld(player.getWorld().getName()).getBlockAt(BlockBreak.treasure_holo.get(player.getName()).getLocation().add(-0.5, -1.5, -0.5)).setType(Material.AIR);
        }
        Gamer.tp.remove(player.getUniqueId());
        Main.gamers.remove(player.getUniqueId());
        BlockBreak.to_break.remove(player.getName());
        BattlePassMenu.data.remove(player.getName());
    }

    private void removeMissions(Player player) {
        BattlePass.missions.forEach(weeklyMission -> weeklyMission.getMissions().forEach(mission -> {
            if (!mission.getValues().containsKey(player.getName())) return;
            EConfig.BATTLEPASS_DATA.getConfig().set(mission.hashCode() + "." + player.getName(), mission.getValues().get(player.getName()));
            mission.getValues().remove(player.getName());
        }));
        EConfig.BATTLEPASS_DATA.saveConfig();
    }

    private void removeBar(Player player) {
        Main.MoneyBar.removePlayer(player);
        Main.BlocksBar.removePlayer(player);
    }

    public static void SavePlayer(String g) {
        Gamer gamer = GamerManager.getGamer(g);
        if (Main.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            Arrays.stream(EStat.values()).forEach(eStat -> {
                PreparedRequests.voidRequest(DoVoid.UPDATE, Main.getMainDatabase(), g, Main.getInstance().stat_table, gamer.getStatistics(eStat), eStat.getStatName());
                eStat.getMap().remove(g);
            });
            return;
        }

        Arrays.stream(EStat.values()).forEach(stat -> {
            EConfig.STATISTICS.getConfig().set(g + "." + stat.getStatName(), gamer.getStatistics(stat));
            stat.getMap().remove(g);
        });
        EConfig.STATISTICS.saveConfig();
    }
}
