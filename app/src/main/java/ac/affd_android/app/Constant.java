package ac.affd_android.app;

/**
 * Created by ac on 4/28/16.
 */
public interface Constant {
    //    struct SplitTriangle {
    //        ivec3 pointIndex;
//        vec3 adjacent_pn_normal[6];
//    };
//    struct SPlitPoint {
//        vec4 pn_position3_tex1;
//        vec4 pn_normal3_tex1;
//        vec3 original_position;
//    };
    int PRE_SPLIT_TRIANGLE_SIZE = 112;
    int PRE_SPLIT_POINT_SIZE = 48;

    //平均每条边分成10 × 10 × 10段
    int PRE_SPLIT_TRIANGLE_NUMBER = 100;
    int PRE_SPLIT_POINT_NUMBER = 66;

    int PRE_SPLIT_TOTAL_SIZE = PRE_SPLIT_TRIANGLE_NUMBER * PRE_SPLIT_TRIANGLE_SIZE
            + PRE_SPLIT_POINT_NUMBER * PRE_SPLIT_POINT_SIZE;

    int POINT_SIZE = (3 + 3 + 2) * 4;
    int TRIANGLE_POINT_SIZE = 3 * POINT_SIZE;
    int PN_TRIANGLE_SIZE = (10 + 6) * 4 * 4;
    int TRIANGLE_INDEX_SIZE = 3 * 4;
    int VEC4_BYTE_SIZE = 4 * 4;

    boolean DEBUG_SWITCH = true;
    boolean ACTIVE_DEBUG_BUFFER = true;

    int MV_MATRIX_LOCATION = 0;
    int MVP_MATRIX_LOCATION = 1;
    int ATTR1_LOCATION = 0;
    int ATTR2_LOCATION = 1;
    int PRE_COMPUTE_INPUT_BINDING_POINT = 0;
    int RENDERER_POINT_BINDING_POINT = 1;
    int RENDERER_INDEX_BINDING_POINT = 2;
    int SPLIT_RESULT_BINDING_POINT = 5;
    int DEBUG_BINDING_POINT = 16;

    String PATTERN_DATA_LEVEL = "level";
    String PATTERN_DATA_OFFSETS_AND_LENGTHS = "offsets_and_lengths";
    String PATTERN_DATA_POINTS = "points";
    String PATTERN_DATA_TRIANGLES = "triangles";
    String PATTERN_DATA_PARAMETERS = "parameters";
}