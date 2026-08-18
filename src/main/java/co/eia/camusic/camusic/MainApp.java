package co.eia.camusic.camusic;

import co.eia.camusic.camusic.service.PersistenceService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Label statusLabel = new Label();

        try {
            PersistenceService persistenceService =
                    new PersistenceService(Path.of("data"));

            persistenceService.saveLibrary(List.of());
            persistenceService.saveFavorites(Map.of());
            persistenceService.saveHistory(Map.of());

            statusLabel.setText("Persistencia correcta");
        } catch (RuntimeException exception) {
            statusLabel.setText(
                    "Error de persistencia: "
                            + exception.getMessage()
            );
        }

        StackPane root = new StackPane(statusLabel);
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("CAMusic - Fase 4");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}