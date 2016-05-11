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
        int[] d = {3, 4, 5};
        float[] data = new float[60];
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
        Assert.assertEquals(new float[]{0f, 100f, 200f}, actual.data);

        d = new Integer[]{-1, 0, 1};
        actual = matrix.get(d);
        Assert.assertEquals(new int[]{3}, actual.shape);
        Assert.assertEquals(new float[]{1f, 101f, 201f}, actual.data);

        d = new Integer[]{-1, 3, 1};
        actual = matrix.get(d);
        Assert.assertEquals(new int[]{3}, actual.shape);
        Assert.assertEquals(new float[]{31f, 131f, 231f}, actual.data);

        d = new Integer[]{1, -1, 1};
        actual = matrix.get(d);
        Assert.assertEquals(new int[]{4}, actual.shape);
        Assert.assertEquals(new float[]{101f, 111f, 121f, 131f}, actual.data);

        d = new Integer[]{-1, -1, 4};
        actual = matrix.get(d);
        Assert.assertEquals(new int[]{3, 4}, actual.shape);
        Assert.assertEquals(new float[]{4f, 14f, 24f, 34f,
                104f, 114f, 124f, 134f,
                204f, 214f, 224f, 234f}, actual.data);

        ACMatrix.Index[] di = new ACMatrix.Index[]{new ACMatrix.Index(0, 2), new ACMatrix.Index(2, 4), new ACMatrix.Index(4)};
        actual = matrix.get(di);
        Assert.assertEquals(new int[]{2, 2}, actual.shape);
        Assert.assertEquals(new float[]{24f, 34f,
                124f, 134f}, actual.data);

        di = new ACMatrix.Index[]{new ACMatrix.Index(0, 3), new ACMatrix.Index(2, 3), new ACMatrix.Index(4)};
        actual = matrix.get(di);
        System.out.println(Arrays.toString(actual.shape));
        Assert.assertEquals(new int[]{3}, actual.shape);
        Assert.assertEquals(new float[]{24f,
                124f,
                224f}, actual.data);

        d = new Integer[]{-1, -1, -1};
        actual = matrix.get(d);
        Assert.assertEquals(new int[]{3, 4, 5}, actual.shape);
        Assert.assertEquals(matrix.data, actual.data);
    }

    @Test
    public void testPutMatrix() {
        ACMatrix m34 = new ACMatrix(new float[]{9f, 10f, 11f, 12f, 9f, 10f, 11f, 12f, 9f, 10f, 11f, 12f}, 3, 4);
        ACMatrix m = new ACMatrix(matrix);
        Integer[] index = {-1, -1, 0};
        m.put(m34, index);
        Assert.assertEquals(m.get(index).data, m34.data);
        index = new Integer[]{-1, -1, 1};
        Assert.assertEquals(m.get(index).data, matrix.get(index).data);
        index = new Integer[]{-1, -1, 2};
        Assert.assertEquals(m.get(index).data, matrix.get(index).data);
        index = new Integer[]{-1, -1, 3};
        Assert.assertEquals(m.get(index).data, matrix.get(index).data);
        index = new Integer[]{-1, -1, 4};
        Assert.assertEquals(m.get(index).data, matrix.get(index).data);
    }

    @Test
    public void testPutSignal() {
        ACMatrix zero = new ACMatrix(0f);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    matrix.put(zero, i, j, k);
                }
            }
        }
        Assert.assertEquals(matrix.data, new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 5; ++k) {
                    final Integer[] index = {i, j, k};
                    final float f = (float) (i * 100 + j * 10 + k);
                    matrix.put(f, index);
                    final ACMatrix ACMatrix = matrix.get(index);
                    Assert.assertEquals(ACMatrix.data, new float[]{f});
                    Assert.assertEquals(ACMatrix.shape, new int[0]);
                }
            }
        }
    }

    @Test
    public void testMultiply() {
        ACMatrix m1 = new ACMatrix(new float[]{1f, 2f, 3f, 4f, 5f, 6f}, 2, 3);
        ACMatrix m2 = new ACMatrix(new float[]{7f, 6f, 5f, 4f, 3f, 2f}, 3, 2);
        ACMatrix m3 = m1.multiply(m2);
        Assert.assertEquals(m3.shape, new int[]{2, 2});
        Assert.assertEquals(m3.data, new float[]{26f, 20f, 71f, 56f});
    }

    @Test
    public void testT() {
        ACMatrix m1 = new ACMatrix(new float[]{1f, 2f, 3f, 4f, 5f, 6f}, 2, 3);
        Assert.assertEquals(m1.T().data, new float[]{1f, 4f, 2f, 5f, 3f, 6f});
    }

}