package net.dom.bank;

import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;
import net.dom.bank.Util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class Language
{
    private final Bank bank;
    private FileConfiguration Lang;
    private File LangFile;
    
    public Language(final Bank money) {
        this.Lang = null;
        this.LangFile = null;
        this.bank = money;
        this.loadLangFile();
    }
    
    public void loadLangFile() {
        if (LangFile == null) {
            LangFile = new File(bank.getDataFolder() + File.separator, "lt_LT.yml");
        }
        if (!new File(bank.getDataFolder().getPath() + File.separator + "lt_LT.yml").exists()) {
            bank.saveResource("lt_LT.yml", false);
        }
        Lang = YamlConfiguration.loadConfiguration(this.LangFile);
    }
    
    public void sendMessage(FormattedString msg, Player p) {
        if (p == null) {
            return;
        }
        msg.GetMsg().forEach(string -> p.sendMessage(ColorUtil.colorString(string)));
    }
    
    public void sendRawMessage(FormattedString msg, Player p) {
        if (p == null) {
            return;
        }
        msg.GetMsg().forEach(string -> p.sendRawMessage(ColorUtil.colorString(string)));
    }
    
    public String Text(FormattedString msg) {
        return ColorUtil.colorString(msg.GetMsg().get(0));
    }
    
    public List<String> TextList(FormattedString msg) {
        List<String> text = new ArrayList<>();
        msg.GetMsg().forEach(string -> text.add(ColorUtil.colorString(string)));
        return text;
    }
    
    public String getColored(String key) {
        return ColorUtil.colorString(getStringFromLang(key));
    }
    
    public String getStringFromLang(String key) {
        if (!Lang.contains(key)) {
            bank.getLogger().severe("Could not locate " + key + " in the language folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return Lang.getString(key);
    }
    
    public List<String> getStringList(String key) {
        if (!Lang.contains(key)) {
            bank.getLogger().severe("Could not locate " + key + " in the language folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return Lang.getStringList(key);
    }
    
    public Integer getInteger(String key) {
        if (!Lang.contains(key)) {
            bank.getLogger().severe("Could not locate " + key + " in the language folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return Lang.getInt(key);
    }
    
    public Boolean getBoolean(String key) {
        if (!Lang.contains(key)) {
            bank.getLogger().severe("Could not locate " + key + " in the language folder! (Try generating a new one by deleting the current)");
            return null;
        }
        return Lang.getBoolean(key);
    }
    
    public String parseFormattingCodes(String message) {
        message = message.replaceAll("&0", ChatColor.BLACK + "");
        message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
        message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
        message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
        message = message.replaceAll("&4", ChatColor.DARK_RED + "");
        message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        message = message.replaceAll("&6", ChatColor.GOLD + "");
        message = message.replaceAll("&7", ChatColor.GRAY + "");
        message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
        message = message.replaceAll("&9", ChatColor.BLUE + "");
        message = message.replaceAll("&a", ChatColor.GREEN + "");
        message = message.replaceAll("&b", ChatColor.AQUA + "");
        message = message.replaceAll("&c", ChatColor.RED + "");
        message = message.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        message = message.replaceAll("&e", ChatColor.YELLOW + "");
        message = message.replaceAll("&f", ChatColor.WHITE + "");
        message = message.replaceAll("&l", ChatColor.BOLD + "");
        message = message.replaceAll("&o", ChatColor.ITALIC + "");
        message = message.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
        message = message.replaceAll("&n", ChatColor.UNDERLINE + "");
        message = message.replaceAll("&k", ChatColor.MAGIC + "");
        message = message.replaceAll("&r", ChatColor.RESET + "");
        return message;
    }
}
