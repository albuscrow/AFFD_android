package ac.affd_android.app;

/**
 * Created by ac on 4/28/16.
 */
public interface Constant {
    int PRE_SPLIT_TRIANGLE_NUMBER = 100;
    int POINT_SIZE =  (3 + 3 + 2) * 4;
    int TRIANGLE_POINT_SIZE = 3 * POINT_SIZE;
    int PN_TRIANGLE_SIZE = (10 + 6) * 4 * 4;
    int TRIANGLE_INDEX_SIZE = 3 * 4;
    int VEC4_BYTE_SIZE = 4 * 4;

    boolean DEBUG_SWITCH = true;
    boolean ACTIVE_DEBUG_BUFFER = false;

    int MV_MATRIX_LOCATION = 0;
    int MVP_MATRIX_LOCATION = 1;
    int ATTR1_LOCATION = 0;
    int ATTR2_LOCATION = 1;
    int PRE_COMPUTE_INPUT_BINDING_POINT = 0;
    int PRE_COMPUTE_OUTPUT_POINT_BINDING_POINT = 1;
    int PRE_COMPUTE_OUTPUT_INDEX_BINDING_POINT = 2;
    int DEBUG_BINDING_POINT = 16;

    String PATTERN_DATA_LEVEL = "level";
    String PATTERN_DATA_OFFSETS_AND_LENGTHS = "offsets_and_lengths";
    String PATTERN_DATA_POINTS = "points";
    String PATTERN_DATA_TRIANGLES = "triangles";
    String PATTERN_DATA_PARAMETERS = "parameters";
}
