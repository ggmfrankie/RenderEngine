import com.Engine.GameEngine;
import com.Engine.Games.DummyGame;
import com.Extern.KotlinTest;
import com.Extern.Rechner;

import static com.Basics.Utils.*;
import static com.Extern.Rechner.*;

public class Main {

    public static void main(String[] args) {
        gameEngine = new GameEngine("Game", 1000, 1000, new DummyGame());
        gameEngine.start();
        double delta_alpha = 0.1;
        var KotlinTest = new KotlinTest();

        double[] werte = new double[]{800.042- 798.243, 801.847- 800.042, 803.612- 801.847, 805.460- 803.612, 807.283- 805.460};

        average(werte);
        //System.out.printf("Ergebnis: %f\n", 800.042- 798.243);
        //System.out.printf("Ergebnis: %f\n", 801.847- 800.042);
        //System.out.printf("Ergebnis: %f\n", 803.612- 801.847);
        //System.out.printf("Ergebnis: %f\n", 805.460- 803.612);
        //System.out.printf("Ergebnis: %f\n", 807.283- 805.460);
    }


}