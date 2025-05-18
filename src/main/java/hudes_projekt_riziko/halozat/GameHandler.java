package hudes_projekt_riziko.halozat;

import hudes_projekt_riziko.GameLogic;
import hudes_projekt_riziko.gameElements.Jatekos;
import hudes_projekt_riziko.gameElements.Kartya;
import hudes_projekt_riziko.gameElements.Kontinens;
import hudes_projekt_riziko.gameElements.Terulet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 * A GameHandler osztály felelős a játékosokkal való kommunikáció kezeléséért a játék szerverén.
 * Minden új játékos számára külön szálat hoz létre a szerver, amely a játék során folyamatosan
 * figyeli a bejövő parancsokat, kommunikál a klienssel, és kezeli a játékmenethez szükséges műveleteket.
 */
public class GameHandler extends Thread {
    protected Socket clientSocket;  // A klienshez tartozó socket
    protected BufferedReader clientReader;  // Kliens olvasója (input stream)
    protected PrintWriter clientWriter;  // Kliens írója (output stream)
    static List<Jatekos> jatekosok = new ArrayList<>();  // Játékosok listája
    static Integer playerAvailable = 0;  // Elérhető játékosok száma
    static Integer playerNum = 0;  // Játékosok teljes száma
    static Integer kezdet = 0;  // A játékosok kezdési állapota
    static GameLogic risk;  // A játékmenetet irányító logikai objektum
    Jatekos jatekos;  // Az aktuális játékos

    /**
     * Konstruktor, amely a klienshez tartozó socket-et inicializálja, és létrehozza a bemeneti és kimeneti stream-eket.
     *
     * @param clientSocket A klienshez tartozó socket.
     * @throws IOException Ha nem sikerül létrehozni a stream-eket.
     */
    public GameHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.clientWriter = new PrintWriter(clientSocket.getOutputStream());
    }

    /**
     * Üzenet küldése a kliensnek.
     *
     * @param line A küldendő üzenet.
     * @throws IOException Ha nem sikerül küldeni az üzenetet.
     */
    protected void sendLine(String line) throws IOException {
        clientWriter.println(line);
        clientWriter.flush();
    }

    /**
     * Várakozás egy specifikus üzenet fogadására a kliensről.
     *
     * @param line Az elvárt üzenet.
     * @return true, ha az üzenet megegyezik az elvárttal, különben false.
     * @throws IOException Ha hiba történt az üzenet fogadásakor.
     */
    protected boolean expect(String line) throws IOException {
        String clientLine = clientReader.readLine();
        return clientLine.equals(line);
    }

    protected void setKezdet(Integer kezdet) {
        if ((playerNum == 2 && kezdet == 2) || (playerNum == 3 && kezdet == 3)) {
            kezdet = 1;
        } else {
            kezdet++;
        }

        System.out.println(kezdet);
        this.kezdet = kezdet;
    }

    /**
     * A játékos szálának futtatása, amely kezeli a klienssel való kommunikációt,
     * beleértve a játékszabályok inicializálását, a játékosok listájának frissítését,
     * és a különböző játékállapotok kezelését.
     */
    @Override
    public void run() {
        System.out.println("Klienssel kommunikáció indul");

        try {
            synchronized (jatekosok) {
                if (!jatekosok.isEmpty()) {
                    while (playerAvailable < 1) {
                        jatekosok.wait();
                    }
                    playerAvailable--;
                    sendLine(String.valueOf(jatekosok.size()+1));
                    sendLine(String.valueOf(playerNum));
                    jatekosok.add(new Jatekos(jatekosok.size()+1));
                    jatekos = jatekosok.get(jatekosok.size()-1);
                } else {
                    jatekosok.add(new Jatekos(1));
                    jatekos = jatekosok.get(0);
                    sendLine(String.valueOf(1));

                    playerAvailable = Integer.parseInt(clientReader.readLine());
                    playerNum = playerAvailable;
                    playerAvailable--;
                    jatekosok.notifyAll();
                }
            }

            clientReader.readLine();

            synchronized (jatekosok) {
                kezdet++;
                while (kezdet < playerNum) {
                    jatekosok.wait();
                }
                jatekosok.notifyAll();

                if (risk == null && jatekos.getID() == 1) {
                    risk = new GameLogic(playerNum, jatekosok);
                    risk.setup();
                    jatekosok = risk.getJatekosok();
                    jatekosok.notifyAll();
                } else {
                    while (risk == null) {
                        jatekosok.wait();
                    }
                }
            }

            // Setup information for UI
            sendLine("start");
            sendLine(String.valueOf(playerNum));
            sendLine(String.valueOf(jatekos.getID()));

            for (Jatekos j : jatekosok) {
                sendLine(String.valueOf(j.getTeruletek().size()));
                sendLine(String.valueOf(j.getKontinensek().size()));
                sendLine(String.valueOf(j.getHadosztalyNum()));
                sendLine(String.valueOf(j.getPakli().size()));

                if (j.getID() == jatekos.getID()) {
                    // kartyak, kuldetes, saját területek
                    for (Terulet t : j.getTeruletek()) {
                        sendLine(t.getName());
                    }

                    for (Kartya k : j.getPakli()) {
                        sendLine(k.getTeruletNev() + " " + k.getTipus());
                    }

                    sendLine(j.getKuldetes());
                }
            }

            sendLine(String.valueOf(risk.getKontinensek().size()));
            for (Kontinens k : risk.getKontinensek()) {
                sendLine(k.name());
                sendLine(String.valueOf(k.getTeruletek().size()));
                for (Terulet t : k.getTeruletek()) {
                    sendLine(t.getName() + ": " + t.getKatonak() + ": " + t.getJatekos().getID());
                }
            }

            sendLine("end");
            if (jatekos.getID() == 1) {
                kezdet = 1;
                sendLine("TE_JOSSZ");
            } else {
                sendLine("NEM");
            }
            sendLine(String.valueOf(1));
            sendLine(String.valueOf(risk.erosites(jatekos)));

            // Kommunikáció a játékmenet során
            while (! Thread.currentThread().isInterrupted()) {
                String command = clientReader.readLine();
                switch (command) {
                    case "TAMADAS" -> {
                        int tamado_ID = Integer.parseInt(clientReader.readLine());
                        String honnan = clientReader.readLine();
                        String hova = clientReader.readLine();
                        int tamad_mennyi = Integer.parseInt(clientReader.readLine());
                        int vedo_ID = Integer.parseInt(clientReader.readLine());
                        int vedo_mennyi = Integer.parseInt(clientReader.readLine());

                        List<Integer> tamado = risk.tamadoDobas(tamad_mennyi);
                        List<Integer> vedo = risk.vedoDobas(vedo_mennyi);
                        List<Integer> eredmeny = risk.eredmeny(tamado, vedo);
                        
                        boolean isFog = risk.isFoglalas(hova, vedo_ID, eredmeny.get(1));
                        Kartya kartya = null;
                        if (isFog) {
                             kartya = risk.foglalas(tamado_ID, hova, tamad_mennyi, vedo_ID, eredmeny);
                        } else {
                            risk.tamadas(tamado_ID, honnan, hova, vedo_ID, eredmeny);
                        }

                        for (GameHandler g : gameServer.handlerek) {
                            g.sendLine("TAMADAS");
                            g.sendLine(String.valueOf(tamado_ID));
                            g.sendLine(honnan);
                            g.sendLine(hova);
                            g.sendLine(String.valueOf(tamad_mennyi));
                            g.sendLine(String.valueOf(vedo_ID));
                            g.sendLine(String.valueOf(vedo_mennyi));

                            g.sendLine(String.valueOf(tamado.size()));
                            for (Integer i : tamado) {
                                g.sendLine(String.valueOf(i));
                            }
                            g.sendLine(String.valueOf(vedo.size()));
                            for (Integer i : vedo) {
                                g.sendLine(String.valueOf(i));
                            }
                            g.sendLine(String.valueOf(eredmeny.size()));
                            for (Integer i : eredmeny) {
                                g.sendLine(String.valueOf(i));
                            }
                            g.sendLine(String.valueOf(isFog));
                            
                            if (isFog && g.jatekos.getID() == tamado_ID) {
                                g.sendLine(kartya.toString());
                            }
                        }

                    }
                    case "TAMADAS_SZANDEK" -> {
                        int ID = Integer.parseInt(clientReader.readLine());
                        String honnan = clientReader.readLine();
                        String hova = clientReader.readLine();
                        int mennyi = Integer.parseInt(clientReader.readLine());

                        Integer vedo_ID = risk.getVedoID(hova);
                        Integer vedo_DB = risk.getVedekzok(hova);

                        if (vedo_ID == 3 && playerNum == 2) {
                            sendLine("NEUTRAL");
                            sendLine(String.valueOf(ID));
                            sendLine(honnan);
                            sendLine(hova);
                            sendLine(String.valueOf(mennyi));
                            sendLine(String.valueOf(vedo_DB));
                        } else {
                            GameHandler g = gameServer.getHandler(vedo_ID-1);
                            System.out.println(vedo_ID);

                            g.sendLine("VEDEKEZZ");
                            g.sendLine(String.valueOf(ID));
                            g.sendLine(honnan);
                            g.sendLine(hova);
                            g.sendLine(String.valueOf(mennyi));
                            g.sendLine(String.valueOf(vedo_DB));
                        }
                    }
                    case "ADD" -> {
                        int ID = Integer.parseInt(clientReader.readLine());
                        String t = clientReader.readLine();
                        risk.addTerulet(ID, t);

                        for (GameHandler g: gameServer.handlerek) {
                            g.sendLine("ADD");
                            g.sendLine(String.valueOf(ID));
                            g.sendLine(t);
                        }
                    }
                    case "ATHELYEZES" -> {
                        int ID = Integer.parseInt(clientReader.readLine());
                        String honnan = clientReader.readLine();
                        String hova = clientReader.readLine();
                        int mennyi = Integer.parseInt(clientReader.readLine());

                        risk.athelyezes(ID, honnan, hova, mennyi);

                        if ((playerNum == 2 && kezdet == 2) || (playerNum == 3 && kezdet == 3)) {
                            kezdet = 1;
                        } else {
                            kezdet++;
                        }

                        for (GameHandler g: gameServer.handlerek) {
                            g.sendLine("ATHELYEZES");
                            g.sendLine(honnan);
                            g.sendLine(hova);
                            g.sendLine(String.valueOf(mennyi));

                            if (g.jatekos.getID() == kezdet) {
                                g.sendLine("TE_JOSSZ");
                            } else {
                                g.sendLine("NEM");
                            }
                            g.sendLine(String.valueOf(kezdet));
                            g.sendLine(String.valueOf(risk.erosites(g.jatekos)));
                        }
                    }
                    case "NEXT" -> {
                        int k_prev = kezdet;

                        setKezdet(kezdet);
                        while (jatekosok.get(kezdet-1).isKiesett()) {
                            setKezdet(kezdet);
                        }

                        if (k_prev == kezdet) {
                            GameHandler g = gameServer.getHandler(kezdet-1);
                            g.sendLine("NYERT");
                        }

                        for (GameHandler g: gameServer.handlerek) {
                            if (g.jatekos.getID() == kezdet) {
                                g.sendLine("TE_JOSSZ");
                            } else {
                                g.sendLine("NEM");
                            }
                            g.sendLine(String.valueOf(kezdet));
                            g.sendLine(String.valueOf(risk.erosites(g.jatekos)));
                        }
                    }
                    case "ATHELYEZES_LISTA" -> {
                        int ID = Integer.parseInt(clientReader.readLine());
                        String t = clientReader.readLine();
                        List<String> sajat = risk.getSajat(ID, t);

                        sendLine("ATHELYEZES_LISTA");
                        sendLine(String.valueOf(sajat.size()));
                        for (String s : sajat) {
                            sendLine(s);
                        }
                    }
                    case "TAMADAS_LISTA" -> {
                        int ID = Integer.parseInt(clientReader.readLine());
                        String t = clientReader.readLine();
                        List<String> ellen = risk.getEllenfel(ID, t);

                        sendLine("TAMADAS_LISTA");
                        sendLine(String.valueOf(ellen.size()));
                        for (String s : ellen) {
                            sendLine(s);
                        }
                    }
                    case "MENNYISEG" -> {
                        String t = clientReader.readLine();
                        int i = risk.getMennyiseg(t);
                        sendLine("MENNYISEG");
                        sendLine(String.valueOf(i));
                    }
                    case "BACKKARTYA" -> {
                        String k = clientReader.readLine();
                    }
                    case "KIESETT" -> {
                        jatekos.setKiesett();
                        sendLine("KIESETT");
                    }
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("A klienssel a kommunikáció sikeres volt!");
    }
}
