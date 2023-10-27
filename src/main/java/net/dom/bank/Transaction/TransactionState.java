package net.dom.bank.Transaction;

public enum TransactionState
{
    PREPARING, 
    WAIT_FOR_VERIFY, 
    EXTRA, 
    REJECTED, 
    DONE;
}
