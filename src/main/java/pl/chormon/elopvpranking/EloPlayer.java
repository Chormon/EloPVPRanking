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

import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author Chormon
 */
public class EloPlayer implements Comparable<EloPlayer> {

    private final UUID uniqueId;
    private final String name;
    private int eloPoints;
    private int raking;

    public EloPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        EloPlayer ep = EloPVPRanking.get().getEloFile().getPlayer(uniqueId);
        if (ep != null) {
            this.name = ep.getName();
            this.eloPoints = ep.getEloPoints();
            this.raking = ep.getRaking();
            return;
        }
        Player OnlinePlayer = EloPVPRanking.get().getServer().getPlayer(uniqueId);
        this.name = OnlinePlayer != null ? OnlinePlayer.getName() : EloPVPRanking.get().getServer().getOfflinePlayer(uniqueId).getName();
        this.eloPoints = Config.getStartingPoints();
        this.raking = 0;
    }

    public EloPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.eloPoints = Config.getStartingPoints();
    }

    public EloPlayer(UUID uniqueId, String name, int eloPoints) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.eloPoints = eloPoints;
        this.raking = 0;
    }

    public EloPlayer(UUID uniqueId, String name, int eloPoints, int raking) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.eloPoints = eloPoints;
        this.raking = raking;
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

    public int getRaking() {
        return raking;
    }

    public void setEloPoints(int eloPoints) {
        this.eloPoints = eloPoints;
    }

    public void setRaking(int raking) {
        this.raking = raking;
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
            if(ep.getRaking()!= this.raking)
                return false;
            if(ep.getEloPoints()!= this.eloPoints)
                return false;
            return true;
        }
        return false;
    }
    
}
