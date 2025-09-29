package com.Basics;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Parsing {
    public static Vector4f convertToVec4f(String line){
        String[] values = line.split(" ");
        Vector4f vec4 = new Vector4f();
        try{
            vec4.x = Float.parseFloat(values[0]);
            vec4.y = Float.parseFloat(values[1]);
            vec4.z = Float.parseFloat(values[2]);
            vec4.w = Float.parseFloat(values[3]);

        } catch (Exception e){
            System.out.println("Error converting Vec4 to float: " +line);
            throw new RuntimeException();
        }
        return vec4;
    }

    public static Vector3f convertToVec3f(String line){
        String[] values = line.split(" ");
        Vector3f vec3 = new Vector3f();
        try{
            vec3.x = Float.parseFloat(values[0]);
            vec3.y = Float.parseFloat(values[1]);
            vec3.z = Float.parseFloat(values[2]);

        } catch (Exception e){
            System.out.println("Error converting Vec3 to float: " +line);
            throw new RuntimeException();
        }
        return vec3;
    }
}
