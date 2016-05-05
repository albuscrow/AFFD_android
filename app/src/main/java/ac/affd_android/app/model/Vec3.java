package ac.affd_android.app.model;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class Vec3 {
    public final float x;
    public final float y;
    public final float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(String[] tokens) {
        this.x = Float.parseFloat(tokens[0]);
        this.y = Float.parseFloat(tokens[1]);
        this.z = Float.parseFloat(tokens[2]);
    }

    public Vec3(float xyz) {
        this.x = xyz;
        this.y = xyz;
        this.z = xyz;
    }

    public Vec3 subtract(Vec3 v) {
        return new Vec3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vec3 div(float v) {
        return new Vec3(this.x / v, this.y / v, this.z / v);
    }

    public Vec3 normalize() {
        float temp = (float) Math.sqrt(x * x + y * y + z * z);
        return this.div(temp);
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vec3 mid(Vec3 v) {
        return this.add(v).div(2);
    }

    public Vec3 min(Vec3 v) {
        return new Vec3(Math.min(this.x, v.x),
                Math.min(this.y, v.y),
                Math.min(this.z, v.z));
    }

    public Vec3 max(Vec3 v) {
        return new Vec3(Math.max(this.x, v.x),
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

    public Vec3 add(int i) {
        return new Vec3(this.x + i, this.y + i, this.z + i);
    }

    public Vec3 subtract(int i) {
        return new Vec3(this.x - i, this.y - i, this.z - i);
    }

    public Float getComponent(int i) {
        if (i == 0) {
            return x;
        } else if (i == 1) {
            return y;
        } else if (i == 2) {
            return z;
        } else {
            throw new RuntimeException();
        }
    }

    public Vec3 multiply(Float f) {
        return new Vec3(x / f, y / f, z / f);
    }
}
