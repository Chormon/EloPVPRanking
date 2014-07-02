/*
 * The MIT License
 *
 * Copyright 2014 Chormon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.chormon.elopvpranking;

import java.text.MessageFormat;
import java.util.List;
import org.bukkit.ChatColor;

/**
 *
 * @author Chormon
 */
public class Config {

    public static void initConfig() {
        EloPVPRanking.get().reloadConfig();
        EloPVPRanking.get().saveDefaultConfig();
    }
    
    public static String getMessage(String path)
    {
        return EloPVPRanking.get().getConfig().getString("messages." + path);
    }
    
    @Deprecated
    public static String getMessage(String path, Object... params) {
        String message = "";
        try {
            message = ChatColor.translateAlternateColorCodes('&', EloPVPRanking.get().getConfig().getString("messages." + path));
        } catch (Exception e) {
            return "";
        }
        if (params != null) {
            return MessageFormat.format(message, params);
        } else {
            return message;
        }
    }
    
    public static int getStartingPoints() {
        return EloPVPRanking.get().getConfig().getInt("settings.startingPoints");
    }
    
    public static int getConstantValue() {
        return EloPVPRanking.get().getConfig().getInt("settings.constantValue");
    }    
    
    public static int getPlayersTop() {
        return EloPVPRanking.get().getConfig().getInt("settings.playersTop");
    }
    
    public static int getPlayersPerPage() {
        return EloPVPRanking.get().getConfig().getInt("settings.playersPerPage");
    }
    
    public static boolean getLogPointsChange() {
        return EloPVPRanking.get().getConfig().getBoolean("settings.logPointsChange");
    }
    
    public static int getMaxPoints() {
        if(EloPVPRanking.get().getConfig().getString("settings.maxPoints").equals("inf"))
            return Integer.MAX_VALUE;
        return EloPVPRanking.get().getConfig().getInt("settings.maxPoints");
    }
    
    public static int getMinPoints() {
        if(EloPVPRanking.get().getConfig().getString("settings.minPoints").equals("-inf"))
            return Integer.MIN_VALUE;
        return EloPVPRanking.get().getConfig().getInt("settings.minPoints");
    }
    
    public static List<String> getWorlds() {
        return EloPVPRanking.get().getConfig().getStringList("settings.worlds");
    }
}
