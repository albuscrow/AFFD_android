package ac.affd_android.affdview.model;

import java.nio.Buffer;

/**
 * Created by ac on 5/11/16.
 * todo some describe
 */
public interface GlobalInfoProvider {
    int getOriginalTriangleNumber();
    int getOriginalPointNumber();
    int getSplitTriangleNumber();
    int getSplitPointNumber();
    Buffer getBsplineBodyInfo();
    Buffer getBsplineBodyFastControlPoint();
    int getRendererTriangleNumber();
}
