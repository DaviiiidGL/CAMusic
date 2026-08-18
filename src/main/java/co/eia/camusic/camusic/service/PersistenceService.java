package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.dto.HistoryEntryDto;
import co.eia.camusic.camusic.dto.SongDto;
import co.eia.camusic.camusic.model.HistoryEntry;
import co.eia.camusic.camusic.model.Song;
import co.eia.camusic.camusic.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

public class PersistenceService {

    private static final String LIBRARY_FILE = "library.json";
    private static final String FAVORITES_FILE = "favorites.json";
    private static final String HISTORY_FILE = "history.json";

    private final Path dataDirectory;

    public PersistenceService(Path dataDirectory) {
        if (dataDirectory == null) throw new IllegalArgumentException("Data directory cannot be null");

        this.dataDirectory = dataDirectory;
        createDataDirectory();
    }

    public void saveLibrary(List<Song> songs) {
        validateSongs(songs);

        List<SongDto> dtos = songs.stream().map(SongDto::fromSong).toList();

        writeFile(LIBRARY_FILE, JsonUtil.toJson(dtos));
    }

    public List<Song> loadLibrary() {
        String json = readFile(LIBRARY_FILE);

        if (json == null || json.isBlank()) return new ArrayList<>();

        Type type = new TypeToken<List<SongDto>>() {}.getType();

        List<SongDto> dtos = JsonUtil.fromJson(json, type);

        if (dtos == null) return new ArrayList<>();

        if (dtos.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Library cannot contain null songs");

        return dtos.stream().map(SongDto::toSong).collect(Collectors.toCollection(ArrayList::new));
    }

    public void saveFavorites(Map<String, Set<String>> favorites) {
        validateFavorites(favorites);

        writeFile(FAVORITES_FILE, JsonUtil.toJson(favorites));
    }

    public Map<String, Set<String>> loadFavorites() {
        String json = readFile(FAVORITES_FILE);

        if (json == null || json.isBlank()) return new HashMap<>();

        Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();

        Map<String, Set<String>> favorites = JsonUtil.fromJson(json, type);

        if (favorites == null) return new HashMap<>();

        validateFavorites(favorites);

        Map<String, Set<String>> favoritesCopy = new HashMap<>();

        favorites.forEach((userId, songIds) -> {favoritesCopy.put(userId, new HashSet<>(songIds));});

        return favoritesCopy;
    }

    public void saveHistory(Map<String, List<HistoryEntry>> history) {
        validateHistory(history);

        Map<String, List<HistoryEntryDto>> dtos = new HashMap<>();

        history.forEach((userId, entries) -> {List<HistoryEntryDto> entryDtos =
                    entries.stream().map(HistoryEntryDto::fromEntry).toList();
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
            validateUser(userId);

            if (entries == null) throw new IllegalArgumentException("History entries cannot be null");

            List<HistoryEntry> converted = entries.stream().map(entry -> {
                        if (entry == null) throw new IllegalArgumentException("History entry cannot be null");

                        return entry.toEntry();
                    }).toList();

            converted.forEach(entry -> {validateHistoryEntry(userId, entry);});

            history.put(userId, new ArrayList<>(converted));
        });

        return history;
    }

    private void validateSongs(List<Song> songs) {
        if (songs == null) throw new IllegalArgumentException("Songs cannot be null");

        if (songs.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Songs cannot contain null elements");
    }

    private void validateFavorites(Map<String, Set<String>> favorites) {
        if (favorites == null) throw new IllegalArgumentException("Favorites cannot be null");

        favorites.forEach((userId, songIds) -> {validateUser(userId);
            if (songIds == null) throw new IllegalArgumentException("Favorite song IDs cannot be null");

            songIds.forEach(this::validateSongId);
        });
    }

    private void validateHistory(Map<String, List<HistoryEntry>> history) {
        if (history == null) throw new IllegalArgumentException("History cannot be null");

        history.forEach((userId, entries) -> {validateUser(userId);
            if (entries == null) throw new IllegalArgumentException("History entries cannot be null");

            entries.forEach(entry -> validateHistoryEntry(userId, entry));
        });
    }

    private void validateHistoryEntry(String mapUserId, HistoryEntry entry) {
        if (entry == null) throw new IllegalArgumentException("History entry cannot be null");
        validateUser(entry.getUserId());
        validateSongId(entry.getSongId());

        if (!mapUserId.equals(entry.getUserId())) throw new IllegalArgumentException("History entry user does not match map key");


        if (entry.getTimestamp() == null) throw new IllegalArgumentException("History timestamp cannot be null");
    }

    private void validateUser(String userId) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    private void validateSongId(String songId) {
        if (songId == null || songId.isBlank()) throw new IllegalArgumentException("Song ID cannot be null or blank");
    }

    private void createDataDirectory() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create data directory: " + dataDirectory, exception);
        }
    }

    private void writeFile(String fileName, String content
    ) {
        Path target = dataDirectory.resolve(fileName);

        Path temporary = null;

        try {
            temporary = Files.createTempFile(dataDirectory, fileName, ".tmp");

            Files.writeString(temporary, content);

            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }

            throw new IllegalStateException("Could not write file: " + target, exception);
        }
    }

    private String readFile(String fileName) {
        Path file = dataDirectory.resolve(fileName);

        if (!Files.exists(file)) return null;

        try {
            return Files.readString(file);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read file: " + file, exception);
        }
    }
}