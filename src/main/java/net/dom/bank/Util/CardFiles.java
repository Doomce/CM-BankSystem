package net.dom.bank.Util;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Arrays;
import net.dom.bank.Objects.BankCard;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.bukkit.configuration.file.YamlConfiguration;
import net.dom.bank.Bank;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class CardFiles
{
    private final FileConfiguration BList;
    private final File BListFile;
    
    public CardFiles(final Bank plugin) {
        this.BListFile = new File(plugin.getDataFolder(), "Cards_ATM_Blist.yml");
        this.BList = YamlConfiguration.loadConfiguration(this.BListFile);
    }
    
    public boolean isCardInBList(final int CardID) {
        return this.BList.contains("Card." + CardID);
    }
    
    public LocalDateTime BannedCardDate(final int CardID) {
        if (!this.BList.contains("Card." + CardID)) {
            return null;
        }
        long BanMillis = Long.parseLong(this.BList.getStringList("Card." + CardID).get(0));
        long CardIban = Integer.parseInt(this.BList.getStringList("Card." + CardID).get(1));
        return new Timestamp(BanMillis).toLocalDateTime();
    }
    
    public void BanCard(final BankCard Card) {
        int CardID = Card.CardID;
        long Iban = Card.IBAN;
        this.BList.set("Card." + CardID, Arrays.asList(new Timestamp(System.currentTimeMillis()).getTime() + "", Iban + ""));
        try {
            this.BList.save(this.BListFile);
        }
        catch (IOException ignore) {
            Bank.log.log(Level.SEVERE, "N\u0117ra Card-BLIST failo.");
        }
    }
    
    public boolean isATMExists(int ID) {
        return this.BList.contains("ATM." + ID);
    }
    
    public void RemoveATM(int ID) {
        this.BList.set("ATM." + ID, null);
        try {
            this.BList.save(this.BListFile);
        }
        catch (IOException ignore) {
            Bank.log.log(Level.SEVERE, "N\u0117ra Card-BLIST failo.");
        }
    }
    
    public Location getATMLoc(int id) {
        if (!this.BList.contains("ATM." + id)) {
            return null;
        }
        return (Location)this.BList.getObject("ATM." + id, Location.class);
    }
    
    public List<Integer> AllATMs() {
        List<Integer> atms = new ArrayList<>();
        if (this.BList.getConfigurationSection("ATM") == null) {
            return null;
        }
        Map<String, Object> allAtms = this.BList.getConfigurationSection("ATM").getValues(false);
        for (String obj : allAtms.keySet()) {
            atms.add(Integer.parseInt(obj));
        }
        return atms;
    }
    
    public int GenerateATMID() {
        int ATMId = 1;
        if (this.AllATMs() != null && this.AllATMs().size() >= 1) {
            ATMId = this.AllATMs().get(this.AllATMs().size() - 1) + 1;
        }
        return ATMId;
    }
    
    public void AddATMToFile(final Location loc, final int ID) {
        this.BList.set("ATM." + ID, loc);
        try {
            this.BList.save(this.BListFile);
        }
        catch (IOException ignore) {
            Bank.log.log(Level.SEVERE, "N\u0117ra Card-BLIST failo.");
        }
    }
}
