package com.witchica.respawninhibition;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerRespawnData {
    public long deathTime;
    public long respawnTimer;

    public PlayerRespawnData(long deathTime, long respawnTimer) {
        this.deathTime = deathTime;
        this.respawnTimer = respawnTimer;
    }

    public boolean canRespawn() {
        return System.currentTimeMillis() > (deathTime + (respawnTimer * 1000));
    }
}
