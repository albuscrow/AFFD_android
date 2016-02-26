package ac.affd_android.app.GL;

import android.content.Context;
import android.text.InputFilter;
import android.util.Log;
import android.widget.ListView;

import java.io.*;
import java.util.*;
import java.util.concurrent.TransferQueue;

/**
 * Created by ac on 2/26/16.
 */
public class ACOBJ {
    private final String TAG = "ACOBJ";
    private double max_x = Double.MIN_VALUE, min_x = Double.MAX_VALUE,
            max_y = Double.MIN_VALUE, min_y = Double.MAX_VALUE,
            max_z = Double.MIN_VALUE, min_z = Double.MAX_VALUE;

    public ACOBJ(String objFileName, String mtlFileName, Context c) throws IOException {
        InputStream is = c.getAssets().open(objFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        List<Vec3<Double>> vertices = new ArrayList<>();
        List<Vec3<Double>> normals = new ArrayList<>();
        List<Vec3<Double>> texCoords = new ArrayList<>();
        List<String[]> tempFaceTokens = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            String[] tokens = line.split(" ");
            switch (tokens[0]) {
                case "v":
                    double x = Double.parseDouble(tokens[1]);
                    double y = Double.parseDouble(tokens[2]);
                    double z = Double.parseDouble(tokens[3]);
                    vertices.add(new Vec3<>(x, y, z));
                    updateMaxMin(x, y, z);
                    break;
                case "vn":
                    double xn = Double.parseDouble(tokens[1]);
                    double yn = Double.parseDouble(tokens[2]);
                    double zn = Double.parseDouble(tokens[3]);
                    normals.add(new Vec3<>(xn, yn, zn));
                    break;
                case "vt":
                    double xt = Double.parseDouble(tokens[1]);
                    double yt = Double.parseDouble(tokens[2]);
                    double zt = Double.parseDouble(tokens[3]);
                    texCoords.add(new Vec3<>(xt, yt, zt));
                    break;
                case "f":
                    tempFaceTokens.add(Arrays.copyOfRange(tokens, 1, tokens.length));
                    break;

                default:
                    Log.e(TAG, "unknown element: " + tokens[0]);
            }
        }
        Point.addData(vertices, normals, texCoords);
        for (String[] faceToken : tempFaceTokens) {
            if (faceToken.length == 3 || faceToken.length == 4) {
                parseFace(faceToken[0], faceToken[1], faceToken[2]);
                if (faceToken.length == 4) {
                    parseFace(faceToken[0], faceToken[2], faceToken[3]);
                }
            } else {
                Log.e(TAG, "can noly handle 3 or 4 points in one face");
            }
        }
    }

    private static Map<Integer, List<Triangle>> trianglePositionMap = new HashMap<>();

    private void parseFace(String p0String, String p1String, String p2String) {
        Triangle t = new Triangle();
        t.p0 = Point.getPoint(p0String);
        t.p1 = Point.getPoint(p1String);
        t.p2 = Point.getPoint(p2String);
        addTriangleToPositionMap(t, t.p0.positionIndex);
        addTriangleToPositionMap(t, t.p1.positionIndex);
        addTriangleToPositionMap(t, t.p2.positionIndex);
        //todo add adjacent table;
    }

    private void addTriangleToPositionMap(Triangle t, int key) {
        List<Triangle> temp = trianglePositionMap.get(key);
        if (temp == null) {
            temp = new ArrayList<>();
            trianglePositionMap.put(key, temp);
        }
        temp.add(t);
    }

    private void updateMaxMin(double x, double y, double z) {
        min_x = Math.min(min_x, x);
        max_x = Math.max(max_x, x);

        min_y = Math.min(min_y, y);
        max_y = Math.max(max_y, y);

        min_z = Math.min(min_z, z);
        max_z = Math.max(max_z, z);
    }


    public static class Vec3<T> {
        public T x;
        public T y;
        public T z;

        public Vec3(T x, T y, T z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

//        public static Vec3<Double> makeVec3Double(String x, String y, String z) {
//            return new Vec3<>(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
//        }
    }

    public static class Point extends ACRoot{
        public long id;
        public Vec3<Double> position;
        public Vec3<Double> normal;
        public Vec3<Double> texCoord;

        public int positionIndex;
        public int normalIndex;
        public int texCoordIndex;

        private static List<Vec3<Double>> vertices;
        private static List<Vec3<Double>> normals;
        private static List<Vec3<Double>> texCoords;
        private static Map<String, Point> pointPool = new HashMap<>();

        public static Point getPoint(String pointString) {
            Point point = pointPool.get(pointString);
            if (point == null) {
                String[] indexes = pointString.split("/");
                point = new Point();
                point.positionIndex = Integer.parseInt(indexes[0]) - 1;
                point.position = vertices.get(point.positionIndex);
                if (indexes[1].length() != 0) {
                    point.texCoordIndex = Integer.parseInt(indexes[1]) - 1;
                    point.texCoord = texCoords.get(point.texCoordIndex);
                }
                if (indexes.length == 3) {
                    point.normalIndex = Integer.parseInt(indexes[2]) - 1;
                    point.normal = normals.get(point.normalIndex);
                }
                pointPool.put(pointString, point);

            }
            return point;
        }

        public static void addData(List<Vec3<Double>> vertices, List<Vec3<Double>> normals, List<Vec3<Double>> texCoords) {
            Point.vertices = vertices;
            Point.normals = normals;
            Point.texCoords = texCoords;
        }
    }

    public static class Triangle extends ACRoot{
        public final static int EDGE20 = 0;
        public final static int EDGE01 = 1;
        public final static int EDGE12 = 2;

        public Point p0, p1, p2;
        public Triangle t0, t1, t2;
        public int adjacent_dege20, adjacent_dege01, adjacent_dege12;

    }

    private static class ACRoot {
        public long id;

        public ACRoot() {
            this.id = genId();
        }

        private static long currentMaxId = -1;

        private static long genId() {
            return ++currentMaxId;
        }
    }
}
