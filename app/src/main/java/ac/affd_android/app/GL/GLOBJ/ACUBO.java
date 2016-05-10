package ac.affd_android.app.GL.GLOBJ;

import static android.opengl.GLES31.GL_ATOMIC_COUNTER_BUFFER;

/**
 * Created by ac on 5/10/16.
 * todo some describe
 */
public class ACUBO extends ACGLBuffer {
    public ACUBO(int bufferId) {
        super(bufferId);
        this.bufferType = GL_ATOMIC_COUNTER_BUFFER;
    }
}
