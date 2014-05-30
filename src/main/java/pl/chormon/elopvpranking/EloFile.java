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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chormon
 */
public class EloFile {

    private final String fileName;
    private File configFile;
    private FileConfiguration fileConfiguration;
    
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
        if(playerExists(player))
            setPlayer(player);
        else
            addPlayer(player);
    }
    
    private void addPlayer(EloPlayer player) {
        String section = "players."+player.getUniqueId();
        setPlayer(player);
    }
    
    private void setPlayer(EloPlayer player) {
        String section = "players."+player.getUniqueId();
        this.fileConfiguration.set(section+".name", player.getName());
        this.fileConfiguration.set(section+".elopoints", player.getEloPoints());
        this.fileConfiguration.set(section+".rank", player.getRaking());
        saveConfig();
    }
    
    public void setRanking() {
        saveConfig();
    }
    
    public void getRanking() {
        String path = "players";
        Set<String> players = this.fileConfiguration.getConfigurationSection(path).getKeys(false);
        SortedSet<Integer> eloPoints = new TreeSet<>();
        for(String p : players) {
            eloPoints.add(this.fileConfiguration.getInt(path + "." + p));
        }
    }
    
    public boolean playerExists(EloPlayer player) {
        String path = "players."+player.getUniqueId();
        return this.fileConfiguration.contains(path);
    }
    
    public EloPlayer getPlayer(UUID uniqueId) {
        String path = "players."+uniqueId;
        if(!this.fileConfiguration.contains(path))
            return null;
        String name = this.fileConfiguration.getString(path + ".name");
        int eloPoints = this.fileConfiguration.getInt(path + ".elopoints");
        return new EloPlayer(uniqueId, name, eloPoints);
    }
    
    public int getEloPoints(EloPlayer player) {
        String path = "players."+player.getUniqueId();
        return this.fileConfiguration.getInt(path+".elopoints");
    }
    
    public TreeMap<String, EloPlayer> getPlayers() {
        TreeMap<String, EloPlayer> players = new TreeMap<>();
        
        try {
            Set<String> uuids = this.fileConfiguration.getConfigurationSection("players").getKeys(false);

            for(String s : uuids) {
                EloPlayer ep = new EloPlayer(UUID.fromString(s), this.fileConfiguration.getString("players."+s+".name"), this.fileConfiguration.getInt("players."+s+".elopoints"));
                players.put(ep.getName().toLowerCase(), ep);
            }

            return players;
        } catch (Exception ex) {
            return null;
        }
    }
    
}
