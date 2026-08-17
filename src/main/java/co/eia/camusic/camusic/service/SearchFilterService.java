package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.model.Song;

import java.util.List;
import java.util.stream.Collectors;

public class SearchFilterService {

    private final LibraryService libraryService;

    public SearchFilterService(LibraryService libraryService) {
        if (libraryService == null) throw new IllegalArgumentException("LibraryService cannot be null");

        this.libraryService = libraryService;
    }

    public List<Song> search(String text) {
        if (text == null || text.isBlank()) {
            return libraryService.listAll();
        }

        String normalizedQuery = text.toLowerCase().trim();

        return libraryService.listAll().stream()
                .filter(song -> matchesSearch(song, normalizedQuery))
                .collect(Collectors.toList());
    }

    private boolean matchesSearch(Song song, String query) {
        return song.getName().toLowerCase().contains(query) ||
                song.getArtist().toLowerCase().contains(query) ||
                song.getAlbum().toLowerCase().contains(query) ||
                song.getGenre().toLowerCase().contains(query);
    }

    public List<Song> filterByArtist(String artist) {
        if (artist == null || artist.isBlank()) return libraryService.listAll();

        String normalizedArtist = artist.strip();
        return libraryService.listAll().stream()
                .filter(song -> song.getArtist().equalsIgnoreCase(normalizedArtist))
                .collect(Collectors.toList());
    }

    public List<Song> filterByGenre(String genre) {
        if (genre == null || genre.isBlank()) return libraryService.listAll();


        return libraryService.listAll().stream()
                .filter(song -> song.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    public List<Song> filterByAlbum(String album) {
        if (album == null || album.isBlank()) return libraryService.listAll();

        return libraryService.listAll().stream()
                .filter(song -> song.getAlbum().equalsIgnoreCase(album))
                .collect(Collectors.toList());
    }

    public List<Song> filterByRating(double minimumRating) {
        return libraryService.listAll().stream()
                .filter(song -> song.getRating() >= minimumRating)
                .collect(Collectors.toList());
    }

    public List<Song> filterByYear(int year) {
        return libraryService.listAll().stream()
                .filter(song -> song.getReleaseYear() == year)
                .collect(Collectors.toList());
    }

    public List<Song> applyFilters(
            String text,
            String artist,
            String genre,
            String album
    ) {
        List<Song> results = libraryService.listAll();

        if (text != null && !text.isBlank()) {
            results = search(text);
        }

        if (artist != null && !artist.isBlank()) {
            results = results.stream()
                    .filter(song -> song.getArtist().equalsIgnoreCase(artist))
                    .collect(Collectors.toList());
        }

        if (genre != null && !genre.isBlank()) {
            results = results.stream()
                    .filter(song -> song.getGenre().equalsIgnoreCase(genre))
                    .collect(Collectors.toList());
        }

        if (album != null && !album.isBlank()) {
            results = results.stream()
                    .filter(song -> song.getAlbum().equalsIgnoreCase(album))
                    .collect(Collectors.toList());
        }

        return results;
    }

    public List<String> getAvailableArtists() {
        return libraryService.listAll().stream()
                .map(Song::getArtist)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAvailableGenres() {
        return libraryService.listAll().stream()
                .map(Song::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAvailableAlbums() {
        return libraryService.listAll().stream()
                .map(Song::getAlbum)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}