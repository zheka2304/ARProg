package arprog.inc.ar.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by hardik on 13.06.2017.
 */

public class ModelLoader {
    public static class Vec3{
        public Vec3 (float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public float x, y, z;
    }

    private static class Face{
        ArrayList<FaceVertex> verts = new ArrayList<FaceVertex>();
    }

    private static class FaceVertex{
        public int vertex, normal, uv;

        public FaceVertex(int vertex, int normal, int uv) {
            this.vertex = vertex;
            this.uv = uv;
            this.normal = normal;
        }
    }

    public static FaceVertex readFaceVertex (String string){
        String[] splittedLine;
        splittedLine = string.split("/");
        // TODO: Normals, UVs
        return new FaceVertex(Integer.valueOf(splittedLine[0]) - 1, -1, -1);
    }

    public static void addVertices(Model model, Face face, ArrayList<Vec3> vertecies){
        Vec3 vert1 = vertecies.get(face.verts.get(0).vertex);

        for (int i = 1; i < face.verts.size() - 1; i++){
            Vec3 vert2 = vertecies.get(face.verts.get(i).vertex);
            Vec3 vert3 = vertecies.get(face.verts.get(i + 1).vertex);
            model.color4f(1, 1, 1, 1);
            model.vertex3f(vert1.x, vert1.y, vert1.z);
            model.color4f(1, 1, 1, 1);
            model.vertex3f(vert2.x, vert2.y, vert2.z);
            model.color4f(1, 1, 1, 1);
            model.vertex3f(vert3.x, vert3.y, vert3.z);
        }
    }

    public static Model asObj(InputStream inputStream) {
        String line;
        String[] splittedLine;

        ArrayList<Face> faces = new ArrayList<Face>();
        ArrayList<Vec3> vertecies = new ArrayList<Vec3>();
        Model model = new Model();

        // TODO: create *.obj models loader

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = reader.readLine()) != null) {
                splittedLine = line.split(" ");

                if (splittedLine.length != 0) {
                    if (splittedLine[0].equals("v")) {
                        vertecies.add(new Vec3(Float.valueOf(splittedLine[1]) * 0.1f, Float.valueOf(splittedLine[2]) * 0.1f, Float.valueOf(splittedLine[3]) * 0.1f));
                    }
                    else if (splittedLine[0].equals("f")) {
                        Face face = new Face();
                        for (int i = 1; i < splittedLine.length; i++) {
                            face.verts.add(readFaceVertex(splittedLine[i]));
                        }
                        faces.add(face);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Face face: faces) {
            addVertices(model, face, vertecies);
        }

        model.compile();

        return model;
    }
}
