// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.util;

import java.util.function.Consumer;
import java.util.Objects;
import java.util.ArrayList;
import net.dom.bank.gui.components.GuiType;
import net.dom.bank.gui.components.exception.GuiException;
import net.dom.bank.gui.guis.PaginatedGui;
import java.util.List;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import net.dom.bank.gui.guis.GuiItem;
import net.dom.bank.gui.guis.BaseGui;

public final class GuiFiller
{
    private final BaseGui gui;
    
    public GuiFiller(final BaseGui gui) {
        this.gui = gui;
    }
    
    public void fillTop(@NotNull final GuiItem guiItem) {
        this.fillTop(Collections.singletonList(guiItem));
    }
    
    public void fillTop(@NotNull final List<GuiItem> guiItems) {
        final List<GuiItem> items = this.repeatList(guiItems, this.gui.getRows() * 9);
        for (int i = 0; i < 9; ++i) {
            if (!this.gui.getGuiItems().containsKey(i)) {
                this.gui.setItem(i, items.get(i));
            }
        }
    }
    
    public void fillBottom(@NotNull final GuiItem guiItem) {
        this.fillBottom(Collections.singletonList(guiItem));
    }
    
    public void fillBottom(@NotNull final List<GuiItem> guiItems) {
        final int rows = this.gui.getRows();
        final List<GuiItem> items = this.repeatList(guiItems, rows * 9);
        for (int i = 9; i > 0; --i) {
            if (this.gui.getGuiItems().get(rows * 9 - i) == null) {
                this.gui.setItem(rows * 9 - i, items.get(i));
            }
        }
    }
    
    public void fillBorder(@NotNull final GuiItem guiItem) {
        this.fillBorder(Collections.singletonList(guiItem));
    }
    
    public void fillBorder(@NotNull final List<GuiItem> guiItems) {
        final int rows = this.gui.getRows();
        if (rows <= 2) {
            return;
        }
        final List<GuiItem> items = this.repeatList(guiItems, rows * 9);
        for (int i = 0; i < rows * 9; ++i) {
            if (i <= 8 || i >= rows * 9 - 9 || i == 9 || i == 18 || i == 27 || i == 36 || i == 17 || i == 26 || i == 35 || i == 44) {
                this.gui.setItem(i, items.get(i));
            }
        }
    }
    
    public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, @NotNull final GuiItem guiItem) {
        this.fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(guiItem));
    }
    
    public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, @NotNull final List<GuiItem> guiItems) {
        final int minRow = Math.min(rowFrom, rowTo);
        final int maxRow = Math.max(rowFrom, rowTo);
        final int minCol = Math.min(colFrom, colTo);
        final int maxCol = Math.max(colFrom, colTo);
        final int rows = this.gui.getRows();
        final List<GuiItem> items = this.repeatList(guiItems, rows * 9);
        for (int row = 1; row <= rows; ++row) {
            for (int col = 1; col <= 9; ++col) {
                final int slot = this.getSlotFromRowCol(row, col);
                if (row >= minRow && row <= maxRow && col >= minCol) {
                    if (col <= maxCol) {
                        this.gui.setItem(slot, items.get(slot));
                    }
                }
            }
        }
    }
    
    public void fill(@NotNull final GuiItem guiItem) {
        this.fill(Collections.singletonList(guiItem));
    }
    
    public void fill(@NotNull final List<GuiItem> guiItems) {
        if (this.gui instanceof PaginatedGui) {
            throw new GuiException("Full filling a GUI is not supported in a Paginated GUI!");
        }
        final GuiType type = this.gui.guiType();
        int fill;
        if (type == GuiType.CHEST) {
            fill = this.gui.getRows() * type.getLimit();
        }
        else {
            fill = type.getLimit();
        }
        final int rows = this.gui.getRows();
        final List<GuiItem> items = this.repeatList(guiItems, rows * 9);
        for (int i = 0; i < fill; ++i) {
            if (this.gui.getGuiItems().get(i) == null) {
                this.gui.setItem(i, items.get(i));
            }
        }
    }
    
    private List<GuiItem> repeatList(@NotNull final List<GuiItem> guiItems, final int newLength) {
        final List<GuiItem> repeated = new ArrayList<GuiItem>();
        final List<List<GuiItem>> nCopies = Collections.nCopies(this.gui.getRows() * 9, guiItems);
        final List<GuiItem> obj = repeated;
        Objects.requireNonNull((ArrayList)obj);
        nCopies.forEach(obj::addAll);
        return repeated;
    }
    
    private int getSlotFromRowCol(final int row, final int col) {
        return col + (row - 1) * 9 - 1;
    }
}
