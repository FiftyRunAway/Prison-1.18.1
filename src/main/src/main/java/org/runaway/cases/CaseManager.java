package org.runaway.cases;

import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.rewards.LootItem;
import org.runaway.rewards.MoneyReward;
import org.runaway.rewards.RuneReward;
import org.runaway.runes.armor.*;
import org.runaway.runes.armor.boots.AntiGravityRune;
import org.runaway.runes.armor.boots.GearsRune;
import org.runaway.runes.armor.boots.SpringsRune;
import org.runaway.runes.pickaxe.BlastRune;
import org.runaway.runes.pickaxe.SpeedRune;
import org.runaway.runes.sword.*;

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
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("star")).minAmount(1).maxAmount(2).probability(4).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("steak")).minAmount(2).maxAmount(4).probability(200).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("apple")).minAmount(2).maxAmount(8).probability(200).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("arrow")).minAmount(1).maxAmount(3).probability(50).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("gapple")).amount(2).probability(8).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("podvalPass")).probability(3).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("ledPass")).probability(1).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("gladiatorPass")).probability(1).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wspade0_1")).probability(9).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wspade2_3")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wpickaxe0_1")).probability(9).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wpickaxe2_3")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("bow0_1")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("swpickaxe0_1")).probability(2).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("wsword0_1")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lhelmet0_1")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lchestplate0_1")).probability(8).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lleggings0_1")).probability(8).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("lboots0_1")).probability(7).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("fishing0_1")).probability(8).build(),
                        MoneyReward.builder().minAmount(1).maxAmount(3.5).probability(250).build()))
                .build().init();

        SimpleCase.builder()
                .minLevel(1)
                .name("&6Легендарный ключ")
                .rare(PrisonItem.Rare.SUPER_RARE)
                .techName("legendaryKey")
                .rollTimes(10)
                .stopAfter(16)
                .rewardItems(Arrays.asList(
                        RuneReward.builder().rune(new BlastRune()).probability(10).build(),
                        RuneReward.builder().rune(new AntiGravityRune()).probability(10).build(),
                        RuneReward.builder().rune(new GearsRune()).probability(10).build(),
                        RuneReward.builder().rune(new SpringsRune()).probability(10).build(),
                        RuneReward.builder().rune(new BurnShieldRune()).probability(10).build(),
                        RuneReward.builder().rune(new CactusRune()).probability(10).build(),
                        RuneReward.builder().rune(new DrunkRune()).probability(10).build(),
                        RuneReward.builder().rune(new EnlightenedRune()).probability(10).build(),
                        RuneReward.builder().rune(new FortifyRune()).probability(10).build(),
                        RuneReward.builder().rune(new FreezeRune()).probability(10).build(),
                        RuneReward.builder().rune(new HulkRune()).probability(10).build(),
                        RuneReward.builder().rune(new InsomniaRune()).probability(10).build(),
                        RuneReward.builder().rune(new MoltenRune()).probability(10).build(),
                        RuneReward.builder().rune(new NinjaRune()).probability(10).build(),
                        RuneReward.builder().rune(new PainGiverRune()).probability(10).build(),
                        RuneReward.builder().rune(new RecoverRune()).probability(10).build(),
                        RuneReward.builder().rune(new SaviorRune()).probability(10).build(),
                        RuneReward.builder().rune(new SmokeBombRune()).probability(10).build(),
                        RuneReward.builder().rune(new ValorRune()).probability(10).build(),
                        RuneReward.builder().rune(new VoodooRune()).probability(10).build(),
                        RuneReward.builder().rune(new SpeedRune()).probability(10).build(),
                        RuneReward.builder().rune(new BlindnessRune()).probability(10).build(),
                        RuneReward.builder().rune(new ConfusionRune()).probability(10).build(),
                        RuneReward.builder().rune(new DoubleDamageRune()).probability(10).build(),
                        RuneReward.builder().rune(new ExecutionRune()).probability(10).build(),
                        RuneReward.builder().rune(new LifeStealRune()).probability(10).build(),
                        RuneReward.builder().rune(new NutritionRune()).probability(10).build(),
                        RuneReward.builder().rune(new ObliterateRune()).probability(10).build(),
                        RuneReward.builder().rune(new ParalyzeRune()).probability(10).build(),
                        RuneReward.builder().rune(new SlowMoRune()).probability(10).build(),
                        RuneReward.builder().rune(new SnareRune()).probability(10).build(),
                        RuneReward.builder().rune(new TrapRune()).probability(10).build(),
                        RuneReward.builder().rune(new VampireRune()).probability(10).build(),
                        RuneReward.builder().rune(new ViperRune()).probability(10).build(),
                        RuneReward.builder().rune(new WitherRune()).probability(10).build()
                        ))
                .build().init();
    }

    public static void initAllKeys() {
        SimpleCase.builder()
                .minLevel(1)
                .name("&7Обычный ключ")
                .rare(PrisonItem.Rare.RARE)
                .techName("defaultKey").build().init();
        SimpleCase.builder()
                .minLevel(1)
                .name("&6Легендарный ключ")
                .rare(PrisonItem.Rare.SUPER_RARE)
                .techName("legendaryKey").build().init();
    }

    public static CaseRefactored getCase(String techName) {
        return caseMap.get(techName);
    }

    protected static void addCase(CaseRefactored caseRefactored) {
        caseMap.put(caseRefactored.getTechName(), caseRefactored);
    }
}
