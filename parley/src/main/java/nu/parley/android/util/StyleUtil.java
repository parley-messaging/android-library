package nu.parley.android.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import nu.parley.android.R;
import nu.parley.android.view.BalloonView;

public final class StyleUtil {

    public static Drawable getDrawable(Context context, TypedArray attributesArray, int key) {
        int drawableResourceId = attributesArray.getResourceId(key, -1);
        if (drawableResourceId == -1) {
            return null;
        }
        return AppCompatResources.getDrawable(context, drawableResourceId);
    }

    public static ColorStateList getColorStateList(TypedArray attributesArray, int key) {
        return attributesArray.getColorStateList(key);
    }

    @ColorInt
    @Nullable
    public static Integer getColor(TypedArray attributesArray, int key) {
        int color = attributesArray.getColor(key, -1);
        if (color == -1) {
            return null;
        }
        return color;
    }

    public static Typeface getFont(Context context, TypedArray attributesArray, int keyFontFamily) {
        // Font Family
        int fontFamilyResourceId = attributesArray.getResourceId(keyFontFamily, -1);
        if (fontFamilyResourceId == -1) {
            int fontFamilyConstantValue = attributesArray.getInt(keyFontFamily, -1);
            for (AndroidFontDefinition definition : AndroidFontDefinition.values()) {
                if (definition.key == fontFamilyConstantValue) {
                    return definition.getFont(Typeface.NORMAL);
                }
            }
            // We couldn't parse it: error
            throw new IllegalArgumentException("Unknown font passed for font family key `" + keyFontFamily + "` which has value: " + fontFamilyConstantValue);
        } else {
            return ResourcesCompat.getFont(context, fontFamilyResourceId);
        }
    }

    public static int getFontStyle(TypedArray attributesArray, int keyFontStyle) {
        // Font Style
        int fontStyleConstantValue = attributesArray.getInt(keyFontStyle, -1);
        if (fontStyleConstantValue == -1) {
            return Typeface.NORMAL;
        } else {
            switch (fontStyleConstantValue) {
                case Typeface.NORMAL:
                case Typeface.BOLD:
                case Typeface.ITALIC:
                case Typeface.BOLD_ITALIC:
                    return fontStyleConstantValue;
                default:
                    throw new IllegalArgumentException("Unknown font passed for font style key `" + keyFontStyle + "` which has value: " + fontStyleConstantValue);
            }
        }
    }

    public static StyleSpacing getSpacingData(TypedArray ta, int keyOverallSpacing, int keySpacingTop, int keySpacingRight, int keySpacingBottom, int keySpacingLeft) {
        int overallSpacing = ta.getDimensionPixelSize(keyOverallSpacing, -1);
        if (overallSpacing == -1) {
            // Attempt other spacing
            return new StyleSpacing(
                    getDimension(ta, keySpacingTop),
                    getDimension(ta, keySpacingRight),
                    getDimension(ta, keySpacingBottom),
                    getDimension(ta, keySpacingLeft)
            );
        } else {
            return new StyleSpacing(overallSpacing);
        }
    }

    public static int getDimension(TypedArray ta, int key) {
        return ta.getDimensionPixelSize(key, StyleSpacing.DEFAULT_SPACING_PX);
    }

    public static String getString(TypedArray ta, int key) {
        return ta.getString(key);
    }

    @Nullable
    public static Integer getInteger(TypedArray ta, int key) {
        int value = ta.getInteger(key, -1);
        if (value == -1) {
            return null;
        } else {
            return value;
        }
    }

    public static boolean getBoolean(TypedArray ta, int key, boolean defaultValue) {
        return ta.getBoolean(key, defaultValue);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // Fonts as defined in `attrs.xml`
    private enum AndroidFontDefinition {
        NORMAL(0, "normal"),
        SANS(1, "sans"),
        SERIF(2, "serif"),
        MONOSPACE(3, "monospace");

        private int key;
        private String fontFamilyName;

        AndroidFontDefinition(int key, String fontFamilyName) {
            this.key = key;
            this.fontFamilyName = fontFamilyName;
        }

        private Typeface getFont(int fontStyle) {
            return Typeface.create(fontFamilyName, fontStyle);
        }
    }

    public static final class Helper {

        /**
         * Applies a background color to the background drawable, if possible. Does nothing in case
         * there is no background drawable or when the key isn't set.
         *
         * @param view
         * @param ta
         * @param key
         */
        public static void applyBackgroundColor(View view, TypedArray ta, int key) {
            @ColorInt @Nullable Integer backgroundColor = StyleUtil.getColor(ta, key);
            if (backgroundColor != null) {
                view.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
            }
        }

        public static void applySpacing(View view, StyleSpacing data) {
            view.setPadding(data.left, data.top, data.right, data.bottom);
        }

        public static void applyLoaderTint(ProgressBar loader, @ColorInt int color) {
            loader.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        public static void applyCornerRadius(GradientDrawable mutatingDrawable, int topLeft, int topRight, int bottomRight, int bottomLeft) {
            mutatingDrawable.setCornerRadii(new float[] {topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
        }
    }

    public static final class StyleSpacing {

        private static final int DEFAULT_SPACING_PX = 0;

        public final int top;
        public final int right;
        public final int bottom;
        public final int left;

        private StyleSpacing(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }

        private StyleSpacing(int allSpacing) {
            this.top = allSpacing;
            this.right = allSpacing;
            this.bottom = allSpacing;
            this.left = allSpacing;
        }
    }
}
