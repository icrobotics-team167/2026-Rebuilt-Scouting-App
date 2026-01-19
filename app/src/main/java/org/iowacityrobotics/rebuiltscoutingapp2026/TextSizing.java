package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Context;
import android.util.DisplayMetrics;

public final class TextSizing {

    private static float scale = 1f;

    public static void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float screenWidthDp = dm.widthPixels / dm.density;

        scale = screenWidthDp / 1280f;

        scale *= context.getResources().getConfiguration().fontScale;
    }

    public static float sp(float baseSp) {
        return baseSp * scale;
    }

    public static float sp(float baseSp, float min, float max) {
        float size = baseSp * scale;
        return Math.max(min, Math.min(size, max));
    }
}
