package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.dto.HistoryEntryDto;
import co.eia.camusic.camusic.dto.SongDto;
import co.eia.camusic.camusic.model.HistoryEntry;
import co.eia.camusic.camusic.model.Song;
import co.eia.camusic.camusic.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PersistenceService {

    private static final String LIBRARY_FILE = "library.json";
    private static final String FAVORITES_FILE = "favorites.json";
    private static final String HISTORY_FILE = "history.json";

    private final Path dataDirectory;

    public PersistenceService(Path dataDirectory) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("Data directory cannot be null");
        }
        this.dataDirectory = dataDirectory;
        createDataDirectory();
    }

    public void saveLibrary(List<Song> songs) {
        if (songs == null) throw new IllegalArgumentException("Songs cannot be null");
        List<SongDto> dtos = songs.stream().map(SongDto::fromSong).toList();
        writeFile(LIBRARY_FILE, JsonUtil.toJson(dtos));
    }

    public List<Song> loadLibrary() {
        String json = readFile(LIBRARY_FILE);
        if (json == null || json.isBlank()) return new ArrayList<>();

        Type type = new TypeToken<List<SongDto>>() {}.getType();
        List<SongDto> dtos = JsonUtil.fromJson(json, type);
        if (dtos == null) return new ArrayList<>();

        return dtos.stream().map(SongDto::toSong)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void saveFavorites(Map<String, Set<String>> favorites) {
        if (favorites == null) throw new IllegalArgumentException("Favorites cannot be null");
        writeFile(FAVORITES_FILE, JsonUtil.toJson(favorites));
    }

    public Map<String, Set<String>> loadFavorites() {
        String json = readFile(FAVORITES_FILE);
        if (json == null || json.isBlank()) return new HashMap<>();

        Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
        Map<String, Set<String>> favorites = JsonUtil.fromJson(json, type);
        return favorites == null ? new HashMap<>() : favorites;
    }

    public void saveHistory(Map<String, List<HistoryEntry>> history) {
        if (history == null) throw new IllegalArgumentException("History cannot be null");

        Map<String, List<HistoryEntryDto>> dtos = new HashMap<>();
        history.forEach((userId, entries) -> {
            List<HistoryEntryDto> entryDtos = entries == null
                    ? new ArrayList<>()
                    : entries.stream().map(HistoryEntryDto::fromEntry).toList();
            dtos.put(userId, entryDtos);
        });

        writeFile(HISTORY_FILE, JsonUtil.toJson(dtos));
    }

    public Map<String, List<HistoryEntry>> loadHistory() {
        String json = readFile(HISTORY_FILE);
        if (json == null || json.isBlank()) return new HashMap<>();

        Type type = new TypeToken<Map<String, List<HistoryEntryDto>>>() {}.getType();
        Map<String, List<HistoryEntryDto>> dtos = JsonUtil.fromJson(json, type);
        if (dtos == null) return new HashMap<>();

        Map<String, List<HistoryEntry>> history = new HashMap<>();
        dtos.forEach((userId, entries) -> {
            List<HistoryEntry> converted = entries == null
                    ? new ArrayList<>()
                    : entries.stream().map(HistoryEntryDto::toEntry).toList();
            history.put(userId, new ArrayList<>(converted));
        });

        return history;
    }

    private void createDataDirectory() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create data directory: " + dataDirectory, e);
        }
    }

    private void writeFile(String fileName, String content) {
        Path file = dataDirectory.resolve(fileName);
        try {
            Files.writeString(file, content);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write file: " + file, e);
        }
    }

    private String readFile(String fileName) {
        Path file = dataDirectory.resolve(fileName);
        if (!Files.exists(file)) return null;
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file: " + file, e);
        }
    }
}