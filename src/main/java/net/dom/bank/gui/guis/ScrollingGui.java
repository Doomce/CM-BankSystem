// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.bukkit.entity.HumanEntity;
import net.dom.bank.gui.components.InteractionModifier;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import net.dom.bank.gui.components.ScrollType;

public class ScrollingGui extends PaginatedGui
{
    private final ScrollType scrollType;
    private int scrollSize;
    
    public ScrollingGui(final int rows, final int pageSize, @NotNull final String title, @NotNull final ScrollType scrollType, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, pageSize, title, interactionModifiers);
        this.scrollSize = 0;
        this.scrollType = scrollType;
    }
    
    @Deprecated
    public ScrollingGui(final int rows, final int pageSize, @NotNull final String title, @NotNull final ScrollType scrollType) {
        super(rows, pageSize, title);
        this.scrollSize = 0;
        this.scrollType = scrollType;
    }
    
    @Deprecated
    public ScrollingGui(final int rows, final int pageSize, @NotNull final String title) {
        this(rows, pageSize, title, ScrollType.VERTICAL);
    }
    
    @Deprecated
    public ScrollingGui(final int rows, @NotNull final String title) {
        this(rows, 0, title, ScrollType.VERTICAL);
    }
    
    @Deprecated
    public ScrollingGui(final int rows, @NotNull final String title, @NotNull final ScrollType scrollType) {
        this(rows, 0, title, scrollType);
    }
    
    @Deprecated
    public ScrollingGui(@NotNull final String title) {
        this(2, title);
    }
    
    @Deprecated
    public ScrollingGui(@NotNull final String title, @NotNull final ScrollType scrollType) {
        this(2, title, scrollType);
    }
    
    @Override
    public boolean next() {
        if (this.getPageNum() * this.scrollSize + this.getPageSize() > this.getPageItems().size() + this.scrollSize) {
            return false;
        }
        this.setPageNum(this.getPageNum() + 1);
        this.updatePage();
        return true;
    }
    
    @Override
    public boolean previous() {
        if (this.getPageNum() - 1 == 0) {
            return false;
        }
        this.setPageNum(this.getPageNum() - 1);
        this.updatePage();
        return true;
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
        this.getInventory().clear();
        this.getMutableCurrentPageItems().clear();
        this.populateGui();
        if (this.getPageSize() == 0) {
            this.setPageSize(this.calculatePageSize());
        }
        if (this.scrollSize == 0) {
            this.scrollSize = this.calculateScrollSize();
        }
        if (openPage > 0 && openPage * this.scrollSize + this.getPageSize() <= this.getPageItems().size() + this.scrollSize) {
            this.setPageNum(openPage);
        }
        this.populatePage();
        player.openInventory(this.getInventory());
    }
    
    @Override
    void updatePage() {
        this.clearPage();
        this.populatePage();
    }
    
    private void populatePage() {
        for (final GuiItem guiItem : this.getPage(this.getPageNum())) {
            if (this.scrollType == ScrollType.HORIZONTAL) {
                this.putItemHorizontally(guiItem);
            }
            else {
                this.putItemVertically(guiItem);
            }
        }
    }
    
    private int calculateScrollSize() {
        int counter = 0;
        if (this.scrollType == ScrollType.VERTICAL) {
            boolean foundCol = false;
            for (int row = 1; row <= this.getRows(); ++row) {
                for (int col = 1; col <= 9; ++col) {
                    final int slot = this.getSlotFromRowCol(row, col);
                    if (this.getInventory().getItem(slot) == null) {
                        if (!foundCol) {
                            foundCol = true;
                        }
                        ++counter;
                    }
                }
                if (foundCol) {
                    return counter;
                }
            }
            return counter;
        }
        boolean foundRow = false;
        for (int col2 = 1; col2 <= 9; ++col2) {
            for (int row2 = 1; row2 <= this.getRows(); ++row2) {
                final int slot = this.getSlotFromRowCol(row2, col2);
                if (this.getInventory().getItem(slot) == null) {
                    if (!foundRow) {
                        foundRow = true;
                    }
                    ++counter;
                }
            }
            if (foundRow) {
                return counter;
            }
        }
        return counter;
    }
    
    private void putItemVertically(final GuiItem guiItem) {
        for (int slot = 0; slot < this.getRows() * 9; ++slot) {
            if (this.getInventory().getItem(slot) == null) {
                this.getMutableCurrentPageItems().put(slot, guiItem);
                this.getInventory().setItem(slot, guiItem.getItemStack());
                break;
            }
        }
    }
    
    private void putItemHorizontally(final GuiItem guiItem) {
        for (int col = 1; col < 10; ++col) {
            for (int row = 1; row <= this.getRows(); ++row) {
                final int slot = this.getSlotFromRowCol(row, col);
                if (this.getInventory().getItem(slot) == null) {
                    this.getMutableCurrentPageItems().put(slot, guiItem);
                    this.getInventory().setItem(slot, guiItem.getItemStack());
                    return;
                }
            }
        }
    }
    
    private List<GuiItem> getPage(final int givenPage) {
        final int page = givenPage - 1;
        final int pageItemsSize = this.getPageItems().size();
        final List<GuiItem> guiPage = new ArrayList<GuiItem>();
        int max = page * this.scrollSize + this.getPageSize();
        if (max > pageItemsSize) {
            max = pageItemsSize;
        }
        for (int i = page * this.scrollSize; i < max; ++i) {
            guiPage.add(this.getPageItems().get(i));
        }
        return guiPage;
    }
}
