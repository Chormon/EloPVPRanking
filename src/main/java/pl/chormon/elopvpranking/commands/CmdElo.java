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
package pl.chormon.elopvpranking.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chormon.elopvpranking.Config;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.elopvpranking.EloPlayer;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class CmdElo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        EloPVPRanking plugin = EloPVPRanking.get();
        if (args.length > 0) {
            if (args.length > 1) {
                return false;
            }
            String name = args[0].toLowerCase();
            if (sender.hasPermission("elopvpranking.elo.others") || !(sender instanceof Player)) {
                EloPlayer ep = plugin.eloPlayers.get(name);
                if (ep == null) {
                    MsgUtils.msg(sender, Config.getMessage("playerNotExist"), "{player}", name);
                    return true;
                }
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (ep.getUniqueId().equals(p.getUniqueId())) {
                        displayElo(sender, true, ep);
                        return true;
                    }
                }
                displayElo(sender, false, ep);
                return true;
            } else {
                MsgUtils.msg(sender, "&4Nie masz do tego uprawnień!");
            }
        } else if (sender instanceof Player) {
            EloPlayer ep = plugin.eloPlayers.get(((Player) sender).getName().toLowerCase());
            if (ep != null) {
                displayElo(sender, true, ep);
            } else {
                MsgUtils.msg(sender, Config.getMessage("error"));
            }
            return true;
        }
        return false;
    }

    public void displayElo(CommandSender sender, boolean self, EloPlayer ep) {
        if (self) {
            MsgUtils.msg(sender, Config.getMessage("eloHeader"));
        } else {
            MsgUtils.msg(sender, Config.getMessage("eloHeaderOther"), "{player}", ep.getName());
        }
        MsgUtils.msg(sender, Config.getMessage("eloKills"), "{kills}", ep.getKills(), "{deaths}", ep.getDeaths(), "{kdr}", ep.getKDR());
        MsgUtils.msg(sender, Config.getMessage("eloPoints"), "{points}", ep.getEloPoints(), "{ranking}", ep.getRanking());
        StringBuilder sb1 = new StringBuilder();
        List<String> lastKills = ep.getLastKills();
        String separator = Config.getMessage("separator");
        if (lastKills.isEmpty()) {
            sb1.append(Config.getMessage("none"));
        } else {
            for (int i = Config.getKillsHistory() > lastKills.size() ? lastKills.size() - 1 : Config.getKillsHistory() - 1; i >= 0; i--) {
                sb1.append(lastKills.get(i));
                if (i > 0) {
                    sb1.append(separator);
                }
            }
        }
        StringBuilder sb2 = new StringBuilder();
        List<String> lastDeaths = ep.getLastDeaths();
        if (lastDeaths.isEmpty()) {
            sb2.append(Config.getMessage("none"));
        } else {
            for (int i = Config.getDeathsHistory() > lastDeaths.size() ? lastDeaths.size() - 1 : Config.getDeathsHistory() - 1; i >= 0; i--) {
                sb2.append(lastDeaths.get(i));
                if (i > 0) {
                    sb2.append(separator);
                }
            }
        }
        MsgUtils.msg(sender, Config.getMessage("lastKills"), "{players}", sb1.toString());
        MsgUtils.msg(sender, Config.getMessage("lastDeaths"), "{players}", sb2.toString());
    }

}
