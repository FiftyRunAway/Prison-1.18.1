package org.runaway.board;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.battlepass.BattlePass;
import org.runaway.enums.*;
import org.runaway.managers.GamerManager;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;
import org.runaway.utils.color.ColorAPI;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public class Board {

    private static boolean emojisEnabled = false;

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
        replaceScore(objective, 15, invise(player) + ChatColor.GRAY + date());
        replaceScore(objective, 14, invise(player) + " ");
        replaceScore(objective, 13, invise(player) + "&eСтатистика:");
        replaceScore(objective, 12, invise(player) + "&f Уровень" + getSplitter() + "&a&l" + gamer.getDisplayLevel());
        replaceScore(objective, 11, invise(player) + "&f Баланс" + getSplitter() + "&a&l" + FormatMoney(gamer.getStatistics(EStat.MONEY)));
        replaceScore(objective, 10, invise(player) + "&f Блоков " + getSplitter() + "&6&l" + FormatBlocks(gamer));
        replaceScore(objective, 9,  invise(player) + "&f Убийств" + getSplitter() + "&c&l" +
                gamer.getStatistics(EStat.KILLS) + Utils.colored(gamer.isInPvp() ? " &7[&c" + gamer.getInPvpLeft() + " сек&7]" : ""));
        replaceScore(objective, 8, invise(player) + "&f Крыс убито" + getSplitter() + "&c&l" + gamer.getMobKills("rat"));
        replaceScore(objective, 7, invise(player) + "&f Ключей" + getSplitter() + "&c&l" + gamer.getStatistics(EStat.KEYS));
        replaceScore(objective, 6, invise(player) + "&f Фракция" + getSplitter() + "&c&l" + (gamer.getFaction().getColor() + gamer.getFaction().getName()));
        replaceScore(objective, 5, invise(player) + "  ");
        replaceScore(objective, 4, invise(player) + "&eСервер:");
        replaceScore(objective, 3, invise(player) + "&f Игроков" + getSplitter() + "&a&l" + Utils.getPlayers().size()
                + (gamer.isHideEnabled() ? " &7[Скрыты]" : ""));
        replaceScore(objective, 2, invise(player) + "   ");
        replaceScore(objective, 1, invise(player) + "&f&l" + Vars.getSite());

        if(objective.getDisplaySlot() != DisplaySlot.SIDEBAR) objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(score);
    }

    private static String invise(Player player) {
        return emojisEnabled ? new FontImageWrapper("%img_offset_-500%").getString() : "";
    }

    private static String getSplitter() {
        return ChatColor.GRAY + "" + ChatColor.BOLD + " • ";
    }

    public static List<String> getScoreBoardUpdate() {
        return ScoreBoardUpdate;
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

    private static String updateDisplayName(String displayName) {
        return ColorAPI.process("<RAINBOW1>Тюрьма</RAINBOW>");
        /*
        if (displayName != null && displayName.length() >= 1) {
            int i = 0;
            while (i < ScoreBoardUpdate.size()) {
                final String s = ScoreBoardUpdate.get(i);
                if (!s.equals(displayName)) {
                    i++;
                } else {
                    if (i == ScoreBoardUpdate.size() - 1) {
                        return ScoreBoardUpdate.get(0);
                    }
                    return ScoreBoardUpdate.get(i + 1);
                }
            }
            return ScoreBoardUpdate.get(0);
        }
        return ScoreBoardUpdate.get(0);*/
    }

    private static void titleUpdate() {
        new SyncRepeatTask(() -> {
            Utils.getPlayers().forEach(player -> {
                Player p = Bukkit.getPlayer(player);
                if (p == null) return;
                Scoreboard scoreboard = p.getScoreboard();
                if (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null) return;
                if (Prison.event != null) {
                    scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(updateDisplayName(scoreboard.getObjective(DisplaySlot.SIDEBAR).getDisplayName()
                            .replace(Utils.colored(" &7| " + Prison.event), "")) + Utils.colored(" &7| " + Prison.event));
                } else {
                    scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(
                            ColorAPI.process(GamerManager.getGamer(p).getBoardName()));
                }
            });
        }, 4, 40);
    }

    public static void loadBoard() {
        try {
            titleUpdate();
            Prison.event = EConfig.SHOP.getConfig().getString("event");
            dateFormat = new SimpleDateFormat("HH:mm МСК | dd.MM.yy");
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatSymbols.setGroupingSeparator(' ');
            ScoreBoardUpdate.addAll(EConfig.CONFIG.getConfig().getStringList("DisplayName"));
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Prison.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(Board::sendBoard), 0, 10);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading scoreboard!");
            Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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
            if(getEntryFromScore(o, score).equalsIgnoreCase(Utils.colored(name))) return;
            if(!(getEntryFromScore(o, score).equalsIgnoreCase(Utils.colored(name))))
                o.getScoreboard().resetScores(getEntryFromScore(o, score));
        }
        o.getScore(Utils.colored(name)).setScore(score);
    }
}
