package ac.affd_android.affdview.GL.control;

import ac.affd_android.affdview.GL.GLProgram.GLSLPreprocessor;

import java.util.Collections;
import java.util.List;

/**
 * Created by ac on 5/11/16.
 * todo some describe
 */
class ACController {
    private static final String VERSION = "#version 310 es\n";
    int local_size_x = 64;

    String preCompile(String source, List<GLSLPreprocessor> compilers) {
        if (source.startsWith("#version 310 es")) {
            source = source.substring(source.indexOf('\n') + 1);
        }

        if (compilers != null) {
            compilers.add(getLocalSizePreCompiler());
        } else {
            compilers = Collections.singletonList(getLocalSizePreCompiler());
        }

        for (GLSLPreprocessor c : compilers) {
            source = c.preCompile(source);
        }

        return VERSION + getLocalSizePreCompiler().preCompile(source);
    }

    private GLSLPreprocessor getLocalSizePreCompiler() {
        return new GLSLPreprocessor()
                .add("LOCAL_SIZE_X", Integer.toString(local_size_x));
    }
}
