package com.Basics;

import com.Rendering.GUI.Elements.TextField;
import com.Rendering.Light.Material;
import com.Rendering.Mesh.Mesh;
import com.Rendering.Textures.Texture;
import org.joml.*;

import java.text.ParseException;
import java.util.*;

import static com.Basics.Utils.readFile;

public class OBJLoader {
    Map<String, Material> materials;
    Defaults defaults = new Defaults();

    public OBJLoader() {

    }

    public Mesh loadMesh(String fileName){
        return loadMeshesFromFile(fileName).getFirst();
    }

    private Map<String, Material> loadMaterialsFromFile(String fileName){
        List<String> file;
        Map<String, Material> materials = new HashMap<>();
        List<String> currentMaterialFile = new ArrayList<>();
        if(fileName.isEmpty()) file = readFile("\\Resources\\Objects\\grass_block.mtl");
        else file = readFile("\\Resources\\Objects\\" + fileName);
        file = preProcessFile(file);

        for(String line : file){
            if(line.startsWith("newmtl")){
                if(!currentMaterialFile.isEmpty()){
                    Material mat = loadMaterial(currentMaterialFile);
                    materials.put(mat.getName(), mat);
                }
                currentMaterialFile.clear();
                currentMaterialFile.add(line.replace("newmtl", "").trim());
            } else {
                currentMaterialFile.add(line);
            }
        }
        return materials;
    }

    private List<Mesh> loadMeshesFromFile(String fileName){
        return null;
    }

    private Material loadMaterial(List<String> lines){
        String name = lines.getFirst();
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

    private List<Vector4f> getVec4f(String prefix, List<String> lines, Vector4f defaultValue){
        List<String> matchedLines = getLinesWith(prefix, lines);

        if (matchedLines.isEmpty()) {
            return List.of(defaultValue);
        }

        return matchedLines.stream()
                .map(this::convertToVec4f)
                .filter(Objects::nonNull)
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
