package com.Engine.GameObjects;

import com.Basics.Saving.GUIJsonReader;
import com.Basics.Saving.JsonReader;
import com.Engine.Events;
import com.Rendering.GUI.Elements.BaseGuiComponent;
import com.Rendering.GUI.Elements.Button;
import com.Rendering.GUI.Elements.TextField;
import com.Rendering.GUI.GUIRenderer;
import com.Rendering.Light.DirectionalLight;
import com.Rendering.Light.PointLight;
import com.Rendering.Light.SpotLight;
import com.Rendering.Textures.PatchTexture;
import com.Rendering.Textures.Texture;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashSet;

import static com.Basics.Utils.gameEngine;

public class ObjectLoader {
    Events events;
    GUIJsonReader guiJsonReader;

    public ObjectLoader(){
        events = new Events();
        guiJsonReader = new GUIJsonReader("GuiLayout.json");
    }

    public void loadObjects(){
        addGameItemsManual();
        addGuiElementsFromFile();
    }

    private void addGuiElementsFromFile() {
        HashSet<BaseGuiComponent> guiComponents = guiJsonReader.getGuiComponents();
        for(BaseGuiComponent component : guiComponents){
            gameEngine.addGUIComponent(component);
        }
    }

    private void addGameItemsManual(){
        Vector3f color = new Vector3f(0.5f, 0.0f, 0.0f);
        Vector3f color2 = new Vector3f(0.5f, 0.5f, 0.5f);
        Vector3f position = new Vector3f(-1.0f, 1.0f, -1.5f);
        Vector3f direction = new Vector3f(0.0f, 0.0f, 0.0f);

        float intensity = 0.5f;

        PointLight.Attenuation att = new PointLight.Attenuation(1.0f, 0.005f, 0.01f);
        gameEngine.addGameItem(
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(90.0f, 0.0f, 0.0f),
                "grass_block.obj",
                events.getUpdateAction("spin")
        );

        /*
        gameEngine.addGameItem(
                new Vector3f(5.0f, -10.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                "Sting-Sword-lowpoly.obj"
        );

         */
        /*
        gameEngine.addGameItem(
                new Vector3f(30.0f, -10.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                "Humvee.obj",
                0.1f
        );
         */
        /*
        gameEngine.addGameItem(
                new Vector3f(0.0f, -10.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                "Marlow66.obj"
        );

         */

        gameEngine.addPointLight(color, position, intensity, att);
        gameEngine.addDirectionalLight(new DirectionalLight(color2, direction, 0.0f));
        gameEngine.addSpotLight(new SpotLight(color2, new Vector3f(4.0f, 0.0f, -2.0f), 0.5f, att, direction, org.joml.Math.cos(Math.toRadians(45))));

        gameEngine.addPointLight(color, new Vector3f(26.0f, -10.0f, 0.0f), intensity, att);
    }

    private void addGuiElementsManual() {

        PatchTexture patchTexture = new PatchTexture("GUI\\Button\\Grey_Button.9.png");

        Button button1 = new Button(
                new Vector2f(200.0f, 300.0f),
                500, 500,
                "one",
                patchTexture,
                events.getClickAction("dragging")
        );
        Button button2 = new Button(
                new Vector2f(700.0f, 800.0f),
                200, 200,
                "two",
                patchTexture
        );
        Button button3 = new Button(
                new Vector2f(300.0f, 800.0f),
                500, 150,
                "three",
                patchTexture
        );
        TextField textField1 = new TextField(
                new Vector2f(500.0f, 500.0f),
                100, 100,
                "Test",
                new Texture("GUI\\Button\\Grey_Button.9.png")
        );

        gameEngine.addGUIComponent(button1);
        gameEngine.addGUIComponent(button3);
        //gameEngine.addGUIComponent(button2);
        //gameEngine.addGUIComponent(textField1);
    }

    public Events getEvents(){
        return events;
    }
}
