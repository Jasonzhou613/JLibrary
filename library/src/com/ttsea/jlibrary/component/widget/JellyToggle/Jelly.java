package com.ttsea.jlibrary.component.widget.JellyToggle;

/**
 * Created by Weiping on 2016/5/11.
 */

public enum Jelly {

    ITSELF(new Itself()),
    LAZY_TREMBLE_HEAD_FATTY(new LazyTrembleHeadFatty()),
    LAZY_TREMBLE_HEAD_SLIM_JIM(new LazyTrembleHeadSlimJim()),
    LAZY_TREMBLE_TAIL_FATTY(new LazyTrembleTailFatty()),
    LAZY_TREMBLE_TAIL_SLIM_JIM(new LazyTrembleTailSlimJim()),
    LAZY_TREMBLE_BODY_FATTY(new LazyTrembleBodyFatty()),
    LAZY_TREMBLE_BODY_SLIM_JIM(new LazyTrembleBodySlimJim()),
    LAZY_STIFF_FATTY(new LazyStiffFatty()),
    LAZY_STIFF_SLIM_JIM(new LazyStiffSlimJim()),
    ACTIVE_TREMBLE_HEAD_FATTY(new ActiveTrembleHeadFatty()),
    ACTIVE_TREMBLE_HEAD_SLIM_JIM(new ActiveTrembleHeadSlimJim()),
    ACTIVE_TREMBLE_TAIL_FATTY(new ActiveTrembleTailFatty()),
    ACTIVE_TREMBLE_TAIL_SLIM_JIM(new ActiveTrembleTailSlimJim()),
    ACTIVE_TREMBLE_BODY_FATTY(new ActiveTrembleBodyFatty()),
    ACTIVE_TREMBLE_BODY_SLIM_JIM(new ActiveTrembleBodySlimJim()),
    ACTIVE_STIFF_FATTY(new ActiveStiffFatty()),
    ACTIVE_STIFF_SLIM_JIM(new ActiveStiffSlimJim()),
    RANDOM(new Random());

    public JellyStyle c;

    Jelly(JellyStyle c) {
        this.c = c;
    }

    public void changeShape(
            PointWithHorizontalPoints p1,
            PointWithVerticalPoints p2,
            PointWithHorizontalPoints p3,
            PointWithVerticalPoints p4,
            float stretchDistance,
            float bezierControlValue,
            float bezierScaleRatioValue,
            float thumbRadius,
            float process,
            State state) {
        try {
            (c).changeShape(p1, p2, p3, p4, stretchDistance, bezierControlValue, bezierScaleRatioValue, thumbRadius, process, state);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Jelly style init error, className:" + c.getClass().getSimpleName() + ", msg:" + e.toString());
        }
    }

    public void changeOffset(PointWithHorizontalPoints p1, PointWithVerticalPoints p2, PointWithHorizontalPoints p3, PointWithVerticalPoints p4, float totalLength, float extractLength, float process, State state, EaseType easeType) {
        try {
            (c).changeOffset(p1, p2, p3, p4, totalLength, extractLength, process, state, easeType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Jelly style init error, className:" + c.getClass().getSimpleName() + ", msg:" + e.toString());
        }
    }

    public float extractLength(float stretchDistance, float bezierControlValue, float bezierScaleRatioValue, float thumbRadius) {
        try {
            return (c).extractLength(stretchDistance, bezierControlValue, bezierScaleRatioValue, thumbRadius);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Jelly style init error, className:" + c.getClass().getSimpleName() + ", msg:" + e.toString());
        }
    }
}
