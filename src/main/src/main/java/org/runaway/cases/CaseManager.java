package org.runaway.cases;

import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.rewards.LootItem;
import org.runaway.rewards.MoneyReward;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CaseManager {
    protected static Map<String, CaseRefactored> caseMap = new HashMap<>();

    public static void initAllCases() {
        SimpleCase.builder()
                .minLevel(1)
                .name("&7Обычный ключ")
                .rare(PrisonItem.Rare.RARE)
                .techName("defaultKey")
                .rollTimes(10)
                .stopAfter(16)
                .rewardItems(Arrays.asList(
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("star")).minAmount(1).maxAmount(2).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("steak")).minAmount(2).maxAmount(4).probability(90).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("apple")).minAmount(2).maxAmount(8).probability(140).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("arrow")).minAmount(1).maxAmount(3).probability(35).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("gapple")).amount(2).probability(6).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("podvalPass")).probability(3).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("ledPass")).probability(1).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("gladiatorPass")).probability(1).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wspade0_1")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wspade2_3")).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wpickaxe0_1")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wpickaxe2_3")).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("bow0_1")).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("swpickaxe0_1")).probability(2).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wsword0_1")).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lhelmet0_1")).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lchestplate0_1")).probability(6).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lleggings0_1")).probability(6).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lboots0_1")).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("fishing0_1")).probability(6).build(),
                        MoneyReward.builder().minAmount(1).maxAmount(3.5).probability(120).build()))
                .build().init();
    }

    public static void initAllKeys() {
        SimpleCase.builder()
                .minLevel(1)
                .name("&7Обычный ключ")
                .rare(PrisonItem.Rare.RARE)
                .techName("defaultKey").build().init();
    }

    public static CaseRefactored getCase(String techName) {
        return caseMap.get(techName);
    }

    protected static void addCase(CaseRefactored caseRefactored) {
        caseMap.put(caseRefactored.getTechName(), caseRefactored);
    }
}
