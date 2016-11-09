package ac.affd_android.affdview.GL.GLOBJ;

import static android.opengl.GLES30.GL_UNIFORM_BUFFER;

/**
 * Created by ac on 5/10/16.
 * todo some describe
 */
public class ACUBO extends ACGLBuffer {
    public ACUBO(int bufferId) {
        super(bufferId);
        this.bufferType = GL_UNIFORM_BUFFER;
    }
}
