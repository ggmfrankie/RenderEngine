import com.Engine.GameEngine;
import com.Engine.Games.DummyGame;
import com.Extern.KotlinTest;
import com.Extern.Rechner;

import static com.Basics.Utils.*;
import static com.Extern.Rechner.*;

public class Main {

    public static void main(String[] args) {
        //gameEngine = new GameEngine("Game", 1000, 1000, new DummyGame());
        //gameEngine.start();
        double delta_alpha = 0.1;
        var KotlinTest = new KotlinTest();

        String s = "19,8\n" +
                "18,3\n" +
                "17,3\n" +
                "15,7\n" +
                "13,5\n" +
                "11,5\n" +
                "9,7\n" +
                "8,2\n" +
                "6,8\n" +
                "5,7\n" +
                "4,6\n" +
                "3,6\n" +
                "2,8\n" +
                "2\n" +
                "1,3\n" +
                "0,7\n" +
                "0,1\n" +
                "0,6\n";

        refactorString(s);

        double[] werte = new double[]{800.042- 798.243, 801.847- 800.042, 803.612- 801.847, 805.460- 803.612, 807.283- 805.460};

        //average(werte);

    }


}