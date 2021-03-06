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
import pl.chormon.utils.MsgUtils;

/**
 *
 * @author Chormon
 */
public class CmdEloReload extends EloCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            printTooManyArgs(sender);
            return true;
        }
        EloPVPRanking plugin = EloPVPRanking.get();
        plugin.reloadConfig();
        plugin.eloPlayers.clear();
        plugin.getEloFile().reloadConfig();
        plugin.eloPlayers.putAll(plugin.getEloFile().getPlayers());
        if (sender instanceof Player) {
            MsgUtils.msg(sender, "&2Plugin reloaded!");
        } else {
            MsgUtils.info("Plugin reloaded!");
        }
        return true;
    }

    @Override
    protected void printUsage(CommandSender sender) {
        MsgUtils.msg(sender, Config.getMessage("usage"), "{command}", "/eloreload");
    }

}
