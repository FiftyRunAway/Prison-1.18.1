package org.runaway.battlepass;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.battlepass.missions.EMissions;
import org.runaway.battlepass.rewards.ERewards;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.inventories.BattlePassMenu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BattlePass {

    public static ArrayList<WeeklyMission> missions = new ArrayList<>();
    private static ArrayList<IReward> rewards = new ArrayList<>();
    private static HashMap<Integer, ArrayList<IReward>> level_rewards = new HashMap<>();
    public static HashMap<IReward, Integer> slots = new HashMap<>();
    public static ArrayList<Integer> glass_slots = new ArrayList<>();
    public static int season;

    public static int level = 80000;

    static void checkLevelUp(Gamer gamer) {
        if ((int)gamer.getStatistics(EStat.BATTLEPASS_SCORE) / level >= 1) {
            levelUp(gamer);
        }
    }

    private static void levelUp(Gamer gamer) {
        Player player = gamer.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 10, 1);

        gamer.sendTitle("&eБоевой пропуск", "&eПовышен");

        gamer.setStatistics(EStat.BATTLEPASS_LEVEL, (int)gamer.getStatistics(EStat.BATTLEPASS_LEVEL) + 1);
        gamer.setStatistics(EStat.BATTLEPASS_SCORE, (int)gamer.getStatistics(EStat.BATTLEPASS_SCORE) % level);

        level_rewards.get((int)gamer.getStatistics(EStat.BATTLEPASS_LEVEL)).forEach(reward -> reward.get(gamer));
    }

    public static void load() {
        // Load season number
        season = EConfig.BATTLEPASS.getConfig().getInt("season");
        // Load missions of season
        EConfig.BATTLEPASS.getConfig().getConfigurationSection("missions").getKeys(false).forEach(s -> {
            ConfigurationSection section = EConfig.BATTLEPASS.getConfig().getConfigurationSection("missions." + s);
            ArrayList<IMission> list = new ArrayList<>();

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

                EConfig.BATTLEPASS_DATA.getConfig().set(m.getHashCode() + ".none", 0);
                list.add(m);
            });

            missions.add(new WeeklyMission(section.getString("name"), list));
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

                if (splitter[0].equals("free")) {
                    reward.setFree(true);
                } else reward.setFree(false);

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
                Bukkit.getConsoleSender().sendMessage(level + " - " + slot);
                BattlePass.slots.put(reward, slot);
            });
            if (pos.getAndIncrement() - startPage.get() == 8) {
                pos.addAndGet(45);
                startPage.set(pos.get());
            }
        });
        BattlePassMenu.load();
    }
}
