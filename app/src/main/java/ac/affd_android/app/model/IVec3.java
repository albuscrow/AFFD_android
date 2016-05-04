package ac.affd_android.app.model;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class IVec3 {
    public final int x;
    public final int y;
    public final int z;

    public IVec3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IVec3(String[] tokens) {
        this.x = Integer.parseInt(tokens[0]);
        this.y = Integer.parseInt(tokens[1]);
        this.z = Integer.parseInt(tokens[2]);
    }

    public IVec3(int xyz) {
        this.x = xyz;
        this.y = xyz;
        this.z = xyz;
    }

    public IVec3 add(IVec3 v) {
        return new IVec3(x + v.x, y + v.y, z + v.z);
    }

    public IVec3 add(int i) {
        return new IVec3(x + i, y + i, z + i);
    }

    public IVec3 subtract(IVec3 v) {
        return new IVec3(x - v.x, y - v.y, z - v.z);
    }

    public IVec3 subtract(int i) {
        return new IVec3(x - i, y - i, z - i);
    }


    @Override
    public String toString() {
        return "x:" + x + " y:" + y + " z:" + z;
    }

    public int getComponent(int i) {
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

}
