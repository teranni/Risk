package hudes_projekt_riziko.gameElements;

/**
 * A Kartya osztály egy területhez tartozó kártyát reprezentál a Rizikó játékban.
 * Egy kártya tartalmazhat egy hadosztály típust, vagy lehet joker típusú is.
 */
public class Kartya {
    private final String teruletNev;       // A kártyához tartozó terület neve
    private final Hadosztaly tipus;        // A hadosztály típusa, joker esetén null
    private boolean joker;                 // Jelzi, hogy a kártya joker-e

    /**
     * Kártya létrehozása egy adott területnévvel és hadosztály típussal.
     * A joker kártyának "joker" névvel és null típussal kell rendelkeznie.
     *
     * @param teruletNev A terület neve vagy "joker" joker esetén
     * @param tipus A hadosztály típusa, joker esetén null
     * @throws IllegalArgumentException ha a megadott kombináció nem felel meg a szabályoknak
     */
    public Kartya(String teruletNev, Hadosztaly tipus) {
        if (teruletNev == "joker" && tipus == null) {
            joker = true;
        } else if (teruletNev != "joker" && teruletNev != null && tipus != null) {
            joker = false;
        } else {
            throw new IllegalArgumentException("Ilyen kártya típus nem hozható létre.");
        }

        this.teruletNev = teruletNev;
        this.tipus = tipus;
    }

    /**
     * Visszaadja a kártyához tartozó terület nevét.
     *
     * @return A terület neve
     */
    public String getTeruletNev() {
        return teruletNev;
    }

    /**
     * Visszaadja a kártya hadosztály típusát.
     *
     * @return A hadosztály típusa, joker esetén null
     */
    public Hadosztaly getTipus() {
        return tipus;
    }

    /**
     * Megadja, hogy a kártya joker típusú-e.
     *
     * @return true, ha joker; egyébként false
     */
    public boolean isJoker() {
        return joker;
    }

    /**
     * A kártya szöveges reprezentációja, a konzolra való kiíráshoz.
     *
     * @return A kártya szöveges leírása
     */
    @Override
    public String toString() {
        return isJoker()
                ? "Joker kártya"
                : String.format("Terület: %s, Hadosztály típus: %s", teruletNev, tipus);
    }
}
