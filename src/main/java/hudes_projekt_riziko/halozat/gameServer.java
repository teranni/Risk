package hudes_projekt_riziko.halozat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A gameServer osztály felelős a játék szerverének kezeléséért, beleértve az új kliensek
 * fogadását, azok kezelőinek indítását, és a kapcsolatok kezelését.
 *
 * A szerver egy TCP alapú kapcsolatot használ, amely lehetővé teszi több játékos
 * csatlakozását és azok kezelését külön szálakon.
 */
public class gameServer implements Runnable {
    public static final int PORT_NUMBER = 10612;  // A szerver portjának száma
    protected ServerSocket serverSocket;  // A szerver socket-je
    static List<GameHandler> handlerek = new ArrayList<>();  // A játékosok kezelőinek listája

    /**
     * A gameServer konstruktorának segítségével létrejön egy új szerver socket,
     * amely a megadott porton fogadja a kapcsolatokat.
     *
     * @throws IOException ha nem sikerül létrehozni a serverSocket-et
     */
    public gameServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
    }

    /**
     * Lezárja a szerver socket-jét.
     *
     * @throws IOException ha nem sikerül bezárni a socket-et
     */
    public void close() throws IOException {
        serverSocket.close();
    }

    /**
     * Visszaadja a játékosok kezelését végző handler-t a megadott index alapján.
     *
     * @param i a handler indexe
     * @return a megfelelő GameHandler objektum
     */
    public static GameHandler getHandler(int i) {
        return handlerek.get(i);
    }

    /**
     * A szerver szálának futtatása, amely folyamatosan figyeli a bejövő kapcsolatokat,
     * és minden új klienshez egy új GameHandler szálat indít.
     *
     * A szerver folyamatosan figyelni fogja a kapcsolatokat, és minden új kapcsolatot
     * kezelni fog a GameHandler objektumokkal.
     */
    @Override
    public void run() {
        try {
            while (! Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                try {
                    GameHandler handler = new GameHandler(clientSocket);
                    handler.start();
                    handlerek.add(handler);
                } catch (IOException e) {
                    System.err.println("Failed to communicate with client!");
                }
            }
        } catch (IOException e) {
            System.out.println("Accept failed!");
        }

        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("A Rizikó játékszerver leáll");
    }

    /**
     * A játékszerver indítása.
     *
     * Elindítja a szerver szálat és megjeleníti az elindulásról szóló üzenetet.
     *
     * @param args parancssori argumentumok (nem használtak a példában)
     */
    public static void main(String[] args) {
        try {
            new Thread(new gameServer()).start();
            System.out.println("A Rizikó játékszerver elindult");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
