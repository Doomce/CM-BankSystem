// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import java.util.Collections;
import java.util.EnumSet;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;
import com.google.common.base.Preconditions;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryAction;
import java.util.Set;
import org.bukkit.event.Listener;

public final class InteractionModifierListener implements Listener
{
    private static final Set<InventoryAction> ITEM_TAKE_ACTIONS;
    private static final Set<InventoryAction> ITEM_PLACE_ACTIONS;
    private static final Set<InventoryAction> ITEM_SWAP_ACTIONS;
    private static final Set<InventoryAction> ITEM_DROP_ACTIONS;
    
    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        final BaseGui gui = (BaseGui)event.getInventory().getHolder();
        if ((!gui.canPlaceItems() && this.isPlaceItemEvent(event)) || (!gui.canTakeItems() && this.isTakeItemEvent(event)) || (!gui.canSwapItems() && this.isSwapItemEvent(event)) || (!gui.canDropItems() && this.isDropItemEvent(event)) || (!gui.allowsOtherActions() && this.isOtherEvent(event))) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
    
    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) {
            return;
        }
        final BaseGui gui = (BaseGui)event.getInventory().getHolder();
        if (gui.canPlaceItems() || !this.isDraggingOnGui(event)) {
            return;
        }
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }
    
    private boolean isTakeItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull((Object)event, (Object)"event cannot be null");
        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        return (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) && inventory.getType() != InventoryType.PLAYER && (action == InventoryAction.MOVE_TO_OTHER_INVENTORY || this.isTakeAction(action));
    }
    
    private boolean isPlaceItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull((Object)event, (Object)"event cannot be null");
        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        return (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER && inventory.getType() != clickedInventory.getType()) || (this.isPlaceAction(action) && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) && inventory.getType() != InventoryType.PLAYER);
    }
    
    private boolean isSwapItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull((Object)event, (Object)"event cannot be null");
        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        return this.isSwapAction(action) && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) && inventory.getType() != InventoryType.PLAYER;
    }
    
    private boolean isDropItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull((Object)event, (Object)"event cannot be null");
        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        return this.isDropAction(action) && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }
    
    private boolean isOtherEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull((Object)event, (Object)"event cannot be null");
        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        return this.isOtherAction(action) && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }
    
    private boolean isDraggingOnGui(final InventoryDragEvent event) {
        Preconditions.checkNotNull((Object)event, (Object)"event cannot be null");
        final int topSlots = event.getView().getTopInventory().getSize();
        return event.getRawSlots().stream().anyMatch(slot -> slot < topSlots);
    }
    
    private boolean isTakeAction(final InventoryAction action) {
        Preconditions.checkNotNull((Object)action, (Object)"action cannot be null");
        return InteractionModifierListener.ITEM_TAKE_ACTIONS.contains(action);
    }
    
    private boolean isPlaceAction(final InventoryAction action) {
        Preconditions.checkNotNull((Object)action, (Object)"action cannot be null");
        return InteractionModifierListener.ITEM_PLACE_ACTIONS.contains(action);
    }
    
    private boolean isSwapAction(final InventoryAction action) {
        Preconditions.checkNotNull((Object)action, (Object)"action cannot be null");
        return InteractionModifierListener.ITEM_SWAP_ACTIONS.contains(action);
    }
    
    private boolean isDropAction(final InventoryAction action) {
        Preconditions.checkNotNull((Object)action, (Object)"action cannot be null");
        return InteractionModifierListener.ITEM_DROP_ACTIONS.contains(action);
    }
    
    private boolean isOtherAction(final InventoryAction action) {
        Preconditions.checkNotNull((Object)action, (Object)"action cannot be null");
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }
    
    static {
        ITEM_TAKE_ACTIONS = Collections.unmodifiableSet((Set<? extends InventoryAction>)EnumSet.of(InventoryAction.PICKUP_ONE, new InventoryAction[] { InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY }));
        ITEM_PLACE_ACTIONS = Collections.unmodifiableSet((Set<? extends InventoryAction>)EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL));
        ITEM_SWAP_ACTIONS = Collections.unmodifiableSet((Set<? extends InventoryAction>)EnumSet.of(InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_MOVE_AND_READD));
        ITEM_DROP_ACTIONS = Collections.unmodifiableSet((Set<? extends InventoryAction>)EnumSet.of(InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR));
    }
}
