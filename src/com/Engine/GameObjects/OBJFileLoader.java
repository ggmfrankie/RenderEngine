package com.Engine.GameObjects;

import com.Basics.Parsing;
import com.Rendering.Light.Material;
import com.Rendering.Mesh.Mesh;
import com.Rendering.Textures.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;
import java.util.stream.Collectors;

import static com.Basics.Utils.readFile;

public class OBJFileLoader {
    Map<String, Material> materials;
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
                        .trim());


        vertexSubstrings = getLinesWith("v", file);
        textureSubstrings = getLinesWith("vt", file);
        normalSubstrings = getLinesWith("vn", file);

        printFile(vertexSubstrings);
        printFile(textureSubstrings);
        printFile(normalSubstrings);

        materials = loadMaterialsFromFile(materialFile);

        List<Mesh> meshes = new ArrayList<>();
        List<String> currentFaces = new ArrayList<>();

        boolean foundGroup = false;
        String currentMaterial = "";

        for(String line : file){
            if(line.startsWith("usemtl")){
                currentMaterial = line.replace("usemtl", "").trim();
                continue;
            }
            if(line.startsWith("g ") || line.startsWith("o ")){
                if(!currentFaces.isEmpty()){
                    meshes.add(loadMesh(currentFaces, materials.getOrDefault(currentMaterial, new Material("default"))));
                }
                currentFaces = new ArrayList<>();
                currentFaces.add(line);
                foundGroup = true;
                continue;
            }
            if(!foundGroup) continue;
            if(line.startsWith("f ")) currentFaces.add(line);
        }
        if(!currentFaces.isEmpty()){
            meshes.add(loadMesh(currentFaces, materials.getOrDefault(currentMaterial, new Material("default"))));
        }
        return meshes;
    }

    private Mesh loadMesh(List<String> file, Material material){
        String name = file.getFirst().replace("g ", "").replace("o ", "").trim();

        List<Vector3f> vertices;
        List<Vector2f> textures;
        List<Vector3f> normals;

        List<Face> faces = new ArrayList<>();

        List<Vector3f> finalPositions = new ArrayList<>();
        List<Vector2f> finalTexCoords = new ArrayList<>();
        List<Vector3f> finalNormals = new ArrayList<>();
        List<Integer> indicesList= new ArrayList<>();

        return null;
    }

    private Map<String, Material> loadMaterialsFromFile(List<String> file){
        file = preProcessFile(file);
        return group(file, "newmtl").stream()
                .map(this::loadMaterial)
                .collect(Collectors.toMap(
                        Material::getName,
                        m -> m,
                        (a, b) -> a,
                        HashMap::new
                ));
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

    private List<List<String>> group(List<String> list, String key){
        boolean foundGroup = false;
        List<List<String>> result = new ArrayList<>();
        List<String> currentFile = new ArrayList<>();
        for(String line : list){
            if(line.startsWith(key)){
                if(!currentFile.isEmpty()){
                    result.add(currentFile);
                }
                currentFile.clear();
                currentFile.add(line.replace(key, "").trim());
                foundGroup = true;
                _PRINT_("Adding to materialFile:");
                _PRINT_(currentFile.getLast());
                continue;
            }
            if(foundGroup) currentFile.add(line);
        }
        if (!currentFile.isEmpty()) {
            result.add(currentFile);
        }
        return result;
    }

    private List<String> preProcessFile(List<String> file){
        return  file.stream()
                .map(s -> s.replaceAll("\\s+", " "))
                .map(s -> s.replaceAll("#.*$", ""))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
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
                .map(Parsing::parseVec3f)
                .map(vec3f -> new Vector4f(vec3f, 1.0f))
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
    private <T> void _PRINT_(T input){
        if(DEBUG) return;
        System.out.println(input.toString());
    }

    protected static class Face{
        private final List<IdxGroup> idxGroups;

        public Face(String substring){
            substring = substring.replaceFirst("^f\\s+", "");
            String[] vertexTokens = substring.split(" ");

            idxGroups = new ArrayList<>();
            for (String token : vertexTokens) {
                idxGroups.add(parseVertexData(token));
            }
        }

        private IdxGroup parseVertexData(String token){
            String[] parts = token.split("/");

            return new IdxGroup(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        }
    }

    protected static record IdxGroup(int pos, int tex, int nor){

    }

    protected static class IdxGroup1{
        public static final int NO_VALUE = -1;
        public int idxPos;
        public int idxTextCoord;
        public int idxVecNormal;

        public IdxGroup1(){
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OBJLoader.IdxGroup other)) return false;
            return idxPos == other.idxPos &&
                    idxTextCoord == other.idxTextCoord &&
                    idxVecNormal == other.idxVecNormal;
        }

        @Override
        public int hashCode() {
            return Objects.hash(idxPos, idxTextCoord, idxVecNormal);
        }

        @Override
        public String toString(){
            return "idxPos: " +idxPos+ " idxTextcoord: " +idxTextCoord+ " idxVecNormal: " +idxVecNormal;
        }
    }
}
