package org.runaway.items.parameters;

import lombok.Getter;
import org.runaway.enums.StatType;
import org.runaway.items.PrisonItem;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.formatters.NameFormatter;
import org.runaway.items.formatters.NbtFormatter;

import java.util.List;

public class ParameterManager {
    @Getter
    private static String ownerString, nodropString, upgradableString, stattrakPlayersString, stattrakMobsString, runesAmountString,
            runeInfoString, categoryInfoString, rareInfoString, minLevelString;
    @Getter
    private static Parameter ownerParameter, nodropParameter, upgradableParameter, stattrakPlayerParameter, stattrakMobsParameter,
            //stattrakBlocksParameter,
            runesAmountParameter, runesInfoParameter, minLevelParameter;

    public static void init() {
        initValues();
        ownerParameter = DefaultParameter.builder().loreString(getOwnerString()).nbtString("owner")
                .priority(30)
                .mutable(true)
                .preSpace(true)
                .statType(StatType.STRING)
                .build();
        nodropParameter = DefaultParameter.builder()
                .loreString(getNodropString())
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("nodrop").finalValue(true).build())
                .statType(StatType.BOOLEAN)
                .priority(10)
                .preSpace(true)
                .build();
        upgradableParameter = DefaultParameter.builder()
                .loreString(getUpgradableString())
                .statType(StatType.BOOLEAN)
                .priority(5)
                .preSpace(true)
                .build();
        stattrakPlayerParameter = DefaultParameter.builder()
                .preSpace(true)
                .statType(StatType.INTEGER)
                .mutable(true)
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getStattrakPlayersString()).finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("plKills").finalValue(0).build())
                .priority(14).build();
        stattrakMobsParameter = DefaultParameter.builder()
                .mutable(true)
                .statType(StatType.INTEGER)
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getStattrakMobsString()).finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("mobKills").finalValue(0).build())
                .priority(15).build();
        /*stattrakBlocksParameter = DefaultParameter.builder()
                .mutable(true)
                .statType(StatType.DOUBLE)
                .preSpace(true)
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getStattrakBlocksString()).finalValue(0).build())
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("stBlocks").finalValue(0D).build())
                .priority(15).build(); */
        runesInfoParameter = new RunesParameter("rune%d");
        runesAmountParameter = DefaultParameter.builder()
                .nbtString("runesAmount")
                .loreString(getRunesAmountString())
                .statType(StatType.INTEGER)
                .preSpace(true)
                .priority(20)
                .build();
        minLevelParameter = DefaultParameter.builder()
                .statType(StatType.INTEGER)
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("minLevel").build())
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getMinLevelString()).finalValue(0).build())
                .priority(21)
                .preSpace(true)
                .build();
    }

    private static void initValues() {
        ownerString = "&5☬ &7Владелец: &d";
        nodropString = "&a&l✪ &aНе выпадает";
        upgradableString = "&a✔ Можно улучшить";
        stattrakPlayersString = "&4☠ &7Убито игроков: &c";
        stattrakMobsString = "&4☠ &7Убито мобов: &c";
        runesAmountString = "&5⚝ &7Вмещается рун: &d";
        runeInfoString = " &5• &7&n%d&7 руна: %s";
        rareInfoString = "&e★ &7Редкость: ";
        categoryInfoString = "&1⚒ &7Категория: ";
        minLevelString = "&7➤ С &6%d &7уровня";
        //stattrakBlocksString = "&c✄ &4Сломано блоков: &c";
    }

    public static Parameter getCategoryParameter(PrisonItem.Category category) {
        return DefaultParameter.builder()
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getCategoryInfoString()).finalValue(category.getName()).build())
                .priority(24)
                .preSpace(true)
                .build();
    }

    public static Parameter getMinLevelParameter(int minLevel) {
        return DefaultParameter.builder()
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getMinLevelString()).replaceObjects(new Object[]{minLevel}).build())
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("minLevel").finalValue(minLevel).build())
                .priority(3)
                .build();
    }

    public static Parameter getItemLevelParameter(int itemLevel) {
        return DefaultParameter.builder()
                .defaultNbtFormatter(NbtFormatter.builder().nbtString("itemLevel").finalValue(itemLevel).build())
                .defaultNameFormatter(NameFormatter.builder().finalValue(" &6" + itemLevel + " LvL").build())
                .priority(0)
                .build();
    }

    public static Parameter getRareParameter(PrisonItem.Rare rare) {
        return DefaultParameter.builder()
                .preSpace(true)
                .defaultLoreFormatter(LoreFormatter.builder().loreString(getRareInfoString()).finalValue(rare.getName()).build())
                .priority(25)
                .build();
    }

    public static Parameter getRunesParameter(int amount, List<String> defaultRunes) {
        return new RunesParameter("rune%d", 20, amount, defaultRunes);
    }

    public static Parameter getRunesParameter(int amount) {
        return getRunesParameter(amount, null);
    }
}
