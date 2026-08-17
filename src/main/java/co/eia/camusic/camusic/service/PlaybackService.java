package co.eia.camusic.camusic.service;

import co.eia.camusic.camusic.ds.BinarySearchTree;
import co.eia.camusic.camusic.ds.CircularDoublyLinkedList;
import co.eia.camusic.camusic.ds.SimpleQueue;
import co.eia.camusic.camusic.ds.interfaces.PlaylistStructure;
import co.eia.camusic.camusic.model.PlaybackMode;
import co.eia.camusic.camusic.model.Song;

import java.util.List;

public class PlaybackService {

    private final LibraryService libraryService;

    private PlaylistStructure<Song> activeStructure;
    private PlaybackMode currentMode;
    private Song currentSong;
    private boolean playing;

    public PlaybackService() {
        this(new LibraryService());
    }

    public PlaybackService(LibraryService libraryService) {
        if (libraryService == null) throw new IllegalArgumentException("Library service cannot be null");

        this.libraryService = libraryService;
        this.currentMode = PlaybackMode.RANDOM_CIRCULAR;
        this.activeStructure = new CircularDoublyLinkedList();
        this.playing = false;

        loadSongs();
    }

    public void switchMode(PlaybackMode mode) {
        if (mode == null) throw new IllegalArgumentException("Playback mode cannot be null");

        this.currentMode = mode;
        this.currentSong = null;
        this.playing = false;

        this.activeStructure = createStructure(mode);
        loadSongs();
    }

    private PlaylistStructure<Song> createStructure(PlaybackMode mode) {
        return switch (mode) {
            case RANDOM_CIRCULAR -> new CircularDoublyLinkedList();
            case FIFO_QUEUE -> new SimpleQueue();
            case ALPHABETICAL_BST -> new BinarySearchTree();
        };
    }

    private void loadSongs() {
        if (activeStructure == null) return;

        activeStructure.clear();

        List<Song> allSongs = libraryService.listAll();

        for (Song song : allSongs) activeStructure.add(song);
    }

    public Song current() {
        return currentSong;
    }

    public void play() {
        if (activeStructure == null) return;


        if (currentSong == null) {
            Song firstSong = activeStructure.current();

            if (firstSong == null) firstSong = activeStructure.next();

            this.currentSong = firstSong;
        }

        if (currentSong != null) this.playing = true;
    }

    public void pause() {
        this.playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public Song next() {
        if (activeStructure == null) return null;

        Song nextSong = activeStructure.next();

        if (nextSong != null) {
            this.currentSong = nextSong;
            this.playing = true;
        }

        return nextSong;
    }

    public Song previous() {
        if (currentMode == PlaybackMode.FIFO_QUEUE) return null;


        if (activeStructure == null) return null;

        Song prevSong = activeStructure.previous();

        if (prevSong != null) {
            this.currentSong = prevSong;
            this.playing = true;
        }

        return prevSong;
    }

    public boolean canGoPrevious() {
        return currentMode != PlaybackMode.FIFO_QUEUE;
    }

    public void loadSongs(List<Song> songs) {
        if (songs == null) throw new IllegalArgumentException("Songs cannot be null");

        if (activeStructure == null) return;

        activeStructure.clear();

        for (Song song : songs) activeStructure.add(song);

        this.currentSong = null;
        this.playing = false;
    }

    public void reset() {
        if (activeStructure != null) activeStructure.clear();

        currentSong = null;
        playing = false;
    }

    public PlaybackMode getCurrentMode() {
        return currentMode;
    }

    public boolean hasCurrentSong() {
        return currentSong != null;
    }

    public List<Song> getActiveSongs() {
        if (activeStructure == null) return List.of();

        return activeStructure.toList();
    }
}