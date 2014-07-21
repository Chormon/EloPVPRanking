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

import java.util.Map;
import java.util.TreeMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.chormon.elopvpranking.Config;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.elopvpranking.EloPlayer;
import pl.chormon.elopvpranking.ValueComparator;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class CmdEloTop extends EloCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        EloPVPRanking plugin = EloPVPRanking.get();
        int page = 1;
        if (args.length > 1) {
            printTooManyArgs(sender);
            return true;
        }
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (Exception e) {
                MsgUtils.msg(sender, Config.getMessage("integer"));
                printUsage(sender);
                return true;
            }
            if (page < 1) {
                MsgUtils.msg(sender, Config.getMessage("integer"));
                printUsage(sender);
                return true;
            }
        }
        int amount = Config.getPlayersTop() > plugin.eloPlayers.size() ? plugin.eloPlayers.size() : Config.getPlayersTop();
        if (amount == 0) {
            MsgUtils.msg(sender, Config.getMessage("noPlayers"));
            return true;
        }
        int perpage = Config.getPlayersPerPage();
        int pages = amount % perpage == 0 ? amount / perpage : amount / perpage + 1;
        int last = amount % perpage == 0 ? perpage : amount % perpage;
        int start = perpage * (page - 1);
        int mypage = page;
        if (page >= pages) {
            mypage = pages - 1;
            perpage = last;
            start = perpage * mypage;
        }
        int i = 0, cnt = 0;
        EloPlayer[] top = new EloPlayer[perpage];
        Map sorted = new TreeMap(new ValueComparator((plugin.eloPlayers)));
        sorted.putAll(plugin.eloPlayers);
        for (Object ep : sorted.values()) {
            if (ep == null) {
                continue;
            }
            if (cnt >= start && i < perpage) {
                top[i++] = (EloPlayer) ep;
            }
            cnt++;
        }
        i = start;
        MsgUtils.msg(sender, Config.getMessage("topHeader"), "{amount}", amount);
        for (EloPlayer ep : top) {
            MsgUtils.msg(sender, Config.getMessage("top"), "{lp}", ++i, "{player}", ep.getName(), "{points}", ep.getEloPoints());
        }
        if (page < pages) {
            MsgUtils.msg(sender, Config.getMessage("topMore"), "{page}", mypage + 1);
        }
        return true;
    }

    @Override
    protected void printUsage(CommandSender sender) {
        MsgUtils.msg(sender, Config.getMessage("usage"), "{command}", "/elotop [strona]");
    }

}
