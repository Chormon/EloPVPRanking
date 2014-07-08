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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class EloFile {

    private final String fileName;
    private File configFile;
    private FileConfiguration fileConfiguration;
    private SimpleDateFormat sdf;

    public EloFile(String fileName) {
        if (EloPVPRanking.get() == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        if (!EloPVPRanking.get().isInitialized()) {
            throw new IllegalArgumentException("plugin must be initiaized");
        }
        this.fileName = fileName;
        File dataFolder = EloPVPRanking.get().getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = new File(EloPVPRanking.get().getDataFolder(), fileName);
        this.saveDefaultConfig();
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

        // Look for defaults in the jar
        InputStream defConfigStream = EloPVPRanking.get().getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) {
            return;
        } else {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                EloPVPRanking.get().getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }

    private void saveDefaultConfig() {
        if (!configFile.exists()) {
            EloPVPRanking.get().saveResource(fileName, false);
        }
        reloadConfig();
    }

    public void savePlayer(EloPlayer player) {
        setPlayer(player);
    }

    private void setPlayer(EloPlayer player) {
        String path = "players." + player.getUniqueId();
        this.fileConfiguration.set(path + ".name", player.getName());
        this.fileConfiguration.set(path + ".elopoints", player.getEloPoints());
        this.fileConfiguration.set(path + ".kills", player.getKills());
        this.fileConfiguration.set(path + ".deaths", player.getDeaths());
        List<String> lastKills = player.getLastKills();
        if (lastKills.size() > Config.getKillsHistory()) {
            int size = lastKills.size();
            int diff = size - Config.getKillsHistory();
            lastKills = lastKills.subList(diff, size);
        }
        List<String> lastDeaths = player.getLastDeaths();
        if (lastDeaths.size() > Config.getDeathsHistory()) {
            int size = lastDeaths.size();
            int diff = size - Config.getDeathsHistory();
            lastDeaths = lastDeaths.subList(diff, size);
        }
        this.fileConfiguration.set(path + ".lastKills", lastKills);
        this.fileConfiguration.set(path + ".lastDeaths", lastDeaths);
        this.fileConfiguration.set(path + ".lastVisit", sdf.format(player.getLastVisit()));
        saveConfig();
    }

    public boolean playerExists(UUID uniqueId) {
        String path = "players." + uniqueId.toString();
        return this.fileConfiguration.contains(path);
    }

    public UUID playerExists(String name) {
        String path = "players.";
        Set<String> uuids = this.fileConfiguration.getConfigurationSection("players").getKeys(false);
        for (String s : uuids) {
            String n = this.fileConfiguration.getString(path + s + ".name");
            if(name.equalsIgnoreCase(n))
                return UUID.fromString(s);
        }
        return null;
    }

    public EloPlayer getPlayer(UUID uniqueId) {
        String path = "players." + uniqueId;
        if (!this.fileConfiguration.contains(path)) {
            return null;
        }
        String name = this.fileConfiguration.getString(path + ".name");
        int eloPoints = this.fileConfiguration.getInt(path + ".elopoints");
        int kills = this.fileConfiguration.getInt(path + ".kills");
        int deaths = this.fileConfiguration.getInt(path + ".deaths");

        boolean needSave = false;
        List<String> lastKills = this.fileConfiguration.getStringList(path + ".lastKills");
        if (lastKills.size() > Config.getKillsHistory()) {
            int size = lastKills.size();
            int diff = size - Config.getKillsHistory();
            lastKills = lastKills.subList(diff, size);
            needSave = true;
        }
        List<String> lastDeaths = this.fileConfiguration.getStringList(path + ".lastDeaths");
        if (lastDeaths.size() > Config.getDeathsHistory()) {
            int size = lastDeaths.size();
            int diff = size - Config.getDeathsHistory();
            lastDeaths = lastDeaths.subList(diff, size);
            needSave = true;
        }
        Date date = null;
        try {
            date = sdf.parse(this.fileConfiguration.getString(path + ".lastVisit"));
        } catch (Exception ex) {
            date = new Date();
            needSave = true;
        }
        EloPlayer ep = new EloPlayer(uniqueId, name, eloPoints, kills, deaths, lastKills, lastDeaths, date);
        if (needSave) {
            savePlayer(ep);
        }
        return ep;
    }

    public String getPlayerName(UUID uniqueId) {
        String path = "players." + uniqueId;
        if (!this.fileConfiguration.contains(path)) {
            return null;
        }
        return this.fileConfiguration.getString(path + ".name");
    }

    public TreeMap<String, EloPlayer> getPlayers() {
        TreeMap<String, EloPlayer> players = new TreeMap<>();

        try {
            Set<String> uuids = this.fileConfiguration.getConfigurationSection("players").getKeys(false);

            for (String s : uuids) {
                EloPlayer ep = getPlayer(UUID.fromString(s));
                EloPlayer old = players.put(ep.getName().toLowerCase(), ep);
                if(old != null)
                    MsgUtils.warning("Player {oldplayer} was replaced with player {newplayer}", "{oldplayer}", old.getName(), "{newplayer}", ep.getName());
            }
            saveConfig();
            return players;
        } catch (Exception ex) {
            return null;
        }
    }

    public void removePlayer(UUID uniqueId) {
        this.fileConfiguration.getConfigurationSection("players").set(uniqueId.toString(), null);
    }

}
