package hudes_projekt_riziko;

import hudes_projekt_riziko.gameElements.Hadosztaly;
import hudes_projekt_riziko.gameElements.Kartya;
import hudes_projekt_riziko.gameElements.Kontinens;
import hudes_projekt_riziko.gameElements.Terulet;

import java.util.*;

/**
 * A Setup osztály a játék világának inicializálásáért felelős.
 * Beállítja a kontinenseket, területeket és azok kapcsolatait, valamint generálja a kártyákat.
 * A rizikó játékban alapvető a világ térképe és a területek közötti kapcsolatok megfelelő beállítása.
 */
public class Setup {
    private static final Random rand = new Random();

    /**
     * Inicializálja a játék kontinenseit, területeit és azok szomszédos területeit,
     * valamint generálja a pakli kártyákat.
     *
     * A rizikó játék egyik alapvető lépése a világ térképének felépítése, amely meghatározza a
     * játékosok közötti interakciókat és a stratégiai döntéseket.
     *
     * @param pakli a kártyák listája, amelyet a területek alapján töltünk fel
     * @param mindenTerulet a világ összes területe
     * @return a kontinensek listája
     */
    public static List<Kontinens> setup(List<Kartya> pakli, List<Terulet> mindenTerulet) {
        List<Terulet> teruletek = new ArrayList<>();
        Kontinens EAm = new Kontinens("Észak-Amerika", 5, teruletek);
        List<Terulet> teruletek1 = new ArrayList<>();
        Kontinens DAm = new Kontinens("Dél-Amerika", 2, teruletek1);
        List<Terulet> teruletek2 = new ArrayList<>();
        Kontinens Eu = new Kontinens("Európa", 5, teruletek2);
        List<Terulet> teruletek3 = new ArrayList<>();
        Kontinens Afrika = new Kontinens("Afrika", 3, teruletek3);
        List<Terulet> teruletek4 = new ArrayList<>();
        Kontinens Azsia = new Kontinens("Ázsia", 7, teruletek4);
        List<Terulet> teruletek5 = new ArrayList<>();
        Kontinens Au = new Kontinens("Ausztrália", 2, teruletek5);

        // Területek és szomszédos területek beállítása
        Terulet Alaska = new Terulet(1, 1, "Alaska", EAm);
        EAm.addTerulet(Alaska);
        Terulet Alberta = new Terulet(2, 2, "Alberta", EAm);
        EAm.addTerulet(Alberta);
        Terulet C_America = new Terulet(3, 3, "Central America", EAm);
        EAm.addTerulet(C_America);
        Terulet Ea_US = new Terulet(4,4,"Eastern US", EAm);
        EAm.addTerulet(Ea_US);
        Terulet Greenland = new Terulet(5, 5, "Greenland", EAm);
        EAm.addTerulet(Greenland);
        Terulet Nw_Terr = new Terulet(6, 6, "Northwest Territory", EAm);
        EAm.addTerulet(Nw_Terr);
        Terulet Ontario = new Terulet(7, 7, "Ontario", EAm);
        EAm.addTerulet(Ontario);
        Terulet Quebec = new Terulet(8, 8, "Quebec", EAm);
        EAm.addTerulet(Quebec);
        Terulet W_US = new Terulet(9, 9, "Western US", EAm);
        EAm.addTerulet(W_US);

        Terulet Argentina = new Terulet(10, 1, "Argentina", DAm);
        DAm.addTerulet(Argentina);
        Terulet Brazil = new Terulet(11, 2, "Brazil", DAm);
        DAm.addTerulet(Brazil);
        Terulet Venezuela = new Terulet(12, 3, "Venezuela", DAm);
        DAm.addTerulet(Venezuela);
        Terulet Peru = new Terulet(13, 4, "Peru", DAm);
        DAm.addTerulet(Peru);

        Terulet GB = new Terulet(14, 1, "Great Britain", Eu);
        Eu.addTerulet(GB);
        Terulet Iceland = new Terulet(15, 2, "Iceland", Eu);
        Eu.addTerulet(Iceland);
        Terulet N_Eu = new Terulet(16, 3, "Northern Europe", Eu);
        Eu.addTerulet(N_Eu);
        Terulet Scandinavia = new Terulet(17, 4, "Scandinavia", Eu);
        Eu.addTerulet(Scandinavia);
        Terulet S_Eu = new Terulet(18, 5, "Southern Europe", Eu);
        Eu.addTerulet(S_Eu);
        Terulet Ukraine = new Terulet(19, 6, "Ukraine", Eu);
        Eu.addTerulet(Ukraine);
        Terulet W_Eu = new Terulet(20, 7, "Western Europe", Eu);
        Eu.addTerulet(W_Eu);

        Terulet Congo = new Terulet(21, 1, "Congo", Afrika);
        Afrika.addTerulet(Congo);
        Terulet Ea_Af = new Terulet(22, 2, "East Africa", Afrika);
        Afrika.addTerulet(Ea_Af);
        Terulet Egypt = new Terulet(23, 3, "Egypt", Afrika);
        Afrika.addTerulet(Egypt);
        Terulet Madagascar = new Terulet(24, 4, "Madagascar", Afrika);
        Afrika.addTerulet(Madagascar);
        Terulet N_Af = new Terulet(25, 5, "North Africa", Afrika);
        Afrika.addTerulet(N_Af);
        Terulet S_Af = new Terulet(26, 6, "South Africa", Afrika);
        Afrika.addTerulet(S_Af);

        Terulet Afghanistan = new Terulet(27, 1, "Afghanistan", Azsia);
        Azsia.addTerulet(Afghanistan);
        Terulet China = new Terulet(28, 2, "China", Azsia);
        Azsia.addTerulet(China);
        Terulet India = new Terulet(29, 3, "India", Azsia);
        Azsia.addTerulet(India);
        Terulet Irkutsk = new Terulet(30, 4, "Irkutsk", Azsia);
        Azsia.addTerulet(Irkutsk);
        Terulet Japan = new Terulet(31, 5, "Japan", Azsia);
        Azsia.addTerulet(Japan);
        Terulet Kamchatka = new Terulet(32, 6, "Kamchatka", Azsia);
        Azsia.addTerulet(Kamchatka);
        Terulet M_East = new Terulet(33, 7, "Middle East", Azsia);
        Azsia.addTerulet(M_East);
        Terulet Mongolia = new Terulet(34, 8, "Mongolia", Azsia);
        Azsia.addTerulet(Mongolia);
        Terulet Siam = new Terulet(35, 9, "Siam", Azsia);
        Azsia.addTerulet(Siam);
        Terulet Siberia = new Terulet(36, 10, "Siberia", Azsia);
        Azsia.addTerulet(Siberia);
        Terulet Ural = new Terulet(37, 11, "Ural", Azsia);
        Azsia.addTerulet(Ural);
        Terulet Yakutsk = new Terulet(38, 12, "Yakutsk", Azsia);
        Azsia.addTerulet(Yakutsk);

        Terulet Ea_Au = new Terulet(39, 1, "Eastern Australia", Au);
        Au.addTerulet(Ea_Au);
        Terulet NewGui = new Terulet(40, 2, "New Guinea", Au);
        Au.addTerulet(NewGui);
        Terulet Indonesia = new Terulet(41, 3, "Indonesia", Au);
        Au.addTerulet(Indonesia);
        Terulet W_Au = new Terulet(42, 4, "Western Australia", Au);
        Au.addTerulet(W_Au);

        Alaska.setSz(Set.of(Nw_Terr, Alberta, Kamchatka));
        Alberta.setSz(Set.of(Nw_Terr, Alaska, Ontario, W_US));
        C_America.setSz(Set.of(W_US, Ea_US, Venezuela));
        Ea_US.setSz(Set.of(W_US, Ontario, Quebec, C_America));
        Greenland.setSz(Set.of(Nw_Terr, Ontario, Quebec, Iceland));
        Nw_Terr.setSz(Set.of(Alaska, Ontario, Alberta, Greenland));
        Ontario.setSz(Set.of(Nw_Terr, Alberta, Quebec, Greenland, W_US, Ea_US));
        Quebec.setSz(Set.of(Ontario, Ea_US, Greenland));
        W_US.setSz(Set.of(Alaska, Ontario, Ea_US, C_America));

        Argentina.setSz(Set.of(Peru, Brazil));
        Brazil.setSz(Set.of(Argentina, Peru, Venezuela, N_Af));
        Venezuela.setSz(Set.of(Brazil, Peru, C_America));
        Peru.setSz(Set.of(Argentina, Brazil, Venezuela));

        GB.setSz(Set.of(Iceland, Scandinavia, N_Eu, W_Eu));
        Iceland.setSz(Set.of(Greenland, GB, Scandinavia));
        N_Eu.setSz(Set.of(GB, Scandinavia, Ukraine, S_Eu, W_Eu));
        Scandinavia.setSz(Set.of(GB, N_Eu, Iceland, Ukraine));
        S_Eu.setSz(Set.of(N_Eu, Ukraine, W_Eu, M_East, Egypt, N_Af));
        Ukraine.setSz(Set.of(Scandinavia, N_Eu, S_Eu, M_East, Afghanistan, Ural));
        W_US.setSz(Set.of());

        Congo.setSz(Set.of(N_Af, Ea_Af, S_Af));
        Ea_Af.setSz(Set.of(Egypt, N_Af, Congo, S_Af, Madagascar, M_East));
        Egypt.setSz(Set.of(S_Eu, N_Af, Ea_Af, M_East));
        Madagascar.setSz(Set.of(S_Af, Ea_Af));
        N_Af.setSz(Set.of(W_Eu, S_Eu, Egypt, Ea_Af, Congo, Brazil));
        S_Af.setSz(Set.of(Congo, Ea_Af, Madagascar));

        Afghanistan.setSz(Set.of(Ural, Ukraine, M_East, India, China));
        China.setSz(Set.of(Afghanistan, India, Siam, Mongolia, Siberia));
        India.setSz(Set.of(M_East, Afghanistan, China, Siam));
        Irkutsk.setSz(Set.of(Siberia, Yakutsk, Kamchatka, Mongolia));
        Japan.setSz(Set.of(Mongolia, Kamchatka));
        Kamchatka.setSz(Set.of(Alaska, Yakutsk, Irkutsk, Mongolia, Japan));
        M_East.setSz(Set.of(S_Eu, Egypt, Ea_Af, India, Afghanistan, Ukraine));
        Mongolia.setSz(Set.of(Irkutsk, Kamchatka, Japan, China, Siberia));
        Siam.setSz(Set.of(India, China, Indonesia));
        Siberia.setSz(Set.of(Ural, Mongolia, China, Irkutsk, Yakutsk));
        Ural.setSz(Set.of(Ukraine, Afghanistan, Siberia));
        Yakutsk.setSz(Set.of(Siberia, Irkutsk, Kamchatka));

        Ea_Au.setSz(Set.of(W_Au, NewGui));
        NewGui.setSz(Set.of(Ea_Au, W_Au, Indonesia));
        Indonesia.setSz(Set.of(Siam, NewGui, W_Au));
        W_Au.setSz(Set.of(Ea_Au, NewGui, Indonesia));


        // Kartyak generálása
        mindenTerulet = List.of(
                Alaska, Alberta, C_America, Ea_US, Greenland, Nw_Terr, Ontario, Quebec, W_US,
                Argentina, Brazil, Venezuela, Peru,
                GB, Iceland, N_Eu, Scandinavia, S_Eu, Ukraine, W_Eu,
                Congo, Ea_Af, Egypt, Madagascar, N_Af, S_Af,
                Afghanistan, China, India, Irkutsk, Japan, Kamchatka, M_East, Mongolia,
                Siam, Siberia, Ural, Yakutsk,
                Ea_Au, NewGui, Indonesia, W_Au
        );

        for (Terulet t : mindenTerulet) {
            Hadosztaly tipus = Hadosztaly.values()[rand.nextInt(Hadosztaly.values().length)];
            pakli.add(new Kartya(t.getName(), tipus));
        }

        // Joker kártyák
        pakli.add(new Kartya("joker", null));
        pakli.add(new Kartya("joker", null));

        return List.of(EAm, DAm, Eu, Afrika, Azsia, Au);
    }
}
