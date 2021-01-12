package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.battlepass.BattlePass;
import org.runaway.board.Board;
import org.runaway.commands.SetStatCommand;
import org.runaway.enums.*;
import org.runaway.managers.GamerManager;
import org.runaway.needs.Needs;
import org.runaway.passiveperks.PassivePerks;
import org.runaway.sqlite.DoReturn;
import org.runaway.sqlite.DoVoid;
import org.runaway.sqlite.PreparedRequests;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

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
            CreateInConfig(event); // Add in config file
            Utils.getPlayers().add(player.getName());
            Gamer gamer = GamerManager.getGamer(player);
            Board.sendBoard(player); // Set up scoreboard to player
            gamer.setLevelBar();
            gamer.setExpProgress();
            addBar(player); // Add boss bar with boosters
            addPaper(player); // Add menu paper item
            addToMissions(gamer); // Add to missions of battle pass
            if (Main.useNametagEdit) gamer.setNametag(); // Add nametag
            Needs.onJoin(event);
            PassivePerks.onJoin(gamer);
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

    private void addToMissions(Gamer gamer) {
        BattlePass.missions.forEach(weeklyMission -> weeklyMission.getMissions().forEach(mission -> {
            if (mission.isCompletedOffline(gamer)) return;
            ConfigurationSection section = EConfig.BATTLEPASS_DATA.getConfig().getConfigurationSection(String.valueOf(mission.hashCode()));
            if (section.contains(gamer.getGamer())) {
                mission.getValues().put(gamer.getGamer(), section.getInt(gamer.getGamer()));
            } else {
                mission.getValues().put(gamer.getGamer(), 0);
            }
        }));
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
        Gamer gamer = GamerManager.getGamer(player);
        if (Main.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            try {
                PreparedStatement ps = Main.getMainDatabase().getSQLConnection().prepareStatement("SELECT * FROM " + Main.getInstance().stat_table + " WHERE player = ?");
                ps.setString(1, player.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Arrays.stream(EStat.values()).forEach(eStat -> {
                            try {
                                addInMap(eStat, gamer , rs.getObject(eStat.getStatName()));
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }
                        });
                    }
                }
            } catch (SQLException ex) {
                try {
                    StringBuilder sb = new StringBuilder("mode");
                    ArrayList<Object> objs = new ArrayList<>();
                    Arrays.stream(EStat.values()).forEach(eStat -> {
                        if (eStat.equals(EStat.MODE)) return;
                        sb.append(", ").append(eStat.getStatName());
                        objs.add(eStat.getDefualt());

                        System.out.println("added " + eStat.getStatName());
                        eStat.getMap().put(player.getName(), eStat.getDefualt());
                    });
                    PreparedStatement ps = Main.getMainDatabase().getSQLConnection().prepareStatement("INSERT INTO " +
                            Main.getInstance().stat_table + " (player, " + sb.toString() +
                            ") VALUES (" + player.getName() + "," + objs.toString().replace("[", "").replace("]", "") + ")");
                    ps.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

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
        addInMap(stat, player, stat.getFromConfig(player.getGamer()));
    }

    private void addInMap(EStat stat, Gamer player, Object value) {
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
        if (player.isOp() || Main.getInstance().getStatus().equals(ServerStatus.NORMAL)) return true;
        ServerStatus now = Main.getInstance().getStatus();
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
