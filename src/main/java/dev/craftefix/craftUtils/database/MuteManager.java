package dev.craftefix.craftUtils.database;

import dev.craftefix.craftUtils.Main;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MuteManager {
    private final DatabaseManager databaseManager;
    
    public MuteManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public static class MuteData {
        public final UUID playerUuid;
        public final String playerName;
        public final String mutedBy;
        public final String reason;
        public final long muteTime;
        public final Long unmuteTime;
        public final boolean active;
        
        public MuteData(UUID playerUuid, String playerName, String mutedBy, String reason, long muteTime, Long unmuteTime, boolean active) {
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.mutedBy = mutedBy;
            this.reason = reason;
            this.muteTime = muteTime;
            this.unmuteTime = unmuteTime;
            this.active = active;
        }
        
        public boolean isPermanent() {
            return unmuteTime == null;
        }
        
        public boolean isExpired() {
            return !isPermanent() && System.currentTimeMillis() >= unmuteTime;
        }
    }
    
    public void mutePlayer(UUID playerUuid, String playerName, String mutedBy, String reason, Long duration) {
        String sql = "INSERT OR REPLACE INTO mutes (player_uuid, player_name, muted_by, reason, mute_time, unmute_time, active) VALUES (?, ?, ?, ?, ?, ?, 1)";
        if (databaseManager.getDatabaseType() == DatabaseManager.DatabaseType.MARIADB) {
            sql = "INSERT INTO mutes (player_uuid, player_name, muted_by, reason, mute_time, unmute_time, active) VALUES (?, ?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE player_name=VALUES(player_name), muted_by=VALUES(muted_by), reason=VALUES(reason), mute_time=VALUES(mute_time), unmute_time=VALUES(unmute_time), active=1";
        }
        
        try (Connection conn = databaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, playerName);
            stmt.setString(3, mutedBy);
            stmt.setString(4, reason);
            stmt.setLong(5, System.currentTimeMillis());
            if (duration != null) {
                stmt.setLong(6, System.currentTimeMillis() + (duration * 1000));
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error muting player: " + e.getMessage());
        }
    }
    
    public void unmutePlayer(UUID playerUuid) {
        String sql = "UPDATE mutes SET active = 0 WHERE player_uuid = ?";
        
        try (Connection conn = databaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error unmuting player: " + e.getMessage());
        }
    }
    
    public MuteData getMuteData(UUID playerUuid) {
        String sql = "SELECT player_uuid, player_name, muted_by, reason, mute_time, unmute_time, active FROM mutes WHERE player_uuid = ? AND active = 1";
        
        try (Connection conn = databaseManager.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    String playerName = rs.getString("player_name");
                    String mutedBy = rs.getString("muted_by");
                    String reason = rs.getString("reason");
                    long muteTime = rs.getLong("mute_time");
                    long unmuteTimeLong = rs.getLong("unmute_time");
                    Long unmuteTime = rs.wasNull() ? null : unmuteTimeLong;
                    boolean active = rs.getBoolean("active");
                    
                    return new MuteData(uuid, playerName, mutedBy, reason, muteTime, unmuteTime, active);
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error getting mute data: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean isPlayerMuted(UUID playerUuid) {
        MuteData muteData = getMuteData(playerUuid);
        if (muteData == null) return false;
        
        if (muteData.isExpired()) {
            unmutePlayer(playerUuid);
            return false;
        }
        
        return muteData.active;
    }
    
    public MuteData getMuteDataByName(String playerName) {
        String sql = "SELECT player_uuid, player_name, muted_by, reason, mute_time, unmute_time, active FROM mutes WHERE LOWER(player_name) = LOWER(?) AND active = 1";
        
        try (Connection conn = databaseManager.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    String name = rs.getString("player_name");
                    String mutedBy = rs.getString("muted_by");
                    String reason = rs.getString("reason");
                    long muteTime = rs.getLong("mute_time");
                    long unmuteTimeLong = rs.getLong("unmute_time");
                    Long unmuteTime = rs.wasNull() ? null : unmuteTimeLong;
                    boolean active = rs.getBoolean("active");
                    
                    return new MuteData(uuid, name, mutedBy, reason, muteTime, unmuteTime, active);
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error getting mute data by name: " + e.getMessage());
        }
        
        return null;
    }
    
    public void unmutePlayerByName(String playerName) {
        String sql = "UPDATE mutes SET active = 0 WHERE LOWER(player_name) = LOWER(?)";
        
        try (Connection conn = databaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error unmuting player by name: " + e.getMessage());
        }
    }
}