// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import java.util.Collections;
import java.util.Iterator;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import java.util.Optional;
import java.util.function.Consumer;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import net.dom.bank.gui.components.InteractionModifier;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.List;

public class PaginatedGui extends BaseGui
{
    private final List<GuiItem> pageItems;
    private final Map<Integer, GuiItem> currentPage;
    private int pageSize;
    private int pageNum;
    
    public PaginatedGui(final int rows, final int pageSize, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
        this.pageItems = new ArrayList<GuiItem>();
        this.pageNum = 1;
        this.pageSize = pageSize;
        final int inventorySize = rows * 9;
        this.currentPage = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }
    
    @Deprecated
    public PaginatedGui(final int rows, final int pageSize, @NotNull final String title) {
        super(rows, title);
        this.pageItems = new ArrayList<GuiItem>();
        this.pageNum = 1;
        this.pageSize = pageSize;
        final int inventorySize = rows * 9;
        this.currentPage = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }
    
    @Deprecated
    public PaginatedGui(final int rows, @NotNull final String title) {
        this(rows, 0, title);
    }
    
    @Deprecated
    public PaginatedGui(@NotNull final String title) {
        this(2, title);
    }
    
    public BaseGui setPageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
    
    public void addItem(@NotNull final GuiItem item) {
        this.pageItems.add(item);
    }
    
    @Override
    public void addItem(@NotNull final GuiItem... items) {
        this.pageItems.addAll(Arrays.asList(items));
    }
    
    @Override
    public void update() {
        this.getInventory().clear();
        this.populateGui();
        this.updatePage();
    }
    
    public void updatePageItem(final int slot, @NotNull final ItemStack itemStack) {
        if (!this.currentPage.containsKey(slot)) {
            return;
        }
        final GuiItem guiItem = this.currentPage.get(slot);
        guiItem.setItemStack(itemStack);
        this.getInventory().setItem(slot, guiItem.getItemStack());
    }
    
    public void updatePageItem(final int row, final int col, @NotNull final ItemStack itemStack) {
        this.updateItem(this.getSlotFromRowCol(row, col), itemStack);
    }
    
    public void updatePageItem(final int slot, @NotNull final GuiItem item) {
        if (!this.currentPage.containsKey(slot)) {
            return;
        }
        final GuiItem oldItem = this.currentPage.get(slot);
        final int index = this.pageItems.indexOf(this.currentPage.get(slot));
        this.currentPage.put(slot, item);
        this.pageItems.set(index, item);
        this.getInventory().setItem(slot, item.getItemStack());
    }
    
    public void updatePageItem(final int row, final int col, @NotNull final GuiItem item) {
        this.updateItem(this.getSlotFromRowCol(row, col), item);
    }
    
    public void removePageItem(@NotNull final GuiItem item) {
        this.pageItems.remove(item);
        this.updatePage();
    }
    
    public void removePageItem(@NotNull final ItemStack item) {
        final Optional<GuiItem> guiItem = this.pageItems.stream().filter(it -> it.getItemStack().equals((Object)item)).findFirst();
        guiItem.ifPresent(this::removePageItem);
    }
    
    @Override
    public void open(@NotNull final HumanEntity player) {
        this.open(player, 1);
    }
    
    public void open(@NotNull final HumanEntity player, final int openPage) {
        if (player.isSleeping()) {
            return;
        }
        if (openPage <= this.getPagesNum() || openPage > 0) {
            this.pageNum = openPage;
        }
        this.getInventory().clear();
        this.currentPage.clear();
        this.populateGui();
        if (this.pageSize == 0) {
            this.pageSize = this.calculatePageSize();
        }
        this.populatePage();
        player.openInventory(this.getInventory());
    }
    
    @NotNull
    @Override
    public BaseGui updateTitle(@NotNull final String title) {
        this.setUpdating(true);
        final List<HumanEntity> viewers = new ArrayList<HumanEntity>(this.getInventory().getViewers());
        this.setInventory(Bukkit.createInventory((InventoryHolder)this, this.getInventory().getSize(), title));
        for (final HumanEntity player : viewers) {
            this.open(player, this.getPageNum());
        }
        this.setUpdating(false);
        return this;
    }
    
    @NotNull
    public Map<Integer, GuiItem> getCurrentPageItems() {
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends GuiItem>)this.currentPage);
    }
    
    @NotNull
    public List<GuiItem> getPageItems() {
        return Collections.unmodifiableList((List<? extends GuiItem>)this.pageItems);
    }
    
    public int getCurrentPageNum() {
        return this.pageNum;
    }
    
    public int getNextPageNum() {
        if (this.pageNum + 1 > this.getPagesNum()) {
            return this.pageNum;
        }
        return this.pageNum + 1;
    }
    
    public int getPrevPageNum() {
        if (this.pageNum - 1 == 0) {
            return this.pageNum;
        }
        return this.pageNum - 1;
    }
    
    public boolean next() {
        if (this.pageNum + 1 > this.getPagesNum()) {
            return false;
        }
        ++this.pageNum;
        this.updatePage();
        return true;
    }
    
    public boolean previous() {
        if (this.pageNum - 1 == 0) {
            return false;
        }
        --this.pageNum;
        this.updatePage();
        return true;
    }
    
    GuiItem getPageItem(final int slot) {
        return this.currentPage.get(slot);
    }
    
    private List<GuiItem> getPageNum(final int givenPage) {
        final int page = givenPage - 1;
        final List<GuiItem> guiPage = new ArrayList<GuiItem>();
        int max = page * this.pageSize + this.pageSize;
        if (max > this.pageItems.size()) {
            max = this.pageItems.size();
        }
        for (int i = page * this.pageSize; i < max; ++i) {
            guiPage.add(this.pageItems.get(i));
        }
        return guiPage;
    }
    
    public int getPagesNum() {
        return (int)Math.ceil(this.pageItems.size() / (double)this.pageSize);
    }
    
    private void populatePage() {
        for (final GuiItem guiItem : this.getPageNum(this.pageNum)) {
            for (int slot = 0; slot < this.getRows() * 9; ++slot) {
                if (this.getGuiItem(slot) == null && this.getInventory().getItem(slot) == null) {
                    this.currentPage.put(slot, guiItem);
                    this.getInventory().setItem(slot, guiItem.getItemStack());
                    break;
                }
            }
        }
    }
    
    Map<Integer, GuiItem> getMutableCurrentPageItems() {
        return this.currentPage;
    }
    
    void clearPage() {
        for (final Map.Entry<Integer, GuiItem> entry : this.currentPage.entrySet()) {
            this.getInventory().setItem((int)entry.getKey(), (ItemStack)null);
        }
    }
    
    public void clearPageItems(final boolean update) {
        this.pageItems.clear();
        if (update) {
            this.update();
        }
    }
    
    public void clearPageItems() {
        this.clearPageItems(false);
    }
    
    int getPageSize() {
        return this.pageSize;
    }
    
    int getPageNum() {
        return this.pageNum;
    }
    
    void setPageNum(final int pageNum) {
        this.pageNum = pageNum;
    }
    
    void updatePage() {
        this.clearPage();
        this.populatePage();
    }
    
    int calculatePageSize() {
        int counter = 0;
        for (int slot = 0; slot < this.getRows() * 9; ++slot) {
            if (this.getInventory().getItem(slot) == null) {
                ++counter;
            }
        }
        return counter;
    }
}
