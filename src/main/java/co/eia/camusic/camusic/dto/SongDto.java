package co.eia.camusic.camusic.dto;

import co.eia.camusic.camusic.model.Song;

import java.util.UUID;

public class SongDto {
    private String id;
    private String name;
    private String artist;
    private String album;
    private int durationSeconds;
    private String formattedDuration;
    private String genre;
    private int releaseYear;
    private int rating;
    private String coverPath;

    public SongDto() {
    }

    public static SongDto fromSong(Song song) {
        if (song == null) return null;

        SongDto dto = new SongDto();
        dto.setId(song.getId().toString());
        dto.setName(song.getName());
        dto.setArtist(song.getArtist());
        dto.setAlbum(song.getAlbum());
        dto.setDurationSeconds(song.getDurationSeconds());
        dto.setFormattedDuration(song.getFormattedDuration());
        dto.setGenre(song.getGenre());
        dto.setReleaseYear(song.getReleaseYear());
        dto.setRating(song.getRating());
        dto.setCoverPath(song.getCoverPath());

        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getFormattedDuration() {
        return formattedDuration;
    }

    public void setFormattedDuration(String formattedDuration) {
        this.formattedDuration = formattedDuration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public Song toSong() {
        return new Song(
                java.util.UUID.fromString(this.id),
                this.name,
                this.artist,
                this.album,
                this.durationSeconds,
                this.genre,
                this.releaseYear,
                this.rating,
                this.coverPath
        );
    }
}
