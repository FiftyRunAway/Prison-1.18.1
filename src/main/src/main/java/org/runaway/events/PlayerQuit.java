package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        gamer.savePlayer();
        //player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        quitInPvp(gamer);
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
            Gamer.leaveCombat(gamer);
            gamer.getCombatLog().forEach(s -> {
                Gamer g = GamerManager.getGamer(s);
                if (g != null) {
                    g.getCombatLog().remove(gamer.getGamer());
                    g.sendMessage("&cИгрок " + gamer.getGamer() + " вышел из игры во время боя с вами и умер!");
                    if (g.getCombatLog().isEmpty()) {
                        Gamer.leaveCombat(g);
                    }
                }
            });
            gamer.getPlayer().damage(gamer.getPlayer().getHealth() + 5);
            gamer.getPlayer().teleport(Prison.SPAWN);
        }
    }

    private void removeBar(Player player) {
        Prison.MoneyBar.removePlayer(player);
        Prison.BlocksBar.removePlayer(player);
    }
}
