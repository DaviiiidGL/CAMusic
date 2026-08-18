package co.eia.camusic.camusic.dto;

import co.eia.camusic.camusic.model.HistoryEntry;

import java.time.Instant;

public class HistoryEntryDto {

    private String userId;
    private String songId;
    private Instant timestamp;

    public HistoryEntryDto() {}

    public HistoryEntryDto(String userId, String songId, Instant timestamp) {
        this.userId = userId;
        this.songId = songId;
        this.timestamp = timestamp;
    }

    public static HistoryEntryDto fromEntry(HistoryEntry entry) {
        if (entry == null) return null;
        return new HistoryEntryDto(
                entry.getUserId(),
                entry.getSongId(),
                entry.getTimestamp()
        );
    }

    public HistoryEntry toEntry() {
        return new HistoryEntry(userId, songId, timestamp);
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSongId() { return songId; }
    public void setSongId(String songId) { this.songId = songId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}