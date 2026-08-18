package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.model.HistoryEntry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryService {

    private final Map<String, List<HistoryEntry>> historyByUser;

    public HistoryService() {
        this.historyByUser = new HashMap<>();
    }

    public void register(String userId, String songId) {
        validateUser(userId);
        validateSong(songId);

        HistoryEntry entry = new HistoryEntry(userId, songId, Instant.now());

        historyByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(entry);
    }

    public List<HistoryEntry> listHistory(String userId) {
        validateUser(userId);

        return historyByUser.getOrDefault(userId, List.of()).stream().sorted(Comparator.comparing(HistoryEntry::getTimestamp).reversed()).toList();
    }

    public void clearHistory(String userId) {
        validateUser(userId);

        historyByUser.remove(userId);
    }

    public Map<String, List<HistoryEntry>> getAllHistory() {
        Map<String, List<HistoryEntry>> historyCopy = new HashMap<>();

        for(Map.Entry<String, List<HistoryEntry>> entry: historyByUser.entrySet()){
            historyCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return historyCopy;
    }

    public void loadHistory(Map<String, List<HistoryEntry>> loadedHistory) {
        Map<String, List<HistoryEntry>> loadedCopy = new HashMap<>();

        if(loadedHistory == null){
            historyByUser.clear();
            return;
        }

        loadedHistory.forEach((userId, entries) -> {
            validateUser(userId);

            List<HistoryEntry> entriesCopy = entries == null ? new ArrayList<>() : new ArrayList<>(entries);

            entriesCopy.forEach(entry -> {
                if (entry == null) throw new IllegalArgumentException("History entry cannot be null");

                validateUser(entry.getUserId());
                validateSong(entry.getSongId());

                if(!userId.equals(entry.getUserId())) throw new IllegalArgumentException("History entry does not match the map key");

                if(entry.getTimestamp() == null) throw new IllegalArgumentException("Timestamp cannot be null");
            });

            loadedCopy.put(userId, entriesCopy);
        });

        historyByUser.clear();
        historyByUser.putAll(loadedCopy);
    }

    // AUXILIARY METHODS

    private void validateUser(String userId) {
        if(userId == null || userId.isBlank()) throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    private void validateSong(String songId) {
        if(songId == null || songId.isBlank()) throw new IllegalArgumentException("Song ID cannot be null or blank");
    }
}