// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<T extends Event>
{
    void execute(final T p0);
}
