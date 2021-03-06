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
package pl.chormon.elopvpranking.listeners;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.chormon.elopvpranking.Config;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.elopvpranking.EloPlayer;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class PlayerOnDeath implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent e) {
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
        if (e.getEntity().getKiller().equals(e.getEntity())) {
            return;
        }
        List<String> worlds = Config.getWorlds();

        String world = e.getEntity().getWorld().getName();

        if (!worlds.isEmpty() && !worlds.contains(world)) {
            return;
        }

        EloPlayer victim = EloPVPRanking.get().eloPlayers.get(e.getEntity().getName().toLowerCase());
        if (victim == null) {
            Player p = e.getEntity();
            victim = EloPVPRanking.get().eloPlayers.get(p.getName().toLowerCase());
        }
        EloPlayer killer = EloPVPRanking.get().eloPlayers.get(e.getEntity().getKiller().getName().toLowerCase());
        if (killer == null) {
            Player p = e.getEntity().getKiller();
            killer = EloPVPRanking.get().eloPlayers.get(p.getName().toLowerCase());
        }

        int Rv = victim.getEloPoints();
        int Rk = killer.getEloPoints();
//        float Ev = (float) (1 / (1 + Math.pow(10, (Rk - Rv) / 400f)));
//        float Ek = (float) (1 / (1 + Math.pow(10, (Rv - Rk) / 400f)));
        int C = Config.getConstantValue();
//        int Rvn = Math.max(Math.round(Rv + C * (-Ev)), Integer.MIN_VALUE);
//        int Rkn = Math.min(Math.round(Rk + C * (1 - Ek)), Integer.MAX_VALUE);
//
//        if (Rvn < Config.getMinPoints()) {
//            Rvn = Config.getMinPoints();
//        }
//        if (Rkn > Config.getMaxPoints()) {
//            Rkn = Config.getMaxPoints();
//        }
        
        int min = Config.getMinPoints();
        int max = Config.getMaxPoints();
        int Rvn = calcElo(Rv, Rk, C, false, min, max);
        int Rkn = calcElo(Rk, Rv, C, true, min, max);

        victim.setEloPoints(Rvn);
        victim.addDeath(killer);
        victim.save();
        killer.setEloPoints(Rkn);
        killer.addKill(victim);
        killer.save();
        MsgUtils.msg(EloPVPRanking.get().getServer().getPlayer(victim.getUniqueId()), Config.getMessage("lostPoints"), "{lost}", Rv - Rvn, "{points}", Rvn, "{ranking}", victim.getRanking());
        MsgUtils.msg(EloPVPRanking.get().getServer().getPlayer(killer.getUniqueId()), Config.getMessage("gainedPoints"), "{gained}", Rkn - Rk, "{points}", Rkn, "{ranking}", killer.getRanking());
        if (Config.getLogPointsChange()) {
            MsgUtils.info("Player &f{player} &rlost &f{lost} &rpoints and now has &f{points}&r.", "{player}", victim.getName(), "{lost}", Rv - Rvn, "{points}", Rvn);
            MsgUtils.info("Player &f{player}&r gained &f{gained} &rpoints and now has &f{points}&r.", "{player}", killer.getName(), "{gained}", Rkn - Rk, "{points}", Rkn);
        }
    }

    private static int calcElo(int my, int opp, int C, boolean win, int min, int max) {
        float e = (float) (1 / (1 + Math.pow(10, (opp - my) / 400f)));
        int newElo;

        if (win) {
            newElo = Math.min(Math.round(my + C * (1 - e)), Integer.MAX_VALUE);
        } else {
            newElo = Math.max(Math.round(my + C * (-e)), Integer.MIN_VALUE);
        }

        if (newElo < min) {
            return min;
        } else if (newElo > max) {
            return max;
        }

        return newElo;
    }

}
