package com.Engine.GameObjects;

import com.Basics.Interface.UpdateAction;
import com.Rendering.Light.Material;
import com.Rendering.Mesh.Mesh;
import org.joml.Vector3f;

import java.util.Arrays;

public class GameItem {
    private final Mesh[] meshes;
    private Vector3f position;
    private float scale;
    private Vector3f rotation;
    private UpdateAction updateAction;

    private final String name;

    public GameItem(Mesh[] mesh, String name){
        this.name = name;
        this.meshes = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GameItem(GameItem gameItem){
        name = gameItem.getName();
        meshes = gameItem.getMeshes();
        position = new Vector3f(gameItem.getPosition());
        scale = gameItem.getScale();
        rotation = new Vector3f(gameItem.getRotation());
        updateAction = gameItem.getUpdateAction();
    }

    public void init(){
        for(Mesh mesh : meshes){
            try {
                mesh.init();
            } catch (NullPointerException npe){
                System.out.println("GameItem "+ this.getName() +" with Mesh " + Arrays.toString(this.getMeshes()) + "had no valid material");
                npe.printStackTrace();
            }

        }
    }

    public void setPosition(float x, float  y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(float x, float y, float z){
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void update(){
        if(updateAction == null) return;
        updateAction.update(this);
    }

    public void setUpdateAction(UpdateAction updateAction){
        this.updateAction = updateAction;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void render(){
        for(Mesh mesh : meshes){
            mesh.render();
        }
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public String getName() {
        return name;
    }

    public UpdateAction getUpdateAction() {
        return updateAction;
    }

    public void cleanUp() {
        for(Mesh mesh : meshes){
            mesh.cleanUp();
        }
    }
}
