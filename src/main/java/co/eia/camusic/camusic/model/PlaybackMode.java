package co.eia.camusic.camusic.model;

public enum PlaybackMode {
   RANDOM_CIRCULAR("Vuelta del Camello", "Lista Ligada Circular Doble", "Insercion O(1), navegación", true),
    FIFO_QUEUE("Fila de duendes", "Cola simple", "Encolar O(1), desencolar O(1), recorrido O(n), sin navegación anterior", false),
    ALPHABETICAL_BST("Árbol del Desierto", "Árbol binario de búsqueda", "Inserción O(h), búsqueda O(h), recorrido inorden O(n)", true);

   private final String displayName;
   private final String structureName;
   private final String complexitySummary;
   private final boolean allowsPrevious;

   PlaybackMode(String displayName, String structureName, String complexitySummary, boolean allowsPrevious) {
       this.displayName = displayName;
       this.structureName = structureName;
       this.complexitySummary = complexitySummary;
       this.allowsPrevious = allowsPrevious;
   }

    public String getDisplayName() {
        return displayName;
    }

    public String getStructureName() {
        return structureName;
    }

    public String getComplexitySummary() {
        return complexitySummary;
    }

    public boolean allowsPrevious() {
        return allowsPrevious;
    }
}
