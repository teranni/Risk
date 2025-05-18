package hudes_projekt_riziko.gui;

import hudes_projekt_riziko.halozat.gameClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

/**
 * A GameController osztály felelős a Rizikó játék felhasználói felületének vezérléséért.
 *
 * Ez az osztály inicializálja a grafikus felület főbb elemeit, kezeli a játékos statisztikák,
 * küldetések, térkép és játékos interakciók megjelenítését, valamint biztosítja az alapvető GUI logikát.
 * Az FXML fájlban megadott felületi elemekhez kapcsolódik.
 */
public class GameController implements Initializable {
    static Integer playerNum = 0;   // Az aktuális játékosok száma.
    Integer clientID = 0;           // Az aktuális kliens azonosítója.
    gameClient c;                   // A hálózati kapcsolatot kezelő kliens példánya.
    boolean kor;                    // Azt jelöli, hogy a játékos köre van-e.
    boolean isKartya = false;       // Jelzi, hogy a játékos szerzett-e már kártyát az adott körben.

    @FXML
    public HBox mainHBox;
    @FXML
    public VBox playerStats;
    @FXML
    public VBox middleVBox;
    @FXML
    public VBox rightVBox;

    @FXML
    public TextFlow allapot;
    @FXML
    public Text allapotText;
    @FXML
    public Text teruletText;
    @FXML
    public ImageView mapImage;
    @FXML
    public Accordion kontStats;

    @FXML
    public TextFlow mission;
    @FXML
    public Text missionText;
    @FXML
    public ChoiceBox lepes;
    @FXML
    public ComboBox honnan;
    @FXML
    public ComboBox hova;
    @FXML
    public ComboBox mennyi;
    @FXML
    public Button lepesButton;
    @FXML
    public ListView myTeruletek;

    public Button addButton;
    @FXML
    public ListView myKartyak;

    /**
     * Az osztály inicializálási metódusa.
     *
     * Ez a metódus állítja be a GUI méreteit a képernyőhöz igazítva,
     * létrehozza a játékos statisztikákat, kontinens információkat,
     * és inicializálja az interakciókhoz kapcsolódó eseményfigyelőket.
     *
     * @param url Az FXML fájl elérési útja
     * @param rb Az erőforráscsomag
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); // Or use getScreens().get(...) for multi-monitor
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            mainHBox.setMaxWidth(screenWidth);
            mainHBox.setMaxHeight(screenHeight);

            playerStats.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.25));
            middleVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.5));
            rightVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.25));
        });

        // Játékos statisztikai panelek hozzáadása
        for (int i = 0; i < 3; i++) {
            Label nameLabel = getLabel(i, playerNum);

            VBox statsBox = new VBox(5);
            statsBox.setPadding(new Insets(5));

            statsBox.getChildren().addAll(
                    new Label("Területek száma: " + 0),
                    new Label("Kontinensek száma: " + 0),
                    new Label("Hadosztályok száma: " + 0),
                    new Label("Kártyái száma: " + 0)
            );

            TitledPane statsPane = new TitledPane("Stats", statsBox);

            // Add both name label and stats accordion to the VBox
            VBox playerBox = new VBox(5, nameLabel, statsPane);
            playerBox.setPadding(new Insets(10));
            playerBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

            playerStats.getChildren().add(playerBox);
        }

        // Térkép méretének beállítása a jelenethez igazítva
        Platform.runLater(() -> {
            Scene scene = mapImage.getScene();

            if (scene != null) {
                mapImage.fitWidthProperty().bind(scene.widthProperty().divide(2));
                mapImage.fitHeightProperty().bind(scene.heightProperty().divide(2));
                mapImage.setPreserveRatio(true);
            }
        });

        // Kontinensek és területek hozzáadása
        for (int i = 0; i < 6; i++) {
            String continentName = "Név";
            String[] details = new String[0];

            VBox continentDetails = new VBox(10);
            for (String detail : details) {
                continentDetails.getChildren().add(new Label(detail));
            }

            TitledPane continentPane = new TitledPane();
            continentPane.setText(continentName);
            continentPane.setContent(continentDetails);

            kontStats.getPanes().add(continentPane);
        }

        // Küldetés szövegének beállítása
        missionText.setText("Ez a te küldetésed.");

        // Eseményfigyelők: a választási lehetőségek frissítése a felhasználói interakciók alapján
        lepes.valueProperty().addListener((observable, oldValue, newValue) -> {
            honnan.setItems(FXCollections.observableArrayList(myTeruletek.getItems()));
            updateHova();
            updateMennyi();
        });

        honnan.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateHova();
            updateMennyi();
        });
    }

    /**
     * Létrehoz egy színezett, stílusos Label komponenst az adott játékoshoz.
     *
     * A label színe eltérő, hogy megkülönböztesse az egyes játékosokat,
     * és jelöli, ha az adott játékos a felhasználó (kliens).
     *
     * @param i A játékos indexe (0-al kezdődő)
     * @param players Az összes játékos száma
     * @return A játékos nevét és státuszát tartalmazó Label objektum
     */
    private Label getLabel(int i, Integer players) {
        Label nameLabel = new Label("Player " + (i + 1));
        if (i+1 == clientID) {
            nameLabel = new Label("Player " + (i + 1) + " (te)");
        }

        if (i == 0) {
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: red");
        } else if (i == 1) {
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: blue");
        } else if (i == 2 && players == 3) {
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: green");
        } else {
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: gray");
        }
        return nameLabel;
    }

    /**
     * Beállítja a játékosok számát.
     *
     * @param playerNum A játékban részt vevő játékosok száma
     */
    public void setPlayersNum(Integer playerNum) {
        GameController.playerNum = playerNum;
    }

    /**
     * Beállítja az aktuális kliens azonosítóját.
     *
     * @param ID A kliens egyedi azonosítója
     */
    public void setClientID(Integer ID) {
        clientID = ID;
    }

    /**
     * Beállítja, hogy a kliens köre van (true).
     */
    public void setTrue() {
        kor = true;
    }

    /**
     * Beállítja, hogy a kliens köre nincs (false).
     */
    public void setFalse() {
        kor = false;
    }

    /**
     * Beállítja a hálózati kommunikációért felelős gameClient példányt.
     *
     * @param c A gameClient példány
     */
    public void setClient(gameClient c) {
        this.c = c;
    }

    /**
     * Frissíti egy adott játékos statisztikáit a felhasználói felületen.
     *
     * @param ID A játékos azonosítója
     * @param terSum Területek száma
     * @param kontSum Kontinensek száma
     * @param hadSum Hadosztályok száma
     * @param pakliSum Kártyák száma
     */
    public void setJatekosStat(Integer ID, Integer terSum, Integer kontSum, Integer hadSum, Integer pakliSum) {
        Platform.runLater(() -> {
            if (ID > playerStats.getChildren().size()) return;

            VBox playerBox = (VBox) playerStats.getChildren().get(ID - 1);

            Label newNameLabel = getLabel(ID - 1, playerNum); // ID-1 because getLabel uses 0-based index
            playerBox.getChildren().set(0, newNameLabel);

            TitledPane statsPane = (TitledPane) playerBox.getChildren().get(1);
            VBox statsBox = new VBox(5);
            statsBox.setPadding(new Insets(5));
            statsBox.getChildren().addAll(
                    new Label("Területek száma: " + terSum),
                    new Label("Kontinensek száma: " + kontSum),
                    new Label("Hadosztályok száma: " + hadSum),
                    new Label("Kártyái száma: " + pakliSum)
            );

            statsPane.setContent(statsBox);
        });
    }

    /**
     * Beállítja a játékos által birtokolt területek listáját.
     *
     * @param lista A területek neveinek listája
     */
    public void setTeruletek(List<String> lista) {
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList(lista);
            myTeruletek.setItems(items);
        });
    }

    /**
     * Beállítja a játékos által birtokolt kártyák listáját.
     *
     * @param lista A kártyák neveinek listája
     */
    public void setKartyak(List<String> lista) {
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList(lista);
            myKartyak.setItems(items);
        });
    }

    /**
     * Kártya hozzáadása a játékos paklijához.
     *
     * Ha a játékos már rendelkezik kártyával, akkor visszaküldi azt a szervernek.
     *
     * @param kartya A hozzáadandó kártya neve
     */
    public void addKartya(String kartya) {
        Platform.runLater(() -> {
            if (isKartya == false) {
                myKartyak.getItems().add(kartya);
                isKartya = true;
            } else {
                backKartya(kartya);
            }
        });
    }

    /**
     * Visszaküldi a megadott kártyát a szervernek.
     *
     * @param kartya A visszaküldendő kártya neve
     */
    public void backKartya(String kartya) {
        c.backKartya(kartya);
    }

    /**
     * Beállítja a játékos küldetésének szövegét a felületen.
     *
     * @param s A küldetés szövege
     */
    public void setKuldetes(String s) {
        Platform.runLater(() -> {
            missionText.setText("Küldetésed:\n" + s);
        });
    }

    /**
     * Beállítja az aktuális játékos körét és az elhelyezhető területek számát.
     *
     * @param i Az aktuális játékos azonosítója
     * @param j A lehelyezhető területek száma
     */
    public void setAllapot(Integer i, Integer j) {
        Platform.runLater(() -> {
            allapotText.setText("Player " + i + " köre van most\n");
            if (i == clientID) {
                teruletText.setText("Lehelyezhető területek: " + j + " db");
                addButton.setDisable(false);
            }
        });
    }

    /**
     * Frissíti a terület szöveget, csökkenti a hátralevő lehelyezhető területek számát,
     * és engedélyezi a mozgáshoz szükséges vezérlőelemeket, ha nincs több lehelyezhető terület.
     */
    public void setTeruletText() {
        Platform.runLater(() -> {
            int j = Integer.parseInt(String.valueOf(teruletText.getText().charAt(24))) -1;

            if (j == 0) {
                teruletText.setText(null);
                addButton.setDisable(true);
                lepes.setDisable(false);
                honnan.setDisable(false);
                hova.setDisable(false);
                mennyi.setDisable(false);
                lepesButton.setDisable(false);
            } else {
                teruletText.setText("Lehelyezhető területek: " + j + " db");
            }
        });
    }

    /**
     * Frissíti a kontinenst és területekhez tartozó katonák számát.
     * Az egyes területek és azok katonai állapota alapján vizualizálja a térképet.
     * A játékosok számára világosan jelzi, hogy melyik területeken hány katona található.
     *
     * @param lista A kontinensek és területek listája, ahol minden kontinenshez tartozik egy lista a területekkel.
     */
    public void setKont(Map<String, List<String>> lista) {
        Platform.runLater(() -> {
            kontStats.getPanes().clear();

            for (Map.Entry<String, List<String>> entry : lista.entrySet()) {
                String continentName = entry.getKey();
                List<String> territories = entry.getValue();

                VBox continentDetails = new VBox(5);
                for (String ter : territories) {
                    String[] parts = ter.split(":\\s*"); // Elválasztás a ": " szerint
                    if (parts.length == 3) {
                        // Területnév Label
                        Label nameLabel = new Label(parts[0]);

                        // Katonaszám Label színezve
                        Label troopLabel = new Label(parts[1]);

                        if (parts[2].equals("1")) {
                            troopLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else if (parts[2].equals("2")) {
                            troopLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                        } else if (parts[2].equals("3") && playerNum == 3) {
                            troopLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        } else {
                            troopLabel.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
                        }

                        VBox territoryBox = new VBox(2, nameLabel, troopLabel);
                        territoryBox.setPadding(new Insets(2));
                        continentDetails.getChildren().add(territoryBox);
                    } else {
                        // Ha nem sikerült szétválasztani, írjuk ki egyben
                        continentDetails.getChildren().add(new Label(ter));
                    }
                }

                TitledPane continentPane = new TitledPane(continentName, continentDetails);

                kontStats.getPanes().add(continentPane);
            }
        });
    }

    /**
     * A felhasználó által kiválasztott területet hozzáadja a játékhoz.
     * A kód felelős azért, hogy a felhasználó új területet vegyen birtokba,
     * ami közvetlenül befolyásolja a területek számát és a háború stratégiáját.
     *
     * @param mouseEvent A felhasználó kattintása.
     */
    public void addSubmit(MouseEvent mouseEvent) {
        String selectedTerritory = (String) myTeruletek.getSelectionModel().getSelectedItem();
        if (selectedTerritory == null) return;
        c.addTerulet(clientID, selectedTerritory);
    }

    /**
     * Frissíti a terület katonai állapotát és a játékosok statisztikáit, miután egy új területet hozzáadtak.
     * Az új terület katonai ereje (hadsereg) módosulhat a választott terület függvényében.
     *
     * @param id A játékos ID-ja.
     * @param selectedTerritory A kiválasztott terület.
     */
    public void addSubmitUI(int id, String selectedTerritory) {
        Platform.runLater(() -> {
            if (kor) {
                setTeruletText();
            }

            for (TitledPane continentPane : kontStats.getPanes()) {
                VBox continentBox = (VBox) continentPane.getContent();
                for (javafx.scene.Node node : continentBox.getChildren()) {
                    if (node instanceof VBox) {
                        VBox territoryBox = (VBox) node;
                        if (territoryBox.getChildren().size() == 2 &&
                                territoryBox.getChildren().get(0) instanceof Label &&
                                territoryBox.getChildren().get(1) instanceof Label) {

                            Label nameLabel = (Label) territoryBox.getChildren().get(0);
                            Label troopLabel = (Label) territoryBox.getChildren().get(1);

                            if (nameLabel.getText().equals(selectedTerritory)) {
                                int currentTroops = Integer.parseInt(troopLabel.getText());
                                troopLabel.setText(String.valueOf(currentTroops + 1));


                                VBox playerBox = (VBox) playerStats.getChildren().get(id - 1);
                                TitledPane statsPane = (TitledPane) playerBox.getChildren().get(1);
                                VBox statsBox = (VBox) statsPane.getContent();

                                for (Node n : statsBox.getChildren()) {
                                    if (n instanceof Label label && label.getText().startsWith("Hadosztályok száma: ")) {
                                        String text = label.getText();
                                        int current = Integer.parseInt(text.replaceAll("\\D+", ""));
                                        label.setText("Hadosztályok száma: " + (current + 1));
                                        break;
                                    }
                                }

                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Frissíti a választható területek listáját egy új lépés előtt.
     * A játékos kiválaszthatja, hogy melyik területre akar áthelyezni egységeket
     * vagy támadni, így közvetlen hatással van a játék dinamikájára.
     */
    private void updateHova() {
        String selectedLepes = (String) lepes.getValue();
        String selectedHonnan = (String) honnan.getValue();

        hova.getItems().clear();

        if (selectedLepes == null || selectedHonnan == null) return;

        if (selectedLepes.equals("Áthelyezés")) {
            c.athelyezes(clientID, selectedHonnan);
        } else if (selectedLepes.equals("Csata")) {
            c.tamadas(clientID, selectedHonnan);
        }
    }

    /**
     * A támadás vagy áthelyezés előtt frissíti az elérhető mennyiségeket.
     * A támadások sikeressége és az áthelyezés mértéke a kiválasztott mennyiségtől függ.
     */
    private void updateMennyi() {
        String selectedHonnan = (String) honnan.getValue();
        mennyi.getItems().clear();
        if (selectedHonnan == null) return;
        c.mennyiseg(selectedHonnan);
    }

    /**
     * Beállítja a legyőzhető egységek számát az adott választási lehetőségekhez a játékban.
     * A max paraméter határozza meg a legyőzhető egységek maximális számát.
     * Ezt a függvényt használjuk a védekezés során, amikor a játékosok eldönthetik, hány egységgel kívánnak védekezni.
     *
     * @param max A maximális szám, amely a legyőzhető egységek számát szabályozza.
     */
    public void setMennyiseg(int max) {
        Platform.runLater(() -> {
            mennyi.getItems().clear();
            for (int i = 1; i < max; i++) {
                mennyi.getItems().add(i);
            }
        });
    }

    /**
     * Beállítja a játékos által birtokolt, áthelyezhető területek listáját.
     * Ezt a függvényt használjuk a terület áthelyezésekor, amikor a játékos választani tudja, melyik területet szeretné mozgatni.
     *
     * @param sajat A játékos által birtokolt területek listája.
     */
    public void setAthelyezhetoTeruletek(List<String> sajat) {
        Platform.runLater(() -> {
            hova.getItems().clear();
            hova.getItems().addAll(sajat);
        });
    }

    /**
     * Beállítja a támadható területek listáját, amelyek elérhetőek a támadó játékos számára.
     * Ezt a függvényt akkor használjuk, amikor a játékos támadni szeretne egy területet.
     *
     * @param sajat A támadható területek listája.
     */
    public void setTamadhatoTeruletek(List<String> sajat) {
        Platform.runLater(() -> {
            hova.getItems().clear();
            hova.getItems().addAll(sajat);
        });
    }

    /**
     * A lépés beküldésére szolgáló funkció. A játékos kiválasztja a műveletet (áthelyezés vagy csata),
     * az érintett területeket és a szükséges mennyiséget, majd elindítja a megfelelő akciót.
     * Ezt a metódust akkor használjuk, amikor a játékos befejezi a választását és elindítja a műveletet.
     *
     * @param mouseEvent A kattintási esemény, amely a lépés beküldését indítja el.
     */
    public void lepesSubmit(MouseEvent mouseEvent) {
        String selectedLepes = (String) lepes.getValue();
        String selectedHonnan = (String) honnan.getValue();
        String selectedHova = (String) hova.getValue();
        Integer selectedMennyi = (Integer) mennyi.getValue();

        if (selectedLepes.equals("Áthelyezés") && selectedHova == null && selectedMennyi == null) {
            teruletText.setText("");
            lepesButton.setDisable(true);
            isKartya = false;
            c.next();
            return;
        }
        if (selectedHonnan == null || selectedHova == null || selectedMennyi == null) {
            return;
        }

        if (selectedLepes.equals("Áthelyezés")) {
            teruletText.setText("");
            lepesButton.setDisable(true);
            isKartya = false;
            c.athelyezesAction(clientID, selectedHonnan, selectedHova, selectedMennyi);
        } else if (selectedLepes.equals("Csata")) {
            c.tamadasAction(clientID, selectedHonnan, selectedHova, selectedMennyi);
            lepesButton.setDisable(true);
            Platform.runLater(() -> {
                teruletText.setText("Várakozás a védekezőre...");
            });
        }
    }

    /**
     * A lépés beküldése a felhasználói felületen. A kiválasztott területek és mennyiség frissítése
     * a játékos területein, hogy tükrözze az áthelyezés vagy támadás változásait.
     * Ezt akkor használjuk, amikor a játékos lépése megtörtént, és a játék területein is frissítés történik.
     *
     * @param selectedHonnan Az a terület, ahonnan a lépés indul.
     * @param selectedHova Az a terület, ahová a lépés irányul.
     * @param selectedMennyi A lépéshez szükséges egységek száma.
     */
    public void lepesSubmitUI(String selectedHonnan, String selectedHova, Integer selectedMennyi) {
        Platform.runLater(() -> {
            for (TitledPane continentPane : kontStats.getPanes()) {
                VBox continentBox = (VBox) continentPane.getContent();
                for (Node node : continentBox.getChildren()) {
                    if (node instanceof VBox territoryBox && territoryBox.getChildren().size() == 2) {
                        Label nameLabel = (Label) territoryBox.getChildren().get(0);
                        Label troopLabel = (Label) territoryBox.getChildren().get(1);

                        if (nameLabel.getText().equals(selectedHonnan)) {
                            int current = Integer.parseInt(troopLabel.getText());
                            troopLabel.setText(String.valueOf(current - selectedMennyi));
                        } else if (nameLabel.getText().equals(selectedHova)) {
                            int current = Integer.parseInt(troopLabel.getText());
                            troopLabel.setText(String.valueOf(current + selectedMennyi));
                        }
                    }
                }
            }

            lepesButton.setDisable(true);
        });
    }

    /**
     * A védekezés lehetőségét biztosítja, amely akkor jelenik meg, amikor a játékos másik játékos támadását várja.
     * A védekező játékos eldöntheti, hány egységgel kíván védekezni.
     *
     * @param ki A támadó játékos ID-ja.
     * @param adat_honnan A támadás kiinduló területe.
     * @param adat_hova A támadás célzott területe.
     * @param adat_mennyi A támadott egységek száma.
     * @param vedekezok A védekező egységek maximális száma.
     */
    public void vedekezes(Integer ki, String adat_honnan, String adat_hova, Integer adat_mennyi, Integer vedekezok) {
        Platform.runLater(() -> {
            teruletText.setText("Player " + ki + " megtámadta " + adat_honnan + "-ból/ből " + adat_hova + " nevű területedet " + adat_mennyi + " katonával.\n" +
                    "Döntsd el hány egységgel szeretnél ellene védekezni!");

            lepes.setValue("Csata");
            lepes.setDisable(true);
            honnan.setValue(adat_hova);
            honnan.setDisable(true);
            hova.setValue(adat_honnan);
            hova.setDisable(true);

            mennyi.setDisable(false);
            lepesButton.setDisable(false);

            setMennyiseg(vedekezok+1);

            // Add flag to determine we're in defense mode
            lepesButton.setOnMouseClicked(event -> {
                Integer selectedMennyi = (Integer) mennyi.getValue();
                if (selectedMennyi != null) {
                    c.vedekezesAction(ki, adat_honnan, adat_hova, adat_mennyi, clientID, selectedMennyi);
                    lepesButton.setDisable(true);
                    teruletText.setText("Védekezés elküldve, várakozás...");
                }
            });
        });
    }

    /**
     * Frissíti a támadás eredményét, beleértve a kockadobásokat, a győzelmeket és vereségeket.
     * A támadó és védő kockadobásait is megjeleníti, és tükrözi a csata kimenetelét.
     * Ezt a függvényt akkor használjuk, amikor a csata kimenetele eldőlt.
     *
     * @param tamado A támadó dobásainak listája.
     * @param vedo A védő dobásainak listája.
     * @param eredmeny A csata eredménye (győzelmek és vereségek száma).
     * @param isFog Igaz, ha a támadó elvesztette a területet, hamis, ha nem.
     */
    public void updateVedekezo(List<Integer> tamado, List<Integer> vedo, List<Integer> eredmeny, boolean isFog) {
        Platform.runLater(() -> {
            String s = "A kockadobások eredményei:\n" +
                    "\t támadó dobásai: ";
            for (Integer i : tamado) {
                s = s + i + " ";
            }
            s = s + "\t védő dobásai: ";
            for (Integer i : vedo) {
                s = s + i + " ";
            }

            s = s + "\nA csata eredménye:\n" +
                    "\t győzelmek: " + eredmeny.get(0) +
                    "\t vereségek: " + eredmeny.get(1) + "\n";

            if (isFog) {
                s = s + "Sajnos elvesztetted ezt a területet";
            } else {
                s = s + "A terület nem lett elfoglalva";
            }

            teruletText.setText(s);
        });
    }

    /**
     * Frissíti a támadó csata eredményét. A támadó győzelmei és vereségei mellett frissíti a játékos
     * statisztikáit és a térképet, hogy tükrözze a csata kimenetelét.
     * Ezt akkor használjuk, amikor a támadás sikeres volt vagy elbukott.
     *
     * @param tamado A támadó dobásainak listája.
     * @param vedo A védő dobásainak listája.
     * @param eredmeny A csata eredménye (győzelmek és vereségek száma).
     * @param isFog Igaz, ha a támadó sikeresen elfoglalta a területet, hamis, ha nem.
     */
    public void updateTamado(List<Integer> tamado, List<Integer> vedo, List<Integer> eredmeny, boolean isFog) {
        Platform.runLater(() -> {
            String s = "A kockadobások eredményei:\n" +
                    "\t támadó dobásai: ";
            for (Integer i : tamado) {
                s = s + i + " ";
            }
            s = s + "\t védő dobásai: ";
            for (Integer i : vedo) {
                s = s + i + " ";
            }

            s = s + "\nA csata eredménye:\n" +
                    "\t győzelmek: " + eredmeny.get(1) +
                    "\t vereségek: " + eredmeny.get(0) + "\n";

            if (isFog) {
                s = s + "A terület elfoglalása sikeres volt!";
            } else {
                s = s + "A területet nem sikerült elfoglalni";
            }

            teruletText.setText(s);
            lepesButton.setDisable(false);
            honnan.setValue(null);
            hova.setValue(null);
            mennyi.setValue(null);
        });
    }

    /**
     * Frissíti a csata eredményét a felhasználói felületen, figyelembe véve a támadó és védő veszteségeket.
     * A térképen az elfoglalt és elvesztett területek számát is frissíti.
     * Ezt a függvényt akkor használjuk, amikor a csata befejeződött, és a területet sikeresen elfoglalták vagy elvesztették.
     *
     * @param tamadoID A támadó játékos ID-ja.
     * @param honnan A támadó kiinduló területe.
     * @param hova A támadó célzott területe.
     * @param vedoID A védő játékos ID-ja.
     * @param eredmeny A csata eredménye (győzelmek és vereségek száma).
     */
    public void updateCsataGUI(Integer tamadoID, String honnan, String hova, Integer vedoID, List<Integer> eredmeny) {
        Platform.runLater(() -> {
            int tamadoVeszteseg = eredmeny.get(0);
            int vedoVeszteseg = eredmeny.get(1);

            // Update player stats
            for (int id : new int[]{tamadoID, vedoID}) {
                int veszteseg = (id == tamadoID) ? tamadoVeszteseg : vedoVeszteseg;
                VBox playerBox = (VBox) playerStats.getChildren().get(id - 1);
                TitledPane statsPane = (TitledPane) playerBox.getChildren().get(1);
                VBox statsBox = (VBox) statsPane.getContent();

                for (Node node : statsBox.getChildren()) {
                    if (node instanceof Label label && label.getText().startsWith("Hadosztályok száma: ")) {
                        int current = Integer.parseInt(label.getText().replaceAll("\\D+", ""));
                        label.setText("Hadosztályok száma: " + (current - veszteseg));
                        break;
                    }
                }
            }

            // Update troop counts in map
            for (TitledPane continentPane : kontStats.getPanes()) {
                VBox continentBox = (VBox) continentPane.getContent();
                for (Node node : continentBox.getChildren()) {
                    if (node instanceof VBox territoryBox && territoryBox.getChildren().size() == 2) {
                        Label nameLabel = (Label) territoryBox.getChildren().get(0);
                        Label troopLabel = (Label) territoryBox.getChildren().get(1);
                        String name = nameLabel.getText();

                        if (name.equals(honnan)) {
                            int current = Integer.parseInt(troopLabel.getText());
                            troopLabel.setText(String.valueOf(current - tamadoVeszteseg));
                        } else if (name.equals(hova)) {
                            int current = Integer.parseInt(troopLabel.getText());
                            troopLabel.setText(String.valueOf(current - vedoVeszteseg));
                        }
                    }
                }
            }
        });
    }

    /**
     * Frissíti a csata eredményét és a térképen lévő területek számát, figyelembe véve a terület elfoglalásának sikerességét.
     * A statisztikák frissítése mellett az elfoglalt területek számát is frissíti.
     * Ezt akkor használjuk, amikor egy terület sikeresen elfoglalásra kerül vagy elveszik.
     *
     * @param tamadoID A támadó játékos ID-ja.
     * @param vedoID A védő játékos ID-ja.
     * @param adat_honnan A támadó kiinduló területe.
     * @param adat_hova A támadó célzott területe.
     * @param eredmeny A csata eredménye (győzelmek és vereségek száma).
     * @param tamadMennyi A támadó által használt egységek száma.
     */
    public void updateFoglalasGUI(Integer tamadoID, Integer vedoID, String adat_honnan, String adat_hova, List<Integer> eredmeny, Integer tamadMennyi) {
        Platform.runLater(() -> {
            int tamadoVeszteseg = eredmeny.get(0);
            int vedoVeszteseg = eredmeny.get(1);
            int vedoTerulet = -1;
            int tamadoTerulet = 1;

            // Update player stats
            for (int id : new int[]{tamadoID, vedoID}) {
                int veszteseg = (id == tamadoID) ? tamadoVeszteseg : vedoVeszteseg;
                int terulet = (id == tamadoID) ? tamadoTerulet : vedoTerulet;
                VBox playerBox = (VBox) playerStats.getChildren().get(id - 1);
                TitledPane statsPane = (TitledPane) playerBox.getChildren().get(1);
                VBox statsBox = (VBox) statsPane.getContent();

                for (Node node : statsBox.getChildren()) {
                    if (node instanceof Label label) {
                        if (label.getText().startsWith("Hadosztályok száma: ")) {
                            int current = Integer.parseInt(label.getText().replaceAll("\\D+", ""));
                            label.setText("Hadosztályok száma: " + (current - veszteseg));
                            if (current - veszteseg == 0) {
                                teruletText.setText("Kiestél!");
                                c.kieses();
                            }
                        } else if (label.getText().startsWith("Területek száma: ")) {
                            int current = Integer.parseInt(label.getText().replaceAll("\\D+", ""));
                            label.setText("Területek száma: " + (current + terulet));
                        }
                    }
                }
            }

            // Update troop counts in map
            for (TitledPane continentPane : kontStats.getPanes()) {
                VBox continentBox = (VBox) continentPane.getContent();
                for (Node node : continentBox.getChildren()) {
                    if (node instanceof VBox territoryBox && territoryBox.getChildren().size() == 2) {
                        Label nameLabel = (Label) territoryBox.getChildren().get(0);
                        Label troopLabel = (Label) territoryBox.getChildren().get(1);
                        String name = nameLabel.getText();

                        if (name.equals(adat_honnan)) {
                            int current = Integer.parseInt(troopLabel.getText());
                            troopLabel.setText(String.valueOf(current - tamadMennyi));
                        } else if (name.equals(adat_hova)) {
                            troopLabel.setText(String.valueOf(tamadMennyi - tamadoVeszteseg));
                            if (tamadoID == 1) {
                                troopLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            } else if (tamadoID == 2) {
                                troopLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            } else if (tamadoID == 3 && playerNum == 3) {
                                troopLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            } else {
                                troopLabel.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Eltávolít egy megadott terület nevet a myTeruletek ListView aktuális elemei közül.
     *
     * @param elem A terület neve, amelyet el szeretnénk távolítani a listából.
     */
    public void removeTerulet(String elem) {
        Platform.runLater(() -> {
            ObservableList<String> items = myTeruletek.getItems();
            items.remove(elem);
        });
    }

    /**
     * Hozzáad egy új megadott területet a myTeruletek ListView aktuális elemeihez, ha még nem szerepel benne.
     *
     * @param elem A terület neve, amelyet hozzá szeretnénk adni a listához.
     */
    public void addTerulet(String elem) {
        Platform.runLater(() -> {
            ObservableList<String> items = myTeruletek.getItems();
            if (!items.contains(elem)) {
                items.add(elem);
            }
        });
    }

    /**
     * Megjelenít egy információs ablakot, amely jelzi a játékos győzelmét.
     * Az ablak címe és tartalma tájékoztat a játék végéről és gratulál a játékosnak.
     * Az ablak bezárása után a program bezárul.
     */
    public void nyert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(List.of(ButtonType.OK));
        alert.setTitle("Nyertél");
        alert.setHeaderText("Gratulálok, nyertél!");
        alert.setContentText("A játéknak vége");
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * Megjelenít egy információs ablakot, amely jelzi, hogy a játékos kiesett a játékból.
     * Az ablak címe és tartalma figyelmezteti a játékost a játék végét jelentő eseményre.
     * Az ablak bezárása után a program bezárul.
     */
    public void kiesett() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(List.of(ButtonType.OK));
        alert.setTitle("Kiestél");
        alert.setHeaderText("Sajnálom, kiestél a játékból");
        alert.setContentText("Számodra a játék véget ért");
        alert.showAndWait();
        Platform.exit();
    }
}