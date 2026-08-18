package co.eia.camusic.camusic.bridge;

import co.eia.camusic.camusic.dto.HistoryEntryDto;
import co.eia.camusic.camusic.dto.PlaybackStateDto;
import co.eia.camusic.camusic.dto.SongDto;
import co.eia.camusic.camusic.dto.StatsDto;
import co.eia.camusic.camusic.model.HistoryEntry;
import co.eia.camusic.camusic.model.PlaybackMode;
import co.eia.camusic.camusic.model.Song;
import co.eia.camusic.camusic.service.FavoritesService;
import co.eia.camusic.camusic.service.HistoryService;
import co.eia.camusic.camusic.service.LibraryService;
import co.eia.camusic.camusic.service.PersistenceService;
import co.eia.camusic.camusic.service.PlaybackService;
import co.eia.camusic.camusic.service.StatisticsService;
import co.eia.camusic.camusic.util.JsonUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebAppBridge {

    private final LibraryService libraryService;
    private final FavoritesService favoritesService;
    private final HistoryService historyService;
    private final StatisticsService statisticsService;
    private final PersistenceService persistenceService;
    private final PlaybackService playbackService;

    private final String currentUserId = "user-default";

    public WebAppBridge(
            LibraryService libraryService,
            FavoritesService favoritesService,
            HistoryService historyService,
            PersistenceService persistenceService
    ) {
        if (libraryService == null) {
            throw new IllegalArgumentException(
                    "Library service cannot be null"
            );
        }

        if (favoritesService == null) {
            throw new IllegalArgumentException(
                    "Favorites service cannot be null"
            );
        }

        if (historyService == null) {
            throw new IllegalArgumentException(
                    "History service cannot be null"
            );
        }

        if (persistenceService == null) {
            throw new IllegalArgumentException(
                    "Persistence service cannot be null"
            );
        }

        this.libraryService = libraryService;
        this.favoritesService = favoritesService;
        this.historyService = historyService;
        this.persistenceService = persistenceService;
        this.statisticsService = new StatisticsService(
                libraryService,
                historyService
        );
        this.playbackService = new PlaybackService(
                libraryService
        );
    }

    public String ping() {
        System.out.println(
                "[Java] JavaScript invocó ping()"
        );

        return JsonUtil.toJson(
                Map.of(
                        "success", true,
                        "message",
                        "Conexión exitosa"
                )
        );
    }

    public String getLibrary() {
        List<SongDto> songs = libraryService
                .listAll()
                .stream()
                .map(SongDto::fromSong)
                .toList();

        return JsonUtil.toJson(songs);
    }

    public String addSong(String songJson) {
        try {
            SongDto songDto =
                    JsonUtil.fromJson(
                            songJson,
                            SongDto.class
                    );

            if (songDto == null) {
                return error(
                        "Song data cannot be null"
                );
            }

            Song song = createSongFromDto(songDto);

            boolean added =
                    libraryService.addSong(song);

            if (!added) {
                return error(
                        "A song with the same ID already exists"
                );
            }

            playbackService.loadSongs(
                    libraryService.listAll()
            );

            saveLibrary();

            return success(
                    SongDto.fromSong(song)
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String editSong(
            String id,
            String songJson
    ) {
        try {
            SongDto songDto =
                    JsonUtil.fromJson(
                            songJson,
                            SongDto.class
                    );

            if (songDto == null) {
                return error(
                        "Song data cannot be null"
                );
            }

            Song songData = createSongFromDto(songDto);

            boolean edited = libraryService.editSong(
                    id,
                    songData.getName(),
                    songData.getArtist(),
                    songData.getAlbum(),
                    songData.getDurationSeconds(),
                    songData.getGenre(),
                    songData.getReleaseYear(),
                    songData.getRating(),
                    songData.getCoverPath()
            );

            if (!edited) {
                return error(
                        "Song not found"
                );
            }

            playbackService.loadSongs(
                    libraryService.listAll()
            );

            saveLibrary();

            Song editedSong =
                    libraryService.getSongById(id);

            return success(
                    SongDto.fromSong(editedSong)
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String deleteSong(String id) {
        try {
            Song song =
                    libraryService.getSongById(id);

            if (song == null) {
                return error(
                        "Song not found"
                );
            }

            boolean deleted =
                    libraryService.deleteSong(id);

            if (!deleted) {
                return error(
                        "Song could not be deleted"
                );
            }

            favoritesService.removeSongFromFavorites(id);

            playbackService.loadSongs(
                    libraryService.listAll()
            );

            saveAll();

            return success(
                    Map.of(
                            "deletedId",
                            id
                    )
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String nextSong() {
        try {
            Song nextSong =
                    playbackService.next();

            if (nextSong == null) {
                return error(
                        "There is no next song"
                );
            }

            historyService.register(
                    currentUserId,
                    nextSong.getId().toString()
            );

            saveHistory();

            return success(
                    createPlaybackState()
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String previousSong() {
        try {
            if (!playbackService.canGoPrevious()) {
                return error(
                        "Previous song is not available in FIFO mode"
                );
            }

            Song previousSong =
                    playbackService.previous();

            if (previousSong == null) {
                return error(
                        "There is no previous song"
                );
            }

            historyService.register(
                    currentUserId,
                    previousSong.getId().toString()
            );

            saveHistory();

            return success(
                    createPlaybackState()
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String switchMode(String modeName) {
        try {
            if (modeName == null || modeName.isBlank()) {
                return error(
                        "Playback mode cannot be null or blank"
                );
            }

            PlaybackMode mode =
                    PlaybackMode.valueOf(
                            modeName.trim().toUpperCase()
                    );

            playbackService.switchMode(mode);

            return success(
                    createPlaybackState()
            );
        } catch (IllegalArgumentException exception) {
            return error(
                    "Invalid playback mode: " + modeName
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String getStats() {
        try {
            List<StatsDto.ArtistCountDto> topArtists =
                    statisticsService.getTopArtists(5);

            StatsDto stats = new StatsDto(
                    statisticsService.getTotalSongs(),
                    statisticsService.getAverageRating(),
                    topArtists,
                    countTotalFavorites()
            );

            return JsonUtil.toJson(stats);
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String getFavorites() {
        try {
            return JsonUtil.toJson(
                    favoritesService.listFavorites(
                            currentUserId
                    )
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String toggleFavorite(String songId) {
        try {
            boolean isFavorite =
                    favoritesService.toggleFavorite(
                            currentUserId,
                            songId
                    );

            saveFavorites();

            return success(
                    Map.of(
                            "songId",
                            songId,
                            "favorite",
                            isFavorite
                    )
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String getHistory() {
        try {
            List<HistoryEntryDto> history =
                    historyService
                            .listHistory(currentUserId)
                            .stream()
                            .map(HistoryEntryDto::fromEntry)
                            .toList();

            return JsonUtil.toJson(history);
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String clearHistory() {
        try {
            historyService.clearHistory(
                    currentUserId
            );

            saveHistory();

            return success(
                    Map.of(
                            "userId",
                            currentUserId
                    )
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String getPlaybackState() {
        try {
            return JsonUtil.toJson(
                    createPlaybackState()
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String play() {
        try {
            playbackService.play();

            Song currentSong =
                    playbackService.current();

            if (currentSong != null) {
                historyService.register(
                        currentUserId,
                        currentSong.getId().toString()
                );

                saveHistory();
            }

            return success(
                    createPlaybackState()
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    public String pause() {
        try {
            playbackService.pause();

            return success(
                    createPlaybackState()
            );
        } catch (RuntimeException exception) {
            return error(exception.getMessage());
        }
    }

    private PlaybackStateDto createPlaybackState() {
        Song currentSong =
                playbackService.current();

        return new PlaybackStateDto(
                SongDto.fromSong(currentSong),
                playbackService.getCurrentMode(),
                playbackService.isPlaying(),
                0.0,
                playbackService.canGoPrevious()
        );
    }

    private Song createSongFromDto(SongDto dto) {
        if (dto.getId() == null || dto.getId().isBlank()) {
            return new Song(
                    dto.getName(),
                    dto.getArtist(),
                    dto.getAlbum(),
                    dto.getDurationSeconds(),
                    dto.getGenre(),
                    dto.getReleaseYear(),
                    dto.getCoverPath()
            );
        }

        return dto.toSong();
    }

    private int countTotalFavorites() {
        return favoritesService
                .getAllFavorites()
                .values()
                .stream()
                .mapToInt(Set::size)
                .sum();
    }

    private void saveLibrary() {
        persistenceService.saveLibrary(
                libraryService.listAll()
        );
    }

    private void saveFavorites() {
        persistenceService.saveFavorites(
                favoritesService.getAllFavorites()
        );
    }

    private void saveHistory() {
        persistenceService.saveHistory(
                historyService.getAllHistory()
        );
    }

    private void saveAll() {
        saveLibrary();
        saveFavorites();
        saveHistory();
    }

    private String success(Object data) {
        return JsonUtil.toJson(
                Map.of(
                        "success",
                        true,
                        "data",
                        data
                )
        );
    }

    private String error(String message) {
        return JsonUtil.toJson(
                Map.of(
                        "success",
                        false,
                        "error",
                        message == null
                                ? "Unknown error"
                                : message
                )
        );
    }
}
