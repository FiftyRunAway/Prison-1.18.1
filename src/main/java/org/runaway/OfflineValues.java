package org.runaway;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class OfflineValues {
    private static final Map<UUID, OfflineValues> cooldownList = new HashMap();

    private final UUID uuid;
    private final Map<String, Long> cooldowns;

    public OfflineValues(UUID uuid) {
        this.uuid = uuid;
        this.cooldowns = new HashMap();
        cooldownList.put(uuid, this);
    }

    public Map<String, Long> getCooldowns() {
        return cooldowns;
    }

    public static OfflineValues getPlayerCooldown(UUID uuid) {
        if (cooldownList.containsKey(uuid)) {
            return cooldownList.get(uuid);
        }
        return new OfflineValues(uuid);
    }
}