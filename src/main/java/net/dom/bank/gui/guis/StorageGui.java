// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import org.bukkit.entity.HumanEntity;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import net.dom.bank.gui.components.InteractionModifier;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class StorageGui extends BaseGui
{
    public StorageGui(final int rows, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }
    
    @Deprecated
    public StorageGui(final int rows, @NotNull final String title) {
        super(rows, title);
    }
    
    @Deprecated
    public StorageGui(@NotNull final String title) {
        super(1, title);
    }
    
    @NotNull
    public Map<Integer, ItemStack> addItem(@NotNull final ItemStack... items) {
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends ItemStack>)this.getInventory().addItem(items));
    }
    
    public Map<Integer, ItemStack> addItem(@NotNull final List<ItemStack> items) {
        return this.addItem((ItemStack[])items.toArray(new ItemStack[0]));
    }
    
    @Override
    public void open(@NotNull final HumanEntity player) {
        if (player.isSleeping()) {
            return;
        }
        this.populateGui();
        player.openInventory(this.getInventory());
    }
}
