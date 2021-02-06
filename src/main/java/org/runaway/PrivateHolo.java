package org.runaway;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Utils;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class PrivateHolo {
    private static final Map<Entity, PrivateHolo> privateHolograms = new HashMap();

    public PrivateHolo(Player player, Location loc, StandType st, String value) {
        this(player, loc.add(0d, st.getToRemove(), 0d), String.format(st.getText(), value), 50);
    }

    public PrivateHolo(Player player, Location loc, String value, int delay) {
        WorldServer s = ((CraftWorld) loc.getWorld()).getHandle();
        EntityArmorStand stand = new EntityArmorStand(s);
        stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0.0f, 0.0f);
        stand.setCustomName(Utils.colored(value));
        stand.setCustomNameVisible(true);
        stand.setNoGravity(true);
        stand.setInvisible(true);
        stand.setSmall(true);
        privateHolograms.put(stand.getBukkitEntity(), this);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        if (delay != -1) {
            new SyncTask(() -> {
                remove(stand.getBukkitEntity());
                privateHolograms.remove(stand.getBukkitEntity());
            }, delay);
        }
    }



    private static void remove(Entity stand) {
        final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(stand.getEntityId());
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
        privateHolograms.remove(stand);
    }

    @Getter
    public enum StandType {
        MONEY("§b+%s$", 0.2),
        ANOTHER("§d+%s", -0.1);

        double toRemove;
        String text;

        StandType(String text, double toRemove) {
            this.text = Utils.colored(text);
            this.toRemove = toRemove;
        }
    }
}