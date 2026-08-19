// src/main/resources/web/js/ui-player.js
const UIPlayer = {
    progressInterval: null,
    currentProgress: 0,
    isPlaying: false,

    init: function() {
        this.fetchAndRenderState();
    },

    fetchAndRenderState: function() {
        const state = API.getPlaybackState();
        if (state) this.updateUI(state);
    },

    updateUI: function(state) {
        this.renderCurrentSong(state.currentSong);

        // ¡CORRECCIÓN AQUÍ! Usamos isPlaying y mode (nombres exactos de Java)
        this.updateTransportButtons(state.canGoPrevious, state.isPlaying, state.mode);

        // Simulación de barra de progreso
        if (state.isPlaying && !this.isPlaying) {
            this.startSimulation();
        } else if (!state.isPlaying && this.isPlaying) {
            this.stopSimulation();
        }
        this.isPlaying = state.isPlaying;

        // ¡Y AQUÍ TAMBIÉN! Usamos state.mode
        const modeSelector = document.getElementById('mode-selector');
        if (modeSelector && state.mode) {
            modeSelector.value = state.mode;
        }
    },

    renderCurrentSong: function(song) {
        const titleEl = document.getElementById('player-title');
        const artistEl = document.getElementById('player-artist');

        if (!song) {
            if (titleEl) titleEl.innerText = "Sin reproducir";
            if (artistEl) artistEl.innerText = "---";
            this.renderProgressBar(0);
        } else {
            if (titleEl) titleEl.innerText = song.name;
            if (artistEl) artistEl.innerText = `${song.artist} • ★ ${song.rating || 0}/100`;
        }
    },

    renderProgressBar: function(percentage) {
        const bar = document.getElementById('progress-bar');
        if (bar) bar.style.width = `${percentage}%`;
    },

    updateTransportButtons: function(canGoPrevious, isPlaying, mode) {
        const btnPrev = document.getElementById('btn-prev');
        const btnPlay = document.getElementById('btn-play');

        // Regla FIFO: Anterior deshabilitado en cola simple
        if (btnPrev) {
            if (mode === 'FIFO_QUEUE' || !canGoPrevious) {
                btnPrev.disabled = true;
                btnPrev.style.opacity = '0.3';
                btnPrev.style.cursor = 'not-allowed';
            } else {
                btnPrev.disabled = false;
                btnPrev.style.opacity = '1';
                btnPrev.style.cursor = 'pointer';
            }
        }

        if (btnPlay) {
            btnPlay.innerText = isPlaying ? "⏸" : "▶";
        }
    },

    togglePlay: function() {
        if (this.isPlaying) {
            API.pause();
        } else {
            API.play();
        }
        this.fetchAndRenderState();
    },

    next: function() {
        this.currentProgress = 0;
        this.renderProgressBar(0);
        API.nextSong();
        this.fetchAndRenderState();
    },

    previous: function() {
        const btnPrev = document.getElementById('btn-prev');
        if (btnPrev && btnPrev.disabled) return;

        this.currentProgress = 0;
        this.renderProgressBar(0);
        API.previousSong();
        this.fetchAndRenderState();
    },

    onModeChange: function(event) {
        API.switchMode(event.target.value);
        this.currentProgress = 0;
        this.renderProgressBar(0);
        this.fetchAndRenderState();
    },

    startSimulation: function() {
        if (this.progressInterval) clearInterval(this.progressInterval);
        this.progressInterval = setInterval(() => {
            this.currentProgress += 5;
            if (this.currentProgress > 100) {
                this.currentProgress = 100;
                this.next();
            } else {
                this.renderProgressBar(this.currentProgress);
            }
        }, 1000);
    },

    stopSimulation: function() {
        if (this.progressInterval) {
            clearInterval(this.progressInterval);
            this.progressInterval = null;
        }
    }
};

// Polling de inicialización a prueba de demoras de carga
function iniciarUIPlayer(intentos = 0) {
    if (window.javaBridge) {
        UIPlayer.init();
    } else if (intentos < 20) {
        setTimeout(() => iniciarUIPlayer(intentos + 1), 150);
    }
}

window.addEventListener('DOMContentLoaded', () => {
    iniciarUIPlayer();
});