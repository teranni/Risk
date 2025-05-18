package hudes_projekt_riziko.gui;

import hudes_projekt_riziko.halozat.gameClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A SetupController osztály kezeli a játékosok számának választását és a játék indítási folyamatát.
 * Ez az osztály felelős az alapértelmezett beállítások és a játékmenet indításáért a várakozási képernyőre történő navigálással.
 * A rizikó játékban az első lépés, hogy a játékosok kiválasztják a játékosok számát, majd elindítják a játékot.
 */
public class SetupController {
    Stage stage;
    String num;
    gameClient c;

    @FXML
    public Button tovabbButton;
    @FXML
    public Label errorText;
    @FXML
    public ComboBox playerChoice;

    /**
     * Az inicializáló függvény, amely a ComboBox alapértelmezett értékét beállítja és a játékosok számának kiválasztásához
     * eseménykezelőt rendel.
     *
     * A rizikó játékban a játékosok számának kiválasztása alapvető fontosságú lépés, mivel a játék többi része (pl. térképfelosztás,
     * erőforrások elosztása) ezen paraméterek függvényében történik.
     */
    @FXML
    public void initialize() {
        playerChoice.getSelectionModel().selectFirst(); // Optional: select default
        num = playerChoice.getSelectionModel().getSelectedItem().toString();
        playerChoice.setOnAction(event -> {
            num = playerChoice.getSelectionModel().getSelectedItem().toString();
        });
    }

    /**
     * A játék indítását végző függvény, amely a várakozási képernyőre navigál, és elindítja a játék kliensét.
     *
     * A rizikó játékban a játékosok közötti kommunikáció és szinkronizálás kulcsfontosságú. Ez a függvény kezeli az átmenetet
     * a kezdő képernyőről a várakozási képernyőre, és biztosítja a kliens inicializálását.
     *
     * @param actionEvent az esemény, amely a gombnyomást jelzi
     * @throws IOException ha a képernyő váltása közben hiba lép fel
     */
    @FXML
    public void onStartButtonAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene oldScene = stage.getScene();
        double width = oldScene.getWidth();
        double height = oldScene.getHeight();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("waitingScreen.fxml"));
        Parent root = fxmlLoader.load();
        WaitingController controller = fxmlLoader.getController();
        controller.setClient(c);
        c.setWaitController(controller);

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        controller.setStage(stage);

        Scene scene = new Scene(root, width, height);
        stage.setTitle("Waiting Screen");
        stage.setScene(scene);
        stage.show();

        c.startSignal.countDown();
    }

    /**
     * Visszaadja a kiválasztott játékosok számát.
     *
     * A kiválasztott szám a rizikó játék működéséhez szükséges, mivel ezen alapulnak a különböző játékmeneti beállítások,
     * például a játékosok közötti felosztás és az erőforrások elosztása.
     *
     * @return a kiválasztott játékosok száma
     */
    public String getPlayerNum() {
        return num;
    }

    /**
     * Beállítja a játékosok számát, és letiltja a ComboBoxot.
     *
     * A játék indítása előtt fontos, hogy a játékosok számát rögzítsük, mivel ezt követően a beállítások már nem változtathatók.
     *
     * @param playerNum a játékosok száma
     */
    public void setPlayerNum(String playerNum) {
        num = playerNum + " játékos";
        playerChoice.setValue(num);
        playerChoice.setDisable(true);
    }

    /**
     * Beállítja a Stage objektumot, amely a jelenlegi ablakot jelenti.
     *
     * A Stage beállítása szükséges a képernyő váltásához és az új tartalom megjelenítéséhez.
     *
     * @param stage a jelenlegi Stage objektum
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Beállítja a játék klienset, amely a játék kommunikációját kezeli.
     *
     * A játék kliens kulcsszerepet játszik a játékosok közötti adatkommunikációban. Ezt a beállítást a játék indításakor végzik el,
     * hogy a kliens képes legyen a megfelelő adatokat küldeni és fogadni a játék során.
     *
     * @param c a játék kliens
     */
    public void setClient(gameClient c) {
        this.c = c;
    }
}
