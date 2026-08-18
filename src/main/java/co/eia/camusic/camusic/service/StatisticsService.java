package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.dto.StatsDto;
import co.eia.camusic.camusic.model.HistoryEntry;
import co.eia.camusic.camusic.model.Song;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatisticsService {

    private final LibraryService libraryService;
    private final HistoryService historyService;

    public StatisticsService(
            LibraryService libraryService,
            HistoryService historyService
    ) {
        if (libraryService == null)
            throw new IllegalArgumentException("Library service cannot be null");

        if (historyService == null) throw new IllegalArgumentException("History service cannot be null");

        this.libraryService = libraryService;
        this.historyService = historyService;
    }

    public int getTotalSongs() {return libraryService.count();}

    public double getAverageRating() {
        return libraryService
                .listAll()
                .stream()
                .mapToInt(Song::getRating)
                .filter(rating -> rating > 0)
                .average()
                .orElse(0.0);
    }

    public List<StatsDto.ArtistCountDto> getTopArtists(int n) {
        validateLimit(n);

        return libraryService
                .listAll().stream()
                .collect(
                        Collectors.groupingBy(
                                Song::getArtist,
                                Collectors.counting()
                        )
                )
                .entrySet().stream().sorted(
                        Comparator
                                .comparing(Map.Entry<String, Long>::getValue, Comparator.reverseOrder())
                                .thenComparing(Map.Entry<String, Long>::getKey)
                )
                .limit(n)
                .map(entry -> new StatsDto.ArtistCountDto(entry.getKey(), entry.getValue().intValue())).toList();
    }

    public int getTotalPlays(String userId) {
        validateUser(userId);

        return historyService.listHistory(userId).size();
    }

    public List<Song> getMostPlayedSongs(String userId, int n) {
        validateUser(userId);
        validateLimit(n);

        return historyService.listHistory(userId).stream()
                .collect(
                        Collectors.groupingBy(HistoryEntry::getSongId, Collectors.counting())
                ).entrySet().stream().sorted(
                        Comparator
                                .comparing(Map.Entry<String, Long>::getValue, Comparator.reverseOrder())
                                .thenComparing(Map.Entry<String, Long>::getKey))
                .map(entry -> libraryService.getSongById(entry.getKey()))
                .filter(Objects::nonNull).limit(n).toList();
    }

    // AUXILIARY METHODS

    private void validateLimit(int n) {
        if (n <= 0) throw new IllegalArgumentException("Limit must be a positive integer");
    }

    private void validateUser(String userId) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("User ID cannot be null or blank");

    }
}