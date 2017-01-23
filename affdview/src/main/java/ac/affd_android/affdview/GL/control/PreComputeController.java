package ac.affd_android.affdview.GL.control;

import ac.affd_android.affdview.Constant;
import ac.affd_android.affdview.GL.GLOBJ.ACACBO;
import ac.affd_android.affdview.GL.GLOBJ.ACGLBuffer;
import ac.affd_android.affdview.GL.GLProgram.ACProgram;
import ac.affd_android.affdview.GL.GLProgram.ACShader;
import ac.affd_android.affdview.GL.GLProgram.GLSLPreprocessor;
import ac.affd_android.affdview.Util.ByteUtil;
import ac.affd_android.affdview.Util.FileUtil;
import ac.affd_android.affdview.model.GlobalInfoProvider;
import android.content.Context;

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
public class PreComputeController extends ACController {
    private static final String TAG = "PreComputeProgram";
    private static final int MAX_SPLIT = 20;
    private float splitFactor = 10f;
    //    private final ACModelParse obj;
    private ACGLBuffer patternBuffer;
    private int splitPatternOffsetSize;
    private int splitPatternPointIndexSize;
    private int splitPatternTriangleIndexSize;
    private int splitPatternParameterSize;
    private ACGLBuffer splittedTriangleAccouter;
    private ACGLBuffer PNTriangleBuffer;
    private ACProgram splitProgram = new ACProgram("splitProgram");
    private ACProgram genPNTriangleProgram = new ACProgram("genPNTriangleProgram");
    private GlobalInfoProvider modelInfoProvider;

    public PreComputeController(GlobalInfoProvider modelInfoProvider) {
        this.modelInfoProvider = modelInfoProvider;
    }

    public void glOnSurfaceCreated(Context c,
                                   List<GLSLPreprocessor> preComputeControllersPN,
                                   List<GLSLPreprocessor> preComputeControllersSplit) {
        //read split pattern
        readSplitPattern(c);

        //init buffer
        glInitBuffer();

        glInitShaderProgram(c, preComputeControllersPN, preComputeControllersSplit);

        glCompute();
        //GLUtil.glCheckError(TAG + "#glOnSurfaceCreated");
    }

    private void glCompute() {
        resetAtomicBuffer();
        glAsyncBuffer();
        final int layout_x = modelInfoProvider.getOriginalTriangleNumber() / local_size_x + 1;
        genPNTriangleProgram.compute(layout_x);
        splitProgram.compute(layout_x);
        glFinish();
    }

    private void glInitShaderProgram(Context c,
                                     List<GLSLPreprocessor> preComputeControllersPN,
                                     List<GLSLPreprocessor> preComputeControllersSplit) {
        String source;
        try {
            source = FileUtil.convertStreamToString(c.getAssets().open("pre_computer_gen_pn_triangle.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        preComputeControllersPN = new ArrayList<>(preComputeControllersPN);
        preComputeControllersSplit = new ArrayList<>(preComputeControllersSplit);
        wrapPreCompilerSplit(preComputeControllersSplit);

        genPNTriangleProgram.addShader(new ACShader(preCompile(source, preComputeControllersPN), GL_COMPUTE_SHADER));
        genPNTriangleProgram.glCompileAndLink(c);

        try {
            source = FileUtil.convertStreamToString(c.getAssets().open("pre_computer_split.glsl"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        splitProgram.addShader(new ACShader(preCompile(source, preComputeControllersSplit), GL_COMPUTE_SHADER));
        splitProgram.glCompileAndLink(c);
    }

    private void wrapPreCompilerSplit(List<GLSLPreprocessor> preComputeControllersSplit) {
        GLSLPreprocessor splitPreCompiler = new GLSLPreprocessor()
                .add("SPLIT_FACTOR", Float.toString(splitFactor) + "f")
                .add("MAX_SPLIT_FACTOR", Integer.toString(MAX_SPLIT))
                .add("LOOK_UP_TABLE", getLookupTableForI())
                .add("BUFFER_SPLIT_PARAMETER_NUMBER", Integer.toString(splitPatternParameterSize / 4))
                .add("BUFFER_SPLIT_TRIANGLE_INDEX_NUMBER", Integer.toString(splitPatternTriangleIndexSize / 4))
                .add("BUFFER_OFFSET_NUMBER_NUMBER", Integer.toString(splitPatternOffsetSize / 4))
                .add("BUFFER_SPLIT_POINT_INDEX_NUMBER", Integer.toString(splitPatternPointIndexSize));
        preComputeControllersSplit.add(splitPreCompiler);
    }

    private void glAsyncBuffer() {
        patternBuffer.glAsyncWithGPU(GL_DYNAMIC_DRAW);
        PNTriangleBuffer.glAsyncWithGPU(GL_STATIC_COPY);
        splittedTriangleAccouter.glAsyncWithGPU(GL_STATIC_READ);
    }

    private void glInitBuffer() {
        // init output pn-triangle buffer
        PNTriangleBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(4)
                .postUpdate(null, modelInfoProvider.getOriginalTriangleNumber() * Constant.PN_TRIANGLE_SIZE);

        splittedTriangleAccouter = ACGLBuffer.glGenBuffer(GL_ATOMIC_COUNTER_BUFFER).glSetBindingPoint(Constant.SPLIT_ATOMIC_BINDING_POINT);
    }

    private void resetAtomicBuffer() {
        ByteBuffer bb = ByteUtil.genDirectBuffer(ByteUtil.INT_BYTE_SIZE * 2);
        bb.putInt(0);
        bb.putInt(0);
        bb.flip();
        splittedTriangleAccouter.postUpdate(bb, bb.limit());
    }

    private void readSplitPattern(Context c) {
        InputStream inputStream;
        try {
            inputStream = c.getAssets().open("new_pattern_data.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int maxSplit = 0;
        String[] offsets = null;
        String[] pointIndexes = null;
        String[] triangleIndexes = null;
        String[] parameters = null;
        try {
            for (int i = 0; i < 5; ++i) {
                String line = reader.readLine();
                switch (line) {
                    case Constant.PATTERN_DATA_LEVEL:
                        maxSplit = Integer.parseInt(reader.readLine());
                        break;
                    case Constant.PATTERN_DATA_OFFSETS_AND_LENGTHS:
                        offsets = reader.readLine().trim().split(" ");
                        break;
                    case Constant.PATTERN_DATA_POINTS:
                        pointIndexes = reader.readLine().trim().split(" ");
                        break;
                    case Constant.PATTERN_DATA_TRIANGLES:
                        triangleIndexes = reader.readLine().trim().split(" ");
                        break;
                    case Constant.PATTERN_DATA_PARAMETERS:
                        parameters = reader.readLine().trim().split(" ");
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        if (maxSplit != MAX_SPLIT || offsets == null || pointIndexes == null || triangleIndexes == null || parameters == null) {
            throw new RuntimeException("MAX_SPLIT is not right");
        }

        splitPatternOffsetSize = offsets.length;
        splitPatternPointIndexSize = pointIndexes.length;
        splitPatternTriangleIndexSize = triangleIndexes.length;
        splitPatternParameterSize = parameters.length;
        final int capacity = (splitPatternOffsetSize + splitPatternPointIndexSize + splitPatternParameterSize + splitPatternTriangleIndexSize) * 4;
        ByteBuffer splitPatternData = ByteUtil.genDirectBuffer(capacity);

        for (String s : parameters) {
            splitPatternData.putFloat(Math.max(Float.parseFloat(s), 0));
        }
        for (String s : triangleIndexes) {
            splitPatternData.putInt(Integer.parseInt(s));
        }
        for (String s : offsets) {
            splitPatternData.putInt(Integer.parseInt(s));
        }
        for (String s : pointIndexes) {
            splitPatternData.putInt(Integer.parseInt(s));
        }
        splitPatternData.flip();

        patternBuffer = ACGLBuffer.glGenBuffer(GL_SHADER_STORAGE_BUFFER)
                .glSetBindingPoint(3)
                .postUpdate(splitPatternData, splitPatternData.limit());
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
        return ((ACACBO) splittedTriangleAccouter).getValue(0);
    }

    public int getSplittedPointNumber() {
        return ((ACACBO) splittedTriangleAccouter).getValue(1);
    }
}
