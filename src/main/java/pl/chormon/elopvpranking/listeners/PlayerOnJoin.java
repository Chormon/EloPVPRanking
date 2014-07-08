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

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.elopvpranking.EloPlayer;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class PlayerOnJoin implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        EloPlayer ep2 = EloPVPRanking.get().eloPlayers.get(p.getName().toLowerCase());
        UUID uniqueId = p.getUniqueId();
        if (ep2 != null && !ep2.getUniqueId().equals(p.getUniqueId())) {
            uniqueId = ep2.getUniqueId();
            sendMessage(p.getName(), ep2.getUniqueId().toString(), ep2.getName());
        } else {
            UUID uuid = EloPVPRanking.get().getEloFile().playerExists(p.getName());
            if (uuid != null && !uuid.equals(p.getUniqueId())) {
                uniqueId = uuid;
                sendMessage(p.getName(), uuid.toString(), EloPVPRanking.get().getEloFile().getPlayerName(uuid));
            }
        }
        EloPlayer ep = new EloPlayer(uniqueId, p.getName());
        ep.setLastVisit();
        ep.save();
    }

    private void sendMessage(String name, String uuid, String name2) {
        MsgUtils.warning("Player {player} is already in database under different UUID ({uuid}) and name ({name})!", "{player}", name, "{uuid}", uuid, "{name}", name2);
    }

}
