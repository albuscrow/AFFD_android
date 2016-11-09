package ac.affd_android.affdview.GL.GLProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ac on 5/11/16.
 * todo some describe
 */
public class ShaderPreCompiler {
    static final String TAG = "PreCompiler";
    final List<String> before = new ArrayList<>();
    final List<String> after = new ArrayList<>();

//    public ShaderPreCompiler() {
//    }

    public ShaderPreCompiler(String[] before, String[] after) {
        if ((before == null && after == null) || before.length != after.length) {
            throw new RuntimeException(TAG);
        }
        this.before.addAll(Arrays.asList(before));
        this.after.addAll(Arrays.asList(after));
    }

    public ShaderPreCompiler() {
    }

    public ShaderPreCompiler add(String b, String a) {
        this.before.add(b);
        this.after.add(a);
        return this;
    }

    public String preCompile(String src) {
        for (int i = 0; i < before.size(); ++i) {
            System.out.println(after.get(i));
            src = src.replace(before.get(i), after.get(i));
        }
        return src;
    }
}
