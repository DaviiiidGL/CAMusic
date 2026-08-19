// src/main/resources/web/js/ui-library.js

const UILibrary = {
    init: function() {
        this.loadAndRender();
    },

    // --- RENDERIZADO ---
    loadAndRender: function() {
        const songs = API.getLibrary() || [];
        this.renderLibrary(songs);
    },

    renderLibrary: function(songs) {
        const container = document.getElementById('library-list');

        // Cabecera con botón de agregar
        let html = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2>Mi Biblioteca (${songs.length})</h2>
                <button class="btn" onclick="UILibrary.openAddSongForm()">+ Nueva Canción</button>
            </div>
        `;

        if (songs.length === 0) {
            html += `<p style="text-align: center; opacity: 0.7; margin-top: 40px;">No hay canciones en tu biblioteca. ¡Comienza a agregar algunas!</p>`;
        } else {
            html += `<div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: var(--spacing-4);">`;
            songs.forEach(song => {
                html += this.renderSongCard(song);
            });
            html += `</div>`;
        }

        container.innerHTML = html;
    },

    renderSongCard: function(song) {
        return `
            <div class="glass-panel card" style="display: flex; flex-direction: column; padding: 15px;">
                <div style="background: var(--color-border); height: 140px; border-radius: var(--radius-sm); display: flex; justify-content: center; align-items: center; font-size: 3em;">
                    ${song.rating > 80 ? '🔥' : '🎵'}
                </div>
                <h3 style="margin-top: 15px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;" title="${song.name}">${song.name}</h3>
                <p style="opacity: 0.8; margin-bottom: 10px;" title="${song.artist}">${song.artist}</p>
                <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.9em; margin-bottom: 15px;">
                    <span style="color: var(--color-btn-hover);">★ ${song.rating}/100</span>
                    <span style="opacity: 0.6;">${song.genre || 'Sin gen'}</span>
                </div>
                <div style="display: flex; gap: 5px; margin-top: auto;">
                    <button class="btn w-100" onclick="UILibrary.openEditSongForm('${song.id}')">✏️ Editar</button>
                    <button class="btn" onclick="UILibrary.handleDelete('${song.id}')">🗑️</button>
                </div>
            </div>
        `;
    },

    // --- FORMULARIOS ---
    openAddSongForm: function() {
        document.getElementById('song-form').reset();
        document.getElementById('song-id').value = '';
        document.getElementById('modal-title').innerText = '✨ Agregar Canción';
        document.getElementById('song-modal').style.display = 'flex';
    },

    openEditSongForm: function(id) {
        const songs = API.getLibrary();
        const song = songs.find(s => s.id === id);
        if (!song) return;

        document.getElementById('song-id').value = song.id;
        document.getElementById('song-name').value = song.name;
        document.getElementById('song-artist').value = song.artist;
        document.getElementById('song-album').value = song.album || '';
        document.getElementById('song-genre').value = song.genre || '';
        document.getElementById('song-duration').value = song.durationSeconds || '';
        document.getElementById('song-year').value = song.releaseYear || '';
        document.getElementById('song-rating').value = song.rating || 0;

        document.getElementById('modal-title').innerText = '✏️ Editar Canción';
        document.getElementById('song-modal').style.display = 'flex';
    },

    closeModal: function() {
        document.getElementById('song-modal').style.display = 'none';
    },

    // --- LÓGICA CRUD ---
    handleFormSubmit: function(event) {
        event.preventDefault(); // Evita que la página intente navegar a otra URL

        const id = document.getElementById('song-id').value;
        const name = document.getElementById('song-name').value.trim();
        const artist = document.getElementById('song-artist').value.trim();
        const rating = parseInt(document.getElementById('song-rating').value) || 0;

        // Validación estricta en el cliente (como pide la rúbrica)
        if (!name || !artist) {
            alert("El nombre y el artista son obligatorios.");
            return;
        }
        if (rating < 0 || rating > 100) {
            alert("El rating debe ser un número entre 0 y 100.");
            return;
        }

        const songData = {
            name: name,
            artist: artist,
            album: document.getElementById('song-album').value.trim(),
            genre: document.getElementById('song-genre').value.trim(),
            durationSeconds: parseInt(document.getElementById('song-duration').value) || 0,
            releaseYear: parseInt(document.getElementById('song-year').value) || new Date().getFullYear(),
            rating: rating
        };

        let result;
        if (id) {
            result = API.editSong(id, songData);
        } else {
            result = API.addSong(songData);
        }

        // ¡ATENCIÓN AQUÍ! Solo cerramos y recargamos si Java nos dio un OK
        if (result) {
            this.closeModal();
            this.loadAndRender();
        }
    },

    handleDelete: function(songId) {
        if (confirm("¿Estás seguro de que quieres eliminar esta canción de la biblioteca de la carrera?")) {
            const result = API.deleteSong(songId);
            if (result) {
                this.loadAndRender();
            }
        }
    }
};

// Arrancar el UI solo cuando la página y el puente estén completamente listos
window.addEventListener('DOMContentLoaded', () => {
    // Damos un margen de 100ms para asegurar que Java inyectó el objeto window.javaBridge
    setTimeout(() => {
        if (window.javaBridge) {
            UILibrary.init();
        } else {
            document.getElementById('library-list').innerHTML = "<p>Error crítico: Puente Java no detectado.</p>";
        }
    }, 100);
});