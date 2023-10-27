// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.gui;

import net.dom.bank.gui.guis.BaseGui;
import java.util.function.Consumer;
import net.dom.bank.gui.components.util.Legacy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.dom.bank.gui.components.ScrollType;
import net.dom.bank.gui.guis.ScrollingGui;

public final class ScrollingBuilder extends BaseGuiBuilder<ScrollingGui, ScrollingBuilder>
{
    private ScrollType scrollType;
    private int pageSize;
    
    public ScrollingBuilder(@NotNull final ScrollType scrollType) {
        this.pageSize = -1;
        this.scrollType = scrollType;
    }
    
    @NotNull
    @Contract("_ -> this")
    public ScrollingBuilder scrollType(@NotNull final ScrollType scrollType) {
        this.scrollType = scrollType;
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public ScrollingBuilder pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
    
    @NotNull
    @Contract(" -> new")
    @Override
    public ScrollingGui create() {
        final ScrollingGui gui = new ScrollingGui(this.getRows(), this.pageSize, Legacy.SERIALIZER.serialize(this.getTitle()), this.scrollType, this.getModifiers());
        final Consumer<ScrollingGui> consumer = ((BaseGuiBuilder<ScrollingGui, B>)this).getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}
