package ac.affd_android.app.model;

import ac.affd_android.app.Util.ByteUtil;

import java.nio.Buffer;
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
    private static final int INFO_SIZE = 96;
    private ACMatrix controllerPoint = new ACMatrix(null, 5, 5, 5, 3);
    private ACMatrix originalControlPoint;
    private Vec3i order = new Vec3i(3, 3, 3);
    private Vec3i controlPointNumber = new Vec3i(5, 5, 5);
    private Vec3f length;

    public BSplineBody(Vec3f length) {
        this.length = length;
        initData();
    }

    private void initData() {
        Float[][] aux = new Float[3][];
        for (int i = 0; i < 3; i++) {
            aux[i] = getControlPointAuxList(length.getComponent(i),
                    controlPointNumber.getComponent(i),
                    order.getComponent(i));
        }
        for (int i = 0; i < controlPointNumber.x; i++) {
            for (int j = 0; j < controlPointNumber.y; j++) {
                for (int k = 0; k < controlPointNumber.z; k++) {
                    controllerPoint.put(new ACMatrix(new float[]{aux[0][i], aux[1][j], aux[2][k]}, 3),
                            new ACMatrix.Index(i),
                            new ACMatrix.Index(j),
                            new ACMatrix.Index(k),
                            new ACMatrix.Index(0, 3));
                }
            }
        }
        originalControlPoint = new ACMatrix(controllerPoint);
    }

    private Float[] getControlPointAuxList(Float cuarrentLength, Integer currentControlPointNumber, Integer currentOrder) {
        if (Objects.equals(currentControlPointNumber, currentOrder)) {
            Float step = cuarrentLength / (currentControlPointNumber - 1);
            Float[] res = new Float[currentControlPointNumber];
            for (int i = 0; i < currentControlPointNumber; ++i) {
                res[0] = -cuarrentLength / 2 + step * i;
            }
            return res;
        } else if (currentControlPointNumber > currentOrder) {
            List<Float> aux = new ArrayList<>();
            aux.add(0f);
            for (int i = 1; i < currentControlPointNumber / 2 + 1; ++i) {
                Integer step = Math.min(i, currentOrder - 1);
                aux.add(aux.get(aux.size() - 1) + step);
            }
            for (int i = currentControlPointNumber / 2 - (currentControlPointNumber + 1) % 2; i > 0; --i) {
                Integer step = Math.min(i, currentOrder - 1);
                aux.add(aux.get(aux.size() - 1) + step);
            }
            Integer size = aux.size();
            Float last = aux.get(size - 1);
            Float[] res = new Float[size];
            for (int i = 0; i < size; ++i) {
                res[i] = (aux.get(i) / last - 0.5f) * cuarrentLength;
            }
            return res;
        } else {
            throw new RuntimeException("control point number can not less than order");
        }
    }

    void dirctFFD(Vec3f parameter, Vec3f displament) {
        parameter = parameter.max(length.div(-2)).min(length.div(2));
        ACMatrix Rs = new ACMatrix(null, controlPointNumber.x, controlPointNumber.y, controlPointNumber.z);
        Float aux = 0f;
        for (int i = 0; i < controlPointNumber.x; i++) {
            for (int j = 0; j < controlPointNumber.y; j++) {
                for (int k = 0; k < controlPointNumber.z; k++) {
                    final Float temp = R(parameter, new Vec3i(i, j, k));
                    Rs.put(temp, i, j, k);
                    aux += temp * temp;
                }
            }
        }
        if (aux == 0) {
            return;
        }
        controllerPoint = new ACMatrix(originalControlPoint);
        for (int i = 0; i < controlPointNumber.x; i++) {
            for (int j = 0; j < controlPointNumber.y; j++) {
                for (int k = 0; k < controlPointNumber.z; k++) {
                    Vec3f k_aux = displament.multiply(Rs.get(i, j, k).data[0]).div(aux);
                    controllerPoint.put(controllerPoint.get(i, j, k).add(k_aux), i, j, k);
                }
            }
        }

    }

    public Buffer getControllerPointForSpeedUp() {
        Vec3i intervalNumber = getIntervalNumber();
        ACMatrix result = new ACMatrix(
                null, intervalNumber.x, intervalNumber.y, intervalNumber.z,
                order.x, order.y, order.z, 4);
        for (int i = 0; i < intervalNumber.x; ++i) {
            for (int j = 0; j < intervalNumber.y; ++j) {
                for (int k = 0; k < intervalNumber.z; ++k) {
                    Vec3i leftIndex = new Vec3i(i, j, k).add(order).subtract(1);
                    ACMatrix[] m = new ACMatrix[3];

                    for (int q = 0; q < 3; ++q) {
                        m[q] = SampleAuxMatrix.get_aux_matrix_offset(order.getComponent(q),
                                controlPointNumber.getComponent(q),
                                leftIndex.getComponent(q));
                    }

                    ACMatrix intermediateResult1 = new ACMatrix(null, order.x, order.y, order.z, 3);
                    for (int w = 0; w < order.z; ++w) {
                        ACMatrix tempControlPoint = controllerPoint.get(
                                new ACMatrix.Index(i, i + order.x),
                                new ACMatrix.Index(j, j + order.y),
                                new ACMatrix.Index(k + w),
                                new ACMatrix.Index(0, 3));

                        for (int q = 0; q < 3; ++q) {
                            final ACMatrix m1 = tempControlPoint.get(
                                    new ACMatrix.Index(0, order.x),
                                    new ACMatrix.Index(0, order.y),
                                    new ACMatrix.Index(q));
                            final ACMatrix multiply = m[0].multiply(m1);

                            intermediateResult1.put(multiply,
                                    new ACMatrix.Index(0, order.x),
                                    new ACMatrix.Index(0, order.y),
                                    new ACMatrix.Index(w),
                                    new ACMatrix.Index(q)
                            );
                        }
                    }

                    ACMatrix intermediateResult2 = new ACMatrix(null, order.x, order.y, order.z, 3);
                    for (int u = 0; u < order.x; ++u) {
                        ACMatrix tempControlPoint = intermediateResult1.get(
                                new ACMatrix.Index(u),
                                new ACMatrix.Index(0, order.y),
                                new ACMatrix.Index(0, order.z),
                                new ACMatrix.Index(0, 3));
                        for (int q = 0; q < 3; ++q) {
                            intermediateResult2.put(m[1].multiply(tempControlPoint.get(
                                    new ACMatrix.Index(0, order.y),
                                    new ACMatrix.Index(0, order.z),
                                    new ACMatrix.Index(q)
                                    )),
                                    new ACMatrix.Index(u),
                                    new ACMatrix.Index(0, order.y),
                                    new ACMatrix.Index(0, order.z),
                                    new ACMatrix.Index(q)
                            );
                        }
                    }
                    for (int v = 0; v < order.y; ++v) {
                        ACMatrix tempControlPoint = intermediateResult2.get(
                                new ACMatrix.Index(0, order.x),
                                new ACMatrix.Index(v),
                                new ACMatrix.Index(0, order.z),
                                new ACMatrix.Index(0, 3));
                        for (int q = 0; q < 3; ++q) {
                            result.put(tempControlPoint.get(
                                    new ACMatrix.Index(0, order.x),
                                    new ACMatrix.Index(0, order.z),
                                    new ACMatrix.Index(q)
                                    ).multiply(m[2].T()),
                                    new ACMatrix.Index(i),
                                    new ACMatrix.Index(j),
                                    new ACMatrix.Index(k),
                                    new ACMatrix.Index(0, order.x),
                                    new ACMatrix.Index(v),
                                    new ACMatrix.Index(0, order.z),
                                    new ACMatrix.Index(q)
                            );
                        }
                    }
                }
            }
        }
        return ByteUtil.ACMatrix2FloatBuffer(result);
    }

    Vec3i getIntervalNumber() {
        return controlPointNumber.subtract(order).add(1);
    }

    Float R(Vec3f parameter, Vec3i ijk) {
        Float res = 1f;
        Float[][] knots = getKnots();
        for (int i = 0; i < 3; i++) {
            res *= B(knots[i], ijk.getComponent(i), order.getComponent(i), parameter.getComponent(i));
        }
        return res;
    }

    static Float B(Float[] t, int i, int k, Float x) {
        if (k == 1) {
            if ((t[i] <= x && x < t[i + 1])
                    || x.equals(t[t.length - 1])) {
                return 1f;
            } else {
                return 0f;
            }
        } else {
            Float temp1 = t[i + k - 1] - t[i];
            if (!temp1.equals(0f)) {
                temp1 = (x - t[i]) / temp1;
            }
            Float temp2 = t[i + k] - t[i + 1];
            if (!temp2.equals(0f)) {
                temp2 = (t[i + k] - x) / temp2;
            }
            return temp1 * B(t, i, k - 1, x) + temp2 * B(t, i + 1, k - 1, x);
        }
    }

    private static Float[] getKnotsHelper(Float currentLength, Integer currentOrder, Integer currentInternalNumber) {
        final int knotsLength = 2 * currentOrder + currentInternalNumber - 1;
        Float[] res = new Float[knotsLength];
        Arrays.fill(res, 0, currentOrder, -currentLength / 2f);
        Arrays.fill(res, currentOrder + currentInternalNumber - 1, knotsLength, currentLength / 2f);
        Float step = currentLength / currentInternalNumber;
        for (int i = 1; i < currentInternalNumber; i++) {
            res[currentOrder + i - 1] = res[0] + i * step;
        }
        return res;
    }

    ACMatrix getControllerPoint() {
        return controllerPoint;
    }

    public Float[][] getKnots() {
        Float[][] knots = new Float[3][];
        for (int i = 0; i < 3; i++) {
            knots[i] = getKnotsHelper(length.getComponent(i), order.getComponent(i), getIntervalNumber().getComponent(i));
        }
        return knots;
    }
    
    public ByteBuffer getInfo() {
        ByteBuffer res = ByteUtil.genDirectBuffer(INFO_SIZE);
        ByteUtil.addToBuffer(res, order);
        ByteUtil.addToBuffer(res, order.innerProduct());
        ByteUtil.addToBuffer(res, controlPointNumber, 1);
        ByteUtil.addToBuffer(res, getIntervalNumber(), 1);
        ByteUtil.addToBuffer(res, length, 1);
        ByteUtil.addToBuffer(res, length.div(-2), 1);
        ByteUtil.addToBuffer(res, length.div(getIntervalNumber()), 1);
        res.flip();
        return res;
    }
}
