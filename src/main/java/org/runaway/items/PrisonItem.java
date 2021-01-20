package org.runaway.items;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.items.parameters.Parameter;
import org.runaway.runes.Rune;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter @Builder
public class PrisonItem {
    @Setter
    private String vanillaName, techName, name;
    private Material material;
    private short data;
    private Consumer<Gamer> consumerOnClick;
    private Category category;
    private Rare rare;
    private int runesAmount, itemLevel;
    private List<String> defaultRunes;
    @Setter
    private List<Parameter> parameters, mutableParameters;
    @Setter
    private ItemStack vanillaItem, itemStack;

    public ItemStack getItemStack() {
        return this.itemStack.clone();
    }

    public ItemStack getItemStack(int amount) {
        ItemStack resultItem = this.itemStack.clone();
        resultItem.setAmount(amount);
        return resultItem;
    }

    public enum Category {
        WEAPON("&cОружие", new ItemStack(Material.IRON_SWORD)),
        TOOLS("&9Инструменты", new ItemStack(Material.DIAMOND_PICKAXE)),
        FOOD("&aЕда", new ItemStack(Material.APPLE)),
        UTILS("&eУтилиты", new ItemStack(Material.ENDER_CHEST)),
        QUEST("&bКвест", new ItemStack(Material.BOOK_AND_QUILL)),
        UPGRADES("&2Улучшения", new ItemStack(Material.FIREWORK_CHARGE)),
        RUNES("&5Руны", new ItemStack(Material.NETHER_STAR)),
        ARMOR("&aБроня", new ItemStack(Material.DIAMOND_CHESTPLATE)),
        MONEY("&aДеньги", new ItemStack(Material.EMERALD), true),
        EXP("&6Опыт", new ItemStack(Material.EXP_BOTTLE), true),
        KEYS("&3Ключи", new ItemStack(Material.TRIPWIRE_HOOK)),
        OTHER("&5Прочее", new ItemStack(Material.FISHING_ROD)),
        HIDDEN("&7Скрыто", new ItemStack(Material.BARRIER), true);

        @Getter
        String name;
        @Getter
        ItemStack defaultItem;
        @Getter
        boolean isHidden;
        @Getter
        List<PrisonItem> jediItems;

        Category(String description, ItemStack defaultItem) {
            this(description, defaultItem, false);
        }

        Category(String description, ItemStack defaultItem, boolean isHidden) {
            this.name = Utils.colored(description);
            this.defaultItem = defaultItem;
            this.isHidden = isHidden;
            this.jediItems = new ArrayList();
        }
    }

    public enum Rare {
        DEFAULT("&rБазовый I", "&r"),
        COMMON("&rОбычный II", "&r"),
        UNCOMMON("&7Необычный III", "&7"),
        RARE("&cРедкий IV", "&c"),
        VERY_RARE("&cОчень редкий V", "&c"),
        SUPER_RARE("&4Супер редкий VI", "&4"),
        COVERT("&4&lТайный VII", "&4&l"),
        EPIC("&5Эпический VIII", "&5"),
        MYTHIC("&5&lМифический IX", "&5&l"),
        LEGENDARY("&6Легендарный X", "&6"),
        EXTRA("&6Экстраординарный XI", "&6"),
        CONTRABAND("&e&lКонтрабандный XII", "&e&l"),
        UNREACHABLE("&8&l&nНедостижимый XIII", "&8&l&n");

        @Getter
        String name, color;
        @Getter
        List<PrisonItem> prisonItems;

        Rare(String name, String color) {
            this.name = Utils.colored(name);
            this.color = Utils.colored(color);
            this.prisonItems = new ArrayList();
        }
    }
}
