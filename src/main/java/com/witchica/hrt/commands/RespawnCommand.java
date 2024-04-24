package com.witchica.hrt.commands;

import com.witchica.hrt.HardcoreRespawnTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RespawnCommand extends BukkitCommand {
    protected RespawnCommand() {
        super("respawn");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String alais, @NotNull String[] args) {
        UUID commandSenderUUID = Bukkit.getPlayerUniqueId(commandSender.getName());

        if(HardcoreRespawnTimer.getInstance().playerDeathMap.containsKey(commandSenderUUID)) {
            long timeToRespawn = HardcoreRespawnTimer.getInstance().playerDeathMap.get(commandSenderUUID) + HardcoreRespawnTimer.getInstance().playerRespawnTimer;

            if(System.currentTimeMillis() > timeToRespawn) {
                Player player = Bukkit.getServer().getPlayer(commandSenderUUID);
                commandSender.sendMessage(Component.text("Respawning..."));
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(player.getRespawnLocation());
                return true;
            }
        }

        return false;
    }
}
