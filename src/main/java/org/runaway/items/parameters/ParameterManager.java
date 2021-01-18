package org.runaway.items.parameters;

import lombok.Getter;
import org.runaway.items.PrisonItem;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.formatters.NameFormatter;
import org.runaway.items.formatters.NbtFormatter;

import java.util.List;

@Getter
public class ParameterManager {
    private String ownerString, nodropString, upgradableString, stattrakPlayersString, stattrakMobsString, runesAmountString,
            runeInfoString, categoryInfoString, rareInfoString, minLevelString, stattrakBlocksString;
    Parameter ownerParameter, nodropParameter, upgradableParameter, stattrakPlayerParameter, stattrakMobsParameter,
            stattrakBlocksParameter, runesAmountParameter, runesInfoParameter;

    public ParameterManager() {
        initValues();
        ownerParameter = DefaultParameter.builder().loreString(getOwnerString()).nbtString("owner").priority(30).build();
        nodropParameter = DefaultParameter.builder().loreString(getNodropString()).nbtString("nodrop").defaultNbtFormatter(NbtFormatter.builder().finalValue(1).build()).priority(10).build();
        upgradableParameter = DefaultParameter.builder().loreString(getUpgradableString()).priority(5).build();
        stattrakMobsParameter = DefaultParameter.builder().loreString(getStattrakPlayersString()).nbtString("plKills")
                .preSpace(true)
                .defaultLoreFormatter(LoreFormatter.builder().finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(0).build())
                .priority(14).build();
        stattrakMobsParameter = DefaultParameter.builder().loreString(getStattrakMobsString()).nbtString("mobKills")
                .defaultLoreFormatter(LoreFormatter.builder().finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(0).build())
                .priority(15).build();
        stattrakBlocksParameter = DefaultParameter.builder().loreString(getStattrakBlocksString()).nbtString("stBlocks")
                .preSpace(true)
                .defaultLoreFormatter(LoreFormatter.builder().finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(0).build())
                .priority(15).build();
        runesInfoParameter = new RunesParameter("rune%d");
        runesAmountParameter = DefaultParameter.builder().nbtString("runesAmount").priority(5).build();
    }

    private void initValues() {
        ownerString = "&5☬ &7Владелец: &d";
        nodropString = "&a&l✪ &aНе выпадает";
        upgradableString = "&a✔ Можно улучшить";
        stattrakPlayersString = "&4☠ &7Убито игроков: &c";
        stattrakMobsString = "&4☠ &7Убито мобов: &c";
        runesAmountString = "&5⚝ &7Вмещается рун: &d%d";
        runeInfoString = "&5⚝ &7&n%d&7 руна: %s";
        rareInfoString = "&e★ &7Редкость: ";
        categoryInfoString = "&1⚒ &7Категория: ";
        minLevelString = "&7➤ С &6%d &7уровня";
        stattrakBlocksString = "&c✄ &4Сломано блоков: &c";
    }

    public Parameter getCategoryParameter(PrisonItem.Category category) {
        return DefaultParameter.builder().loreString(getCategoryInfoString()).defaultLoreFormatter(LoreFormatter.builder().finalValue(category.getName()).build()).priority(24).build();
    }

    public Parameter getMinLevelParameter(int minLevel) {
        return DefaultParameter.builder().loreString(getMinLevelString()).nbtString("minLevel")
                .defaultLoreFormatter(LoreFormatter.builder().replaceObjects(new Object[]{minLevel}).build())
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(minLevel).build())
                .priority(3).build();
    }

    public Parameter getItemLevelParameter(int itemLevel) {
        return DefaultParameter.builder().nbtString("itemLevel")
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(itemLevel).build())
                .defaultNameFormatter(NameFormatter.builder().finalValue(" &6" + itemLevel + " LvL").build())
                .priority(0).build();
    }

    public Parameter getRareParameter(PrisonItem.Rare rare) {
        return DefaultParameter.builder().preSpace(true).loreString(getRareInfoString()).defaultLoreFormatter(LoreFormatter.builder().finalValue(rare.getName()).build()).priority(25).build();
    }

    public Parameter getRunesParameter(int amount, List<String> defaultRunes) {
        return new RunesParameter("rune%d", 20, amount, defaultRunes);
    }

    public Parameter getRunesParameter(int amount) {
        return getRunesParameter(amount, null);
    }
}
