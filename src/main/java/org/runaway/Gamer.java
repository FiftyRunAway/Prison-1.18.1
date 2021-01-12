package org.runaway;

import com.nametagedit.plugin.NametagEdit;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.achievements.Achievement;
import org.runaway.auctions.TrashAuction;
import org.runaway.donate.Privs;
import org.runaway.donate.features.BoosterBlocks;
import org.runaway.donate.features.BoosterMoney;
import org.runaway.donate.features.FractionDiscount;
import org.runaway.enums.*;
import org.runaway.inventories.FractionMenu;
import org.runaway.passiveperks.EPassivePerk;
import org.runaway.passiveperks.PassivePerks;
import org.runaway.passiveperks.perks.BBMoneySecond;
import org.runaway.passiveperks.perks.BBlocksFirst;
import org.runaway.passiveperks.perks.BBlocksSecond;
import org.runaway.passiveperks.perks.BMoneyFirst;
import org.runaway.rebirth.ESkill;
import org.runaway.tasks.AsyncRepeatTask;
import org.runaway.tasks.SyncTask;
import org.runaway.trainer.Trainer;
import org.runaway.trainer.TypeTrainings;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by _RunAway_ on 16.1.2019
 */

public class Gamer {

    public static HashMap<Player, Long> messages = new HashMap<>();

    private static final String bp_perm = "prison.battlepass";

    public static ArrayList<UUID> tp = new ArrayList<>();
    private static final int teleportTimer = 3;
    public static int toRebirth = 30;

    private String gamer;
    private Player player;
    private final UUID uuid;
    private String replyPlayer;

    private boolean isOnline = false;

    private Map<String, Long> cooldowns;

    public Gamer(UUID uuid) {
        this.uuid = uuid;
        this.cooldowns = OfflineValues.getPlayerCooldown(uuid).getCooldowns();
        if (Utils.getPlayers().contains(Bukkit.getOfflinePlayer(getUUID()).getName()) || Bukkit.getOfflinePlayer(getUUID()).isOnline()) {
            this.player = Bukkit.getPlayer(this.uuid);
            this.gamer = this.player.getName();
            this.isOnline = true;
        }
    }

    public boolean isEndedCooldown(String name) {
        if(cooldowns == null) return false;
        boolean ended = !cooldowns.containsKey(name) || cooldowns.get(name) <= System.currentTimeMillis();
        if (ended) {
            cooldowns.remove(name);
        }
        return ended;
    }

    public void addCooldown(String name, long cooldown) {
        if (this.cooldowns.containsKey(name)) return;
        this.cooldowns.put(name, System.currentTimeMillis() + cooldown);
    }

    private boolean isOnline() {
        return this.isOnline;
    }

    public void setReplyPlayer(String replyPlayer) {
        this.replyPlayer = replyPlayer;
    }

    public String getReplyPlayer() {
        return replyPlayer;
    }

    public void sendMessage(EMessage message) {
        switch (message.geteMessageType()) {
            case CHAT:
                sendMessage(message.getMessage());
                break;
            case TITLE:
                sendTitle(message.getMessage());
                break;
            case ACTION_BAR:
                sendActionbar(message.getMessage());
                break;
        }
    }

    public void sendMessage(String message) {
        // Avoid command spam
        if(!isEndedCooldown("lastMsg")) {
            return;
        }
        addCooldown("lastMsg", 600);
        getPlayer().sendMessage(Utils.colored("&7[&4&lPrison&7] &r" + message));
    }

    public Privs getPrivilege() {
        return Privs.DEFAULT.getPrivilege(getPlayer());
    }

    public float rebirthSale() {
        return (float)(getValueRebirth(ESkill.SALE) / 100);
    }

    public int getValueRebirth(ESkill skill) {
        return skill.getSkill().getValue(getGamer());
    }

    public boolean hasMoney(double money) {
        return getDoubleStatistics(EStat.MONEY) >= money;
    }

    public boolean hasBattlePass() {
        return getPlayer().hasPermission(bp_perm);
    }

    public void addExperienceBP(int experience) {
        increaseIntStatistics(EStat.BATTLEPASS_SCORE, experience);
        sendActionbar(Utils.colored("&dПолучено " + experience + " опыта"));
    }

    public ArrayList<PassivePerks> getPassivePerks() {
        ArrayList<PassivePerks> list = new ArrayList<>();
        FileConfiguration cfg = EConfig.TALANTS.getConfig();
        if (!cfg.contains(getGamer())) return null;
        for (String s : cfg.getStringList(getGamer())) {
            list.add(EPassivePerk.valueOf(s).getPerk());
        }
        return list;
    }

    public boolean hasPassivePerk(PassivePerks perk) {
        ArrayList<PassivePerks> perks = getPassivePerks();
        if (perks == null) return false;
        for (PassivePerks p : perks) {
            if (p.getSlot() == perk.getSlot()) {
                return true;
            }
        }
        return false;
    }

    public void addPassivePerk(PassivePerks perk) {
        ArrayList<String> strs = new ArrayList<>();
        strs.add(perk.getClass().getSimpleName().toUpperCase());
        if (EConfig.TALANTS.getConfig().contains(getGamer())) {
            strs.addAll(EConfig.TALANTS.getConfig().getStringList(getGamer()));
        }
        EConfig.TALANTS.getConfig().set(getGamer(), strs);
        EConfig.TALANTS.saveConfig();
    }

    public void setHearts() {
        int res = 19 + getIntStatistics(EStat.LEVEL);
        getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(res);
        getPlayer().setHealth(res);
    }

    public void setNametag() {
        NametagEdit.getApi().setPrefix(getPlayer(), ChatColor.YELLOW + getDisplayRebirth() + (getIntStatistics(EStat.REBIRTH) > 0 ? " " : "") + "&7[" + getLevelColor() + getDisplayLevel() + "&7] " + getFaction().getColor());
    }

    public void teleportBase() {
        getPlayer().closeInventory();
        if (!getFaction().equals(FactionType.DEFAULT)) {
            teleport(Utils.unserializeLocation(EConfig.CONFIG.getConfig().getString("locations.base_" + getFaction().toString().toLowerCase())));
        } else {
            sendMessage(EMessage.NOFACTION);
        }
    }

    public void teleportTrashAuction() {
        getPlayer().closeInventory();
        if (is1_15_2()) {
            if (!TrashAuction.auctions.isEmpty()) {
                teleport(TrashAuction.auction_spawn);
            } else {
                StringBuilder times = new StringBuilder();
                AtomicInteger i = new AtomicInteger(0);
                TrashAuction.times.forEach(integer -> {
                    times.append(integer);
                    if (TrashAuction.times.size() != i.getAndIncrement() + 1) times.append(", ");
                });
                sendMessage(Utils.colored(EMessage.AUCTIONTIMES.getMessage().replaceAll("%time%", times.toString() + " часов по МСК")));
            }
        } else {
            sendTitle("&cТолько", "&41.15.2+");
        }
    }

    public boolean isInventory() {
        return getPlayer().getInventory().firstEmpty() != -1;
    }

    public void addEffect(PotionEffectType effect, int ticks, int level) {
        getPlayer().addPotionEffect(new PotionEffect(effect, ticks + 25, level, true));
    }

    public void addBooster(BoosterType type, double multiplier, long time, boolean global) {
        String format = type.name() + " " + (global ? "GLOBAL" : "LOCAL") + " " + multiplier + " " + time;
        if (!EConfig.BOOSTERS.getConfig().contains(getGamer())) {
            List<String> list = new ArrayList<>();
            list.add(format);
            EConfig.BOOSTERS.getConfig().set(getGamer(), list);
        } else {
            List<String> list = EConfig.BOOSTERS.getConfig().getStringList(getGamer());
            list.add(format);
            EConfig.BOOSTERS.getConfig().set(getGamer(), list);
        }
        EConfig.BOOSTERS.saveConfig();
    }

    public boolean isEffected(PotionEffectType e) {
        return getPlayer().hasPotionEffect(e);
    }

    public String getDisplayRebirth() {
        switch (getIntStatistics(EStat.REBIRTH)) {
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            case 3: {
                return "III";
            }
            case 4: {
                return "IV";
            }
            case 5: {
                return "V";
            }
            case 6: {
                return "VI";
            }
            default:
                return "";
        }
    }

    public int getLevelItem() {
        return getLevelItem(getPlayer().getInventory().getItemInMainHand());
    }

    public int getLevelItem(ItemStack item) {
        try {
            if (!getPlayer().getGameMode().equals(GameMode.CREATIVE) && item.getItemMeta().getDisplayName() != null) {
                String lore = ChatColor.stripColor(item.getItemMeta().getLore().get(0)).toLowerCase();
                if (lore.contains("минимальный")) {
                    return Integer.parseInt(lore.replace("минимальный уровень: ", ""));
                }
            }
        } catch (Exception ignored) { return -1; }
        return -1;
    }

    public int getDisplayLevel() {
        if (getIntStatistics(EStat.LEVEL) != toRebirth) {
            return getIntStatistics(EStat.LEVEL) % 30;
        }
        return getIntStatistics(EStat.LEVEL);
    }

    public void rebirth () {
        increaseIntStatistics(EStat.REBIRTH);
        increaseIntStatistics(EStat.REBIRTH_SCORE, 5);
        Bukkit.broadcastMessage(Utils.colored(EMessage.BROADCAST_REBITH.getMessage()).replaceAll("%player%", getPlayer().getName()).replaceAll("%rebirth%", ChatColor.YELLOW + getDisplayRebirth()));

        Inventory inventory = getPlayer().getInventory();
        Inventory ec = getPlayer().getEnderChest();

        Arrays.stream(inventory.getContents()).forEach(inventory::remove);
        Arrays.stream(ec.getContents()).forEach(ec::remove);

        getPlayer().teleport(Main.SPAWN);
        annulateStat();
        setNametag();

        setHearts();
        getPlayer().setFoodLevel(20);
        getPlayer().setExp(0);
        getPlayer().setLevel(0);

        Achievement.JOIN.get(player, false);
        Achievement.REBIRTH.get(player, false);

        ItemStack is = new Item.Builder(Material.PAPER).name("&aМеню").lore(new Lore.BuilderLore().addSpace().addString("&7>> &bОткрыть").build()).build().item();
        if (!player.getInventory().contains(is)) {
            player.getInventory().setItem(8, is);
        }
        player.getInventory().addItem(UpgradeMisc.buildItem("waxe0", false, player, false));
        player.getInventory().addItem(new Item.Builder(Material.COOKED_BEEF).name("&dВкуснейший стейк").amount(8).build().item());
    }

    private void resetQuests() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "questadmin reset " + getGamer());
    }

    private void annulateStat() {
        Arrays.stream(EStat.values()).forEach(stat -> {
            if (!stat.equals(EStat.KEYS) &&
                    !stat.toString().startsWith("REBIRTH") &&
                    !stat.equals(EStat.AUTOSELL) &&
                    !stat.equals(EStat.BOOSTERBLOCKS) &&
                    !stat.equals(EStat.BOOSTERMONEY) &&
                    !stat.toString().endsWith("TRAINER") &&
                    !stat.equals(EStat.PLAYEDTIME) &&
                    !stat.equals(EStat.AUTOSELLDONATE) &&
                    !stat.equals(EStat.ZBT)) {
                setStatistics(stat, stat.getDefualt());
            }
        });
        EConfig.BLOCKS.getConfig().set(getGamer(), null);
        Achievement.removeAll(getPlayer());
        resetQuests();
    }

    public boolean needRebirth() {
        return getIntStatistics(EStat.LEVEL) % toRebirth == 0;
    }

    public void inFraction(FactionType type, boolean isRandom, int cost) {
        FactionType in = type;
        boolean money = true;
        if (isRandom) {
            while (in.equals(FactionType.DEFAULT)) {
                int r = new Random().nextInt(FactionType.values().length);
                in = FactionType.values()[r];
            }
            money = false;
        }
        if (money) {
            if (hasMoney(cost)) {
                withdrawMoney(cost);
            } else {
                sendMessage(EMessage.MONEYNEEDS);
                return;
            }
        }
        sendMessage(Utils.colored(EMessage.FRACTIONSUCCESS.getMessage()).replaceAll("%fraction%", in.getColor() + in.getName()));
        setStatistics(EStat.FACTION, in.getConfigName().toUpperCase());
        getPlayer().closeInventory();
    }

    public void leaveFraction() {
        if (!getFaction().equals(FactionType.DEFAULT)) {
            int mon = EConfig.CONFIG.getConfig().getInt("costs.FractionLeave") * getIntStatistics(EStat.LEVEL);
            Object obj = getPrivilege().getValue(new FractionDiscount());
            if (obj != null) mon = mon * (1 - Integer.parseInt(obj.toString()) / 100);
            if (hasMoney(mon)) {
                setStatistics(EStat.FACTION, FactionType.DEFAULT.getConfigName().toUpperCase());
                withdrawMoney(mon);
                sendMessage(EMessage.SUCCESSFULYLEAVE);
                teleport(Main.SPAWN);
            } else {
                sendMessage(EMessage.MONEYNEEDS);
            }
        } else {
           new FractionMenu(getPlayer());
        }
    }

    public boolean isActiveLocalBlocks() {
        return Utils.getlBlocksTime().containsKey(getGamer());
    }

    public boolean isActiveLocalMoney() {
        return Utils.getlMoneyTime().containsKey(getGamer());
    }

    public void setLevelBar() {
        getPlayer().setLevel(getIntStatistics(EStat.LEVEL) % 30);
    }

    public void setExpProgress() {
        /*int needblocks = EConfig.CONFIG.getConfig().getInt("levels." + (getIntStatistics(EStat.LEVEL) + 1) + ".blocks");
        float toSet = (float)(getDoubleStatistics(EStat.BLOCKS) / needblocks);
        if (toSet > 1) toSet = 1;
        if (Math.round(getDoubleStatistics(EStat.BLOCKS)) <= needblocks) getPlayer().setExp(toSet);*/
        getPlayer().setExp(0);
    }

    public void teleport(Location location) {
        if (getPlayer().hasPermission("prison.admin")) {
            sendTitle(ChatColor.GREEN + "Телепортация...");
            getPlayer().teleport(location);
            return;
        }
        if (!tp.contains(uuid)) {
            tp.add(uuid);
            addEffect(PotionEffectType.BLINDNESS, 60, 3);
            AtomicInteger lef = new AtomicInteger(teleportTimer);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (tp.contains(uuid)) {
                        if (lef.get() == 0) {
                            sendTitle(ChatColor.GREEN + "Телепортация...");
                            getPlayer().teleport(location);
                            tp.remove(uuid);
                            cancel();
                            return;
                        }
                        sendTitleTeleport(lef.get());
                        lef.decrementAndGet();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0L, 20L);
        } else {
            sendMessage(EMessage.ALREADYTP);
        }
    }

    public void depositMoney(double money) {
        if (money <= 0) return;
        if (getStatistics(EStat.MONEY) instanceof Integer) {
            setStatistics(EStat.MONEY, new BigDecimal(money + getIntStatistics(EStat.MONEY)).setScale(2, RoundingMode.UP).doubleValue());
        } else {
            setStatistics(EStat.MONEY, new BigDecimal(money + getDoubleStatistics(EStat.MONEY)).setScale(2, RoundingMode.UP).doubleValue());
        }
        if (isOnline()) {
            sendActionbar(Utils.colored("&a+" + new BigDecimal(money).setScale(2, RoundingMode.UP).doubleValue() + " " + MoneyType.RUBLES.getShortName()));
            double m = getMoney();
            if (m >= 15) Achievement.GET_15.get(getPlayer(), false);
            if (m >= 100) Achievement.GET_100.get(getPlayer(), false);
            if (m >= 1500) Achievement.GET_1000.get(getPlayer(), false);
            if (m >= 15000) Achievement.GET_15000.get(getPlayer(), false);
            if (m >= 100000) Achievement.GET_100000.get(getPlayer(), false);
        }
    }

    private int getVerison() {
        return 1000;
    }

    private boolean is1_15_2() {
        return getVerison() >= 578;
    }

    public void withdrawMoney(double money, int sale) {
        double to_withdraw = (1 - (double)sale / 100) * money;
        setStatistics(EStat.MONEY, getMoney() - to_withdraw);
        if (isOnline()) {
            sendActionbar(Utils.colored("&c-" + money + " " + MoneyType.RUBLES.getShortName()));
            if (getIntStatistics(EStat.CASHBACK_TRAINER) > 0) {
                Utils.trainer.forEach(trainer -> {
                    Trainer tr = (Trainer) trainer;
                    if (tr.getType() != TypeTrainings.CASHBACK) return;
                    if (Math.random() < tr.getValue(getPlayer())) {
                        double cashback = Math.round(to_withdraw / 4);
                        sendMessage(Utils.colored(EMessage.CASHBACK.getMessage()).replace("%cashback%", cashback + " " + MoneyType.RUBLES.getShortName()).replace("%money%", money + " " + MoneyType.RUBLES.getShortName()));
                        depositMoney(cashback);
                    }
                });
            }
        }
    }

    public void withdrawMoney(double money) {
        withdrawMoney(money, 0);
    }

    public int getCurrentBlocks(String block, int data) {
        try {
            return (int)EConfig.BLOCKS.getConfig().getDouble(getGamer() + "." + block + "-" + data);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCurrentBlocks(String block) {
        return getCurrentBlocks(block, 0);
    }

    public double getBoosterBlocks() {
        double boost = getDoubleStatistics(EStat.BOOSTERBLOCKS);
        if (Main.gBlocks.isActive()) boost += Main.gBlocks.getMultiplier() - 1.0;
        if (isActiveLocalBlocks()) boost += Utils.getlBlocksMultiplier().get(getGamer()) - 1.0;
        Object b = getPrivilege().getValue(new BoosterBlocks());
        if (b != null) boost += Double.parseDouble(b.toString()) - 1.0;
        if (hasPassivePerk(new BBlocksFirst())) boost += 0.1;
        if (hasPassivePerk(new BBlocksSecond())) boost += 0.2;
        return new BigDecimal(boost).setScale(2, RoundingMode.UP).doubleValue();
    }

    public double getBoosterMoney() {
        double boost = getDoubleStatistics(EStat.BOOSTERMONEY);
        if (Main.gMoney.isActive()) boost += Main.gMoney.getMultiplier() - 1.0;
        if (isActiveLocalMoney()) boost += Utils.getlMoneyMultiplier().get(getGamer()) - 1.0;
        Object b = getPrivilege().getValue(new BoosterMoney());
        if (b != null) boost += Double.parseDouble(b.toString()) - 1.0;
        if (hasPassivePerk(new BMoneyFirst())) boost += 0.1;
        if (hasPassivePerk(new BBMoneySecond())) boost += 0.2;
        return new BigDecimal(boost).setScale(2, RoundingMode.UP).doubleValue();
    }

    public FactionType getFaction() {
        try {
            return FactionType.valueOf(getStatistics(EStat.FACTION).toString().toUpperCase());
        } catch (Exception ex) { return null; }
    }

    /*public String getModeString() {
        String mode = getStatistics(EStat.MODE).toString().toLowerCase();
        if ("easy".equals(mode)) { return "&eL";  } else if ("normal".equals(mode)) { return "&aN";  } else if ("hard".equals(mode)) { return "&cH"; }
        return "";
    }*/

    public ChatColor getLevelColor() {
        int level = getIntStatistics(EStat.LEVEL);
        if (level > 0 && level < 5) { return ChatColor.GRAY; }
        if (level >= 5 && level < 10) { return ChatColor.YELLOW; }
        if (level >= 10 && level < 15) { return ChatColor.GREEN; }
        if (level >= 15 && level < 20) { return ChatColor.RED; }
        if (level >= 20) { return ChatColor.LIGHT_PURPLE; }
        return null;
    }

    public Object getStatisticsFromConfig(EStat statistic, String name) {
        return statistic.getFromConfig(name);
    }

    public void increaseIntStatistics(EStat stat, int value) {
        setStatistics(stat, getIntStatistics(stat) + value);
    }

    public void increaseIntStatistics(EStat stat) {
        this.increaseIntStatistics(stat, 1);
    }

    public void increaseDoubleStatistics(EStat stat, double value) {
        setStatistics(stat, getDoubleStatistics(stat) + value);
    }

    public void increaseDoubleStatistics(EStat stat) {
        this.increaseDoubleStatistics(stat, 1D);
    }

    public int getIntStatistics(EStat stat) {
        return (int) getStatistics(stat);
    }

    public String getStringStatistics(EStat stat) {
        return (String) getStatistics(stat);
    }

    public double getDoubleStatistics(EStat stat) {
        return (double) getStatistics(stat);
    }

    public boolean getBooleanStatistics(EStat stat) {
        return (boolean) getStatistics(stat);
    }

    public Object getStatistics(EStat statistic) {
        if (isOnline()) {
            return statistic.getMap().get(getGamer());
        } else {
            System.out.println(Bukkit.getOfflinePlayer(getUUID()).getName());
            return getStatisticsFromConfig(statistic, Bukkit.getOfflinePlayer(getUUID()).getName());
        }
    }

    public void setStatistics(EStat statistic, Object value) {
        if (isOnline()) {
            statistic.getMap().put(getPlayer().getName(), value);
        } else {
            System.out.println(Bukkit.getOfflinePlayer(getUUID()).getName());
            statistic.setInConfig(Bukkit.getOfflinePlayer(getUUID()).getName(), value);
        }
    }

    public double getMoney() {
        return getDoubleStatistics(EStat.MONEY);
    }

    public String getGamer() {
        return this.gamer;
    }

    public Player getPlayer() {
        return this.player;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void sendTitle(String title, String subtitle) {
        getPlayer().sendTitle(Utils.colored(title), Utils.colored(subtitle), 15, 15, 15);
    }

    private void sendTitleTeleport(int time) {
        getPlayer().sendTitle(Utils.colored("&aДо телепортации"), Utils.colored(ChatColor.YELLOW + "" + time + " сек."), 0, 25, 0);
    }

    public void sendAchievementTitle(String name) {
        getPlayer().sendTitle(Utils.colored("&aНовое достижение!"), Utils.colored(name), 50, 70, 50);
    }

    public void sendTitle(String title) {
        String subtitle = null;
        if(title.contains("\n")) {
            title = title.split("\n")[0];
            subtitle = title.split("\n")[1];
        }
        sendTitle(title, subtitle);
    }

    public void sendActionbar(String msg) {
        getPlayer().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.colored(msg)));
    }
}
