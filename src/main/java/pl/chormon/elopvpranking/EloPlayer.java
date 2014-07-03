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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 *
 * @author Chormon
 */
public class EloPlayer implements Comparable<EloPlayer> {

    private final UUID uniqueId;
    private final String name;
    private int eloPoints;
    private int kills;
    private int deaths;
    private List<String> lastKills;
    private List<String> lastDeaths;

    public EloPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        EloPlayer ep = EloPVPRanking.get().getEloFile().getPlayer(uniqueId);
        if (ep != null) {
            this.name = name;
            this.eloPoints = ep.getEloPoints();
            this.kills = ep.getKills();
            this.deaths = ep.getDeaths();
            this.lastKills = ep.getLastKills();
            this.lastDeaths = ep.getLastDeaths();
            return;
        }
        this.name = name;
        this.eloPoints = Config.getStartingPoints();
        this.kills = 0;
        this.deaths = 0;
        this.lastKills = new ArrayList<>();
        this.lastDeaths = new ArrayList<>();
    }

    public EloPlayer(UUID uniqueId, String name, int eloPoints, int kills, int deaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.eloPoints = eloPoints;
        this.kills = kills;
        this.deaths = deaths;
        this.lastKills = new ArrayList<>();
        this.lastDeaths = new ArrayList<>();
    }

    public EloPlayer(UUID uniqueId, String name, int eloPoints, int kills, int deaths, List<String> lastKills, List<String> lastDeaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.eloPoints = eloPoints;
        this.kills = kills;
        this.deaths = deaths;
        this.lastKills = lastKills;
        this.lastDeaths = lastDeaths;
    }

    public void save() {
        String lastName = EloPVPRanking.get().getEloFile().getPlayerName(uniqueId);
        EloPVPRanking.get().getEloFile().savePlayer(this);
        if(EloPVPRanking.get().eloPlayers.containsKey(name.toLowerCase())) {
            EloPlayer ep = EloPVPRanking.get().eloPlayers.get(name.toLowerCase());
            if(this != ep) {
                EloPVPRanking.get().eloPlayers.remove(name.toLowerCase());
                EloPVPRanking.get().eloPlayers.put(name.toLowerCase(), this);
            }
            return;
        }
        if(lastName != null && !lastName.isEmpty() && EloPVPRanking.get().eloPlayers.containsKey(lastName.toLowerCase())) {
            EloPVPRanking.get().eloPlayers.remove(lastName.toLowerCase());
            EloPVPRanking.get().eloPlayers.put(name.toLowerCase(), this);
            return;
        }
        EloPVPRanking.get().eloPlayers.put(name.toLowerCase(), this);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public int getEloPoints() {
        return eloPoints;
    }

    public void setEloPoints(int eloPoints) {
        this.eloPoints = eloPoints;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }
    
    public void addKill(EloPlayer ep) {
        if(lastKills.size() >= Config.getKillsHistory()) {
            lastKills = lastKills.subList(1, lastKills.size());
        }
        lastKills.add(ep.getName());
        kills++;
    }
    
    public void addDeath(EloPlayer ep) {
        if(lastDeaths.size() >= Config.getDeathsHistory()) {
            lastDeaths = lastDeaths.subList(1, lastDeaths.size());
        }
        lastDeaths.add(ep.getName());
        deaths++;
    }
    
    public float getKDR() {
        if(deaths == 0)
            return kills;
        return kills/deaths;
    }

    public int getRanking() {
        EloPVPRanking plugin = EloPVPRanking.get();
        Map sorted = new TreeMap(new ValueComparator((plugin.eloPlayers)));
        sorted.putAll(plugin.eloPlayers);       
        int i = 1;
        for(Object s : sorted.keySet()) {
            if(name.equalsIgnoreCase((String) s))
                break;
            i++;
        }
        return i;
    }
    
    public List<String> getLastKills() {
        return lastKills;
    }
    
    public List<String> getLastDeaths() {
        return lastDeaths;
    }

    @Override
    public int compareTo(EloPlayer o) {
        int ept = Integer.compare(o.eloPoints, eloPoints);
        if(ept != 0)
            return ept;
        int kdr = Float.compare(o.getKDR(), getKDR());
        if(kdr != 0)
            return kdr;
        int k = Integer.compare(o.kills, kills);
        if(k != 0)
            return k;
        int d = Integer.compare(deaths, o.deaths);
        if(d != 0)
            return d;
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EloPlayer) {
            EloPlayer ep = (EloPlayer) obj;
            if(ep.getName() != this.name)
                return false;
            if(ep.getEloPoints()!= this.eloPoints)
                return false;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "EloPlayer{" + "uniqueId=" + uniqueId + ", name=" + name + ", eloPoints=" + eloPoints + ", kills=" + kills + ", deaths=" + deaths + '}';
    }
    
}
