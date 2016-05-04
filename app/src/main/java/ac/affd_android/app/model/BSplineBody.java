package ac.affd_android.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class BSplineBody {
    ACMatrix controllerPoint = new ACMatrix(null, 5, 5, 5, 3);
    IVec3 order = new IVec3(3, 3, 3);
    IVec3 controlPointNumber = new IVec3(5, 5, 5);
    Vec3 length;

    public BSplineBody(Vec3 length) {
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
                    controllerPoint.put(new ACMatrix(new Float[]{aux[0][i], aux[1][j], aux[2][k]}, 3),
                            new ACMatrix.Index(i),
                            new ACMatrix.Index(j),
                            new ACMatrix.Index(k),
                            new ACMatrix.Index(0, 3));
                }
            }
        }
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

    void dirctFFD(Vec3 start, Vec3 end) {
        //todo
    }

    ACMatrix getControllerPointForSpeedUp() {
        IVec3 intervalNumber = getIntervalNumber();
        ACMatrix result = new ACMatrix(
                null, intervalNumber.x, intervalNumber.y, intervalNumber.z,
                order.x, order.y, order.z, 4);
        for (int i = 0; i < intervalNumber.x; ++i) {
            for (int j = 0; j < intervalNumber.y; ++j) {
                for (int k = 0; k < intervalNumber.z; ++k) {
                    IVec3 leftIndex = new IVec3(i, j, k).add(order).subtract(1);
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
                            intermediateResult1.put(m[0].multiply(tempControlPoint.get(
                                    new ACMatrix.Index(0, order.x),
                                    new ACMatrix.Index(0, order.y),
                                    new ACMatrix.Index(0),
                                    new ACMatrix.Index(q))),

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
                                    new ACMatrix.Index(0),
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
                                    new ACMatrix.Index(0),
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
        return result;
    }

    IVec3 getIntervalNumber() {
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

    public ACMatrix getControllerPoint() {
        return controllerPoint;
    }


}
