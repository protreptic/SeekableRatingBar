package name.peterbukhal.android.seekableratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.SeekBar;

/**
 * Created on 10/02/16 13:37 by
 *
 * @author Peter Bukhal (petr@taxik.ru)
 */
public class SeekableRatingBar extends View {

    public interface OnRatingChangedListener {
        void onRatingChanged(int rating);
    }

    public static final int DEFAULT_STARS_COUNT = 5;
    public static final int DEFAULT_RATING = 3;
    public static final int DEFAULT_STAR_BEAMS = 5;
    public static final float DEFAULT_STAR_RADIUS = 50f;
    public static final float DEFAULT_STAR_INNER_RADIUS = DEFAULT_STAR_RADIUS * 0.5f;
    public static final int DEFAULT_STAR_RATED_COLOR = Color.argb(179, 127, 136, 140);
    public static final int DEFAULT_STAR_UNRATED_COLOR = Color.argb(255, 220, 220, 220);

    private Paint ratedPaint;
    private Paint unratedPaint;

    private int stars = DEFAULT_STARS_COUNT;
    private int rating = DEFAULT_RATING;
    private int starBeams = DEFAULT_STAR_BEAMS;
    private float starRadius = DEFAULT_STAR_RADIUS;
    private float starInnerRadius = DEFAULT_STAR_INNER_RADIUS;
    private int starRatedColor = DEFAULT_STAR_RATED_COLOR;
    private int starUnratedColor = DEFAULT_STAR_UNRATED_COLOR;

    private OnRatingChangedListener listener;

    public SeekableRatingBar(Context context) {
        super(context);

        init();
    }

    public SeekableRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs, 0);
        init();
    }

    public SeekableRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        ratedPaint = new Paint();
        ratedPaint.setColor(starRatedColor);
        ratedPaint.setStyle(Paint.Style.FILL);
        ratedPaint.setAntiAlias(true);

        unratedPaint = new Paint();
        unratedPaint.setColor(starUnratedColor);
        unratedPaint.setStyle(Paint.Style.FILL);
        unratedPaint.setAntiAlias(true);
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SeekableRatingBar, defStyleAttr, 0);

        if (ta != null)
            try {
                if (ta.hasValue(R.styleable.SeekableRatingBar_stars)) {
                    stars = ta.getInt(R.styleable.SeekableRatingBar_stars, DEFAULT_STARS_COUNT);
                }
                if (ta.hasValue(R.styleable.SeekableRatingBar_rating)) {
                    rating = ta.getInt(R.styleable.SeekableRatingBar_rating, DEFAULT_RATING);
                }
                if (ta.hasValue(R.styleable.SeekableRatingBar_starBeams)) {
                    starBeams = ta.getInt(R.styleable.SeekableRatingBar_starBeams, DEFAULT_STAR_BEAMS);
                }
                if (ta.hasValue(R.styleable.SeekableRatingBar_starRadius)) {
                    starRadius = ta.getDimension(R.styleable.SeekableRatingBar_starRadius, DEFAULT_STAR_RADIUS);

                    if (starRadius < starInnerRadius) {
                        starRadius = starInnerRadius;
                    }

                    starInnerRadius = starRadius * 0.5f;
                }
                if (ta.hasValue(R.styleable.SeekableRatingBar_starInnerRadius)) {
                    starInnerRadius = ta.getDimension(R.styleable.SeekableRatingBar_starInnerRadius, DEFAULT_STAR_INNER_RADIUS);

                    if (starInnerRadius > starRadius) {
                        starInnerRadius = starRadius;
                    }
                }
                if (ta.hasValue(R.styleable.SeekableRatingBar_starRatedColor)) {
                    starRatedColor = ta.getColor(R.styleable.SeekableRatingBar_starRatedColor, DEFAULT_STAR_RATED_COLOR);
                }
                if (ta.hasValue(R.styleable.SeekableRatingBar_starUnratedColor)) {
                    starUnratedColor = ta.getColor(R.styleable.SeekableRatingBar_starUnratedColor, DEFAULT_STAR_UNRATED_COLOR);
                }
            } finally {
                ta.recycle();
            }

        SeekBar s;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;

        invalidate();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;

        invalidate();
    }

    public int getStarBeams() {
        return starBeams;
    }

    public void setStarBeams(int beams) {
        starBeams = beams;

        invalidate();
    }

    public float getStarRadius() {
        return starRadius;
    }

    public void setStarRadius(float radius) {
        starRadius = radius;
        starInnerRadius = radius * 0.5f;

        invalidate();
    }

    public float getStarInnerRadius() {
        return starInnerRadius;
    }

    public void setStarInnerRadius(float radius) {
        starInnerRadius = radius;

        invalidate();
    }

    public int getStarRatedColor() {
        return starRatedColor;
    }

    public void setStarRatedColor(int color) {
        starRatedColor = color;
        ratedPaint.setColor(starRatedColor);

        invalidate();
    }

    public int getStarUnratedColor() {
        return starUnratedColor;
    }

    public void setStarUnratedColor(int color) {
        starUnratedColor = color;

        invalidate();
    }

    public OnRatingChangedListener getOnRatingChangedListener() {
        return listener;
    }

    public void setOnRatingChangedListener(OnRatingChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) (padding + ((starRadius * 2 + padding) * stars)), (int) (starRadius * 2 + padding * 2));
    }

    private float padding = 0f;

    @Override
    protected void onDraw(Canvas canvas) {
        float x, y;

        x = starRadius + padding;
        y = starRadius + padding;

        for (int star = 0; star < stars; star++) {
            RatingStar ratingStar = new RatingStar(x + ((starRadius * 2 + padding) * star), y, starRadius, starInnerRadius, starBeams);

            canvas.drawPath(ratingStar, (rating > star ? ratedPaint : unratedPaint));
        }
    }

    public class RatingStar extends Path {

        public RatingStar(float x, float y, float radius, float innerRadius, int beams) {
            double section = 2 * Math.PI / beams;
            double startFrom = (3 * Math.PI) / 2;

            moveTo(
                    (float) (x + radius * Math.cos(startFrom)),
                    (float) (y + radius * Math.sin(startFrom)));
            lineTo(
                    (float) (x + innerRadius * Math.cos(startFrom + section / 2.0)),
                    (float) (y + innerRadius * Math.sin(startFrom + section / 2.0)));

            for(int i = 1; i < beams; i++) {
                lineTo(
                        (float) (x + radius * Math.cos(startFrom + section * i)),
                        (float) (y + radius * Math.sin(startFrom + section * i)));
                lineTo(
                        (float) (x + innerRadius * Math.cos(startFrom + section * i + section / 2.0)),
                        (float) (y + innerRadius * Math.sin(startFrom + section * i + section / 2.0)));
            }

            close();
        }

    }

    public final class Range<T extends Comparable<? super T>> {

        private final T mLower;
        private final T mUpper;

        public Range(final T lower, final T upper) {
            if (lower.compareTo(upper) > 0) {
                throw new IllegalArgumentException("lower must be less than or equal to upper");
            }

            mLower = lower;
            mUpper = upper;
        }

        public boolean contains(T value) {
            boolean gteLower = value.compareTo(mLower) >= 0;
            boolean lteUpper  = value.compareTo(mUpper) <= 0;

            return gteLower && lteUpper;
        }

    }

    private boolean isDragging;

    void onStartTrackingTouch() {
        isDragging = true;
    }

    void onStopTrackingTouch() {
        isDragging = false;
    }

    private float mTouchDownX;
    boolean mIsUserSeekable = true;
    private int mScaledTouchSlop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsUserSeekable || !isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
//                if (isInScrollingContainer()) {
//                    mTouchDownX = event.getX();
//                } else {
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();
                //}
            } break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    trackTouchEvent(event);
                } else {
                    final float x = event.getX();
                    if (Math.abs(x - mTouchDownX) > mScaledTouchSlop) {
                        setPressed(true);
                        onStartTrackingTouch();
                        trackTouchEvent(event);
                        attemptClaimDrag();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate(); // see above explanation
                break;
        }

        return true;
    }

    private void trackTouchEvent(MotionEvent event) {
        setRating(++rating);
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void dispatchRatingChange() {
        if (listener != null) {
            listener.onRatingChanged(rating);
        }
    }

}
