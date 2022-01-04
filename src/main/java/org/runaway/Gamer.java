package org.runaway;

import com.nametagedit.plugin.NametagEdit;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.achievements.Achievement;
import org.runaway.auctions.TrashAuction;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.battlepass.IReward;
import org.runaway.donate.Privs;
import org.runaway.donate.features.BoosterBlocks;
import org.runaway.donate.features.BoosterMoney;
import org.runaway.donate.features.FractionDiscount;
import org.runaway.enums.*;
import org.runaway.fishing.EFishType;
import org.runaway.inventories.BattlePassMenu;
import org.runaway.inventories.FractionMenu;
import org.runaway.items.Item;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.Parameter;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.managers.GamerManager;
import org.runaway.menu.IMenu;
import org.runaway.passiveperks.EPassivePerk;
import org.runaway.passiveperks.PassivePerks;
import org.runaway.passiveperks.perks.BBMoneySecond;
import org.runaway.passiveperks.perks.BBlocksFirst;
import org.runaway.passiveperks.perks.BBlocksSecond;
import org.runaway.passiveperks.perks.BMoneyFirst;
import org.runaway.rebirth.ESkill;
import org.runaway.sqlite.PreparedRequests;
import org.runaway.trainer.Trainer;
import org.runaway.trainer.TypeTrainings;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/*
 * Created by _RunAway_ on 16.1.2019
 */

@Getter @Setter
public class Gamer {

    private static final String bpPerm = "prison.battlepass";

    public static ArrayList<UUID> tp = new ArrayList<>();
    private static final int teleportTimer = 3;
    public static int toRebirth = 30;
    public static PreparedRequests preparedRequests;

    private String gamer, name;
    private Player player;
    private final UUID uuid;
    private String replyPlayer;

    private List<IMission> pins;

    private final Map<Saveable, Object> statisticsMap;

    private BiConsumer<Player, String> chatConsumer;
    private Map<String, String> offlineValues;
    private Map<String, Double> blocksValues;
    private Map<String, Integer> mobKills;
    private List<PassivePerks> passivePerks;
    private List<String> boosters;
    private List<Achievement> achievements;
    private Map<String, Integer> jobValues;
    private Map<String, Integer> trainings;

    private boolean isOnline = false;
    private boolean isExist = true;

    private IMenu currentIMenu;
    private Map<String, Long> cooldowns;

    private List<String> hiddenPlayers;

    public Gamer(Player player) {
        this.uuid = player.getUniqueId();
        this.cooldowns = OfflineValues.getPlayerCooldown(uuid).getCooldowns();
        this.player = player;
        this.gamer = this.player.getName();
        this.isOnline = true;
        this.name = player.getName();
        this.hiddenPlayers = new ArrayList<>();
        if(!preparedRequests.isExist("player", getPlayer().getName())) {
            isExist = false;
            getPlayer().teleport(Prison.SPAWN);
            addCooldown("newPlayer", TimeUnit.HOURS.toMillis(8));
        }
        pins = new ArrayList<>(BattlePass.getPinnedTasks(this));

        statisticsMap = preparedRequests.getAllValues(player.getName(), EStat.values(), isExist);
        setStatistics(EStat.UUID, getPlayer().getUniqueId());
        setStatistics(EStat.FULL_NAME, player.getName());
        offlineValues = Utils.fromStringToMap(getStringStatistics(EStat.OFFLINE_VALUES));
        blocksValues = new HashMap<>();
        Utils.fromStringToMap(getStringStatistics(EStat.BLOCKS_AMOUNT)).forEach((block, amount) -> {
            blocksValues.put(block.toString(), Double.parseDouble(amount.toString()));
        });
        mobKills = new HashMap<>();
        Utils.fromStringToMap(getStringStatistics(EStat.MOB_KILLS)).forEach((mob, amount) -> {
            mobKills.put(mob.toString(), Integer.parseInt(amount.toString()));
        });
        passivePerks = new ArrayList<>();
        Utils.fromStringToList(getStringStatistics(EStat.PERKS)).forEach(perk -> {
            passivePerks.add(EPassivePerk.valueOf(perk).getPerk());
        });
        boosters = new ArrayList<>();
        boosters.addAll(Utils.fromStringToList(getStringStatistics(EStat.BOOSTERS)));
        achievements = new ArrayList<>();
        Utils.fromStringToList(getStringStatistics(EStat.ACHIEVEMENTS)).forEach(achievement ->
                achievements.add(Achievement.valueOf(achievement)));
        jobValues = new HashMap<>();
        Utils.fromStringToMap(getStringStatistics(EStat.JOB)).forEach((data, value) -> {
            jobValues.put(data.toString(), Integer.parseInt(value.toString()));
        });
        trainings = new HashMap<>();
        Utils.fromStringToMap(getStringStatistics(EStat.TRAINER)).forEach((training, value) ->
                trainings.put(training.toString(), Integer.parseInt(value.toString())));
    }

    public void savePlayer() {
        setStatistics(EStat.BLOCKS_AMOUNT, Utils.fromMapToString(blocksValues));
        setStatistics(EStat.OFFLINE_VALUES, Utils.fromMapToString(offlineValues));
        setStatistics(EStat.PERKS, Utils.fromListToString(getPassivePerks().stream().map(passivePerks1 -> passivePerks1.getClass().getSimpleName().toUpperCase()).collect(Collectors.toList())));
        setStatistics(EStat.ACHIEVEMENTS, Utils.fromListToString(getAchievements().stream().map(Enum::name).collect(Collectors.toList())));
        setStatistics(EStat.MOB_KILLS, Utils.fromMapToString(mobKills));
        setStatistics(EStat.BOOSTERS, Utils.fromListToString(boosters));
        setStatistics(EStat.JOB, Utils.fromMapToString(jobValues));
        setStatistics(EStat.TRAINER, Utils.fromMapToString(trainings));
        if(isExist) {
            preparedRequests.saveAllValues("player", getPlayer().getName(), statisticsMap);
        } else {
            preparedRequests.create("player", getPlayer().getName(), statisticsMap);
            isExist = true;
        }
        sendMessage("&aВаши данные сохранены!");
    }

    public boolean isHideEnabled() {
        return !hiddenPlayers.isEmpty();
    }

    public void setChatConsumer(BiConsumer<Player, String> chatConsumer) {
        this.chatConsumer = chatConsumer;
    }

    public BiConsumer<Player, String> getChatConsumer() {
        return chatConsumer;
    }

    public boolean isEndedCooldown(String name) {
        if(cooldowns == null) return false;
        boolean ended = !cooldowns.containsKey(name) || cooldowns.get(name) <= System.currentTimeMillis();
        if (ended) {
            cooldowns.remove(name);
        }
        return ended;
    }

    public void increaseQuestValue(String quest, int value) {
        if (this.player == null) return;
        getOfflineValues().put(quest, String.valueOf(getIntQuestValue(quest) + value));
    }

    public int getIntQuestValue(String quest) {
        if (getOfflineValues().isEmpty()) {
            return 0;
        }
        return Integer.valueOf(getQuestValue(quest));
    }

    public String getQuestValue(String quest) {
        if (getOfflineValues().isEmpty()) {
            return "0";
        }
        return getOfflineValues().getOrDefault(quest, "0");
    }

    public void getNewBattlePass() {
        setStatistics(EStat.BATTLEPASS, true);

        //get old rewards
        int nowBp = getIntStatistics(EStat.BATTLEPASS_LEVEL);
        if (nowBp == 0) return;

        StringBuilder get = new StringBuilder();
        AtomicInteger s = new AtomicInteger();
        for (int i = 1; i <= nowBp; i++) {
            ArrayList<IReward> rews = new ArrayList<>(BattlePass.level_rewards.get(i));
            rews.forEach(reward -> {
                if (!reward.isFree()) {
                    get.append(reward.getName());
                    if (!reward.isStringValue() && reward.getValue() > 0)
                        get.append(" &7(&bx").append(reward.getValue()).append("&7)");
                    get.append(", ");
                    s.getAndIncrement();
                    reward.get(this);
                }
            });
        }
        if (s.get() == 0) return;
        get.delete(get.length() - 2, get.length());
        sendMessage(Utils.colored(EMessage.BPBUY.getMessage().replace("%reward%",
                get.append('.').toString())));
    }

    public void setQuestValue(String quest, String value) {
        getOfflineValues().put(quest, value);
    }

    public void setQuestValue(String quest, int value) {
        getOfflineValues().put(quest, String.valueOf(value));
    }

    public int getLevel() {
        return getIntStatistics(EStat.LEVEL);
    }

    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission("prison." + permission) || getPlayer().hasPermission(permission);
    }

    public void debug(String message) {
        if(!hasPermission("admin")) return;
        getPlayer().sendMessage(Utils.colored("&7[&aDEBUG&7] &a" + message));
    }

    public void bpStatus(IMission mission) {
        if(!isEndedCooldown(mission.hashCode() + "bpMsgCd")) {
            return;
        }
        addCooldown(mission.hashCode() + "bpMsgCd", 3000);
        getPlayer().sendMessage(Utils.colored("&7[&6&lBattlePass&7] &e" + mission.getDescription() + " &7• &c" + mission.getValues().get(getGamer()) + " / " + mission.getValue()));
    }

    public void addItem(String techName) {
        addItem(techName, 1);
    }

    public void addItem(String techName, int amount) {
        addItem(ItemManager.getPrisonItem(techName).getItemStack(amount), "CUSTOM");
    }

    public void addItem(ItemStack itemStack) {
        addItem(itemStack, "CUSTOM");
    }

    public void addItem(ItemStack itemStack, String source) {
        if(getPlayer().getInventory().firstEmpty() == -1) {
            getPlayer().getWorld().dropItem(getPlayer().getLocation(), itemStack);
            return;
        }
        getPlayer().getInventory().addItem(ItemManager.initItem(itemStack, this));
    }


    public void addCooldown(String name, long cooldown) {
        if (this.cooldowns.containsKey(name)) return;
        this.cooldowns.put(name, System.currentTimeMillis() + cooldown);
    }

    public PrisonItem getItemInMainHand() {
        return ItemManager.getPrisonItem(getPlayer().getInventory().getItemInMainHand());
    }

    public PrisonItem getItemInOffHand() {
        return ItemManager.getPrisonItem(getPlayer().getInventory().getItemInOffHand());
    }

    public int getAmount(PrisonItem prisonItem) {
        return getAmount(prisonItem, false);
    }

    public int getAmount(PrisonItem prisonItem, boolean ignoreLvl) {
        ItemStack itemStack = prisonItem.getItemStack();
        final Map<Integer, ? extends ItemStack> ammo = getPlayer().getOpenInventory().getBottomInventory().all(itemStack.getType());
        int found = 0;

        for (ItemStack stack : ammo.values()) {
            PrisonItem prisonItemTarget = ItemManager.getPrisonItem(stack);
            if (prisonItemTarget == null) continue;
            if (ItemUtils.containsNbtTag(stack, "event")) {
                continue;
            }
            String techName = prisonItem.getTechName();
            String vanillaName = prisonItem.getVanillaName();
            if (ignoreLvl && !prisonItemTarget.getVanillaName().equals(vanillaName)) {
                continue;
            }
            if(!ignoreLvl && !prisonItemTarget.getTechName().equals(techName)) {
                continue;
            }
            if (prisonItem.getParameters().contains(ParameterManager.getOwnerParameter())) {
                if (ItemManager.isOwner(this, stack)) found += stack.getAmount();
            } else {
                found += stack.getAmount();
            }
        }
        return found;
    }

    public boolean removeItem(PrisonItem prisonItem, int count, boolean ignoreLvl) {
        int found = getAmount(prisonItem, ignoreLvl);
        if (count > found) {
            return false;
        }
        ItemStack itemStack = prisonItem.getItemStack();
        Material material = itemStack.getType();
        final Map<Integer, ? extends ItemStack> ammo = getPlayer().getOpenInventory().getBottomInventory().all(material);
        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);
            PrisonItem prisonItemTarget = ItemManager.getPrisonItem(stack);
            if (prisonItemTarget == null) continue;
            if (ItemUtils.containsNbtTag(stack, "event")) {
                continue;
            }
            String techName = prisonItem.getTechName();
            String vanillaName = prisonItem.getVanillaName();
            if (ignoreLvl && !prisonItemTarget.getVanillaName().equals(vanillaName)) {
                continue;
            }
            if(!ignoreLvl && !prisonItemTarget.getTechName().equals(techName)) {
                continue;
            }
            boolean consume = false;
            if (prisonItem.getParameters().contains(ParameterManager.getOwnerParameter())) {
                if (ItemManager.isOwner(this, stack)) {
                    consume = true;
                }
            } else {
                consume = true;
            }
            if (consume) {
                int removed = Math.min(count, stack.getAmount());
                count -= removed;
                if (stack.getAmount() == removed) {
                    getPlayer().getInventory().setItem(index, null);
                } else {
                    stack.setAmount(stack.getAmount() - removed);
                }
                if (count <= 0) {
                    break;
                }
            }
        }
        getPlayer().updateInventory();
        return true;
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

    public Map<Saveable, Object> getStatisticsMap() {
        return statisticsMap;
    }

    public Map<String, String> getOfflineValues() {
        return offlineValues;
    }

    public Map<String, Double> getBlocksValues() {
        return blocksValues;
    }

    public Map<String, Integer> getMobKills() {
        return mobKills;
    }

    public List<String> getBoosters() {
        return boosters;
    }

    public int getMobKills(String mobName) {
        return getMobKills().getOrDefault(mobName, 0);
    }

    public void addMobKill(String mobName) {
        getMobKills().put(mobName, getMobKills(mobName) + 1);
    }

    public Map<String, Integer> getJobValues() {
        return jobValues;
    }

    public int getJobValues(String name) {
        return getJobValues().getOrDefault(name, 0);
    }

    public Map<String, Integer> getTrainings() {
        return trainings;
    }

    public int getTrainingLevel(String name) {
        return getTrainings().getOrDefault(name, 0);
    }

    public boolean isExist() {
        return isExist;
    }

    public Map<String, Long> getCooldowns() {
        return cooldowns;
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
        if(!isEndedCooldown(message.hashCode() + "msgCd")) {
            return;
        }
        addCooldown(message.hashCode() + "msgCd", 500);
        getPlayer().sendMessage(Utils.colored("&7[&4&lPrison&7] &r" + message));
    }

    public Privs getPrivilege() {
        return Privs.DEFAULT.getPrivilege(getPlayer());
    }

    public float rebirthSale() {
        return (float)(getValueRebirth(ESkill.SALE) / 100.0);
    }

    public int getValueRebirth(ESkill skill) {
        return skill.getSkill().getValue(getGamer());
    }

    public boolean hasMoney(double money) {
        return getDoubleStatistics(EStat.MONEY) >= money;
    }

    public boolean hasBattlePass() {
        return getBooleanStatistics(EStat.BATTLEPASS);
    }

    public void addExperienceBP(int experience) {
        increaseIntStatistics(EStat.BATTLEPASS_SCORE, experience);
        sendActionbar(Utils.colored("&dПолучено " + experience + " опыта"));
    }

    public List<PassivePerks> getPassivePerks() {
        return passivePerks;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public boolean hasPassivePerk(PassivePerks perk) {
        List<PassivePerks> perks = getPassivePerks();
        if (perks == null) return false;
        for (PassivePerks p : perks) {
            if (p.getSlot() == perk.getSlot()) {
                return true;
            }
        }
        return false;
    }

    public void addPassivePerk(PassivePerks perk) {
        getPassivePerks().add(perk);
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
        if (!getFaction().equals(FactionType.DEFAULT)) {
            teleport(Utils.unserializeLocation(EConfig.CONFIG.getConfig().getString("locations.base_" + getFaction().toString().toLowerCase())));
            getPlayer().closeInventory();
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
        String format = type.name().toLowerCase() + "-" + (global ? "g" : "l") + "-" + multiplier + "-" + time;
        getBoosters().add(format);
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
        PrisonItem prisonItem = ItemManager.getPrisonItem(item);
        if(prisonItem == null) return 0;
        String levelString;
        try {
            levelString = ChatColor.stripColor(item.getItemMeta().getLore().get(0).toLowerCase())
                    .replace("➤ с ", "")
                    .replace(" уровня", "");
            return Integer.parseInt(levelString);
        } catch (Exception e) {
            return 0;
        }
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
        Bukkit.broadcastMessage(Utils.colored(EMessage.BROADCAST_REBITH.getMessage()).replace("%player%", getPlayer().getName()).replaceAll("%rebirth%", ChatColor.YELLOW + getDisplayRebirth()));

        Inventory inventory = getPlayer().getInventory();
        Inventory ec = getPlayer().getEnderChest();

        Arrays.stream(inventory.getContents()).forEach(inventory::remove);
        Arrays.stream(ec.getContents()).forEach(ec::remove);

        getPlayer().teleport(Prison.SPAWN);
        annulateStat();
        setNametag();

        setHearts();
        getPlayer().setFoodLevel(20);
        getPlayer().setExp(0);
        getPlayer().setLevel(0);

        Achievement.JOIN.get(player);
        Achievement.REBIRTH.get(player);

        if(getAmount(ItemManager.getPrisonItem("menu")) == 0) {
            addItem("menu");
        }
        addItem("waxe0_1");
        addItem("steak", 8);
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
        blocksValues.clear();
        Achievement.removeAll(getPlayer());
        resetQuests();
    }

    public boolean needRebirth() {
        return getIntStatistics(EStat.LEVEL) % toRebirth == 0;
    }

    public boolean isOwner(ItemStack itemStack) {
        if(itemStack == null) return false;
        String owner = ItemManager.getOwner(itemStack);
        return getName().equalsIgnoreCase(owner);
    }

    public boolean isOwner() {
        return isOwner(getPlayer().getInventory().getItemInMainHand());
    }

    public void inFraction(FactionType type, boolean isRandom, int cost) {
        FactionType in = type;
        boolean money = true;
        if (isRandom) {
            while (in.equals(FactionType.DEFAULT)) {
                int r = 0;
                try {
                    r = SecureRandom.getInstanceStrong().nextInt(FactionType.values().length);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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
                teleport(Prison.SPAWN);
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
            }.runTaskTimer(Prison.getInstance(), 0L, 20L);
        } else {
            sendMessage(EMessage.ALREADYTP);
        }
    }

    public void depositMoney(double money) {
        if (money <= 0) return;
        if (getStatistics(EStat.MONEY) instanceof Integer) {
            setStatistics(EStat.MONEY, BigDecimal.valueOf(money + getIntStatistics(EStat.MONEY)).setScale(2, RoundingMode.UP).doubleValue());
        } else {
            setStatistics(EStat.MONEY, BigDecimal.valueOf(money + getDoubleStatistics(EStat.MONEY)).setScale(2, RoundingMode.UP).doubleValue());
        }
        if (isOnline()) {
            sendActionbar(Utils.colored("&a+" + BigDecimal.valueOf(money).setScale(2, RoundingMode.UP).doubleValue() + " " + MoneyType.RUBLES.getShortName()));
            double m = getMoney();
            if (m >= 15) Achievement.GET_15.get(getPlayer());
            if (m >= 100) Achievement.GET_100.get(getPlayer());
        }
    }

    private int getVerison() {
        return 1000;
    }

    private boolean is1_15_2() {
        return getVerison() >= 578;
    }

    public void withdrawMoney(double money, int sale) {
        double to_withdraw = (1 - (double) sale / 100) * money;
        setStatistics(EStat.MONEY, getMoney() - to_withdraw);
        if (isOnline()) {
            sendActionbar(Utils.colored("&c-" + money + " " + MoneyType.RUBLES.getShortName()));
            if (getTrainingLevel(TypeTrainings.CASHBACK.name()) > 0) {
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

    public void addCurrentBlocks(String block, int data, double amount) {
        String blockString = block + "-" + data;
        blocksValues.put(blockString, getCurrentBlocks(block, data) + amount);
    }

    public int getCurrentBlocks(String block, int data) {
        String blockString = block + "-" + data;
        return blocksValues.getOrDefault(blockString, 0D).intValue();
    }

    public int getCurrentBlocks(String block) {
        return getCurrentBlocks(block, 0);
    }

    public double getBoosterBlocks() {
        double boost = getDoubleStatistics(EStat.BOOSTERBLOCKS);
        if (Prison.gBlocks.isActive()) boost += Prison.gBlocks.getMultiplier() - 1.0;
        if (isActiveLocalBlocks()) boost += Utils.getlBlocksMultiplier().get(getGamer()) - 1.0;
        Object b = getPrivilege().getValue(new BoosterBlocks());
        if (b != null) boost += Double.parseDouble(b.toString()) - 1.0;
        if (hasPassivePerk(new BBlocksFirst())) boost += 0.1;
        if (hasPassivePerk(new BBlocksSecond())) boost += 0.2;
        return BigDecimal.valueOf(boost).setScale(2, RoundingMode.UP).doubleValue();
    }

    public double getBoosterMoney() {
        double boost = getDoubleStatistics(EStat.BOOSTERMONEY);
        if (Prison.gMoney.isActive()) boost += Prison.gMoney.getMultiplier() - 1.0;
        if (isActiveLocalMoney()) boost += Utils.getlMoneyMultiplier().get(getGamer()) - 1.0;
        Object b = getPrivilege().getValue(new BoosterMoney());
        if (b != null) boost += Double.parseDouble(b.toString()) - 1.0;
        if (hasPassivePerk(new BMoneyFirst())) boost += 0.1;
        if (hasPassivePerk(new BBMoneySecond())) boost += 0.2;
        return BigDecimal.valueOf(boost).setScale(2, RoundingMode.UP).doubleValue();
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
        return (String)getStatistics(stat);
    }

    public double getDoubleStatistics(EStat stat) {
        return (double) getStatistics(stat);
    }

    public boolean getBooleanStatistics(EStat stat) {
        return Boolean.parseBoolean(getStatistics(stat).toString());
    }

    public Object getStatistics(EStat statistic) {
        if (isOnline()) {
            return statisticsMap.get(statistic);
        } else {
            return getStatisticsFromConfig(statistic, Bukkit.getOfflinePlayer(getUUID()).getName());
        }
    }

    public void setStatistics(EStat statistic, Object value) {
        if(statistic.getStatType() == StatType.DOUBLE) {
            value = Double.parseDouble(value.toString());
        } else if(statistic.getStatType() == StatType.INTEGER) {
            value = Integer.parseInt(value.toString());
        } else if(statistic.getStatType() == StatType.BOOLEAN) {
            value = Boolean.parseBoolean(value.toString());
        }
        if (isOnline()) {
            statisticsMap.put(statistic, value);
        } else {
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

    public void sendFishingTitle(String main) {
        getPlayer().sendTitle(Utils.colored(main), Utils.colored(ChatColor.YELLOW + "КРУТИ КАТУШКУ!"), 0, 2, 0);
    }

    public void sendFishRewardTitle(EFishType type) {
        sendTitle(type.getName(), type.getRewardName());
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
        try {
            getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.colored(msg)));
        } catch (Exception e) { }
    }
}
