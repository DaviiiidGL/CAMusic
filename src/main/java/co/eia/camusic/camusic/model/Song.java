package co.eia.camusic.camusic.model;

import java.time.Year;
import java.util.UUID;

public final class Song {

    private final UUID id;
    private String name;
    private String artist;
    private String album;
    private int durationSeconds;
    private String genre;
    private int releaseYear;
    private int rating;
    private String coverPath;

    // Constructor used when creating a new song
    public Song(
            String name,
            String artist,
            String album,
            int durationSeconds,
            String genre,
            int releaseYear,
            int rating,
            String coverPath
    ) {
        this(UUID.randomUUID(), name, artist, album, durationSeconds,
                genre, releaseYear, rating, coverPath);
    }

    // Constructor used when loading a song
    public Song(
            UUID id,
            String name,
            String artist,
            String album,
            int durationSeconds,
            String genre,
            int releaseYear,
            int rating,
            String coverPath
    ) {
        validateId(id);
        this.id = id;

        setName(name);
        setArtist(artist);
        setAlbum(album);
        setDurationSeconds(durationSeconds);
        setGenre(genre);
        setReleaseYear(releaseYear);
        setRating(rating);
        setCoverPath(coverPath);
    }

    // Getters
    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getArtist() {return artist;}
    public String getAlbum() {return album;}
    public int getDurationSeconds() {return durationSeconds;}
    public String getGenre() {return genre;}
    public int getReleaseYear() {return releaseYear;}
    public int getRating() {return rating;}
    public String getCoverPath() {return coverPath;}

    // Converts duration in seconds to mm:ss format
    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Setters
    public void setName(String name) {this.name = normalizeText(name, "name");}
    public void setArtist(String artist) {this.artist = normalizeText(artist, "artist");}
    public void setAlbum(String album) {this.album = normalizeText(album, "album");}

    public void setDurationSeconds(int durationSeconds) {
        validateDuration(durationSeconds);
        this.durationSeconds = durationSeconds;
    }
    public void setGenre(String genre) {this.genre = normalizeText(genre, "genre");}

    public void setReleaseYear(int releaseYear) {
        validateReleaseYear(releaseYear);
        this.releaseYear = releaseYear;
    }

    public void setRating(int rating) {
        validateRating(rating);
        this.rating = rating;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = normalizeCoverPath(coverPath);
    }

    // Editing all the info
    public void updateInfo(String name, String artist, String album, int durationSeconds, String genre, int releaseYear, int rating, String coverPath) {
        setName(name);
        setArtist(artist);
        setAlbum(album);
        setDurationSeconds(durationSeconds);
        setGenre(genre);
        setReleaseYear(releaseYear);
        setRating(rating);
        setCoverPath(coverPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Song other = (Song) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + getFormattedDuration() + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                ", rating=" + rating +
                '}';
    }

    // Validation methods
    private void validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Song id cannot be null");
        }
    }

    private void validateText(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "The " + fieldName + " of the song cannot be null or blank"
            );
        }

        if (text.strip().length() > 100) {
            throw new IllegalArgumentException("The " + fieldName + " cannot be longer than 100 characters. ");
        }
    }

    private void validateDuration(int durationSeconds) {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Song duration must be greater than zero seconds");
        }
    }

    private void validateReleaseYear(int releaseYear) {
        int currentYear = Year.now().getValue();

        if (releaseYear <= 0 || releaseYear > currentYear) {
            throw new IllegalArgumentException("Release year must be between 1 and " + currentYear);
        }
    }

    private void validateRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 0 and 5"
            );
        }
    }

    // Normalization methods
    private String normalizeText(String value, String fieldName) {
        validateText(value, fieldName);
        return value.strip();
    }

    private String normalizeCoverPath(String coverPath) {
        if (coverPath == null || coverPath.isBlank()) {
            return null;
        }

        String normalized = coverPath.strip();

        if (normalized.length() > 255) {
            throw new IllegalArgumentException(
                    "Cover path cannot be longer than 255 characters"
            );
        }

        return normalized;
    }
}