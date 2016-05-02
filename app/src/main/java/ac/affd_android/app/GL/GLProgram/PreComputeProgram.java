package ac.affd_android.app.GL.GLProgram;

import ac.affd_android.app.GL.GLOBJ.ACACBO;
import ac.affd_android.app.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.app.Util.ByteUtil;
import ac.affd_android.app.model.ACModelParse;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES31.*;

/**
 * Created by ac on 2/29/16.
 * todo some describe
 */
public class PreComputeProgram extends ACProgram {
    private static final String TAG = "PreComputeProgram";
    private static final int GROUP_SIZE = 64;
    private static final int MAX_SPLIT = 20;
    private float splitFactor = 0.5f;
    private final ACModelParse obj;
    private ACGLBuffer patternBuffer;
    private int splitPatternOffsetSize;
    private int splitPatternIndexSize;
    private int splitPatternParameterSize;
    private ACGLBuffer splittedTriangleAccouter;

    public PreComputeProgram(ACModelParse obj) {
        super();
        this.obj = obj;
    }

    public void glOnSurfaceCreated(Context c) {
        //read split pattern
        readSplitPattern(c);

        //init atomic buffer
        initAtomicBuffer();

        initShaderProgram(c);

        glAsyncBuffer();
        glUse();
        glDispatchCompute(obj.getTriangleNumber() / GROUP_SIZE + 1, 1, 1);
        Log.d(TAG, "atomic: " + splittedTriangleAccouter.toString());
    }

    private void initShaderProgram(Context c) {
        String source;
        try {
            source = IOUtils.toString(c.getAssets().open("pre_computer.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        addShader(new ACShader(preCompile(source, obj), GL_COMPUTE_SHADER));
        super.glCompileAndLink();
    }

    private void glAsyncBuffer() {
        patternBuffer.glAsyncWithGPU();
        splittedTriangleAccouter.glAsyncWithGPU();
    }

    private void initAtomicBuffer() {
        ByteBuffer bb = ByteUtil.genDirctBuffer(ByteUtil.INT_BYTE_SIZE);
        bb.putInt(0);
        bb.flip();
        splittedTriangleAccouter = ACGLBuffer.glGenBuffer(GL_ATOMIC_COUNTER_BUFFER)
                .glSetBindingPoint(0)
                .postUpdate(bb, bb.limit(), ACGLBuffer.INT);
    }

    private void readSplitPattern(Context c) {
        InputStream inputStream;
        try {
            inputStream = c.getAssets().open("20.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int maxSplit;
        String[] offsets;
        String[] indexes;
        String[] parameters;
        try {
            maxSplit = Integer.parseInt(reader.readLine());
            offsets = reader.readLine().trim().split(" ");
            indexes = reader.readLine().trim().split(" ");
            parameters = reader.readLine().trim().split(" ");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        if (maxSplit != MAX_SPLIT) {
            throw new RuntimeException("MAX_SPLIT is not right");
        }

        splitPatternOffsetSize = offsets.length;
        splitPatternIndexSize = indexes.length;
        splitPatternParameterSize = parameters.length;

        ByteBuffer splitPatternData = ByteUtil.genDirctBuffer((splitPatternOffsetSize + splitPatternIndexSize + splitPatternParameterSize) * 4);
        for (String s : indexes) {
            splitPatternData.putInt(Integer.parseInt(s));
        }
        for (String s : parameters) {
            splitPatternData.putFloat(Math.abs(Float.parseFloat(s)));
        }
        for (String s : offsets) {
            splitPatternData.putInt(Integer.parseInt(s));
        }
        splitPatternData.flip();

        patternBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER);

        patternBuffer.glSetBindingPoint(3);
        patternBuffer.postUpdate(splitPatternData, splitPatternData.limit(), ACGLBuffer.BYTE);
    }

    private String preCompile(String source, ACModelParse obj) {
        int pointNumber = obj.getPointNumber();
        int triangleNumber = obj.getTriangleNumber();

        return source.replace("Point[", "Point[" + pointNumber)
                .replace("Triangle[", "Triangle[" + triangleNumber)
                .replace("local_size_x = 1", "local_size_x = " + GROUP_SIZE)
                .replace("const float CONST_SPLIT_FACTOR = 0", "const float CONST_SPLIT_FACTOR = " + splitFactor + "f")
                .replace("const int CONST_MAX_SPLIT_FACTOR = 0", "const int CONST_MAX_SPLIT_FACTOR = " + MAX_SPLIT)
                .replace("const int LOOK_UP_TABLE_FOR_I[1] = {0}", "const int LOOK_UP_TABLE_FOR_I[" + MAX_SPLIT + "] = " + getLookupTableForI())
                .replace("const int MAX_SPLIT_FACTOR = 0", "const int MAX_SPLIT_FACTOR = " + MAX_SPLIT)
                .replace("int BUFFER_OFFSET_NUMBER[", "int BUFFER_OFFSET_NUMBER[" + splitPatternOffsetSize)
                .replace("ivec4 BUFFER_SPLIT_INDEX[", "ivec4 BUFFER_SPLIT_INDEX[" + splitPatternIndexSize / 4)
                .replace("vec4 BUFFER_SPLIT_PARAMETER[", "vec4 BUFFER_SPLIT_PARAMETER[" + splitPatternParameterSize / 4);
    }

    private String getLookupTableForI() {
        List<Integer> lookupTableForI = new ArrayList<>();
        lookupTableForI.add(0);
        for (int i = 1; i < MAX_SPLIT; ++i) {
            int temp = Math.min(MAX_SPLIT - i, i);
            lookupTableForI.add(
                    lookupTableForI.get(lookupTableForI.size() - 1)
                            + (i + temp) * temp / 2
                            + Math.max(0, (i + 1) * (MAX_SPLIT - 2 * i))
            );
        }
        String res = "int[" + MAX_SPLIT + "](";
        for (Integer i : lookupTableForI) {
            res += (i + ",");
        }
        return res.substring(0, res.length() - 1) + ")";
    }

    public int getSplittedTriangleNumber() {
        return ((ACACBO) splittedTriangleAccouter).getValue();
    }
}
