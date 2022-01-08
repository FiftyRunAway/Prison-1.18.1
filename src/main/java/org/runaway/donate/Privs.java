package org.runaway.donate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.Item;
import org.runaway.donate.features.*;
import org.runaway.enums.MoneyType;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public enum Privs {
    DEFAULT("prison.default", null, 1, 0, null, 0, 0),
    VIP("prison.vip", new IFeature[] {
            new FractionDiscount().setValue(15),
            new BoosterBlocks().setValue(1.1),
            new BoosterMoney().setValue(1.1),
            new NeedsLonger().setValue(12),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(5)
    }, 2, 14, "&7Привилегия: &aVIP", 10, 150),
   PREMIUM("prison.premium", new IFeature[] {
           new FractionDiscount().setValue(25),
           new BoosterBlocks().setValue(1.2),
           new BoosterMoney().setValue(1.2),
           new BossMoney().setValue(10),
           new NeedsLonger().setValue(14),
           new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
           new StringFeature().setName("Максимум предметов на аукционе").setValue(6)
    }, 3, 11, "&7Привилегия: &bPremium", 11, 200),
    CRYSTAL("prison.crystal", new IFeature[] {
            new FractionDiscount().setValue(35),
            new BoosterBlocks().setValue(1.3),
            new BoosterMoney().setValue(1.3),
            new BossMoney().setValue(15),
            new NeedsLonger().setValue(16),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(7)
    }, 4, 12, "&7Привилегия: &bCrystal", 12, 300),
    LORD("prison.lord", new IFeature[] {
            new FractionDiscount().setValue(40),
            new BoosterBlocks().setValue(1.4),
            new BoosterMoney().setValue(1.4),
            new BossMoney().setValue(20),
            new NeedsLonger().setValue(18),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(8)
    }, 5, 10, "&7Привилегия: &aLord", 13, 450),
    MAGMA("prison.magma", new IFeature[] {
            new FractionDiscount().setValue(45),
            new BoosterBlocks().setValue(1.5),
            new BoosterMoney().setValue(1.5),
            new BossMoney().setValue(25),
            new NeedsLonger().setValue(20),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(9),
            new BossNotify().setValue(true)
    }, 6, 1, "&7Привилегия: &cMagma", 14, 550),
    JUNIOR("prison.junior", new IFeature[] {
            new FractionDiscount().setValue(60),
            new BoosterBlocks().setValue(1.6),
            new BoosterMoney().setValue(1.6),
            new BossMoney().setValue(33),
            new NeedsLonger().setValue(25),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(10),
            new BossNotify().setValue(true)
    }, 7, 5, "&7Привилегия: &dJunior", 15, 750),;

    public static Map<Privs, ItemStack> icons = new EnumMap<>(Privs.class);

    private String perm;
    private IFeature[] features;
    private int priority;
    private int slot;
    private int price;

    private int subid;
    private String name;

    Privs(String perm, IFeature[] features, int priority, int sub_id, String name, int slot, int price) {
        this.perm = perm;
        this.features = features;
        this.priority = priority;
        this.slot = slot;
        this.price = price;

        this.subid = sub_id;
        this.name = name;
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

    public int getPrice() {
        return price;
    }

    private static Privs current;
    private static Player player;

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
            list.add("&7• &a" + feature.getName() + ": &e" + feature.getValue().toString().replace("true", "есть"));
        }
        return new Item.Builder(Material.INK_SACK)
                .data((short) priv.subid)
                .name(Utils.colored(priv.name))
                .lore(new Lore.BuilderLore().addList(list)
                        .addSpace()
                        .addString("&fЦена: &l&b&n" + priv.getPrice() + " " + MoneyType.REAL_RUBLES.getShortName()).build())
                .build().item();
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
        return this;
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
}
