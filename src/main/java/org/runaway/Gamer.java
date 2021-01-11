package org.runaway;

import com.nametagedit.plugin.NametagEdit;
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
import org.runaway.trainer.Trainer;
import org.runaway.trainer.TypeTrainings;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import us.myles.ViaVersion.api.Via;

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
    private final long msg_cd = 200;

    private static final String bp_perm = "prison.battlepass";

    public static ArrayList<UUID> tp = new ArrayList<>();
    private static final int teleportTimer = 3;
    public static int toRebirth = 30;

    private String gamer;
    private Player player;
    private final UUID uuid;

    private boolean isOnline = false;

    public Gamer(UUID uuid) {
        this.uuid = uuid;
        if (Utils.getPlayers().contains(Bukkit.getOfflinePlayer(getUUID()).getName()) || Bukkit.getOfflinePlayer(getUUID()).isOnline()) {
            this.player = Bukkit.getPlayer(this.uuid);
            this.gamer = this.player.getName();
            this.isOnline = true;
        }
    }

    private boolean isOnline() {
        return this.isOnline;
    }

    public void sendMessage(EMessage message) {
        // Avoid command spam
        Long oldCooldown = messages.get(getPlayer());
        if (oldCooldown != null) {
            if (System.currentTimeMillis() < oldCooldown) {
                return;
            }
        }
        messages.put(getPlayer(), System.currentTimeMillis() + 200); // 0.2 seconds cooldown
        
        getPlayer().sendMessage(Utils.colored(message.getMessage()));
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
        return (double)getStatistics(EStat.MONEY) >= money;
    }

    public boolean hasBattlePass() {
        return getPlayer().hasPermission(bp_perm);
    }

    public void addExperienceBP(int experience) {
        setStatistics(EStat.BATTLEPASS_SCORE, (int)getStatistics(EStat.BATTLEPASS_SCORE) + experience);
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
        int res = 19 + (int)getStatistics(EStat.LEVEL);
        getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(res);
        getPlayer().setHealth(res);
    }

    public void setNametag() {
        NametagEdit.getApi().setPrefix(getPlayer(), ChatColor.YELLOW + getDisplayRebirth() + ((int)getStatistics(EStat.REBIRTH) > 0 ? " " : "") + "&7[" + getLevelColor() + getDisplayLevel() + "&7] " + getFaction().getColor());
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
            if (TrashAuction.auctions.size() > 0) {
                teleport(TrashAuction.auction_spawn);
            } else {
                StringBuilder times = new StringBuilder();
                AtomicInteger i = new AtomicInteger(0);
                TrashAuction.times.forEach(integer -> {
                    times.append(integer);
                    if (TrashAuction.times.size() != i.getAndIncrement() + 1) times.append(", ");
                });
                player.sendMessage(Utils.colored(EMessage.AUCTIONTIMES.getMessage().replaceAll("%time%", times.toString() + " часов по МСК")));
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
        AtomicBoolean s = new AtomicBoolean(false);
        getPlayer().getActivePotionEffects().forEach(effect -> {
            if (effect.getType().equals(e)) {
                s.set(true);
            }
        });
        return s.get();
    }

    public String getDisplayRebirth() {
        switch ((int)getStatistics(EStat.REBIRTH)) {
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
        if ((int)getStatistics(EStat.LEVEL) != toRebirth) {
            return (int)getStatistics(EStat.LEVEL) % 30;
        }
        return (int)getStatistics(EStat.LEVEL);
    }

    public void rebirth () {
        setStatistics(EStat.REBIRTH, (int)getStatistics(EStat.REBIRTH) + 1);
        setStatistics(EStat.REBIRTH_SCORE, (int)getStatistics(EStat.REBIRTH_SCORE) + 5);
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
        return (int)getStatistics(EStat.LEVEL) % toRebirth == 0;
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
        getPlayer().sendMessage(Utils.colored(EMessage.FRACTIONSUCCESS.getMessage()).replaceAll("%fraction%", in.getColor() + in.getName()));
        setStatistics(EStat.FACTION, in.getConfigName().toUpperCase());
        getPlayer().closeInventory();
    }

    public void leaveFraction() {
        if (!getFaction().equals(FactionType.DEFAULT)) {
            int mon = EConfig.CONFIG.getConfig().getInt("costs.FractionLeave") * (int)getStatistics(EStat.LEVEL);
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
        getPlayer().setLevel((int)getStatistics(EStat.LEVEL) % 30);
    }

    public void setExpProgress() {
        /*int needblocks = EConfig.CONFIG.getConfig().getInt("levels." + ((int)getStatistics(EStat.LEVEL) + 1) + ".blocks");
        float toSet = (float)((double)getStatistics(EStat.BLOCKS) / needblocks);
        if (toSet > 1) toSet = 1;
        if (Math.round((double)getStatistics(EStat.BLOCKS)) <= needblocks) getPlayer().setExp(toSet);*/
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
            addEffect(PotionEffectType.BLINDNESS, 70, 3);
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
            setStatistics(EStat.MONEY, new BigDecimal(money + (int)getStatistics(EStat.MONEY)).setScale(2, RoundingMode.UP).doubleValue());
        } else {
            setStatistics(EStat.MONEY, new BigDecimal(money + (double)getStatistics(EStat.MONEY)).setScale(2, RoundingMode.UP).doubleValue());
        }
        if (isOnline()) {
            sendActionbar(Utils.colored("&a+" + new BigDecimal(money).setScale(2, RoundingMode.UP).doubleValue() + " " + MoneyType.RUBLES.getShortName()));
            double m = (double)getStatistics(EStat.MONEY);
            if (m >= 15) Achievement.GET_15.get(getPlayer(), false);
            if (m >= 100) Achievement.GET_100.get(getPlayer(), false);
            if (m >= 1500) Achievement.GET_1000.get(getPlayer(), false);
            if (m >= 15000) Achievement.GET_15000.get(getPlayer(), false);
            if (m >= 100000) Achievement.GET_100000.get(getPlayer(), false);
        }
    }

    private int getVerison() {
        if (Main.useViaVersion) {
            return Via.getAPI().getPlayerVersion(this.uuid);
        }
        return 1000;
    }

    private boolean is1_15_2() {
        return getVerison() >= 578;
    }

    public void withdrawMoney(double money, int sale) {
        double to_withdraw = (1 - (double)sale / 100) * money;
        setStatistics(EStat.MONEY, (double)getStatistics(EStat.MONEY) - to_withdraw);
        if (isOnline()) {
            sendActionbar(Utils.colored("&c-" + money + " " + MoneyType.RUBLES.getShortName()));
            if ((int)getStatistics(EStat.CASHBACK_TRAINER) > 0) {
                Utils.trainer.forEach(trainer -> {
                    Trainer tr = (Trainer) trainer;
                    if (tr.getType() != TypeTrainings.CASHBACK) return;
                    if (Math.random() < tr.getValue(getPlayer())) {
                        double cashback = Math.round(to_withdraw / 4);
                        getPlayer().sendMessage(Utils.colored(EMessage.CASHBACK.getMessage()).replace("%cashback%", cashback + " " + MoneyType.RUBLES.getShortName()).replace("%money%", money + " " + MoneyType.RUBLES.getShortName()));
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
        double boost = (double)getStatistics(EStat.BOOSTERBLOCKS);
        if (Main.gBlocks.isActive()) boost += Main.gBlocks.getMultiplier() - 1.0;
        if (isActiveLocalBlocks()) boost += Utils.getlBlocksMultiplier().get(getGamer()) - 1.0;
        Object b = getPrivilege().getValue(new BoosterBlocks());
        if (b != null) boost += Double.parseDouble(b.toString()) - 1.0;
        if (hasPassivePerk(new BBlocksFirst())) boost += 0.1;
        if (hasPassivePerk(new BBlocksSecond())) boost += 0.2;
        return new BigDecimal(boost).setScale(2, RoundingMode.UP).doubleValue();
    }

    public double getBoosterMoney() {
        double boost = (double)getStatistics(EStat.BOOSTERMONEY);
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
        int level = (int) getStatistics(EStat.LEVEL);
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
        sendTitle(title, null);
    }

    public void sendActionbar(String msg) {
        try {
            Constructor<?> constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutChat")).getConstructor(Utils.getNMSClass("IChatBaseComponent"), Utils.getNMSClass("ChatMessageType"));
            Object icbc = Objects.requireNonNull(Utils.getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + msg + "\"}");
            Object packet = constructor.newInstance(icbc, Objects.requireNonNull(Utils.getNMSClass("ChatMessageType")).getEnumConstants()[2]);
            Object entityPlayer = this.getPlayer().getClass().getMethod("getHandle").invoke(this.getPlayer());
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getMethod("sendPacket", Utils.getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
