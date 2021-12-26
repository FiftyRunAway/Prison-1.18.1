package org.runaway.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.runaway.Prison;
import org.runaway.utils.Utils;

public class ListPing implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (Prison.getInstance().getStatus() != null) {
            event.setMotd(Utils.colored("&4&lPrison"));
        }
    }
}
