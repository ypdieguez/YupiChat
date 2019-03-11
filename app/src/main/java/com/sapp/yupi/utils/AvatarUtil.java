package com.sapp.yupi.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Patterns;

import com.bumptech.glide.Glide;
import com.sapp.yupi.R;
import com.sapp.yupi.data.Contact;

import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AvatarUtil {

    private Context mContext;

    private static int sIconWidth;
    private static int sIconHeight;
    private static Bitmap sDefaultPersonBitmap;

    private AvatarUtil(Context context) {
        mContext = context;
        Resources resources = context.getResources();
        sIconHeight = (int) resources.getDimension(R.dimen.large_icon_height);
        sIconWidth = (int) resources.getDimension(R.dimen.large_icon_width);
    }

    public static AvatarUtil with(Context context) {
        return new AvatarUtil(context);
    }

    public Bitmap bitmapFromContact(Contact contact) throws ExecutionException, InterruptedException {
        Uri uri = contact.getThumbnailUri();

        Object model = uri;
        if (uri == null) {
            String name = contact.getName();
            if (!Patterns.PHONE.matcher(name).matches())
                model = renderLetterTile(name);
            else
                model = renderDefaultAvatar();
        }

        return Glide.with(mContext).asBitmap().load(model).apply(
                bitmapTransform(new RoundedCornersTransformation(90, 0,
                        RoundedCornersTransformation.CornerType.ALL))
        ).submit().get();
    }

    private Bitmap renderDefaultAvatar() {
        int width = sIconWidth;
        int height = sIconHeight;

        final Bitmap bitmap = createBitmap(width, height, getBackgroundColor());
        final Canvas canvas = new Canvas(bitmap);

        if (sDefaultPersonBitmap == null) {
            final BitmapDrawable defaultPerson = (BitmapDrawable) mContext.getResources()
                    .getDrawable(R.drawable.ic_person_white_120dp);
            sDefaultPersonBitmap = defaultPerson.getBitmap();
        }

        Bitmap defaultPerson = sDefaultPersonBitmap;

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Matrix matrix = new Matrix();
        final RectF source = new RectF(0, 0, defaultPerson.getWidth(), defaultPerson.getHeight());
        final RectF dest = new RectF(0, 0, width, height);
        matrix.setRectToRect(source, dest, Matrix.ScaleToFit.FILL);

        canvas.drawBitmap(defaultPerson, matrix, paint);

        return bitmap;
    }

    private Bitmap renderLetterTile(final String name) {
        int width = sIconWidth;
        int height = sIconHeight;

        final float halfWidth = width / 2f;
        final float halfHeight = height / 2f;
        final int minOfWidthAndHeight = Math.min(width, height);
        final Bitmap bitmap = createBitmap(width, height, getBackgroundColor());
        final Resources resources = mContext.getResources();
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        paint.setColor(resources.getColor(R.color.letter_tile_font_color));
        final float letterToTileRatio = resources.getFraction(R.fraction.letter_to_tile_ratio, 1, 1);
        paint.setTextSize(letterToTileRatio * minOfWidthAndHeight);

        final String firstCharString = name.substring(0, 1).toUpperCase();
        final Rect textBound = new Rect();
        paint.getTextBounds(firstCharString, 0, 1, textBound);

        final Canvas canvas = new Canvas(bitmap);
        final float xOffset = halfWidth - textBound.centerX();
        final float yOffset = halfHeight - textBound.centerY();
        canvas.drawText(firstCharString, xOffset, yOffset, paint);

        return bitmap;
    }

    /**
     * Create a new bitmap.
     *
     * @param width           desired bitmap width
     * @param height          desired bitmap height
     * @param backgroundColor the background color for the returned bitmap
     * @return the created or reused mutable bitmap with the requested background color
     */
    private Bitmap createBitmap(final int width, final int height,
                                final int backgroundColor) {
        Bitmap retBitmap;
        retBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        retBitmap.eraseColor(backgroundColor);
        return retBitmap;
    }

    private int getBackgroundColor() {
        return mContext.getResources().getColor(R.color.primary_color);
    }
}
