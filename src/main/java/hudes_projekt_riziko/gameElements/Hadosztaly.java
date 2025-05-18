package hudes_projekt_riziko.gameElements;

/**
 * Az enum, amely a Rizikó játékban lévő különböző hadosztályokat reprezentálja.
 * Minden hadosztálynak van egy neve és egy hozzá tartozó értéke.
 */
public enum Hadosztaly {
    KATONA("Katonaság", 1),     // A gyalogos egységet reprezentálja.
    LOVASSAG("Lovasság", 5),    // A lovasságot reprezentálja.
    TUZERSEG("Tüzérség", 10);   // A tüzérséget reprezentálja.

    private final String nev;
    private final int ertek;

    /**
     * Konstruktor, amely inicializálja a hadosztály nevét és értékét.
     *
     * @param nev A hadosztály neve.
     * @param ertek A hadosztályhoz tartozó érték.
     */
    Hadosztaly(String nev, int ertek) {
        this.nev = nev;
        this.ertek = ertek;
    }

    /**
     * Visszaadja a hadosztály nevét.
     *
     * @return A hadosztály neve.
     */
    public String getNev() {
        return nev;
    }

    /**
     * Visszaadja a hadosztályhoz tartozó értéket.
     *
     * @return A hadosztály értéke.
     */
    public int getErtek() {
        return ertek;
    }
}
