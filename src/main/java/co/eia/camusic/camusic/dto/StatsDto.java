package co.eia.camusic.camusic.dto;

import java.util.List;

public class StatsDto {
    private int totalSongs;
    private double averageRating;
    private List<ArtistCountDto> topArtists;
    private int totalFavorites;

    public static class ArtistCountDto {
        private String artist;
        private int count;

        public ArtistCountDto(String artist, int count) {
            this.artist = artist;
            this.count = count;
        }

        public String getArtist() {return artist;}
        public int getCount() {return count;}
    }

    public StatsDto(int totalSongs, double averageRating, List<ArtistCountDto> topArtists, int totalFavorites) {
        this.totalSongs = totalSongs;
        this.averageRating = averageRating;
        this.topArtists = topArtists;
        this.totalFavorites = totalFavorites;
    }

    public int getTotalSongs() {return totalSongs;}
    public void setTotalSongs(int totalSongs) {this.totalSongs = totalSongs;}

    public double getAverageRating() {return averageRating;}
    public void setAverageRating(double averageRating) {this.averageRating = averageRating;}

    public List<ArtistCountDto> getTopArtists() {return topArtists;}
    public void setTopArtists(List<ArtistCountDto> topArtists) {this.topArtists = topArtists;}

    public int getTotalFavorites() {return totalFavorites;}
    public void setTotalFavorites(int totalFavorites) {this.totalFavorites = totalFavorites;}
}