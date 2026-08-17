package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class LibraryService {

    private final List<Song> songs = new ArrayList<>();

    public boolean addSong(Song song) {
        if (song == null) throw new IllegalArgumentException("Song cannot be null");

        if (song.getId() == null) throw new IllegalArgumentException("Song ID cannot be null");

        if (exists(song.getId().toString())) return false;

        songs.add(song);
        return true;
    }

    public Song createSong(String name, String artist, String album, int durationSeconds, String genre, int releaseYear, String coverPath) {
        Song song = new Song(name, artist, album, durationSeconds, genre, releaseYear, coverPath);

        addSong(song);
        return song;
    }

    public Song getSongById(String id) {
        if (id == null || id.isBlank()) return null;


        return songs.stream()
                .filter(song -> song.getId() != null)
                .filter(song -> song.getId().toString().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean editSong(String id, String name, String artist, String album, int durationSeconds, String genre, int releaseYear, int rating, String coverPath) {
        Song song = getSongById(id);

        if (song == null) return false;

        song.updateInfo(name, artist, album, durationSeconds, genre, releaseYear, rating, coverPath);

        return true;
    }

    public boolean deleteSong(String id) {
        Song song = getSongById(id);

        if (song == null) return false;


        return songs.remove(song);
    }

    public List<Song> listAll() {
        return List.copyOf(songs);
    }

    public boolean exists(String id) {
        return getSongById(id) != null;
    }

    public int count() {
        return songs.size();
    }

    public void clear() {
        songs.clear();
    }
}