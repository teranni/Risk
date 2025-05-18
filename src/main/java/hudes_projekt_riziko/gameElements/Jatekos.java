package hudes_projekt_riziko.gameElements;

import java.util.HashSet;
import java.util.Set;

/**
 * A Jatekos osztály, amely a Rizikó játékban szereplő játékost reprezentálja.
 * A játékosnak van egy azonosítója, küldetése, területei, kártyái, kontinensei és hadosztály száma.
 */
public class Jatekos {
    Integer ID; // A játékos egyedi azonosítója
    String kuldetes = ""; // A játékos küldetése
    Set<Terulet> teruletek; // A játékos birtokában lévő területek
    Set<Kartya> karytak; // A játékos birtokában lévő kártyák
    Set<Kontinens> kontinensek = new HashSet<>(); // A játékos birtokában lévő kontinensek
    Integer hadosztalyNum; // A játékos hadosztályának száma
    boolean kiesett = false;    // Játékban van-e még a játékos?

    /**
     * Konstruktor, amely inicializálja a játékost azonosítóval.
     *
     * @param ID A játékos egyedi azonosítója.
     */
    public Jatekos(Integer ID) {
        this.ID = ID;
        teruletek = new HashSet<>();
        karytak = new HashSet<>();
    }

    /**
     * Konstruktor, amely inicializálja a játékost azonosítóval és egy területekkel.
     *
     * @param ID A játékos egyedi azonosítója.
     * @param teruletek A játékos birtokában lévő területek.
     */
    public Jatekos(Integer ID, Set<Terulet> teruletek) {
        this.ID = ID;
        this.teruletek = teruletek;
    }

    /**
     * Hozzáad egy területet a játékos birtokolt területeihez.
     * Ha a játékos birtokolja az összes területet egy kontinensen, akkor az adott kontinens is a játékosé lesz.
     *
     * @param terulet A hozzáadni kívánt terület.
     */
    public void addTerulet(Terulet terulet) {
        teruletek.add(terulet);

        int a = 0;
        for (Terulet t : teruletek) {
            if (t.getKontinens() == terulet.getKontinens()) {
                a++;
            }
        }
        if (a == terulet.getKontinens().getTeruletek().size()) {
            kontinensek.add(terulet.getKontinens());
        }
    }

    /**
     * Eltávolít egy területet a játékos birtokolt területeiből.
     * Ha a terület eltávolításával a játékos nem birtokolja tovább az adott kontinenst, akkor a kontinens is eltávolításra kerül.
     *
     * @param terulet A törölni kívánt terület.
     */
    public void removeTerulet(Terulet terulet) {
        teruletek.remove(terulet);
        kontinensek.remove(terulet.getKontinens());
    }

    /**
     * Visszaadja a játékos birtokolt területeit.
     *
     * @return A játékos birtokolt területeinek halmaza.
     */
    public Set<Terulet> getTeruletek() {
        return teruletek;
    }

    /**
     * Beállítja hogy a játékos kiesett a játékból
     */
    public void setKiesett() {
        kiesett = true;
    }

    /**
     * Visszaadja hogy a játékos kiesett-e a játékból
     */
    public boolean isKiesett() {
        return kiesett;
    }

    /**
     * Beállítja a játékos küldetését.
     *
     * @param kuldetes A játékos küldetése.
     */
    public void setKuldetes(String kuldetes) {
        this.kuldetes = kuldetes;
    }

    /**
     * Visszaadja a játékos küldetését.
     *
     * @return A játékos küldetése.
     */
    public String getKuldetes() {
        return kuldetes;
    }

    /**
     * Visszaadja a játékos birtokolt kártyáit.
     *
     * @return A játékos birtokolt kártyáinak halmaza.
     */
    public Set<Kartya> getPakli() {
        return karytak;
    }

    /**
     * Hozzáad egy kártyát a játékos kártyáihoz.
     *
     * @param kartya A hozzáadni kívánt kártya.
     */
    public void addKartya(Kartya kartya) {
        karytak.add(kartya);
    }

    /**
     * Visszaadja a játékos birtokolt kontinenseit.
     *
     * @return A játékos birtokolt kontinenseinek halmaza.
     */
    public Set<Kontinens> getKontinensek() {
        return kontinensek;
    }

    /**
     * Visszaadja a játékos egyedi azonosítóját.
     *
     * @return A játékos egyedi azonosítója.
     */
    public Integer getID() {
        return ID;
    }

    /**
     * Beállítja a játékos hadosztályának számát.
     *
     * @param hadosztalyNum A hadosztály száma.
     */
    public void setHadosztalyNum(Integer hadosztalyNum) {
        this.hadosztalyNum = hadosztalyNum;
    }

    /**
     * Hozzáad egy értéket a játékos hadosztályának számához.
     *
     * @param h A hozzáadni kívánt érték.
     */
    public void addHadosztalyNum(Integer h) {
        hadosztalyNum += h;
    }

    /**
     * Visszaadja a játékos hadosztályának számát.
     *
     * @return A játékos hadosztályának száma.
     */
    public Integer getHadosztalyNum() {
        return hadosztalyNum;
    }
}
