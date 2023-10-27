package net.dom.bank.Objects;

import java.sql.Date;

public class UserPreferences
{
    private String Name;
    private Double Rate;
    private Double DayRate;
    private Date ExpiryDate;
    private Double StartSum;
    
    public String getName() {
        return this.Name;
    }
    
    public void setName(String name) {
        this.Name = name;
    }
    
    public double getRate() {
        return this.Rate;
    }
    
    public void setRate(double rate) {
        this.Rate = rate;
    }
    
    public double getDayRate() {
        return this.DayRate;
    }
    
    public void setDayRate(double dayRate) {
        this.DayRate = dayRate;
    }
    
    public Date getExpiryDate() {
        return this.ExpiryDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.ExpiryDate = expiryDate;
    }
    
    public double getStartSum() {
        return this.StartSum;
    }
    
    public void setStartSum(double startSum) {
        this.StartSum = startSum;
    }
}
