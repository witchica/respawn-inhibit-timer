package com.witchica.respawninhibition.commands;

import com.witchica.respawninhibition.PlayerRespawnData;
import com.witchica.respawninhibition.RespawnInhibition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RespawnInhibitionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!RespawnInhibition.getInstance().getEnabledState()) {
            commandSender.sendMessage("Respawn Inhibition is not enabled.");
            return true;
        }

        if(args.length == 0) {
            return false;
        }

        String subcommand = args[0];

        switch(subcommand) {
            case "enable": {
                RespawnInhibition.getInstance().setEnabledState(true);
                return true;
            }
            case "disable": {
                RespawnInhibition.getInstance().setEnabledState(false);
                return true;
            }
            case "settime": {
                if(args.length > 2) {
                    String username = args[1];
                    long time = Long.parseLong(args[2]);
                    boolean resetTime = false;

                    if(args.length == 4) {
                        resetTime = Boolean.parseBoolean(args[3]);
                    }

                    boolean result = RespawnInhibition.getInstance().setPlayerRespawnTimer(Bukkit.getServer().getPlayerUniqueId(username), time, resetTime);

                    if(!result) {
                        commandSender.sendMessage(Component.text("Player is not currently on cooldown").color(NamedTextColor.RED));
                    } else {
                        commandSender.sendMessage(Component.text("Updated cooldown for " + username + ".").color(NamedTextColor.GOLD));
                    }

                    return true;
                }
            }
            case "checktime": {
                if(args.length > 1) {
                    String username = args[1];
                    UUID uuid = Bukkit.getPlayerUniqueId(username);

                    if(RespawnInhibition.getInstance().isPlayerOnCooldown(uuid)) {
                        long seconds = RespawnInhibition.getInstance().getRemainingTime(uuid);
                        commandSender.sendMessage(Component.text(username + " has " + seconds + "s remaining on their cooldown").color(NamedTextColor.GOLD));
                    } else {
                        commandSender.sendMessage(Component.text("Player is not currently on cooldown").color(NamedTextColor.RED));
                    }

                    return true;
                }
            }
            case "list": {
                if(RespawnInhibition.getInstance().getPlayerRespawns().size() == 0) {
                    commandSender.sendMessage(Component.text("No players are currently on cooldown").color(NamedTextColor.RED));
                }
                for(String id : RespawnInhibition.getInstance().getPlayerRespawns().keySet()) {
                    long timeRemaining = RespawnInhibition.getInstance().getRemainingTime(UUID.fromString(id));

                    commandSender.sendMessage(Component.empty()
                            .append(Component.text(Bukkit.getServer().getPlayer(UUID.fromString(id)).getName() + ": "))
                            .append(Component.text("" + timeRemaining + "s").color(NamedTextColor.RED)));
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static List<String> tabCompleter(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<String>();

        if(args.length == 1) {
            options.add("enable");
            options.add("disable");
            options.add("list");
            options.add("checktime");
            options.add("settime");
            options.add("end");
        } else if(args.length > 1) {
            String subcommand = args[0];

            switch(subcommand) {
                case "checktime", "end": {
                    if(args.length == 2) {
                        Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                            options.add(p.getName());
                        });
                    }
                }
                case "settime": {
                    if(args.length == 2) {
                        Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                            options.add(p.getName());
                        });
                    } else if(args.length == 4) {
                        options.add("true");
                        options.add("false");
                    }
                }
            }
        }

        return options;
    }
}
