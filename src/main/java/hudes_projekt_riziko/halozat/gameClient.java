package hudes_projekt_riziko.halozat;
;
import hudes_projekt_riziko.gui.GameController;
import hudes_projekt_riziko.gui.SetupController;
import hudes_projekt_riziko.gui.WaitingController;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * A játékos kliensét reprezentáló osztály, amely kommunikál a szerverrel.
 * Ez az osztály kezeli a hálózati kommunikációt a szerverrel, elküldi és fogadja a játékhoz kapcsolódó adatokat,
 * valamint frissíti a felhasználói felületet a megfelelő információk alapján.
 */
public class gameClient implements Runnable {
    protected Socket clientSocket;
    Integer playerID;
    public final CountDownLatch startSignal = new CountDownLatch(1);
    public final CountDownLatch appSignal = new CountDownLatch(1);
    public final CountDownLatch controllerReady = new CountDownLatch(1);
    GameController controller;
    WaitingController wait_cont;
    SetupController setup_cont;
    BufferedReader serverInput;
    PrintWriter serverOutput;

    /**
     * Létrehoz egy új klienst, amely a megadott hosztra csatlakozik.
     *
     * @param host a játék szerverének hosztneve vagy IP címe
     * @throws UnknownHostException ha nem található a megadott hoszt
     * @throws IOException ha I/O hiba történik a socket létrehozásakor
     */
    public gameClient(String host) throws UnknownHostException, IOException {
        clientSocket = new Socket(host, gameServer.PORT_NUMBER);
        serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        serverOutput = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    private Runnable runna;

    /**
     * Beállítja a futtatható feladatot, amelyet a kliens készenléti állapotba kerülésekor kell végrehajtani.
     *
     * @param runnable a feladat, amelyet a kliens készenléti állapotba kerülésekor futtatni kell
     */
    public void setRunna(Runnable runnable) {
        runna = runnable;
    }

    /**
     * Lezárja a kapcsolatot a szerverrel.
     *
     * @throws IOException ha I/O hiba történik a socket bezárásakor
     */
    public void close() throws IOException {
        clientSocket.close();
    }

    /**
     * Beállítja a játék vezérlőt a kliens számára.
     *
     * @param controller a beállítandó játék vezérlő
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Beállítja a várakozó vezérlőt a kliens számára.
     *
     * @param controller a beállítandó várakozó vezérlő
     */
    public void setWaitController(WaitingController controller) {
        this.wait_cont = controller;
    }

    /**
     * Beállítja a beállító vezérlőt a kliens számára.
     *
     * @param controller a beállítandó beállító vezérlő
     */
    public void setSetupController(SetupController controller) {
        this.setup_cont = controller;
    }

    /**
     * Kérést küld a szervernek egy új terület hozzáadására a játékos számára.
     *
     * @param ID        a játékos ID-ja
     * @param territory a hozzáadandó terület neve
     */
    public void addTerulet(Integer ID, String territory) {
        serverOutput.println("ADD");
        serverOutput.println(ID);
        serverOutput.println(territory);
    }

    /**
     * Kérést küld a szervernek egy elérhető területek listájának lekérésére a játékos számára.
     *
     * @param ID        a játékos ID-ja
     * @param territory a terület neve, amelyhez a lista kérhető
     */
    public void athelyezes(Integer ID, String territory) {
        serverOutput.println("ATHELYEZES_LISTA");
        serverOutput.println(ID);
        serverOutput.println(territory);
    }

    /**
     * Kérést küld a szervernek egy támadható területek listájának lekérésére a játékos számára.
     *
     * @param ID        a játékos ID-ja
     * @param territory a terület neve, amelyhez a támadási lista kérhető
     */
    public void tamadas(Integer ID, String territory) {
        serverOutput.println("TAMADAS_LISTA");
        serverOutput.println(ID);
        serverOutput.println(territory);
    }

    /**
     * Kérést küld a szervernek a területen lévő katonák számának lekérésére.
     *
     * @param territory a terület neve, amelyhez a katonák számát kérjük
     */
    public void mennyiseg(String territory) {
        serverOutput.println("MENNYISEG");
        serverOutput.println(territory);
    }

    /**
     * Kérést küld a szervernek egy mozgási akció végrehajtására.
     *
     * @param ID        a játékos ID-ja
     * @param honnan    az induló terület
     * @param hova      a cél terület
     * @param mennyi    a mozgó katonák száma
     */
    public void athelyezesAction(Integer ID, String honnan, String hova, Integer mennyi) {
        serverOutput.println("ATHELYEZES");
        serverOutput.println(ID);
        serverOutput.println(honnan);
        serverOutput.println(hova);
        serverOutput.println(mennyi);
    }

    /**
     * Kérést küld a szervernek egy támadási akció végrehajtására.
     *
     * @param ID        a játékos ID-ja
     * @param honnan    az induló terület
     * @param hova      a cél terület
     * @param mennyi    a támadó katonák száma
     */
    public void tamadasAction(Integer ID, String honnan, String hova, Integer mennyi) {
        serverOutput.println("TAMADAS_SZANDEK");
        serverOutput.println(ID);
        serverOutput.println(honnan);
        serverOutput.println(hova);
        serverOutput.println(mennyi);
    }

    /**
     * Kérést küld a szervernek egy védekezési akció végrehajtására.
     *
     * @param tamado_ID a támadó ID-ja
     * @param honnan    a támadó terület
     * @param hova      a cél terület
     * @param tamad_mennyi a támadó katonák száma
     * @param vedo_ID   a védekező ID-ja
     * @param vedo_mennyi a védekező katonák száma
     */
    public void vedekezesAction(Integer tamado_ID, String honnan, String hova, Integer tamad_mennyi, Integer vedo_ID, Integer vedo_mennyi) {
        serverOutput.println("TAMADAS");
        serverOutput.println(tamado_ID);
        serverOutput.println(honnan);
        serverOutput.println(hova);
        serverOutput.println(tamad_mennyi);
        serverOutput.println(vedo_ID);
        serverOutput.println(vedo_mennyi);
    }

    /**
     * Kérést küld a szervernek a következő játékos lépésének végrehajtására.
     */
    public void next() {
        serverOutput.println("NEXT");
    }

    /**
     * Kérést küld a szervernek egy kártya visszavonására.
     *
     * @param kartya a visszavonandó kártya
     */
    public void backKartya(String kartya) {
        serverOutput.println("BACKKARTYA");
        serverOutput.println(kartya);
    }

    /**
     * Kérést küld a szervernek, amivel jelzi, hogy az adott játékos kiesett.
     */
    public void kieses() {
        serverOutput.println("KIESETT");
    }

    /**
     * Kezeli a szerverrel való kommunikációt és a játék logikát.
     * Ez a metódus figyeli a szerver válaszait és ennek megfelelően frissíti a játék állapotát.
     */
    @Override
    public void run() {
        try {
            Integer s = Integer.parseInt(serverInput.readLine());

            if (s == 1) {
                Platform.runLater(runna);
                playerID = 1;
                startSignal.await();
                serverOutput.println(setup_cont.getPlayerNum().charAt(0));
            } else {
                playerID = s;
                setup_cont.setPlayerNum(serverInput.readLine());
                Platform.runLater(runna);
                startSignal.await();
            }

            serverOutput.println(1);

            // start
            serverInput.readLine();
            wait_cont.kezdes();
            controllerReady.await();

            controller.setPlayersNum(Integer.parseInt(serverInput.readLine()));
            int ID = Integer.parseInt(serverInput.readLine());
            controller.setClientID(ID);

            for (int i = 0; i < 3; i++) {
                int terSum = Integer.parseInt(serverInput.readLine());
                int kontSum = Integer.parseInt(serverInput.readLine());
                int hadSum = Integer.parseInt(serverInput.readLine());
                int pakliSum = Integer.parseInt(serverInput.readLine());
                controller.setJatekosStat(i+1, terSum, kontSum, hadSum, pakliSum);

                if (i+1 == ID) {
                    List<String> teruletek = new ArrayList<>();
                    for (int j = 0; j < terSum; j++) {
                        teruletek.add(serverInput.readLine());
                    }
                    controller.setTeruletek(teruletek);

                    List<String> pakli = new ArrayList<>();
                    for (int j = 0; j < pakliSum; j++) {
                        pakli.add(serverInput.readLine());
                    }
                    controller.setKartyak(pakli);

                    controller.setKuldetes(serverInput.readLine());
                }
            }

            // Kontinensek
            int kontSum = Integer.parseInt(serverInput.readLine());
            Map<String, List<String>> vilag = new HashMap<>();
            for (int i = 0; i < kontSum; i++) {
                String nev = serverInput.readLine();

                int teruletSzam = Integer.parseInt(serverInput.readLine());
                List<String> teruletek = new ArrayList<>();
                for (int j = 0; j < teruletSzam; j++) {
                    teruletek.add(serverInput.readLine());
                }

                vilag.put(nev, teruletek);
            }
            controller.setKont(vilag);

            serverInput.readLine();

            while (! Thread.currentThread().isInterrupted()) {
                String response = serverInput.readLine();
                switch (response) {
                    case "TE_JOSSZ" -> {
                        int id = Integer.parseInt(serverInput.readLine());
                        int pont = Integer.parseInt(serverInput.readLine());
                        controller.setTrue();
                        controller.setAllapot(id, pont);
                    }
                    case "NEM" -> {
                        int id = Integer.parseInt(serverInput.readLine());
                        int pont = Integer.parseInt(serverInput.readLine());
                        controller.setFalse();
                        controller.setAllapot(id, pont);
                    }
                    case "VEDEKEZZ" -> {
                        Integer ki = Integer.parseInt(serverInput.readLine());
                        String honnan = serverInput.readLine();
                        String hova = serverInput.readLine();
                        Integer mennyi = Integer.parseInt(serverInput.readLine());
                        Integer vedekezok = Integer.parseInt(serverInput.readLine());
                        controller.vedekezes(ki, honnan, hova, mennyi, vedekezok);
                    }
                    case "NEUTRAL" -> {
                        Integer ki = Integer.parseInt(serverInput.readLine());
                        String honnan = serverInput.readLine();
                        String hova = serverInput.readLine();
                        Integer mennyi = Integer.parseInt(serverInput.readLine());
                        Integer vedekezok = Integer.parseInt(serverInput.readLine());

                        Random rand = new Random();
                        vedekezesAction(ki, honnan, hova, mennyi, 3, rand.nextInt(vedekezok) + 1);
                    }
                    case "TAMADAS" -> {
                        Integer tamado_ID = Integer.parseInt(serverInput.readLine());
                        String honnan = serverInput.readLine();
                        String hova = serverInput.readLine();
                        Integer tamad_mennyi = Integer.parseInt(serverInput.readLine());
                        Integer vedo_ID = Integer.parseInt(serverInput.readLine());
                        Integer vedo_mennyi = Integer.parseInt(serverInput.readLine());

                        int tamadoMeret = Integer.parseInt(serverInput.readLine());
                        List<Integer> tamado = new ArrayList<>();
                        for (int i = 0; i < tamadoMeret; i++) {
                            tamado.add(Integer.parseInt(serverInput.readLine()));
                        }
                        int vedoMeret = Integer.parseInt(serverInput.readLine());
                        List<Integer> vedo = new ArrayList<>();
                        for (int i = 0; i < vedoMeret; i++) {
                            vedo.add(Integer.parseInt(serverInput.readLine()));
                        }
                        int eredmenyMeret = Integer.parseInt(serverInput.readLine());
                        List<Integer> eredmeny = new ArrayList<>();
                        for (int i = 0; i < eredmenyMeret; i++) {
                            eredmeny.add(Integer.parseInt(serverInput.readLine()));
                        }

                        boolean isFog = Boolean.parseBoolean(serverInput.readLine());

                        if (vedo_ID == playerID) {
                            controller.updateVedekezo(tamado, vedo, eredmeny, isFog);
                        } else if (tamado_ID == playerID) {
                            controller.updateTamado(tamado, vedo, eredmeny, isFog);
                        }

                        if (isFog) {
                            controller.updateFoglalasGUI(tamado_ID, vedo_ID, honnan, hova, eredmeny, tamad_mennyi);
                        } else {
                            controller.updateCsataGUI(tamado_ID, honnan, hova, vedo_ID, eredmeny);
                        }

                        if (tamado_ID == playerID && isFog) {
                            // +hozzáadhat még katonákat opció

                            String kartya = serverInput.readLine();
                            controller.addKartya(kartya);
                        }

                    }
                    case "ADD" -> {
                        int id = Integer.parseInt(serverInput.readLine());
                        String t = serverInput.readLine();
                        controller.addSubmitUI(id, t);
                    }
                    case "ATHELYEZES" -> {
                        String honnan = serverInput.readLine();
                        String hova = serverInput.readLine();
                        Integer mennyi = Integer.parseInt(serverInput.readLine());
                        controller.lepesSubmitUI(honnan, hova, mennyi);
                    }
                    case "ATHELYEZES_LISTA" -> {
                        int i = Integer.parseInt(serverInput.readLine());
                        List<String> sajat = new ArrayList<>();
                        for (int j = 0; j < i; j++) {
                            sajat.add(serverInput.readLine());
                        }
                        controller.setAthelyezhetoTeruletek(sajat);
                    }
                    case "TAMADAS_LISTA" -> {
                        int i = Integer.parseInt(serverInput.readLine());
                        List<String> ellen = new ArrayList<>();
                        for (int j = 0; j < i; j++) {
                            ellen.add(serverInput.readLine());
                        }
                        controller.setTamadhatoTeruletek(ellen);
                    }
                    case "MENNYISEG" -> {
                        int i = Integer.parseInt(serverInput.readLine());
                        controller.setMennyiseg(i);
                    }
                    case "NYERT" -> {
                        controller.nyert();
                    }
                    case "KIESETT" -> {
                        controller.kiesett();
                    }
                }
            }

            close();
        } catch (IOException e) {
            System.err.println("Failed to communicate with server!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
