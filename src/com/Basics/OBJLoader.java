package com.Basics;

import com.Rendering.GUI.Elements.TextField;
import com.Rendering.Light.Material;
import com.Rendering.Textures.Texture;
import org.joml.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.Basics.Utils.readFile;

public class OBJLoader {
    List<Material> materials;
    Defaults defaults = new Defaults();

    public OBJLoader() {
        materials = new ArrayList<>();
    }

    private List<Material> loadMaterialsFromFile(String fileName){
        List<String> file;
        List<Material> materials = new ArrayList<>();
        List<String> currentMaterialFile = new ArrayList<>();
        if(fileName.isEmpty()) file = readFile("\\Resources\\Objects\\grass_block.mtl");
        else file = readFile("\\Resources\\Objects\\" + fileName);
        file = preProcessFile(file);
        for(String line : file){
            if(line.startsWith("newmtl")){
                if(!currentMaterialFile.isEmpty()) materials.add(loadMaterial(currentMaterialFile));

                currentMaterialFile.clear();
                currentMaterialFile.add(line.replace("newmtl", "").trim());
            } else {
                currentMaterialFile.add(line);
            }
        }
        return materials;
    }

    private Material loadMaterial(List<String> lines){
        String name = lines.getFirst();
        Material material;

        Vector4f ambientColor = convertToVec4f(getLinesWith("Ka", lines).getFirst());
        Vector4f diffuseColor = convertToVec4f(getLinesWith("Kd", lines).getFirst());
        Vector4f specularColor = convertToVec4f(getLinesWith("Ks", lines).getFirst());

        Vector4f emissiveColor = convertToVec4f(getLinesWith("Ke", lines).getFirst());

        float specularExponent = parseFloat(getLinesWith("Ns", lines).getFirst());
        float opticalDensity = parseFloat(getLinesWith("Ni", lines).getFirst());
        float dissolve = parseFloat((getLinesWith("d", lines).getFirst()));

        int illum = parseInt(getLinesWith("illum", lines).getFirst());

        String textureName = getLinesWith("map_Kd", lines).getFirst();

        material = new Material(
                name,
                ambientColor == null ? defaults.AMBIENT_COLOR : ambientColor,
                diffuseColor == null ? defaults.DIFFUSE_COLOR : diffuseColor,
                specularColor == null ? defaults.SPECULAR_COLOR : specularColor,
                emissiveColor == null ? defaults.EMISSION_COLOR : emissiveColor,
                Float.isNaN(specularExponent) ? defaults.SPECULAR_EXPONENT : specularExponent,
                Float.isNaN(opticalDensity) ? defaults.OPTICAL_DENSITY : opticalDensity,
                Float.isNaN(dissolve) ? defaults.DISSOLVE : dissolve,
                illum == Integer.MAX_VALUE ? defaults.ILLUM : illum,
                new Texture(textureName)
        );
        return material;
    }

    private float parseFloat(String s){
        try {
            return Float.parseFloat(s);
        } catch (Exception e){
            return Float.NaN;
        }
    }

    private int parseInt(String s){
        try {
            return Integer.parseInt(s);
        } catch (Exception e){
            return Integer.MAX_VALUE;
        }
    }

    private Texture loadTexture(String textureName){
        return new Texture(textureName);
    }

    private List<String> preProcessFile(List<String> file){
        List<String> processedFile = new ArrayList<>();
        for (int i = 0; i < file.size(); i++) {
            String line = file.get(i).replaceAll("\\s+", " ");
            line = line.replaceAll("#.*$", "").trim();

            processedFile.add(line);
        }
        return  processedFile;
    }

    private List<String> getLinesWith(String key, List<String> file){
        List<String> Lines = new ArrayList<>();

        for(String lineString : file){
            if(lineString.startsWith(key+ " ")){
                lineString = lineString.replace(key+ " ", "");
                Lines.add(lineString);
            }
        }
        return Lines;
    }

    private Vector4f convertToVec4f(String line){
        String[] values = line.split(" ");
        Vector4f vec4 = new Vector4f();
        try{
            vec4.x = Float.parseFloat(values[0]);
            vec4.y = Float.parseFloat(values[1]);
            vec4.z = Float.parseFloat(values[2]);
            vec4.w = 1.0f;
        } catch (Exception e){
            return null;
        }
        return vec4;
    }

    private Vector3i convertToVec3i(String line){
        String[] values = line.split(" ");
        Vector3i vec3 = new Vector3i();
        try {
            vec3.x = Integer.parseInt(values[0]);
            vec3.y = Integer.parseInt(values[1]);
            vec3.z = Integer.parseInt(values[2]);
        } catch (Exception e){
            return null;
        }
        return vec3;
    }

    private Vector2f convertToVec2f(String line){
        String[] values = line.split(" ");
        Vector2f vec2 = new Vector2f();
        try {
            vec2.x = Float.parseFloat(values[0]);
            vec2.y = Float.parseFloat(values[1]);
        } catch (Exception e){
            return null;
        }
        return vec2;
    }

    private Vector2i convertToVec2i(String line){
        String[] values = line.split(" ");
        Vector2i vec2 = new Vector2i();
        try {
            vec2.x = Integer.parseInt(values[0]);
            vec2.y = Integer.parseInt(values[1]);

        } catch (Exception e){
            return null;
        }
        return vec2;
    }

    static class Defaults {
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

    protected static class Face {
        private final List<OBJLoader.IdxGroup> idxGroups;

        public Face(String substring) {
            substring = substring.replace("f ", "");
            String[] vertexTokens = substring.split(" ");

            idxGroups = new ArrayList<>();
            for (String token : vertexTokens) {
                idxGroups.add(parseVertexData(token));
            }
        }

        public List<OBJLoader.Face> triangulate() {
            List<OBJLoader.Face> result = new ArrayList<>();

            if (idxGroups.size() < 3) return result; // invalid face

            for (int i = 1; i < idxGroups.size() - 1; i++) {
                OBJLoader.Face triangle = new OBJLoader.Face(idxGroups.getFirst(), idxGroups.get(i), idxGroups.get(i + 1));
                result.add(triangle);
            }

            return result;
        }

        private Face(OBJLoader.IdxGroup v0, OBJLoader.IdxGroup v1, OBJLoader.IdxGroup v2) {
            this.idxGroups = List.of(v0, v1, v2);
        }

        private OBJLoader.IdxGroup parseVertexData(String token) {
            String[] parts = token.split("/");

            OBJLoader.IdxGroup group = new OBJLoader.IdxGroup();
            group.idxPos = parts.length > 0 && !parts[0].isEmpty() ? Integer.parseInt(parts[0]) : -1;
            group.idxTextCoord = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : -1;
            group.idxVecNormal = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : -1;
            return group;
        }

        public List<OBJLoader.IdxGroup> getFaceVertexIndices() {
            return idxGroups;
        }
    }

    protected static class IdxGroup{
        public static final int NO_VALUE = -1;
        public int idxPos;
        public int idxTextCoord;
        public int idxVecNormal;

        public IdxGroup(){
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
