package ac.affd_android.app.GL.control;

import ac.affd_android.app.GL.GLProgram.ShaderPreCompiler;

import java.util.List;

/**
 * Created by ac on 5/11/16.
 * todo some describe
 */
class ACController {
    int group_size = 64;
    String preCompile(String source, List<ShaderPreCompiler> compilers) {
        for (ShaderPreCompiler c : compilers) {
            source = c.preCompile(source);
        }
        return source;
    }

    ShaderPreCompiler getLocalSizePreCompiler() {
        return new ShaderPreCompiler(new String[]{"local_size_x = 1"}, new String[]{"local_size_x = " + group_size});
    }
}
