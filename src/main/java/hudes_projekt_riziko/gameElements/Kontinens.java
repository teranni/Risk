package hudes_projekt_riziko.gameElements;

import java.util.List;

/**
 * A Kontinens rekord egy földrészt reprezentál a Rizikó játékban.
 * Minden kontinensnek van neve, katonai értéke és hozzá tartozó területek listája.
 *
 * @param name A kontinens neve
 * @param ertek A kontinens értéke, azaz mennyi bónusz hadosztály jár érte körönként
 * @param teruletek A kontinenshez tartozó területek listája
 */
public record Kontinens(String name, Integer ertek, List<Terulet> teruletek) {

    /**
     * Konstruktor ellenőrzéssel, hogy a név ne legyen null vagy üres.
     */
    public Kontinens {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }
    }

    /**
     * Visszaadja a kontinens nevét.
     *
     * @return A kontinens neve
     */
    public String getName() {
        return name;
    }

    /**
     * Visszaadja a kontinens katonai értékét.
     *
     * @return A kontinens értéke
     */
    public Integer getErtek() {
        return ertek;
    }

    /**
     * Visszaadja a kontinenshez tartozó területek listáját.
     *
     * @return A területek listája
     */
    public List<Terulet> getTeruletek() {
        return teruletek;
    }

    /**
     * Új terület hozzáadása a kontinenshez.
     *
     * @param t A hozzáadandó terület
     */
    public void addTerulet(Terulet t) {
        teruletek.add(t);
    }
}
