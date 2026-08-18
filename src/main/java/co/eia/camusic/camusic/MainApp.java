package co.eia.camusic.camusic;

import co.eia.camusic.camusic.model.HistoryEntry;
import co.eia.camusic.camusic.model.Song;
import co.eia.camusic.camusic.service.FavoritesService;
import co.eia.camusic.camusic.service.HistoryService;
import co.eia.camusic.camusic.service.LibraryService;
import co.eia.camusic.camusic.service.PersistenceService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOGGER =
            Logger.getLogger(MainApp.class.getName());

    @Override
    public void start(Stage stage) {
        Label statusLabel = new Label(
                "Cargando datos..."
        );

        LibraryService libraryService =
                new LibraryService();

        FavoritesService favoritesService =
                new FavoritesService();

        HistoryService historyService =
                new HistoryService();

        Task<PersistenceState> loadTask =
                new Task<>() {
                    @Override
                    protected PersistenceState call() {
                        PersistenceService persistenceService =
                                new PersistenceService(
                                        Path.of("data")
                                );

                        return new PersistenceState(
                                persistenceService.loadLibrary(),
                                persistenceService.loadFavorites(),
                                persistenceService.loadHistory()
                        );
                    }
                };

        loadTask.setOnSucceeded(event -> {
            PersistenceState state =
                    loadTask.getValue();

            libraryService.loadAll(
                    state.library()
            );

            favoritesService.loadFavorites(
                    state.favorites()
            );

            historyService.loadHistory(
                    state.history()
            );

            statusLabel.setText(
                    "Persistencia correcta"
            );
        });

        loadTask.setOnFailed(event -> {
            Throwable error =
                    loadTask.getException();

            LOGGER.log(
                    Level.SEVERE,
                    "Error loading persistence data",
                    error
            );

            statusLabel.setText(
                    "Error al cargar los datos guardados"
            );
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();

        StackPane root =
                new StackPane(statusLabel);

        Scene scene =
                new Scene(root, 800, 600);

        stage.setTitle("CAMusic - Fase 4");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private record PersistenceState(
            List<Song> library,
            Map<String, Set<String>> favorites,
            Map<String, List<HistoryEntry>> history
    ) {
    }
}