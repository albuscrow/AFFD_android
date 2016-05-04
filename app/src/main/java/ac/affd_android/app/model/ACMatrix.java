package ac.affd_android.app.model;

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
class ACMatrix {
    static class Index {
        Integer start;
        Integer end;

        public Index(Integer i) {
            start = i;
            end = i + 1;
        }

        public Index(Integer start, Integer end) {
            if (start >= end) {
                throw new RuntimeException("start must less than end");
            }
            this.start = start;
            this.end = end;
        }

        boolean isInterval() {
            return this.start + 1 != this.end;
        }

        Integer intervalSize() {
            return this.end - this.start;
        }

    }

    final public Integer[] shape;
    final public Float[] data;

    public ACMatrix(@Nullable Float[] data, Integer... shape) {
        this.shape = shape;
        if (data != null) {
            this.data = data;
        } else {
            this.data = new Float[size()];
            Arrays.fill(this.data, 0f);
        }
    }

    public ACMatrix(ACMatrix matrix) {
        this.shape = matrix.shape.clone();
        this.data = matrix.data.clone();
    }

    public ACMatrix(float data) {
        this.shape = new Integer[0];
        this.data = new Float[]{data};
    }

    public ACMatrix(List<ACMatrix> temp) {
        if (temp.size() == 1) {
            this.shape = temp.get(0).shape;
            this.data = temp.get(0).data;
        } else {
            Integer[] d = temp.get(0).shape;
            for (int i = 1; i < temp.size(); ++i) {
                if (!Arrays.equals(d, temp.get(i).shape)) {
                    throw new RuntimeException("dimensional must same");
                }
            }
            shape = new Integer[d.length + 1];
            shape[0] = temp.size();
            System.arraycopy(d, 0, shape, 1, d.length);

            final Integer elementSize = temp.get(0).size();
            data = new Float[temp.size() * elementSize];
            for (int i = 0; i < temp.size(); ++i) {
                final Float[] tempData = temp.get(i).data;
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

    public ACMatrix get(Index... indices) {
        return getHelper(indices, 0, 0);
    }

    public ACMatrix get(Integer... indices) {
        return getHelper(indicesInt2Index(indices), 0, 0);
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

    private ACMatrix getHelper(Index[] index, Integer i, Integer offset) {
        if (i == shape.length) {
            return new ACMatrix(Arrays.copyOfRange(data, offset, offset + 1));
        } else {
            List<ACMatrix> temp = new ArrayList<>(shape[i]);
            for (int ii = index[i].start; ii < index[i].end; ++ii) {
                temp.add(getHelper(index, i + 1, offset * shape[i] + ii));
            }
            return new ACMatrix(temp);
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
                Float e = 0f;
                for (int k = 0; k < shape[1]; ++k) {
                    e += get(i, k).data[0] * m.get(k, j).data[0];
                }
                res.put(e, i, j);
            }
        }
        return res;
    }

    public ACMatrix T() {
        if (shape.length != 2) {
            throw new RuntimeException("matrix dimension must 2");
        }
        Float[] newData = new Float[data.length];
        for (int i = 0; i < shape[0]; ++i) {
            for (int j = 0; j < shape[1]; ++j) {
                newData[j * shape[0] + i] = data[i * shape[1] + j];
            }
        }
        return new ACMatrix(newData, shape[1], shape[0]);
    }
}
