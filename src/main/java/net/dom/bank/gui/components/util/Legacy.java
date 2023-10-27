// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.util;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Legacy
{
    public static final LegacyComponentSerializer SERIALIZER;
    
    private Legacy() {
        throw new UnsupportedOperationException("Class should not be instantiated!");
    }
    
    static {
        SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    }
}
