package net.dom.bank.CustomEvents;

import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class InterestEvent extends Event
{
    private static final HandlerList HANDLERS;
    private final UUID PlayerUid;
    private final double Amount;
    
    private static HandlerList getHandlerList() {
        return InterestEvent.HANDLERS;
    }
    
    public InterestEvent(UUID playerUid, double amount) {
        this.PlayerUid = playerUid;
        this.Amount = amount;
    }
    
    public HandlerList getHandlers() {
        return InterestEvent.HANDLERS;
    }
    
    public UUID getPlayer() {
        return this.PlayerUid;
    }
    
    public double getAmount() {
        return this.Amount;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
