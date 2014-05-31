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

package pl.chormon.elopvprating.listeners;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.chormon.elopvpranking.Config;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.elopvpranking.EloPlayer;

/**
 *
 * @author Chormon
 */
public class PlayerListeners implements Listener {

    public PlayerListeners() {
        EloPVPRanking.get().getServer().getPluginManager().registerEvents(this, EloPVPRanking.get());
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent e) {
        if(!(e.getEntity().getKiller() instanceof Player))
            return;
        EloPlayer victim = EloPVPRanking.get().eloPlayers.get(e.getEntity().getName().toLowerCase());
        EloPlayer killer = EloPVPRanking.get().eloPlayers.get(e.getEntity().getKiller().getName().toLowerCase());
        if(victim == null || killer == null)
            return;
        int Rv = victim.getEloPoints();
        int Rk = killer.getEloPoints();
        float Ev = (float) (1/(1+Math.pow(10,(Rk-Rv)/400f)));
        float Ek = (float) (1/(1+Math.pow(10,(Rv-Rk)/400f)));
        int C = Config.getConstantValue();
        int Rvn = Math.min(Math.round(Rv + C * (-Ev)), Integer.MIN_VALUE);
        int Rkn = Math.max(Math.round(Rk + C * (1-Ek)), Integer.MAX_VALUE);
        
        if(Rvn < Config.getMinPoints())
            Rvn = Config.getMinPoints();
        if(Rkn > Config.getMaxPoints())
            Rkn = Config.getMaxPoints();
        
        victim.setEloPoints(Rvn);
        victim.addDeath();
        victim.save();
        EloPVPRanking.get().getServer().getPlayer(victim.getUniqueId()).sendMessage(Config.getMessage("lostPoints", Rv - Rvn, Rvn, EloPVPRanking.get().calculateRanking(killer.getName())));
        if(Config.getLogPointsChange())
            EloPVPRanking.get().getLogger().log(Level.INFO, "Player {0} lost {1} points and now has {2}.", new Object[] {victim.getName(), Rv - Rvn, Rvn});
        killer.setEloPoints(Rkn);
        killer.addKill();
        killer.save();
        EloPVPRanking.get().getServer().getPlayer(killer.getUniqueId()).sendMessage(Config.getMessage("geinedPoints", Rv - Rvn, Rvn, EloPVPRanking.get().calculateRanking(killer.getName())));
        if(Config.getLogPointsChange())
            EloPVPRanking.get().getLogger().log(Level.INFO, "Player {0} gained {1} points and now has {2}.", new Object[] {killer.getName(), Rk - Rkn, Rkn});
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        EloPlayer ep = new EloPlayer(p.getUniqueId(), p.getName());
        ep.save();
    }
}
