package com.witchica.respawninhibition;

import com.witchica.respawninhibition.commands.RespawnCommand;
import com.witchica.respawninhibition.commands.RespawnInhibitionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnInhibition extends JavaPlugin implements Listener {
    private static RespawnInhibition _instance;

    private FileConfiguration config;
    private YamlConfiguration _playerDataFile;
    private File _playerDataStore;
    private Map<String, PlayerRespawnData> _playerRespawnData;
    
    private boolean _enabled = true;
    private long _respawnTimer = 60 * 60 * 24;

    public static RespawnInhibition getInstance() {
        return _instance;
    }

    public boolean getEnabledState() {
        return _enabled;
    }

    public long getRespawnTimer() {
        return _respawnTimer;
    }

    public Map<String, PlayerRespawnData> getPlayerRespawns() {
        return _playerRespawnData;
    }

    private void loadPlayerData() {
        this._playerDataStore = new File(getDataFolder(), "players.yml");
        if(!_playerDataStore.exists()) {
            try {
                _playerDataStore.getParentFile().mkdirs();
                _playerDataStore.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        this._playerDataFile = YamlConfiguration.loadConfiguration(_playerDataStore);

        if(this._playerDataFile.contains("Players")) {
            this._playerRespawnData = (Map<String, PlayerRespawnData>) this._playerDataFile.getMapList("Players");
        } else {
            this._playerRespawnData = new HashMap<>();
        }
    }

    private void savePlayerData() {
        this._playerDataFile.set("Players", this._playerRespawnData);

        try {
            this._playerDataFile.save(_playerDataStore);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void setPlayerDeath(UUID uniqueId, long respawnTime) {
        PlayerRespawnData data = new PlayerRespawnData(System.currentTimeMillis(), respawnTime);
        this._playerRespawnData.put(uniqueId.toString(), data);
        savePlayerData();
    }

    public boolean isPlayerOnCooldown(UUID uniqueId) {
        String playerId = uniqueId.toString();

        return _playerRespawnData.containsKey(playerId);
    }

    public boolean canPlayerRespawn(UUID uniqueId) {
        String playerId = uniqueId.toString();

        if(_playerRespawnData.containsKey(playerId)) {
            return _playerRespawnData.get(playerId).canRespawn();
        }

        return true;
    }

    public boolean clearPlayerEntry(UUID uniqueId) {
        if(!_playerRespawnData.containsKey(uniqueId.toString())) {
            return false;
        }

        _playerRespawnData.remove(uniqueId.toString());
        savePlayerData();
        return true;
    }

    public void respawnPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(player.getRespawnLocation() == null ? player.getWorld().getSpawnLocation() : player.getRespawnLocation());
        player.sendMessage(Component.text("Your respawn cooldown has ended!").color(NamedTextColor.GOLD));
    }

    public boolean setPlayerRespawnTimer(UUID uniqueId, long newTime, boolean updateStartTime) {
        String playerId = uniqueId.toString();

        if(_playerRespawnData.containsKey(playerId)) {
            PlayerRespawnData newData = _playerRespawnData.get(playerId);
            newData.respawnTimer = newTime;

            if(updateStartTime) {
                newData.deathTime = System.currentTimeMillis();
            }

            _playerRespawnData.remove(playerId);
            _playerRespawnData.put(playerId, newData);
            savePlayerData();
            return true;
        }

        return false;
    }

    public long getRemainingTime(UUID uniqueId) {
        if(!_playerRespawnData.containsKey(uniqueId.toString())) {
            return -1;
        } else {
            PlayerRespawnData data = _playerRespawnData.get(uniqueId.toString());

            return ((data.deathTime + (data.respawnTimer*1000)) - System.currentTimeMillis());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        _instance = this;

        this.config = this.getConfig();
        this.config.addDefault("defaultPlayerRespawnTimer", 60 * 60 * 24);
        this.config.addDefault("enabled", true);
        this.config.options().copyDefaults(true);

        this._enabled = this.config.getBoolean("enabled");
        this._respawnTimer = this.config.getLong("defaultPlayerRespawnTimer");

        loadPlayerData();

        Bukkit.getPluginManager().registerEvents(this, this);
        PluginCommand respawni = this.getCommand("respawni");
        respawni.setExecutor(new RespawnInhibitionCommand());
        respawni.setTabCompleter(RespawnInhibitionCommand::tabCompleter);

        getCommand("respawn").setExecutor(new RespawnCommand());
    }

    public void setEnabledState(boolean state) {
        this.config.set("enabled", state);
        this._enabled = state;
        saveConfig();
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if(getEnabledState()) {
            setPlayerDeath(event.getPlayer().getUniqueId(), getRespawnTimer());
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if(!canPlayerRespawn(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("You cannot respawn").color(NamedTextColor.GOLD));
        }
    }
}
