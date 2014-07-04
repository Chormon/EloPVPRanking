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
package pl.chormon.elopvpranking.schedulers;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.bukkit.scheduler.BukkitRunnable;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.elopvpranking.EloPlayer;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class InactivePlayerRemover extends BukkitRunnable {
    private final long days;

    public InactivePlayerRemover(long days) {
        this.days = days;
    }

    @Override
    public void run() {
        if (this.days < 0) {
            return;
        }
        boolean removed = false;
        Date date = new Date();
        for (EloPlayer ep : EloPVPRanking.get().eloPlayers.values()) {
            long diffMil = date.getTime() - ep.getLastVisit().getTime();
            long diffDays = TimeUnit.DAYS.convert(diffMil, TimeUnit.MILLISECONDS);
            if (days <= diffDays) {
                if(EloPVPRanking.get().eloPlayers.remove(ep.getName().toLowerCase()) == null) {
                    MsgUtils.error("Couldn't remove player {player} due to {days} days of inactivity.", "{player}", ep.getName(), "{days}", diffDays);
                    return;
                }
                MsgUtils.info("Removed player {player} due to {days} days of inactivity.", "{player}", ep.getName(), "{days}", diffDays);
                removed = true;
                EloPVPRanking.get().getEloFile().removePlayer(ep.getUniqueId());
            }
        }
        if(removed)
            EloPVPRanking.get().getEloFile().saveConfig();
    }

}
