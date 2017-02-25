package pro.prieran.fractus;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PatternView extends View {
    private final List<PointF> patternPoints = new ArrayList<>();
    private final Paint paint = new Paint();

    private Path path;
    private float xPrev;
    private float yPrev;

    private boolean wtfIDo;

    public PatternView(Context context) {
        super(context);
        init();
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PatternView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (path == null) {
                    path = new Path();
                    if (wtfIDo) {
                        path.moveTo(xPrev, yPrev);
                    } else {
                        path.moveTo(x, y);
                    }
                } else {
                    path.lineTo(x, y);
                    wtfIDo = true;
                    xPrev = x;
                    yPrev = y;
                }
                patternPoints.add(new PointF(x, y));
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path != null) {
            canvas.drawPath(path, paint);
        }
    }

    public void onClearButtonClick() {
        wtfIDo = false;
        path = null;
        patternPoints.clear();
        invalidate();
    }

    public List<PointF> getPatternPoints() {
        return patternPoints;
    }

    private void init() {
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }
}

