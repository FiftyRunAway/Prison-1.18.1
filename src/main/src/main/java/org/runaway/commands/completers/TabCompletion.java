package org.runaway.commands.completers;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.runaway.Prison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public abstract class TabCompletion {
    private String commandName;
    private Map<Integer, List<String>> variants;
    private String permission;

    public TabCompletion(String commandName, Map<Integer, List<String>> args) {
        this(commandName, args, null);
    }

    public TabCompletion(String commandName, Map<Integer, List<String>> args, String permission) {
        this.commandName = commandName;
        this.variants = args;
        this.permission = permission;
    }

    public List<String> getVariants(CommandSender commandSender, Command command, String[] strings) {
        List<String> result;
        if (command.getName().equalsIgnoreCase(this.commandName)) {
            if (commandSender instanceof Player player) {
                if (permission != null && !player.hasPermission(permission)) return null;
                result = new ArrayList<>();
                variants.forEach((i, l) -> {
                    if (strings.length == i) {
                        result.addAll(l);
                    }
                });
                return result;

            }
        }
        return null;
    }
}
