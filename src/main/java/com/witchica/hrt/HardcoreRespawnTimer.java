package com.witchica.hrt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HardcoreRespawnTimer extends JavaPlugin implements Listener {
    private static HardcoreRespawnTimer _instance;
    public Map<UUID, Long> playerDeathMap;
    public List<UUID> playersThatCanRespawn;
    public long playerRespawnTimer = (1000 * 60);


    public static HardcoreRespawnTimer getInstance() {
        return _instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if(!Bukkit.getServer().isHardcore()) {
            System.err.println("Hardcore Respawn Timer only works within hardcore worlds");
        } else {
            Bukkit.getPluginManager().registerEvents(this, this);
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent playerDeathEvent) {
        Player player = playerDeathEvent.getPlayer();
        UUID playerId = player.getUniqueId();
        playerDeathMap.put(playerId, System.currentTimeMillis());

        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(Component.text("You have died! You can respawn in 24h"));
    }

    public void resetPlayer(UUID uuid) {
        Bukkit.getServer().getPlayer(uuid);
    }
}
