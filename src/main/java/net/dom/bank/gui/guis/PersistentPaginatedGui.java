// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import org.bukkit.inventory.Inventory;
import java.util.LinkedHashMap;
import java.util.Iterator;
import org.bukkit.entity.HumanEntity;
import java.util.Collections;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.List;
import net.dom.bank.gui.components.Serializable;

class PersistentPaginatedGui extends PaginatedGui implements Serializable
{
    private final List<Page> pages;
    private final YamlConfiguration yamlConfiguration;
    
    public PersistentPaginatedGui(final int rows, final int pageSize, @NotNull final String title, final int pages) {
        super(rows, pageSize, title);
        this.pages = new ArrayList<Page>();
        this.yamlConfiguration = new YamlConfiguration();
        if (pages <= 0) {
            this.pages.add(new Page());
            return;
        }
        for (int i = 0; i < pages; ++i) {
            this.pages.add(new Page());
        }
    }
    
    public PersistentPaginatedGui(@NotNull final String title) {
        this(1, title);
    }
    
    public PersistentPaginatedGui(final int rows, @NotNull final String title) {
        this(rows, 0, title, 1);
    }
    
    public PersistentPaginatedGui(@NotNull final String title, final int pages) {
        this(1, 0, title, pages);
    }
    
    public PersistentPaginatedGui(final int rows, @NotNull final String title, final int pages) {
        this(rows, 0, title, pages);
    }
    
    @NotNull
    public Map<Integer, ItemStack> addItem(@NotNull final ItemStack... items) {
        return this.addItem(1, items);
    }
    
    @NotNull
    public Map<Integer, ItemStack> addItem(final int page, @NotNull final ItemStack... items) {
        int finalPage = page;
        if (page <= 0 || page > this.pages.size()) {
            finalPage = 1;
        }
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends ItemStack>)this.getInventory().addItem(items));
    }
    
    @Override
    public void open(@NotNull final HumanEntity player) {
        this.open(player, 1);
    }
    
    @Override
    public void open(@NotNull final HumanEntity player, final int openPage) {
        if (player.isSleeping()) {
            return;
        }
        if (openPage < this.pages.size() || openPage > 0) {
            this.setPageNum(openPage - 1);
        }
        this.getInventory().clear();
        this.populateGui();
        if (this.getPageSize() == 0) {
            this.setPageSize(this.calculatePageSize());
        }
        this.pages.get(this.getPageNum()).populatePage(this.getInventory());
        player.openInventory(this.getInventory());
    }
    
    @Override
    public boolean next() {
        if (this.getPageNum() + 1 >= this.pages.size()) {
            return false;
        }
        this.savePage();
        this.setPageNum(this.getPageNum() + 1);
        this.updatePage();
        return true;
    }
    
    @Override
    public boolean previous() {
        if (this.getPageNum() - 1 < 0) {
            return false;
        }
        this.savePage();
        this.setPageNum(this.getPageNum() - 1);
        this.updatePage();
        return true;
    }
    
    @Override
    public int getCurrentPageNum() {
        return this.getPageNum() + 1;
    }
    
    @Override
    void updatePage() {
        this.clearPage();
        this.populatePage();
    }
    
    @Override
    void clearPage() {
        for (int i = 0; i < this.getInventory().getSize(); ++i) {
            final ItemStack itemStack = this.getInventory().getItem(i);
            if (itemStack != null) {
                if (this.getGuiItems().get(i) == null) {
                    this.getInventory().setItem(i, (ItemStack)null);
                }
            }
        }
    }
    
    void savePage() {
        this.pages.get(this.getPageNum()).savePage(this.getInventory(), this.getGuiItems());
    }
    
    private void populatePage() {
        this.pages.get(this.getPageNum()).populatePage(this.getInventory());
    }
    
    @NotNull
    @Override
    public List<String> encodeGui() {
        final int inventorySize = this.getInventory().getSize();
        final List<String> pageItems = new ArrayList<String>();
        for (final Page page : this.pages) {
            this.yamlConfiguration.set("inventory", (Object)page.getContent(inventorySize));
        }
        return pageItems;
    }
    
    @Override
    public void decodeGui(@NotNull final List<String> encodedItem) {
        for (int i = 0; i < this.pages.size(); ++i) {
            final Page page = this.pages.get(i);
            final List<ItemStack> content = (List<ItemStack>)this.yamlConfiguration.get("inventory");
            if (content != null) {
                page.loadPageContent(content, this.getInventory().getSize());
            }
        }
    }
    
    private static class Page
    {
        private final Map<Integer, ItemStack> pageItems;
        
        private Page() {
            this.pageItems = new LinkedHashMap<Integer, ItemStack>();
        }
        
        private void populatePage(@NotNull final Inventory inventory) {
            for (final Map.Entry<Integer, ItemStack> entry : this.pageItems.entrySet()) {
                inventory.setItem((int)entry.getKey(), (ItemStack)entry.getValue());
            }
        }
        
        private void savePage(@NotNull final Inventory inventory, @NotNull final Map<Integer, GuiItem> guiItems) {
            for (int i = 0; i < inventory.getSize(); ++i) {
                final ItemStack itemStack = inventory.getItem(i);
                if (itemStack == null) {
                    this.pageItems.remove(i);
                }
                else if (guiItems.get(i) == null) {
                    this.pageItems.put(i, itemStack);
                }
            }
        }
        
        @NotNull
        private ItemStack[] getContent(final int inventorySize) {
            final ItemStack[] content = new ItemStack[inventorySize];
            for (int i = 0; i < inventorySize; ++i) {
                content[i] = this.pageItems.get(i);
            }
            return content;
        }
        
        private void loadPageContent(@NotNull final List<ItemStack> items, final int inventorySize) {
            this.pageItems.clear();
            for (int i = 0; i < inventorySize; ++i) {
                final ItemStack item = items.get(i);
                if (item != null) {
                    this.pageItems.put(i, item);
                }
            }
        }
    }
}
