package org.runaway.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.runaway.Main;
import org.runaway.utils.Utils;

public class ListPing implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (Main.getInstance().getStatus() != null) {
            event.setMotd(Utils.colored("&ePrison Minecraft &7[&a1.12.2 - 1.15.2&7] " +
                    "\n&7[" + Main.getInstance().getStatus().getStatus() + "&7] &n&cОткрытие в ближайшее время!"));
        }
    }
}
