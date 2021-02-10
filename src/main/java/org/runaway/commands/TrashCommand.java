package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;

import java.util.Collections;

public class TrashCommand extends CommandManager {

    public TrashCommand() {
        super("trash", "prison.commands", Collections.singletonList("мусор"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        StandardMenu menu = StandardMenu.create(4, "&eМусорка");
        IMenuButton btn = closeButton();
        for (int i = 27; i < 36; i++) {
            menu.addButton(btn.setSlot(i).clone());
        }
        menu.build();
        menu.setCancelClickEvent(false);
        menu.open(GamerManager.getGamer(p));
    }

    private IMenuButton closeButton() {
        IMenuButton close = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(14, "&cУНИЧТОЖИТЬ"));
        close.setClickEvent(buttonClickEvent -> buttonClickEvent.getWhoClicked().closeInventory());
        return close;
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
