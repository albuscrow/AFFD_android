package ac.affd_android.app.GL;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.zip.InflaterInputStream;

/**
 * Created by ac on 2/26/16.
 */
public class ACOBJ {
    private final String TAG = "ACOBJ";
    private double max_x = Double.MIN_VALUE, min_x = Double.MAX_VALUE,
            max_y = Double.MIN_VALUE, min_y = Double.MAX_VALUE,
            max_z = Double.MIN_VALUE, min_z = Double.MAX_VALUE;

    private List<Triangle> triangles = new ArrayList<>();

    public ACOBJ(InputStream objInputStream, InputStream mtlInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
        String line;
        List<Vec3<Double>> vertices = new ArrayList<>();
        List<Vec3<Double>> normals = new ArrayList<>();
        List<Vec2<Double>> texCoords = new ArrayList<>();
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
                    texCoords.add(new Vec2<>(xt, yt));
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

    public DoubleBuffer getVertices() {
        List<Point> points = Point.getPoints();
        DoubleBuffer db = ByteBuffer.allocateDirect(points.size() * 24).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        for (Point p : points) {
            db.put(p.position.x);
            db.put(p.position.y);
            db.put(p.position.z);
        }
        db.flip();
        return db;
    }

    public DoubleBuffer getNormal() {
        List<Point> points = Point.getPoints();
        DoubleBuffer db = ByteBuffer.allocateDirect(points.size() * 24).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        for (Point p : points) {
            db.put(p.normal.x);
            db.put(p.normal.y);
            db.put(p.normal.z);
        }
        db.flip();
        return db;
    }

    public DoubleBuffer getTexcoord() {
        List<Point> points = Point.getPoints();
        DoubleBuffer db = ByteBuffer.allocateDirect(points.size() * 16).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        for (Point p : points) {
            db.put(p.texCoord.x);
            db.put(p.texCoord.y);
        }
        db.flip();
        return db;
    }

    public IntBuffer getIndex() {
        IntBuffer db = ByteBuffer.allocateDirect(triangles.size() * 12).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (Triangle t : triangles) {
            db.put((int) t.p0.id);
            db.put((int) t.p1.id);
            db.put((int) t.p2.id);
        }
        db.flip();
        return db;
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
        triangles.add(t);
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


    public static class Vec2<T> {
        public T x;
        public T y;

        public Vec2(T x, T y) {
            this.x = x;
            this.y = y;
        }

//        public static Vec3<Double> makeVec3Double(String x, String y, String z) {
//            return new Vec3<>(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
//        }
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

    public static class Point extends ACRoot implements Comparable<Point>{
        public Vec3<Double> position;
        public Vec3<Double> normal;
        public Vec2<Double> texCoord;

        public int positionIndex;
        public int normalIndex;
        public int texCoordIndex;

        private static long currentMaxId = -1;

        protected long genId() {
            return ++currentMaxId;
        }

        private static List<Vec3<Double>> vertices;
        private static List<Vec3<Double>> normals;
        private static List<Vec2<Double>> texCoords;
        private static Map<String, Point> pointPool = new HashMap<>();

        public static List<Point> getPoints() {
            ArrayList<Point> list = new ArrayList<>(pointPool.values());
            Collections.sort(list);
            return list;
        }

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

        public static void addData(List<Vec3<Double>> vertices, List<Vec3<Double>> normals, List<Vec2<Double>> texCoords) {
            Point.vertices = vertices;
            Point.normals = normals;
            Point.texCoords = texCoords;
        }

        @Override
        public int compareTo(@NonNull Point another) {
            if (id > another.id) {
                return 1;
            } else if (id < another.id) {
                return -1;
            } else {
                return 0;
            }
        }

        static public void init() {
            pointPool.clear();
            currentMaxId = -1;
        }
    }

    public static class Triangle extends ACRoot{
        public final static int EDGE20 = 0;
        public final static int EDGE01 = 1;
        public final static int EDGE12 = 2;

        public Point p0, p1, p2;
        public Triangle t0, t1, t2;
        public int adjacent_dege20, adjacent_dege01, adjacent_dege12;

        private static long currentMaxId = -1;

        protected long genId() {
            return ++currentMaxId;
        }

    }

    private static class ACRoot {
        public long id;

        public ACRoot() {
            this.id = genId();
        }

        protected long genId() {
            return 0;
        }
    }
}
