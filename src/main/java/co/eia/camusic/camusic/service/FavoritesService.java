package co.eia.camusic.camusic.service;

import java.util.*;

public class FavoritesService {

    private final Map<String, Set<String>> favoritesByUser;

    public FavoritesService() {
        this.favoritesByUser = new HashMap<>();
    }

    public boolean toggleFavorite(String userId, String songId) {
        validateSong(songId);
        validateUser(userId);

        Set<String> favorites = favoritesByUser.computeIfAbsent(userId, k -> new HashSet<>());

        if(favorites.contains(songId)){
            favorites.remove(songId);
            return false;
        }

        favorites.add(songId);
        return true;
    }

    public boolean isFavorite(String userId, String songId) {
        validateSong(songId);
        validateUser(userId);

        return favoritesByUser.getOrDefault(userId, Collections.emptySet()).contains(songId);
    }

    public Set<String> listFavorites(String userId) {
        validateUser(userId);

        return new HashSet<>(favoritesByUser.getOrDefault(userId, Collections.emptySet()));
    }

    public void removeSongFromFavorites(String songId) {
        validateSong(songId);

        favoritesByUser.values().forEach(favorites -> favorites.remove(songId));
    }

    public Map<String, Set<String>> getAllFavorites() {
        Map<String, Set<String>> favoritesCopy = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry: favoritesByUser.entrySet()) {

            favoritesCopy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        return favoritesCopy;
    }

    public void loadFavorites(Map<String, Set<String>> loadedFavorites) {
        Map<String, Set<String>> loadedCopy = new HashMap<>();

        if(loadedFavorites == null){
            favoritesByUser.clear();
            return;
        }

        loadedFavorites.forEach((userId, songs) -> {
            validateUser(userId);

            Set<String> songsCopy = new HashSet<>();

            if (songs != null) {
                songs.forEach(songId -> {
                    validateSong(songId);
                    songsCopy.add(songId);
                });
            }

            loadedCopy.put(userId, songsCopy);
        });

        favoritesByUser.clear();
        favoritesByUser.putAll(loadedCopy);
    }

    // AUXILIARY METHODS
    private void validateUser(String userId) {
        if(userId == null || userId.isBlank()) throw new IllegalArgumentException("User ID cannot be null");
    }

    private void validateSong(String songId) {
        if(songId == null || songId.isBlank()) throw new IllegalArgumentException("Song ID cannot be null");
    }
}
