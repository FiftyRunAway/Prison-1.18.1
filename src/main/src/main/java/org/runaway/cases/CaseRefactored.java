package org.runaway.cases;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.menu.IMenu;
import org.runaway.menu.MenuAnimation;
import org.runaway.menu.type.StandardMenu;
import org.runaway.rewards.IReward;
import org.runaway.tasks.Cancellable;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface CaseRefactored {
    List<IReward> getRewardItems();

    List<IReward> getOriginalRewards();

    String getTechName();

    String getName();

    ItemStack getCaseItem();

    int getRollTimes();

    int getMinLevel();

    int getStopAfter();

    Random getRandom();

    default IReward generateReward() {
        return getRewardItems().get(getRandom().nextInt(getRewardItems().size()));
    }

    default void open(Gamer gamer) {
        gamer.sendMessage("&aГенерация предметов...");
        gamer.increaseQuestValue("caseOpen", 1);
        gamer.increaseQuestValue(getTechName() + "Open", 1);
        List<IReward> rewards = new ArrayList<>();
        int stopAfter = getStopAfter();
        generateRewards(rewards, getRollTimes() + stopAfter);
        IReward finalReward = generateReward();
        rewards.set(getRollTimes() + stopAfter - 4, finalReward);
        IMenu caseMenu = StandardMenu.create(3, getName());
        caseMenu.setItem(4, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("&aНаграда").durability(4).build());
        caseMenu.setItem(22, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("&aНаграда").durability(4).build());
        for (int i = 0; i < 27; i++) {
            if (i < 10 || i > 16) {
                if (caseMenu.getButton(i) == null)
                    caseMenu.setItem(i, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&r").durability(5).build());
            }
        }
        for (int i = 1; i < 8; i++) {
            caseMenu.setItem(i + 9, rewards.get(i).getItemStack());
        }
        AtomicBoolean rollEnded = new AtomicBoolean(false);
        AtomicBoolean closedInventory = new AtomicBoolean(false);
        AtomicInteger itemsRolled = new AtomicInteger(9);
        AtomicInteger times = new AtomicInteger(0);
        AtomicInteger stopValue = new AtomicInteger(1);
        AtomicInteger afterStop = new AtomicInteger(0);

        Cancellable cancellable = new SyncRepeatTask(() -> {
            if (itemsRolled.get() < getRollTimes()) {
                rollItems(caseMenu, rewards, itemsRolled);
            } else {
                times.incrementAndGet();
                if (times.get() >= stopValue.get() && itemsRolled.get() < getRollTimes() + stopAfter) {
                    rollItems(caseMenu, rewards, itemsRolled);
                    times.set(0);
                    afterStop.incrementAndGet();
                    if (afterStop.get() > 5) {
                        stopValue.incrementAndGet();
                    }
                } else if (itemsRolled.get() >= getRollTimes() + stopAfter) {
                    if (!rollEnded.get()) {
                        rollEnded.set(true);
                        new SyncTask(() -> {
                            for (int i = 0; i < 27; i++) {
                                if (i != 13) caseMenu.setItem(i, new ItemStack(Material.NETHER_STAR));
                            }
                            giveReward(gamer, finalReward);
                            new SyncTask(() -> {
                                if (!closedInventory.get()) {
                                    if(gamer.getCurrentIMenu() != null) {
                                        gamer.getPlayer().closeInventory();
                                    }
                                }
                            }, 40);
                        }, 30);
                        return;
                    }
                }
            }
        }, 1);
        caseMenu.setCloseListener(closeEvent -> {
            if (!rollEnded.get()) {
                giveReward(gamer, finalReward);
                rollEnded.set(true);
            }
            closedInventory.set(true);
            cancellable.stop();
        });
        caseMenu.open(gamer);
    }

    default void rollItems(IMenu caseMenu, List<IReward> caseRewards, AtomicInteger itemsRolled) {
        for (int i = 1; i < 8; i++) {
            caseMenu.setItem(i + 9, caseMenu.getButton(i + 10).getItem());
        }
        IReward nextReward = caseRewards.get(itemsRolled.get());
        caseMenu.setItem(7 + 9, nextReward.getItemStack().clone());
        itemsRolled.incrementAndGet();
    }

    default void giveReward(Gamer gamer, IReward finalReward) {
        if (!gamer.isEndedCooldown("rewarded")) {
            gamer.getPlayer().kickPlayer(ChatColor.RED + "Перезайдите.");
            return;
        }
        gamer.addCooldown("rewarded", 50);
        finalReward.giveReward(gamer);
        gamer.sendMessage("&aНаграда выдана.");
        return;
    }

    default void generateRewards(List<IReward> caseRewards, int rewards) {
        for (int i = 0; i < rewards; i++) {
            caseRewards.add(generateRollItems());
        }
    }

    default IReward generateRollItems() {
        Random random = getRandom();
        return getRewardItems().get(random.nextInt(getRewardItems().size()));
    }

    default IMenu getChancesMenu(Gamer gamer) {
        List<IReward> originalRewards = getOriginalRewards();
        IMenu standardMenu = StandardMenu.create((originalRewards.size() / 9) + 1, getName() + " Шансы");
        for (int i = 0; i < originalRewards.size(); i++) {
            ItemStack rewardItem = originalRewards.get(i).getItemStack();
            ItemStack itemChance = rewardItem.clone(); //
            double chance = ((double) originalRewards.get(i).getProbability() / getRewardItems().size()) * 100;
            ItemUtils.addLore(itemChance, "&r", "&bШанс выпадения: " + String.format("§a%.2f", chance) + "%");
            standardMenu.setItem(i, itemChance);
        }
        standardMenu.open(gamer);
        return standardMenu;
    }
}
