package org.runaway.board;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.enums.*;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public class Board {

    private static ArrayList<Player> initialized = new ArrayList<>();
    private static List<String> ScoreBoardUpdate = new ArrayList<>();

    private static DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    private static SimpleDateFormat dateFormat;

    public static void sendBoard(Player player) {
        if (initialized.contains(player)) return;
        Scoreboard sb = player.getScoreboard();
        if(sb.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) player.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
        Scoreboard score = player.getScoreboard();
        Objective objective = score.getObjective(player.getName()) == null ? score.registerNewObjective(player.getName(), "dummy") : score.getObjective(player.getName());
        String oldname = "";
        if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) oldname = sb.getObjective(DisplaySlot.SIDEBAR).getDisplayName();
        objective.setDisplayName(oldname);
        Gamer gamer = GamerManager.getGamer(player);
        replaceScore(objective, 15, ChatColor.GRAY + date());
        replaceScore(objective, 14, " ");
        replaceScore(objective, 13, ChatColor.YELLOW + "Статистика:");
        replaceScore(objective, 12, ChatColor.WHITE + " Уровень" + getSplitter(ChatColor.GREEN) + ChatColor.BOLD + gamer.getDisplayLevel() + (gamer.getDisplayRebirth().equals("") ? "" : ChatColor.DARK_GRAY + " [" + ChatColor.YELLOW + gamer.getDisplayRebirth() + ChatColor.DARK_GRAY + "]"));
        replaceScore(objective, 11, ChatColor.WHITE + " Баланс" + getSplitter(ChatColor.GREEN) + ChatColor.BOLD + FormatMoney(gamer.getStatistics(EStat.MONEY)));
        replaceScore(objective, 10, ChatColor.WHITE + " Блоков " + getSplitter(ChatColor.GOLD) + ChatColor.BOLD + FormatBlocks(gamer));
        replaceScore(objective, 9, ChatColor.WHITE + " Убийств" + getSplitter(ChatColor.RED) + ChatColor.BOLD + gamer.getStatistics(EStat.KILLS));
        replaceScore(objective, 8, ChatColor.WHITE + " Крыс убито" + getSplitter(ChatColor.RED) + ChatColor.BOLD + gamer.getMobKills("rat"));
        replaceScore(objective, 7, ChatColor.WHITE + " Ключей добыто" + getSplitter(ChatColor.RED) + ChatColor.BOLD + gamer.getStatistics(EStat.KEYS));
        replaceScore(objective, 6, ChatColor.WHITE + " Фракция" + getSplitter(ChatColor.RED) + ChatColor.BOLD + "" + (gamer.getFaction().getColor() + gamer.getFaction().getName()));
        replaceScore(objective, 5, "  ");
        replaceScore(objective, 4, ChatColor.YELLOW + "Сервер:");
        replaceScore(objective, 3, ChatColor.WHITE + " Игроков" + getSplitter(ChatColor.GREEN) + ChatColor.BOLD + Utils.getPlayers().size());
        replaceScore(objective, 2, "   ");
        replaceScore(objective, 1, ChatColor.WHITE + "  " + ChatColor.BOLD + Vars.getSite());

        if(objective.getDisplaySlot() != DisplaySlot.SIDEBAR) objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(score);
    }

    private static String getSplitter(ChatColor before) {
        return ChatColor.GRAY + "" + ChatColor.BOLD + " • " + before;
    }

    public static String FormatBlocks(Gamer gamer) {
        double blocks = gamer.getDoubleStatistics(EStat.BLOCKS);
        if (blocks >= 1000.0 && blocks < 1000000) {
            return Math.round(blocks / 1000.0 * 100.0) / 100.0 + "K";
        }
        if (blocks >= 1000000) {
            return Math.round(blocks / 1000000.0 * 100.0) / 100.0 + "M";
        }
        return String.valueOf(Math.round(gamer.getDoubleStatistics(EStat.BLOCKS)));
    }

    public static String FormatBlocks(String blks) {
        double blocks = Double.parseDouble(blks);
        if (blocks >= 1000.0 && blocks < 1000000) {
            return Math.round(blocks / 1000.0 * 100.0) / 100.0 + "K";
        }
        if (blocks >= 1000000) {
            return Math.round(blocks / 1000000.0 * 100.0) / 100.0 + "M";
        }
        return String.valueOf(Math.round(Float.parseFloat(blks)));
    }

    public static String FormatMoney(Object balance) {
        if (balance instanceof Integer) {
            return balance + " " + MoneyType.RUBLES.getShortName();
        } else if (balance instanceof String) {
            return "&cСЛОМАЛОСЬ:(";
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.0", decimalFormatSymbols);
            String r = decimalFormat.format(balance);
            return r + " " + MoneyType.RUBLES.getShortName();
        }
    }

    private String updateDisplayName(String displayName) {
        if (displayName != null && displayName.length() >= 1) {
            int i = 0;
            while (i < ScoreBoardUpdate.size()) {
                final String s = ScoreBoardUpdate.get(i);
                if (!s.equals(displayName)) {
                    ++i;
                } else {
                    if (i == ScoreBoardUpdate.size() - 1) {
                        return ScoreBoardUpdate.get(0);
                    }
                    return ScoreBoardUpdate.get(i + 1);
                }
            }
            return ScoreBoardUpdate.get(0);
        }
        return ScoreBoardUpdate.get(0);
    }

    private void titleUpdate() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            Utils.getPlayers().forEach(player -> {
                Player p = Bukkit.getPlayer(player);
                Scoreboard scoreboard = p.getScoreboard();
                if (scoreboard != null) {
                    if (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null) return;
                    if (Main.event != null) {
                        scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(updateDisplayName(scoreboard.getObjective(DisplaySlot.SIDEBAR).getDisplayName()
                                .replace(Utils.colored(" &7| " + Main.event), "")) + Utils.colored(" &7| " + Main.event));
                    } else {
                        scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(updateDisplayName(scoreboard.getObjective(DisplaySlot.SIDEBAR).getDisplayName()
                                .replace(Utils.colored(" &7| &c" + /*BattlePass.season +*/ "ОТКРЫТИЕ"), "")) + Utils.colored(" &7| &c" + /*BattlePass.season + */"ОТКРЫТИЕ"));
                    }
                }
            });
        }, 0L, 4L);
    }

    public void loadBoard() {
        try {
            titleUpdate();
            Main.event = EConfig.SHOP.getConfig().getString("event");
            dateFormat = new SimpleDateFormat("HH:mm МСК | dd.MM.yy");
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatSymbols.setGroupingSeparator(' ');
            ScoreBoardUpdate.addAll(EConfig.CONFIG.getConfig().getStringList("DisplayName"));
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(Board::sendBoard), 0, 10);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading scoreboard!");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    private static String date() {
        return dateFormat.format(new Date());
    }

    private static String getEntryFromScore(Objective o, int score) {
        if(o == null) return null;
        if(!hasScoreTaken(o, score)) return null;
        for (String s : o.getScoreboard().getEntries()) {
            if(o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
        }
        return null;
    }

    private static boolean hasScoreTaken(Objective o, int score) {
        for (String s : o.getScoreboard().getEntries()) {
            if(o.getScore(s).getScore() == score) return true;
        }
        return false;
    }

    private static void replaceScore(Objective o, int score, String name) {
        if(hasScoreTaken(o, score)) {
            if(getEntryFromScore(o, score).equalsIgnoreCase(name)) return;
            if(!(getEntryFromScore(o, score).equalsIgnoreCase(name))) o.getScoreboard().resetScores(getEntryFromScore(o, score));
        }
        o.getScore(name).setScore(score);
    }
}
