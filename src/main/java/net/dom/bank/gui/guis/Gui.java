// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.guis;

import net.dom.bank.gui.builder.gui.ScrollingBuilder;
import net.dom.bank.gui.components.ScrollType;
import net.dom.bank.gui.builder.gui.PaginatedBuilder;
import net.dom.bank.gui.builder.gui.StorageBuilder;
import org.jetbrains.annotations.Contract;
import net.dom.bank.gui.builder.gui.SimpleBuilder;
import net.dom.bank.gui.components.GuiType;
import net.dom.bank.gui.components.InteractionModifier;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class Gui extends BaseGui
{
    public Gui(final int rows, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }
    
    public Gui(@NotNull final GuiType guiType, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(guiType, title, interactionModifiers);
    }
    
    @Deprecated
    public Gui(final int rows, @NotNull final String title) {
        super(rows, title);
    }
    
    @Deprecated
    public Gui(@NotNull final String title) {
        super(1, title);
    }
    
    @Deprecated
    public Gui(@NotNull final GuiType guiType, @NotNull final String title) {
        super(guiType, title);
    }
    
    @NotNull
    @Contract("_ -> new")
    public static SimpleBuilder gui(@NotNull final GuiType type) {
        return new SimpleBuilder(type);
    }
    
    @NotNull
    @Contract(" -> new")
    public static SimpleBuilder gui() {
        return gui(GuiType.CHEST);
    }
    
    @NotNull
    @Contract(" -> new")
    public static StorageBuilder storage() {
        return new StorageBuilder();
    }
    
    @NotNull
    @Contract(" -> new")
    public static PaginatedBuilder paginated() {
        return new PaginatedBuilder();
    }
    
    @NotNull
    @Contract("_ -> new")
    public static ScrollingBuilder scrolling(@NotNull final ScrollType scrollType) {
        return new ScrollingBuilder(scrollType);
    }
    
    @NotNull
    @Contract(" -> new")
    public static ScrollingBuilder scrolling() {
        return scrolling(ScrollType.VERTICAL);
    }
}
