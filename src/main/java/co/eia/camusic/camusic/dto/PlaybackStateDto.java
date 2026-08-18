package co.eia.camusic.camusic.dto;

import co.eia.camusic.camusic.model.PlaybackMode;

public class PlaybackStateDto {
    private SongDto currentSong;
    private PlaybackMode mode;
    private boolean isPlaying;
    private double simulatedProgress;
    private boolean canGoPrevious;

    public PlaybackStateDto(SongDto currentSong, PlaybackMode mode, boolean isPlaying, double simulatedProgress, boolean canGoPrevious) {
        this.currentSong = currentSong;
        this.mode = mode;
        this.isPlaying = isPlaying;
        this.simulatedProgress = simulatedProgress;
        this.canGoPrevious = canGoPrevious;
    }

    public SongDto getCurrentSong() {return currentSong;}
    public void setCurrentSong(SongDto currentSong) {this.currentSong = currentSong;}

    public PlaybackMode getMode() {return mode;}
    public void setMode(PlaybackMode mode) {this.mode = mode;}

    public boolean isPlaying() {return isPlaying;}
    public void setPlaying(boolean playing) {isPlaying = playing;}

    public double getSimulatedProgress() {return simulatedProgress;}
    public void setSimulatedProgress(double simulatedProgress) {this.simulatedProgress = simulatedProgress;}

    public boolean isCanGoPrevious() {return canGoPrevious;}
    public void setCanGoPrevious(boolean canGoPrevious) {this.canGoPrevious = canGoPrevious;}
}
