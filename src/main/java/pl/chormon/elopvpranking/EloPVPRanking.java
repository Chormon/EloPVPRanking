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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
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
        getLogger().log(Level.INFO, "{0} {1} enabled!", new Object[]{pdf.getName(), pdf.getVersion()});
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        HandlerList.unregisterAll(this);
        getLogger().log(Level.INFO, "{0} {1} disabled!", new Object[]{pdf.getName(), pdf.getVersion()});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("elo")) {
            if (args.length > 0) {
                if (args.length > 1) {
                    return false;
                }
                String name = args[0].toLowerCase();
                if (sender.hasPermission("elopvpranking.elo.others") || !(sender instanceof Player)) {
                    EloPlayer ep = eloPlayers.get(name);
                    if (ep == null) {
                        sender.sendMessage(Config.getMessage("playerNotExist", name));
                        return true;
                    }
                    sender.sendMessage(Config.getMessage("eloOther", ep.getName(), ep.getEloPoints(), calculateRanking(ep.getName())));
                    return true;
                }
            } else if (sender instanceof Player) {
                EloPlayer ep = eloPlayers.get(((Player) sender).getName().toLowerCase());
                if (ep != null) {
                    sender.sendMessage(Config.getMessage("elo", ep.getEloPoints(), calculateRanking(ep.getName())));
                } else {
                    sender.sendMessage(Config.getMessage("error"));
                }
                return true;
            }
            return false;
        }
        if (command.getName().equalsIgnoreCase("elotop")) {
            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    return false;
                }
                if (page < 1) {
                    return false;
                }
            }
            int amount = Config.getPlayersTop() > eloPlayers.size() ? eloPlayers.size() : Config.getPlayersTop();
            int perpage = Config.getPlayersPerPage();
            int pages = amount / perpage;
            int last = amount % perpage == 0 ? perpage : amount % perpage;
            int start = perpage * (page - 1);
            int mypage = page;
            if (page > pages) {
                start -= perpage;
                mypage = pages - 1;
                perpage = last;
            }
            int i = 0, cnt = 0;
            EloPlayer[] top = new EloPlayer[perpage];
            Map sorted = new TreeMap(new ValueComparator((eloPlayers)));
            sorted.putAll(eloPlayers);
            for (Object ep : sorted.values()) {
                if (cnt >= start && i < perpage) {
                    top[i++] = (EloPlayer) ep;
                }
                cnt++;
            }
            i = start;
            sender.sendMessage(Config.getMessage("topHeader", amount));
            for (EloPlayer ep : top) {
                sender.sendMessage(Config.getMessage("top", ++i, ep.getName(), ep.getEloPoints()));
            }
            if (page < pages) {
                sender.sendMessage(Config.getMessage("topMore", mypage + 1));
            }
            return true;
        }
        return false;
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