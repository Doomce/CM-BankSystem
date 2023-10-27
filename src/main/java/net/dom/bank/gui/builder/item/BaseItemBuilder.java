// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import net.dom.bank.gui.components.exception.GuiException;
import java.util.Collection;
import org.bukkit.event.inventory.InventoryClickEvent;
import net.dom.bank.gui.components.GuiAction;
import net.dom.bank.gui.guis.GuiItem;
import net.dom.bank.gui.components.util.ItemNbt;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Color;
import org.bukkit.persistence.PersistentDataContainer;
import java.util.Iterator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.List;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Contract;
import net.dom.bank.gui.components.util.Legacy;
import net.dom.bank.gui.components.util.VersionHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.Field;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import java.util.EnumSet;

public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>>
{
    private static final EnumSet<Material> LEATHER_ARMOR;
    private static final GsonComponentSerializer GSON;
    private static final Field DISPLAY_NAME_FIELD;
    private static final Field LORE_FIELD;
    private ItemStack itemStack;
    private ItemMeta meta;
    
    protected BaseItemBuilder(@NotNull final ItemStack itemStack) {
        Validate.notNull((Object)itemStack, "Item can't be null!");
        this.itemStack = itemStack;
        this.meta = (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
    }
    
    @NotNull
    @Contract("_ -> this")
    public B name(@NotNull final Component name) {
        if (this.meta == null) {
            return (B)this;
        }
        if (VersionHelper.IS_ITEM_LEGACY) {
            this.meta.setDisplayName(Legacy.SERIALIZER.serialize(name));
            return (B)this;
        }
        try {
            BaseItemBuilder.DISPLAY_NAME_FIELD.set(this.meta, BaseItemBuilder.GSON.serialize(name));
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B amount(final int amount) {
        this.itemStack.setAmount(amount);
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B lore(@Nullable final Component... lore) {
        return this.lore(Arrays.asList(lore));
    }
    
    @NotNull
    @Contract("_ -> this")
    public B lore(@NotNull final List<Component> lore) {
        if (this.meta == null) {
            return (B)this;
        }
        if (VersionHelper.IS_ITEM_LEGACY) {
            final ItemMeta meta = this.meta;
            final Stream<Object> filter = lore.stream().filter(Objects::nonNull);
            final LegacyComponentSerializer serializer = Legacy.SERIALIZER;
            Objects.requireNonNull(serializer);
            meta.setLore((List)filter.map((Function<? super Object, ?>)serializer::serialize).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()));
            return (B)this;
        }
        final Stream<Object> filter2 = lore.stream().filter(Objects::nonNull);
        final GsonComponentSerializer gson = BaseItemBuilder.GSON;
        Objects.requireNonNull(gson);
        final List<String> jsonLore = filter2.map((Function<? super Object, ?>)gson::serialize).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        try {
            BaseItemBuilder.LORE_FIELD.set(this.meta, jsonLore);
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B lore(@NotNull final Consumer<List<Component>> lore) {
        if (this.meta == null) {
            return (B)this;
        }
        List<Component> components;
        if (VersionHelper.IS_ITEM_LEGACY) {
            final List<String> stringLore = (List<String>)this.meta.getLore();
            if (stringLore == null) {
                return (B)this;
            }
            final Stream<Object> stream = stringLore.stream();
            final LegacyComponentSerializer serializer = Legacy.SERIALIZER;
            Objects.requireNonNull(serializer);
            components = stream.map((Function<? super Object, ?>)serializer::deserialize).collect((Collector<? super Object, ?, List<Component>>)Collectors.toList());
        }
        else {
            try {
                final List<String> jsonLore = (List<String>)BaseItemBuilder.LORE_FIELD.get(this.meta);
                final Stream<Object> stream2 = jsonLore.stream();
                final GsonComponentSerializer gson = BaseItemBuilder.GSON;
                Objects.requireNonNull(gson);
                components = stream2.map((Function<? super Object, ?>)gson::deserialize).collect((Collector<? super Object, ?, List<Component>>)Collectors.toList());
            }
            catch (IllegalAccessException exception) {
                components = new ArrayList<Component>();
                exception.printStackTrace();
            }
        }
        lore.accept(components);
        return this.lore(components);
    }
    
    @NotNull
    @Contract("_, _, _ -> this")
    public B enchant(@NotNull final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        this.meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return (B)this;
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public B enchant(@NotNull final Enchantment enchantment, final int level) {
        return this.enchant(enchantment, level, true);
    }
    
    @NotNull
    @Contract("_ -> this")
    public B enchant(@NotNull final Enchantment enchantment) {
        return this.enchant(enchantment, 1, true);
    }
    
    @NotNull
    @Contract("_ -> this")
    public B disenchant(@NotNull final Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B flags(@NotNull final ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B unbreakable() {
        return this.unbreakable(true);
    }
    
    @NotNull
    @Contract("_ -> this")
    public B unbreakable(final boolean unbreakable) {
        if (VersionHelper.IS_UNBREAKABLE_LEGACY) {
            return this.setNbt("Unbreakable", unbreakable);
        }
        this.meta.setUnbreakable(unbreakable);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B glow() {
        return this.glow(true);
    }
    
    @NotNull
    @Contract("_ -> this")
    public B glow(final boolean glow) {
        if (glow) {
            this.meta.addEnchant(Enchantment.LURE, 1, false);
            this.meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
            return (B)this;
        }
        for (final Enchantment enchantment : this.meta.getEnchants().keySet()) {
            this.meta.removeEnchant(enchantment);
        }
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B pdc(@NotNull final Consumer<PersistentDataContainer> consumer) {
        consumer.accept(this.meta.getPersistentDataContainer());
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B model(final int modelData) {
        if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
            this.meta.setCustomModelData(Integer.valueOf(modelData));
        }
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B color(@NotNull final Color color) {
        if (BaseItemBuilder.LEATHER_ARMOR.contains(this.itemStack.getType())) {
            final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.getMeta();
            leatherArmorMeta.setColor(color);
            this.setMeta((ItemMeta)leatherArmorMeta);
        }
        return (B)this;
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public B setNbt(@NotNull final String key, @NotNull final String value) {
        this.itemStack.setItemMeta(this.meta);
        this.itemStack = ItemNbt.setString(this.itemStack, key, value);
        this.meta = this.itemStack.getItemMeta();
        return (B)this;
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public B setNbt(@NotNull final String key, final boolean value) {
        this.itemStack.setItemMeta(this.meta);
        this.itemStack = ItemNbt.setBoolean(this.itemStack, key, value);
        this.meta = this.itemStack.getItemMeta();
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B removeNbt(@NotNull final String key) {
        this.itemStack.setItemMeta(this.meta);
        this.itemStack = ItemNbt.removeTag(this.itemStack, key);
        this.meta = this.itemStack.getItemMeta();
        return (B)this;
    }
    
    @NotNull
    public ItemStack build() {
        this.itemStack.setItemMeta(this.meta);
        return this.itemStack;
    }
    
    @NotNull
    @Contract(" -> new")
    public GuiItem asGuiItem() {
        return new GuiItem(this.build());
    }
    
    @NotNull
    @Contract("_ -> new")
    public GuiItem asGuiItem(@NotNull final GuiAction<InventoryClickEvent> action) {
        return new GuiItem(this.build(), action);
    }
    
    @NotNull
    protected ItemStack getItemStack() {
        return this.itemStack;
    }
    
    protected void setItemStack(@NotNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    @NotNull
    protected ItemMeta getMeta() {
        return this.meta;
    }
    
    protected void setMeta(@NotNull final ItemMeta meta) {
        this.meta = meta;
    }
    
    @Deprecated
    public B setName(@NotNull final String name) {
        this.getMeta().setDisplayName(name);
        return (B)this;
    }
    
    @Deprecated
    public B setAmount(final int amount) {
        this.getItemStack().setAmount(amount);
        return (B)this;
    }
    
    @Deprecated
    public B addLore(@NotNull final String... lore) {
        return this.addLore(Arrays.asList(lore));
    }
    
    @Deprecated
    public B addLore(@NotNull final List<String> lore) {
        final List<String> newLore = this.getMeta().hasLore() ? this.getMeta().getLore() : new ArrayList<String>();
        newLore.addAll(lore);
        return this.setLore(newLore);
    }
    
    @Deprecated
    public B setLore(@NotNull final String... lore) {
        return this.setLore(Arrays.asList(lore));
    }
    
    @Deprecated
    public B setLore(@NotNull final List<String> lore) {
        this.getMeta().setLore((List)lore);
        return (B)this;
    }
    
    @Deprecated
    public B addEnchantment(@NotNull final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        this.getMeta().addEnchant(enchantment, level, ignoreLevelRestriction);
        return (B)this;
    }
    
    @Deprecated
    public B addEnchantment(@NotNull final Enchantment enchantment, final int level) {
        return this.addEnchantment(enchantment, level, true);
    }
    
    @Deprecated
    public B addEnchantment(@NotNull final Enchantment enchantment) {
        return this.addEnchantment(enchantment, 1, true);
    }
    
    @Deprecated
    public B removeEnchantment(@NotNull final Enchantment enchantment) {
        this.getItemStack().removeEnchantment(enchantment);
        return (B)this;
    }
    
    @Deprecated
    public B addItemFlags(@NotNull final ItemFlag... flags) {
        this.getMeta().addItemFlags(flags);
        return (B)this;
    }
    
    @Deprecated
    public B setUnbreakable(final boolean unbreakable) {
        return this.unbreakable(unbreakable);
    }
    
    static {
        LEATHER_ARMOR = EnumSet.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
        GSON = GsonComponentSerializer.gson();
        try {
            final Class<?> metaClass = VersionHelper.craftClass("inventory.CraftMetaItem");
            (DISPLAY_NAME_FIELD = metaClass.getDeclaredField("displayName")).setAccessible(true);
            (LORE_FIELD = metaClass.getDeclaredField("lore")).setAccessible(true);
        }
        catch (NoSuchFieldException | ClassNotFoundException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException exception = ex;
            exception.printStackTrace();
            throw new GuiException("Could not retrieve displayName nor lore field for ItemBuilder.");
        }
    }
}
