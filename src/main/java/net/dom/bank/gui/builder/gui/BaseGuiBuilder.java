// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.gui;

import java.util.Set;
import org.jetbrains.annotations.Nullable;
import net.dom.bank.gui.components.exception.GuiException;
import java.util.Collection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;
import net.dom.bank.gui.components.InteractionModifier;
import java.util.EnumSet;
import net.kyori.adventure.text.Component;
import net.dom.bank.gui.guis.BaseGui;

public abstract class BaseGuiBuilder<G extends BaseGui, B extends BaseGuiBuilder<G, B>>
{
    private Component title;
    private int rows;
    private final EnumSet<InteractionModifier> interactionModifiers;
    private Consumer<G> consumer;
    
    public BaseGuiBuilder() {
        this.title = null;
        this.rows = 1;
        this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
    }
    
    @NotNull
    @Contract("_ -> this")
    public B rows(final int rows) {
        this.rows = rows;
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B title(@NotNull final Component title) {
        this.title = title;
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B disableItemPlace() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B disableItemTake() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B disableItemSwap() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B disableItemDrop() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B disableOtherActions() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B disableAllInteractions() {
        this.interactionModifiers.addAll((Collection<?>)InteractionModifier.VALUES);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B enableItemPlace() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B enableItemTake() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B enableItemSwap() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B enableItemDrop() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B enableOtherActions() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> this")
    public B enableAllInteractions() {
        this.interactionModifiers.clear();
        return (B)this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public B apply(@NotNull final Consumer<G> consumer) {
        this.consumer = consumer;
        return (B)this;
    }
    
    @NotNull
    @Contract(" -> new")
    public abstract G create();
    
    @NotNull
    protected Component getTitle() {
        if (this.title == null) {
            throw new GuiException("GUI title is missing!");
        }
        return this.title;
    }
    
    protected int getRows() {
        return this.rows;
    }
    
    @Nullable
    protected Consumer<G> getConsumer() {
        return this.consumer;
    }
    
    @NotNull
    protected Set<InteractionModifier> getModifiers() {
        return this.interactionModifiers;
    }
}
