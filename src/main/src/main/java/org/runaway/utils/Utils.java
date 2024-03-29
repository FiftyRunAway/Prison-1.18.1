package org.runaway.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.boosters.Booster;
import org.runaway.donate.features.BossMoney;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.TypeMessage;
import org.runaway.managers.GamerManager;
import org.runaway.trainer.Trainer;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class Utils {

    //Статистика

    //Locations
    private static HashMap<String, Boolean> gladiator = new HashMap<>();
    private static HashMap<String, Boolean> vault = new HashMap<>();
    private static HashMap<String, Boolean> ice = new HashMap<>();

    // Battle pass
    private static HashMap<String, Integer> bpScores = new HashMap<>();
    private static HashMap<String, Integer> bpLevel = new HashMap<>();

    private static HashMap<String, String> auth_code = new HashMap<>();

    //Квесты
    private static HashMap<String, String> dailyquests = new HashMap<>();
    private static HashMap<String, String> dailystart = new HashMap<>();

    //бустеры локальные
    private static HashMap<String, Double> lBlocksMultiplier = new  HashMap<>();
    private static HashMap<String, String> lBlocksTime = new  HashMap<>();
    private static HashMap<String, Long> lBlocksRealTime = new  HashMap<>();
    private static HashMap<String, Long> lBlocksActivatingTime = new  HashMap<>();

    private static HashMap<String, Double> lMoneyMultiplier = new  HashMap<>();
    private static HashMap<String, String> lMoneyTime = new  HashMap<>();
    private static HashMap<String, Long> lMoneyRealTime = new  HashMap<>();
    private static HashMap<String, Long> lMoneyActivatingTime = new  HashMap<>();

    //Тренер
    private static HashMap<String, Integer> cashback = new HashMap<>();
    private static HashMap<String, Integer> upgrade = new HashMap<>();
    private static HashMap<String, Integer> luck = new HashMap<>();
    private static HashMap<String, Integer> gym = new HashMap<>();

    private static ArrayList<String> players = new ArrayList<>();

    //Донат меню
    public static List donate = new LinkedList<>();

    //Тренер
    public static List<Trainer> trainer = new LinkedList<>();

    //Списки, связанные с подарками
    private static HashMap<String, ItemStack> gift = new HashMap<>();
    private static HashMap<String, String> gift_owners = new HashMap<>();

    public static String getMessage(EMessage message) {
        return colored(message.getMessage());
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Utils.getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack getColoredArmor(Material leatherArmor, Color color) {
        ItemStack armor = new ItemStack(leatherArmor);
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setColor(color);
        armor.setItemMeta(meta);
        return armor;
    }

    public static boolean depositOffline(String name, double value) {
        EStat.MONEY.setInConfig(name, (double)EStat.MONEY.getFromConfig(name) + value);
        return true;
    }

    public static String generateUID() {
        return UUID.randomUUID().toString().substring(0, 13);
    }

    public static Location getLocation(String name) {
        String l = EConfig.CONFIG.getConfig().getString("locations." + name);
        return unserializeLocation(l);
    }

    public static Location unserializeLocation(String l) {
        String[] var = l.split(" ");
        return new Location(Bukkit.getWorld(var[3]), Double.parseDouble(var[0]), Double.parseDouble(var[1]), Double.parseDouble(var[2]));
    }

    public static String serializeLocation(Location l) {
        return l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getWorld().getName();
    }

    public static boolean getRandom(double chance) {
        return Math.random() < chance;
    }

    public static HashMap<String, Long> getlBlocksActivatingTime() {
        return lBlocksActivatingTime;
    }

    public static HashMap<String, Long> getlMoneyActivatingTime() {
        return lMoneyActivatingTime;
    }

    public void RegisterEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Prison.getInstance());
    }

    public static HashMap<String, Double> calculatePercents(Map<String, Integer> attackers, double damage) {
        HashMap<String, Double> percents = new HashMap<>();
        for (Map.Entry<String, Integer> entry : attackers.entrySet()) {
            double to_add = 1;
            if (Utils.getPlayers().contains(entry.getKey())) {
                Gamer gamer = GamerManager.getGamer(Bukkit.getPlayer(entry.getKey()).getUniqueId());
                Object obj = gamer.getPrivilege().getValue(new BossMoney());
                if (obj != null) to_add += (double) Integer.parseInt(obj.toString()) / 100;
            }
            percents.put(entry.getKey(), (entry.getValue() / damage) * to_add);
        }
        return percents;
    }

    public static List<String> color(List<String> strings) {
        if (strings == null) {
            return strings;
        }
        return strings.stream().map(Utils::colored).collect(Collectors.toList());
    }

    public static String[] color(String[] strings) {
        if (strings == null) {
            return strings;
        }
        return color(Arrays.asList(strings)).toArray(new String[strings.length]);
    }

    public static String colored(String format) {
        if (format == null) {
            return "";
        }
        if (format.contains("&")) {
            return ChatColor.translateAlternateColorCodes('&', format);
        }
        return format;
    }

    public static void sendClickableMessage(Gamer gamer, String message, String command) {
        sendClickableMessage(gamer, message, command, null);
    }

    public static void sendClickableMessage(Gamer gamer, String message, String command, String hover) {
        Text.send(new Text.TextBuilder().message(message)
                .suffix("&8[&7ЛКМ&8]")
                .hover(hover)
                .action(ClickEvent.Action.RUN_COMMAND)
                .eventValue(command).build(), gamer);
    }

    public static void sendSiteMessage(Gamer gamer) {
        org.runaway.utils.Text.send(new org.runaway.utils.Text.TextBuilder()
                .message("&bНажмите здесь, чтобы открыть сайт!")
                .action(ClickEvent.Action.OPEN_URL)
                .eventValue(Vars.getSite())
                .hover("&eСайт проекта &7• &b" + Vars.getSite())
                .build(), gamer);
    }

    public static void DisableKick() {
        try {
            if (!Utils.getPlayers().isEmpty()) {
                Utils.getPlayers().forEach(player -> {
                    GamerManager.getGamer(player).savePlayer();
                    Bukkit.getPlayer(player).kickPlayer(ChatColor.RED + "Запланированная перезагрузка сервера");
                });
                Vars.sendSystemMessage(TypeMessage.SUCCESS, "All players were kicked from the server!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void globalBoostersSetter(String booster, Booster boost) {
        String path = "global." + booster;
        String path2 = ".information.";
        EConfig.LOG.getConfig().set(path + ".activity", true);
        EConfig.LOG.getConfig().set(path + path2 + "owner", boost.getOwner());
        EConfig.LOG.getConfig().set(path + path2 + "time", boost.getTime());
        EConfig.LOG.getConfig().set(path + path2 + "multiplier", boost.getMultiplier());

        EConfig.LOG.saveConfig();
    }

    public static void localBoostersSetter(String name, String boost, long time, double mult, long start) {
        String path = "local." + name + "." + boost + ".";
        EConfig.LOG.getConfig().set(path + "time", time);
        EConfig.LOG.getConfig().set(path + "multiplier", mult);
        EConfig.LOG.getConfig().set(path + "activated", start);

        EConfig.LOG.saveConfig();
    }

    public static String upCurLetter(String word, int LetterNumber) {
        String t = word.toLowerCase();
        String[] var = t.split("");
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < t.length(); i++) {
            if (i == (LetterNumber - 1)) {
                ret.append(var[i].toUpperCase());
            } else {
                ret.append(var[i]);
            }
        }
        return ret.toString();
    }

    public static String formatTime(long time) {
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;
        if (hours > 0) {
            if (minutes > 9) {
                if (seconds > 9) {
                    return hours + ":" + minutes + ":" + seconds;
                } else {
                    return hours + ":" + minutes + ":0" + seconds;
                }
            } else {
                if (seconds > 9) {
                    return hours + ":0" + minutes + ":" + seconds;
                } else {
                    return hours + ":0" + minutes + ":0" + seconds;
                }
            }
        } else if (minutes > 0) {
            if (seconds > 9) {
                return minutes + ":" + seconds;
            } else {
                return minutes + ":0" + seconds;
            }
        }
        return time + " сек";
    }

    public static String FormatMoney(Object balance) {
        if (balance instanceof Integer) {
            return balance + "";
        } else if (balance instanceof String) {
            return "&cСЛОМАЛОСЬ:(";
        } else {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatSymbols.setGroupingSeparator(' ');
            DecimalFormat decimalFormat = new DecimalFormat("#,##0", decimalFormatSymbols);
            String r = decimalFormat.format(balance);
            return r;
        }
    }

    public static String combine(List<String> args, int firstIndex) {
        return String.join(" ", args.stream().skip(firstIndex).collect(Collectors.toList()));
    }

    public static String combine(String[] args, int firstIndex) {
        return String.join(" ", Arrays.stream(args).skip(firstIndex).collect(Collectors.toList()));
    }

    public static List<String> fromStringToList(String string) {
        if (string == null || string.equals("")) {
            return new ArrayList();
        }
        return Splitter.on(",").splitToList(string);
    }

    public static String fromListToString(List list) {
        if (list.isEmpty()) {
            return "";
        }
        return Joiner.on(",").join(list);
    }

    public static Map fromStringToMap(String string) {
        return fromStringToMap(string, ",", ";");
    }

    public static String fromMapToString(Map map) {
        return fromMapToString(map, ",", ";");
    }

    public static Map<String, Object> fromStringToMap(String string, String on, String separator) {
        if (string == null || string.equals("")) {
            return new HashMap();
        }
        return new HashMap<>(Splitter.on(on).withKeyValueSeparator(separator).split(string));
    }

    public static String fromMapToString(Map map, String on, String separator) {
        if (map.isEmpty()) {
            return "";
        }
        return Joiner.on(on).withKeyValueSeparator(separator).join(map);
    }

    public static HashMap<String, Double> getlBlocksMultiplier() {
        return lBlocksMultiplier;
    }

    public static HashMap<String, String> getlBlocksTime() {
        return lBlocksTime;
    }

    public static HashMap<String, Double> getlMoneyMultiplier() {
        return lMoneyMultiplier;
    }

    public static HashMap<String, String> getlMoneyTime() {
        return lMoneyTime;
    }

    public static ArrayList<String> getPlayers() {
        return players;
    }

    public static Map<String, ItemStack> getGifts() { return gift; }

    public static Map<String, String> getGifters() { return gift_owners; }


    public static HashMap<String, Boolean> getVault() {
        return vault;
    }


    public static HashMap<String, Integer> getCashback() {
        return cashback;
    }

    public static HashMap<String, Integer> getUpgrade() {
        return upgrade;
    }

    public static HashMap<String, Integer> getLuck() {
        return luck;
    }

    public static HashMap<String, Integer> getGym() {
        return gym;
    }


    public static HashMap<String, Integer> getBattlePassScores() {
        return bpScores;
    }

    public static HashMap<String, Integer> getBattlePassLevel() {
        return bpLevel;
    }

    public static HashMap<String, String> getDailyQuests() {
        return dailyquests;
    }

    public static HashMap<String, String> getDailyStart() {
        return dailystart;
    }

    public static HashMap<String, String> getAuthCode() {
        return auth_code;
    }

    public static HashMap<String, Long> getlBlocksRealTime() {
        return lBlocksRealTime;
    }

    public static HashMap<String, Long> getlMoneyRealTime() {
        return lMoneyRealTime;
    }

    public static HashMap<String, Boolean> getGladiator() {
        return gladiator;
    }

    public static HashMap<String, Boolean> getIce() {
        return ice;
    }
}
