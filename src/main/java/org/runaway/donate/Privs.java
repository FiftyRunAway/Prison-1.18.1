package org.runaway.donate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Item;
import org.runaway.donate.features.*;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public enum Privs {
    DEFAULT("prison.default", null, 1, 0, null, 0),
    VIP("prison.vip", new IFeature[] {
            new FLeaveDiscount().setValue(15),
            new BoosterBlocks().setValue(1.2),
            new BoosterMoney().setValue(1.2)
    }, 2, 14, "&7Привилегия: &6VIP", 10),
    PREMIUM("prison.premium", new IFeature[] {
            new FLeaveDiscount().setValue(20),
            new BoosterBlocks().setValue(1.3),
            new BoosterMoney().setValue(1.3),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть")
    }, 3, 11, "&7Привилегия: &ePremium", 11),
    CRYSTAL("prison.crystal", new IFeature[] {
            new FLeaveDiscount().setValue(25),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.4),
            new BoosterMoney().setValue(1.4),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть")
    }, 4, 12, "&7Привилегия: &bCrystal", 12),
    LORD("prison.lord", new IFeature[] {
            new FLeaveDiscount().setValue(30),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.5),
            new BoosterMoney().setValue(1.5),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть")
    }, 5, 10, "&7Привилегия: &aLord", 13),
    MAGMA("prison.magma", new IFeature[] {
            new FLeaveDiscount().setValue(35),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.6),
            new BoosterMoney().setValue(1.6),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть")
    }, 6, 1, "&7Привилегия: &cMagma", 14),
    JUNIOR("prison.junior", new IFeature[] {
            new FLeaveDiscount().setValue(40),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.8),
            new BoosterMoney().setValue(1.8),
            new StringFeature().setName("Авто-продажа блоков").setValue("есть")
    }, 7, 5, "&7Привилегия: &dJunior", 15);

    public static HashMap<Privs, ItemStack> icons = new HashMap<>();

    private String perm;
    private IFeature[] features;
    private int priority;
    private int slot;

    private int subid;
    private String name;

    Privs(String perm, IFeature[] features, int priority, int sub_id, String name, int slot) {
        this.perm = perm;
        this.features = features;
        this.priority = priority;
        this.slot = slot;

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
                .lore(new Lore.BuilderLore().addList(list).build())
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
