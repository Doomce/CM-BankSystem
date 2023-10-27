package net.dom.bank.Objects;

import net.dom.bank.Util.Utils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Timestamp;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.annotations.Type;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.TableGenerator;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import javax.persistence.UniqueConstraint;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "bank_accounts", uniqueConstraints = { @UniqueConstraint(columnNames = { "IBAN" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BankUser implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "iban_sequence")
    @TableGenerator(name = "iban_sequence", table = "sequences", pkColumnName = "gen_name", valueColumnName = "IBAN", initialValue = 100000, allocationSize = 1)
    @Column(name = "IBAN")
    public Long IBAN;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "UUID", nullable = false, columnDefinition = "CHAR(38)")
    public UUID UUID;

    @Column(name = "Money", precision = 10, scale = 2)
    public double Money;

    @Column(name = "cardID")
    private Integer cardID;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "level")
    private AccountLevel level;

    @Column(name = "settings")
    private String settings;

    @UpdateTimestamp
    @Column(name = "last_seen")
    public Timestamp LastChangesTime;

    @CreationTimestamp
    @Column(name = "created_date")
    public Timestamp CreatedDate;
    
    public long getIBAN() {
        return this.IBAN;
    }
    
    public void setIBAN(long id) {
        this.IBAN = id;
    }
    
    public double getMoney() {
        return this.Money;
    }
    
    public Timestamp getLastChangesTime() {
        return this.LastChangesTime;
    }
    
    public UUID getUUID() {
        return this.UUID;
    }
    
    public void setUUID(UUID UUID2) {
        this.UUID = UUID2;
    }
    
    public void setMoney(double money) {
        this.Money = money;
        this.Money = Utils.RoundNumber(this.Money);
    }
    
    public void addMoney(double money) {
        this.Money += money;
        this.Money = Utils.RoundNumber(this.Money);
    }
    
    public void subtractMoney(double money) {
        this.Money -= money;
        this.Money = Utils.RoundNumber(this.Money);
    }
    
    public void subtractMoneyStrict(double money) {
        this.Money -= money;
    }
    
    public Integer getCardID() {
        return this.cardID;
    }
    
    public void setCardID(Integer cardID) {
        this.cardID = cardID;
    }
    
    public AccountLevel getLevel() {
        return this.level;
    }
    
    public void setLevel(AccountLevel level) {
        this.level = level;
    }
    
    public UserPreferences getSettings() {
        return Utils.ParseGsonToObject(this.settings);
    }
    
    public void setSettings(UserPreferences settings) {
        this.settings = Utils.ParseObjectToGson(settings);
    }
}
