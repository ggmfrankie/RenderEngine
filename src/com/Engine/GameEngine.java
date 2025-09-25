package com.Engine;
import com.Basics.Interface.FloatFunction;
import com.Basics.OBJLoader;
import com.Basics.Saving.GUIJsonWriter;
import com.Engine.GameObjects.GameItem;
import com.Engine.GameObjects.ObjectLoader;
import com.Basics.Interface.UpdateAction;
import com.Rendering.GUI.Elements.BaseGuiComponent;
import com.Rendering.GUI.GUIRenderer;
import com.Rendering.Mesh.MeshData;
import com.Engine.Games.IGameLogic;
import com.Rendering.Light.DirectionalLight;
import com.Rendering.Light.PointLight;
import com.Rendering.Light.SpotLight;
import com.Rendering.Mesh.Mesh;
import com.Rendering.Renderer;
import com.Window.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.*;


import static com.Basics.Utils.debugPrint;
import static org.lwjgl.glfw.GLFW.*;


public class GameEngine implements Runnable{
    Window window;
    double current;
    List<MeshData> meshData;
    Renderer renderer;
    GUIRenderer guiRenderer;

    MouseInput mouseInput;
    IGameLogic gameLogic;

    OBJLoader objFileLoader;
    ObjectLoader objectLoader;
    Map<String, Mesh> existingMeshes;

    private final Thread gameLoopThread;

    public GameEngine(String windowTitle, int width, int height, IGameLogic gameLogic){

        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height);
        renderer = new Renderer();
        guiRenderer = new GUIRenderer();
        mouseInput = new MouseInput();
        meshData = new ArrayList<>();
        objFileLoader = new OBJLoader();

        objectLoader = new ObjectLoader();

        existingMeshes = new HashMap<>();
        this.gameLogic = gameLogic;

    }

    public void start() {
        gameLogic.init();
        objectLoader.loadObjects();
        existingMeshes.clear();
        gameLoopThread.start();
    }

    @Override
    public void run() {
        try{
            init();

            window.createWindow();
            window.initWindow();


            debugPrint("Game was initialized");

            gameLogic.setCamera(renderer.getCamera());
            gameLogic.setDirectionalLight(renderer.getDirectionalLight());


            renderer.init();
            renderer.initGameItems();

            guiRenderer.init();
            guiRenderer.initGuiComponents();

            gameLogic.setGUIComponents(guiRenderer.getGuiComponents());
            gameLogic.setGameItems(renderer.getGameItems());

            mouseInput.init(window);
            gameLoop();
        } catch (Exception e) {
            System.out.println("Crash!");
            e.printStackTrace();
        }
    }

    public void gameLoop(){

        double secsPerUpdate = 1d/30d;
        double previous = getTime();
        double steps = 0.0d;
        boolean isGameActive = true;

        while(isGameActive){
            double loopStartTime = getTime();
            double elapsed = loopStartTime - previous;
            current = getTime();
            previous = current;
            steps += elapsed;

            // Receive Key and Mouse inputs
            handleInputs();

            while (steps >= secsPerUpdate){
                // Update everything not Render related
                updateGameState();
                steps -= secsPerUpdate;
            }
            // Actually render
            isGameActive = update(window);

            sync(current);
        }
        renderer.cleanUp();
        guiRenderer.cleanUp();
        glfwTerminate();
    }

    public void sync(double loopStartTime){
        debugPrint("Sleeping");
        float loopSlot = 1f/50;
        double endTime = loopStartTime + loopSlot;
        while(getTime() < endTime){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie){

            }
        }
    }

    public void saveAll(){
        GUIJsonWriter GUIJsonWriter = new GUIJsonWriter("GuiLayout.json");
        HashSet<BaseGuiComponent> guiComponents = guiRenderer.getGuiComponents();
        for(BaseGuiComponent guiComponent : guiComponents){
            GUIJsonWriter.addGuiComponent(guiComponent);
        }
        GUIJsonWriter.printToFile();
    }

    protected void input(){
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    public boolean update(Window window) {
        renderer.render(window);
        guiRenderer.render(window);
        return window.update();
    }

    public void addPointLight(Vector3f color, Vector3f position, float intensity, PointLight.Attenuation att){
        renderer.addPointLight(color, position, intensity, att);
    }

    public void addSpotLight(SpotLight spotLight){
        renderer.addSpotLight(spotLight);
    }

    public void addPointLight(PointLight pointLight){
        renderer.addPointLight(pointLight);
    }
    public void addDirectionalLight(DirectionalLight directionalLight){
        renderer.addDirectionalLight(directionalLight);
    }

    @Deprecated
    public void addGameItem_RandomPos(String fileName){
        Random random = new Random();
        Mesh mesh = objFileLoader.loadMesh(fileName);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(random.nextFloat(10.0f), random.nextFloat(10.0f), random.nextFloat(10.0f));
        gameItem.setScale(random.nextFloat(3.0f));
        renderer.addGameItem(gameItem);
    }

    public void addGameItem(Vector3f pos, Vector3f rotation, String fileName){
        addGameItem(pos, rotation, fileName, 1.0f);
    }

    public void addGameItem(GameItem gameItem){
        renderer.addGameItem(gameItem);
    }

    public void addGameItem(Vector3f pos, Vector3f rotation, String fileName, float scale){
        Mesh mesh;
        if(existingMeshes.containsKey(fileName)){
            mesh = existingMeshes.get(fileName);
        } else {
            mesh = objFileLoader.loadMesh(fileName);
        }
        existingMeshes.put(fileName, mesh);

        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(pos);
        gameItem.setRotation(rotation);
        gameItem.setScale(scale);
        renderer.addGameItem(gameItem);
    }

    public void addGameItem(Vector3f pos, Vector3f rotation, String fileName, UpdateAction updateAction){
        Mesh mesh;
        if(existingMeshes.containsKey(fileName)){
            mesh = existingMeshes.get(fileName);
        } else {
            mesh = objFileLoader.loadMesh(fileName);
        }
        existingMeshes.put(fileName, mesh);

        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(pos);
        gameItem.setRotation(rotation);
        gameItem.setUpdateAction(updateAction);
        renderer.addGameItem(gameItem);
    }
    @Deprecated
    public void addGameItem(String fileName, FloatFunction positionCalculator){
        Mesh mesh = objFileLoader.loadMesh(fileName);
        GameItem gameItem = new GameItem(mesh);
        float x = positionCalculator.calc(0);
        float y = positionCalculator.calc(1);
        float z = positionCalculator.calc(2);

        float pitch = positionCalculator.calc(3);
        float yaw = positionCalculator.calc(4);
        float roll = positionCalculator.calc(5);
        gameItem.setPosition(x, y, z);
        gameItem.setRotation(pitch, yaw, roll);
        renderer.addGameItem(gameItem);
    }
    @Deprecated
    public void addGameItem_RandomPos(GameItem gameItem){
        renderer.addGameItem(gameItem);
    }
    @Deprecated
    public void addGameItem_RandomPos(float x, float y, float z){
        renderer.addGameItemAt(x, y, z);
    }

    public void addGUIComponent(BaseGuiComponent guiComponent){
        guiRenderer.addGUIComponent(guiComponent);
    }

    public void setCurrentMesh(Mesh mesh){
        renderer.setCurrentMesh(mesh);
    }



    public void updateGameState(){
        gameLogic.update(0.016f, mouseInput);
    }

    private void init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
    }

    public void handleInputs() {
        glfwPollEvents();
        input();
    }

    public long getTime(){
        return System.currentTimeMillis();
    }

    public Window getWindow(){
        return  this.window;
    }

    public ObjectLoader getObjectLoader() {
        return objectLoader;
    }
}
