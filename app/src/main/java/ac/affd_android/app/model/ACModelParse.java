package ac.affd_android.app.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;

/**
 * Created by ac on 2/26/16.
 * some describe
 */
public class ACModelParse {
    public enum InputType {
        OBJ, BEZIER
    }

    private static final String TAG = "ACOBJ";
    private final List<Vec3> vertices = new ArrayList<>();
    private final List<Vec3> normals = new ArrayList<>();
    private final List<Vec2> texCoords = new ArrayList<>();

    private Vec3 minPoint = new Vec3(Float.MAX_VALUE);
    private Vec3 maxPoint = new Vec3(Float.MIN_VALUE);

    private List<Triangle> triangles = new ArrayList<>();

    public ACModelParse(InputStream objInputStream, InputStream mtlInputStream, InputType inputType) throws Exception {
        switch (inputType) {
            case OBJ:
                parseOBJ(objInputStream, mtlInputStream);
                break;
            case BEZIER:
                //todo
                Log.e(TAG, "bezier path unimplemented");
                break;
            default:
                Log.e(TAG, "input type error");
        }
    }

    private void parseOBJ(InputStream objInputStream, InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
        String line;
        List<String[]> tempFaceTokens = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            String[] tokens = line.split(" ");
            switch (tokens[0]) {
                case "v":
                    final Vec3 position = new Vec3(Arrays.copyOfRange(tokens, 1, tokens.length));
                    vertices.add(position);
                    updateMaxMin(position);
                    break;
                case "vn":
                    normals.add(new Vec3(Arrays.copyOfRange(tokens, 1, tokens.length)).normalize());
                    break;
                case "vt":
                    texCoords.add(new Vec2(Arrays.copyOfRange(tokens, 1, tokens.length)));
                    break;
                case "f":
                    tempFaceTokens.add(Arrays.copyOfRange(tokens, 1, tokens.length));
                    break;

                default:
                    Log.e(TAG, "unknown element: " + tokens[0]);
            }
        }
        //归一化position(-1,1)
        normalizedPosition();

        parseFace(tempFaceTokens);

        //build points list
        points = new ArrayList<>(pointPool.values());
        Collections.sort(points);

        buildAdjacentTable();
    }

    private void parseFace(List<String[]> tempFaceTokens) {
        for (String[] token : tempFaceTokens) {
            if (token.length == 3 || token.length == 4) {
                parseFace(token[0], token[1], token[2]);
                if (token.length == 4) {
                    parseFace(token[0], token[2], token[3]);
                }
            } else {
                Log.e(TAG, "can noly handle 3 or 4 points in one face");
            }
        }
    }

    private void normalizedPosition() {
        Float d = maxPoint.subtract(minPoint).maxComponent() / 2;
        Vec3 centre = minPoint.mid(maxPoint);
        for (int i = 0; i < vertices.size(); i++) {
            vertices.set(i, vertices.get(i).subtract(centre).div(d));
        }
    }

    private void buildAdjacentTable() throws Exception {
        for (Triangle t : triangles) {
            t.buildAdjacent();
        }
    }

    public ByteBuffer getDataForComputeShader() {
        ByteBuffer bb = ByteBuffer
                .allocateDirect(points.size() * Point.SIZE_AS_BYTE + triangles.size() * Triangle.SIZE_AS_BYTE)
                .order(ByteOrder.nativeOrder());
        bb.put(getPointsAsByteBuffer());
        bb.put(getIndexAndAdjacentAsByteBuffer());
        bb.flip();
        return bb;
    }

    public ByteBuffer getPointsAsByteBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(points.size() * Point.SIZE_AS_BYTE).order(ByteOrder.nativeOrder());
        for (Point p : points) {
            bb.put(p.toByteBuffer());
        }
        bb.flip();
        return bb;
    }

    public ByteBuffer getIndexAndAdjacentAsByteBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(triangles.size() * Triangle.SIZE_AS_BYTE).order(ByteOrder.nativeOrder());
        for (Triangle t : triangles) {
            bb.put(t.toByteBuffer());
        }
        bb.flip();
        return bb;
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

    private void updateMaxMin(Vec3 position) {
        minPoint = minPoint.min(position);
        maxPoint = maxPoint.max(position);
    }

    public int getPointNumber() {
        return points.size();
    }

    public int getTriangleNumber() {
        return triangles.size();
    }


    public static class Vec2 {
        public final Float x;
        public final Float y;

        public Vec2(String[] tokens) {
            this.x = Float.parseFloat(tokens[0]);
            this.y = Float.parseFloat(tokens[1]);
        }

        public Vec2(Float x, Float y) {
            this.x = x;
            this.y = y;
        }
    }


    public static class Vec3 {
        public final float x;
        public final float y;
        public final float z;

        public Vec3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3(String[] tokens) {
            this.x = Float.parseFloat(tokens[0]);
            this.y = Float.parseFloat(tokens[1]);
            this.z = Float.parseFloat(tokens[2]);
        }

        public Vec3(float xyz) {
            this.x = xyz;
            this.y = xyz;
            this.z = xyz;
        }

        public Vec3 subtract(Vec3 v) {
            return new Vec3(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        public Vec3 div(float v) {
            return new Vec3(this.x / v, this.y / v, this.z / v);
        }

        public Vec3 normalize() {
            float temp = (float) Math.sqrt(x * x + y * y + z * z);
            return this.div(temp);
        }

        public Vec3 add(Vec3 v) {
            return new Vec3(this.x + v.x, this.y + v.y, this.z + v.z);
        }

        public Vec3 mid(Vec3 v) {
            return this.add(v).div(2);
        }

        public Vec3 min(Vec3 v) {
            return new Vec3(Math.min(this.x, v.x),
                    Math.min(this.y, v.y),
                    Math.min(this.z, v.z));
        }

        public Vec3 max(Vec3 v) {
            return new Vec3(Math.max(this.x, v.x),
                    Math.max(this.y, v.y),
                    Math.max(this.z, v.z));
        }

        public Float maxComponent() {
            return Math.max(Math.max(x, y), z);
        }

        @Override
        public String toString() {
            return "x:" + x + " y:" + y + " z:" + z;
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

    private int currentMaxPointId = -1;

    public class Point extends ACRoot implements Comparable<Point> {
        static final int SIZE_AS_BYTE = 32;
        static final int SIZE_AS_FLOAT = 8;

        public Vec3 position;
        public Vec3 normal;
        public Vec2 texCoord;

        public int positionIndex;
        public int normalIndex;
        public int texCoordIndex;


        protected int genId() {
            return ++currentMaxPointId;
        }

        public ByteBuffer toByteBuffer() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE_AS_BYTE).order(ByteOrder.nativeOrder());
            bb.putFloat(position.x);
            bb.putFloat(position.y);
            bb.putFloat(position.z);
            bb.putFloat(texCoord.x);
            bb.putFloat(normal.x);
            bb.putFloat(normal.y);
            bb.putFloat(normal.z);
            bb.putFloat(texCoord.y);
            bb.flip();
            return bb;
        }

        public FloatBuffer toFloatBuffer() {
            return toByteBuffer().asFloatBuffer();
        }


        @Override
        public int compareTo(@NonNull Point another) {
            return id - another.id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Point && id == ((Point) o).id;
        }
    }

    private int currentMaxTriangleId = -1;

    public class Triangle extends ACRoot {
        public final static int SIZE_AS_BYTE = 32;
        public final static int EDGE20 = 0;
        public final static int EDGE01 = 1;
        public final static int EDGE12 = 2;
        public final static int NONE = -1;

        public Point p0, p1, p2;
        public Triangle t20, t01, t12;
        public int adjacent_dege20, adjacent_dege01, adjacent_dege12;

        private int[] adjacentTable;

        protected int genId() {
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
            if (adjacentTable == null) {
                adjacentTable = new int[3];
                adjacentTable[0] = getAdjacentElement(t20, adjacent_dege20);
                adjacentTable[1] = getAdjacentElement(t01, adjacent_dege01);
                adjacentTable[2] = getAdjacentElement(t12, adjacent_dege12);
            }
            return adjacentTable;
        }

        private int getAdjacentElement(Triangle t, int adjacent_dege) {
            if (t == null) {
                return -1;
            } else {
                return (t.id << 2) + adjacent_dege;
            }
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Triangle && id == ((Triangle) o).id;
        }

        public ByteBuffer toByteBuffer() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE_AS_BYTE).order(ByteOrder.nativeOrder());
            bb.putInt(p0.id);
            bb.putInt(p1.id);
            bb.putInt(p2.id);
            bb.putInt(-1);
            for (int i : getAdjacentTable()) {
                bb.putInt(i);
            }
            bb.putInt(-1);
            bb.flip();
            return bb;
        }

        public FloatBuffer toFloatBuffer() {
            return toByteBuffer().asFloatBuffer();
        }
    }

    private static class ACRoot {
        public int id;

        public ACRoot() {
            this.id = genId();
        }

        protected int genId() {
            return 0;
        }

    }
}
