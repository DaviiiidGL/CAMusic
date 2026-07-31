module co.eia.camusic.camusic {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.eia.camusic.camusic to javafx.fxml;
    exports co.eia.camusic.camusic;
}