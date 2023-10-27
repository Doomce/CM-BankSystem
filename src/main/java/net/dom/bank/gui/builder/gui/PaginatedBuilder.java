// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.gui;

import net.dom.bank.gui.guis.BaseGui;
import java.util.function.Consumer;
import net.dom.bank.gui.components.util.Legacy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.dom.bank.gui.guis.PaginatedGui;

public class PaginatedBuilder extends BaseGuiBuilder<PaginatedGui, PaginatedBuilder>
{
    private int pageSize;
    
    public PaginatedBuilder() {
        this.pageSize = 0;
    }
    
    @NotNull
    @Contract("_ -> this")
    public PaginatedBuilder pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
    
    @NotNull
    @Contract(" -> new")
    @Override
    public PaginatedGui create() {
        final PaginatedGui gui = new PaginatedGui(this.getRows(), this.pageSize, Legacy.SERIALIZER.serialize(this.getTitle()), this.getModifiers());
        final Consumer<PaginatedGui> consumer = ((BaseGuiBuilder<PaginatedGui, B>)this).getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}
