package org.runaway.google;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EStat;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.UUID;

public class TWOFA implements Listener {

    public static ArrayList<UUID> authlocked;

    public TWOFA() {
        authlocked = new ArrayList<>();
    }

    private boolean playerInputCode(Gamer gamer, int code) {
        String secretkey = gamer.getStatistics(EStat.TWOFA_CODE).toString();

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean codeisvalid = gAuth.authorize(secretkey, code);


        if (codeisvalid) {
            authlocked.remove(gamer.getUUID());
            return codeisvalid;
        }

        return codeisvalid;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void chat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        if (authlocked.contains(player.getUniqueId())) {
            try {
                int code = Integer.parseInt(message);
                if (playerInputCode(gamer, code)) {
                    authlocked.remove(player.getUniqueId());
                    player.sendMessage(Utils.colored("&aДоступ к аккаунту успешно получен!"));
                } else {
                    player.sendMessage(Utils.colored("&cНеверно введён код. Он может содержать только цифры."));

                }
            } catch (Exception e) {
                player.sendMessage(Utils.colored("&cНеверно введён код. Он может содержать только цифры."));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (authlocked.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockbreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (authlocked.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockplace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (authlocked.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
