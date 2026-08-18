package co.eia.camusic.camusic.model;

import java.time.Instant;

public class HistoryEntry {
    private final String userId;
    private final String songId;
    private final Instant timestamp;

    public HistoryEntry(String userId, String songId, Instant timestamp) {
        validateUser(userId);
        validateSong(songId);
        if(timestamp == null) throw new IllegalArgumentException("Timestamp cannot be null");

        this.userId = userId;
        this.songId = songId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getSongId() {
        return songId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    // AUXILIARY METHODS
    private void validateUser(String userId) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    private void validateSong(String songId) {
        if (songId == null || songId.isBlank()) throw new IllegalArgumentException("Song ID cannot be null or blank");
    }
}
