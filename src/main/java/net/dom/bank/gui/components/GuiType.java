// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components;

import org.jetbrains.annotations.NotNull;
import org.bukkit.event.inventory.InventoryType;

public enum GuiType
{
    CHEST(InventoryType.CHEST, 9), 
    WORKBENCH(InventoryType.WORKBENCH, 9), 
    HOPPER(InventoryType.HOPPER, 5), 
    DISPENSER(InventoryType.DISPENSER, 8), 
    BREWING(InventoryType.BREWING, 4);
    
    @NotNull
    private final InventoryType inventoryType;
    private final int limit;
    
    private GuiType(final InventoryType inventoryType, final int limit) {
        this.inventoryType = inventoryType;
        this.limit = limit;
    }
    
    @NotNull
    public InventoryType getInventoryType() {
        return this.inventoryType;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    private static /* synthetic */ GuiType[] $values() {
        return new GuiType[] { GuiType.CHEST, GuiType.WORKBENCH, GuiType.HOPPER, GuiType.DISPENSER, GuiType.BREWING };
    }
    
    static {
        $VALUES = $values();
    }
}
