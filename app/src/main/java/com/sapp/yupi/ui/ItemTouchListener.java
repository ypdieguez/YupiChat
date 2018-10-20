package com.sapp.yupi.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.sapp.yupi.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final int mMinFlingVelocity;

    private RecyclerView mRecyclerView;
    private View mView;
    private View mFgView;     // Foreground view (to be swiped)
    private View mBgLeftView; // Background view (to show)
    private View mBgRightView;
    private View mPrevFgView;

    private boolean mPaused;  // Recycler View is scrolling
    private boolean swipe = false; // mFgView is selected

    private float mFinalX = 0f; // Last translation

    private GestureDetector gestures;
    private Context mContext;
    private boolean fling = false;

    ItemTouchListener(Context context, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mContext = context;

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();

        /*
          This will ensure that this helper is paused during recycler view scrolling.
          If a scroll listener is already assigned, the caller should still pass scroll changes through
          to this listener.
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                mPaused = newState == RecyclerView.SCROLL_STATE_DRAGGING;
                if (mPaused && mFinalX != 0 && mFgView != null) {
                    mFinalX = 0;
                    spring(mFgView, 0);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            }
        });

        GestureListener listener = new GestureListener();
        gestures = new GestureDetector(context, listener);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return !mPaused && gestures.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestures.onTouchEvent(e);

        // releasing finger from screen
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (!fling) {
                up(mView, mFinalX);
            }

            mFinalX = 0;
            mView = null;
            mPrevFgView = mFgView;
            mFgView = null;
            mBgLeftView = null;
            mBgRightView = null;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public void up(final View view, float distanceX) {
        if (distanceX > 0) {
            View actionView = view.findViewById(R.id.view_background_left_actions);
            int actionWidth = actionView.getWidth();
            distanceX = distanceX >= actionWidth / 2 ? actionWidth : 0;
        } else {
            View actionView = view.findViewById(R.id.view_background_right_actions);
            int actionWidth = actionView.getWidth();
            distanceX = -distanceX >= actionWidth / 2 ? -actionWidth : 0;
        }

        spring(view.findViewById(R.id.view_foreground), distanceX);
    }

    private void setVisible(View bgLeftView, View bgRightView, float translationX) {
        if (translationX > 0) {
            bgLeftView.setVisibility(View.VISIBLE);
            bgRightView.setVisibility(View.GONE);
        } else {
            bgLeftView.setVisibility(View.GONE);
            bgRightView.setVisibility(View.VISIBLE);
        }
    }

    private void spring(final View view, final float distanceX) {
        SpringAnimation springX = new SpringAnimation(view, DynamicAnimation.TRANSLATION_X);

        springX.addEndListener((animation, canceled, value, velocity) -> {
            if (value == 0) {
                GradientDrawable border = new GradientDrawable();
                border.setColor(ContextCompat.getColor(mContext, R.color.background));
                view.setBackground(border);
            }
        });

        SpringForce springForceX = new SpringForce(distanceX);
        springForceX.setStiffness(SpringForce.STIFFNESS_LOW);
        springForceX.setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);
        springX.setSpring(springForceX);
        springX.start();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // Touching somewhere in the RecyclerView
            // Determine the View we touch
            mView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

            if (mView != null) {
                mFgView = mView.findViewById(R.id.view_foreground);
                mBgLeftView = mView.findViewById(R.id.view_background_left);
                mBgRightView = mView.findViewById(R.id.view_background_right);

                float fgX = mFgView.getTranslationX();
                swipe = !(fgX > e.getRawX() || fgX + mFgView.getWidth() < e.getRawX());

                if (swipe) {
                    mFinalX = fgX;
                }

                if (mPrevFgView != null && mFgView != mPrevFgView &&
                        mPrevFgView.getTranslationX() != 0) {
                    spring(mPrevFgView, 0);
                }
            }

            fling = false;

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (swipe && Math.abs(distanceX) > Math.abs(distanceY)) {
                // finger is moving horizontally
                mFinalX -= distanceX;
                setVisible(mBgLeftView, mBgRightView, mFinalX);
                mFgView.setTranslationX(mFinalX);

                GradientDrawable border = new GradientDrawable();
                border.setColor(ContextCompat.getColor(mContext, R.color.background));
                border.setStroke(1, ContextCompat.getColor(mContext, R.color.divider));
                mFgView.setBackground(border);

                return true;
            }
            // finger is moving vertically
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (swipe && Math.abs(velocityX) >= mMinFlingVelocity
                    && Math.abs(velocityX) > Math.abs(velocityY)) {
                final View view = mView;
                final View bgLeftView = view.findViewById(R.id.view_background_left);
                final View bgRightView = view.findViewById(R.id.view_background_right);
                final FlingAnimation flingX = new FlingAnimation(mFgView, DynamicAnimation.TRANSLATION_X);
                flingX.setStartVelocity(velocityX);
                flingX.setFriction(10f);
                flingX.addUpdateListener((animation, value, velocity) -> setVisible(bgLeftView, bgRightView, value));
                flingX.addEndListener((animation, canceled, value, velocity) -> up(view, value));
                flingX.start();

                fling = true;
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mFinalX = 0;
            if(mFgView != null) {
                spring(mFgView, 0);
            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            mFinalX = 0;
            if (mFgView != null) {
                spring(mFgView, 0);
            }
        }
    }
}
