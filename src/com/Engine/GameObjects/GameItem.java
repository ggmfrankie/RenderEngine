package com.Engine.GameObjects;

import com.Basics.Interface.UpdateAction;
import com.Rendering.Light.Material;
import com.Rendering.Mesh.Mesh;
import org.joml.Vector3f;

public class GameItem {
    private final Mesh mesh;
    private Vector3f position;
    private float scale;
    private Vector3f rotation;
    private UpdateAction updateAction;

    public GameItem(Mesh mesh){
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GameItem(GameItem gameItem){
        mesh = gameItem.getMesh();
        position = new Vector3f(gameItem.getPosition());
        scale = gameItem.getScale();
        rotation = new Vector3f(gameItem.getRotation());
        updateAction = gameItem.getUpdateAction();
    }

    public void init(){
        mesh.init();
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
        mesh.render();
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

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getColor(){
        return mesh.getColor();
    }

    public Material getMaterial(){
        return mesh.getMaterial();
    }

    public UpdateAction getUpdateAction() {
        return updateAction;
    }
}
