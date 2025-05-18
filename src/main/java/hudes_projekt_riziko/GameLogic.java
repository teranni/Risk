package hudes_projekt_riziko;

import hudes_projekt_riziko.gameElements.*;
import java.util.*;

import static hudes_projekt_riziko.gameElements.TitkosKuldi.Missions_2;
import static hudes_projekt_riziko.gameElements.TitkosKuldi.Missions_3;

/**
 * A játékmenet logikáját kezelő osztály. Ez az osztály felelős a játékosok elhelyezéséért,
 * a területek kezeléséért, a támadások és védekezések lebonyolításáért, valamint a kártyák
 * kezeléséért. Az osztály lehetőséget biztosít a játékmenet szabályainak implementálására,
 * mint a támadások kimenetele, a területek megszerzése és a játékosok erőforrásainak kezelése.
 */
public class GameLogic {
    Integer playerNum;  // A játékosok száma
    Integer hadosztalySum;  // A játékosok összes hadosztályának száma
    List<Jatekos> jatekosok;  // A játékosok listája
    List<Kartya> pakli = new ArrayList<>();  // A játék kártyáinak paklija

    List<Terulet> teruletek = new ArrayList<>();  // A játéktér területeinek listája
    List<Kontinens> vilag = new ArrayList<>();  // A játéktér kontinenseinek listája

    /**
     * Konstruktor, amely inicializálja a játékosok számát és a játékhoz szükséges alapadatokat.
     *
     * @param playerNum A játékosok száma.
     * @param jatekosok A játékosok listája.
     */
    public GameLogic(Integer playerNum, List<Jatekos> jatekosok) {
        this.playerNum = playerNum;
        if (playerNum == 2) {
            hadosztalySum = 40;
        } else {
            hadosztalySum = 35;
        }

        this.jatekosok = jatekosok;
        if (jatekosok.size() == 2) {
            this.jatekosok.add(new Jatekos(3));
        }
    }

    /**
     * A játéktér és a játékosok beállítását végzi el. A játékosok területekhez kerülnek,
     * a játékosok kezdő hadosztályainak száma és küldetései beállításra kerülnek.
     */
    public void setup() {
        // A világ és a területek inicializálása
        vilag = Setup.setup(pakli, teruletek);

        Random rand = new Random();

        List<Terulet> maradek = new ArrayList<>();
        for (Kontinens kontinens : vilag) {
            List<Terulet> kont_ter = new ArrayList<>(kontinens.getTeruletek());
            Collections.shuffle(kont_ter, rand);

            boolean a;
            if (kont_ter.size() % 3 == 0) {
                a = false;
            } else {
                a = true;
            }

            for (int i = 0; i < kont_ter.size(); i++) {
                if (a && i == kont_ter.size() - 1) {
                    maradek.add(kont_ter.get(i));
                } else {
                    Jatekos jatekos = jatekosok.get(i % 3);
                    Terulet t = kont_ter.get(i);
                    jatekos.addTerulet(t);
                    t.setJatekos(jatekos);
                    t.addKatona(1);
                }
            }
        }

        // A maradék területek elosztása
        for (int i = 0; i < maradek.size(); i++) {
            Jatekos jatekos = jatekosok.get(i);
            Terulet t = maradek.get(i);
            jatekos.addTerulet(t);
            t.setJatekos(jatekos);
            t.addKatona(1);
        }

        // Ezen a ponton minden játékosnak 14 területe van és minden területen 1-1 katona
        // A játékosok küldetéseinek és hadosztályainak beállítása
        Random rand2 = new Random();
        for (Jatekos jatekos : jatekosok) {
            if (playerNum == 2) {
                jatekos.setKuldetes(Missions_2);
            } else {
                int index = new Random().nextInt(Missions_3.size());
                jatekos.setKuldetes(Missions_3.remove(index));
            }

            Set<Terulet> teruletei = jatekos.getTeruletek();
            List<Terulet> sajatTeruletek = new ArrayList<>(teruletei);

            jatekos.setHadosztalyNum(hadosztalySum);
            int megmaradt = hadosztalySum - sajatTeruletek.size(); // 26 vagy 21

            // Hadosztályok elosztása
            while (megmaradt > 0) {
                Terulet cel = sajatTeruletek.get(rand2.nextInt(sajatTeruletek.size()));
                cel.addKatona(1);
                megmaradt--;
            }
        }
    }

    /**
     * Visszaadja a játékosok listáját.
     *
     * @return A játékosok listája.
     */
    public List<Jatekos> getJatekosok() {
        return jatekosok;
    }

    /**
     * Visszaadja a kontinensek listáját.
     *
     * @return A kontinensek listája.
     */
    public List<Kontinens> getKontinensek() {
        return vilag;
    }

    /**
     * A játékos erőforrásait (katonák, területek és kontinens értékek) összesíti.
     *
     * @param jatekos A vizsgált játékos.
     * @return Az összesített erőforrások száma.
     */
    public Integer erosites(Jatekos jatekos) {
        // teruletek szerint
        int a = 0;
        if (jatekos.getTeruletek().size() / 3 < 3) {
            a += 3;
        } else {
            a += jatekos.getTeruletek().size() / 3;
        }

        // kontinens szerint
        for (Kontinens k : jatekos.getKontinensek()) {
            a += k.getErtek();
        }

        return a;
    }

    /**
     * Új terület hozzáadása a játékhoz, egy játékoshoz és annak területeihez.
     *
     * @param ID A játékos ID-ja.
     * @param teruletNev A hozzáadott terület neve.
     */
    public void addTerulet(Integer ID, String teruletNev) {
        Jatekos j = jatekosok.get(ID - 1);
        j.addHadosztalyNum(1);

        for (Terulet t : j.getTeruletek()) {
            if (t.getName().equals(teruletNev)) {
                t.addKatona(1);
            }
        }
    }

    /**
     * Visszaadja a saját területek listáját egy adott terület szomszédairól.
     *
     * @param ID A játékos ID-ja.
     * @param teruletNev A vizsgált terület neve.
     * @return A saját területek listája.
     */
    public List<String> getSajat(Integer ID, String teruletNev) {
        Jatekos j = jatekosok.get(ID - 1);
        Terulet ter = null;
        List<String> sajat = new ArrayList<>();

        for (Terulet t : j.getTeruletek()) {
            if (t.getName().equals(teruletNev)) {
                ter = t;
                break;
            }
        }

        for (Terulet t : ter.getSzomszedok()) {
            if (t.getJatekos().getID() == ID) {
                sajat.add(t.getName());
            }
        }

        return sajat;
    }

    /**
     * Visszaadja az ellenfél területeit egy adott terület szomszédairól.
     *
     * @param ID A játékos ID-ja.
     * @param teruletNev A vizsgált terület neve.
     * @return Az ellenfél területeinek listája.
     */
    public List<String> getEllenfel(Integer ID, String teruletNev) {
        Jatekos j = jatekosok.get(ID - 1);
        Terulet ter = null;
        List<String> ellen = new ArrayList<>();

        for (Terulet t : j.getTeruletek()) {
            if (t.getName().equals(teruletNev)) {
                ter = t;
            }
        }

        for (Terulet t : ter.getSzomszedok()) {
            if (t.getJatekos().getID() != ID) {
                ellen.add(t.getName());
            }
        }

        return ellen;
    }

    /**
     * Visszaadja egy terület katonáinak számát.
     *
     * @param territory A vizsgált terület neve.
     * @return A terület katonáinak száma.
     */
    public Integer getMennyiseg(String territory) {
        for (Jatekos j : jatekosok) {
            for (Terulet t : j.getTeruletek()) {
                if (t.getName().equals(territory)) {
                    return t.getKatonak();
                }
            }
        }
        return 0;
    }

    /**
     * Katonák áthelyezése két terület között.
     *
     * @param ID A játékos ID-ja.
     * @param honnan A kiinduló terület neve.
     * @param hova A cél terület neve.
     * @param mennyi A áthelyezett katonák száma.
     */
    public void athelyezes(int ID, String honnan, String hova, int mennyi) {
        Jatekos j = jatekosok.get(ID - 1);

        for (Terulet t : j.getTeruletek()) {
            if (t.getName().equals(honnan)) {
                t.addKatona(-mennyi);
            }
            if (t.getName().equals(hova)) {
                t.addKatona(mennyi);
            }
        }
    }

    /**
     * A védekező játékos ID-ját adja vissza a terület alapján.
     *
     * @param hova A terület neve.
     * @return A védekező játékos ID-ja.
     */
    public Integer getVedoID(String hova) {
        for (Jatekos j : jatekosok) {
            for (Terulet t : j.getTeruletek()) {
                if (t.getName().equals(hova)) {
                    return j.getID();
                }
            }
        }

        return 0;
    }

    /**
     * Visszaadja egy terület védő katonáinak számát.
     *
     * @param hova A terület neve.
     * @return A védekező katonák száma.
     */
    public Integer getVedekzok(String hova) {
        for (Jatekos j : jatekosok) {
            for (Terulet t : j.getTeruletek()) {
                if (t.getName().equals(hova)) {
                    return t.getKatonak();
                }
            }
        }

        return 0;
    }

    /**
     * Dob egy kockával és visszaadja az eredményt.
     *
     * @return A dobás eredménye (1-6).
     */
    public int rollDice() {
        Random rand = new Random();
        return rand.nextInt(6) + 1; // 0-5 + 1 => 1-6
    }

    /**
     * A támadó dobásait végzi el.
     *
     * @param tamadMennyi A támadók száma.
     * @return A támadó kockák eredményeinek listája.
     */
    public List<Integer> tamadoDobas(int tamadMennyi) {
        List<Integer> tamado_kocka = new ArrayList<>();

        if (tamadMennyi <= 3) {
            for (int i = 0; i < tamadMennyi; i++) {
                tamado_kocka.add(rollDice());
            }
        } else {
            for (int i = 0; i < 3; i++) {
                tamado_kocka.add(rollDice());
            }
        }
        Collections.sort(tamado_kocka, Collections.reverseOrder());
        return tamado_kocka;
    }

    /**
     * A védő dobásait végzi el.
     *
     * @param vedoMennyi A védők száma.
     * @return A védő kockák eredményeinek listája.
     */
    public List<Integer> vedoDobas(int vedoMennyi) {
        List<Integer> vedo_kocka = new ArrayList<>();

        if (vedoMennyi <= 2) {
            for (int i = 0; i < vedoMennyi; i++) {
                vedo_kocka.add(rollDice());
            }
        } else {
            for (int i = 0; i < 2; i++) {
                vedo_kocka.add(rollDice());
            }
        }
        Collections.sort(vedo_kocka, Collections.reverseOrder());
        return vedo_kocka;
    }

    /**
     * Az eredményeket összevetve meghatározza a támadó és védő veszteségeit.
     *
     * @param tamado_kocka A támadó kockáinak eredményei.
     * @param vedo_kocka A védő kockáinak eredményei.
     * @return A támadó és védő veszteségeinek listája (támadó veszteség, védő veszteség).
     */
    public List<Integer> eredmeny(List<Integer> tamado_kocka, List<Integer> vedo_kocka) {
        List<Integer> eredmeny = new ArrayList<>();

        int db = 0;
        if (tamado_kocka.size() < vedo_kocka.size()) {
            db = tamado_kocka.size();
        } else {
            db = vedo_kocka.size();
        }

        int tlose = 0;
        int vlose = 0;
        for (int i = 0; i < db; i++) {
            if (tamado_kocka.get(i) > vedo_kocka.get(i)) {
                vlose++;
            } else {
                tlose++;
            }
        }

        eredmeny.add(tlose);
        eredmeny.add(vlose);
        return eredmeny;
    }

    /**
     * Ellenőrzi, hogy a támadó elfoglalhatja-e a területet.
     *
     * @param hova A terület neve.
     * @param vedoID A védő játékos ID-ja.
     * @param vlose A védő veszteségeinek száma.
     * @return Igaz, ha a támadó elfoglalhatja a területet.
     */
    public boolean isFoglalas(String hova, int vedoID, int vlose) {
        Terulet ter = null;

        for (Terulet t : jatekosok.get(vedoID - 1).getTeruletek()) {
            if (t.getName().equals(hova)) {
                ter = t;
            }
        }

        boolean foglalas = false;
        if (ter.getKatonak() - vlose == 0) {
            foglalas = true;
        }

        return foglalas;
    }

    /**
     * A támadás végrehajtása és a kártya húzása.
     *
     * @param tamadoId A támadó játékos ID-ja.
     * @param hova A célzott terület neve.
     * @param tamadMennyi A támadó katonák száma.
     * @param vedoID A védő játékos ID-ja.
     * @param eredmeny A támadás és védekezés eredményei.
     * @return Az új kártya, amit a támadó kap.
     */
    public Kartya foglalas(int tamadoId, String hova, int tamadMennyi, int vedoID, List<Integer> eredmeny) {
        Jatekos jatek = jatekosok.get(tamadoId-1);
        Terulet ter = null;
        for (Jatekos j : jatekosok) {
            for (Terulet t : j.getTeruletek()) {
                if (t.getName().equals(hova)) {
                   ter = t;
                }
            }
        }

        ter.setJatekos(jatek);
        ter.setKatonak(tamadMennyi - eredmeny.get(0));
        jatek.addHadosztalyNum(-eredmeny.get(0));
        jatek.addTerulet(ter);
        Kartya k = kartyaHuzas();
        jatek.addKartya(k);

        Jatekos vesztes = jatekosok.get(vedoID-1);
        vesztes.addHadosztalyNum(-eredmeny.get(1));
        vesztes.removeTerulet(ter);

        return k;
    }

    /**
     * A támadás lebonyolítása, a támadó és a védő területének frissítése.
     *
     * @param tamadoId A támadó játékos ID-ja.
     * @param honnan A támadó terület neve.
     * @param hova A védett terület neve.
     * @param vedoId A védő játékos ID-ja.
     * @param eredmeny A támadás és védekezés eredményei.
     */
    public void tamadas(int tamadoId, String honnan, String hova, int vedoId, List<Integer> eredmeny) {
        Jatekos j1 = jatekosok.get(tamadoId-1);
        Terulet t1 = null;
        for (Terulet t : j1.getTeruletek()) {
            if (t.getName().equals(honnan)) {
                t1 = t;
            }
        }

        j1.addHadosztalyNum(-eredmeny.get(0));
        t1.addKatona(-eredmeny.get(0));

        Jatekos j2 = jatekosok.get(vedoId-1);
        Terulet t2 = null;
        for (Terulet t : j2.getTeruletek()) {
            if (t.getName().equals(hova)) {
                t2 = t;
            }
        }

        j2.addHadosztalyNum(-eredmeny.get(1));
        t2.addKatona(-eredmeny.get(1));
    }

    /**
     * A pakliból egy véletlenszerű kártyát húz és visszaadja azt.
     * A húzott kártyát eltávolítja a pakliból.
     *
     * @return A pakliból húzott kártya.
     */
    public Kartya kartyaHuzas() {
        int index = new Random().nextInt(pakli.size());
        return pakli.remove(index);
    }

    /**
     * Visszaállít egy kártyát a megadott szöveges leírás alapján.
     * A leírás tartalmazza a kártya típusát és a hozzá tartozó hadosztály típusát.
     * Ha a leírás "Joker kártya", akkor egy Joker kártyát hoz létre.
     * Ha nem, akkor a formátumnak "Terület: X, Hadosztály típus: Y"-nak kell lennie,
     * ahol X a terület neve, Y pedig a hadosztály típusa.
     *
     * @param str A kártya leírása szöveges formátumban.
     * @return A létrehozott kártya, amely a leírásnak megfelelően van inicializálva.
     * @throws IllegalArgumentException Ha a leírás formátuma érvénytelen vagy ismeretlen hadosztály típust tartalmaz.
     */
    public Kartya kartyaBack(String str) {
        if (str.equals("Joker kártya")) {
            return new Kartya("joker", null);
        }

        // Expected format: "Terület: X, Hadosztály típus: Y"
        String[] parts = str.split(", ");
        String terulet = parts[0].split(": ")[1];
        String tipusStr = parts[1].split(": ")[1];

        Hadosztaly tipus = Hadosztaly.valueOf(tipusStr.toUpperCase());

        return new Kartya(terulet, tipus);
    }
}