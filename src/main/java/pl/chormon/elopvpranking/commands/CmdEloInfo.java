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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.chormon.elopvpranking.Config;
import pl.chormon.elopvpranking.EloPVPRanking;
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class CmdEloInfo extends EloCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            printTooManyArgs(sender);
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("&2Wersja: &f");
        sb.append(EloPVPRanking.getVersion());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2-----[&f EloPVPRanking &2]-----"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', sb.toString()));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Autor: &fChormon"));
        return true;
    }

    @Override
    protected void printUsage(CommandSender sender) {
        MsgUtils.msg(sender, Config.getMessage("usage"), "{command}", "/eloinfo");
    }

}
