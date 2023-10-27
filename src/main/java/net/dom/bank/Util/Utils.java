package net.dom.bank.Util;

import java.math.RoundingMode;
import java.math.BigDecimal;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import net.dom.bank.Objects.UserPreferences;

public class Utils
{
    public static String ParseObjectToGson(final UserPreferences settings) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(settings, UserPreferences.class);
    }
    
    public static UserPreferences ParseGsonToObject(final String json) {
        return new Gson().fromJson(json, UserPreferences.class);
    }
    
    public static double RoundNumber(final double num) {
        return new BigDecimal(num).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
