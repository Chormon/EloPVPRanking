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

import org.bukkit.command.Command;
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
public class CmdEloReset extends EloCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EloPVPRanking plugin = EloPVPRanking.get();
        boolean senderIsConsole = false;
        if (!(sender instanceof Player)) {
            senderIsConsole = true;
        }
        if (args.length > 1) {
            printTooManyArgs(sender);
            return true;
        }
        if (args.length > 0) {
            String name = args[0].toLowerCase();
            EloPlayer ep = plugin.eloPlayers.get(name);
            if (ep == null) {
                MsgUtils.msg(sender, Config.getMessage("playerNotExist"), "{player}", name);
                return true;
            }
            try {
                ep.reset();
                MsgUtils.msg(sender, Config.getMessage("rankingReseted"), "{player}", ep.getName());
            } catch (Exception ex) {
                MsgUtils.msg(sender, Config.getMessage("rankingNotReseted"), "{player}", ep.getName());
            }
            return true;
        } else {
            printNotEnoughArgs(sender);
            return true;
        }
    }

    @Override
    protected void printUsage(CommandSender sender) {
        MsgUtils.msg(sender, Config.getMessage("usage"), "{command}", "/eloreset [gracz]");
    }

}
