package org.runaway.menu.button;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.function.Consumer;
import org.runaway.menu.IMenu;
import org.runaway.menu.MenuLang;
import org.runaway.menu.design.MenuDesigner;
import org.runaway.menu.events.ButtonClickEvent;
import org.runaway.menu.exceptions.PageNotFoundException;
import org.runaway.menu.exceptions.ParentNotFoundException;
import org.runaway.menu.type.PagedMenu;

import java.util.Map;

public enum DefaultButtons {

    /**
     * Filler is basically a dummy item mostly used in {@link MenuDesigner}
     */
    FILLER("filler", null, button -> {
        button.assignOption(ButtonOptions.IS_DUMMY, true);
        button.setFiller(true);
    }),

    /**
     * Next page button is basically a dummy for {@link PagedMenu} to switch pages.
     */

    NEXT_PAGE("nextPage", event -> {

        if (event.getClickedMenu() instanceof PagedMenu) {

            PagedMenu menu = ((PagedMenu) event.getClickedMenu());
            Inventory toOpen = menu.getNextPage(event.getClickedInventory());

            if (toOpen != null) {

                menu.assignData("closeReason", "switchPage");
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(toOpen);

            }

        }

    }),

    /**
     * Last page button is basically a dummy for {@link PagedMenu} to switch pages.
     */

    LAST_PAGE("lastPage", event -> {
        if (event.getClickedMenu() instanceof PagedMenu) {

            PagedMenu menu = ((PagedMenu) event.getClickedMenu());
            Inventory toOpen = menu.getLastPage(event.getClickedInventory());

            if (toOpen != null) {

                menu.assignData("closeReason", "switchPage");
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(toOpen);

            }

        }
    }),

    /**
     * Open can be used in any {@link IMenu} and forwards who clicked to defined
     * {@link IMenu}, if there's no page found, throws a
     * {@link PageNotFoundException}
     */

    OPEN("open", event -> {

        String nameOfInventory = event.getClickedButton().getIdentifier();

        Map<String, IMenu> allMenus = event.getClickedMenu().getAllMenus();

        if (allMenus.containsKey(nameOfInventory)) {

            Inventory toOpen = allMenus.get(nameOfInventory).getInventory();
            if (toOpen != null) {

                event.getClickedMenu().assignData("closeReason", "switchMenu");
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(toOpen);

            } else {
                event.getWhoClicked().sendMessage(MenuLang.PAGE_NOT_FOUND.getLang().replace("%name%", nameOfInventory));
                try {
                    throw new PageNotFoundException("Page named " + nameOfInventory + " not found!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else {
            event.getWhoClicked().sendMessage(MenuLang.PAGE_NOT_FOUND.getLang().replace("%name%", nameOfInventory));
            try {
                throw new PageNotFoundException("Page named " + nameOfInventory + " not found!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }),

    /**
     * Return can be used in any {@link IMenu} and forwards to parent of clicked
     * menu, if there's no parent found throws a
     * {@link ParentNotFoundException}
     */

    RETURN("return", event -> {

        Inventory toOpen = event.getClickedMenu().getParent().getInventory();
        if (toOpen != null) {

            event.getClickedMenu().assignData("closeReason", "return");
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().openInventory(toOpen);

        } else
            event.getWhoClicked().sendMessage(MenuLang.PAGE_NOT_FOUND.getLang().replace("%name%", "null"));
    });

    /**
     *
     * @return a identifier for the Button
     */

    public String getIdentifier() {
        return identifier;
    }

    public Consumer<ButtonClickEvent> getConsumer() {
        return consumer;
    }

    private String identifier;
    private Consumer<ButtonClickEvent> consumer;
    private Consumer<IMenuButton> initializer = null;

    DefaultButtons(String identifier, Consumer<ButtonClickEvent> consumer) {
        this.consumer = consumer;
        this.identifier = identifier;
    }

    DefaultButtons(String identifier, Consumer<ButtonClickEvent> consumer, Consumer<IMenuButton> initializer) {
        this.consumer = consumer;
        this.identifier = identifier;
        this.initializer = initializer;
    }

    /**
     * @param item
     *            a {@link ItemStack} which is set when {@link IMenu} is building...
     * @return a {@link MenuButton}
     */

    public MenuButton getButtonOfItemStack(ItemStack item) {
        return new MenuButton(item, -1, this);
    }

    public MenuButton getButtonOfItemStack(ItemStack item, Object value) {
        return new MenuButton(item, -1, this, value);
    }

    /**
     * @return a initializer which will be ran on {@link IMenuButton} constructor
     */

    public Consumer<IMenuButton> getInitializer() {
        return initializer;
    }
}
