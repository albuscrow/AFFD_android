package ac.affd_android.affdview.model;

import ac.affd_android.affdview.Constant;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class Vec2 {
    public final float x;
    public final float y;

    public Vec2(String[] tokens) {
        this.x = Float.parseFloat(tokens[0]);
        this.y = Float.parseFloat(tokens[1]);
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isZero() {
        return Math.abs(x) < Constant.ZERO && Math.abs(y) < Constant.ZERO;
    }
}
