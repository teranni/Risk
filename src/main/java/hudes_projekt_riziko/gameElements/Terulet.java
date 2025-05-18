package hudes_projekt_riziko.gameElements;

import java.util.HashSet;
import java.util.Set;

/**
 * A Terulet osztály a Rizikó játék egy területét reprezentálja.
 * Minden területhez tartozik egy kontinenshez, lehetnek szomszédos területei, tartozhat egy játékoshoz,
 * és tárolja, hogy hány katona állomásozik rajta.
 */
public class Terulet{
    Integer ID;
    Integer sorszam;
    String name;
    Kontinens kontinens;
    Set<Terulet> szomszedok;
    Integer katonak = 0;
    Jatekos jatekos;

    /**
     * Visszaadja a terület nevét.
     * @return A terület neve
     */
    public String getName() {
        return name;
    }

    /**
     * Konstruktor, amely létrehoz egy új területet a megadott adatokkal.
     *
     * @param ID Azonosító
     * @param sorszam A terület sorszáma
     * @param name A terület neve
     * @param kontinens A kontinens, amelyhez tartozik
     */
    public Terulet(Integer ID, Integer sorszam, String name, Kontinens kontinens) {
        this.ID = ID;
        this.sorszam = sorszam;
        this.name = name;
        this.kontinens = kontinens;
        szomszedok = new HashSet<>();
        katonak = 0;
    }

    /**
     * Beállítja a terület szomszédait.
     * @param szomszedok A szomszédos területek halmaza
     */
    public void setSz(Set<Terulet> szomszedok) {
        this.szomszedok = szomszedok;
    }

    /**
     * Beállítja, hogy melyik játékos birtokolja a területet.
     * @param j A játékos, aki birtokolja
     */
    public void setJatekos(Jatekos j) {
        jatekos = j;
    }

    /**
     * Visszaadja a területet birtokló játékost.
     * @return A játékos
     */
    public Jatekos getJatekos() {
        return jatekos;
    }

    /**
     * Hozzáad adott számú katonát a területhez.
     * @param katonak A hozzáadandó katonák száma
     */
    public void addKatona(Integer katonak) {
        this.katonak += katonak;
    }

    /**
     * Beállítja a katonák számát a területen.
     * @param katonak Az új katonaszám
     */
    public void setKatonak(Integer katonak) {
        this.katonak = katonak;
    }

    /**
     * Visszaadja a területen lévő katonák számát.
     * @return Katonák száma
     */
    public Integer getKatonak() {
        return katonak;
    }

    /**
     * Visszaadja a szomszédos területek halmazát.
     * @return A szomszédos területek
     */
    public Set<Terulet> getSzomszedok() {
        return szomszedok;
    }

    /**
     * Visszaadja, melyik kontinenshez tartozik a terület.
     * @return A kontinens objektum
     */
    public Kontinens getKontinens() {
        return kontinens;
    }
}
