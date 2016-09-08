package com.ttsea.jlibrary.photo.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

class PhotoViewAttacher implements IPhotoView, View.OnTouchListener,
        VersionedGestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private final String TAG = "Gallery.PhotoViewAttacher";

    private final int EDGE_NONE = -1;
    private final int EDGE_LEFT = 0;
    private final int EDGE_RIGHT = 1;
    private final int EDGE_BOTH = 2;

    private final float DEFAULT_MAX_SCALE = 5.0f;
    private final float DEFAULT_MID_SCALE = 2.5f;
    private final float DEFAULT_MIN_SCALE = 1.0f;

    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;

    private boolean mAllowParentInterceptOnEdge = true;

    private ImageSaveListener imageSaveListener;

    private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException("MinZoom should be less than MidZoom");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException("MidZoom should be less than MaxZoom");
        }
    }

    /**
     * @return true if the ImageView exists, and it's Drawable existss
     */
    private static boolean hasDrawable(ImageView imageView) {
        return null != imageView && null != imageView.getDrawable();
    }

    /**
     * @return true if the ScaleType is supported.
     */
    private boolean isSupportedScaleType(ScaleType scaleType) {
        if (null == scaleType) {
            return false;
        }

        switch (scaleType) {
            case MATRIX:
                return false;

            default:
                return true;
        }
    }

    public void setImageSaveListener(ImageSaveListener l) {
        this.imageSaveListener = l;
    }

    /**
     * Set's the ImageView's ScaleType to Matrix.
     */
    private void setImageViewScaleTypeMatrix(ImageView imageView) {
        if (null != imageView) {
            if (imageView instanceof PhotoView) {
                /**
                 * PhotoView sets it's own ScaleType to Matrix, then diverts all
                 * calls setScaleType to this.setScaleType. Basically we don't
                 * need to do anything here
                 */
            } else {
                imageView.setScaleType(ScaleType.MATRIX);
            }
        }
    }

    private WeakReference<ImageView> mImageViewRef;
    private ViewTreeObserver mViewTreeObserver;

    // Gesture Detectors
    private GestureDetector mGestureDetector;
    private VersionedGestureDetector mScaleDragDetector;

    // These are set so we don't keep allocating them on the heap
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final Matrix mRotate = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private final float[] mMatrixValues = new float[9];

    // Listeners
    private OnMatrixChangedListener mMatrixChangeListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnViewTapListener mViewTapListener;
    private OnLongClickListener mLongClickListener;

    private int mIvTop, mIvRight, mIvBottom, mIvLeft;
    private int mScrollEdge = EDGE_BOTH;
    private FlingRunnable mCurrentFlingRunnable;

    private boolean mZoomEnabled;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;

    public PhotoViewAttacher(ImageView imageView) {
        mImageViewRef = new WeakReference<ImageView>(imageView);

        imageView.setOnTouchListener(this);

        mViewTreeObserver = imageView.getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(this);

        // Make sure we using MATRIX Scale Type
        setImageViewScaleTypeMatrix(imageView);

        if (!imageView.isInEditMode()) {
            // Create Gesture Detectors...
            mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);

            mGestureDetector = new GestureDetector(imageView.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        // forward long click listener
                        @Override
                        public void onLongPress(MotionEvent e) {
                            if (null != mLongClickListener) {
                                mLongClickListener.onLongClick(mImageViewRef.get());
                            }
                        }
                    });

            mGestureDetector.setOnDoubleTapListener(this);
            // Finally, update the UI so that we're zoomable
            setZoomable(true);
        }
    }

    @Override
    public final boolean canZoom() {
        return mZoomEnabled;
    }

    @SuppressWarnings("deprecation")
    public final void cleanup() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            if (null != mImageViewRef) {
                mImageViewRef.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

            if (null != mViewTreeObserver && mViewTreeObserver.isAlive()) {
                mViewTreeObserver.removeOnGlobalLayoutListener(this);
                mViewTreeObserver = null;

                // Clear listeners too
                mMatrixChangeListener = null;
                mPhotoTapListener = null;
                mViewTapListener = null;
                // Finally, clear ImageView
                mImageViewRef = null;
            }

        } else {
            if (null != mImageViewRef) {
                mImageViewRef.get().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }

            if (null != mViewTreeObserver && mViewTreeObserver.isAlive()) {
                mViewTreeObserver.removeGlobalOnLayoutListener(this);
                mViewTreeObserver = null;

                // Clear listeners too
                mMatrixChangeListener = null;
                mPhotoTapListener = null;
                mViewTapListener = null;
                // Finally, clear ImageView
                mImageViewRef = null;
            }
        }
    }

    @Override
    public final RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDisplayMatrix());
    }

    public final ImageView getImageView() {
        ImageView imageView = null;

        if (null != mImageViewRef) {
            imageView = mImageViewRef.get();
        }

        // If we don't have an ImageView, call cleanup()
        if (null == imageView) {
            cleanup();
            throw new IllegalStateException("ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
        }

        return imageView;
    }


    /**
     * 获取图片的bitmap
     *
     * @return bitmap or null
     */
    private Bitmap getBitmap() {
        ImageView imageView = getImageView();
        if (imageView == null || !hasDrawable(imageView)) {
            JLog.e(TAG, "saveImage, imageView is null or has no Drawable");
            return null;
        }

        Bitmap bitmap = null;
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }

        if (bitmap == null) {
            JLog.e(TAG, "saveImage, bitmap is null");
        }

        return bitmap;
    }

    @Override
    public float getMinScale() {
        return mMinScale;
    }

    @Override
    public float getMidScale() {
        return mMidScale;
    }

    @Override
    public float getMaxScale() {
        return mMaxScale;
    }

    @Override
    public final float getScale() {
        return getValue(mSuppMatrix, Matrix.MSCALE_X);
    }

    @Override
    public final ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public final boolean onDoubleTap(MotionEvent ev) {
        try {
            float scale = getScale();
            float x = ev.getX();
            float y = ev.getY();

            if (scale < mMidScale) {
                zoomTo(mMidScale, x, y);
            } else if (scale >= mMidScale && scale < mMaxScale) {
                zoomTo(mMaxScale, x, y);
            } else {
                zoomTo(mMinScale, x, y);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            JLog.e(TAG, "ArrayIndexOutOfBoundsException e:" + e.toString());
            // Can sometimes happen when getX() and getY() is called
        }

        return true;
    }

    @Override
    public final boolean onDoubleTapEvent(MotionEvent e) {
        // Wait for the confirmed onDoubleTap() instead
        return false;
    }

    public final void onDrag(float dx, float dy) {
        JLog.d(TAG, String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy));

        ImageView imageView = getImageView();

        if (null != imageView && hasDrawable(imageView)) {
            mSuppMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();

            /**
             * Here we decide whether to let the ImageView's parent to start
             * taking over the touch event.
             *
             * First we check whether this function is enabled. We never want
             * the parent to take over if we're scaling. We then check the edge
             * we're on, and the direction of the scroll (i.e. if we're pulling
             * against the edge, aka 'overscrolling', let the parent take over).
             */
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling()) {
                if (mScrollEdge == EDGE_BOTH
                        || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                        || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                    imageView.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        }
    }

    @Override
    public final void onFling(float startX, float startY, float velocityX, float velocityY) {
        JLog.d(TAG, "onFling. sX: " + startX + " sY: " + startY
                + " Vx: " + velocityX + " Vy: " + velocityY);

        ImageView imageView = getImageView();
        if (hasDrawable(imageView)) {
            mCurrentFlingRunnable = new FlingRunnable(imageView.getContext());
            mCurrentFlingRunnable.fling(imageView.getWidth(),
                    imageView.getHeight(), (int) velocityX, (int) velocityY);
            imageView.post(mCurrentFlingRunnable);
        }
    }

    @Override
    public final void onGlobalLayout() {
        ImageView imageView = getImageView();

        if (null != imageView && mZoomEnabled) {
            final int top = imageView.getTop();
            final int right = imageView.getRight();
            final int bottom = imageView.getBottom();
            final int left = imageView.getLeft();

            /**
             * We need to check whether the ImageView's bounds have changed.
             * This would be easier if we targeted API 11+ as we could just use
             * View.OnLayoutChangeListener. Instead we have to replicate the
             * work, keeping track of the ImageView's bounds and then checking
             * if the values change.
             */
            if (top != mIvTop || bottom != mIvBottom || left != mIvLeft
                    || right != mIvRight) {
                // Update our base matrix, as the bounds have changed
                updateBaseMatrix(imageView.getDrawable());

                // Update values as something has changed
                mIvTop = top;
                mIvRight = right;
                mIvBottom = bottom;
                mIvLeft = left;
            }
        }
    }

    @Override
    public final void onScale(float scaleFactor, float focusX, float focusY) {
        JLog.d(TAG, String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", scaleFactor, focusX, focusY));

        if (hasDrawable(getImageView())
                && (getScale() < mMaxScale || scaleFactor < 1f)) {
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    public final boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            if (null != mPhotoTapListener) {
                final RectF displayRect = getDisplayRect();

                if (null != displayRect) {
                    final float x = e.getX(), y = e.getY();

                    // Check to see if the user tapped on the photo
                    if (displayRect.contains(x, y)) {
                        float xResult = (x - displayRect.left) / displayRect.width();
                        float yResult = (y - displayRect.top) / displayRect.height();

                        mPhotoTapListener.onPhotoTap(imageView, xResult, yResult);
                        return true;
                    }
                }
            }
            if (null != mViewTapListener) {
                mViewTapListener.onViewTap(imageView, e.getX(), e.getY());
            }
        }
        return false;
    }

    @Override
    public final boolean onTouch(View v, MotionEvent ev) {
        boolean handled = false;

        if (mZoomEnabled) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // First, disable the Parent from intercepting the touch event
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    // If we're flinging, and the user presses down, cancel fling
                    cancelFling();
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (getScale() < mMinScale) {
                        RectF rect = getDisplayRect();
                        if (null != rect) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMinScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                    break;
            }

            // Check to see if the user double tapped
            if (null != mGestureDetector && mGestureDetector.onTouchEvent(ev)) {
                handled = true;
            }

            // Finally, try the Scale/Drag detector
            if (null != mScaleDragDetector
                    && mScaleDragDetector.onTouchEvent(ev)) {
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    @Override
    public void setMinScale(float minScale) {
        checkZoomLevels(minScale, mMidScale, mMaxScale);
        mMinScale = minScale;
    }

    @Override
    public void setMidScale(float midScale) {
        checkZoomLevels(mMinScale, midScale, mMaxScale);
        mMidScale = midScale;
    }

    @Override
    public void setMaxScale(float maxScale) {
        checkZoomLevels(mMinScale, mMidScale, maxScale);
        mMaxScale = maxScale;
    }

    @Override
    public final void setOnLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    @Override
    public final void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mMatrixChangeListener = listener;
    }

    @Override
    public final void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    @Override
    public final void setOnViewTapListener(OnViewTapListener listener) {
        mViewTapListener = listener;
    }

    @Override
    public final void setScaleType(ScaleType scaleType) {
        if (isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType;
            // Finally update
            update();
        }
    }

    @Override
    public final void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;
        update();
    }

    public final void update() {
        ImageView imageView = getImageView();

        if (null != imageView) {
            if (mZoomEnabled) {
                // Make sure we using MATRIX Scale Type
                setImageViewScaleTypeMatrix(imageView);
                // Update the base matrix using the current drawable
                updateBaseMatrix(imageView.getDrawable());
            } else {
                // Reset the Matrix...
                resetMatrix();
            }
        }
    }

    @Override
    public final void zoomTo(float scale, float focalX, float focalY) {
        ImageView imageView = getImageView();
        if (null != imageView) {
            imageView.post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
        }
    }

    @Override
    public void rotate(float angle, float pivotX, float pivotY) {
        JLog.d(TAG, "rotate, angle:" + angle + ", pivotX:" + pivotX + ", pivotY:" + pivotY);
        if (!hasDrawable(getImageView())) {
            return;
        }

        Bitmap bitmap = getBitmap();

        if (bitmap == null || bitmap.isRecycled()) {
            JLog.e(TAG, "bitmap is null or is recycled");
            return;
        }
        mRotate.postRotate(angle, pivotX, pivotY);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mRotate, true);
        getImageView().setImageBitmap(bm);
        bitmap.recycle();
        update();
        mRotate.reset();
    }

    @Override
    public void rotate(float angle) {
        if (!hasDrawable(getImageView())) {
            return;
        }
        ImageView imageView = getImageView();
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        int x = width / 2;
        int y = height / 2;

        rotate(angle, x, y);
    }

    @Override
    public void saveImage(String savePath, String fileName) {
        if (imageSaveListener != null) {
            imageSaveListener.onStartSave();
        }
        JLog.d(TAG, "saveImage, savePath:" + savePath + File.separator + fileName);

        Bitmap bitmap = getBitmap();

        if (bitmap == null) {
            JLog.e(TAG, "saveImage, bitmap is null");
            if (imageSaveListener != null) {
                imageSaveListener.onSaveFailed("bitmap is null");
            }
            return;
        }

        if (bitmap.isRecycled()) {
            JLog.e(TAG, "saveImage, bitmap is recycled");
            if (imageSaveListener != null) {
                imageSaveListener.onSaveFailed("bitmap is null");
            }
            return;
        }

        File f = new File(savePath, fileName);
        SaveImageTask saveImageTask = new SaveImageTask(f, bitmap);
        saveImageTask.execute();
    }


    protected Matrix getDisplayMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private void cancelFling() {
        if (null != mCurrentFlingRunnable) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        checkMatrixBounds();
        setImageViewMatrix(getDisplayMatrix());
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();

        /**
         * PhotoView's getScaleType() will just divert to this.getScaleType() so
         * only call if we're not attached to a PhotoView.
         */
        if (null != imageView && !(imageView instanceof PhotoView)) {
            if (imageView.getScaleType() != ScaleType.MATRIX) {
                throw new IllegalStateException("The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher");
            }
        }
    }

    private void checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return;
        }

        final RectF rect = getDisplayRect(getDisplayMatrix());
        if (null == rect) {
            return;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = imageView.getHeight();
        if (height <= viewHeight) {
            switch (mScaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;

                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;

                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;

        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = imageView.getWidth();
        if (width <= viewWidth) {
            switch (mScaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;

                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;

                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            mScrollEdge = EDGE_BOTH;

        } else if (rect.left > 0) {
            mScrollEdge = EDGE_LEFT;
            deltaX = -rect.left;

        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdge = EDGE_RIGHT;

        } else {
            mScrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            Drawable d = imageView.getDrawable();
            if (null != d) {
                mDisplayRect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                matrix.mapRect(mDisplayRect);
                return mDisplayRect;
            }
        }
        return null;
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     - Matrix to unpack
     * @param whichValue - Which value from Matrix.M* to return
     * @return float - returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageViewMatrix(getDisplayMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (null != imageView) {

            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);

            // Call MatrixChangedListener if needed
            if (null != mMatrixChangeListener) {
                RectF displayRect = getDisplayRect(matrix);
                if (null != displayRect) {
                    mMatrixChangeListener.onMatrixChanged(displayRect);
                }
            }
        }
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (null == imageView || null == d) {
            return;
        }

        final float viewWidth = imageView.getWidth();
        final float viewHeight = imageView.getHeight();
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();

        mBaseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            switch (mScaleType) {
                case FIT_CENTER:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                    break;

                case FIT_START:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                    break;

                case FIT_END:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                    break;

                case FIT_XY:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                    break;

                default:
                    break;
            }
        }

        resetMatrix();
    }

    /**
     * Interface definition for a callback to be invoked when the internal
     * Matrix has changed for this View.
     *
     * @author Chris Banes
     */
    public interface OnMatrixChangedListener {
        /**
         * Callback for when the Matrix displaying the Drawable has changed.
         * This could be because the View's bounds have changed, or the user has
         * zoomed.
         *
         * @param rect - Rectangle displaying the Drawable's new bounds.
         */
        void onMatrixChanged(RectF rect);
    }

    /**
     * Interface definition for a callback to be invoked when the Photo is
     * tapped with a single tap.
     *
     * @author Chris Banes
     */
    public interface OnPhotoTapListener {

        /**
         * A callback to receive where the user taps on a photo. You will only
         * receive a callback if the user taps on the actual photo, tapping on
         * 'whitespace' will be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the of the Drawable, as
         *             percentage of the Drawable width.
         * @param y    - where the user tapped from the top of the Drawable, as
         *             percentage of the Drawable height.
         */
        void onPhotoTap(View view, float x, float y);
    }

    /**
     * Interface definition for a callback to be invoked when the ImageView is
     * tapped with a single tap.
     *
     * @author Chris Banes
     */
    public interface OnViewTapListener {

        /**
         * A callback to receive where the user taps on a ImageView. You will
         * receive a callback if the user taps anywhere on the view, tapping on
         * 'whitespace' will not be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the left of the View.
         * @param y    - where the user tapped from the top of the View.
         */
        void onViewTap(View view, float x, float y);
    }


    //获取变换矩阵Matrix中的每个值
    private void printMatrix(Matrix matrix) {
        float matrixValues[] = new float[9];
        matrix.getValues(matrixValues);
        JLog.d(TAG, "-------------");
        for (int i = 0; i < 3; i++) {
            String valueString = "";
            for (int j = 0; j < 3; j++) {
                int index = 3 * i + j;
                if (index % 3 == 0) {
                    valueString = valueString + "[ ";
                }
                valueString = valueString + matrixValues[index] + ", ";
            }
            valueString = valueString + "]";
            JLog.d(TAG, valueString);
        }
        JLog.d(TAG, "-------------");
    }

    private class AnimatedZoomRunnable implements Runnable {

        // These are 'postScale' values, means they're compounded each iteration
        static final float ANIMATION_SCALE_PER_ITERATION_IN = 1.07f;
        static final float ANIMATION_SCALE_PER_ITERATION_OUT = 0.93f;

        private final float mFocalX, mFocalY;
        private final float mTargetZoom;
        private final float mDeltaScale;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mTargetZoom = targetZoom;
            mFocalX = focalX;
            mFocalY = focalY;

            if (currentZoom < targetZoom) {
                mDeltaScale = ANIMATION_SCALE_PER_ITERATION_IN;
            } else {
                mDeltaScale = ANIMATION_SCALE_PER_ITERATION_OUT;
            }
        }

        public void run() {
            ImageView imageView = getImageView();

            if (null != imageView) {
                mSuppMatrix.postScale(mDeltaScale, mDeltaScale, mFocalX, mFocalY);
                checkAndDisplayMatrix();

                final float currentScale = getScale();

                if ((mDeltaScale > 1f && currentScale < mTargetZoom)
                        || (mDeltaScale < 1f && mTargetZoom < currentScale)) {
                    // We haven't hit our target scale yet, so post ourselves
                    // again
                    Compat.postOnAnimation(imageView, this);

                } else {
                    // We've scaled past our target zoom, so calculate the
                    // necessary scale so we're back at target zoom
                    final float delta = mTargetZoom / currentScale;
                    mSuppMatrix.postScale(delta, delta, mFocalX, mFocalY);
                    checkAndDisplayMatrix();
                }
            }
        }
    }

    private class FlingRunnable implements Runnable {
        private final ScrollerProxy mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {
            JLog.d(TAG, "Cancel Fling");
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            final RectF rect = getDisplayRect();
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            JLog.d(TAG, "fling. StartX:" + startX + " StartY:" + startY
                    + " MaxX:" + maxX + " MaxY:" + maxY);

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            ImageView imageView = getImageView();
            if (null != imageView && mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                JLog.d(TAG, "fling run(). CurrentX:" + mCurrentX + " CurrentY:" + mCurrentY
                        + " NewX:" + newX + " NewY:" + newY);

                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                setImageViewMatrix(getDisplayMatrix());

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                Compat.postOnAnimation(imageView, this);
            }
        }
    }

    private class SaveImageTask extends AsyncTask<Void, Void, String> {
        private File saveFile;
        private Bitmap bitmap;
        private Handler handler;

        public SaveImageTask(File saveFile, Bitmap bitmap) {
            this.saveFile = saveFile;
            this.bitmap = bitmap;
            handler = new Handler();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (saveFile.exists()) {
                saveFile.deleteOnExit();
            }
            if (!saveFile.exists()) {
                File parentFile = saveFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    JLog.e(TAG, "saveImage, IOException e:" + e.toString());
                    final String reason = e.toString();
                    if (imageSaveListener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageSaveListener.onSaveFailed(" " + reason);
                            }
                        });
                    }
                    return null;
                }
            }

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(saveFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            } catch (Exception e) {
                JLog.e(TAG, "saveImage, Exception e：" + e.toString());
                final String reason = e.toString();
                if (imageSaveListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageSaveListener.onSaveFailed(" " + reason);
                        }
                    });
                }
                return null;
            }

            try {
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }
            } catch (Exception e) {
                JLog.e(TAG, "saveImage, Exception e：" + e.toString());
                final String reason = e.toString();
                if (imageSaveListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageSaveListener.onSaveFailed(" " + reason);
                        }
                    });
                }
                return null;
            }

            return saveFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String s) {
            if (!Utils.isEmpty(s)) {
                // 最后通知图库更新
                Uri imgUri = Uri.parse("file://" + saveFile.getAbsolutePath());
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);
                if (getImageView() != null) {
                    getImageView().getContext().sendBroadcast(intent);
                }

                JLog.d(TAG, "onSaveComplete, path:" + saveFile.getAbsolutePath());
                if (imageSaveListener != null) {
                    imageSaveListener.onSaveComplete(saveFile.getAbsolutePath());
                }
            }
        }
    }
}
