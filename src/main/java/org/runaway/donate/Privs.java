package org.runaway.donate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.Item;
import org.runaway.donate.features.*;
import org.runaway.enums.MoneyType;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public enum Privs {
    DEFAULT("prison.default", null, 1, 0, null, 0, 0),
    VIP("prison.vip", new IFeature[] {
            new FractionDiscount().setValue(15),
            new BoosterBlocks().setValue(1.2),
            new BoosterMoney().setValue(1.2),
            new NeedsLonger().setValue(13),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(5)
    }, 2, 14, "&7Привилегия: &aVIP", 12, 250),
   PREMIUM("prison.premium", new IFeature[] {
            new FractionDiscount().setValue(25),
            new BoosterBlocks().setValue(1.4),
            new BoosterMoney().setValue(1.4),
            new BossMoney().setValue(10),
            new NeedsLonger().setValue(19),
            new BossNotify().setValue(true),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(7)
    }, 3, 12, "&7Привилегия: &bPremium", 13, 300),
    DELUXE("prison.deluxe", new IFeature[] {
            new FractionDiscount().setValue(30),
            new BoosterBlocks().setValue(1.5),
            new BoosterMoney().setValue(1.5),
            new BossMoney().setValue(15),
            new NeedsLonger().setValue(21),
            new BossNotify().setValue(true),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(8)
    }, 4, 10, "&7Привилегия: &eDeluxe", 14, 500),
    LEGEND("prison.legend", new IFeature[] {
            new FractionDiscount().setValue(50),
            new BoosterBlocks().setValue(1.8),
            new BoosterMoney().setValue(1.8),
            new BossMoney().setValue(25),
            new NeedsLonger().setValue(30),
            new BossNotify().setValue(true),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть"),
            new StringFeature().setName("Максимум предметов на аукционе").setValue(10)
    }, 5, 5, "&7Привилегия: &6Legend", 22, 1000);

    public static HashMap<Privs, ItemStack> icons = new HashMap<>();

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
            list.add("&7• &a" + feature.getName() + ": &e" + feature.getValue().toString().replaceAll("true", "есть"));
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
