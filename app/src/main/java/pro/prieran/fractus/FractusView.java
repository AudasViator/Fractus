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

public class FractusView extends View {
    private List<Path> paths = new ArrayList<>();
    private List<PointF> points = new ArrayList<>();

    private List<PointF> originalCurve = null;

    // TODO: Remove it
    private float xPrev;
    private float yPrev;

    private Paint paint;
    private Path path;

    public FractusView(Context context) {
        super(context);
        init();
    }

    public FractusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FractusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FractusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
                    if (paths.size() != 0) {
                        path.moveTo(xPrev, yPrev);
                    } else {
                        path.moveTo(x, y);
                    }
                } else {
                    path.lineTo(x, y);
                    paths.add(path);
                    xPrev = x;
                    yPrev = y;
                }
                points.add(new PointF(x, y));
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path path : paths) {
            if (path != null) {
                canvas.drawPath(path, paint);
            }
        }
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void onDoButtonClick() {
        paths = new ArrayList<>(points.size());

        if (originalCurve == null) {
            originalCurve = points;
        }

        points = new Twicer().iterateIt(originalCurve, points);
        for (int i = 1; i < points.size(); i++) {
            PointF oldPoint = points.get(i - 1);
            PointF newPoint = points.get(i);
            final Path newPath = new Path();
            newPath.moveTo(oldPoint.x, oldPoint.y);
            newPath.lineTo(newPoint.x, newPoint.y);
            paths.add(newPath);
        }
        invalidate();
    }

    public void onClearButtonClick() {
        points.clear();
        paths.clear();
        path = null;
        originalCurve = null;
        xPrev = 0;
        yPrev = 0;
        invalidate();
    }
}
