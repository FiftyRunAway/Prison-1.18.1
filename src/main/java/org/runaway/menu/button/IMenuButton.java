package org.runaway.menu.button;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.runaway.menu.IMenu;
import org.runaway.menu.events.ButtonClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

abstract public class IMenuButton implements Cloneable {

    private ItemStack item;
    private boolean isFiller = false;
    private int slot;
    private String identifier;
    private Consumer<ButtonClickEvent> clickEvent;
    private Map<String, Object> itemData = new HashMap<>();

    /**
     * @return a map with String and Object aka Item data.
     */
    public Map<String, Object> getItemData() {
        return itemData;
    }

    /**
     * @param item
     *            represents a ItemStack that's gonna be used in {@link IMenu}
     * @param slot
     *            represents a {@link org.bukkit.inventory.Inventory} slot where the
     *            ItemStack is going to be placed.
     * @param identifier
     *            is a id like for the API to detect items.
     */

    public IMenuButton(ItemStack item, int slot, String identifier) {

        this.item = item;
        this.slot = slot;
        this.identifier = identifier;

    }

    /**
     * @param item
     *            represents a ItemStack that's gonna be used in {@link IMenu}
     * @param slot
     *            represents a {@link org.bukkit.inventory.Inventory} slot where the
     *            ItemStack is going to be placed.
     * @param defaultButton
     *            is a one of the DefaultButtons which gives {@link IMenuButton} a
     *            identifier and a task if defined.
     */

    public IMenuButton(ItemStack item, int slot, DefaultButtons defaultButton) {

        this.item = item;
        this.slot = slot;
        this.identifier = defaultButton.getIdentifier();
        this.clickEvent = defaultButton.getConsumer();
        if (defaultButton.getInitializer() != null)
            defaultButton.getInitializer().accept(this);

    }

    /**
     * @param item
     *            represents a ItemStack that's gonna be used in {@link IMenu}
     * @param slot
     *            represents a {@link org.bukkit.inventory.Inventory} slot where the
     *            ItemStack is going to be placed.
     * @param defaultButton
     *            is a one of the DefaultButtons which gives {@link IMenuButton} a
     *            identifier and a task if defined.
     * @param value
     *            is a value that will be set for an task to find it. For example we
     *            use this constructor or
     *            {@link #assignTask(DefaultButtons, Object)} when we have a open
     *            task, the value is name of the inventory to open
     */

    public IMenuButton(ItemStack item, int slot, DefaultButtons defaultButton, Object value) {

        this.item = item;
        this.slot = slot;
        this.identifier = defaultButton.getIdentifier();
        this.clickEvent = defaultButton.getConsumer();
        assignData(defaultButton.getIdentifier(), value);
        if (defaultButton.getInitializer() != null)
            defaultButton.getInitializer().accept(this);

    }

    /**
     * @param item
     *            represents a ItemStack that's gonna be used in {@link IMenu}
     * @param identifier
     *            is a id like for the API to detect items.
     *            NOTE the slot must be set later on, otherwise errors will be threw
     */

    public IMenuButton(ItemStack item, String identifier) {

        this.item = item;
        this.slot = -1;
        this.identifier = identifier;

    }

    /**
     *
     * @param task
     *            a custom task which automates the button.
     * @return a {@link IMenuButton}
     */

    public IMenuButton assignTask(DefaultButtons task) {

        this.clickEvent = task.getConsumer();
        this.identifier = task.getIdentifier();
        if (task.getInitializer() != null)
            task.getInitializer().accept(this);
        return this;

    }

    /**
     *
     * @param task
     *            a custom task which automates the button.
     * @param value
     *            is to set custom value by the task identifier, for example used in
     *            "open" the value is the menu name.
     * @return a {@link IMenuButton}
     */

    public IMenuButton assignTask(DefaultButtons task, Object value) {

        this.clickEvent = task.getConsumer();
        this.identifier = task.getIdentifier();
        assignData(task.getIdentifier(), value);
        if (task.getInitializer() != null)
            task.getInitializer().accept(this);
        return this;

    }

    /**
     * @return returns a ButtonClickEvent or in other words
     *         {@link Consumer<ButtonClickEvent>}
     */

    public Consumer<ButtonClickEvent> getClickEvent() {
        return clickEvent;
    }

    /**
     * @param key
     *            is the key of the map
     * @param object
     *            is the value to be stored
     * @return a {@link IMenuButton}
     */

    public IMenuButton assignData(String key, Object object) {
        getItemData().remove(key);
        getItemData().put(key, object);
        return this;
    }

    /**
     * @param key
     *            is the map key to be found
     * @return {@link Boolean}
     */

    public boolean containsData(String key) {
        return getItemData().containsKey(key);
    }

    /**
     * @return a item defined in constructor
     */

    public ItemStack getItem() {
        return item;
    }

    /**
     * @return a slot of a {@link org.bukkit.inventory.Inventory}
     */

    public int getSlot() {
        return slot;
    }

    /**
     * @return a identifier defined or assigned from constructor or assignTask
     */

    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param second
     *            an itemstack to be compared with one defined in constructor
     * @return {@link Boolean}
     */

    public boolean equalsItem(ItemStack second) {

        Validate.notNull(second, "Cannot compare a null object!");

        boolean similar = false;

        ItemStack first = getItem();

        if (first == null) {
            return false;
        }

        boolean sameTypeId = (first.getType() == second.getType());
        boolean sameDurability = (first.getDurability() == second.getDurability());
        boolean sameHasItemMeta = (first.hasItemMeta() == second.hasItemMeta());
        boolean sameEnchantments = (first.getEnchantments().equals(second.getEnchantments()));
        boolean sameItemMeta = true;

        if (sameHasItemMeta) {
            sameItemMeta = Bukkit.getItemFactory().equals(first.getItemMeta(), second.getItemMeta());
        }

        if (sameTypeId && sameDurability && sameHasItemMeta && sameEnchantments && sameItemMeta) {
            similar = true;
        }

        return similar;
    }

    /**
     * @param option
     *            a button option
     * @param value
     *            a {@link Boolean} value
     * @return a {@link IMenuButton}
     */

    public IMenuButton assignOption(ButtonOptions option, boolean value) {
        assignData(option.getIdentifier(), value);
        return this;
    }

    /**
     * @return a {@link Boolean} if the Button is a dummy one.
     */

    public boolean isDummy() {

        return containsData(ButtonOptions.IS_DUMMY.getIdentifier())
                && (boolean) getItemData().get(ButtonOptions.IS_DUMMY.getIdentifier());
    }

    /**
     * @return a {@link Boolean} if the Button is a filler
     */

    public boolean isFiller() {
        return isFiller;
    }

    /**
     * @param filler
     *            sets button {@link #isFiller} to given value
     */

    public void setFiller(boolean filler) {
        isFiller = filler;
    }

    /**
     *
     * @param slot
     *            sets a slot of a {@link org.bukkit.inventory.Inventory}
     * @return a {@link IMenuButton}
     */

    public IMenuButton setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    /**
     * @return a copy of {@link IMenuButton}
     */

    @Override
    public IMenuButton clone() {
        try {
            return (IMenuButton) super.clone();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Sets custom button click event.
     *
     * @param clickEvent
     *            is a consumer or in other words ButtonClickEvent
     */
    public void setClickEvent(Consumer<ButtonClickEvent> clickEvent) {
        this.clickEvent = clickEvent;
    }
}
