package org.runaway.donate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.runaway.donate.features.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public enum Privs {
    DEFAULT("prison.default", null, 1),
    VIP("prison.vip", new IFeature[] {
            new FLeaveDiscount().setValue(15),
            new BoosterBlocks().setValue(1.2),
            new BoosterMoney().setValue(1.2)
    }, 2),
    PREMIUM("prison.premium", new IFeature[] {
            new FLeaveDiscount().setValue(20),
            new BoosterBlocks().setValue(1.3),
            new BoosterMoney().setValue(1.3)
    }, 3),
    CRYSTAL("prison.crystal", new IFeature[] {
            new FLeaveDiscount().setValue(25),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.4),
            new BoosterMoney().setValue(1.4)
    }, 4),
    LORD("prison.lord", new IFeature[] {
            new FLeaveDiscount().setValue(30),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.5),
            new BoosterMoney().setValue(1.5),
    }, 5),
    MAGMA("prison.magma", new IFeature[] {
            new FLeaveDiscount().setValue(35),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.6),
            new BoosterMoney().setValue(1.6),
    }, 6),
    JUNIOR("prison.junior", new IFeature[] {
            new FLeaveDiscount().setValue(40),
            new BossNotify().setValue(true),
            new BoosterBlocks().setValue(1.8),
            new BoosterMoney().setValue(1.8),
    }, 6);

    private String perm;
    private IFeature[] features;
    private int priority;

    Privs(String perm, IFeature[] features, int priority) {
        this.perm = perm;
        this.features = features;
        this.priority = priority;
    }

    public String getPermission() {
        return perm;
    }

    public IFeature[] getFeatures() {
        return features;
    }

    private static Privs current;
    private static Player player;

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
