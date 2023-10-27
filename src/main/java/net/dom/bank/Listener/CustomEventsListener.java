package net.dom.bank.Listener;

import org.bukkit.event.EventHandler;
import net.dom.bank.CustomEvents.TransactionEvent;
import org.bukkit.event.Listener;

public class CustomEventsListener implements Listener
{
    @EventHandler
    public void onTransaction(TransactionEvent event) {
        event.getSenderIBAN();
    }
}
