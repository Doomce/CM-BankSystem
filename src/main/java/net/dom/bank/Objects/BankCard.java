package net.dom.bank.Objects;

import org.bukkit.entity.Player;

public class BankCard
{
    public int CardID;
    public long IBAN;
    public double ContactlessLimit;
    public Player Owner;
    private int PinTries;
    
    public BankCard(int id, long iban, Player owner, double MaxContactless) {
        this.PinTries = 0;
        this.CardID = id;
        this.IBAN = iban;
        this.Owner = owner;
        this.ContactlessLimit = MaxContactless;
    }
    
    public BankCard(int id, long iban, Player owner) {
        this.PinTries = 0;
        this.CardID = id;
        this.IBAN = iban;
        this.Owner = owner;
        this.ContactlessLimit = 0.0;
    }
    
    public void addPinTry() {
        ++this.PinTries;
    }
    
    public int getPinTries() {
        return this.PinTries;
    }
    
    public void RemovePinTries() {
        this.PinTries = 0;
    }
}
