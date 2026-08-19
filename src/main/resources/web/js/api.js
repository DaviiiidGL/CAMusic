// src/main/resources/web/js/api.js
const API = {
    call: function(javaAction) {
        try {
            if (!window.javaBridge) {
                console.warn("[API] javaBridge aún no disponible.");
                return null;
            }

            const responseString = javaAction();
            if (!responseString) return null;

            const response = JSON.parse(responseString);

            // Si es un Array directo o un objeto sin la bandera 'success', es respuesta cruda válida
            if (Array.isArray(response) || response.success === undefined) {
                return response;
            }

            // Si viene envuelto y falló
            if (response.success === false) {
                console.error("[API] Error desde Java:", response.error);
                alert("Error de Java: " + (response.error || "Operación no completada"));
                return null;
            }

            return response.data;
        } catch (e) {
            console.error("[API] Error de comunicación:", e);
            return null;
        }
    },

    // Fase 8 - Biblioteca CRUD
    getLibrary: () => API.call(() => window.javaBridge.getLibrary()),
    addSong: (songData) => API.call(() => window.javaBridge.addSong(JSON.stringify(songData))),
    editSong: (id, songData) => API.call(() => window.javaBridge.editSong(id, JSON.stringify(songData))),
    deleteSong: (id) => API.call(() => window.javaBridge.deleteSong(id)),

    // Fase 9 - Reproducción y Modos
    getPlaybackState: () => API.call(() => window.javaBridge.getPlaybackState()),
    play: () => API.call(() => window.javaBridge.play()),
    pause: () => API.call(() => window.javaBridge.pause()),
    nextSong: () => API.call(() => window.javaBridge.nextSong()),
    previousSong: () => API.call(() => window.javaBridge.previousSong()),
    switchMode: (mode) => API.call(() => window.javaBridge.switchMode(mode))
};