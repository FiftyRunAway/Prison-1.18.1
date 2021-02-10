package org.runaway.cases;

import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.rewards.LootItem;
import org.runaway.rewards.MoneyReward;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CaseManager {
    protected static Map<String, CaseRefactored> caseMap = new HashMap();

    public static void initAllCases() {
        SimpleCase.builder()
                .minLevel(1)
                .name("&7Обычный ключ")
                .rare(PrisonItem.Rare.RARE)
                .techName("defaultKey")
                .rollTimes(5)
                .stopAfter(15)
                .rewardItems(Arrays.asList(LootItem.builder().prisonItem(ItemManager.getPrisonItem("star")).amount(2).probability(5).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("steak")).probability(15).build(),
                        MoneyReward.builder().amount(2).probability(10).build()))
                .build().init();
    }
    public static CaseRefactored getCase(String techName) {
        return caseMap.get(techName);
    }

    protected static void addCase(CaseRefactored caseRefactored) {
        caseMap.put(caseRefactored.getTechName(), caseRefactored);
    }
}
