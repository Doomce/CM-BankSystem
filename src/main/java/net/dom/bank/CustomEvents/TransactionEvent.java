package net.dom.bank.CustomEvents;

import net.dom.bank.Transaction.TransactionState;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class TransactionEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLERS;
    private final String SenderIBAN;
    private final String ReceiverIBAN;
    private double Amount;
    private String Description;
    private TransactionState TS;
    private boolean isCancelled;
    
    private static HandlerList getHandlerList() {
        return TransactionEvent.HANDLERS;
    }
    
    public TransactionEvent(final String Sender, final String Receiver2, final double amount, final String Desc, final TransactionState ts) {
        this.SenderIBAN = Sender;
        this.ReceiverIBAN = Receiver2;
        this.Amount = amount;
        this.Description = Desc;
        this.TS = ts;
        this.isCancelled = false;
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    public void setCancelled(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    
    public HandlerList getHandlers() {
        return TransactionEvent.HANDLERS;
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
    
    public TransactionState getTrState() {
        return this.TS;
    }
    
    public void SetTransactionState(final TransactionState TrState) {
        if (this.TS.equals(TransactionState.DONE) || this.TS.equals(TransactionState.REJECTED)) {
            return;
        }
        this.TS = TrState;
    }
    
    public void setMoney(final double amount, final String description) {
        if (this.TS.equals(TransactionState.DONE) || this.TS.equals(TransactionState.REJECTED)) {
            return;
        }
        this.Amount = amount;
        if (!description.isEmpty()) {
            this.Description = description;
        }
    }
    
    public void AddMoney(final double amount, final String description) {
        if (this.TS.equals(TransactionState.DONE) || this.TS.equals(TransactionState.REJECTED)) {
            return;
        }
        this.Amount += amount;
        if (!description.isEmpty()) {
            this.Description = this.Description + "; " + description;
        }
    }
    
    public void SubtractMoney(final double amount, final String description) {
        if (this.TS.equals(TransactionState.DONE) || this.TS.equals(TransactionState.REJECTED)) {
            return;
        }
        if (this.Amount >= amount) {
            this.Amount -= amount;
            if (!description.isEmpty()) {
                this.Description = this.Description + "; " + description;
            }
            return;
        }
        this.TS = TransactionState.REJECTED;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
