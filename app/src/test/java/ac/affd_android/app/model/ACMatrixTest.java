package ac.affd_android.app.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */

@RunWith(MockitoJUnitRunner.class)
public class ACMatrixTest {
    private ACMatrix matrix;

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
        matrix = new ACMatrix(data, d);
    }

    @Test
    public void testGetSignal() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    Integer[] d = {i, j, k};
                    final ACMatrix actual = matrix.get(d);
                    Assert.assertEquals(0, actual.shape.length);
                    assert actual.data != null;
                    Assert.assertThat(actual.data[0], is((float) (i * 100 + j * 10 + k)));
                }
            }
        }
    }

    @Test
    public void testGetMatrix() {
        Integer[] d = {-1, 0, 0};
        ACMatrix actual = matrix.get(d);
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

        ACMatrix.Index[] di = new ACMatrix.Index[]{new ACMatrix.Index(0, 2), new ACMatrix.Index(2, 4), new ACMatrix.Index(4)};
        actual = matrix.get(di);
        Assert.assertArrayEquals(new Integer[]{2, 2}, actual.shape);
        Assert.assertArrayEquals(new Float[]{24f, 34f,
                124f, 134f}, actual.data);

        di = new ACMatrix.Index[]{new ACMatrix.Index(0, 3), new ACMatrix.Index(2, 3), new ACMatrix.Index(4)};
        actual = matrix.get(di);
        System.out.println(Arrays.toString(actual.shape));
        Assert.assertArrayEquals(new Integer[]{3}, actual.shape);
        Assert.assertArrayEquals(new Float[]{24f,
                124f,
                224f}, actual.data);

        d = new Integer[]{-1, -1, -1};
        actual = matrix.get(d);
        Assert.assertArrayEquals(new Integer[]{3, 4, 5}, actual.shape);
        Assert.assertArrayEquals(matrix.data, actual.data);
    }

    @Test
    public void testPutMatrix() {
        ACMatrix m34 = new ACMatrix(new Float[]{9f, 10f, 11f, 12f, 9f, 10f, 11f, 12f, 9f, 10f, 11f, 12f}, 3, 4);
        ACMatrix m = new ACMatrix(matrix);
        Integer[] index = {-1, -1, 0};
        m.put(m34, index);
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
        ACMatrix zero = new ACMatrix(0);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    matrix.put(zero, i, j, k);
                }
            }
        }
        Assert.assertArrayEquals(matrix.data, new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    final Integer[] index = {i, j, k};
                    final float f = (float) (i * 100 + j * 10 + k);
                    matrix.put(f, index);
                    final ACMatrix ACMatrix = matrix.get(index);
                    Assert.assertArrayEquals(ACMatrix.data, new Float[]{f});
                    Assert.assertArrayEquals(ACMatrix.shape, new Integer[0]);
                }
            }
        }
    }

    @Test
    public void testMultiply() {
        ACMatrix m1 = new ACMatrix(new Float[]{1f, 2f, 3f, 4f, 5f, 6f}, 2, 3);
        ACMatrix m2 = new ACMatrix(new Float[]{7f, 6f, 5f, 4f, 3f, 2f}, 3, 2);
        ACMatrix m3 = m1.multiply(m2);
        Assert.assertArrayEquals(m3.shape, new Integer[]{2, 2});
        Assert.assertArrayEquals(m3.data, new Float[]{26f, 20f, 71f, 56f});
    }

    @Test
    public void testT() {
        ACMatrix m1 = new ACMatrix(new Float[]{1f, 2f, 3f, 4f, 5f, 6f}, 2, 3);
        Assert.assertArrayEquals(m1.T().data, new Float[]{1f, 4f, 2f, 5f, 3f, 6f});
    }
}