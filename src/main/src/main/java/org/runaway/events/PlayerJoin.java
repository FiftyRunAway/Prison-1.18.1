package org.runaway.events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.commands.HideCommand;
import org.runaway.huds.CustomHUD;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.achievements.Achievement;
import org.runaway.battlepass.BattlePass;
import org.runaway.board.Board;
import org.runaway.enums.*;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.managers.GamerManager;
import org.runaway.nametag.Teams;
import org.runaway.needs.Needs;
import org.runaway.passiveperks.PassivePerks;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        if (isAccess(event)) {
            GamerManager.createGamer(player); // Add Gamer class to player
            Utils.getPlayers().add(player.getName());
            Gamer gamer = GamerManager.getGamer(player);
            Board.sendBoard(player); // Set up scoreboard to player
            gamer.setLevelBar();
            gamer.setExpProgress();
            HideCommand.hideOnJoin(gamer);
            addBar(player); // Add boss bar with boosters
            addPaper(player); // Add menu paper item
            if (Prison.useNametagEdit) gamer.setNametag(); // Add nametag
            Needs.onJoin(event);
            PassivePerks.onJoin(gamer);
            Teams.load(player);

            if (!player.hasPlayedBefore()) {
                Achievement.JOIN.get(player);
                startKit(event.getPlayer());
                player.teleport(Prison.SPAWN);
            } else {
                if (Utils.getPlayers().size() == 1) Achievement.EMPTY_SERVER.get(player);
            }
        }
    }

    private void addBar(Player player) {
        Prison.MoneyBar.addPlayer(player);
        Prison.BlocksBar.addPlayer(player);
    }

    private void addPaper(Player player) {
        Gamer gamer = GamerManager.getGamer(player);
        if(gamer.getAmount(ItemManager.getPrisonItem("menu"), false) == 0) {
            gamer.addItem("menu");
        }
    }

    private void startKit(Player player) {
        Gamer gamer = GamerManager.getGamer(player);

        gamer.addItem("waxe0_1");
        gamer.addItem("steak", 8);
    }

    private boolean isAccess(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || Prison.getInstance().getStatus().equals(ServerStatus.NORMAL)) return true;
        ServerStatus now = Prison.getInstance().getStatus();
        if (ServerStatus.PREVENTIVEWORKS.equals(now)) {
            player.kickPlayer(Utils.colored("&cСервер в данный момент находится на профилактических работах." +
                    "\n&cЗайдите позже..."));
            return false;
        } else if (ServerStatus.ZBT.equals(now)) {
            if (EStat.ZBT.getFromConfig(player.getName()).equals(false)) {
                player.kickPlayer(Utils.colored("&cСервер находится на стадии ЗБТ!" +
                        "\n&cПриобрести доступ можно на: &4" + Vars.getSite()));
                return false;
            }
        } else if (ServerStatus.WIPE.equals(now)) {
            player.kickPlayer(Utils.colored("&cНа сервере происходит процесс удаления данных..." +
                    "\n&cСервер будет доступен в ближайшее время!"));
            return false;
        } else if (ServerStatus.HACKED.equals(now)) {
            player.kickPlayer(Utils.colored("&cБыла попытка взлома сервера!" +
                    "\n&cСервер восстановится в ближайшее время!"));
            return false;
        } else if (ServerStatus.ERROR.equals(now)) {
            player.kickPlayer(Utils.colored("&cНа сервере возникла проблема с плагином." +
                    "\n&cНапишите админимтрации сервера об этой ошибке!"));
            return false;
        }
        return true;
    }
}
