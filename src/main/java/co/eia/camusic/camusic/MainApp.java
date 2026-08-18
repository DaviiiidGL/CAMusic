package co.eia.camusic.camusic;

import co.eia.camusic.camusic.bridge.WebAppBridge;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Crear el componente WebView que actuará como nuestro navegador interno
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // 2. Configurar el listener de carga (¡Crítico para inyectar el bridge a tiempo!)
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("[Java] index.html cargado exitosamente. Inyectando WebAppBridge...");

                // Obtenemos el objeto 'window' de JavaScript
                JSObject window = (JSObject) webEngine.executeScript("window");

                // Inyectamos nuestra clase Java como 'javaBridge'
                window.setMember("javaBridge", new WebAppBridge());

            } else if (newState == Worker.State.FAILED) {
                System.err.println("[Java] Error grave: No se pudo cargar el index.html.");
            }
        });

        // 3. Cargar el index.html desde el classpath
        URL htmlUrl = getClass().getResource("/web/index.html");
        if (htmlUrl != null) {
            webEngine.load(htmlUrl.toExternalForm());
        } else {
            System.err.println("[Java] No se encontró el archivo en /web/index.html. Verifica tu estructura de carpetas.");
        }

        // 4. Configurar la ventana (Stage)
        BorderPane root = new BorderPane();
        root.setCenter(webView);

        // Usamos una resolución fija razonable para un reproductor de escritorio
        Scene scene = new Scene(root, 1280, 800);

        primaryStage.setTitle("Camellos vs Enanos Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Esto arranca la aplicación JavaFX
        launch(args);
    }
}