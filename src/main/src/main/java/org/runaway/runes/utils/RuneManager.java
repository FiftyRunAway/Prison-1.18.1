package org.runaway.runes.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.runes.armor.StormCallerRune;
import org.runaway.runes.armor.*;
import org.runaway.runes.armor.boots.AntiGravityRune;
import org.runaway.runes.armor.boots.GearsRune;
import org.runaway.runes.armor.boots.SpringsRune;
import org.runaway.runes.armor.helmets.GlowingRune;
import org.runaway.runes.pickaxe.BlastRune;
import org.runaway.runes.pickaxe.SpeedRune;
import org.runaway.runes.sword.*;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuneManager {

    public static List<Rune> runes = new ArrayList<>();

    public static void runeAction(Gamer gamer, String techname) {
        Rune rune = getRune(techname);
        if (rune == null) return;
        if (getRune(techname).act(gamer)) {
            gamer.sendMessage("&fПрименена руна " + getRuneName(rune));
        }
    }

    public static Rune getRune(String techName) {
        for (Rune rune : runes) {
            if (rune.getTechName().equals(techName)) return rune;
        }
        return null;
    }

    public static int slots(ItemStack itemStack) {
        return getRunes(itemStack).size();
    }

    public static List<Rune> getRunes(ItemStack itemStack) {
        List<Rune> result = new ArrayList<>();
        if (itemStack == null) return result;
        PrisonItem prisonItem = ItemManager.getPrisonItem(itemStack);
        if (prisonItem == null) return result;
        if (prisonItem.getMutableParameters() == null) return result;
        new ParameterMeta(itemStack).getParametersMap().forEach((parameter, o) -> {
            if (parameter.getDefaultNbtFormatter().getString().contains("rune")) {
                result.add(getRune(o.toString()));
            }
        });
        return result;
    }

    public static List<String> getTechNames(ItemStack itemStack) {
        List<String> result = new ArrayList<>();
        PrisonItem prisonItem = ItemManager.getPrisonItem(itemStack);
        if (prisonItem == null) return result;
        new ParameterMeta(itemStack).getParametersMap().forEach((parameter, o) -> {
            if (parameter.getDefaultNbtFormatter().getString().contains("rune")) {
                result.add(o.toString());
            }
        });
        return result;
    }

    public static boolean hasRune(ItemStack itemStack, Rune rune) {
        List<Rune> runes = getRunes(itemStack);
        if (runes.isEmpty()) return false;
        for (Rune run : runes) {
            if (run == null) continue;
            if (run.getTechName().equals(rune.getTechName())) return true;
        }
        return false;
    }

    public static ItemStack addRune(ItemStack itemStack, Rune rune) {
        if (rune == null) return itemStack;
        List<Rune> runes = getRunes(itemStack);
        if (runes.isEmpty()) return itemStack;
        if (rune.getRarity().getSlot() > runes.size()) return itemStack;
        return ItemManager.setValue(itemStack, "rune" + rune.getRarity().getSlot(),
                Utils.colored(ParameterManager.getRuneInfoString()
                        .replace("%d", String.valueOf(rune.getRarity().getSlot()))
                        .replace("%s", "")),
                getRuneName(rune),
                rune.getTechName());
    }

    public static ItemStack removeRune(ItemStack itemStack, Rune rune) {
        List<Rune> runes = getRunes(itemStack);
        if (runes.isEmpty()) return null;
        Bukkit.getConsoleSender().sendMessage(rune.getTechName());
        return ItemManager.setValue(itemStack, "rune" + rune.getRarity().getSlot(),
                Utils.colored(ParameterManager.getRuneInfoString()
                        .replace("%d", String.valueOf(rune.getRarity().getSlot()))
                        .replace("%s", "")),
                "-",
                null);
    }

    public static String getRuneName(Rune rune) {
        return Utils.colored(rune.getRarity().getColor() + rune.getName());
    }

    public enum RuneRarity {
        COMMON("&fОбычная", "&f", 1, "itemsadder:crystal", PrisonItem.Rare.COMMON),
        RARE("&aРедкая", "&a", 2, "itemsadder:ruby", PrisonItem.Rare.RARE),
        EPIC("&5Эпическая", "&5", 3, "itemsadder:amethyst", PrisonItem.Rare.EPIC),
        LEGENDARY("&6Легендарная", "&6", 4, "itemsadder:mysterious_amulet", PrisonItem.Rare.LEGENDARY);

        @Getter
        private String name;
        @Getter
        private String color;
        @Getter
        private int slot;
        @Getter
        private String cutomItemStack;
        @Getter
        private PrisonItem.Rare prisonRare;

        RuneRarity(String name, String color, int slot, String customItemStack, PrisonItem.Rare prisonRare) {
            this.name = name;
            this.color = color;
            this.slot = slot;
            this.cutomItemStack = customItemStack;
            this.prisonRare = prisonRare;
        }

        private ItemStack getReplacer() {
            return new ItemStack(Material.EMERALD);
        }

        private ItemStack getItemStack() {
            if (!Prison.useItemsAdder) return getReplacer();
            try {
                return getReplacer();
                //return CustomStack.getInstance(getCutomItemStack()).getItemStack();
            } catch (Exception e) {
                return getReplacer();
            }
        }
    }

    public enum RuneType {
        ARMOR("брони"),
        BOOTS("ботинок"),
        LEGGINGS("штанов"),
        CHESTPLATE("нагрудника"),
        HELMET("шлема"),
        SWORD("меча"),
        PICKAXE("кирки"),
        AXE("топора"),
        BOW("лука");

        @Getter
        String name;

        RuneType(String name) {
            this.name = name;
        }
    }

    public static void init() {
        runes.addAll(List.of(
                //ARMOR
                new FreezeRune(), new FortifyRune(), new EnlightenedRune(), new MoltenRune(), new PainGiverRune(),
                new SaviorRune(), new InsomniaRune(), new SpringsRune(), new AntiGravityRune(), new GearsRune(),
                new GlowingRune(), new BurnShieldRune(), new HulkRune(), new NinjaRune(), new ValorRune(), new SmokeBombRune(),
                new DrunkRune(), new VoodooRune(), new RecoverRune(), new CactusRune(), new StormCallerRune(),
                //SWORD
                new VampireRune(), new LifeStealRune(), new DoubleDamageRune(), new SlowMoRune(), new BlindnessRune(), new ViperRune(),
                new ConfusionRune(), new ExecutionRune(), new NutritionRune(), new ObliterateRune(), new ParalyzeRune(), new SnareRune(),
                new TrapRune(), new WitherRune(),
                //PICKAXE
                new SpeedRune(), new BlastRune()
        ));

        for (Rune rune : runes) {
            ItemStack runeItem = new ItemBuilder(rune.getRarity().getItemStack()).name(
                    "&7Руна • " + RuneManager.getRuneName(rune))
                    .addLoreLine("&7" + rune.getDescription())
                    .addLoreLine(" ")
                    .addLoreLine("&e⚒ &7Для &e" + rune.getType().getName()).build();
            PrisonItem prisonItem = PrisonItem.builder()
                    .vanillaName(rune.getTechName() + "Rune")
                    .vanillaItem(runeItem)
                    .category(PrisonItem.Category.RUNES)
                    .parameters(Arrays.asList(
                            ParameterManager.getNodropParameter(),
                            ParameterManager.getRareParameter(rune.getRarity().getPrisonRare()), //редкость предмета
                            ParameterManager.getCategoryParameter(PrisonItem.Category.RUNES))).build();
            ItemManager.addPrisonItem(prisonItem);
        }
    }
}
