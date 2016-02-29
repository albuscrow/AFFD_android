package ac.affd_android.app.GL;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * Created by ac on 2/26/16.
 */
public class ACOBJ {
    private static final String TAG = "ACOBJ";
    private final ArrayList<Vec3> vertices;
    private final ArrayList<Vec3> normals;
    private final ArrayList<Vec2> texCoords;
    private Float max_x = Float.MIN_VALUE, min_x = Float.MAX_VALUE,
            max_y = Float.MIN_VALUE, min_y = Float.MAX_VALUE,
            max_z = Float.MIN_VALUE, min_z = Float.MAX_VALUE;

    private List<Triangle> triangles = new ArrayList<>();

    public ACOBJ(InputStream objInputStream, InputStream mtlInputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
        String line;
        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        texCoords = new ArrayList<>();
        List<String[]> tempFaceTokens = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            String[] tokens = line.split(" ");
            switch (tokens[0]) {
                case "v":
                    float x = Float.parseFloat(tokens[1]);
                    float y = Float.parseFloat(tokens[2]);
                    float z = Float.parseFloat(tokens[3]);
                    vertices.add(new Vec3(x, y, z));
                    updateMaxMin(x, y, z);
                    break;
                case "vn":
                    float xn = Float.parseFloat(tokens[1]);
                    float yn = Float.parseFloat(tokens[2]);
                    float zn = Float.parseFloat(tokens[3]);
                    Vec3 normal = new Vec3(xn, yn, zn);
                    normal.normalize();
                    normals.add(normal);
                    break;
                case "vt":
                    float xt = Float.parseFloat(tokens[1]);
                    float yt = Float.parseFloat(tokens[2]);
                    texCoords.add(new Vec2(xt, yt));
                    break;
                case "f":
                    tempFaceTokens.add(Arrays.copyOfRange(tokens, 1, tokens.length));
                    break;

                default:
                    Log.e(TAG, "unknown element: " + tokens[0]);
            }
        }
        //归一化position(-1,1)
        Vec3 centre = new Vec3((min_x + max_x) / 2, (min_y + max_y) / 2, (min_z + max_z) / 2);
        for (Vec3 p : vertices) {
            p.subtract(centre);
            p.div(Math.max(max_x - min_x, Math.max(max_y - min_y, max_z - min_z)) / 2);
        }

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
        //build points list
        points = new ArrayList<>(pointPool.values());
        Collections.sort(points);

        buildAdjacentTable();
    }

    private void buildAdjacentTable() throws Exception {
        for (Triangle t : triangles) {
            t.buildAdjacent();
        }
    }

    public ByteBuffer getDataForComputeShader() {
        List<Point> points = getPoints();
        ByteBuffer bb = ByteBuffer.allocateDirect(points.size() * 32 + triangles.size() * 32).order(ByteOrder.nativeOrder());
        for (Point p : points) {
            bb.putFloat(p.position.x);
            bb.putFloat(p.position.y);
            bb.putFloat(p.position.z);
            bb.putFloat(p.texCoord.x);
            bb.putFloat(p.normal.x);
            bb.putFloat(p.normal.y);
            bb.putFloat(p.normal.z);
            bb.putFloat(p.texCoord.y);
        }

        for (Triangle t : triangles) {
            bb.putInt((int) t.p0.id);
            bb.putInt((int) t.p1.id);
            bb.putInt((int) t.p2.id);
            bb.putInt(-1);
            for (int i : t.getAdjacentTable()) {
                bb.putInt(i);
            }
            bb.putInt(-1);
        }
        bb.flip();
        return bb;
    }

    public FloatBuffer getPointsByteArray() {
        List<Point> points = getPoints();
        FloatBuffer fb = ByteBuffer.allocateDirect(points.size() * 32).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (Point p : points) {
            fb.put(p.position.x);
            fb.put(p.position.y);
            fb.put(p.position.z);
            fb.put(p.texCoord.x);
            fb.put(p.normal.x);
            fb.put(p.normal.y);
            fb.put(p.normal.z);
            fb.put(p.texCoord.y);
        }
        fb.flip();
        return fb;
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

    public IntBuffer getAdjTable() {
        IntBuffer ib = ByteBuffer.allocateDirect(triangles.size() * 12).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (Triangle t : triangles) {
            ib.put(t.getAdjacentTable());
        }
        ib.flip();
        return ib;
    }

    private Map<Integer, List<Triangle>> trianglePositionMap = new HashMap<>();

    private void parseFace(String p0String, String p1String, String p2String) {
        Triangle t = new Triangle();
        t.p0 = getPoint(p0String);
        t.p1 = getPoint(p1String);
        t.p2 = getPoint(p2String);
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

    private void updateMaxMin(float x, float y, float z) {
        min_x = Math.min(min_x, x);
        max_x = Math.max(max_x, x);

        min_y = Math.min(min_y, y);
        max_y = Math.max(max_y, y);

        min_z = Math.min(min_z, z);
        max_z = Math.max(max_z, z);
    }

    public int getPointNumber() {
        return points.size();
    }

    public int getTriangleNumber() {
        return triangles.size();
    }


    public static class Vec2 {
        public float x;
        public float y;

        public Vec2(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }


    public static class Vec3 {
        public float x;
        public float y;
        public float z;

        public Vec3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void subtract(Vec3 centre) {
            this.x -= centre.x;
            this.y -= centre.y;
            this.z -= centre.z;
        }

        public void div(float v) {
            this.x /= v;
            this.y /= v;
            this.z /= v;
        }

        public void normalize() {
            float temp = (float) Math.sqrt(x * x + y * y + z * z);
            this.x /= temp;
            this.y /= temp;
            this.z /= temp;
        }
    }

    private Map<String, Point> pointPool = new HashMap<>();
    public Point getPoint(String pointString) {
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

    private List<Point> points = null;
    public List<Point> getPoints() {
        return points;
    }
    private long currentMaxPointId = -1;
    public class Point extends ACRoot implements Comparable<Point> {
        public Vec3 position;
        public Vec3 normal;
        public Vec2 texCoord;

        public int positionIndex;
        public int normalIndex;
        public int texCoordIndex;


        protected long genId() {
            return ++currentMaxPointId;
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

        @Override
        public boolean equals(Object o) {
            return o instanceof Point && id == ((Point) o).id;
        }
    }

    private long currentMaxTriangleId = -1;
    public class Triangle extends ACRoot {
        public final static int EDGE20 = 0;
        public final static int EDGE01 = 1;
        public final static int EDGE12 = 2;
        public final static int NONE = -1;

        public Point p0, p1, p2;
        public Triangle t20, t01, t12;
        public int adjacent_dege20, adjacent_dege01, adjacent_dege12;

        private int[] adjacentTable;

        protected long genId() {
            return ++currentMaxTriangleId;
        }

        public void buildAdjacent() throws Exception {
            List<Triangle> p0AdjacentTriangle = trianglePositionMap.get(p0.positionIndex);
            List<Triangle> p1AdjacentTriangle = trianglePositionMap.get(p1.positionIndex);
            List<Triangle> p2AdjacentTriangle = trianglePositionMap.get(p2.positionIndex);

            List<Triangle> intersection20 = intersection(p2AdjacentTriangle, p0AdjacentTriangle);
            List<Triangle> intersection01 = intersection(p0AdjacentTriangle, p1AdjacentTriangle);
            List<Triangle> intersection12 = intersection(p1AdjacentTriangle, p2AdjacentTriangle);
            t20 = findAdjacentTriangle(intersection20);
            t01 = findAdjacentTriangle(intersection01);
            t12 = findAdjacentTriangle(intersection12);
            adjacent_dege20 = findAdjacentTriangleEdge(t20, p0);
            adjacent_dege01 = findAdjacentTriangleEdge(t01, p1);
            adjacent_dege12 = findAdjacentTriangleEdge(t12, p2);
        }

        private int findAdjacentTriangleEdge(Triangle t, Point p) throws Exception {
            if (t == null) {
                return NONE;
            } else {
                if (p.positionIndex == t.p2.positionIndex) {
                    return EDGE20;
                } else if (p.positionIndex == t.p0.positionIndex) {
                    return EDGE01;
                } else if (p.positionIndex == t.p1.positionIndex) {
                    return EDGE12;
                } else {
                    throw new Exception("find adjacent triangle error");
                }
            }
        }

        private Triangle findAdjacentTriangle(List<Triangle> intersection) throws Exception {
            if (intersection.size() == 0) {
                Log.e(TAG, "find adjacent table error");
                throw new Exception("find adjacent table error");
            } else if (intersection.size() == 1) {
                if (intersection.get(0).equals(this)) {
                    return null;
                } else {
                    Log.e(TAG, "find adjacent table error");
                    throw new Exception("find adjacent table error");
                }
            } else if (intersection.size() == 2) {
                if (intersection.get(0).equals(this)) {
                    return intersection.get(1);
                } else if (intersection.get(1).equals(this)) {
                    return intersection.get(0);
                } else {
                    Log.e(TAG, "find adjacent table error");
                    throw new Exception("find adjacent table error");
                }
            } else {
                Log.e(TAG, "find adjacent table error");
                throw new Exception("find adjacent table error");
            }
        }

        public List<Triangle> intersection(List<Triangle> a, List<Triangle> b) {
            List<Triangle> res = new ArrayList<>();
            for (Triangle ta : a) {
                for (Triangle tb : b) {
                    if (ta.equals(tb)) {
                        res.add(ta);
                    }
                }
            }
            return res;
        }

        public int[] getAdjacentTable() {
            int[] res = new int[3];
            res[0] = getAdjacentElement(t20, adjacent_dege20);
            res[1] = getAdjacentElement(t01, adjacent_dege01);
            res[2] = getAdjacentElement(t12, adjacent_dege12);
            return res;
        }

        private int getAdjacentElement(Triangle t, int adjacent_dege) {
            if (t == null) {
                return -1;
            } else {
                return (int) ((t.id << 2) + adjacent_dege);
            }
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Triangle && id == ((Triangle) o).id;
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
