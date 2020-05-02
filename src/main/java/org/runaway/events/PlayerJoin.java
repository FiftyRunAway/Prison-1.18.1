package org.runaway.events;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.*;
import org.runaway.achievements.Achievement;
import org.runaway.board.Board;
import org.runaway.commands.SetStatCommand;
import org.runaway.enums.*;
import org.runaway.google.TWOFA;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.Arrays;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        Main.gamers.put(player.getUniqueId(), new Gamer(player.getUniqueId())); // Add Gamer class to player

        CreateInConfig(event); // Add in config file
        Utils.getPlayers().add(player.getName());
        if (isAccess(event)) {

            Gamer gamer = Main.gamers.get(player.getUniqueId());
            Board.sendBoard(player); // Set up scoreboard to player
            gamer.setLevelBar();
            gamer.setExpProgress();
            addBar(player); // Add boss bar with boosters
            addPaper(player); // Add menu paper item
            if (Main.useNametagEdit) gamer.setNametag(); // Add nametag
            //if (TWOFA.authlocked != null) twoFA(gamer); // Google Authenticator
            if (!player.hasPlayedBefore()) {
                Achievement.JOIN.get(player, false); // Achievement
                startKit(event.getPlayer()); // Give a start kit
                gamer.teleport(Main.SPAWN); // Teleport to spawn
                Utils.getPlayers().forEach(p -> Bukkit.getPlayer(p).sendMessage(Utils.colored(EMessage.FIRSTJOINPLAYER.getMessage()).replace("%player%", gamer.getGamer())));
            } else {
                if (Utils.getPlayers().size() == 1) Achievement.EMPTY_SERVER.get(player, false); // Achievement
            }
        }
    }

    private void twoFA(Gamer gamer) {
        Player player = gamer.getPlayer();
        if (gamer.getStatistics(EStat.TWOFA_CODE).equals("default")) {
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            final GoogleAuthenticatorKey key = gAuth.createCredentials();

            player.sendMessage(Utils.colored("&aТвой &bGoogle Auth &aкод: &b" + key.getKey()));
            player.sendMessage(Utils.colored("&7Тебе нужно ввести этот код в приложении &eGoogle Authenticator!"));

            gamer.setStatistics(EStat.TWOFA_CODE, key.getKey());
        } else {
            TWOFA.authlocked.add(player.getUniqueId());
            player.sendMessage(Utils.colored("&aОткройте приложение Google Authenticator и введите шестизначный пароль."));
        }
    }

    private void addBar(Player player) {
        Main.MoneyBar.addPlayer(player);
        Main.BlocksBar.addPlayer(player);
    }

    private void addPaper(Player player) {
        ItemStack is = new Item.Builder(Material.PAPER).name("&aМеню").lore(new Lore.BuilderLore().addSpace().addString("&7>> &bОткрыть").build()).build().item();
        if (!player.getInventory().contains(is)) {
            player.getInventory().addItem(is);
        }
    }

    private void startKit(Player player) {
        player.getInventory().addItem(UpgradeMisc.buildItem("waxe0", false, player, false));
        player.getInventory().addItem(new Item.Builder(Material.COOKED_BEEF).name("&dВкуснейший стейк").amount(8).build().item());
    }

    private void CreateInConfig(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        if (!EConfig.STATISTICS.getConfig().contains(player.getName())) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, player.getName() + " was added in config");
            Arrays.stream(EStat.values()).forEach(stat -> {
                stat.setInConfig(player.getName(), stat.getDefualt());
                stat.getMap().put(player.getName(), stat.getDefualt());
            });
        } else {
            Arrays.stream(EStat.values()).forEach(stat -> {
                if (!EConfig.STATISTICS.getConfig().contains(player.getName() + "." + stat.getStatName())) {
                    stat.setInConfig(player.getName(), stat.getDefualt());
                    stat.getMap().put(player.getName(), stat.getDefualt());
                } else {
                    addInMap(stat, gamer);
                }
            });
        }
    }

    private void addInMap(EStat stat, Gamer player) {
        Object value = EConfig.STATISTICS.getConfig().get(player.getGamer() + "." + stat.getStatName());
        try {
            if (stat.getStatType().equals(StatType.INTEGER)) {
                stat.getMap().put(player.getPlayer().getName(), Integer.parseInt(value.toString()));
            } else if (stat.getStatType().equals(StatType.DOUBLE)) {
                stat.getMap().put(player.getPlayer().getName(), Double.parseDouble(value.toString()));
            } else if (stat.getStatType().equals(StatType.BOOLEAN) && new SetStatCommand().isBoolean(value.toString().toLowerCase())) {
                stat.getMap().put(player.getPlayer().getName(), Boolean.parseBoolean(value.toString()));
            } else if (stat.getStatType().equals(StatType.STRING)) {
                stat.getMap().put(player.getPlayer().getName(), value.toString().toLowerCase());
            }
        } catch (Exception ex) { player.getPlayer().sendMessage(ChatColor.RED + "Error 404"); }
    }

    private boolean isAccess(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) return true;
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        ServerStatus now = Main.getInstance().getStatus();
        if (ServerStatus.PREVENTIVEWORKS.equals(now)) {
            player.kickPlayer(Utils.colored("&cСервер в данный момент находится на профилактических работах." +
                    "\n&cЗайдите позже..."));
            return false;
        } else if (ServerStatus.ZBT.equals(now)) {
            if (gamer.getStatistics(EStat.ZBT).equals(false)) {
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
