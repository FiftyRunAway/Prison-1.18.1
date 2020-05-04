package org.runaway.achievements;

import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Vars;
import org.runaway.enums.BoosterType;
import org.runaway.enums.EConfig;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum Achievement {
    JOIN("&cВы попали в тюрьму", "Приговор вынесен", new Reward[]{
            new MoneyReward().setReward(1) }, false),
    FIVE_LEVEL("&aПолучить 5 уровень","Начинающий зэк", new Reward[]{
            new ItemReward().setReward(new Item.Builder(Material.COOKED_BEEF).name("&dВкуснейший стейк").build().item()) }, true),
    TEN_LEVEL("&aПолучить 10 уровень","Опытный зэк", new Reward[]{
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(4).build().item()),
            new BoosterReward().setReward(BoosterType.MONEY, 2.0, 1800, false),
            new BoosterReward().setReward(BoosterType.BLOCKS, 2.0, 1800, false)}, true),
    FIRST_TREASURE("&aНайти свой первый клад","Сыщик I", new Reward[]{
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(4).build().item()) }, false),
    SPIDER_KILL("&aВы убили матку впервые", "Ведьмак", new Reward[]{
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.4, 900, false),
            new MoneyReward().setReward(50) }, false),
    BLAZE_KILL("&aВы убили огненного стража", "Ведьмак II", new Reward[]{
            new MoneyReward().setReward(75) }, true),
    FIFTEEN_RATS("&aВы убили 15 крыс", "Крысолов I", new Reward[]{
            new MoneyReward().setReward(10),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(10).build().item()) }, false),
    FIFTEEN_ZOMBIES("&aВы убили 15 зомби", "Зомболов I", new Reward[]{
            new MoneyReward().setReward(50),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(20).build().item()) }, false),
    FIRST_TRAINER("&aПрокачаться у тренера", "Хороший ученик", new Reward[]{
            new MoneyReward().setReward(150) }, false),
    KILL_ADMIN("&cУбить администратора", "Самоубийца", new Reward[]{
            new MoneyReward().setReward(50),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(16).build().item()) }, true),
    EMPTY_SERVER("&aЗайти на пустой сервер", "Одиночество", new Reward[]{
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(8).build().item()) }, true),
    RARE_RAT("&aВы убили редкую крысу", "Супер крысолов", new Reward[]{
            new MoneyReward().setReward(15),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(4).build().item()) }, false),
    FIRST_UPGRADE("&aУлучшите один инструмент", "Освоение I", new Reward[]{
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(4).build().item()) }, false),
    GET_15("&aЗаработать 15 руб.", "Копатель онлайн", new Reward[]{
            new MoneyReward().setReward(2),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(1).build().item()) }, false),
    GET_100("&aЗаработать 100 руб.", "Повелитель мыла", new Reward[]{
            new MoneyReward().setReward(10),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(12).build().item()) }, true),
    GET_1000("&aЗаработать 1,500 руб.", "Блоковый магистр", new Reward[]{
            new MoneyReward().setReward(150),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(24).build().item()) }, true),
    GET_15000("&aЗаработать 15,000 руб.", "Да что ты вообще творишь?", new Reward[]{
            new MoneyReward().setReward(1500),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(48).build().item()) }, true),
    GET_100000("&aЗаработать 100,000 руб.", "Что? Повтори", new Reward[]{
            new MoneyReward().setReward(10000),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(64).build().item()) }, true),
    DEAD_5("&aУмереть 5 раз", "Как же так...", new Reward[]{
            new MoneyReward().setReward(10) }, false),
    DEAD_100("&aУмереть 100 раз", "Что с вами не так?", new Reward[]{
            new MoneyReward().setReward(150) }, true),
    KILL_5("&aУбить 5 заключённых", "Убийца", new Reward[]{
            new MoneyReward().setReward(10) }, false),
    KILL_100("&aУбить 100 зэков", "Киллер", new Reward[]{
            new MoneyReward().setReward(150) }, true),
    TIME_30("&aСыграть 30 минут", "Только начало огромного пути", new Reward[]{
            new MoneyReward().setReward(10) }, false),
    TIME_90("&aСыграть полтора часа", "Уже что-то)", new Reward[]{
            new BoosterReward().setReward(BoosterType.MONEY, 1.5, 900, false) }, false),
    TIME_4H("&aСыграть 4 часа", "Практически ветеран", new Reward[]{
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.4, 1200, true) }, true),
    TIME_10H("&aСыграть 10 часов", "Кажется, уже ветеран", new Reward[]{
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.5, 1800, true) }, true),
    KILL_ARROW("&aУбить себя из лука", "Хмм.. Молодец)", new Reward[]{
            new BoosterReward().setReward(BoosterType.MONEY, 1.4, 600, false),
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(12).build().item()) }, true),
    FIRST_LOCATION("&aОткрыть новую локацию", "Крупно повезло", new Reward[]{
            new ItemReward().setReward(ExampleItems.getKeyBuilder().amount(16).build().item()) }, false);

    String name;
    String title;
    Reward[] reward;
    boolean isSecret;

    static HashMap<Achievement, AchievementIcon> icons = new HashMap<>();

    static HashMap<String, ArrayList<Achievement>> queue = new HashMap<>();

    Achievement(String name, String title, Reward[] reward, boolean isSecret) {
        this.name = name;
        this.title = title;
        this.reward = reward;
        this.isSecret = isSecret;
    }

    public void get(Player player, boolean fast_get) {
        List<String> list = EConfig.ACHIEVEMENTS.getConfig().getStringList(this.toString());
        if (list.contains(player.getName())) {
            return;
        }

        ArrayList<Achievement> achievements;
        if (!fast_get) {
            if (!queue.containsKey(player.getName())) {
                achievements = new ArrayList<>();

                achievements.add(this);
                queue.put(player.getName(), achievements);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            queue.remove(player.getName());
                            cancel();
                            return;
                        }
                        ArrayList<Achievement> a = queue.get(player.getName());
                        a.remove(Achievement.this);
                        if (a.size() <= 0) {
                            queue.remove(player.getName());
                        } else queue.put(player.getName(), a);
                    }
                }.runTaskLater(Main.getInstance(), 100);
            } else {
                achievements = queue.get(player.getName());
                if (achievements.contains(this)) return;

                achievements.add(this);
                queue.put(player.getName(), achievements);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            queue.remove(player.getName());
                            cancel();
                            return;
                        }
                        ArrayList<Achievement> a = queue.get(player.getName());
                        Achievement achievement = a.get(0);

                        achievement.get(player, true);
                        a.remove(achievement);

                        if (a.size() <= 0) {
                            queue.remove(player.getName());
                        } else queue.put(player.getName(), a);
                    }
                }.runTaskLater(Main.getInstance(), 120 * (achievements.size() + 1));
                return;
            }
        }

        list.add(player.getName());
        EConfig.ACHIEVEMENTS.getConfig().set(this.toString(), list); EConfig.ACHIEVEMENTS.saveConfig();
        Arrays.stream(getReward()).forEach(rew -> rew.giveReward(player));
        Main.gamers.get(player.getUniqueId()).sendAchievementTitle(getName());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
    }

    public ItemStack getIcon(boolean opened) {
        if (opened) return icons.get(this).getIconOpened();
        return icons.get(this).getIconClosed();
    }

    public void load() {
        try {
            Arrays.stream(values()).forEach(achievement -> {
                //Загрузка новых ачивок в конфиг
                if (!EConfig.ACHIEVEMENTS.getConfig().contains(achievement.toString())) {
                    List n = new ArrayList();
                    EConfig.ACHIEVEMENTS.getConfig().set(achievement.toString(), n);
                }
                //Загрузка итемов для меню
                icons.put(achievement, new AchievementIcon.Builder(achievement).build());
            });
            EConfig.ACHIEVEMENTS.saveConfig();
            Vars.sendSystemMessage(TypeMessage.SUCCESS, values().length + " achievements loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with load achievements!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getTitle() { return title; }

    public Reward[] getReward() {
        return reward;
    }
}
