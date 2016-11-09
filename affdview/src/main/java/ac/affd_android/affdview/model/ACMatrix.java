package ac.affd_android.affdview.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class ACMatrix {
    static class Index {
        int start;
        int end;
        boolean used = false;

        public Index(int i) {
            start = i;
            end = i + 1;
        }

        public Index(int start, int end) {
            if (start >= end) {
                throw new RuntimeException("start must less than end");
            }
            this.start = start;
            this.end = end;
        }

        boolean isInterval() {
            return this.start + 1 != this.end;
        }

        int intervalSize() {
            return this.end - this.start;
        }

        Index change(int i) {
            start = i;
            end = i + 1;
            return this;
        }

        Index change(int s, int e) {
            start = s;
            end = e;
            return this;
        }
    }

    final public int[] shape;
    final public float[] data;

    public ACMatrix(@Nullable float[] data, int... shape) {
        this.shape = shape;
        if (data != null) {
            if (shape.length == 0 && data.length != 1) {
                throw new AssertionError("data length should match the shape!");
            }
            int l = 1;
            for (int i : shape) {
                l *= i;
            }
            if (l != data.length) {
                throw new AssertionError("data length should match the shape!");
            }
            this.data = data;
        } else {
            this.data = new float[size()];
//            Arrays.fill(this.data, 0f);
        }
    }

    public ACMatrix(ACMatrix matrix) {
        this.shape = matrix.shape.clone();
        this.data = matrix.data.clone();
    }

    public ACMatrix(float data) {
        this.shape = new int[0];
        this.data = new float[]{data};
    }

    public ACMatrix(List<ACMatrix> temp) {
        if (temp.size() == 1) {
            this.shape = temp.get(0).shape;
            this.data = temp.get(0).data;
        } else {
            int[] d = temp.get(0).shape;
            for (int i = 1; i < temp.size(); ++i) {
                if (!Arrays.equals(d, temp.get(i).shape)) {
                    throw new RuntimeException("dimensional must same");
                }
            }
            shape = new int[d.length + 1];
            shape[0] = temp.size();
            System.arraycopy(d, 0, shape, 1, d.length);

            final Integer elementSize = temp.get(0).size();
            data = new float[temp.size() * elementSize];
            for (int i = 0; i < temp.size(); ++i) {
                final float[] tempData = temp.get(i).data;
                System.arraycopy(tempData, 0, data, elementSize * i, tempData.length);
            }
        }
    }

    public Integer size() {
        Integer res = 1;
        for (Integer i : shape) {
            res *= i;
        }
        return res;
    }

    public void put(ACMatrix matrix, Index... indices) {
        putHelper(indices, 0, 0, matrix, 0, 0);
    }

    public void put(float[] matrixArray, Index... indices) {
        putHelper(indices, 0, 0, matrixArray, 0, 0);
    }

    public void put(ACMatrix matrix, Integer... indices) {
        put(matrix, indicesInt2Index(indices));
    }

    public void put(Float f, Integer... indices) {
        put(new ACMatrix(f), indices);
    }

    private void putHelper(Index[] index, int i, int offset, ACMatrix matrix, int i2, int offset2) {
        if (i == shape.length) {
            if (i2 != matrix.shape.length) {
                throw new RuntimeException("dimensional error");
            }
            data[offset] = matrix.data[offset2];
        } else {
            if (index[i].isInterval()) {
                if (!Objects.equals(index[i].intervalSize(), matrix.shape[i2])) {
                    throw new RuntimeException();
                }
                for (int ii = index[i].start; ii < index[i].end; ++ii) {
                    putHelper(index, i + 1, offset * shape[i] + ii, matrix, i2 + 1, offset2 * matrix.shape[i2] + ii);
                }
            } else {
                putHelper(index, i + 1, offset * shape[i] + index[i].start, matrix, i2, offset2);
            }
        }
    }

    private void putHelper(Index[] index, int i, int offset, float[] matrixArray, int i2, int offset2) {
        if (i == shape.length) {
            data[offset] = matrixArray[offset2];
        } else {
            if (index[i].isInterval()) {
                for (int ii = index[i].start; ii < index[i].end; ++ii) {
                    putHelper(index, i + 1, offset * shape[i] + ii, matrixArray, i2 + 1, offset2 * index[i].intervalSize() + ii - index[i].start);
                }
            } else {
                putHelper(index, i + 1, offset * shape[i] + index[i].start, matrixArray, i2, offset2);
            }
        }
    }

    public ACMatrix get(Index... indices) {
//        checkDimension(indices.length);
        return new ACMatrix(getArray(indices), getShapeFromIndices(indices));
    }

    public float[] getArray(Index... indices) {
//        checkDimension(indices.length);
        float[] res = new float[getLengthFromIndices(indices)];
        getHelper(indices, 0, 0, res, 0);
        return res;
    }

    private int[] getShapeFromIndices(Index[] indices) {
        List<Integer> t = new ArrayList<>(indices.length);
        for (Index i : indices) {
            if (i.end - i.start != 1) {
                t.add(i.end - i.start);
            }
        }
        int[] res = new int[t.size()];
        for (int i = 0; i < t.size(); ++i) {
            res[i] = t.get(i);
        }
        return res;
    }

    private int getLengthFromIndices(Index[] indices) {
        int res = 1;
        for (Index i : indices) {
            res *= (i.end - i.start);
        }
        return res;
    }

    public ACMatrix get(Integer... indices) {
        return get(indicesInt2Index(indices));
    }

    @NonNull
    private Index[] indicesInt2Index(Integer[] indices) {
        Index[] _indices = new Index[indices.length];
        for (int i = 0; i < indices.length; ++i) {
            if (indices[i] == -1) {
                _indices[i] = new Index(0, shape[i]);
            } else {
                _indices[i] = new Index(indices[i]);
            }
        }
        return _indices;
    }

    private void getHelper(Index[] index, int i, int offset, float[] res, int resOffset) {
        if (i == shape.length) {
            res[resOffset] = data[offset];
        } else {
            final int interval = index[i].intervalSize();
            final int start = index[i].start;
            for (int ii = index[i].start; ii < index[i].end; ++ii) {
                getHelper(index, i + 1, offset * shape[i] + ii, res, resOffset * interval + ii - start);
            }
        }
    }

    public ACMatrix multiply(ACMatrix m) {
        if (shape.length != 2 || m.shape.length != 2) {
            throw new RuntimeException("matrix multiply dimension must 2");
        }
        if (!Objects.equals(shape[1], m.shape[0])) {
            throw new RuntimeException("matrix multiply dimension error");
        }
        ACMatrix res = new ACMatrix(null, shape[0], m.shape[1]);
        for (int i = 0; i < shape[0]; ++i) {
            for (int j = 0; j < m.shape[1]; ++j) {
                float e = 0f;
                for (int k = 0; k < shape[1]; ++k) {
                    e += data[i * shape[1] + k] * m.data[k * m.shape[1] + j];
                }
                res.data[i * m.shape[1] + j] = e;
            }
        }
        return res;
    }

    public ACMatrix T() {
        if (shape.length != 2) {
            throw new RuntimeException("matrix dimension must 2");
        }
        float[] newData = new float[data.length];
        for (int i = 0; i < shape[0]; ++i) {
            for (int j = 0; j < shape[1]; ++j) {
                newData[j * shape[0] + i] = data[i * shape[1] + j];
            }
        }
        return new ACMatrix(newData, shape[1], shape[0]);
    }

//    private void checkDimension(Integer d) {
//        if (d != shape.length) {
//            throw new RuntimeException();
//        }
//    }

    ACMatrix add(Vec3f v3) {
        if (!Arrays.equals(shape, new int[]{3})) {
            throw new RuntimeException("dimension not equal");
        }
        ACMatrix res = new ACMatrix(this);
        for (int i = 0; i < 3; i++) {
            res.data[i] += v3.getComponent(i);
        }
        return res;
    }

    static float[] multiply3X3(float[] m1, float[] m2) {
        float[] res = new float[9];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    res[i * 3 + j] += m1[i * 3 + k] * m2[k * 3 + j];
                }
            }
        }
        return res;
    }
}
