package ac.affd_android.affdview.model;

import android.opengl.Matrix;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class Vec3f {
    public final float x;
    public final float y;
    public final float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vec3f(String[] tokens) {
        this.x = Float.parseFloat(tokens[0]);
        this.y = Float.parseFloat(tokens[1]);
        this.z = Float.parseFloat(tokens[2]);
    }

    public Vec3f(float xyz) {
        this.x = xyz;
        this.y = xyz;
        this.z = xyz;
    }

    public Vec3f subtract(Vec3f v) {
        return new Vec3f(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vec3f div(float v) {
        return new Vec3f(this.x / v, this.y / v, this.z / v);
    }


    public Vec3f div(Vec3i intervalNumber) {
        return new Vec3f(this.x / intervalNumber.x, this.y / intervalNumber.x, this.z / intervalNumber.x);
    }

    public Vec3f normalize() {
        float length = length();
        return this.div(length);
    }

    public Vec3f add(Vec3f v) {
        return new Vec3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vec3f mid(Vec3f v) {
        return this.add(v).div(2);
    }

    public Vec3f min(Vec3f v) {
        return new Vec3f(Math.min(this.x, v.x),
                Math.min(this.y, v.y),
                Math.min(this.z, v.z));
    }

    public Vec3f max(Vec3f v) {
        return new Vec3f(Math.max(this.x, v.x),
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

    public Vec3f add(int i) {
        return new Vec3f(this.x + i, this.y + i, this.z + i);
    }

    public Vec3f subtract(int i) {
        return new Vec3f(this.x - i, this.y - i, this.z - i);
    }

    public Float getComponent(int i) {
        return new Float[]{x, y, z}[i];
    }

    public Vec3f multiply(Float f) {
        return new Vec3f(x * f, y * f, z * f);
    }

    public Vec3f multiplyMV(float[] matrix, float w) {
        float[] res = new float[4];
        Matrix.multiplyMV(res, 0, matrix, 0, new float[]{x, y, z, w}, 0);
        return new Vec3f(res[0], res[1], res[2]);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}
