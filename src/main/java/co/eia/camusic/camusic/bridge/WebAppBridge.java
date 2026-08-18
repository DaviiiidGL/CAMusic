package co.eia.camusic.camusic.bridge;

import co.eia.camusic.camusic.util.JsonUtil;

public class WebAppBridge {

    // En el futuro, aquí inyectaremos los servicios (LibraryService, PlaybackService, etc.)
    public WebAppBridge() {
        // Inicialización de servicios...
    }

    // --- Método de Prueba de la Fase 6 ---
    public String ping() {
        System.out.println("[Java] ¡JavaScript acaba de invocar el método ping()!");
        return "¡Conexión exitosa! El puente bidireccional está funcionando :ppp.";
    }

    // --- Esqueletos para las siguientes fases ---
    public String getLibrary() {
        // Ejemplo de lo que hará en el futuro:
        // return JsonUtil.toJson(libraryService.getAllSongs());
        return "[]";
    }

    public String addSong(String songJson) {
        return "{}";
    }

    public String editSong(String id, String songJson) {
        return "{}";
    }

    public String deleteSong(String id) {
        return "{}";
    }

    public String nextSong() {
        return "{}";
    }

    public String previousSong() {
        return "{}";
    }

    public String switchMode(String modeName) {
        return "{}";
    }

    public String getStats() {
        return "{}";
    }
}