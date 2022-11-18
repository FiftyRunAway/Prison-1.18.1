package org.runaway.huds;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Elements.Element;
import cz.apigames.betterhud.api.Elements.TextElement;
import cz.apigames.betterhud.api.Utils.Placeholder;
import cz.apigames.betterhud.api.Utils.ToggleEvent;
import jdk.jfr.EventType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.battlepass.IMission;
import org.runaway.board.Board;
import org.runaway.enums.EStat;
import org.runaway.managers.GamerManager;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomHUD {

    public static void load() {
        new SyncRepeatTask(() -> {
            Utils.getPlayers().forEach(player -> {
                Gamer gamer = GamerManager.getGamer(player);
                List<Placeholder> placeholders = new ArrayList<>();

                placeholders.add(placeholder("blocks",
                        Board.FormatBlocks(gamer, EStat.BLOCKS)));
                placeholders.add(placeholder("money",
                        Board.FormatMoney(gamer.getStatistics(EStat.MONEY))));

                BetterHudAPI.clearPlaceholders(gamer.getPlayer());
                BetterHudAPI.setPlaceholders(gamer.getPlayer(), placeholders);
            });
        }, 30, 60);
    }

    private static Placeholder placeholder(String placeholder, String value) {
        return new Placeholder(placeholder, value);
    }

}
