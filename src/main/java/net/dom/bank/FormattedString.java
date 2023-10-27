package net.dom.bank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import org.bukkit.entity.Player;
import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class FormattedString
{
    private final List<String> message;
    
    public FormattedString(String key) {
        message = new ArrayList<>();
        try {
            Bank.getInstance().getLang().getStringList(key).get(0);
            message.addAll(Bank.getInstance().getLang().getStringList(key));
        }
        catch (IndexOutOfBoundsException ignore) {
            message.add(Bank.getInstance().getLang().getStringFromLang(key));
        }
    }
    
    public FormattedString Replace(String PlKey, String Replacer) {
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, Replacer));
        }
        return this;
    }
    
    public FormattedString Replace(String PlKey, UUID Replacer) {
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, Bukkit.getPlayer(Replacer).getName()));
        }
        return this;
    }
    
    public FormattedString Replace(String PlKey, long Replacer) {
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, Replacer + ""));
        }
        return this;
    }
    
    public FormattedString Replace(String PlKey, double Replacer) {
        DecimalFormat f = new DecimalFormat("0.00");
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, f.format(Replacer)));
        }
        return this;
    }
    
    public FormattedString Replace(String PlKey, Player Replacer) {
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, Replacer.getName()));
        }
        return this;
    }
    
    public FormattedString Replace(String PlKey, LocalDateTime Replacer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, Replacer.format(formatter)));
        }
        return this;
    }
    
    public FormattedString Replace(String PlKey, LocalDate Replacer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < message.size(); ++i) {
            message.set(i, message.get(i).replace(PlKey, Replacer.format(formatter)));
        }
        return this;
    }
    
    public List<String> GetMsg() {
        return message;
    }
}
