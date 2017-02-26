package pro.prieran.fractus;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class FractusView extends View {
    public static final int BITMAP_SIZE = 4000;

    private List<PointF> points = new ArrayList<>();
    private List<PointF> patternPoints = null;

    private Bitmap bitmap;
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
                if (path.isEmpty()) {
                    path.moveTo(x, y);
                } else {
                    final float EPS = 50f;
                    if (points.size() != 0) {
                        PointF firstPoint = points.get(0);
                        if (abs(firstPoint.x - x) < EPS && abs(firstPoint.y - y) < EPS) {
                            x = firstPoint.x;
                            y = firstPoint.y;
                        }
                    }
                    path.lineTo(x, y);
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
        canvas.drawPath(path, paint);
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void onDoButtonClick(List<PointF> pattern) {
        patternPoints = pattern;

        points = new Twicer().iterateIt(patternPoints, points);

        // Paths
        path = new Path();
        PointF firstPoint = points.get(0);
        path.moveTo(firstPoint.x, firstPoint.y);
        for (int i = 1; i < points.size(); i++) {
            PointF oldPoint = points.get(i - 1);
            PointF newPoint = points.get(i);
            path.lineTo(newPoint.x, newPoint.y);
        }

        invalidate();
    }

    public void onClearButtonClick() {
        points.clear();
        path = new Path();
        patternPoints = null;
        invalidate();
    }

    public void onSaveButtonClick() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888);

        // Bitmap-ColHoz-3000
        float maxX = 0.0f;
        float maxY = 0.0f;

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            PointF pointF = points.get(i);
            if (pointF.x > maxX) {
                maxX = pointF.x;
            }
            if (pointF.x < minX) {
                minX = pointF.x;
            }
            if (pointF.y > maxY) {
                maxY = pointF.y;
            }
            if (pointF.y < minY) {
                minY = pointF.y;
            }
        }

        float paddingSize = 0.15f;

        float scaleX = BITMAP_SIZE * (1.0f - paddingSize) / (maxX - minX);
        float scaleY = BITMAP_SIZE * (1.0f - paddingSize) / (maxY - minY);

        float scale = Math.min(scaleX, scaleY);

        Canvas canvas = new Canvas(bitmap);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFFFFFFF);
        canvas.drawRect(0, 0, BITMAP_SIZE, BITMAP_SIZE, backgroundPaint);

        Paint bitmapPaint = new Paint(paint);
        bitmapPaint.setStrokeWidth(20f);
        backgroundPaint.setColor(0xFF000000);

        for (int i = 1; i < points.size(); i++) {
            PointF oldPoint = points.get(i - 1);
            PointF newPoint = points.get(i);

            float paddingY = (BITMAP_SIZE - (maxY - minY) * scale) / 2;
            float paddingX = (BITMAP_SIZE - (maxX - minX) * scale) / 2;

            float startX = (oldPoint.x - minX) * scale + paddingX;
            float startY = (oldPoint.y - minY) * scale + paddingY;
            float stopX = (newPoint.x - minX) * scale + paddingX;
            float stopY = (newPoint.y - minY) * scale + paddingY;

            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        canvas.save();
        storeImage(bitmap);
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TagToSearch", "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TagToSearch", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TagToSearch", "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = Environment.getExternalStorageDirectory();
//                + "/Android/data/"
//                + getContext().getApplicationContext().getPackageName()
//                + "/Files"

//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("TagToSearch", "Can't create directory");
//                return null;
//            }
//        }

        String fileName = "Fractus.png";
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}
