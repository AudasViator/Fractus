package pro.prieran.fractus;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Twicer {

    public List<PointF> iterateIt(final List<PointF> originalCurve, final List<PointF> currentCurve) {
        final List<PointF> newCurve = new ArrayList<>(currentCurve.size() * originalCurve.size() + 1);
        final List<Pair<Integer, List<PointF>>> tempCurve = new CopyOnWriteArrayList<>();

        final int nThreads = Runtime.getRuntime().availableProcessors();
        final int step = 400;
        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int n = 0; n <= currentCurve.size() / step; n++) {
            final int finalN = n;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    final long before = System.currentTimeMillis();
                    int right = (finalN + 1) * step;
                    if ((finalN + 1) * step >= currentCurve.size()) {
                        right = currentCurve.size();
                    }
                    tempCurve.add(new Pair<>(finalN, twiceIt(originalCurve, currentCurve.subList(finalN * step, right))));
                    final long after = System.currentTimeMillis();
                    Log.d("TagToSearch", "From " + (finalN * step) + ", to " + String.valueOf(right) + ". " + currentCurve.size() + ", " + (after - before) / 1000 + "ms");
                }
            });
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(24, TimeUnit.HOURS);
            Log.d("TagToSearch", "Termination");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final List<Pair<Integer, List<PointF>>> tempTempCurve = new ArrayList<>();
        tempTempCurve.addAll(tempCurve);
        Collections.sort(tempTempCurve, (o1, o2) -> o1.first.compareTo(o2.first));
        for (Pair<Integer, List<PointF>> integerListPair : tempTempCurve) {
            newCurve.addAll(integerListPair.second);
        }
        return newCurve;
    }

    private List<PointF> twiceIt(final List<PointF> originalCurve, final List<PointF> currentCurve) {
        final List<PointF> newCurve = new ArrayList<>(currentCurve.size() * originalCurve.size() + 1);

        final PointF first = originalCurve.get(0);
        final PointF last = originalCurve.get(originalCurve.size() - 1);

        for (int k = 1; k < currentCurve.size(); k++) {
            final PointF previous = currentCurve.get(k - 1);
            final PointF current = currentCurve.get(k);

            float scaleFactor = (float) sqrt((pow((last.x - first.x), 2) + pow((last.y - first.y), 2)) / (pow((current.x - previous.x), 2) + pow((current.y - previous.y), 2)));
            double angle = atan2(current.y - previous.y, current.x - previous.x) - atan2(last.y - first.y, last.x - first.x);

            List<PointF> newPoints = new ArrayList<>();
            for (int i = 0; i < originalCurve.size(); i++) {
                PointF point = originalCurve.get(i);
                PointF rotatedPoint = rotate(new PointF(point.x - first.x, point.y - first.y), angle);
                PointF newPoint = new PointF(rotatedPoint.x / scaleFactor + previous.x, rotatedPoint.y / scaleFactor + previous.y);
                newPoints.add(newPoint);
                newCurve.add(newPoint);
            }

            for (int i = 1; i < newPoints.size(); i++) {
                PointF oldPoint = newPoints.get(i - 1);
                PointF newPoint = newPoints.get(i);
                final Path newPath = new Path();
                newPath.moveTo(oldPoint.x, oldPoint.y);
                newPath.lineTo(newPoint.x, newPoint.y);
            }
        }
        return newCurve;
    }

    private PointF rotate(PointF point, double angle) {
        float newX = (float) (point.x * cos(angle) - point.y * sin(angle));
        float newY = (float) (point.x * sin(angle) + point.y * cos(angle));
        return new PointF(newX, newY);
    }
}
