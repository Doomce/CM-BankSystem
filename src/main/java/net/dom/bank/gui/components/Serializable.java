// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface Serializable
{
    List<String> encodeGui();
    
    void decodeGui(@NotNull final List<String> p0);
}
