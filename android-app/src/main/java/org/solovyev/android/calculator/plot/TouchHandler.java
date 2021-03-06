// Copyright (C) 2009-2010 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.AndroidUtils2;

class TouchHandler {

    static interface TouchHandlerListener {
        void onTouchDown(float x, float y);

        void onTouchMove(float x, float y);

        void onTouchUp(float x, float y);

        void onTouchZoomDown(float x1, float y1, float x2, float y2);

        void onTouchZoomMove(float x1, float y1, float x2, float y2);
    }

    @NotNull
    private final VelocityTracker velocityTracker = VelocityTracker.obtain();

    private boolean afterZoom;

    @NotNull
    private TouchHandlerListener listener;

    TouchHandler(@NotNull TouchHandlerListener listener) {
        this.listener = listener;
    }

    public boolean handleTouchEvent(@NotNull MotionEvent event) {
        // Calculator.log("touch " + event + ' ' + event.getPointerCount() + event.getPointerId(0));

        final int fullAction = event.getAction();
        final int action = fullAction & MotionEvent.ACTION_MASK;
        final int pointer = (fullAction & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;

        float x = event.getX();
        float y = event.getY();

        int pointerCount = AndroidUtils2.getPointerCountFromMotionEvent(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                afterZoom = false;
                velocityTracker.clear();
                velocityTracker.addMovement(event);
                listener.onTouchDown(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    if (afterZoom) {
                        velocityTracker.clear();
                        listener.onTouchDown(x, y);
                        afterZoom = false;
                    }
                    velocityTracker.addMovement(event);
                    listener.onTouchMove(x, y);
                } else if (pointerCount == 2) {
                    listener.onTouchZoomMove(x, y, AndroidUtils2.getXFromMotionEvent(event, 1), AndroidUtils2.getYFromMotionEvent(event, 1));
                }
                break;

            case MotionEvent.ACTION_UP:
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                listener.onTouchUp(x, y);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == 2) {
                    listener.onTouchZoomDown(x, y, AndroidUtils2.getXFromMotionEvent(event, 1), AndroidUtils2.getYFromMotionEvent(event, 1));
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (pointerCount == 2) {
                    afterZoom = true;
                }
                break;
        }
        return true;
    }

    public float getXVelocity() {
        return velocityTracker.getXVelocity();
    }

    public float getYVelocity() {
        return velocityTracker.getYVelocity();
    }
}
