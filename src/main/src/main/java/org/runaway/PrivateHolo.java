package org.runaway;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Utils;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class PrivateHolo {
    private static final Map<Entity, PrivateHolo> privateHolograms = new HashMap<>();

    public PrivateHolo(Player player, Location loc, StandType st, String value) {
        this(player, loc.add(0d, st.getToRemove(), 0d), String.format(st.getText(), value), 50);
    }

    public PrivateHolo(Player player, Location loc, StandType st, double value) {
        this(player, loc.add(0d, st.getToRemove(), 0d), String.format(st.getText(), value), 50);
    }

    public PrivateHolo(Player player, Location loc, String value, int delay) {
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setCustomName(Utils.colored(value));
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setSmall(true);
        privateHolograms.put(stand, this);
        if (delay != -1) {
            new SyncTask(() -> remove(stand), delay);
        }
    }



    private static void remove(Entity stand) {
        stand.remove();
        privateHolograms.remove(stand);
    }

    @Getter
    public enum StandType {
        MONEY("§b+%.2f$", 0.2),
        ANOTHER("§d+%s", -0.1);

        double toRemove;
        String text;

        StandType(String text, double toRemove) {
            this.text = Utils.colored(text);
            this.toRemove = toRemove;
        }
    }
}