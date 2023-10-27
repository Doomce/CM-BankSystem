// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import net.dom.bank.gui.components.util.ItemNbt;
import org.jetbrains.annotations.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.EventHandler;
import net.dom.bank.gui.components.GuiAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

public final class GuiListener implements Listener
{
    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        final BaseGui gui = (BaseGui)event.getInventory().getHolder();
        final GuiAction<InventoryClickEvent> outsideClickAction = gui.getOutsideClickAction();
        if (outsideClickAction != null && event.getClickedInventory() == null) {
            outsideClickAction.execute(event);
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }
        final GuiAction<InventoryClickEvent> defaultTopClick = gui.getDefaultTopClickAction();
        if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            defaultTopClick.execute(event);
        }
        final GuiAction<InventoryClickEvent> playerInventoryClick = gui.getPlayerInventoryAction();
        if (playerInventoryClick != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            playerInventoryClick.execute(event);
        }
        final GuiAction<InventoryClickEvent> defaultClick = gui.getDefaultClickAction();
        if (defaultClick != null) {
            defaultClick.execute(event);
        }
        final GuiAction<InventoryClickEvent> slotAction = gui.getSlotAction(event.getSlot());
        if (slotAction != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            slotAction.execute(event);
        }
        GuiItem guiItem;
        if (gui instanceof PaginatedGui) {
            final PaginatedGui paginatedGui = (PaginatedGui)gui;
            guiItem = paginatedGui.getGuiItem(event.getSlot());
            if (guiItem == null) {
                guiItem = paginatedGui.getPageItem(event.getSlot());
            }
        }
        else {
            guiItem = gui.getGuiItem(event.getSlot());
        }
        if (!this.isGuiItem(event.getCurrentItem(), guiItem)) {
            return;
        }
        final GuiAction<InventoryClickEvent> itemAction = guiItem.getAction();
        if (itemAction != null) {
            itemAction.execute(event);
        }
    }
    
    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        final BaseGui gui = (BaseGui)event.getInventory().getHolder();
        final GuiAction<InventoryDragEvent> dragAction = gui.getDragAction();
        if (dragAction != null) {
            dragAction.execute(event);
        }
    }
    
    @EventHandler
    public void onGuiClose(final InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        final BaseGui gui = (BaseGui)event.getInventory().getHolder();
        if (gui instanceof PersistentPaginatedGui) {
            ((PersistentPaginatedGui)gui).savePage();
        }
        final GuiAction<InventoryCloseEvent> closeAction = gui.getCloseGuiAction();
        if (closeAction != null && !gui.isUpdating() && gui.shouldRunCloseAction()) {
            closeAction.execute(event);
        }
    }
    
    @EventHandler
    public void onGuiOpen(final InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        final BaseGui gui = (BaseGui)event.getInventory().getHolder();
        final GuiAction<InventoryOpenEvent> openAction = gui.getOpenGuiAction();
        if (openAction != null && !gui.isUpdating()) {
            openAction.execute(event);
        }
    }
    
    private boolean isGuiItem(@Nullable final ItemStack currentItem, @Nullable final GuiItem guiItem) {
        if (currentItem == null || guiItem == null) {
            return false;
        }
        final String nbt = ItemNbt.getString(currentItem, "mf-gui");
        return nbt != null && nbt.equals(guiItem.getUuid().toString());
    }
}
