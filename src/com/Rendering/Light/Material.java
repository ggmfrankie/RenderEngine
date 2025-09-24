package com.Rendering.Light;

import com.Rendering.Textures.Texture;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.glAreTexturesResident;

public class Material {
    Vector4f ambientColor;
    Vector4f diffuseColor;
    Vector4f specularColor;
    float reflectance;

    Texture texture;

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
    }
    public Material(Texture texture){
        this.ambientColor = new Vector4f(0.5f);
        this.diffuseColor = new Vector4f(0.5f);
        this.specularColor = new Vector4f(0.5f);
        this.reflectance = 0.3f;
        this.texture = texture;
    }

    public Material(){
        this.ambientColor = new Vector4f(0.5f);
        this.diffuseColor = new Vector4f(0.5f);
        this.specularColor = new Vector4f(0.5f);
        this.reflectance = 0.3f;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }


    public float getReflectance() {
        return reflectance;
    }

    public void setTexture(Texture texture){
        this.texture =texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }


    public void initTexture(){
        if(texture == null) return;
        texture.init();
    }
}
