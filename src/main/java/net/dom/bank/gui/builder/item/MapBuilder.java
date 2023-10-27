// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import org.bukkit.map.MapView;
import org.jetbrains.annotations.Contract;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Color;
import net.dom.bank.gui.components.exception.GuiException;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class MapBuilder extends BaseItemBuilder<MapBuilder>
{
    private static final Material MAP;
    
    MapBuilder() {
        super(new ItemStack(MapBuilder.MAP));
    }
    
    MapBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != MapBuilder.MAP) {
            throw new GuiException("MapBuilder requires the material to be a MAP!");
        }
    }
    
    @NotNull
    @Contract("_ -> this")
    @Override
    public MapBuilder color(@Nullable final Color color) {
        final MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setColor(color);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public MapBuilder locationName(@Nullable final String name) {
        final MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setLocationName(name);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public MapBuilder scaling(final boolean scaling) {
        final MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setScaling(scaling);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public MapBuilder view(@NotNull final MapView view) {
        final MapMeta mapMeta = (MapMeta)this.getMeta();
        mapMeta.setMapView(view);
        this.setMeta((ItemMeta)mapMeta);
        return this;
    }
    
    static {
        MAP = Material.MAP;
    }
}
