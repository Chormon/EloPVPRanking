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
package pl.chormon.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author Chormon
 */
public class MsgUtils {

    private static ConsoleCommandSender console;
    private final static String prefix = "&e[EloPVPRanking]&r";

    public static void setConsole(ConsoleCommandSender console) {
        MsgUtils.console = console;
    }

//    private static void log(String msg) {
//        log(msg, (MsgVar) null);
//    }
    private static void log(String msg, MsgVar... vars) {
        msg(MsgUtils.console, prefix + msg, vars);
    }

    public static void info(String msg) {
        MsgUtils.log("&a[INFO]&r" + msg, (MsgVar) null);
    }

    public static void info(String msg, MsgVar... vars) {
        MsgUtils.log("&a[INFO]&r" + msg, vars);
    }

    public static void warning(String msg) {
        MsgUtils.log("&c[WARNING]&r" + msg, (MsgVar) null);
    }

    public static void warning(String msg, MsgVar... vars) {
        MsgUtils.log("&c[WARNING]&r" + msg, vars);
    }

    public static void error(String msg) {
        MsgUtils.log("&c[ERROR]&r" + msg, (MsgVar) null);
    }

    public static void error(String msg, MsgVar... vars) {
        MsgUtils.log("&c[ERROR]&r" + msg, vars);
    }

    public static void msg(CommandSender cs, String msg) {
        msg(cs, msg, (MsgVar) null);
    }

    public static void msg(CommandSender cs, String msg, MsgVar... vars) {
        cs.sendMessage(fixMsg(msg, vars));
    }

    private static String fixMsg(String msg, MsgVar... vars) {
        if (vars != null) {
            for (MsgVar var : vars) {
                if (var == null) {
                    continue;
                }
                msg = msg.replace(var.key, var.value);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
