// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import org.bukkit.Material;
import net.dom.bank.gui.components.util.ItemNbt;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;
import net.dom.bank.gui.components.GuiAction;

public class GuiItem
{
    private GuiAction<InventoryClickEvent> action;
    private ItemStack itemStack;
    private final UUID uuid;
    
    public GuiItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<InventoryClickEvent> action) {
        this.uuid = UUID.randomUUID();
        Validate.notNull((Object)itemStack, "The ItemStack for the GUI Item cannot be null!");
        this.action = action;
        this.itemStack = ItemNbt.setString(itemStack, "mf-gui", this.uuid.toString());
    }
    
    public GuiItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null);
    }
    
    public GuiItem(@NotNull final Material material) {
        this(new ItemStack(material), null);
    }
    
    public GuiItem(@NotNull final Material material, @Nullable final GuiAction<InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }
    
    public void setItemStack(@NotNull final ItemStack itemStack) {
        Validate.notNull((Object)itemStack, "The ItemStack for the GUI Item cannot be null!");
        this.itemStack = ItemNbt.setString(itemStack, "mf-gui", this.uuid.toString());
    }
    
    public void setAction(@Nullable final GuiAction<InventoryClickEvent> action) {
        this.action = action;
    }
    
    @NotNull
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    @NotNull
    UUID getUuid() {
        return this.uuid;
    }
    
    @Nullable
    GuiAction<InventoryClickEvent> getAction() {
        return this.action;
    }
}
