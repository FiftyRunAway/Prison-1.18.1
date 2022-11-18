package org.runaway.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.button.PagedButton;
import org.runaway.menu.design.MenuDesigner;
import org.runaway.menu.events.ButtonClickEvent;
import org.runaway.utils.ExampleItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class IMenu implements InventoryHolder {

    /**
     * A list of {@link IMenuButton} they are set later when {@link IMenu#build()}
     * is ran
     */
    private Map<Integer, IMenuButton> buttons = new HashMap();

    /**
     * A map which stores data for the inventory.
     */

    private Map<String, Object> inventoryData = new HashMap<>();

    /**
     * Parent / Child system
     * Where String is a identifier for the {@link IMenu}
     * Where IMenu is a child of {@link IMenu}
     */

    private Map<String, IMenu> childs = new HashMap<>();

    @Setter @Getter
    boolean cancelClickEvent;

    public void setButtonClickListener(Consumer<ButtonClickEvent> buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public void setCloseListener(Consumer<InventoryCloseEvent> closeListener) {
        this.closeListener = closeListener;
    }

    public void setOpenListener(Consumer<InventoryOpenEvent> openListener) {
        this.openListener = openListener;
    }

    public void setPickupListener(Consumer<InventoryPickupItemEvent> pickupListener) {
        this.pickupListener = pickupListener;
    }

    public void setItemClickEvent(Consumer<InventoryClickEvent> itemClickEvent) {
        this.itemClickEvent = itemClickEvent;
    }

    public IMenu setType(InventoryType type) {
        this.type = type;
        return this;
    }

    public InventoryType getType() {
        return type;
    }

    public void open(Gamer gamer, String texture) {
        if(gamer.getCurrentIMenu() != null) {
            gamer.getPlayer().closeInventory();
        }
        build();
        gamer.setCurrentIMenu(this);
        gamer.getPlayer().openInventory(getInventory());

        if (Prison.useItemsAdder) {
            FontImageWrapper tex = new FontImageWrapper(texture);
            if (tex.exists()) TexturedInventoryWrapper.setPlayerInventoryTexture(gamer.getPlayer(), tex);
        }
    }

    public void open(Gamer gamer) {
        open(gamer, null);
    }

    private Consumer<ButtonClickEvent> buttonClickListener;
    private Consumer<InventoryCloseEvent> closeListener;
    private Consumer<InventoryOpenEvent> openListener;
    private Consumer<InventoryPickupItemEvent> pickupListener;
    private Consumer<InventoryClickEvent> itemClickEvent;

    private MenuDesigner design;

    private IMenu parent;
    private int size = 27;
    final private int maxSize = 54;
    private String title = "Inventory Title";
    private InventoryType type = null;

    public void setAnimation(MenuAnimation animation) {
        this.animation = animation;
    }

    private MenuAnimation animation;

    /**
     * This method should be ran whenever {@link IMenu#build()} is called.
     *
     * @param inventory
     *            is a Inventory "_"
     */

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    private Inventory inventory = null;

    public abstract Inventory build();

    @Override
    public Inventory getInventory() {
        return inventory;
    }


    /**
     * @param rebuild
     *            sets a rule if the {@link IMenu#build()} needs to be called again
     *            or if {@link #inventory} is null
     * @return a Inventory
     */

    public Inventory getInventory(boolean rebuild) {
        if (rebuild)
            return build();
        if (inventory != null)
            return inventory;
        else
            return build();
    }

    /**
     * Adds button to the {@link #buttons} list
     *
     * @param button
     *            is a object that extends {@link IMenuButton}
     * @return a {@link IMenu}
     */

    public IMenu addButton(IMenuButton button) {

        buttons.put(button.getSlot(), button);
        if(getInventory() != null) {
            getInventory().setItem(button.getSlot(), button.getItem());
        }
        return this;
    }

    public IMenuButton getButton(int slot) {
        return getButtons().get(slot);
    }

    public IMenu setItem(int slot, ItemStack itemStack) {
        return addButton(DefaultButtons.FILLER.getButtonOfItemStack(itemStack).setSlot(slot));
    }
    /**
     * Tries to find a {@link IMenuButton} by {@link ItemStack}
     *
     * @param compare
     *            is a {@link ItemStack} that should be compared when looking for
     *            {@link IMenuButton}
     * @return a null or a {@link IMenuButton}
     */

    IMenuButton findButtonByItem(ItemStack compare) {
        return buttons.values().stream().filter(item -> item.equalsItem(compare)).findFirst().orElse(null);
    }

    public IMenuButton findButtonByData(String key) {
        return buttons.values().stream().filter(item -> item.containsData(key)).findFirst().orElse(null);
    }

    /**
     * Tries to find a {@link IMenuButton} by {@link IMenuButton#getIdentifier()}
     *
     * @param identifier
     *            is a {@link IMenuButton#getIdentifier()}
     * @return a null or a {@link IMenuButton}
     */

    private IMenuButton findButtonByIdentifier(String identifier) {
        return buttons.values().stream().filter(item -> item.getIdentifier().equalsIgnoreCase(identifier)).findFirst()
                .orElse(null);
    }

    /**
     * Sets a parent of {@link IMenu}
     *
     * @param menu
     *            is a parent of this {@link IMenu}
     * @return a {@link IMenu}
     */

    private IMenu setParent(IMenu menu) {
        parent = menu;
        return this;
    }

    /**
     * @return a parent of this {@link IMenu}
     */

    public IMenu getParent() {
        return parent;
    }

    /**
     * Assigns data to {@link #inventoryData}
     *
     * @param key
     *            is a key for the map
     * @param object
     *            is a value for the map
     * @return a {@link IMenu}
     */

    public IMenu assignData(String key, Object object) {
        getInventoryData().put(key, object);
        return this;
    }

    /**
     * @return a {@link #inventoryData}
     */
    private Map<String, Object> getInventoryData() {
        return inventoryData;
    }

    /**
     * @return specified data from inventory
     */
    @SuppressWarnings("unchecked")
    public <T> T getInventoryData(String key) {
        return (T) getInventoryData().get(key);
    }

    /**
     * @param key
     *            is a key that will searched for in {@link #inventoryData}
     * @return a {@link Boolean} true if the key was found in {@link #inventoryData}
     *         or false if not.
     */

    public boolean containsData(String key) {
        return getInventoryData().containsKey(key);
    }

    /**
     * @return a {@link IMenu} first menu that was created.
     *         Can be called anywhere in the {@link IMenu}
     */

    public IMenu getMainMenu() {
        if (parent == null)
            return this;
        else
            return parent.getMainMenu();
    }

    /**
     * @param name
     *            name of the child
     * @param menu
     *            {@link IMenu}
     * @return a {@link IMenu}
     */
    public IMenu addChild(String name, IMenu menu) {
        menu.setParent(this);
        childs.put(name, menu);
        return this;
    }

    /**
     * @param name
     *            of a child {@link IMenu}
     * @return link {@link IMenu}
     */

    public IMenu getChild(String name) {
        return childs.get(name);
    }

    /**
     * @return returns all childs of {@link IMenu}
     */

    public Map<String, IMenu> getChilds() {
        return childs;
    }

    /**
     * @return a map of all available menus from first to last children
     */

    public Map<String, IMenu> getAllMenus() {

        Map<String, IMenu> pages = new HashMap<>();

        IMenu currentMenu = this;

        if (currentMenu.getParent() == null)
            return currentMenu.getChilds();

        while (currentMenu.getParent() != null) {

            pages.putAll(currentMenu.childs);

            currentMenu = currentMenu.getParent();

        }

        pages.putAll(currentMenu.childs);

        return pages;

    }

    /**
     * @return a list of {@link IMenuButton} all available buttons
     */

    public Map<Integer, IMenuButton> getButtons() {
        return buttons;
    }

    /**
     * @param rowsCount
     *            is a number not bigger than 6 as max inventory size is 54.
     * @return a {@link IMenu}
     */

    public IMenu setInventorySize(int rowsCount) {

        int equals = rowsCount * 9;

        if (equals > maxSize) {

            this.size = maxSize;

        } else {

            this.size = equals;

        }
        return this;

    }

    /**
     * @return a size of the inventory
     */

    public int getSize() {
        return size;
    }

    /**
     * @return all empty slots = (inventory size - dummies size)
     */

    public int getEmptySlots() {

        return getSize() - amountOfDummiesInMenu();
    }

    /**
     * @return a title of {@link Inventory}
     */

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    /**
     * @param title
     *            sets title of an {@link Inventory}
     */

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return a click listener, if not set null
     */

    public Consumer<ButtonClickEvent> getClickListener() {
        return buttonClickListener;
    }

    /**
     * @return a close listener, if not set null
     */

    public Consumer<InventoryCloseEvent> getCloseListener() {
        return closeListener;
    }

    /**
     * @return a open listener, if not set null
     */

    public Consumer<InventoryOpenEvent> getOpenListener() {
        return openListener;
    }

    /**
     * @return a final inventory size which is 54.
     */

    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @param design
     *            sets design of an menu {@link IMenu}
     * @return a {@link IMenu}
     */

    public IMenu setDesign(MenuDesigner design) {
        this.design = design;
        return this;
    }

    /**
     * @return a Design of an {@link IMenu} {@link MenuDesigner}
     */

    public MenuDesigner getDesign() {
        return design;
    }

    /**
     * @return amount of dummies in the {@link #buttons}
     */

    public int amountOfDummiesInMenu() {

        return (int) buttons.values().stream().filter(IMenuButton::isDummy).count();

    }

    /**
     * @param inventory
     *            is where to place dummies
     * @return a {@link IMenu}
     */

    public IMenu setDummies(Inventory inventory) {

        /*
         * First we gotta set filler dummies
         */
        for (IMenuButton button : getButtons().values().stream().filter(it -> it.isDummy() && it.isFiller())
                .collect(Collectors.toList())) {
            if (button instanceof PagedButton)
                continue;
            inventory.setItem(button.getSlot(), button.getItem());

        }

        /*
         * Then we set everything else
         */
        for (IMenuButton button : getButtons().values().stream().filter(it -> !it.isDummy() && !it.isFiller())
                .collect(Collectors.toList())) {
            if (button instanceof PagedButton)
                continue;
            inventory.setItem(button.getSlot(), button.getItem());

        }
        return this;

    }

    /**
     * @param identifier
     *            is a identifier of a button in {@link #buttons}
     * @param inventory
     *            is a {@link Inventory}
     * @return a {@link IMenu}
     */

    public IMenu setButtonByIdentifer(String identifier, Inventory inventory) {

        IMenuButton button = findButtonByIdentifier(identifier);
        if (button != null) {
            inventory.setItem(button.getSlot(), button.getItem());
        }
        return this;
    }

    /**
     * @return a {@link MenuAnimation} if not set null
     */

    public MenuAnimation getAnimation() {
        return animation;
    }

    /**
     * Changes title of {@link Inventory} and {@link IMenu}
     *
     * @param title
     *            is a string, make sure that it doesn't exceed minecraft limit
     *            which is 32
     * @return a {@link IMenu}
     */

    public IMenu changeTitle(String title) {

        setTitle(title);
        getInventory().getViewers()
                .forEach(player -> MenuUtil.changeTitle(((Player) player), title, "minecraft:chest"));
        return this;

    }

    public Consumer<InventoryPickupItemEvent> getPickupListener() {
        return pickupListener;
    }

    public Consumer<InventoryClickEvent> getItemClickEvent() {
        return itemClickEvent;
    }
}
