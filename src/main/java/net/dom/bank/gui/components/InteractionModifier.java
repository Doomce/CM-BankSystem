// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum InteractionModifier
{
    PREVENT_ITEM_PLACE, 
    PREVENT_ITEM_TAKE, 
    PREVENT_ITEM_SWAP, 
    PREVENT_ITEM_DROP, 
    PREVENT_OTHER_ACTIONS;
    
    public static final Set<InteractionModifier> VALUES;
    
    private static /* synthetic */ InteractionModifier[] $values() {
        return new InteractionModifier[] { InteractionModifier.PREVENT_ITEM_PLACE, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_DROP, InteractionModifier.PREVENT_OTHER_ACTIONS };
    }
    
    static {
        $VALUES = $values();
        VALUES = Collections.unmodifiableSet((Set<? extends InteractionModifier>)EnumSet.allOf(InteractionModifier.class));
    }
}
