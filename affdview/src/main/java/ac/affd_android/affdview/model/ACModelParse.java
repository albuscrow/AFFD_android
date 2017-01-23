package ac.affd_android.affdview.model;

import ac.affd_android.affdview.Util.ByteUtil;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by ac on 2/26/16.
 * some describe
 */
public class ACModelParse {

    private static final String TAG = "ACOBJ";
    private final List<Vec3f> vertices = new ArrayList<>();
    private final List<Vec3f> normals = new ArrayList<>();
    private final List<Vec2> texCoords = new ArrayList<>();
    private Vec3f length;

    private Vec3f minPoint = new Vec3f(Float.MAX_VALUE);
    private Vec3f maxPoint = new Vec3f(Float.MIN_VALUE);

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
                    final Vec3f position = new Vec3f(Arrays.copyOfRange(tokens, 1, tokens.length));
                    vertices.add(position);
                    updateMaxMin(position);
                    break;
                case "vn":
                    normals.add(new Vec3f(Arrays.copyOfRange(tokens, 1, tokens.length)).normalize());
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
        length = maxPoint.subtract(minPoint);
        Float d = length.maxComponent() / 2;
        length = length.div(d);
        d *= 1.3f;
        Vec3f centre = minPoint.mid(maxPoint);
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
        ByteBuffer bb = ByteUtil.genDirectBuffer(points.size() * Point.SIZE_AS_BYTE
                + triangles.size() * Triangle.SIZE_AS_BYTE);
        bb.put(getPointsAsByteBuffer());
        bb.put(getIndexAndAdjacentAsByteBuffer());
        bb.flip();
        return bb;
    }

    private ByteBuffer getPointsAsByteBuffer() {
        ByteBuffer bb = ByteUtil.genBuffer(points.size() * Point.SIZE_AS_BYTE);
        for (Point p : points) {
            bb.put(p.toByteBuffer());
        }
        bb.flip();
        return bb;
    }

    private ByteBuffer getIndexAndAdjacentAsByteBuffer() {
        ByteBuffer bb = ByteUtil.genBuffer(triangles.size() * Triangle.SIZE_AS_BYTE);
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

    private void updateMaxMin(Vec3f position) {
        minPoint = minPoint.min(position);
        maxPoint = maxPoint.max(position);
    }

    public int getPointNumber() {
        return points.size();
    }

    public int getTriangleNumber() {
        return triangles.size();
    }


    private Map<String, Point> pointPool = new HashMap<>();

    private Point getPoint(String pointString) {
        Point point = pointPool.get(pointString);
        if (point == null) {
            String[] indexes = pointString.split("/");
            point = new Point();
            point.positionIndex = Integer.parseInt(indexes[0]) - 1;
            point.position = vertices.get(point.positionIndex);
            if (indexes[1].length() != 0) {
                point.texCoordIndex = Integer.parseInt(indexes[1]) - 1;
                point.texCoord = texCoords.get(point.texCoordIndex);
            } else {
                point.texCoordIndex = -1;
                point.texCoord = new Vec2(0f, 0f);
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

    private int currentMaxPointId = -1;

    public class Point extends ACRoot implements Comparable<Point> {
        static final int SIZE_AS_FLOAT = 8;
        static final int SIZE_AS_BYTE = SIZE_AS_FLOAT * ByteUtil.FLOAT_BYTE_SIZE;

        public Vec3f position;
        public Vec3f normal;
        Vec2 texCoord;

        int positionIndex;
        int normalIndex;
        int texCoordIndex;


        protected int genId() {
            return ++currentMaxPointId;
        }

        ByteBuffer toByteBuffer() {
            ByteBuffer bb = ByteUtil.genBuffer(SIZE_AS_BYTE);
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
        final static int SIZE_AS_BYTE = 32;
        final static int EDGE20 = 0;
        final static int EDGE01 = 1;
        final static int EDGE12 = 2;
        final static int NONE = -1;

        Point p0, p1, p2;
        Triangle t20, t01, t12;
        int adjacent_dege20, adjacent_dege01, adjacent_dege12;

        private int[] adjacentTable;

        protected int genId() {
            return ++currentMaxTriangleId;
        }

        void buildAdjacent() throws Exception {
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

        List<Triangle> intersection(List<Triangle> a, List<Triangle> b) {
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

        int[] getAdjacentTable() {
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

        ByteBuffer toByteBuffer() {
            ByteBuffer bb = ByteUtil.genBuffer(SIZE_AS_BYTE);
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

    }

    private static class ACRoot {
        public int id;

        ACRoot() {
            this.id = genId();
        }

        protected int genId() {
            return 0;
        }

    }


    public Vec3f getLength() {
        return length;
    }

}
