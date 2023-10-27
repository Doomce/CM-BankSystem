// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.dom.bank.gui.components.exception.GuiException;
import org.jetbrains.annotations.Contract;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;
import net.dom.bank.gui.components.util.Legacy;
import net.kyori.adventure.text.Component;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import net.dom.bank.gui.components.InteractionModifier;
import java.util.Set;
import org.bukkit.event.inventory.InventoryClickEvent;
import net.dom.bank.gui.components.GuiAction;
import java.util.Map;
import net.dom.bank.gui.components.GuiType;
import net.dom.bank.gui.components.util.GuiFiller;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.InventoryHolder;

public abstract class BaseGui implements InventoryHolder
{
    private static final Plugin plugin;
    private Inventory inventory;
    private String title;
    private final GuiFiller filler;
    private int rows;
    private GuiType guiType;
    private final Map<Integer, GuiItem> guiItems;
    private final Map<Integer, GuiAction<InventoryClickEvent>> slotActions;
    private final Set<InteractionModifier> interactionModifiers;
    private GuiAction<InventoryClickEvent> defaultClickAction;
    private GuiAction<InventoryClickEvent> defaultTopClickAction;
    private GuiAction<InventoryClickEvent> playerInventoryAction;
    private GuiAction<InventoryDragEvent> dragAction;
    private GuiAction<InventoryCloseEvent> closeGuiAction;
    private GuiAction<InventoryOpenEvent> openGuiAction;
    private GuiAction<InventoryClickEvent> outsideClickAction;
    private boolean updating;
    private boolean runCloseAction;
    private boolean runOpenAction;
    
    public BaseGui(final int rows, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        this.filler = new GuiFiller(this);
        this.rows = 1;
        this.guiType = GuiType.CHEST;
        this.runCloseAction = true;
        this.runOpenAction = true;
        int finalRows = rows;
        if (rows < 1 || rows > 6) {
            finalRows = 1;
        }
        this.rows = finalRows;
        this.interactionModifiers = this.safeCopyOf(interactionModifiers);
        this.title = title;
        final int inventorySize = this.rows * 9;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, inventorySize, title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>(inventorySize);
        this.guiItems = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }
    
    public BaseGui(@NotNull final GuiType guiType, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        this.filler = new GuiFiller(this);
        this.rows = 1;
        this.guiType = GuiType.CHEST;
        this.runCloseAction = true;
        this.runOpenAction = true;
        this.guiType = guiType;
        this.interactionModifiers = this.safeCopyOf(interactionModifiers);
        this.title = title;
        final int inventorySize = guiType.getLimit();
        this.inventory = Bukkit.createInventory((InventoryHolder)this, guiType.getInventoryType(), title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>(inventorySize);
        this.guiItems = new LinkedHashMap<Integer, GuiItem>(inventorySize);
    }
    
    @NotNull
    private EnumSet<InteractionModifier> safeCopyOf(@NotNull final Set<InteractionModifier> set) {
        if (set.isEmpty()) {
            return EnumSet.noneOf(InteractionModifier.class);
        }
        return EnumSet.copyOf(set);
    }
    
    @Deprecated
    public BaseGui(final int rows, @NotNull final String title) {
        this.filler = new GuiFiller(this);
        this.rows = 1;
        this.guiType = GuiType.CHEST;
        this.runCloseAction = true;
        this.runOpenAction = true;
        int finalRows = rows;
        if (rows < 1 || rows > 6) {
            finalRows = 1;
        }
        this.rows = finalRows;
        this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
        this.title = title;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, this.rows * 9, title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>();
        this.guiItems = new LinkedHashMap<Integer, GuiItem>();
    }
    
    @Deprecated
    public BaseGui(@NotNull final GuiType guiType, @NotNull final String title) {
        this.filler = new GuiFiller(this);
        this.rows = 1;
        this.guiType = GuiType.CHEST;
        this.runCloseAction = true;
        this.runOpenAction = true;
        this.guiType = guiType;
        this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
        this.title = title;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, this.guiType.getInventoryType(), title);
        this.slotActions = new LinkedHashMap<Integer, GuiAction<InventoryClickEvent>>();
        this.guiItems = new LinkedHashMap<Integer, GuiItem>();
    }
    
    @Deprecated
    @NotNull
    public String getTitle() {
        return this.title;
    }
    
    @NotNull
    public Component title() {
        return (Component)Legacy.SERIALIZER.deserialize(this.title);
    }
    
    public void setItem(final int slot, @NotNull final GuiItem guiItem) {
        this.validateSlot(slot);
        this.guiItems.put(slot, guiItem);
    }
    
    public void removeItem(@NotNull final GuiItem item) {
        final Optional<Map.Entry<Integer, GuiItem>> entry = this.guiItems.entrySet().stream().filter(it -> it.getValue().equals(item)).findFirst();
        entry.ifPresent(it -> {
            this.guiItems.remove(it.getKey());
            this.inventory.remove(((GuiItem)it.getValue()).getItemStack());
        });
    }
    
    public void removeItem(@NotNull final ItemStack item) {
        final Optional<Map.Entry<Integer, GuiItem>> entry = this.guiItems.entrySet().stream().filter(it -> it.getValue().getItemStack().equals((Object)item)).findFirst();
        entry.ifPresent(it -> {
            this.guiItems.remove(it.getKey());
            this.inventory.remove(item);
        });
    }
    
    public void removeItem(final int slot) {
        this.validateSlot(slot);
        this.guiItems.remove(slot);
        this.inventory.setItem(slot, (ItemStack)null);
    }
    
    public void removeItem(final int row, final int col) {
        this.removeItem(this.getSlotFromRowCol(row, col));
    }
    
    public void setItem(@NotNull final List<Integer> slots, @NotNull final GuiItem guiItem) {
        for (final int slot : slots) {
            this.setItem(slot, guiItem);
        }
    }
    
    public void setItem(final int row, final int col, @NotNull final GuiItem guiItem) {
        this.setItem(this.getSlotFromRowCol(row, col), guiItem);
    }
    
    public void addItem(@NotNull final GuiItem... items) {
        this.addItem(false, items);
    }
    
    public void addItem(final boolean expandIfFull, @NotNull final GuiItem... items) {
        final List<GuiItem> notAddedItems = new ArrayList<GuiItem>();
        for (final GuiItem guiItem : items) {
            for (int slot = 0; slot < this.rows * 9; ++slot) {
                if (this.guiItems.get(slot) == null) {
                    this.guiItems.put(slot, guiItem);
                    break;
                }
                if (slot == this.rows * 9 - 1) {
                    notAddedItems.add(guiItem);
                }
            }
        }
        if (!expandIfFull || this.rows >= 6 || notAddedItems.isEmpty() || (this.guiType != null && this.guiType != GuiType.CHEST)) {
            return;
        }
        ++this.rows;
        this.inventory = Bukkit.createInventory((InventoryHolder)this, this.rows * 9, this.title);
        this.update();
        this.addItem(true, (GuiItem[])notAddedItems.toArray(new GuiItem[0]));
    }
    
    public void setDefaultClickAction(@Nullable final GuiAction<InventoryClickEvent> defaultClickAction) {
        this.defaultClickAction = defaultClickAction;
    }
    
    public void setDefaultTopClickAction(@Nullable final GuiAction<InventoryClickEvent> defaultTopClickAction) {
        this.defaultTopClickAction = defaultTopClickAction;
    }
    
    public void setPlayerInventoryAction(@Nullable final GuiAction<InventoryClickEvent> playerInventoryAction) {
        this.playerInventoryAction = playerInventoryAction;
    }
    
    public void setOutsideClickAction(@Nullable final GuiAction<InventoryClickEvent> outsideClickAction) {
        this.outsideClickAction = outsideClickAction;
    }
    
    public void setDragAction(@Nullable final GuiAction<InventoryDragEvent> dragAction) {
        this.dragAction = dragAction;
    }
    
    public void setCloseGuiAction(@Nullable final GuiAction<InventoryCloseEvent> closeGuiAction) {
        this.closeGuiAction = closeGuiAction;
    }
    
    public void setOpenGuiAction(@Nullable final GuiAction<InventoryOpenEvent> openGuiAction) {
        this.openGuiAction = openGuiAction;
    }
    
    public void addSlotAction(final int slot, @Nullable final GuiAction<InventoryClickEvent> slotAction) {
        this.validateSlot(slot);
        this.slotActions.put(slot, slotAction);
    }
    
    public void addSlotAction(final int row, final int col, @Nullable final GuiAction<InventoryClickEvent> slotAction) {
        this.addSlotAction(this.getSlotFromRowCol(row, col), slotAction);
    }
    
    @Nullable
    public GuiItem getGuiItem(final int slot) {
        return this.guiItems.get(slot);
    }
    
    public boolean isUpdating() {
        return this.updating;
    }
    
    public void setUpdating(final boolean updating) {
        this.updating = updating;
    }
    
    public void open(@NotNull final HumanEntity player) {
        if (player.isSleeping()) {
            return;
        }
        this.inventory.clear();
        this.populateGui();
        player.openInventory(this.inventory);
    }
    
    public void close(@NotNull final HumanEntity player) {
        this.close(player, true);
    }
    
    public void close(@NotNull final HumanEntity player, final boolean runCloseAction) {
        Bukkit.getScheduler().runTaskLater(BaseGui.plugin, () -> {
            this.runCloseAction = runCloseAction;
            player.closeInventory();
            this.runCloseAction = true;
        }, 2L);
    }
    
    public void update() {
        this.inventory.clear();
        this.populateGui();
        for (final HumanEntity viewer : new ArrayList<HumanEntity>(this.inventory.getViewers())) {
            ((Player)viewer).updateInventory();
        }
    }
    
    @Contract("_ -> this")
    @NotNull
    public BaseGui updateTitle(@NotNull final String title) {
        this.updating = true;
        final List<HumanEntity> viewers = new ArrayList<HumanEntity>(this.inventory.getViewers());
        this.inventory = Bukkit.createInventory((InventoryHolder)this, this.inventory.getSize(), title);
        for (final HumanEntity player : viewers) {
            this.open(player);
        }
        this.updating = false;
        this.title = title;
        return this;
    }
    
    public void updateItem(final int slot, @NotNull final ItemStack itemStack) {
        final GuiItem guiItem = this.guiItems.get(slot);
        if (guiItem == null) {
            this.updateItem(slot, new GuiItem(itemStack));
            return;
        }
        guiItem.setItemStack(itemStack);
        this.updateItem(slot, guiItem);
    }
    
    public void updateItem(final int row, final int col, @NotNull final ItemStack itemStack) {
        this.updateItem(this.getSlotFromRowCol(row, col), itemStack);
    }
    
    public void updateItem(final int slot, @NotNull final GuiItem item) {
        this.guiItems.put(slot, item);
        this.inventory.setItem(slot, item.getItemStack());
    }
    
    public void updateItem(final int row, final int col, @NotNull final GuiItem item) {
        this.updateItem(this.getSlotFromRowCol(row, col), item);
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui disableItemPlace() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui disableItemTake() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui disableItemSwap() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui disableItemDrop() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui disableOtherActions() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui disableAllInteractions() {
        this.interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui enableItemPlace() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui enableItemTake() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui enableItemSwap() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui enableItemDrop() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui enableOtherActions() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }
    
    @NotNull
    @Contract(" -> this")
    public BaseGui enableAllInteractions() {
        this.interactionModifiers.clear();
        return this;
    }
    
    public boolean canPlaceItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }
    
    public boolean canTakeItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }
    
    public boolean canSwapItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }
    
    public boolean canDropItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }
    
    public boolean allowsOtherActions() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }
    
    @NotNull
    public GuiFiller getFiller() {
        return this.filler;
    }
    
    @NotNull
    public Map<Integer, GuiItem> getGuiItems() {
        return this.guiItems;
    }
    
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    @NotNull
    public GuiType guiType() {
        return this.guiType;
    }
    
    @Nullable
    GuiAction<InventoryClickEvent> getDefaultClickAction() {
        return this.defaultClickAction;
    }
    
    @Nullable
    GuiAction<InventoryClickEvent> getDefaultTopClickAction() {
        return this.defaultTopClickAction;
    }
    
    @Nullable
    GuiAction<InventoryClickEvent> getPlayerInventoryAction() {
        return this.playerInventoryAction;
    }
    
    @Nullable
    GuiAction<InventoryDragEvent> getDragAction() {
        return this.dragAction;
    }
    
    @Nullable
    GuiAction<InventoryCloseEvent> getCloseGuiAction() {
        return this.closeGuiAction;
    }
    
    @Nullable
    GuiAction<InventoryOpenEvent> getOpenGuiAction() {
        return this.openGuiAction;
    }
    
    @Nullable
    GuiAction<InventoryClickEvent> getOutsideClickAction() {
        return this.outsideClickAction;
    }
    
    @Nullable
    GuiAction<InventoryClickEvent> getSlotAction(final int slot) {
        return this.slotActions.get(slot);
    }
    
    void populateGui() {
        for (final Map.Entry<Integer, GuiItem> entry : this.guiItems.entrySet()) {
            this.inventory.setItem((int)entry.getKey(), entry.getValue().getItemStack());
        }
    }
    
    boolean shouldRunCloseAction() {
        return this.runCloseAction;
    }
    
    boolean shouldRunOpenAction() {
        return this.runOpenAction;
    }
    
    int getSlotFromRowCol(final int row, final int col) {
        return col + (row - 1) * 9 - 1;
    }
    
    public void setInventory(@NotNull final Inventory inventory) {
        this.inventory = inventory;
    }
    
    private void validateSlot(final int slot) {
        final int limit = this.guiType.getLimit();
        if (this.guiType == GuiType.CHEST) {
            if (slot < 0 || slot >= this.rows * limit) {
                this.throwInvalidSlot(slot);
            }
            return;
        }
        if (slot < 0 || slot > limit) {
            this.throwInvalidSlot(slot);
        }
    }
    
    private void throwInvalidSlot(final int slot) {
        if (this.guiType == GuiType.CHEST) {
            throw new GuiException("Slot " + slot + " is not valid for the gui type - " + this.guiType.name() + " and rows - " + this.rows + "!");
        }
        throw new GuiException("Slot " + slot + " is not valid for the gui type - " + this.guiType.name() + "!");
    }
    
    static {
        plugin = (Plugin)JavaPlugin.getProvidingPlugin((Class)BaseGui.class);
        Bukkit.getPluginManager().registerEvents((Listener)new GuiListener(), BaseGui.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new InteractionModifierListener(), BaseGui.plugin);
    }
}
