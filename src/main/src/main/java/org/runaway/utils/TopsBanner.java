package org.runaway.utils;

import me.bigteddy98.bannerboard.api.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Prison;
import org.runaway.board.Board;
import org.runaway.enums.MoneyType;
import org.runaway.tasks.AsyncRepeatTask;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopsBanner extends BannerBoardRenderer<Void> {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm МСК");

    public TopsBanner(List<Setting> parameters, int allowedWidth, int allowedHeight) {
        super(parameters, allowedWidth, allowedHeight);

        if (!this.hasSetting("textureSize")) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have a valid texturesize parameter, renderer disabled...");
        }
        try {
            Integer.parseInt(this.getSetting("textureSize").getValue());
        } catch (NumberFormatException e) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have a valid texturesize parameter, must be a number. Renderer disabled...");
        }
        if (!this.hasSetting("leaderboardname")) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have a valid leaderboardname parameter, renderer disabled...");
        }
        if (!this.hasSetting("timeframe")) {
            this.getSettings().add(new Setting("timeframe", "alltime"));
        }
        String timeframe = this.getSetting("timeframe").getValue().toLowerCase();
        if (!(timeframe.equalsIgnoreCase("alltime") || timeframe.equalsIgnoreCase("daily") || timeframe.equalsIgnoreCase("weekly") || timeframe.equalsIgnoreCase("monthly"))) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have valid timeframe parameter " + timeframe);
        }
        if (!this.hasSetting("number")) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have valid number parameter, renderer disabled...");
        }
        String number = this.getSetting("number").getValue();
        int pos;
        try {
            pos = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have valid number parameter " + number + ", renderer disabled...");
        }
        if (pos < 1 || pos > 10) {
            throw new DisableBannerBoardException("Leaderboard position must be between 0 and 10 (so not " + pos + "), renderer disabled...");
        }
        // check the skin URL
        /*if (!this.hasSetting("skinurl")) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have valid skinurl parameter, renderer disabled...");
        }*/
        // all font related things
        String randomFont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()[0];
        if (!this.hasSetting("font")) {
            parameters.add(new Setting("font", randomFont));
        }
        if (!Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains(this.getSetting("font").getValue())) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WARNING] [BannerBoard] Renderer TEXT has an unknown font value, " + this.getSetting("font").getValue() + ", using random font " + randomFont + "...");
            parameters.add(new Setting("font", randomFont));
        }

        if (!this.hasSetting("nameSize")) {
            parameters.add(new Setting("nameSize", "20"));
        }
        try {
            Integer.parseInt(this.getSetting("nameSize").getValue());
        } catch (NumberFormatException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WARNING] [BannerBoard] Renderer TEXT has an invalid size nameSize, " + this.getSetting("nameSize").getValue() + ", using default size 20...");
            this.getSetting("nameSize").setValue("20");
        }

        if (!this.hasSetting("color")) {
            parameters.add(new Setting("color", "255,255,255"));
        }
        if (!this.hasSetting("style")) {
            parameters.add(new Setting("style", "PLAIN"));
        }
        try {
            FontStyle.valueOf(this.getSetting("style").getValue().toUpperCase());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WARNING] [BannerBoard] Renderer TEXT has an invalid style value, " + this.getSetting("style").getValue() + ", using default style PLAIN...");
            this.getSetting("style").setValue("PLAIN");
        }

        if (!this.hasSetting("strokeColor")) {
            parameters.add(new Setting("strokeColor", "0,0,0"));
        }
        if (!this.hasSetting("strokeThickness")) {
            parameters.add(new Setting("strokeThickness", "0"));
        }
        try {
            Integer.parseInt(this.getSetting("strokeThickness").getValue());
        } catch (NumberFormatException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WARNING] [BannerBoard] Renderer TEXT has an invalid strokeThickness value, " + this.getSetting("strokeThickness").getValue() + ", using default thickness 0...");
            this.getSetting("strokeThickness").setValue("0");
        }

        if (!this.hasSetting("nameOffset")) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have valid nameOffset parameter, renderer disabled...");
        }

        if (!this.hasSetting("textOffset")) {
            throw new DisableBannerBoardException("Renderer PRISON_LEADERS did not have valid textOffset parameter, renderer disabled...");
        }

        new AsyncRepeatTask(this::updateBoard, 2400, 200);
    }

    private volatile Map<String, Long> top;
    private volatile String desc;

    private final Object waitLock = new Object();
    private boolean wait = true;

    private void updateBoard() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String type = TopsBanner.this.getSetting("leaderboardname").getValue();

                    TopsBanner.this.top = Prison.tops.get(type.toLowerCase()).getTopValues();
                    if (TopsBanner.this.top.isEmpty()) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WARNING] [BannerBoard] No data was found for leaderboard " + type);
                        return;
                    }
                    TopsBanner.this.desc = Prison.tops.get(type.toLowerCase()).getDescription();
                } finally {
                    synchronized (waitLock) {
                        wait = false;
                        waitLock.notifyAll();
                    }
                }
            }
        }.runTaskAsynchronously(Prison.getInstance());
    }

    @Override
    public void render(Player p, BufferedImage image, Graphics2D g) {
        synchronized (this.waitLock) {
            while (wait) {
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (top == null) {
            return;
        }
         Integer xOffset = null;
        Integer yOffset = null;

        if (this.hasSetting("xOffset")) {
            xOffset = Integer.parseInt(this.getSetting("xOffset").getValue());
        }
        if (this.hasSetting("yOffset")) {
            yOffset = Integer.parseInt(this.getSetting("yOffset").getValue());
        }

        int size = Integer.parseInt(this.getSetting("nameSize").getValue());

        String fontName = this.getSetting("font").getValue();
        Font font = new Font(fontName, 1, size);
        Color textColor = this.decodeColor(this.getSetting("color").getValue());
        Color blurColor = this.decodeColor(this.getSetting("strokeColor").getValue());
        int strokeThickness = Integer.parseInt(this.getSetting("strokeThickness").getValue());

        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger k = new AtomicInteger(0);
        int finalYOffset = yOffset;
        int finalXOffset = xOffset;

        int s = this.top.keySet().size();
        this.top.remove("_RunAway_");
        this.top.remove("AttempGame");
        this.top.forEach((name, score) -> {
            if (i.get() >= (s - 10)) {
                int pos = k.get() + 1;
                String score_string = score.toString() + " " + desc;
                if (this.desc.equalsIgnoreCase("блоков")) {
                    score_string = Board.FormatBlocks(score.toString()) + " " + desc;
                } else if (this.desc.equalsIgnoreCase(MoneyType.RUBLES.getShortName())) {
                    score_string = Utils.FormatMoney(score) + " " + MoneyType.RUBLES.getShortName();
                } else if (this.desc.equalsIgnoreCase("рублей")) {
                    score_string = Utils.FormatMoney(score) + " рублей";
                }
                g.drawImage(BannerBoardManager.getAPI().drawFancyText(image.getWidth(), image.getHeight(), pos + ". " + name, font, textColor, blurColor, strokeThickness, null, null), finalXOffset - 110,
                        finalYOffset + (k.get() * 27) - 200 + (k.get() * 2), null);
                g.drawImage(BannerBoardManager.getAPI().drawFancyText(image.getWidth(), image.getHeight(), score_string, font, textColor, blurColor, strokeThickness, null, null), finalXOffset + 115,
                        finalYOffset + (k.get() * 27) - 200 + (k.getAndIncrement() * 2), null);
            }
            i.getAndIncrement();
        });
        g.drawImage(BannerBoardManager.getAPI().drawFancyText(image.getWidth(), image.getHeight(), "Последнее обновление: " + dateFormat.format(new Date()), font, textColor, blurColor, strokeThickness, null, null), finalXOffset / 2,
                finalYOffset + 120, null);
    }
}
