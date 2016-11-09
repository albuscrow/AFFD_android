package ac.affd_android.affdview.model;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class Vec3i {
    public final int x;
    public final int y;
    public final int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(String[] tokens) {
        this.x = Integer.parseInt(tokens[0]);
        this.y = Integer.parseInt(tokens[1]);
        this.z = Integer.parseInt(tokens[2]);
    }

    public Vec3i(int xyz) {
        this.x = xyz;
        this.y = xyz;
        this.z = xyz;
    }

    public Vec3i add(Vec3i v) {
        return new Vec3i(x + v.x, y + v.y, z + v.z);
    }

    public Vec3i add(int i) {
        return new Vec3i(x + i, y + i, z + i);
    }

    public Vec3i subtract(Vec3i v) {
        return new Vec3i(x - v.x, y - v.y, z - v.z);
    }

    public Vec3i subtract(int i) {
        return new Vec3i(x - i, y - i, z - i);
    }


    @Override
    public String toString() {
        return "x:" + x + " y:" + y + " z:" + z;
    }

    public int getComponent(int i) {
        return new Integer[]{x, y, z}[i];
    }

    public Integer innerProduct() {
        return x * y * z;
    }
}
