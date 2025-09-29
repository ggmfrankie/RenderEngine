package com.Engine.GameObjects;

import com.Basics.Parsing;
import com.Rendering.Light.Material;
import com.Rendering.Mesh.Mesh;
import com.Rendering.Textures.Texture;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static com.Basics.Utils.readFile;

public class OBJFileLoader {
    final boolean DEBUG = false;
    Defaults defaults = new Defaults();

    List<String> vertexSubstrings;
    List<String> textureSubstrings;
    List<String> normalSubstrings;

    public List<Mesh> loadAllMeshes(String fileName){
        List<String> meshFile;

        if(fileName.isEmpty()) meshFile = readFile("\\Resources\\Objects\\grass_block.mtl");
        else meshFile = readFile("\\Resources\\Objects\\" + fileName);

        return loadMeshesFromFile(meshFile);
    }

    private List<Mesh> loadMeshesFromFile(List<String> file){
        file = preProcessFile(file);
        printFile(file);

        List<String> materialFile = readFile(
                "\\Resources\\Objects\\"
                        +getLinesWith("mtllib", file)
                        .getFirst()
                        .replace("mtllib", "")
                        .trim());
        vertexSubstrings = getLinesWith("v", file);
        textureSubstrings = getLinesWith("vt", file);
        normalSubstrings = getLinesWith("vn", file);

        printFile(vertexSubstrings);
        printFile(textureSubstrings);
        printFile(normalSubstrings);

        return null;
    }

    private Material loadMaterial(List<String> lines){
        String name = lines.getFirst().trim();
        _PRINT_("---------------------------");
        _PRINT_("New material: " +name);
        _PRINT_("With file:");
        _PRINT_(String.valueOf(lines));
        Material material;

        Vector4f ambientColor = getVec4f("Ka", lines, defaults.AMBIENT_COLOR).getFirst();
        Vector4f diffuseColor = getVec4f("Kd", lines, defaults.DIFFUSE_COLOR).getFirst();
        Vector4f specularColor = getVec4f("Ks", lines, defaults.SPECULAR_COLOR).getFirst();

        Vector4f emissiveColor = getVec4f("Ke", lines, defaults.EMISSION_COLOR).getFirst();

        float specularExponent = getFloat("Ns", lines, defaults.SPECULAR_EXPONENT).getFirst();
        float opticalDensity = getFloat("Ni", lines, defaults.OPTICAL_DENSITY).getFirst();
        float dissolve = getFloat("d", lines, defaults.DISSOLVE).getFirst();

        int illum = getInt("illum", lines, defaults.ILLUM).getFirst();

        Texture texture = getTexture("map_Kd", lines, defaults.TEXTURE);

        material = new Material(
                name,
                ambientColor,
                diffuseColor,
                specularColor,
                emissiveColor,
                specularExponent,
                opticalDensity,
                dissolve,
                illum,
                texture
        );
        return material;
    }

    private List<String> preProcessFile(List<String> file){
        List<String> processedFile = new ArrayList<>();
        for (String s : file) {
            String line = s
                    .replaceAll("\\s+", " ")
                    .replaceAll("#.*$", "")
                    .trim();

            processedFile.add(line);
        }
        return  processedFile;
    }

    private void printFile(List<String> file){
        _PRINT_();
        for(String s : file){
            _PRINT_(s);
        }
        _PRINT_();
    }

    private List<Vector4f> getVec4f(String prefix, List<String> lines, Vector4f defaultValue){
        List<String> matchedLines = getLinesWith(prefix, lines);

        if (matchedLines.isEmpty()) {
            return List.of(defaultValue);
        }

        return matchedLines.stream()
                .map(Parsing::convertToVec4f)
                .toList();
    }

    private List<Float> getFloat(String prefix, List<String> lines, float defaultValue){
        List<String> matchedLines = getLinesWith(prefix, lines);

        if (matchedLines.isEmpty()) {
            return List.of(defaultValue);
        }

        return matchedLines.stream()
                .map(Float::parseFloat)
                .toList();
    }

    private List<Integer> getInt(String prefix, List<String> lines, int defaultValue){
        List<String> matchedLines = getLinesWith(prefix, lines);

        if (matchedLines.isEmpty()) {
            return List.of(defaultValue);
        }

        return matchedLines.stream()
                .map(Integer::parseInt)
                .toList();
    }

    private Texture getTexture(String prefix, List<String> lines, Texture defaultValue){
        return getLinesWith(prefix, lines).stream()
                .findFirst()
                .map(this::loadTexture)
                .orElse(defaultValue);
    }

    private Texture loadTexture(String textureName){
        return new Texture(textureName);
    }

    private List<String> getLinesWith(String key, List<String> file){
        return file.stream()
                .filter(s -> s.startsWith(key+ " "))
                .map(s -> s.replace(key+ " ", ""))
                .toList();
    }

    static final class Defaults {
        public final Vector4f AMBIENT_COLOR = new Vector4f(0.5f);
        public final Vector4f DIFFUSE_COLOR = new Vector4f(0.5f);
        public final Vector4f SPECULAR_COLOR = new Vector4f(0.5f);
        public final Vector4f EMISSION_COLOR = new Vector4f(0.5f);

        public final float SPECULAR_EXPONENT = 2.2f;
        public final float OPTICAL_DENSITY = 1.0f;
        public final float DISSOLVE = 1.0f;

        public final int ILLUM = 2;
        public final Texture TEXTURE = new Texture();
    }

    private void _PRINT_(String s){
        if(DEBUG) return;
        System.out.println(s);
    }
    private void _PRINT_(){
        if(DEBUG) return;
        System.out.println();
    }
}
