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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class FractusView extends View {
    private final List<PointF> points = new ArrayList<>();
    private final List<Path> paths = new ArrayList<>();

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
            canvas.drawPath(path, paint);
        }
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
    }

    public void onDoButtonClick() {
        final PointF first = points.get(0);
        final float firstX = first.x;
        final float firstY = first.y;

        final PointF second = points.get(1);
        final float secondX = second.x;
        final float secondY = second.y;

        final PointF last = points.get(points.size() - 1);
        final float lastX = last.x;
        final float lastY = last.y;

        float coef = (float) sqrt((pow((lastX - firstX), 2) + pow((lastY - firstY), 2)) / (pow((secondX - firstX), 2) + pow((secondY - firstY), 2)));

        double angle = atan2(secondY - firstY, secondX - firstX) - atan2(lastY - firstY, lastX - firstX);
        Toast.makeText(getContext(), coef + " " + (-angle * 57.2958), Toast.LENGTH_SHORT).show();
        List<PointF> newPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            PointF point = points.get(i);
            PointF rotatedPoint = rotate(new PointF(point.x - firstX, point.y - firstY), angle);
            PointF newPoint = new PointF(rotatedPoint.x / coef + firstX, rotatedPoint.y / coef + firstY);
            newPoints.add(newPoint);
        }
        newPoints.size();
        for (int i = 1; i < newPoints.size(); i++) {
            PointF oldPoint = newPoints.get(i - 1);
            PointF newPoint = newPoints.get(i);
            final Path newPath = new Path();
            newPath.moveTo(oldPoint.x, oldPoint.y);
            newPath.lineTo(newPoint.x, newPoint.y);
            paths.add(newPath);
        }
        invalidate();
    }

    private PointF rotate(PointF point, double angle) {
        float newX = (float) (point.x * cos(angle) - point.y * sin(angle));
        float newY = (float) (point.x * sin(angle) + point.y * cos(angle));
        return new PointF(newX, newY);
    }
}
