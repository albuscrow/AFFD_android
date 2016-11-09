package ac.affd_android.affdview.model;

/**
 * Created by ac on 5/4/16.
 * todo some describe
 */
class SampleAuxMatrix {
    private static ACMatrix[] sampleAuxMatrix = new ACMatrix[]{
            // ------------------ MB1f, 首地址0 ------------------// 0
            new ACMatrix(new float[]{1.0f}, 1, 1),
            // ------------------ MB2, 首地址1 ------------------// 1
            new ACMatrix(new float[]{
                    1.0f, 0.0f,
                    -1.0f, 1.0f}, 2, 2),
            // ------------------ MB30f, 首地址5 -----------------// 2
            new ACMatrix(new float[]{
                    1.0f, 0.0f, 0.0f,
                    -2.0f, 2.0f, 0.0f,
                    1.0f, -2.0f, 1.0f}, 3, 3),
            // ------------------ MB31f, 首地址14 -----------------// 3
            new ACMatrix(new float[]{
                    1.0f, 0.0f, 0.0f,
                    -2.0f, 2.0f, 0.0f,
                    1.0f, -1.5f, 0.5f}, 3, 3),
            // ------------------ MB32f, 首地址23 -----------------// 4
            new ACMatrix(new float[]{
                    0.5f, 0.5f, 0.0f,
                    -1.0f, 1.0f, 0.0f,
                    0.5f, -1.5f, 1.0f}, 3, 3),
            // ------------------ MB33f, 首地址32 -----------------// 5
            new ACMatrix(new float[]{
                    0.5f, 0.5f, 0.0f,
                    -1.0f, 1.0f, 0.0f,
                    0.5f, -1.0f, 0.5f}, 3, 3),
            // ------------------ MB40f, 首地址41 -----------------// 6
            new ACMatrix(new float[]{
                    1.0f, 0.0f, 0.0f, 0.0f,
                    -3.0f, 3.0f, 0.0f, 0.0f,
                    3.0f, -6.0f, 3.0f, 0.0f,
                    -1.0f, 3.0f, -3.0f, 1.0f}, 4, 4),
            // ------------------ MB41f, 首地址57 -----------------// 7
            new ACMatrix(new float[]{
                    1.0f, 0.0f, 0.0f, 0.0f,
                    -3.0f, 3.0f, 0.0f, 0.0f,
                    3.0f, -4.5f, 1.5f, 0.0f,
                    -1.0f, 1.75f, -1.0f, 0.25f}, 4, 4),
            // ------------------ MB42f, 首地址73 -----------------// 8
            new ACMatrix(new float[]{
                    0.25f, 0.5f, 0.25f, 0.0f,
                    -0.75f, 0.0f, 0.75f, 0.0f,
                    0.75f, -1.5f, 0.75f, 0.0f,
                    -0.25f, 1.0f, -1.75f, 1.0f}, 4, 4),
            // ------------------ MB43f, 首地址89 -----------------// 9
            new ACMatrix(new float[]{
                    1.0f, 0.0f, 0.0f, 0.0f,
                    -3.0f, 3.0f, 0.0f, 0.0f,
                    3.0f, -4.5f, 1.5f, 0.0f,
                    -1.0f, 1.75f, -0.91666666666666666666f, 0.16666666666666666666f}, 4, 4),
            // ------------------ MB44f, 首地址105 -----------------// 10
            new ACMatrix(new float[]{
                    0.25f, 0.58333333333333333333f, 0.16666666666666666666f, 0.0f,
                    -0.75f, 0.25f, 0.5f, 0.0f,
                    0.75f, -1.25f, 0.5f, 0.0f,
                    -0.25f, 0.58333333333333333333f, -0.58333333333333333333f, 0.25f}, 4, 4),
            // ------------------ MB45f, 首地址121 -----------------// 11
            new ACMatrix(new float[]{
                    0.16666666666666666666f, 0.58333333333333333333f, 0.25f, 0.0f,
                    -0.5f, -0.25f, 0.75f, 0.0f,
                    0.5f, -1.25f, 0.75f, 0.0f,
                    -0.16666666666666666666f, 0.91666666666666666666f, -1.75f, 1.0f}, 4, 4),
            // ------------------ MB46f, 首地址137 -----------------// 12
            new ACMatrix(new float[]{
                    0.25f, 0.58333333333333333333f, 0.16666666666666666666f, 0.0f,
                    -0.75f, 0.25f, 0.5f, 0.0f,
                    0.75f, -1.25f, 0.5f, 0.0f,
                    -0.25f, 0.58333333333333333333f, -0.5f, 0.16666666666666666666f}, 4, 4),
            // ------------------ MB47f, 首地址153 -----------------// 13
            new ACMatrix(new float[]{
                    0.16666666666666666666f, 0.66666666666666666666f, 0.16666666666666666666f, 0.0f,
                    -0.5f, 0.0f, 0.5f, 0.0f,
                    0.5f, -1.0f, 0.5f, 0.0f,
                    -0.16666666666666666666f, 0.5f, -0.58333333333333333333f, 0.25f}, 4, 4),
            // ------------------ MB48f, 首地址169 -----------------// 14
            new ACMatrix(new float[]{
                    0.16666666666666666666f, 0.66666666666666666666f, 0.16666666666666666666f, 0.0f,
                    -0.5f, 0.0f, 0.5f, 0.0f,
                    0.5f, -1.0f, 0.5f, 0.0f,
                    -0.16666666666666666666f, 0.5f, -0.5f, 0.16666666666666666666f}, 4, 4)
    };


    static ACMatrix get_aux_matrix_offset(int order, int ctrollerPointNumber, int leftIndex) {
        if (order == 1) {
            return sampleAuxMatrix[0];                    // MB1
        } else if (order == 2) {
            return sampleAuxMatrix[1];                // MB2
        } else if (order == 3) {
            if (ctrollerPointNumber == 3) {
                return sampleAuxMatrix[2];            // MB30
            } else {
                if (leftIndex == 2) {
                    return sampleAuxMatrix[3];    // MB31
                } else if (leftIndex == ctrollerPointNumber - 1) {
                    return sampleAuxMatrix[4];    // MB32
                } else {
                    return sampleAuxMatrix[5];    // MB33
                }
            }
        } else if (order == 4) {
            if (ctrollerPointNumber == 4) {
                return sampleAuxMatrix[6];        // MB40
            } else if (ctrollerPointNumber == 5) {
                if (leftIndex == 3) {
                    return sampleAuxMatrix[7];    // MB41
                } else {
                    return sampleAuxMatrix[8];    // MB42
                }
            } else if (ctrollerPointNumber == 6) {
                if (leftIndex == 3) {
                    return sampleAuxMatrix[9];    // MB43
                } else if (leftIndex == 4) {
                    return sampleAuxMatrix[10];    // MB44
                } else {
                    return sampleAuxMatrix[11];    // MB45
                }
            } else {
                if (leftIndex == 3) {
                    return sampleAuxMatrix[9];    // MB43
                } else if (leftIndex == 4) {
                    return sampleAuxMatrix[12];    // MB46
                } else if (leftIndex == ctrollerPointNumber - 2) {
                    return sampleAuxMatrix[13];    // MB47
                } else if (leftIndex == ctrollerPointNumber - 1) {
                    return sampleAuxMatrix[11];    // MB45
                } else {
                    return sampleAuxMatrix[14];    // MB48
                }
            }
        } else {
            throw new RuntimeException();
        }
    }
}
