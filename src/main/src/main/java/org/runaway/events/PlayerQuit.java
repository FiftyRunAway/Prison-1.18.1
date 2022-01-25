package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.battlepass.BattlePass;
import org.runaway.commands.HideCommand;
import org.runaway.enums.EConfig;
import org.runaway.enums.ServerStatus;
import org.runaway.inventories.BattlePassMenu;
import org.runaway.managers.GamerManager;
import org.runaway.needs.Needs;
import org.runaway.utils.Utils;

import java.util.Arrays;

/*
 * Created by _RunAway_ on 20.1.2019
 */

public class PlayerQuit implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        quitInPvp(gamer);
        gamer.savePlayer();
        HideCommand.removeOnQuit(gamer);
        Utils.getPlayers().remove(player.getName());
        removeBar(player);
        if (BlockBreak.treasure_holo.containsKey(player.getName())) {
            Bukkit.getWorld(player.getWorld().getName()).getBlockAt(BlockBreak.treasure_holo.get(player.getName()).getLocation().add(-0.5, -1.5, -0.5)).setType(Material.AIR);
        }
        Needs.onQuit(event);
        Gamer.tp.remove(player.getUniqueId());
        GamerManager.getGamers().remove(player.getUniqueId());
        BlockBreak.to_break.remove(player.getName());
        BattlePassMenu.data.remove(player.getName());
    }

    private static void quitInPvp(Gamer gamer) {
        if (!Gamer.isEnabledCombatRelog()) return;
        if (!Prison.isDisabling && gamer.isInPvp()) {
            gamer.getPlayer().damage(100D);
            gamer.getPlayer().teleport(Prison.SPAWN);
        }
    }

    private void removeBar(Player player) {
        Prison.MoneyBar.removePlayer(player);
        Prison.BlocksBar.removePlayer(player);
    }
}
