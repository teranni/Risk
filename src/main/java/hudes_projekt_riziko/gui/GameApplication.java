package hudes_projekt_riziko.gui;

import hudes_projekt_riziko.GameLogic;
import hudes_projekt_riziko.halozat.gameClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A GameApplication a Rizikó játék fő JavaFX alkalmazásosztálya.
 * Elindítja a kliens kapcsolatot, betölti a kezdőképernyőt, és beállítja a GUI vezérlőit.
 * Először csak az elsőnek induló kliensnek nyílik meg, ő választhatja ki, hogy 2 vagy 3 játékossal szeretne játszani.
 * A többi kliens ennek a választásnak megfelelő számban tud csatlakozni.
 *
 * A játék megbízhatóan működik 2 és 3 játékosra is, ahol 2 játékos esetén egy neutrális 3. játékos is generálásra kerül.
 * Egy kör felépítése:
 * 1. Erősítések elhelyezése: A lehelyezhető erősítések számát előre megkapja a játékos, majd azokat egyesével elhelyezheti a térképen.
 * (káryták beváltására sajnos nincs lehetőség, de ettől függetlenól megkapja őket a játékos terület foglalási körben)
 * 2. Támadás: akárhány támadás indítható; támadó és védekező meg kell, hogy várják egymás döntéseit, majd a gép visszadja nekik a kockadobás eredményét
 * 3. Áthelyezés: egységek átcsoportosítása
 *      Lépés gombra kattintással ez után automatikusan a következő játékos jön.
 * A támadás/védekezés/áthelyezés műveletek adatainak megadása ugyanazokban a ComboBox-okban történik.
 */
public class GameApplication extends Application {

    /**
     * A JavaFX alkalmazás belépési pontja, inicializálja a GUI-t és elindítja a hálózati klienst.
     *
     * @param stage Az elsődleges ablak (színpad), amelyet a JavaFX használ.
     * @throws IOException ha a FXML fájl betöltése sikertelen.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("startScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        SetupController controller = fxmlLoader.getController();
        controller.setStage(stage);

        gameClient client = new gameClient("localhost");
        new Thread(client).start();

        controller.setClient(client);
        client.setSetupController(controller);
        client.setRunna(() -> {
            stage.setTitle("Rizikó");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        });
    }

    /**
     * A Java alkalmazás belépési pontja – ezzel indul a JavaFX alkalmazás.
     *
     * @param args parancssori argumentumok
     */
    public static void main(String[] args) {
        launch();
    }
}