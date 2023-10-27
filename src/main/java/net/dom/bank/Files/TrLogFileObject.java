package net.dom.bank.Files;

import net.dom.bank.Transaction.TransactionState;

public class TrLogFileObject
{
    String Sender;
    String Receiver;
    double Amount;
    String Description;
    TransactionState TS;
    
    public TrLogFileObject() {
    }
    
    public TrLogFileObject(final String sender, final String receiver, final double amount, final String Desc, final TransactionState ts) {
        this.Sender = sender;
        this.Receiver = receiver;
        this.Amount = amount;
        this.Description = Desc;
        this.TS = ts;
    }
}
