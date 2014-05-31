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

    public EloPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        EloPlayer ep = EloPVPRanking.get().getEloFile().getPlayer(uniqueId);
        if (ep != null) {
            this.name = name;
            this.eloPoints = ep.getEloPoints();
            this.kills = ep.getKills();
            this.deaths = ep.getDeaths();
            return;
        }
        this.name = name;
        this.eloPoints = Config.getStartingPoints();
        this.kills = 0;
        this.deaths = 0;
    }

    public EloPlayer(UUID uniqueId, String name, int eloPoints, int kills, int deaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.eloPoints = eloPoints;
        this.kills = kills;
        this.deaths = deaths;
    }

    public void save() {
        EloPVPRanking.get().getEloFile().savePlayer(this);
        if(EloPVPRanking.get().eloPlayers.containsKey(name.toLowerCase())) {
            EloPlayer ep = EloPVPRanking.get().eloPlayers.get(name.toLowerCase());
            if(this != ep) {
                EloPVPRanking.get().eloPlayers.remove(name.toLowerCase());
                EloPVPRanking.get().eloPlayers.put(name.toLowerCase(), this);
            }
        } else 
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
    
    public void addKill() {
        kills++;
    }
    
    public void addDeath() {
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

    @Override
    public int compareTo(EloPlayer o) {
        int res = Integer.compare(o.eloPoints, eloPoints);
        return res != 0 ? res : name.compareTo(o.name);
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
