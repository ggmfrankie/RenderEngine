package com.Basics;

import com.Rendering.Light.Material;

import java.util.ArrayList;
import java.util.List;

import static com.Basics.Utils.readFile;

public class OBJLoader {
    List<Material> materials;

    public OBJLoader() {
        materials = new ArrayList<>();
    }

    private List<Material> loadMaterialsFromFile(String fileName){
        List<String> file;
        List<String> currentMaterialFile = new ArrayList<>();
        if(fileName.isEmpty()) file = readFile("\\Resources\\Objects\\grass_block.mtl");
        else file = readFile("\\Resources\\Objects\\" + fileName);
        file = preProcessFile(file);
        for(String line : file){
            if(line.startsWith("newmtl")){
                if(!currentMaterialFile.isEmpty()) loadMaterial(currentMaterialFile);

                currentMaterialFile.clear();
                currentMaterialFile.add(line.replace("newmtl", "").trim());
            } else {
                currentMaterialFile.add(line);
            }
        }




    }

    private Material loadMaterial(List<String> lines){
        String name = lines.getFirst();

        String ambientColorLine = getLinesWith("Ka", lines).getFirst();
        String diffuseColorLine = getLinesWith("Kd", lines).getFirst();
        String specularColorLine = getLinesWith("Ks", lines).getFirst();
        String reflectanceLine = getLinesWith("d", lines).getFirst();

        String textureName = getLinesWith("map_Kd", lines).getFirst();
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

    protected static class Face {
        private final List<OBJFileLoader.IdxGroup> idxGroups;

        public Face(String substring) {
            substring = substring.replace("f ", "");
            String[] vertexTokens = substring.split(" ");

            idxGroups = new ArrayList<>();
            for (String token : vertexTokens) {
                idxGroups.add(parseVertexData(token));
            }
        }

        public List<OBJFileLoader.Face> triangulate() {
            List<OBJFileLoader.Face> result = new ArrayList<>();

            if (idxGroups.size() < 3) return result; // invalid face

            for (int i = 1; i < idxGroups.size() - 1; i++) {
                OBJFileLoader.Face triangle = new OBJFileLoader.Face(idxGroups.getFirst(), idxGroups.get(i), idxGroups.get(i + 1));
                result.add(triangle);
            }

            return result;
        }

        private Face(OBJFileLoader.IdxGroup v0, OBJFileLoader.IdxGroup v1, OBJFileLoader.IdxGroup v2) {
            this.idxGroups = List.of(v0, v1, v2);
        }

        private OBJFileLoader.IdxGroup parseVertexData(String token) {
            String[] parts = token.split("/");

            OBJFileLoader.IdxGroup group = new OBJFileLoader.IdxGroup();
            group.idxPos = parts.length > 0 && !parts[0].isEmpty() ? Integer.parseInt(parts[0]) : -1;
            group.idxTextCoord = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : -1;
            group.idxVecNormal = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : -1;
            return group;
        }

        public List<OBJFileLoader.IdxGroup> getFaceVertexIndices() {
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
