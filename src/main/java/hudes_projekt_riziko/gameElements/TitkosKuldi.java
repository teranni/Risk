package hudes_projekt_riziko.gameElements;

import java.util.ArrayList;
import java.util.List;

/**
 * A TitkosKuldi osztály tartalmazza a Rizikó játékban előforduló titkos küldetéseket.
 * Ezeket a küldetéseket a játékosok véletlenszerűen kapják, és a játék megnyerésének feltétele lehet ezek teljesítése.
 */
public class TitkosKuldi {
    /**
     * Három vagy több játékos esetén elérhető titkos küldetések listája.
     */
    public static final List<String> Missions_3 = new ArrayList<>(List.of(
            "Foglald el Észak-Amerikát és Afrikát!",
            "Foglald el Észak-Amerikát és Ausztráliát!",
            "Foglald el Ázsiát és Dél-Amerikát!",
            "Foglald el Ázsiát és Afrikát!",
            "Foglald el Európát, Dél-Amerikát és egy tetszőleges másik kontinenst!",
            "Foglald el Európát, Ausztráliát és egy tetszőleges másik kontinenst!",
            "Foglalj el 24 területet!",
            "Foglalj el 18 területet, mindegyiken legyen legalább két sereged!",
            "Semmisítsd meg a piros hadsereget!",
            "Semmisítsd meg a kék hadsereget!",
            "Semmisítsd meg a zöld hadsereget!"
    ));

    /**
     * Kétjátékos módban használt titkos küldetés.
     */
    public static final String Missions_2 = "Semmisítsd meg a másik játékos teljes hadseregét!";
}
