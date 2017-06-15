package arprog.inc.ar.opengl;

import android.graphics.PorterDuff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import arprog.inc.ar.arprog.MainActivity;
import arprog.inc.ar.arprog.R;

/**
 * Created by hardik on 13.06.2017.
 */

public class ModelLoader {
    public static final boolean SWAP_ZY = true;

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
        return new FaceVertex(
                splittedLine[0].length() > 0 ? Integer.valueOf(splittedLine[0]) : 0,
                splittedLine[2].length() > 0 ? Integer.valueOf(splittedLine[2]) : 0,
                splittedLine[1].length() > 0 ? Integer.valueOf(splittedLine[1]) : 0);
    }

    public static void addVertices(Model model, Face face, ArrayList<Vec3> vertices, ArrayList<Vec3> normals,  ArrayList<Vec3> uvs){
        Vec3 vert1 = vertices.get(face.verts.get(0).vertex);
        Vec3 normal1 = normals.get(face.verts.get(0).normal);
        Vec3 uv1 = uvs.get(face.verts.get(0).uv);

        for (int i = 1; i < face.verts.size() - 1; i++){
            Vec3 vert2 = vertices.get(face.verts.get(i).vertex);
            Vec3 normal2 = normals.get(face.verts.get(i).normal);
            Vec3 uv2 = normals.get(face.verts.get(i).uv);
            Vec3 vert3 = vertices.get(face.verts.get(i + 1).vertex);
            Vec3 normal3 = normals.get(face.verts.get(i + 1).normal);
            Vec3 uv3 = normals.get(face.verts.get(i + 1).uv);
            model.color4f(1, 1, 1, 1);
            model.normal3f(normal1.x, normal1.y, normal1.z);
            model.uv2f(uv1.x, uv1.y);
            model.vertex3f(vert1.x, vert1.y, vert1.z);
            model.color4f(1, 1, 1, 1);
            model.normal3f(normal2.x, normal2.y, normal2.z);
            model.uv2f(uv2.x, uv2.y);
            model.vertex3f(vert2.x, vert2.y, vert2.z);
            model.color4f(1, 1, 1, 1);
            model.normal3f(normal3.x, normal3.y, normal3.z);
            model.uv2f(uv3.x, uv3.y);
            model.vertex3f(vert3.x, vert3.y, vert3.z);
        }
    }

    public static Model asObj(InputStream inputStream, float scale, boolean fixedSize) {
        String line;
        String[] splittedLine;

        ArrayList<Face> faces = new ArrayList<Face>();
        ArrayList<Vec3> vertices = new ArrayList<Vec3>(); vertices.add(new Vec3(0, 0, 0));
        ArrayList<Vec3> normals = new ArrayList<Vec3>(); normals.add(new Vec3(0, 0, 0));
        ArrayList<Vec3> uvs = new ArrayList<Vec3>(); uvs.add(new Vec3(0, 0, 0));
        Model model = new Model(true, true);

        Vec3 vTranslation = new Vec3(0, 0, 0);
        Vec3 vScale = new Vec3(1, 1, 1);
        Vec3 vMax = new Vec3(-99999, -99999, -99999);
        Vec3 vMin = new Vec3(99999, 99999, 99999);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = reader.readLine()) != null) {
                splittedLine = line.split(" ");

                if (splittedLine.length != 0) {
                    if (splittedLine[0].equals("v")) {
                        Vec3 vertex;
                        if (SWAP_ZY)
                            vertex = (new Vec3(Float.valueOf(splittedLine[1]) * scale, Float.valueOf(splittedLine[3]) * scale, Float.valueOf(splittedLine[2]) * scale));
                        else
                            vertex = (new Vec3(Float.valueOf(splittedLine[1]) * scale, Float.valueOf(splittedLine[2]) * scale, Float.valueOf(splittedLine[3]) * scale));
                        vertices.add(vertex);

                        if (fixedSize) {
                            if (vertex.x > vMax.x)
                                vMax.x = vertex.x;
                            if (vertex.x < vMin.x)
                                vMin.x = vertex.x;
                            if (vertex.y > vMax.y)
                                vMax.y = vertex.y;
                            if (vertex.y < vMin.y)
                                vMin.y = vertex.y;
                            if (vertex.z > vMax.z)
                                vMax.z = vertex.z;
                            if (vertex.z < vMin.z)
                                vMin.z = vertex.z;
                        }
                    }
                    else if (splittedLine[0].equals("vn")) {
                        if (SWAP_ZY)
                            normals.add(new Vec3(Float.valueOf(splittedLine[1]), Float.valueOf(splittedLine[3]), Float.valueOf(splittedLine[2])));
                        else
                            normals.add(new Vec3(Float.valueOf(splittedLine[1]), Float.valueOf(splittedLine[2]), Float.valueOf(splittedLine[3])));
                    }
                    else if (splittedLine[0].equals("vt")) {
                        uvs.add(new Vec3(Float.valueOf(splittedLine[1]), Float.valueOf(splittedLine[2]), 0));
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

        if (fixedSize) {
            for (Vec3 vertex : vertices) {
                // TODO: scale vertices
            }
        }

        System.out.println("Model load stats: ");
        System.out.println(" - vertices: " + vertices.size());
        System.out.println(" - normals: " + normals.size());
        System.out.println(" - uvs: " + uvs.size());
        System.out.println(" - faces: " + faces.size());

        for (Face face: faces) {
            addVertices(model, face, vertices, normals, uvs);
        }

        model.compile();

        return model;
    }

    public static Model loadObj(int res, float scale) {
        return asObj(MainActivity.current.getResources().openRawResource(res), scale, false);
    }
}