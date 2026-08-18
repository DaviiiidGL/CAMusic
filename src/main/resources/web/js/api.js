// src/main/resources/web/js/api.js

const API = {
    // Wrapper centralizado de parseo y errores
    call: function(javaAction) {
        try {
            if (!window.javaBridge) throw new Error("Puente Java no inyectado");

            const responseString = javaAction();
            const response = JSON.parse(responseString);

            if (!response.success) {
                console.error("[API] Java reportó un error:", response.error);
                alert("Error: " + response.error);
                return null;
            }
            return response.data;
        } catch (e) {
            console.error("[API] Fallo crítico de comunicación:", e);
            alert("Hubo un problema de conexión con el núcleo de Java.");
            return null;
        }
    },

    // 1. Listar (Read)
    getLibrary: () => API.call(() => window.javaBridge.getLibrary()),

    // 2. Crear (Create)
    addSong: (songData) => API.call(() => window.javaBridge.addSong(JSON.stringify(songData))),

    // 3. Actualizar (Update)
    editSong: (id, songData) => API.call(() => window.javaBridge.editSong(id, JSON.stringify(songData))),

    // 4. Eliminar (Delete)
    deleteSong: (id) => API.call(() => window.javaBridge.deleteSong(id)),

    // 5. Búsqueda (Filtro local rápido usando la lista de Java)
    searchSongs: (query) => {
        const allSongs = API.getLibrary() || [];
        if (!query) return allSongs;
        const q = query.toLowerCase();
        return allSongs.filter(song =>
            song.name.toLowerCase().includes(q) ||
            song.artist.toLowerCase().includes(q)
        );
    },

    // 6. Filtros Complejos
    filterSongs: (criteria) => {
        const allSongs = API.getLibrary() || [];
        return allSongs.filter(song => {
            if (criteria.genre && song.genre !== criteria.genre) return false;
            if (criteria.minRating && song.rating < criteria.minRating) return false;
            return true;
        });
    }
};