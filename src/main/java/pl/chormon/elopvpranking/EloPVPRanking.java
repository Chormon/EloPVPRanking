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

import pl.chormon.elopvpranking.listeners.PlayerOnQuit;
import pl.chormon.elopvpranking.listeners.PlayerOnDeath;
import pl.chormon.elopvpranking.listeners.PlayerOnJoin;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chormon.elopvpranking.commands.*;
import pl.chormon.elopvpranking.schedulers.InactivePlayerRemover;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class EloPVPRanking extends JavaPlugin {

    private static final String version = "1.0.4";

    private static EloPVPRanking plugin;
    private EloFile eloFile;
    public TreeMap<String, EloPlayer> eloPlayers;

    @Override
    public void onEnable() {
        plugin = this;
        PluginDescriptionFile pdf = this.getDescription();
        Config.initConfig();
        eloFile = new EloFile("elopoints.yml");
        getServer().getPluginManager().registerEvents(new PlayerOnJoin(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerOnQuit(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerOnDeath(), plugin);
        MsgUtils.setConsole(Bukkit.getConsoleSender());
        eloPlayers = eloFile.getPlayers();
        if (eloPlayers == null) {
            eloPlayers = new TreeMap<>();
        }
        getCommand("Elo").setExecutor(new CmdElo());
        getCommand("Elotop").setExecutor(new CmdEloTop());
        getCommand("EloReload").setExecutor(new CmdEloReload());
        getCommand("EloInfo").setExecutor(new CmdEloInfo());
        new InactivePlayerRemover(Config.getRemoveAfter()).runTaskTimer(plugin, 0L, 72000);
        MsgUtils.info("{name} {version} enabled!", "{name}", pdf.getName(), "{version}", pdf.getVersion());
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        HandlerList.unregisterAll(this);
        MsgUtils.info("{name} {version} disabled!", "{name}", pdf.getName(), "{version}", pdf.getVersion());
    }

    public EloFile getEloFile() {
        return eloFile;
    }

    public static EloPVPRanking get() {
        return plugin;
    }

    public static String getVersion() {
        return version;
    }

}
