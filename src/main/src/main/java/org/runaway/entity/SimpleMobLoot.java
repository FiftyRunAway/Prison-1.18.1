package org.runaway.entity;

import lombok.Builder;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.runaway.Gamer;
import org.runaway.rewards.LootItem;
import org.runaway.PrivateHolo;
import org.runaway.items.NumberedPrisonItem;
import org.runaway.items.PrisonItem;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter @Builder
public class SimpleMobLoot implements MobLoot {
    List<LootItem> lootItems;
    double minMoney, maxMoney;

    @Override
    public void drop(Map<Gamer, Double> damageList, Location location, Attributable attributable) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        double money = threadLocalRandom.nextDouble(minMoney, maxMoney);
        double needToKill = (100.0 / (damageList.size() > 0 ? damageList.size() : (damageList.size() + 1))) * 0.7;
        damageList.forEach((gamer, damagePercent) -> {
            if (money != 0) {
                gamer.depositMoney(money * damagePercent, true);
            }
            new PrivateHolo(gamer.getPlayer(), location, PrivateHolo.StandType.MONEY, money * damagePercent);
            if (damagePercent * 100 > needToKill) {
                gamer.addMobKill(attributable.getTechName());
                gamer.increaseQuestValue("mobKills", 1);
                if(attributable.isBoss()) {
                    gamer.increaseQuestValue("bossKills", 1);
                }
                //gamer.debug("kill " + needToKill);
            }
        });
        if (lootItems != null) {
            List<NumberedPrisonItem> numberedItems = new ArrayList<>();
            Map<Gamer, List<String>> itemsString = new HashMap<>();
            lootItems.forEach(lootItem -> {
                int amount = lootItem.getAmount() == 0 ? threadLocalRandom.nextInt(lootItem.getMinAmount(), lootItem.getMaxAmount()) : lootItem.getAmount();
                PrisonItem prisonItem = lootItem.getPrisonItem();
                if (threadLocalRandom.nextFloat() <= lootItem.getChance()) {
                    numberedItems.add(new NumberedPrisonItem(prisonItem, amount));
                    damageList.forEach((gamer, damagePercent) -> {
                        double damageChance = damagePercent;
                        if (threadLocalRandom.nextFloat() <= damageChance) {
                            int result = amount;
                            if (amount > damageList.size()) {
                                result = (int) (amount * damagePercent);
                                if (result == 0) result = 1;
                            }
                            double amountBooster = 1;
                            {
                                //ITEMS BOOSTER
                            }
                            int finalAmount = (int) (result * amountBooster);
                            if (finalAmount == 1 && amountBooster > 1) {
                                if (threadLocalRandom.nextFloat() <= 0.5) {
                                    finalAmount = 2;
                                }
                            }
                            gamer.addItem(prisonItem.getItemStack(finalAmount), "MOB_LOOT");
                            if (!itemsString.containsKey(gamer)) {
                                itemsString.put(gamer, new ArrayList<>());
                            }
                            itemsString.get(gamer).add(prisonItem.getName() + "&a x" + finalAmount + (amountBooster > 1 ? " &7[&5x" + amountBooster + "&7]" : ""));
                        }
                    });
                }
            });
            itemsString.forEach(((gamer, strings) -> {
                gamer.sendMessage("Вам выпало: " + String.join(", ", strings));
            }));
        }
        ComponentBuilder killsBuilder = new ComponentBuilder("§aНападавшие:");
        damageList.forEach((gamer, damagePercent) -> {
            killsBuilder.append(Utils.colored("\n  &7• " + "&c" + gamer.getName() + " &4" + (int) (damagePercent.doubleValue() * 100) + "%"));
        });
        BaseComponent[] bc = new ComponentBuilder(Utils.colored("&7[&cИнформация&7] "))
                .append(Utils.colored(attributable.isBoss() ? "&4Босс " + attributable.getName() + " &4был повержен." : "&cСущество " + attributable.getName() + " &cубито."))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, killsBuilder.create()))
                .create();
        if (attributable.isBoss()) {
            GamerManager.getGamers().values().forEach(gamer -> {
                if (gamer.getIntQuestValue("bsPT") == 1 && !damageList.containsKey(gamer)) {
                    return;
                }
                try {
                    gamer.getPlayer().spigot().sendMessage(bc);
                } catch (Exception ignored) { }
            });
        } else {
            damageList.keySet().forEach(gamer -> {
                try {
                    gamer.getPlayer().spigot().sendMessage(bc);
                } catch (Exception ignored) { }
            });
        }
    }

    @Override
    public List<String> getLootLore() {
        List<String> lore = new ArrayList<>();
        for (LootItem lootItem : getLootItems()) {
            int level = lootItem.getPrisonItem().getItemStack().getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            lore.add("  &7• " + lootItem.getPrisonItem().getName() + " &6x" +
                    (lootItem.getMinAmount() == lootItem.getMaxAmount() ? lootItem.getMinAmount() : lootItem.getMinAmount() + "-" + lootItem.getMaxAmount())
                    + " &5" + String.format("%.2f", (lootItem.getChance() * 100)) + "%"
                    + (level > 0 ? " &7[&2Защита " + level + "&7]" : ""));
        }
        if (getMinMoney() > 0) {
            lore.add("  &7• &a" + getMinMoney() + "-" + getMaxMoney() + "$");
        }
        return lore;
    }
}
