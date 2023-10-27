package net.dom.bank.Transaction;

import net.dom.bank.CustomEvents.TransactionEvent;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import net.dom.bank.CustomEvents.SucessfulTransactionEvent;
import javax.annotation.Nullable;

public class TransactionObject
{
    public int Transaction(String Sender, String Receiver2, double amount, String Desc, @Nullable TransactionState TS) {
        if (amount < 0.0) {
            return 0;
        }
        if (amount >= 2000000.0) {
            return 1;
        }
        if (TS == null) {
            TS = TransactionState.PREPARING;
        }
        if (TS.equals(TransactionState.EXTRA)) {
            SucessfulTransactionEvent STE = new SucessfulTransactionEvent(Sender, Receiver2, amount, Desc);
            Bukkit.getPluginManager().callEvent((Event)STE);
            return 6;
        }
        TransactionEvent TE = new TransactionEvent(Sender, Receiver2, amount, Desc, TS);
        Bukkit.getPluginManager().callEvent((Event)TE);
        if (TE.isCancelled() || TE.getTrState().equals(TransactionState.REJECTED)) {
            return 4;
        }
        if (TE.getAmount() >= 100000.0 && TE.getTrState().equals(TransactionState.PREPARING)) {
            TE.SetTransactionState(TransactionState.WAIT_FOR_VERIFY);
            return 5;
        }
        TE.SetTransactionState(TransactionState.DONE);
        SucessfulTransactionEvent STE2 = new SucessfulTransactionEvent(TE.getSenderIBAN(), TE.getReceiverIBAN(), TE.getAmount(), TE.getDescription());
        Bukkit.getPluginManager().callEvent((Event)STE2);
        return 6;
    }
}
