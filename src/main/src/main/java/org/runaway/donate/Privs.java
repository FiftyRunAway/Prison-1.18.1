package org.runaway.donate;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.runaway.Prison;
import org.runaway.items.Item;
import org.runaway.donate.features.*;
import org.runaway.enums.MoneyType;
import org.runaway.items.ItemManager;
import org.runaway.nametag.Teams;
import org.runaway.rewards.LootItem;
import org.runaway.rewards.MoneyReward;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.color.ColorAPI;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public enum Privs {
    DEFAULT("prison.default", null, 1, Material.GRAY_DYE, "", null, 0, 0),
    VIP("prison.vip", new IFeature[] {
            new FractionDiscount().setValue(15),
            new BoosterBlocks().setValue(1.1),
            new BoosterMoney().setValue(1.1),
            new NeedSleep().setValue(64),
            new NeedWash().setValue(17),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(5)
    }, 2, Material.ORANGE_DYE, "&6VIP", "&6V",10, 100),
   PREMIUM("prison.premium", new IFeature[] {
           new FractionDiscount().setValue(25),
           new BoosterBlocks().setValue(1.2),
           new BoosterMoney().setValue(1.2),
           new BossMoney().setValue(10),
           new NeedSleep().setValue(68),
           new NeedWash().setValue(20),
           new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
           new StringFeature().setName("Максимум предметов на аукционе").setValue(6)
    }, 3, Material.YELLOW_DYE, "&ePremium", "&eP",11, 200),
    CRYSTAL("prison.crystal", new IFeature[] {
            new FractionDiscount().setValue(35),
            new BoosterBlocks().setValue(1.3),
            new BoosterMoney().setValue(1.3),
            new BossMoney().setValue(15),
            new NeedSleep().setValue(72),
            new NeedWash().setValue(23),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(7)
    }, 4, Material.LIGHT_BLUE_DYE, "&bCrystal", "&bC",12, 300),
    MAGMA("prison.magma", new IFeature[] {
            new FractionDiscount().setValue(40),
            new BoosterBlocks().setValue(1.4),
            new BoosterMoney().setValue(1.4),
            new BossMoney().setValue(20),
            new NeedSleep().setValue(76),
            new NeedWash().setValue(26),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(8)
    }, 5, Material.RED_DYE, "&cMagma", "&cM",13, 450),
    JUNIOR("prison.junior", new IFeature[] {
            new FractionDiscount().setValue(45),
            new BoosterBlocks().setValue(1.5),
            new BoosterMoney().setValue(1.5),
            new BossMoney().setValue(20),
            new NeedSleep().setValue(80),
            new NeedWash().setValue(29),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(9),
            new BossNotify().setValue(true)
    }, 6, Material.PURPLE_DYE, "&dJunior","&dJ", 14, 750),
    KING("prison.king", new IFeature[] {
            new FractionDiscount().setValue(50),
            new BoosterBlocks().setValue(1.6),
            new BoosterMoney().setValue(1.6),
            new BossMoney().setValue(25),
            new NeedSleep().setValue(84),
            new NeedWash().setValue(32),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(12),
            new BossNotify().setValue(true)
    }, 7, Material.BLUE_DYE, "&9King", "&9K",15, 950),
    PLATINUM("prison.platinum", new IFeature[] {
            new FractionDiscount().setValue(60),
            new BoosterBlocks().setValue(1.7),
            new BoosterMoney().setValue(1.7),
            new BossMoney().setValue(30),
            new NeedSleep().setValue(88),
            new NeedWash().setValue(35),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(14),
            new BossNotify().setValue(true)
    }, 8, Material.WHITE_DYE, ColorAPI.process("<SOLID:e5e4e2>Platinum"), ColorAPI.process("<SOLID:e5e4e2>P"),19, 1250),
    HERO("prison.hero", new IFeature[] {
            new FractionDiscount().setValue(65),
            new BoosterBlocks().setValue(1.8),
            new BoosterMoney().setValue(1.8),
            new BossMoney().setValue(35),
            new NeedSleep().setValue(92),
            new NeedWash().setValue(38),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(16),
            new BossNotify().setValue(true)
    }, 9, Material.LIME_DYE, "&aHero", "&aH",20, 1500),
    AQUA("prison.aqua", new IFeature[] {
            new FractionDiscount().setValue(75),
            new BoosterBlocks().setValue(1.9),
            new BoosterMoney().setValue(1.9),
            new BossMoney().setValue(40),
            new NeedSleep().setValue(92),
            new NeedWash().setValue(41),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(18),
            new BossNotify().setValue(true),
            new StringFeature().setName("&6Боевой пропуск").setValue(true)
    }, 10, Material.MAGENTA_DYE, ColorAPI.process("<SOLID:0272ff>Aqua"), ColorAPI.process("<SOLID:e0a33a>A"),21, 1800),
    SPONSOR("prison.sponsor", new IFeature[] {
            new FractionDiscount().setValue(85),
            new BoosterBlocks().setValue(2.0),
            new BoosterMoney().setValue(2.0),
            new BossMoney().setValue(45),
            new NeedSleep().setValue(100),
            new NeedWash().setValue(45),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(20),
            new BossNotify().setValue(true),
            new StringFeature().setName("&6Боевой пропуск").setValue(true)
    }, 11, Material.CAKE, ColorAPI.process("<GRADIENT:e0a33a>Sponsor</GRADIENT:cf383c>"), ColorAPI.process("<SOLID:e0a33a>S"),22, 6000);

    public static Map<Privs, ItemStack> icons = new EnumMap<>(Privs.class);

    private String perm;
    private IFeature[] features;
    private int priority;
    private int slot;
    private int price;

    private Material sack;
    private String name;
    private String shortPrefix;

    private Team team;

    Privs(String perm, IFeature[] features, int priority, Material sack, String name, String shortPrefix, int slot, int price) {
        this.perm = perm;
        this.features = features;
        this.priority = priority;
        this.slot = slot;
        this.price = price;

        this.sack = sack;
        this.name = name;
        this.shortPrefix = shortPrefix;

        ScoreboardManager sm = Bukkit.getServer().getScoreboardManager();
        Teams.sb = sm.getNewScoreboard();
        this.team = Teams.sb.registerNewTeam(name().toLowerCase(Locale.ROOT));
        this.team.setPrefix(getName());
    }

    public Material getMaterial() {
        return sack;
    }

    public String getPermission() {
        return perm;
    }

    public IFeature[] getFeatures() {
        return features;
    }

    public int getSlot() {
        return slot;
    }

    public Team getTeam() {
        return team;
    }

    public int getPrice() {
        return price;
    }

    public int getPriority() {
        return priority;
    }

    private static Privs current;
    private static Player player;

    public String getName() {
        return name;
    }

    public String getShortPrefix() {
        return shortPrefix;
    }

    public static void loadIcons() {
        Arrays.stream(Privs.values()).forEach(priv -> {
            if (priv.priority > 1) {
                icons.put(priv, priv.getIcon(priv));
            }
        });
    }

    private ItemStack getIcon(Privs priv) {
        ArrayList<String> list = new ArrayList<>();
        for (IFeature feature : priv.features) {
            list.add("  &a" + feature.getName() + " &7• &e" + feature.getValue().toString().replace("true", "есть"));
        }
        return new Item.Builder(priv.sack)
                .name(getGuiName())
                .lore(new Lore.BuilderLore().addList(list)
                        .addSpace()
                        .addString("&fЦена: &l&b&n" + priv.getPrice() + " " + MoneyType.REAL_RUBLES.getShortName()).build())
                .build().item();
    }

    public String getGuiName() {
        return Utils.colored("&7Привилегия • " + (Prison.useItemsAdder ? getImageName() : getName()));
    }

    public String getImageName() {
        try {
            return new FontImageWrapper("moreranks:" + name().toLowerCase(Locale.ROOT)).getString();
        } catch (Exception e) {
            return getName();
        }
    }

    public Privs getPrivilege(Player p) {
        AtomicReference<String> priv = new AtomicReference<>();
        Arrays.stream(Privs.values()).forEach(privs -> {
            if (p.hasPermission(privs.getPermission())) {
                priv.set(privs.toString());
            }
        });
        player = p;
        current = Privs.valueOf(priv.get());
        return current;
    }

    public Object getValue(IFeature feature) {
        if (player != null) {
            if (current.equals(Privs.DEFAULT)) return null;
            AtomicReference<Object> object = new AtomicReference<>(null);
            Arrays.stream(current.getFeatures()).forEach(fets -> {
                if (feature.getCode() == fets.getCode()) {
                    object.set(fets.getValue());
                }
            });
            return object.get();
        }
        return null;
    }

    public Kit getKit() {
        switch (this) {
            case VIP -> {
                return new Kit.KitBuilder().priv(VIP).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(100).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(8).build()
                )).build();
            }
            case PREMIUM -> {
                return new Kit.KitBuilder().priv(PREMIUM).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(150).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(12).build()
                )).build();
            }
            case CRYSTAL -> {
                return new Kit.KitBuilder().priv(CRYSTAL).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(200).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(16).build()
                )).build();
            }
            case MAGMA -> {
                return new Kit.KitBuilder().priv(MAGMA).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(250).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(20).build()
                )).build();
            }
            case JUNIOR -> {
                return new Kit.KitBuilder().priv(JUNIOR).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(300).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(24).build()
                )).build();
            }
            case KING -> {
                return new Kit.KitBuilder().priv(KING).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(350).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(26).build()
                )).build();
            }
            case PLATINUM -> {
                return new Kit.KitBuilder().priv(PLATINUM).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(400).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(30).build()
                )).build();
            }
            case HERO -> {
                return new Kit.KitBuilder().priv(HERO).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(450).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(33).build()
                )).build();
            }
            case AQUA -> {
                return new Kit.KitBuilder().priv(AQUA).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(500).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(36).build()
                )).build();
            }
            case SPONSOR -> {
                return new Kit.KitBuilder().priv(SPONSOR).cooldown(24 * 3600).rewards(List.of(
                        MoneyReward.builder().dependOnLevel(true).amount(600).build(),
                        LootItem.builder().prisonItem(ItemManager.getPrisonItem("defaultKey"))
                                .amount(40).build()
                )).build();
            }
            default -> {
                return null;
            }
        }
    }
}
