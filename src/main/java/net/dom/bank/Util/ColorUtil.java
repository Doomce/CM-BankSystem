package net.dom.bank.Util;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;

public class ColorUtil
{
    private static final String RAW_GRADIENT_HEX_REGEX = "<\\$#[A-Fa-f0-9]{6}>";
    
    public static String legacyToJson(final String legacyString) {
        if (legacyString == null) {
            return "";
        }
        return ComponentSerializer.toString(TextComponent.fromLegacyText(legacyString));
    }
    
    public static String jsonToLegacy(final String json) {
        if (json == null) {
            return "";
        }
        return BaseComponent.toLegacyText(ComponentSerializer.parse(json));
    }
    
    public static String colorString(String legacyMsg) {
        legacyMsg = gradient(legacyMsg);
        final List<String> formatCodes = Arrays.asList("&k", "&l", "&m", "&n", "&o", "&r");
        for (final String code : formatCodes) {
            legacyMsg = legacyMsg.replaceAll(code, ChatColor.getByChar(code.charAt(1)) + "");
        }
        legacyMsg = hex(legacyMsg);
        return legacyMsg;
    }
    
    private static String hex(String legacyMsg) {
        final Matcher matcher = Pattern.compile("<#[A-Fa-f0-9]{6}>").matcher(legacyMsg);
        int hexAmount = 0;
        while (matcher.find()) {
            matcher.region(matcher.end() - 1, legacyMsg.length());
            ++hexAmount;
        }
        int startIndex = 0;
        for (int hexIndex = 0; hexIndex < hexAmount; ++hexIndex) {
            final int msgIndex = legacyMsg.indexOf("<#", startIndex);
            final String hex = legacyMsg.substring(msgIndex + 1, msgIndex + 8);
            startIndex = msgIndex + 2;
            legacyMsg = legacyMsg.replaceFirst("<" + hex + ">", ChatColor.of(hex) + "");
        }
        return legacyMsg;
    }
    
    private static String gradient(final String legacyMsg) {
        final List<String> hexes = new ArrayList<String>();
        final Matcher matcher = Pattern.compile("<\\$#[A-Fa-f0-9]{6}>").matcher(legacyMsg);
        while (matcher.find()) {
            hexes.add(matcher.group().replace("<$", "").replace(">", ""));
        }
        int hexIndex = 0;
        final List<String> texts = new LinkedList<String>(Arrays.asList(legacyMsg.split("<\\$#[A-Fa-f0-9]{6}>")));
        final StringBuilder finalMsg = new StringBuilder();
        for (final String text : texts) {
            if (texts.get(0).equalsIgnoreCase(text)) {
                finalMsg.append(text);
            }
            else {
                if (text.length() == 0) {
                    continue;
                }
                if (hexIndex + 1 >= hexes.size()) {
                    if (finalMsg.toString().contains(text)) {
                        continue;
                    }
                    finalMsg.append(text);
                }
                else {
                    final String fromHex = hexes.get(hexIndex);
                    final String toHex = hexes.get(hexIndex + 1);
                    finalMsg.append(insertFades(text, fromHex, toHex, text.contains("&l"), text.contains("&o"), text.contains("&n"), text.contains("&m"), text.contains("&k")));
                    ++hexIndex;
                }
            }
        }
        return finalMsg.toString();
    }
    
    private static String insertFades(String msg, final String fromHex, final String toHex, final boolean bold, final boolean italic, final boolean underlined, final boolean strikethrough, final boolean magic) {
        msg = msg.replaceAll("&k", "");
        msg = msg.replaceAll("&l", "");
        msg = msg.replaceAll("&m", "");
        msg = msg.replaceAll("&n", "");
        msg = msg.replaceAll("&o", "");
        final int length = msg.length();
        final Color fromRGB = Color.decode(fromHex);
        final Color toRGB = Color.decode(toHex);
        double rStep = Math.abs((fromRGB.getRed() - toRGB.getRed()) / (double)length);
        double gStep = Math.abs((fromRGB.getGreen() - toRGB.getGreen()) / (double)length);
        double bStep = Math.abs((fromRGB.getBlue() - toRGB.getBlue()) / (double)length);
        if (fromRGB.getRed() > toRGB.getRed()) {
            rStep = -rStep;
        }
        if (fromRGB.getGreen() > toRGB.getGreen()) {
            gStep = -gStep;
        }
        if (fromRGB.getBlue() > toRGB.getBlue()) {
            bStep = -bStep;
        }
        Color finalColor = new Color(fromRGB.getRGB());
        msg = msg.replaceAll("<\\$#[A-Fa-f0-9]{6}>", "");
        msg = msg.replace("", "<$>");
        for (int index = 0; index <= length; ++index) {
            int red = (int)Math.round(finalColor.getRed() + rStep);
            int green = (int)Math.round(finalColor.getGreen() + gStep);
            int blue = (int)Math.round(finalColor.getBlue() + bStep);
            if (red > 255) {
                red = 255;
            }
            if (red < 0) {
                red = 0;
            }
            if (green > 255) {
                green = 255;
            }
            if (green < 0) {
                green = 0;
            }
            if (blue > 255) {
                blue = 255;
            }
            if (blue < 0) {
                blue = 0;
            }
            finalColor = new Color(red, green, blue);
            final String hex = "#" + Integer.toHexString(finalColor.getRGB()).substring(2);
            String formats = "";
            if (bold) {
                formats += ChatColor.BOLD;
            }
            if (italic) {
                formats += ChatColor.ITALIC;
            }
            if (underlined) {
                formats += ChatColor.UNDERLINE;
            }
            if (strikethrough) {
                formats += ChatColor.STRIKETHROUGH;
            }
            if (magic) {
                formats += ChatColor.MAGIC;
            }
            msg = msg.replaceFirst("<\\$>", "" + ChatColor.of(hex) + formats);
        }
        return msg;
    }
}
