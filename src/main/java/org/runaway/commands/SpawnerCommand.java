package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.Mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnerCommand extends CommandManager {

    public SpawnerCommand() {
        super("spawners", "prison.admin", Collections.singletonList("спавнер"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
            EConfig.MOBS.getConfig().getConfigurationSection("mobs").getKeys(false).forEach(s ->
                    p.sendMessage(Utils.colored("&b- " + s)));
            p.sendMessage((Utils.colored("&aСписок окончен")));
            return;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (!p.isOnGround()) {
                p.sendMessage(Utils.colored("&cОшибка: Встаньте на землю!"));
                return;
            }
            Mobs entity;
            try {
                entity = Mobs.valueOf(args[1].toUpperCase());
            } catch (Exception ex) {
                p.sendMessage(Utils.colored("&cОшибка: Нет такого вида моба!"));
                return;
            }
            ConfigurationSection section;
            if (entity.isMultispawn()) {
                AtomicInteger num = new AtomicInteger(1);
                EConfig.MOBS.getConfig().getConfigurationSection("mobs").getKeys(false).forEach(s -> {
                    if (s.contains(args[1].toLowerCase())) {
                        int i = Integer.parseInt(s.replace(args[1], ""));
                        if (i > num.get()) num.set(i);
                    }
                });
                int next = num.get() + 1;
                EConfig.MOBS.getConfig().getConfigurationSection("mobs").createSection(args[1].toLowerCase() + next);
                section = EConfig.MOBS.getConfig().getConfigurationSection("mobs." + args[1].toLowerCase() + next);
            } else {
                EConfig.MOBS.getConfig().getConfigurationSection("mobs").createSection(args[1].toLowerCase());
                section = EConfig.MOBS.getConfig().getConfigurationSection("mobs." + args[1].toLowerCase());
            }
            section.set("location", Utils.serializeLocation(p.getLocation()));
            section.set("type", args[1].toLowerCase());
            p.sendMessage(Utils.colored("&aВы успешно установили спавнер &2" + args[1].toUpperCase()));
            EConfig.MOBS.saveConfig();
        } else {
            List<String> mobs = new ArrayList<>();
            Arrays.stream(Mobs.values()).forEach(m -> mobs.add(m.name().toLowerCase()));
            p.sendMessage(Utils.colored("&cИспользование: /" + cmdName + " <debug, set> " + mobs.toString()));
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
