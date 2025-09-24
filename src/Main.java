import com.Engine.GameEngine;
import com.Engine.Games.DummyGame;

import static com.Basics.Utils.*;

public class Main {

    public static void main(String[] args) {
        gameEngine = new GameEngine("Game", 1000, 1000, new DummyGame());
        gameEngine.start();
    }
}