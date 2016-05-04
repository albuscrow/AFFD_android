package ac.affd_android.app.model;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
public class Vec2 {
    public final Float x;
    public final Float y;

    public Vec2(String[] tokens) {
        this.x = Float.parseFloat(tokens[0]);
        this.y = Float.parseFloat(tokens[1]);
    }

    public Vec2(Float x, Float y) {
        this.x = x;
        this.y = y;
    }
}
