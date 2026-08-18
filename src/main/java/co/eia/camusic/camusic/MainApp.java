package co.eia.camusic.camusic;

import co.eia.camusic.camusic.bridge.WebAppBridge;
import co.eia.camusic.camusic.model.HistoryEntry;
import co.eia.camusic.camusic.model.Song;
import co.eia.camusic.camusic.service.FavoritesService;
import co.eia.camusic.camusic.service.HistoryService;
import co.eia.camusic.camusic.service.LibraryService;
import co.eia.camusic.camusic.service.PersistenceService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOGGER =
            Logger.getLogger(MainApp.class.getName());

    private final LibraryService libraryService =
            new LibraryService();

    private final FavoritesService favoritesService =
            new FavoritesService();

    private final HistoryService historyService =
            new HistoryService();

    private PersistenceService persistenceService;

    private WebEngine webEngine;

    private boolean frontendLoaded;
    private boolean persistenceLoaded;

    private WebAppBridge webAppBridge;

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        webEngine = webView.getEngine();

        BorderPane root = new BorderPane();
        root.setCenter(webView);

        Scene scene = new Scene(
                root,
                1280,
                800
        );

        primaryStage.setTitle("CAMusic");
        primaryStage.setScene(scene);
        primaryStage.show();

        webEngine
                .getLoadWorker()
                .stateProperty()
                .addListener(
                        (observable, oldState, newState) -> {
                            if (newState
                                    == Worker.State.SUCCEEDED) {

                                frontendLoaded = true;

                                LOGGER.info(
                                        "index.html cargado correctamente"
                                );

                                injectBridgeIfReady();
                            } else if (newState
                                    == Worker.State.FAILED) {

                                LOGGER.severe(
                                        "No se pudo cargar "
                                                + "index.html"
                                );
                            }
                        }
                );

        loadPersistence();
        loadFrontend();
    }

    private void loadFrontend() {
        URL htmlUrl = getClass()
                .getResource("web/index.html");

        if (htmlUrl == null) {
            LOGGER.severe(
                    "No se encontró /web/index.html"
            );
            return;
        }

        webEngine.load(
                htmlUrl.toExternalForm()
        );
    }

    private void loadPersistence() {
        Task<PersistenceState> loadTask =
                new Task<>() {
                    @Override
                    protected PersistenceState call() {
                        PersistenceService persistence =
                                new PersistenceService(
                                        Path.of("data")
                                );

                        List<Song> library =
                                persistence.loadLibrary();

                        Map<String, Set<String>> favorites =
                                persistence.loadFavorites();

                        Map<String, List<HistoryEntry>> history =
                                persistence.loadHistory();

                        return new PersistenceState(
                                persistence,
                                library,
                                favorites,
                                history
                        );
                    }
                };

        loadTask.setOnSucceeded(event -> {
            PersistenceState state =
                    loadTask.getValue();

            persistenceService =
                    state.persistence();

            libraryService.loadAll(
                    state.library()
            );

            favoritesService.loadFavorites(
                    state.favorites()
            );

            historyService.loadHistory(
                    state.history()
            );

            persistenceLoaded = true;

            LOGGER.info(
                    "Persistencia cargada correctamente"
            );

            injectBridgeIfReady();
        });

        loadTask.setOnFailed(event -> {
            Throwable error =
                    loadTask.getException();

            LOGGER.log(
                    Level.SEVERE,
                    "Error cargando la persistencia",
                    error
            );
        });

        Thread loadThread =
                new Thread(loadTask);

        loadThread.setName(
                "camusic-persistence-loader"
        );

        loadThread.setDaemon(true);
        loadThread.start();
    }

    @SuppressWarnings("removal") //Because WebView FX need it
    private void injectBridgeIfReady() {
        if (!frontendLoaded || !persistenceLoaded) {
            return;
        }

        webAppBridge = new WebAppBridge(
                libraryService,
                favoritesService,
                historyService,
                persistenceService
        );

        JSObject window =
                (JSObject) webEngine.executeScript(
                        "window"
                );

        window.setMember(
                "javaBridge",
                webAppBridge
        );

        LOGGER.info(
                "WebAppBridge inyectado correctamente"
        );
    }

    @Override
    public void stop() {
        LOGGER.info(
                "CAMusic se está cerrando"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    private record PersistenceState(
            PersistenceService persistence,
            List<Song> library,
            Map<String, Set<String>> favorites,
            Map<String, List<HistoryEntry>> history
    ) {
    }
}