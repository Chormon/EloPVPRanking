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

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chormon.elopvpranking.commands.*;
import pl.chormon.elopvprating.listeners.PlayerListeners;

/**
 *
 * @author Chormon
 */
public class EloPVPRanking extends JavaPlugin {

    private static EloPVPRanking plugin;
    private EloFile eloFile;
    public TreeMap<String, EloPlayer> eloPlayers;

    @Override
    public void onEnable() {
        plugin = this;
        PluginDescriptionFile pdf = this.getDescription();
        Config.initConfig();
        eloFile = new EloFile("elopoints.yml");
        new PlayerListeners();
        eloPlayers = eloFile.getPlayers();
        if (eloPlayers == null) {
            eloPlayers = new TreeMap<>();
        }
        getCommand("Elo").setExecutor(new Elo());
        getCommand("Elotop").setExecutor(new EloTop());
        getLogger().log(Level.INFO, "{0} {1} enabled!", new Object[]{pdf.getName(), pdf.getVersion()});
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        HandlerList.unregisterAll(this);
        getLogger().log(Level.INFO, "{0} {1} disabled!", new Object[]{pdf.getName(), pdf.getVersion()});
    }

    public EloFile getEloFile() {
        return eloFile;
    }

    public int calculateRanking(String name) {
        Map sorted = new TreeMap(new ValueComparator((eloPlayers)));
        sorted.putAll(eloPlayers);       
        int i = 1;
        for(Object s : sorted.keySet()) {
            if(name.equalsIgnoreCase((String) s))
                break;
            i++;
        }
        return i;
    }

    public static EloPVPRanking get() {
        return plugin;
    }

}