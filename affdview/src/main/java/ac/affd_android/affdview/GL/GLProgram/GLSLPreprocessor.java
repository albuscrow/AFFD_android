package ac.affd_android.affdview.GL.GLProgram;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ac on 5/11/16.
 * todo some describe
 */
public class GLSLPreprocessor {

    private final List<Pair<String, String>> macros = new ArrayList<>();

    public GLSLPreprocessor() {
    }

    public GLSLPreprocessor add(String name, String value) {
        macros.add(new Pair<>(name, value));
        return this;
    }

    public String preCompile(String src) {
        StringBuilder temp = new StringBuilder();
        for (Pair<String, String> m : macros) {
            temp.append(getDefinition(m));
        }
        return temp.append(src).toString();
    }

    private String getDefinition(Pair<String, String> m) {
        return "#define " + m.first + " " + m.second + "\n";
    }
}
