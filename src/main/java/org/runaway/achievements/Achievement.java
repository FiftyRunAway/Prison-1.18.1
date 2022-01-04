package org.runaway.achievements;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.enums.BoosterType;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Vars;

import java.util.*;

public enum Achievement {
    JOIN("&cВы попали в тюрьму", "Приговор вынесен", new Reward[]{
            new MoneyReward().setReward(1) }, false),
    FIVE_LEVEL("&aПолучить 5 уровень","Начинающий зэк", new Reward[]{
            new ItemReward().setReward(new Item.Builder(Material.COOKED_BEEF).name("&dВкуснейший стейк").build().item()) }, true),
    TEN_LEVEL("&aПолучить 10 уровень","Опытный зэк", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(12)),
            new BoosterReward().setReward(BoosterType.MONEY, 1.4, 1200, false),
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.4, 1200, false)}, true),
    FIFTEEN_LEVEL("&aПолучить 15 уровень","Зэк-ветеран", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(24)),
            new BoosterReward().setReward(BoosterType.MONEY, 1.4, 1800, false),
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.4, 1800, false)}, true),
    TWENTY_LEVEL("&aПолучить 20 уровень","Надёжный зэк", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(48)),
            new BoosterReward().setReward(BoosterType.MONEY, 1.4, 3600, false),
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.4, 3600, false)}, true),
    TWENTYFIFTH_LEVEL("&aПолучить 25 уровень","Зэк магистр", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(64)),
            new BoosterReward().setReward(BoosterType.MONEY, 1.4, 3600, false),
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.4, 3600, false)}, true),
    REBIRTH("&aПереродитесь впервые","Иисус", new Reward[]{
            new BoosterReward().setReward(BoosterType.MONEY, 1.2, 10800, true),
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.2, 10800, true)}, false),
    FIRST_TREASURE("&aНайти свой первый клад","Сыщик I", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(4)) }, false),
    SPIDER_KILL("&aВы добили матку впервые", "Ведьмак", new Reward[]{
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.2, 900, false),
            new MoneyReward().setReward(50) }, false),
    BLAZE_KILL("&aВы добили огненного стража", "Ведьмак II", new Reward[]{
            new MoneyReward().setReward(75) }, true),
    GOLEM_KILL("&aВы добили голема", "Ведьмак III", new Reward[]{
            new MoneyReward().setReward(100) }, true),
    SLIME_KILL("&aВы добили древнего слизняка", "Ведьмак IV", new Reward[]{
            new MoneyReward().setReward(100) }, true),
    FIFTEEN_RATS("&aВы убили 15 крыс", "Крысолов I", new Reward[]{
            new MoneyReward().setReward(10),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(10)) }, false),
    RARE_RAT("&aВы убили редкую крысу", "Супер крысолов", new Reward[]{
            new MoneyReward().setReward(15),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(4)) }, false),
    FIFTEEN_ZOMBIES("&aВы убили 15 зомби", "Зомболов I", new Reward[]{
            new MoneyReward().setReward(50),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(16)) }, false),
    FIFTY_SKELETONS("&aВы убили 50 скелетов", "Скелетный серийный убийца", new Reward[]{
            new MoneyReward().setReward(120),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(24)) }, false),
    FIRST_TRAINER("&aПрокачаться у тренера", "Хороший ученик", new Reward[]{
            new MoneyReward().setReward(150) }, false),
    KILL_ADMIN("&cУбить администратора", "Самоубийца", new Reward[]{
            new MoneyReward().setReward(50),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(16)) }, true),
    EMPTY_SERVER("&aЗайти на пустой сервер", "Одиночество", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(4)) }, true),
    FIRST_UPGRADE("&aУлучшите один инструмент", "Освоение I", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(4)) }, false),
    GET_15("&aЗаработать 15 руб.", "Копатель онлайн", new Reward[]{
            new MoneyReward().setReward(2),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(2)) }, false),
    GET_100("&aЗаработать 100 руб.", "Повелитель мыла", new Reward[]{
            new MoneyReward().setReward(10),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(8)) }, true),
    FIRST_FISH("&aПоймать первую легендарную рыбу", "Рыболоff", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(12)) }, false),
    DEAD_5("&aУмереть 5 раз", "Как же так...", new Reward[]{
            new MoneyReward().setReward(10) }, false),
    DEAD_100("&aУмереть 100 раз", "Что с вами не так?", new Reward[]{
            new MoneyReward().setReward(150) }, true),
    KILL_5("&aУбить 5 заключённых", "Убийца", new Reward[]{
            new MoneyReward().setReward(10) }, false),
    KILL_100("&aУбить сотню зэков", "Киллер", new Reward[]{
            new MoneyReward().setReward(150) }, true),
    TIME_30("&aСыграть 30 минут", "Это только начало пути", new Reward[]{
            new MoneyReward().setReward(10) }, false),
    TIME_90("&aСыграть полтора часа", "Уже что-то)", new Reward[]{
            new BoosterReward().setReward(BoosterType.MONEY, 1.2, 900, false) }, false),
    TIME_4H("&aСыграть 4 часа", "Практически ветеран", new Reward[]{
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.2, 1200, true) }, true),
    TIME_10H("&aСыграть 10 часов", "Кажется, уже ветеран", new Reward[]{
            new BoosterReward().setReward(BoosterType.BLOCKS, 1.3, 1800, true) }, true),
    KILL_ARROW("&aУбить себя из лука", "Хмм.. Молодец)", new Reward[]{
            new BoosterReward().setReward(BoosterType.MONEY, 1.2, 600, false),
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(12)) }, true),
    FIRST_LOCATION("&aОткрыть новую локацию", "Крупно повезло", new Reward[]{
            new ItemReward().setReward(ItemManager.getPrisonItem(Achievement.key).getItemStack(12)) }, false);

    private static final String key = "defaultKey";
    String name;
    String title;
    Reward[] reward;
    boolean isSecret;

    static Map<Achievement, AchievementIcon> icons = new EnumMap<>(Achievement.class);

    Achievement(String name, String title, Reward[] reward, boolean isSecret) {
        this.name = name;
        this.title = title;
        this.reward = reward;
        this.isSecret = isSecret;

    }

    public void get(Player player) {
        Gamer gamer = GamerManager.getGamer(player);
        List<Achievement> list = gamer.getAchievements();
        if (list.contains(this)) {
            return;
        }
        gamer.getAchievements().add(this);
        Arrays.stream(getReward()).forEach(rew -> rew.giveReward(player));
        GamerManager.getGamer(player).sendAchievementTitle(getName());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
    }

    public static void removeAll(Player player) {
        Gamer gamer = GamerManager.getGamer(player);
        gamer.getAchievements().clear();
    }

    public ItemStack getIcon(boolean opened) {
        if (opened) return icons.get(this).getIconOpened();
        return icons.get(this).getIconClosed();
    }

    public void load() {
        try {
            Arrays.stream(values()).forEach(achievement ->
                    icons.put(achievement, new AchievementIcon.Builder(achievement).build()));
            Vars.sendSystemMessage(TypeMessage.SUCCESS, values().length + " achievements loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with load achievements!");
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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

    public boolean isSecret() {
        return isSecret;
    }
}
