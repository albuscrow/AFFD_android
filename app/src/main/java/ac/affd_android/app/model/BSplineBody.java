package ac.affd_android.app.model;

import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class BSplineBody {
    List<Vec3> controllerPoint = new ArrayList<>(125);
    Vec3 order = new Vec3(3, 3, 3);
    Vec3 controlPointNumber = new Vec3(5, 5, 5);
    Vec3 length;

    public BSplineBody(Vec3 length) {
        this.length = length;
    }

    void dirctFFD(Vec3 start, Vec3 end) {
        //todo
    }

    ByteBuffer getControllerPointForSpeedUp() {
        Vec3 intervalNumber = getIntervalNumber();
        for (int i = 0; i < intervalNumber.x; ++i) {
            for (int j = 0; j < intervalNumber.y; ++j) {
                for (int k = 0; k < intervalNumber.z; ++k) {

                }
            }
        }
        return null;
    }

    Vec3 getIntervalNumber() {
        return controlPointNumber.subtract(order).add(1);
    }

    Float B(List<Float> t, int i, int k, Float x) {
        if (k == 1) {
            if ((t.get(i) <= x && x < t.get(i + 1))
                    || x.equals(t.get(t.size() - 1))) {
                return 1f;
            } else {
                return 0f;
            }
        } else {
            Float temp1 = t.get(i + k - 1) - t.get(i);
            if (!temp1.equals(0f)) {
                temp1 = (x - t.get(i)) / temp1;
            }
            Float temp2 = t.get(i + k) - t.get(i + 1);
            if (!temp2.equals(0f)) {
                temp2 = (t.get(i + k) - x) / temp2;
            }
            return temp1 * B(t, i, k - 1, x) + temp2 * B(t, i + 1, k - 1, x);
        }
    }

    static class HighDimensionalMatrix {
        final public Integer[] shape;
        final public Float[] data;

        public HighDimensionalMatrix(Integer[] shape, @Nullable Float[] data) {
            this.shape = shape;
            if (data != null) {
                this.data = data;
            } else {
                this.data = new Float[size()];
                Arrays.fill(this.data, 0f);
            }
        }

        public HighDimensionalMatrix(HighDimensionalMatrix matrix) {
            this.shape = matrix.shape.clone();
            this.data = matrix.data.clone();
        }

        public HighDimensionalMatrix(float data) {
            this.shape = new Integer[0];
            this.data = new Float[]{data};
        }

        public HighDimensionalMatrix(List<HighDimensionalMatrix> temp) {
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

        public Integer size() {
            Integer res = 1;
            for (Integer i : shape) {
                res *= i;
            }
            return res;
        }

        public void put(Integer[] index, HighDimensionalMatrix matrix) {
            putHelper(index, 0, 0, matrix, 0, 0);
        }

        public void put(Integer[] index, Float f) {
            putHelper(index, 0, 0, new HighDimensionalMatrix(f), 0, 0);
        }

        private void putHelper(Integer[] index, int i, int offset, HighDimensionalMatrix matrix, int i2, int offset2) {
            if (i == shape.length) {
                if (i2 != matrix.shape.length) {
                    throw new RuntimeException("dimensional error");
                }
                data[offset] = matrix.data[offset2];
            } else {
                if (index[i] != -1) {
                    putHelper(index, i + 1, offset * shape[i] + index[i], matrix, i2, offset2);
                } else {
                    if (!Objects.equals(shape[i], matrix.shape[i2])) {
                        throw new RuntimeException();
                    }
                    for (int ii = 0; ii < shape[i]; ++ii) {
                        putHelper(index, i + 1, offset * shape[i] + ii, matrix, i2 + 1, offset2 * matrix.shape[i2] + ii);
                    }
                }
            }
        }

        public HighDimensionalMatrix get(Integer[] index) {
            return getHelper(index, 0, 0);
        }

        private HighDimensionalMatrix getHelper(Integer[] index, Integer i, Integer offset) {
            if (i == shape.length) {
                return new HighDimensionalMatrix(new Integer[0], Arrays.copyOfRange(data, offset, offset + 1));
            } else {
                if (index[i] != -1) {
                    return getHelper(index, i + 1, offset * shape[i] + index[i]);
                } else {
                    List<HighDimensionalMatrix> temp = new ArrayList<>(shape[i]);
                    for (int ii = 0; ii < shape[i]; ++ii) {
                        temp.add(getHelper(index, i + 1, offset * shape[i] + ii));
                    }
                    return new HighDimensionalMatrix(temp);
                }
            }
        }
    }

}
