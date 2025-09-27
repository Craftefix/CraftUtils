package dev.craftefix.craftUtils.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A GUI that supports multiple pages with automatic navigation
 */
public class PaginatedGUI extends CustomGUI {
    private final List<Page> pages;
    private int currentPage;
    private final int contentRows; // Rows available for content (excluding navigation row)
    private Consumer<Integer> pageChangeHandler;

    /**
     * Creates a new paginated GUI
     * @param plugin The plugin instance
     * @param title GUI title
     * @param contentRows Number of rows for content (1-5, navigation takes bottom row)
     */
    public PaginatedGUI(JavaPlugin plugin, String title, int contentRows) {
        super(plugin, contentRows + 1, title); // +1 for navigation row
        this.pages = new ArrayList<>();
        this.currentPage = 0;
        this.contentRows = contentRows;

        // Add navigation buttons
        setupNavigation();
    }

    /**
     * Sets up the navigation buttons on the bottom row
     */
    private void setupNavigation() {
        int navRow = contentRows;

        // Previous page button
        GUIItem prevButton = GUIItem.createButton(Material.SPECTRAL_ARROW, "§ePrevious Page")
            .setClickHandler(event -> {
                if (currentPage > 0) {
                    setPage(currentPage - 1);
                }
            });
        setItem(navRow, 2, prevButton.getItemStack(), prevButton.getClickHandler());

        // Page indicator (middle)
        GUIItem pageIndicator = GUIItem.createButton(Material.PAPER, "§fPage " + (currentPage + 1) + "/" + Math.max(1, pages.size()));
        setItem(navRow, 4, pageIndicator.getItemStack());

        // Next page button
        GUIItem nextButton = GUIItem.createButton(Material.SPECTRAL_ARROW, "§eNext Page")
            .setClickHandler(event -> {
                if (currentPage < pages.size() - 1) {
                    setPage(currentPage + 1);
                }
            });
        setItem(navRow, 6, nextButton.getItemStack(), nextButton.getClickHandler());
    }

    /**
     * Adds a page to the GUI
     * @param page The page to add
     */
    public void addPage(Page page) {
        pages.add(page);
        updateNavigation();
    }

    /**
     * Creates and adds a page with the given items
     * @param items List of GUIItems to add to the page
     */
    public void addPage(List<GUIItem> items) {
        Page page = new Page();
        int slot = 0;
        for (GUIItem item : items) {
            if (slot >= contentRows * 9) break; // Don't exceed content area
            page.setItem(slot, item);
            slot++;
        }
        addPage(page);
    }

    /**
     * Sets the current page
     * @param pageIndex The page index (0-based)
     */
    public void setPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pages.size()) {
            currentPage = pageIndex;
            refreshContent();
            updateNavigation();

            if (pageChangeHandler != null) {
                pageChangeHandler.accept(currentPage);
            }
        }
    }

    /**
     * Gets the current page index
     * @return Current page index
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Gets the total number of pages
     * @return Total pages
     */
    public int getTotalPages() {
        return pages.size();
    }

    /**
     * Clears all pages from the GUI
     */
    public void clearPages() {
        pages.clear();
        currentPage = 0;
        updateNavigation();
    }

    /**
     * Sets a handler for page changes
     * @param handler Consumer that receives the new page index
     */
    public void setPageChangeHandler(Consumer<Integer> handler) {
        this.pageChangeHandler = handler;
    }

    /**
     * Sets a back button on the navigation row
     * @param targetGUI The GUI to open when back button is clicked
     */
    public void setBackButton(int col, CustomGUI targetGUI) {
        setBackButton(contentRows, col, targetGUI);
    }

    /**
     * Sets a back button at specific row/column coordinates
     * @param row Row
     * @param col Column
     * @param targetGUI The GUI to open when back button is clicked
     */
    public void setBackButton(int row, int col, CustomGUI targetGUI) {
        GUIItem backButton = GUIItem.createButton(Material.ARROW, "§cBack")
            .setClickHandler(event -> targetGUI.open((org.bukkit.entity.Player) event.getWhoClicked()));
        setItem(row, col, backButton.getItemStack(), backButton.getClickHandler());
    }

    /**
     * Refreshes the content area with the current page
     */
    private void refreshContent() {
        // Clear content area
        for (int row = 0; row < contentRows; row++) {
            for (int col = 0; col < 9; col++) {
                inventory.setItem(row * 9 + col, null);
            }
        }

        // Fill background
        fillBackground();

        // Add current page content
        if (currentPage < pages.size()) {
            Page page = pages.get(currentPage);
            for (int slot = 0; slot < contentRows * 9; slot++) {
                GUIItem item = page.getItem(slot);
                if (item != null) {
                    setItem(slot, item.getItemStack(), item.getClickHandler());
                }
            }
        }
    }

    /**
     * Updates the navigation buttons and page indicator
     */
    private void updateNavigation() {
        int navRow = contentRows;

        // Update page indicator
        GUIItem pageIndicator = GUIItem.createButton(Material.PAPER,
            "§fPage " + (currentPage + 1) + "/" + Math.max(1, pages.size()));
        setItem(navRow, 4, pageIndicator.getItemStack());
    }

    /**
     * Represents a single page in the paginated GUI
     */
    public static class Page {
        private final GUIItem[] items;

        public Page() {
            this.items = new GUIItem[45]; // 5 rows * 9 columns
        }

        /**
         * Sets an item at a specific slot
         * @param slot The slot (0-44)
         * @param item The GUI item
         */
        public void setItem(int slot, GUIItem item) {
            if (slot >= 0 && slot < items.length) {
                items[slot] = item;
            }
        }

        /**
         * Gets the item at a specific slot
         * @param slot The slot
         * @return The GUI item or null
         */
        public GUIItem getItem(int slot) {
            if (slot >= 0 && slot < items.length) {
                return items[slot];
            }
            return null;
        }

        /**
         * Sets an item at row/column coordinates
         * @param row Row (0-4)
         * @param col Column (0-8)
         * @param item The GUI item
         */
        public void setItem(int row, int col, GUIItem item) {
            setItem(row * 9 + col, item);
        }
    }
}