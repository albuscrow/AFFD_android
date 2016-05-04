package ac.affd_android.app.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */

@RunWith(MockitoJUnitRunner.class)
public class HighDimensionalMatrixTest {
    private BSplineBody.HighDimensionalMatrix matrix;

    @Before
    public void init() {
        Integer[] d = {3, 4, 5};
        Float[] data = new Float[60];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    data[i * 20 + j * 5 + k] = (float) (i * 100 + j * 10 + k);
                }
            }
        }
        matrix = new BSplineBody.HighDimensionalMatrix(d, data);
    }

    @Test
    public void testGetSignal() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    Integer[] d = {i, j, k};
                    final BSplineBody.HighDimensionalMatrix actual = matrix.get(d);
                    Assert.assertEquals(0, actual.shape.length);
                    Assert.assertThat(actual.data[0], is((float) (i * 100 + j * 10 + k)));
                }
            }
        }
    }

    @Test
    public void testGetMatrix() {
        Integer[] d = {-1, 0, 0};
        BSplineBody.HighDimensionalMatrix actual = matrix.get(d);
        Assert.assertArrayEquals(new Float[]{0f, 100f, 200f}, actual.data);

        d = new Integer[]{-1, 0, 1};
        actual = matrix.get(d);
        Assert.assertArrayEquals(new Integer[]{3}, actual.shape);
        Assert.assertArrayEquals(new Float[]{1f, 101f, 201f}, actual.data);

        d = new Integer[]{-1, 3, 1};
        actual = matrix.get(d);
        Assert.assertArrayEquals(new Integer[]{3}, actual.shape);
        Assert.assertArrayEquals(new Float[]{31f, 131f, 231f}, actual.data);

        d = new Integer[]{1, -1, 1};
        actual = matrix.get(d);
        Assert.assertArrayEquals(new Integer[]{4}, actual.shape);
        Assert.assertArrayEquals(new Float[]{101f, 111f, 121f, 131f}, actual.data);

        d = new Integer[]{-1, -1, 4};
        actual = matrix.get(d);
        Assert.assertArrayEquals(new Integer[]{3, 4}, actual.shape);
        Assert.assertArrayEquals(new Float[]{4f, 14f, 24f, 34f,
                104f, 114f, 124f, 134f,
                204f, 214f, 224f, 234f}, actual.data);

        d = new Integer[]{-1, -1, -1};
        actual = matrix.get(d);
        Assert.assertArrayEquals(new Integer[]{3, 4, 5}, actual.shape);
        Assert.assertArrayEquals(matrix.data, actual.data);
    }

    @Test
    public void testPutMatrix() {
        BSplineBody.HighDimensionalMatrix m34 = new BSplineBody.HighDimensionalMatrix(new Integer[]{3, 4}, new Float[]{9f, 10f, 11f, 12f, 9f, 10f, 11f, 12f, 9f, 10f, 11f, 12f});
        BSplineBody.HighDimensionalMatrix m = new BSplineBody.HighDimensionalMatrix(matrix);
        Integer[] index = {-1, -1, 0};
        m.put(index, m34);
        Assert.assertArrayEquals(m.get(index).data, m34.data);
        index = new Integer[]{-1, -1, 1};
        Assert.assertArrayEquals(m.get(index).data, matrix.get(index).data);
        index = new Integer[]{-1, -1, 2};
        Assert.assertArrayEquals(m.get(index).data, matrix.get(index).data);
        index = new Integer[]{-1, -1, 3};
        Assert.assertArrayEquals(m.get(index).data, matrix.get(index).data);
        index = new Integer[]{-1, -1, 4};
        Assert.assertArrayEquals(m.get(index).data, matrix.get(index).data);
    }

    @Test
    public void testPutSignal() {
        BSplineBody.HighDimensionalMatrix zero = new BSplineBody.HighDimensionalMatrix(0);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    matrix.put(new Integer[]{i, j, k}, zero);
                }
            }
        }
        Assert.assertArrayEquals(matrix.data, new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    final Integer[] index = {i, j, k};
                    final float f = (float) (i * 100 + j * 10 + k);
                    matrix.put(index, f);
                    final BSplineBody.HighDimensionalMatrix highDimensionalMatrix = matrix.get(index);
                    Assert.assertArrayEquals(highDimensionalMatrix.data, new Float[]{f});
                    Assert.assertArrayEquals(highDimensionalMatrix.shape, new Integer[0]);
                }
            }
        }
    }
}