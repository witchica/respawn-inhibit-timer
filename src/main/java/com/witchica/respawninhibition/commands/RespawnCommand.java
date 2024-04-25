package com.witchica.respawninhibition.commands;

import com.witchica.respawninhibition.RespawnInhibition;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RespawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player) {
            if(RespawnInhibition.getInstance().isPlayerOnCooldown(player.getUniqueId()) && RespawnInhibition.getInstance().canPlayerRespawn(player.getUniqueId())) {
                RespawnInhibition.getInstance().respawnPlayer(player);
                RespawnInhibition.getInstance().clearPlayerEntry(player.getUniqueId());
            }
        }
        return false;
    }
}
