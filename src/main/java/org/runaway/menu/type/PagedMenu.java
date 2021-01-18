package org.runaway.menu.type;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.runaway.menu.IMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.PagedButton;
import org.runaway.menu.exceptions.InventoryEmptyException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PagedMenu extends IMenu {

    private BiMap<Integer, Inventory> pages = HashBiMap.create();
    private boolean created;
    private PagedMenu(int rows, String title) {
        setInventorySize(rows);
        setTitle(title);
    }

    public static PagedMenu create(int rows, String title) {
        return new PagedMenu(rows, title);
    }

    @Override
    public Inventory build() {
        if(created) {
            return getInventory();
        }
        created = true;
        List<PagedButton> pagedButtons = getButtons().stream().filter(button -> button instanceof PagedButton)
                .map(button -> (PagedButton) button).collect(Collectors.toList());
        int pagesRequired = 0;
        int size = 0;

        if (pagedButtons.isEmpty())
            try {
                throw new InventoryEmptyException("Inventory is empty!");
            } catch (InventoryEmptyException e) {
                e.printStackTrace();
            }

        size = getSize();

        if (getEmptySlots() % pagedButtons.size() == 0) {
            pagesRequired = pagedButtons.size() / getEmptySlots();
        } else if (pagedButtons.size() <= getEmptySlots()) {
            pagesRequired = 1;
        } else
            pagesRequired = Math.round(pagedButtons.size() / getEmptySlots());

        if ((pagesRequired * getEmptySlots()) < pagedButtons.size())
            pagesRequired++;

        int currentPage = 1;

        List<Integer> inventoriesWithDummies = new ArrayList<>();

        for (PagedButton button : pagedButtons) {

            Inventory inventory = pages.containsKey(currentPage) ? pages.get(currentPage)
                    : pages.put(currentPage, Bukkit.createInventory(this, size, getTitle()
                    .replace("%current%", "" + currentPage).replace("%available%", "" + pagesRequired)));

            if (inventory == null)
                inventory = pages.get(currentPage);

            if (pagesRequired == 1) {

                if (!inventoriesWithDummies.contains(currentPage)) {
                    setDummies(inventory);
                    inventoriesWithDummies.add(currentPage);
                }

                inventory.addItem(button.getItem());
                continue;

            }

            if (!inventoriesWithDummies.contains(currentPage)) {
                setDummies(inventory);

                if (currentPage == 1) {
                    if (pagesRequired > 1) {

                        setButtonByIdentifer(DefaultButtons.NEXT_PAGE.getIdentifier(), inventory);

                    }
                } else {

                    setButtonByIdentifer(DefaultButtons.LAST_PAGE.getIdentifier(), inventory);

                    if (currentPage < pagesRequired) {
                        setButtonByIdentifer(DefaultButtons.NEXT_PAGE.getIdentifier(), inventory);
                    }

                }
                inventoriesWithDummies.add(currentPage);
            }

            if (inventory.firstEmpty() != -1)
                inventory.addItem(button.getItem());
            else {

                currentPage++;
                pages.put(currentPage, Bukkit.createInventory(this, size,
                        getTitle().replace("%current%", "" + currentPage).replace("%available%", "" + pagesRequired)));
                inventory = pages.get(currentPage);
                if (currentPage == pagesRequired) {
                    setDummies(inventory);
                    setButtonByIdentifer(DefaultButtons.LAST_PAGE.getIdentifier(), inventory);
                }

                inventory.addItem(button.getItem());

            }
        }

        setInventory(pages.get(pages.keySet().stream().findFirst().orElse(1)));

        return pages.get(1);

    }

    public Inventory getNextPage(Inventory clickedInventory) {

        if (pages.inverse().containsKey(clickedInventory))
            return pages.get(pages.inverse().get(clickedInventory) + 1);
        return null;

    }

    public Inventory getLastPage(Inventory clickedInventory) {

        if (pages.inverse().containsKey(clickedInventory))
            return pages.get(pages.inverse().get(clickedInventory) - 1);
        return null;

    }
}
