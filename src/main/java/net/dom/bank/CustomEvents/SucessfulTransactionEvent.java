package net.dom.bank.CustomEvents;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class SucessfulTransactionEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLERS;
    private final String SenderIBAN;
    private final String ReceiverIBAN;
    private final double Amount;
    private final String Description;
    private boolean isCancelled;
    
    private static HandlerList getHandlerList() {
        return SucessfulTransactionEvent.HANDLERS;
    }
    
    public SucessfulTransactionEvent(final String Sender, final String Receiver2, final double amount, final String Desc) {
        this.SenderIBAN = Sender;
        this.ReceiverIBAN = Receiver2;
        this.Amount = amount;
        this.Description = Desc;
        this.isCancelled = false;
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    public void setCancelled(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    
    public HandlerList getHandlers() {
        return SucessfulTransactionEvent.HANDLERS;
    }
    
    public String getSenderIBAN() {
        return this.SenderIBAN;
    }
    
    public String getReceiverIBAN() {
        return this.ReceiverIBAN;
    }
    
    public double getAmount() {
        return this.Amount;
    }
    
    public String getDescription() {
        return this.Description;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
