import com.Engine.GameEngine;
import com.Engine.Games.DummyGame;
import com.Extern.Rechner;

import static com.Basics.Utils.*;
import static com.Extern.Rechner.*;

public class Main {

    public static void main(String[] args) {
        gameEngine = new GameEngine("Game", 1000, 1000, new DummyGame());
        gameEngine.start();
        double[] nums = new double[]{34,119,204,289,374,459,544,628,713,798,883};
        Rechner.averageDistance(nums);
    }
}