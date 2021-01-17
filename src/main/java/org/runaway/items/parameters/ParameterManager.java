package org.runaway.items.parameters;

import lombok.Getter;
import org.runaway.items.PrisonItem;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.formatters.NbtFormatter;

import java.util.List;

@Getter
public class ParameterManager {
    private String ownerString, nodropString, upgradableString, stattrakPlayersString, stattrakMobsString, runesAmountString, runeInfoString, categoryInfoString, rareInfoString;
    Parameter ownerParameter, nodropParameter, upgradableParameter, stattrakPlayerParameter, stattrakMobsParameter;

    public ParameterManager() {
        initValues();
        ownerParameter = DefaultParameter.builder().loreString(getOwnerString()).nbtString("owner").priority(30).build();
        nodropParameter = DefaultParameter.builder().loreString(getNodropString()).nbtString("nodrop").defaultNbtFormatter(NbtFormatter.builder().finalValue(1).build()).priority(10).build();
        upgradableParameter = DefaultParameter.builder().loreString(getUpgradableString()).priority(5).build();
        stattrakMobsParameter = DefaultParameter.builder().loreString(getStattrakPlayersString()).nbtString("plKills")
                .defaultLoreFormatter(LoreFormatter.builder().finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(0).build())
                .priority(14).build();
        stattrakMobsParameter = DefaultParameter.builder().loreString(getStattrakMobsString()).nbtString("mobKills")
                .defaultLoreFormatter(LoreFormatter.builder().finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().finalValue(0).build())
                .priority(15).build();
    }

    private void initValues() {
        ownerString = "&5☬ &7Владелец: &d";
        nodropString = "&a&l✪ &aНе выпадает";
        upgradableString = "&a✔ Предмет можно улучшить";
        stattrakPlayersString = "&4☠ &7Убито игроков: &c";
        stattrakMobsString = "&4☠ &7Убито мобов: &c";
        runesAmountString = "&5⚝ &7Вмещается рун: &d%d";
        runeInfoString = "&5⚝ &7&n%d&7 руна: ";
        rareInfoString = "&e★ &7Тип редкости: %s";
        categoryInfoString = "&1⚒ &7Категория: %s";
    }

    public Parameter getCategoryParameter(PrisonItem.Category category) {
        return DefaultParameter.builder().loreString(getCategoryInfoString()).defaultLoreFormatter(LoreFormatter.builder().finalValue(category.getName()).build()).priority(24).build();
    }

    public Parameter getRareParameter(PrisonItem.Rare rare) {
        return DefaultParameter.builder().loreString(getRareInfoString()).defaultLoreFormatter(LoreFormatter.builder().finalValue(rare.getName()).build()).priority(25).build();
    }

    public Parameter getRunesParameter(int amount, List<String> defaultRunes) {
        return new RunesParameter("rune%d", 15, amount, defaultRunes);
    }

    public Parameter getRunesParameter(int amount) {
        return getRunesParameter(amount, null);
    }
}
