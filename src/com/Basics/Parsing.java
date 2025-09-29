package com.Basics;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class Parsing {
    public static Vector4f parseVec4f(String line){
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

    public static Vector3f parseVec3f(String line){
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

    public static List<Vector3f> parseVec3f(List<String> line){
        return line.stream()
                .map(Parsing::parseVec3f)
                .toList();
    }

    public static List<Vector4f> parseVec4f(List<String> line){
        return line.stream()
                .map(Parsing::parseVec4f)
                .toList();
    }
}
