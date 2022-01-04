package org.runaway.battlepass;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.battlepass.missions.EMissions;
import org.runaway.battlepass.rewards.ERewards;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.TypeMessage;
import org.runaway.inventories.BattlePassMenu;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BattlePass {

    public static List<WeeklyMission> missions = new ArrayList<>();
    private static final ArrayList<IReward> rewards = new ArrayList<>();
    public static final HashMap<Integer, ArrayList<IReward>> level_rewards = new HashMap<>();
    public static Map<IReward, Integer> slots = new HashMap<>();
    public static List<Integer> glass_slots = new ArrayList<>();
    public static int season;

    public static int level = 80000;

    public final static String data_format = "dd/MM/yyyy";
    private final static int MILLISEC_IN_HOUR = 3600000;
    private final static int openingIn = 9;

    static void checkLevelUp(Gamer gamer) {
        if (gamer.getIntStatistics(EStat.BATTLEPASS_SCORE) / level >= 1
                && (int)gamer.getStatistics(EStat.BATTLEPASS_LEVEL) != getMaxLevelBattlePass()) {
            levelUp(gamer);
        }
    }

    static int getMaxLevelBattlePass() {
        return level_rewards.size();
    }

    private static void levelUp(Gamer gamer) {
        Player player = gamer.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);

        gamer.sendTitle("&bБоевой пропуск", "&eУровень повышен!");

        gamer.increaseIntStatistics(EStat.BATTLEPASS_LEVEL);
        gamer.setStatistics(EStat.BATTLEPASS_SCORE, gamer.getIntStatistics(EStat.BATTLEPASS_SCORE) % level);

        int level = gamer.getIntStatistics(EStat.BATTLEPASS_LEVEL);

        // Getting rewards
        StringBuilder get = new StringBuilder();
        StringBuilder can = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        AtomicInteger j = new AtomicInteger();

        ArrayList<IReward> rews = new ArrayList<>(level_rewards.get(level));

        rews.forEach(reward -> {
            if (reward.isFree() || gamer.hasBattlePass()) {
                get.append(reward.getName());
                if (!reward.isStringValue() && reward.getValue() > 0) get.append(" &7(&bx").append(reward.getValue()).append("&7)");
                if (reward.isFree()) get.append(" &7[&eБесплатная награда&7]");
                get.append(", ");
                i.incrementAndGet();
                reward.get(gamer);
            } else {
                can.append(reward.getName());
                if (!reward.isStringValue() && reward.getValue() > 0) get.append(" &7(&bx").append(reward.getValue()).append("&7)");
                can.append(", ");
                j.incrementAndGet();
            }
        });
        if (i.get() > 0) {
            get.delete(get.length() - 2, get.length());
            gamer.sendMessage(Utils.colored(EMessage.BPREWARDGET.getMessage().replace("%reward%",
                    get.append('.').toString())));
        }
        if (j.get() > 0) {
            can.delete(can.length() - 2, can.length());
            gamer.sendMessage(Utils.colored(EMessage.BPREWARDCAN.getMessage().replace("%reward%",
                    can.append('.').toString())));
        }
    }

    public static void load() {
        // Load season number
        season = EConfig.BATTLEPASS.getConfig().getInt("season");
        // Load missions of season
        EConfig.BATTLEPASS.getConfig().getConfigurationSection("missions").getKeys(false).forEach(s -> {
            ConfigurationSection section = EConfig.BATTLEPASS.getConfig().getConfigurationSection("missions." + s);
            ArrayList<IMission> list = new ArrayList<>();

            //Loading data
            Date date = null;
            try {
                date = new SimpleDateFormat(data_format).parse(section.getString("date"));
                date.setTime(date.getTime() + (MILLISEC_IN_HOUR * openingIn));
            } catch (ParseException e) {
                Vars.sendSystemMessage(TypeMessage.ERROR, "Invalid data format. Use: " + data_format);
                e.printStackTrace();
            }

            section.getStringList("missions").forEach(mission -> {
                String[] splitter = mission.split(":");
                StringBuilder sb = new StringBuilder();
                Arrays.stream(EMissions.values()).forEach(mis -> {
                    if (mis.toString().toLowerCase().equals(splitter[0])) sb.append(mis.getMissionClass().getName());
                });
                IMission m = null;
                try {
                    m = (IMission) Class.forName(sb.toString()).getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                assert m != null;
                String[] spl = splitter[1].split(" ");
                ArrayList<Object> objects = new ArrayList<>(Arrays.asList(spl).subList(0, m.getLenghtArguments()));

                m.setDescriptionDetails(objects.toArray());
                m.init();

                EConfig.BATTLEPASS_DATA.getConfig().set(m.hashCode() + ".none", 0);
                list.add(m);
            });

            missions.add(new WeeklyMission(section.getString("name"), list, date));
        });
        EConfig.BATTLEPASS_DATA.saveConfig();
        // Load rewards
        EConfig.BATTLEPASS.getConfig().getConfigurationSection("levels").getKeys(false).forEach(s -> {
            int level = Integer.parseInt(s);

            List<String> list = EConfig.BATTLEPASS.getConfig().getStringList("levels." + s + ".rewards");

            ArrayList<IReward> rs = new ArrayList<>();
            list.forEach(str -> {
                String[] splitter = str.split(":");

                StringBuilder sb = new StringBuilder();
                Arrays.stream(ERewards.values()).forEach(r -> {
                    if (r.toString().toLowerCase().equals(splitter[1].split(" ")[0])) sb.append(r.getRewardClass().getName());
                });

                IReward reward = null;
                try {
                    reward = (IReward) Class.forName(sb.toString()).getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                assert reward != null;

                String[] spl = splitter[1].split(" ");
                ArrayList<Object> objects = new ArrayList<>(Arrays.asList(spl).subList(1, reward.getLengthArgumentString() + 1));

                reward.setDetails(objects.toArray());
                reward.setLevel(level);
                reward.setFree(splitter[0].equals("free"));

                reward.init();

                rs.add(reward);
                BattlePass.rewards.add(reward);
            });
            level_rewards.put(level, rs);
        });

        // Load slots
        AtomicInteger pos = new AtomicInteger(10);
        AtomicInteger startPage = new AtomicInteger(9);
        level_rewards.forEach((level, rewards) -> {
            AtomicBoolean al = new AtomicBoolean(false);
            glass_slots.add(pos.get());
            rewards.forEach(reward -> {
                int slot;
                if (reward.isFree()) {
                    slot = pos.get() - 9;
                } else {
                    if (!al.get()) {
                        slot = pos.get() + 9;
                        al.set(true);
                    } else {
                        slot = pos.get() + 18;
                    }
                }
                BattlePass.slots.put(reward, slot);
            });
            if (pos.getAndIncrement() - startPage.get() == 8) {
                pos.addAndGet(45);
                startPage.set(pos.get());
            }
        });
        BattlePassMenu.load();
    }

    public static List<IMission> getPinnedTasks(Gamer gamer) {
        if (getPins(gamer) == 0) return Collections.emptyList();
        ArrayList<Integer> pins = new ArrayList<>();
        Arrays.stream(EConfig.BATTLEPASS_DATA.getConfig().getString(gamer.getGamer()).split(" "))
                .forEach(s -> pins.add(Integer.parseInt(s)));
        ArrayList<IMission> missions = new ArrayList<>();
        BattlePass.missions.forEach(wm -> wm.getMissions().forEach(mission -> {
            if (wm.isStarted() && pins.contains(mission.hashCode())) missions.add(mission);
        }));
        return missions;
    }

    public static void unPin(IMission mission, Gamer g) {
        unPin(String.valueOf(mission.hashCode()), g);
        g.getPins().remove((Object) mission);
    }

    public static void unPin(String hashCode, Gamer g) {
        Player player = g.getPlayer();
        ArrayList<String> in = new ArrayList<>(Arrays.asList(EConfig.BATTLEPASS_DATA.getConfig().getString(player.getName()).split(" ")));
        EConfig.BATTLEPASS_DATA.getConfig().set(player.getName(), null);
        for (int i = 0; i < in.size(); i++) {
            if (in.get(i).contains(String.valueOf(hashCode))) in.remove(i);
        }
        String str = in.toString().replace("[", "").replace("]", "").replace(",", "");
        if (str.length() > 1) {
            EConfig.BATTLEPASS_DATA.getConfig().set(g.getGamer(), str);
        } else {
            EConfig.BATTLEPASS_DATA.getConfig().set(g.getGamer(), null);
        }
        EConfig.BATTLEPASS_DATA.saveConfig();
        g.sendMessage(EMessage.UNPIN);
    }

    public static void addPin(IMission mission, Gamer g) {
        int pins = BattlePass.getPins(g);
        if (pins < 7) {
            List<IMission> pinned = BattlePass.getPinnedTasks(g);
            if (!pinned.isEmpty() && pinned.contains(mission)) {
                g.sendMessage(EMessage.ALREADYPINNED);
                return;
            }
            if (pins == 0) {
                EConfig.BATTLEPASS_DATA.getConfig().set(g.getGamer(), mission.hashCode());
            } else {
                EConfig.BATTLEPASS_DATA.getConfig().set(g.getGamer(), EConfig.BATTLEPASS_DATA.getConfig().get(g.getGamer()) + " " + mission.hashCode());
            }
            EConfig.BATTLEPASS_DATA.saveConfig();
            g.sendMessage(Utils.colored(EMessage.SETPIN.getMessage().replace("%name%", mission.getName())));
            g.getPins().add(mission);
        } else {
            g.sendMessage(EMessage.MANYPINS);
        }
    }

    public static int getPins(Gamer gamer) {
        if (!EConfig.BATTLEPASS_DATA.getConfig().contains(gamer.getGamer())) return 0;
        return EConfig.BATTLEPASS_DATA.getConfig().getString(gamer.getGamer()).split(" ").length;
    }
}
