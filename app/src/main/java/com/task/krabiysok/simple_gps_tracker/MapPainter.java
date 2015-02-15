package com.task.krabiysok.simple_gps_tracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.location.Location;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Created by KrabiySok on 2/15/2015.
 */
public class MapPainter implements  SurfaceHolder.Callback {
    private static final float GRID_WIDTH_PER = 0.01f;
    private static final float LINE_WIDTH_PER = 0.005f;
    private static final float TRIANGLE_HEIGHT_PER = 0.04f;
    private static final float TRIANGLE_WIDTH_PER = 0.03f;
    private static final float TEXT_SIZE_PER = 0.05f;
    private static final float TEXT_INDENT_PER = TEXT_SIZE_PER / 2 + 0.04f;
    private static final double METERS_IN_DEGREE_Lon = 111120;
    private static final double METERS_IN_DEGREE_Lat = 99000;
    private static final int MAP_RESOLUTION = 100; // resolution of map 100m

    private PointF mMapCenter;
    private Paint mPaint;
    private float mGridWidth, mLineWidth, mTextIndent;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private Path mVertTriangle, mHorisTriangle;
    private PointF mPreviousPosition;
    private Context mContext;
    private Location mBeginLocation;
    private double metersInPixel;
    private Canvas mCanvas;  // Need for canvas copying
    private Bitmap mBitMap;  // Need for canvas copying

    MapPainter(SurfaceView surfaceView, Context context) {
        mSurfaceView = surfaceView;
        mSurfaceHolder = surfaceView.getHolder();
        mContext = context;
    }

    private void getParams() {
        float triangHeight, triangHalfWidth, lowerSide;
        mMapCenter = mPreviousPosition = new PointF(mSurfaceView.getWidth() / 2,
                mSurfaceView.getHeight() / 2);
        lowerSide = mMapCenter.x > mMapCenter.y ? mMapCenter.x : mMapCenter.y;
        mTextIndent = lowerSide * TEXT_INDENT_PER;
        metersInPixel = lowerSide / MAP_RESOLUTION;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(lowerSide * TEXT_SIZE_PER);
        mGridWidth = lowerSide * GRID_WIDTH_PER;
        mLineWidth = lowerSide * LINE_WIDTH_PER;
        triangHeight = lowerSide * TRIANGLE_HEIGHT_PER;
        triangHalfWidth = lowerSide * TRIANGLE_WIDTH_PER;
        mHorisTriangle = drawTriangle(new PointF(mSurfaceView.getWidth() - triangHeight,
                mMapCenter.y - triangHalfWidth), new PointF(mSurfaceView.getWidth() - triangHeight,
                mMapCenter.y + triangHalfWidth), new PointF(mSurfaceView.getWidth(), mMapCenter.y));
        mVertTriangle = drawTriangle(new PointF(mMapCenter.x - triangHalfWidth,
                triangHeight), new PointF(mMapCenter.x + triangHalfWidth,
                triangHeight), new PointF(mMapCenter.x, 0));
        mBitMap = Bitmap.createBitmap(mSurfaceView.getWidth(),
                mSurfaceView.getHeight(), Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitMap);
        cleanAll();
    }

    void drawNewPoint(Location location) {
        Canvas canvas;
        PointF currentPosition;
        // Remember first location(Start point)
        if (mBeginLocation == null) {
            mBeginLocation = location;
            return;
        }
        if (mPaint.getStrokeWidth() != mLineWidth) mPaint.setStrokeWidth(mLineWidth);

        Log.d("Log", "Location " + location.getLatitude() + " " + location.getLongitude());
        Toast.makeText(mContext, "Location " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();

        currentPosition = new PointF((float) (mMapCenter.x + ((location.getLatitude() -
                mBeginLocation.getLatitude()) * METERS_IN_DEGREE_Lat) * metersInPixel),
                (float) (mMapCenter.y + ((location.getLongitude() -
                        mBeginLocation.getLongitude()) * METERS_IN_DEGREE_Lon) * metersInPixel));


        Log.d("Log", "Posit " + currentPosition.x + " " + currentPosition.y);

        if (!mPreviousPosition.equals(currentPosition)) {

            String k = "Posit " + String.valueOf(currentPosition.x) + " " + String.valueOf(currentPosition.y);
            Toast.makeText(mContext, k, Toast.LENGTH_LONG).show();

            mCanvas.drawLine(mPreviousPosition.x, mPreviousPosition.y,
                    currentPosition.x, currentPosition.y, mPaint);
            mPreviousPosition = currentPosition;
            canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                synchronized (mSurfaceHolder) {
                    canvas.drawBitmap(mBitMap, 0, 0, null);
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        Log.d("Log", "draw grid");
        float horXPosition = mSurfaceView.getWidth() - mPaint.getTextSize() * mContext.getResources().
                getString(R.string.grid_east).length() / 2 - mPaint.getTextSize();
        if (mPaint.getStrokeWidth() != mGridWidth) mPaint.setStrokeWidth(mGridWidth);
        canvas.drawLine(0, mMapCenter.y, mSurfaceView.getWidth(), mMapCenter.y, mPaint);
        canvas.drawLine(mMapCenter.x, 0, mMapCenter.x, mSurfaceView.getHeight(), mPaint);
        canvas.drawPath(mHorisTriangle, mPaint);
        canvas.drawPath(mVertTriangle, mPaint);
        canvas.drawText(mContext.getResources().getString(R.string.grid_start),
                mMapCenter.x + mTextIndent, mMapCenter.y + mTextIndent +
                        mPaint.getTextSize(), mPaint);
        canvas.drawText(mContext.getResources().getString(R.string.grid_north),
                mMapCenter.x + mTextIndent, mTextIndent, mPaint);
        canvas.drawText(mContext.getResources().getString(R.string.grid_resolution),
                mMapCenter.x + mTextIndent, mTextIndent + mPaint.getTextSize(), mPaint);
        canvas.drawText(mContext.getResources().getString(R.string.grid_east), horXPosition,
                mMapCenter.y - mTextIndent, mPaint);
        canvas.drawText(mContext.getResources().getString(R.string.grid_resolution),
                horXPosition, mMapCenter.y + mTextIndent + mPaint.getTextSize(), mPaint);
    }

    private Path drawTriangle(PointF point1, PointF point2, PointF point3) {
        Path path = new Path();
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point1.x, point1.y);
        return path;
    }

    void cleanAll() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        mCanvas.drawColor(Color.WHITE);
        drawGrid(mCanvas);
        mPreviousPosition = mMapCenter;
        mBeginLocation = null;
        if (canvas != null) {
            synchronized (mSurfaceHolder){
                canvas.drawBitmap(mBitMap, 0, 0, null);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        getParams();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
    }
}
