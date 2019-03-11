/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sapp.yupi.ui.lettertiles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.sapp.yupi.R;

import org.jetbrains.annotations.NotNull;

import androidx.core.content.res.ResourcesCompat;

/**
 * A drawable that encapsulates all the functionality needed to display a letter tile to
 * represent a contact image.
 */
public class LetterTileDrawable extends Drawable {

    private final Paint mPaint;

    private static int sTileFontColor;
    private static float sLetterToTileRatio;
    private static Bitmap DEFAULT_PERSON_AVATAR;

    /**
     * Reusable components to avoid new allocations
     */
    private static final Paint sPaint = new Paint();
    private static final Rect sRect = new Rect();
    private static final char[] sFirstChar = new char[1];

    private float mScale = 1.0f;
    private float mOffset = 0.0f;
    private boolean mIsCircle = false;

    private int mColor;
    private Character mLetter = null;

    public LetterTileDrawable(final Context context) {
        Resources res = context.getResources();
        /* Letter tile */
        int sDefaultColor = res.getColor(R.color.primary_color);
        sTileFontColor = res.getColor(R.color.letter_tile_font_color);
        sLetterToTileRatio = res.getFraction(R.fraction.letter_to_tile_ratio, 1, 1);
        DEFAULT_PERSON_AVATAR = BitmapFactory.decodeResource(res, R.drawable.ic_person_white_120dp);
        sPaint.setTypeface(ResourcesCompat.getFont(context, R.font.roboto_regular));
        sPaint.setTextAlign(Align.CENTER);
        sPaint.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mColor = sDefaultColor;
    }

    @Override
    public void draw(@NotNull final Canvas canvas) {
        final Rect bounds = getBounds();
        if (!isVisible() || bounds.isEmpty()) {
            return;
        }
        // Draw letter tile.
        drawLetterTile(canvas);
    }

    /**
     * Draw the bitmap onto the canvas at the current bounds taking into account the current scale.
     */
    private void drawBitmap(final Bitmap bitmap, final int width, final int height,
                            final Canvas canvas) {
        // The bitmap should be drawn in the middle of the canvas without changing its width to
        // height ratio.
        final Rect destRect = copyBounds();

        // Crop the destination bounds into a square, scaled and offset as appropriate
        final int halfLength = (int) (mScale * Math.min(destRect.width(), destRect.height()) / 2);

        destRect.set(destRect.centerX() - halfLength,
                (int) (destRect.centerY() - halfLength + mOffset * destRect.height()),
                destRect.centerX() + halfLength,
                (int) (destRect.centerY() + halfLength + mOffset * destRect.height()));

        // Source rectangle remains the entire bounds of the source bitmap.
        sRect.set(0, 0, width, height);

        canvas.drawBitmap(bitmap, sRect, destRect, mPaint);
    }

    private void drawLetterTile(final Canvas canvas) {
        // Draw background color.
        sPaint.setColor(mColor);

        sPaint.setAlpha(mPaint.getAlpha());
        final Rect bounds = getBounds();
        final int minDimension = Math.min(bounds.width(), bounds.height());

        if (mIsCircle) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), minDimension / 2F, sPaint);
        } else {
            canvas.drawRect(bounds, sPaint);
        }

        // Draw letter/digit only if the first character is an english letter or there's a override

        if (mLetter != null) {
            // Draw letter or digit.
            sFirstChar[0] = mLetter;

            // Scale text by canvas bounds and user selected scaling factor
            sPaint.setTextSize(mScale * sLetterToTileRatio * minDimension);
            //sPaint.setTextSize(sTileLetterFontSize);
            sPaint.getTextBounds(sFirstChar, 0, 1, sRect);
            sPaint.setColor(sTileFontColor);

            // Draw the letter in the canvas, vertically shifted up or down by the user-defined
            // offset
            canvas.drawText(sFirstChar, 0, 1, bounds.centerX(),
                    bounds.centerY() + mOffset * bounds.height() - sRect.exactCenterY(),
                    sPaint);
        } else {
            // Draw the default image if there is no letter/digit to be drawn
            final Bitmap bitmap = DEFAULT_PERSON_AVATAR;
            drawBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                    canvas);
        }
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public void setAlpha(final int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(final ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }

    public LetterTileDrawable setLetter(Character letter) {
        mLetter = letter;
        return this;
    }

    public LetterTileDrawable setColor(int color) {
        mColor = color;
        return this;
    }

    public LetterTileDrawable setIsCircular(boolean isCircle) {
        mIsCircle = isCircle;
        return this;
    }
}
