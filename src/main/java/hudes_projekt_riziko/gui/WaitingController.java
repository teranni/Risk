package hudes_projekt_riziko.gui;

import hudes_projekt_riziko.halozat.gameClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A WaitingController osztály kezeli a várakozási képernyőt és a játék indítását.
 * A rizikó játékban a várakozási képernyő biztosítja, hogy a játékosok szinkronizálva legyenek a játék elindítása előtt.
 */
public class WaitingController {
    static Stage stage;
    gameClient c;

    /**
     * Elindítja a játékot és navigál a játékmeneti képernyőre.
     * Fontos a játékosok szinkronizálása és a játék inicializálása.
     */
    @FXML
    public void kezdes() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(WaitingController.class.getResource("gameRisk.fxml"));
                Parent root = fxmlLoader.load();

                GameController controller = fxmlLoader.getController();
                controller.setClient(c);
                c.setController(controller);
                c.controllerReady.countDown();

                Scene scene = new Scene(root);
                stage.setTitle("Rizikó");
                stage.setScene(scene);
                stage.show();
                stage.setMaximized(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Beállítja a Stage objektumot.
     * Szükséges a képernyő váltásához.
     *
     * @param stage a jelenlegi Stage objektum
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Beállítja a játék klienset.
     * A kliens kezeli a játékosok közötti kommunikációt.
     *
     * @param c a játék kliens
     */
    public void setClient(gameClient c) {
        this.c = c;
    }
}
