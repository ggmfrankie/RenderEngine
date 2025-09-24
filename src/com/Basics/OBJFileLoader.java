package com.Basics;

import com.Rendering.Light.Material;
import com.Rendering.Mesh.Mesh;
import com.Rendering.Mesh.MeshData;
import com.Rendering.Textures.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.util.*;

import static com.Basics.Utils.debugPrint;
import static com.Basics.Utils.readFile;

public class OBJFileLoader {

    public Mesh loadMesh(String fileName){
        String texture = fileName.replace(".obj", ".png");
        return loadMesh(fileName, texture);
    }

    public Mesh loadMesh(String fileName, String textureName){
        List<Mesh> mesh = loadMeshes(fileName);
        mesh.getFirst().getMaterial().setTexture(new Texture(textureName));
        return mesh.getFirst();
    }

    public List<Mesh> loadMeshes(String fileName){
        List<Mesh> meshes = new ArrayList<>();
        List<String> file = readFile("\\Resources\\Objects\\" + fileName);
        if(file.isEmpty()){
            file = readFile("\\Resources\\Objects\\grass_block.mtl");
        }
        List<String> processedFile = preProcessFile(file);
        List<String> meshFile = null;
        for(String line : processedFile){
            if(line.isEmpty()) continue;
            if(line.charAt(0) == 'g' || line.charAt(0) == 'o'){
                if(meshFile != null) meshes.add(loadMeshWithoutTexture(meshFile));
                meshFile = new ArrayList<>();

            } else if(meshFile != null){
                meshFile.add(line);
            }
        }
        return meshes;
    }

    public Mesh loadMeshWithoutTexture(List<String> processedFile) {
        List<Vector3f> vertices;
        List<Vector2f> textures;
        List<Vector3f> normals;
        List<Face> faces = new ArrayList<>();

        List<Vector3f> finalPositions = new ArrayList<>();
        List<Vector2f> finalTexCoords = new ArrayList<>();
        List<Vector3f> finalNormals = new ArrayList<>();
        List<Integer> indicesList= new ArrayList<>();

        Map<IdxGroup, Integer> uniqueVertexMap = new HashMap<>();

        String materialName = processedFile.getFirst().replaceAll("mtllib ", "").trim();

        List<String> faceSubstrings = getFaceLines(processedFile);

        List<String> vertexSubstrings = getVertexLines(processedFile);
        List<String> textureSubstrings = getTextureLines(processedFile);
        List<String> normalSubstrings = getNormalLines(processedFile);

        vertices = returnValuesV3(vertexSubstrings);
        textures = returnValuesV2(textureSubstrings);
        normals = returnValuesV3(normalSubstrings);

        for(String faceString : faceSubstrings){
            faces.add(new Face(faceString));
        }
        for (Face face : faces) {
            List<Face> triangles = face.triangulate();

            for (Face triangle : triangles) {
                for (IdxGroup idx : triangle.getFaceVertexIndices()) {
                    if (!uniqueVertexMap.containsKey(idx)) {
                        uniqueVertexMap.put(idx, finalPositions.size());
                        finalPositions.add(vertices.get(idx.idxPos - 1));
                        if(idx.idxTextCoord-1 > -1){
                            finalTexCoords.add(textures.get(idx.idxTextCoord - 1));
                        }
                        finalNormals.add(normals.get(idx.idxVecNormal - 1));
                    }
                    indicesList.add(uniqueVertexMap.get(idx));
                }
            }
        }

        MeshData meshData = generateMeshData(
                finalPositions.toArray(new Vector3f[0]),
                finalTexCoords,
                finalNormals,
                indicesList,
                loadMaterial(materialName)
        );

        return new Mesh(meshData);
    }

    public Texture loadTexture(){
        return null;
    }

    private Material loadMaterial(String fileName){
        List<String> file;
        if(fileName.isEmpty()) file = readFile("\\Resources\\Objects\\grass_block.mtl");
        else {
            file = readFile("\\Resources\\Objects\\" + fileName);
        }
        System.out.println("Searching at: \\Resources\\Objects\\" + fileName);

        List<String> processedFile = preProcessFile(file);

        String ambientColorLine = getLinesWith("Ka", processedFile).getFirst();
        String diffuseColorLine = getLinesWith("Kd", processedFile).getFirst();
        String specularColorLine = getLinesWith("Ks", processedFile).getFirst();
        String reflectanceLine = getLinesWith("d", processedFile).getFirst();

        boolean hasTexture = !String.valueOf(getLinesWith("map_Kd", processedFile)).isEmpty();

        Vector4f ambientColor = new Vector4f(f_convertToVec3(ambientColorLine), 1.0f);
        Vector4f diffuseColor = new Vector4f(f_convertToVec3(diffuseColorLine), 1.0f);
        Vector4f specularColor = new Vector4f(f_convertToVec3(specularColorLine), 1.0f);
        float reflectance = Float.parseFloat(reflectanceLine);

        return new Material(ambientColor, diffuseColor, specularColor, reflectance, new Texture(String.valueOf(getLinesWith("map_Kd", processedFile))));
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

    private MeshData generateMeshData(Vector3f[] verticesArray, List<Vector2f> texturesList, List<Vector3f> normalsList, List<Integer> indicesList, Material material){
        float[] vertexArray = new float[verticesArray.length * 3];
        float[] texturesArray = new float[verticesArray.length * 2];
        float[] normalsArray = new float[verticesArray.length * 3];
        int[] indexArray = indicesList.stream().mapToInt(Integer::intValue).toArray();
        int i = 0;
        for(Vector3f vec3 : verticesArray){
            vertexArray[i * 3] = vec3.x;
            vertexArray[i * 3 + 1] = vec3.y;
            vertexArray[i * 3 + 2] = vec3.z;
            i++;
        }

        i = 0;
        for(Vector2f vec2 : texturesList){
            texturesArray[i * 2] = vec2.x;
            texturesArray[i * 2 + 1] = 1 - vec2.y;
            i++;
        }

        i = 0;
        for(Vector3f vec3 : normalsList){
            normalsArray[i * 3] = vec3.x;
            normalsArray[i * 3 + 1] = vec3.y;
            normalsArray[i * 3 + 2] = vec3.z;
            i++;
        }

        return new MeshData(vertexArray, normalsArray, indexArray, texturesArray, material);
    }

    private static Vector3f f_convertToVec3(String line){
        debugPrint("Array before split: ");
        debugPrint(line);
        String[] values = line.split(" ");
        debugPrint(Arrays.toString(values));
        Vector3f vec3 = new Vector3f();
        vec3.x = parseFloat(values[0]);
        vec3.y = parseFloat(values[1]);
        vec3.z = parseFloat(values[2]);
        return vec3;
    }
    private static Vector3i i_convertToVec3(String line){
        debugPrint("Array before split: ");
        debugPrint(line);
        String[] values = line.split(" ");
        debugPrint(Arrays.toString(values));
        Vector3i vec3 = new Vector3i();
        vec3.x = Integer.parseInt(values[0]);
        vec3.y = Integer.parseInt(values[1]);
        vec3.z = Integer.parseInt(values[2]);
        return vec3;
    }
    private static Vector2f f_convertToVec2(String line){
        String[] values = line.split(" ");
        Vector2f vec2 = new Vector2f();
        vec2.x = parseFloat(values[0]);
        vec2.y = parseFloat(values[1]);

        return vec2;
    }
    private static List<Vector3f> returnValuesV3(List<String> lines){
        List<Vector3f> vec3s = new ArrayList<>();
        for(String line : lines){
            vec3s.add(f_convertToVec3(line));
        }
        return vec3s;
    }
    private static Float parseFloat(String value){
        if(value.isEmpty()){
            debugPrint("value was: "+value);
            return 0.0f;
        }
        value = value.replace("[", "");
        value = value.replace("]", "");

        return Float.parseFloat(value);
    }

    private static List<Vector2f> returnValuesV2(List<String> lines){
        List<Vector2f> vec2s = new ArrayList<>();
        for(String line : lines){
            vec2s.add(f_convertToVec2(line));
        }
        return vec2s;
    }

    private static List<String> getLinesWith(String key, List<String> file){
        List<String> Lines = new ArrayList<>();

        for(String lineString : file){
            if(lineString.startsWith(key+ " ")){
                lineString = lineString.replace(key+ " ", "");
                Lines.add(lineString);
            }
        }
        return Lines;
    }

    private static List<String> getVertexLines(List<String> file){
        return getLinesWith("v", file);
    }

    private static List<String> getFaceLines(List<String> file){
        return getLinesWith("f", file);
    }

    private static List<String> getTextureLines(List<String> file){
        return getLinesWith("vt", file);
    }

    private static List<String> getNormalLines(List<String> file){
        return getLinesWith("vn", file);
    }

    protected static class Face {
        private final List<IdxGroup> idxGroups;

        public Face(String substring) {
            substring = substring.replace("f ", "");
            String[] vertexTokens = substring.split(" ");

            idxGroups = new ArrayList<>();
            for (String token : vertexTokens) {
                idxGroups.add(parseVertexData(token));
            }
        }

        public List<Face> triangulate() {
            List<Face> result = new ArrayList<>();

            if (idxGroups.size() < 3) return result; // invalid face

            for (int i = 1; i < idxGroups.size() - 1; i++) {
                Face triangle = new Face(idxGroups.getFirst(), idxGroups.get(i), idxGroups.get(i + 1));
                result.add(triangle);
            }

            return result;
        }

        private Face(IdxGroup v0, IdxGroup v1, IdxGroup v2) {
            this.idxGroups = List.of(v0, v1, v2);
        }

        private IdxGroup parseVertexData(String token) {
            String[] parts = token.split("/");

            IdxGroup group = new IdxGroup();
            group.idxPos = parts.length > 0 && !parts[0].isEmpty() ? Integer.parseInt(parts[0]) : -1;
            group.idxTextCoord = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : -1;
            group.idxVecNormal = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : -1;
            return group;
        }

        public List<IdxGroup> getFaceVertexIndices() {
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
