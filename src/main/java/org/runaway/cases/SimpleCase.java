package org.runaway.cases;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.requirements.BlocksRequire;
import org.runaway.requirements.LocalizedBlock;
import org.runaway.requirements.MoneyRequire;
import org.runaway.requirements.RequireList;
import org.runaway.rewards.IReward;
import org.runaway.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Getter @Builder
public class SimpleCase implements CaseRefactored {
    @Setter
    List<IReward> rewardItems, originalRewards;
    String name, techName;
    int rollTimes, minLevel, stopAfter;
    Random random;
    PrisonItem.Rare rare;
    ItemStack caseItem;

    public void init() {
        this.random = new Random();
        setOriginalRewards(getRewardItems());
        rewardItems = new ArrayList<>();
        getOriginalRewards().forEach(iReward -> {
            for(int i = 0;i < iReward.getProbability();i++) {
                getRewardItems().add(iReward);
            }
        });
        CaseManager.addCase(this);
        ItemStack caseItem = new ItemBuilder(Material.GHAST_TEAR).name(getName()).build();
        PrisonItem prisonItem = PrisonItem.builder()
                .vanillaName(getTechName())
                .vanillaItem(caseItem)
                .category(PrisonItem.Category.KEYS)
                .parameters(Arrays.asList(
                        ParameterManager.getMinLevelParameter(getMinLevel()), //мин лвл для использования предмета
                        ParameterManager.getRareParameter(getRare()), //редкость предмета
                        ParameterManager.getCategoryParameter(PrisonItem.Category.KEYS))).build();
        ItemManager.addPrisonItem(prisonItem);
    }
}
