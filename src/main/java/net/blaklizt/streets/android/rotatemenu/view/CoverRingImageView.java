package net.blaklizt.streets.android.rotatemenu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import static net.blaklizt.streets.android.rotatemenu.view.RingOperationLayout.*;

@SuppressLint("DrawAllocation")
public class CoverRingImageView extends ImageView {

	private final Paint paint;

	private Canvas mCanvas = null;

	private int mOutsideInnerCircleWidth;

	private int mInsideInnerCircleWidth;

	private int mOutsideRingWidth;

	private int mInsideRingWidth;

	private int mRemoveOutsideCircleDynamicAngle = 0;

	private int mRemoveInsideCircleDynamicAngle = 0;

	private int mDrawOutsideCircleDynamicAngle = 0;

	private int mDrawInsideCircleDynamicAngle = 0;

	private Boolean mDrawOutsideCircleIsDone = null;

	private Boolean mDrawInsideCircleIsDone = null;

	private Boolean mRemoveOutsideCircleIsDone = null;

	private Boolean mRemoveInsideCircleIsDone = null;

	private Boolean mShowOutsideCircleBoolean = false;

	private Boolean mShowInsideCircleBoolean = false;

	private Boolean mHideOutsideCircleBoolean = false;

	private Boolean mHideInsideCircleBoolean = false;

	private Boolean mShowOutsideCircleIsRunning = false;

	private Boolean mShowInsideCircleIsRunning = false;

	private Boolean mHideOutsideCircleIsRunning = false;

	private Boolean mHideInsideCircleIsRunning = false;

	private static final int CIRCLE_COLOR = Color.WHITE;

	private static final int OUTSIDE_INNER_CIRCLE_WIDTH_FITER = 3;

	private static final int INSIDE_INNER_CIRCLE_WIDTH_FITER = 3;

	private static final int OUTSIDE_RING_WIDTH_FITER = 6;

	private static final int INTSIDE_RING_WIDTH_FITER = 4;

	public CoverRingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.paint = new Paint();

		this.paint.setAntiAlias(true);

		this.paint.setStyle(Style.STROKE);

		mRemoveOutsideCircleDynamicAngle = 0;
		mDrawOutsideCircleDynamicAngle = 0;

		mDrawOutsideCircleIsDone = true;
		mRemoveOutsideCircleIsDone = false;
		mRemoveInsideCircleIsDone = false;
		mDrawInsideCircleIsDone = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mOutsideInnerCircleWidth = mLevel2Width / 2 - OUTSIDE_INNER_CIRCLE_WIDTH_FITER;
		mInsideInnerCircleWidth = mLevel1Width / 2 - INSIDE_INNER_CIRCLE_WIDTH_FITER;

		mOutsideRingWidth = (mLevel3Width - mLevel2Width) / 2 + OUTSIDE_RING_WIDTH_FITER;
		mInsideRingWidth = (mLevel2Width - mLevel1Width) / 2 + INTSIDE_RING_WIDTH_FITER;

		mCanvas = canvas;
		super.onDraw(mCanvas);

		int imageViewWidth = getWidth() / 2;

		RectF outsideCircleRect = new RectF(imageViewWidth - (mOutsideInnerCircleWidth + 1 + mOutsideRingWidth / 2), imageViewWidth - (mOutsideInnerCircleWidth + 1 + mOutsideRingWidth / 2), imageViewWidth + (mOutsideInnerCircleWidth + 1 + mOutsideRingWidth / 2), imageViewWidth + (mOutsideInnerCircleWidth + 1 + mOutsideRingWidth / 2));

		RectF insideCircleRect = new RectF(imageViewWidth - (mInsideInnerCircleWidth + 1 + mOutsideRingWidth / 2), imageViewWidth - (mInsideInnerCircleWidth + 1 + mInsideRingWidth / 2), imageViewWidth + (mInsideInnerCircleWidth + 1 + mInsideRingWidth / 2), imageViewWidth + (mInsideInnerCircleWidth + 1 + mInsideRingWidth / 2));

		if (mRemoveInsideCircleIsDone) {
			removeInsideCircle(canvas, insideCircleRect);
		}
		if (mDrawInsideCircleIsDone) {
			drawInsideCircle(canvas, insideCircleRect);
		}
		if (mRemoveOutsideCircleIsDone) {
			removeOutsideCircle(canvas, outsideCircleRect);
		}
		if (mDrawOutsideCircleIsDone) {
			drawOutsideCircle(canvas, outsideCircleRect);
		}

		invalidate();
	}

	public void showInsideCircle() {
		if (!mRemoveInsideCircleIsDone && !mShowInsideCircleIsRunning && !mHideInsideCircleIsRunning) {
			mShowInsideCircleBoolean = true;
			mShowInsideCircleIsRunning = true;
		}
	}

	public void hideInsideCircle() {
		if (!mDrawInsideCircleIsDone && !mShowInsideCircleIsRunning && !mHideInsideCircleIsRunning) {
			mHideInsideCircleBoolean = true;
			mHideInsideCircleIsRunning = true;
		}
	}

	public void showOutsideCircle() {
		if (!mRemoveOutsideCircleIsDone && !mShowOutsideCircleIsRunning && !mHideOutsideCircleIsRunning) {
			mShowOutsideCircleBoolean = true;
			mShowOutsideCircleIsRunning = true;
		}
	}

	public void hideOutsideCircle() {
		if (!mDrawOutsideCircleIsDone && !mShowOutsideCircleIsRunning && !mHideOutsideCircleIsRunning) {
			mHideOutsideCircleBoolean = true;
			mHideOutsideCircleIsRunning = true;
		}
	}

	private void drawInsideCircle(Canvas canvas, RectF rect) {
		if (mDrawInsideCircleDynamicAngle == 360) {
			mDrawInsideCircleIsDone = false;
			mRemoveInsideCircleIsDone = true;
			mShowInsideCircleBoolean = false;
			mRemoveInsideCircleDynamicAngle = 0;
			mShowInsideCircleIsRunning = false;
			mHideInsideCircleIsRunning = false;
			mLevel2ISRuning = false;
		}
		this.paint.setColor(CIRCLE_COLOR);
		this.paint.setStrokeWidth(mInsideRingWidth);
		canvas.drawArc(rect, 360, mDrawInsideCircleDynamicAngle - 360, false, paint);

		if (mShowInsideCircleBoolean) {
			mDrawInsideCircleDynamicAngle += 4;
		}

	}

	private void removeInsideCircle(Canvas canvas, RectF rect) {
		if (mRemoveInsideCircleDynamicAngle == 360) {
			mDrawInsideCircleIsDone = true;
			mRemoveInsideCircleIsDone = false;
			mHideInsideCircleBoolean = false;
			mDrawInsideCircleDynamicAngle = 0;
			mHideInsideCircleIsRunning = false;
			mShowInsideCircleIsRunning = false;
			mLevel2ISRuning = false;

		}
		this.paint.setColor(CIRCLE_COLOR);
		this.paint.setStrokeWidth(mInsideRingWidth);
		canvas.drawArc(rect, -180, mRemoveInsideCircleDynamicAngle, false, paint);

		if (mHideInsideCircleBoolean) {
			mRemoveInsideCircleDynamicAngle += 4;
		}

	}

	private void drawOutsideCircle(Canvas canvas, RectF rect) {
		if (mDrawOutsideCircleDynamicAngle == 360) {
			mDrawOutsideCircleIsDone = false;
			mRemoveOutsideCircleIsDone = true;
			mShowOutsideCircleBoolean = false;
			mRemoveOutsideCircleDynamicAngle = 0;
			mShowOutsideCircleIsRunning = false;
			mHideOutsideCircleIsRunning = false;
			mLevel3ISRuning = false;

		}
		this.paint.setColor(CIRCLE_COLOR);
		this.paint.setStrokeWidth(mOutsideRingWidth);
		canvas.drawArc(rect, 360, mDrawOutsideCircleDynamicAngle - 360, false, paint);

		if (mShowOutsideCircleBoolean) {
			mDrawOutsideCircleDynamicAngle += 2;
		}

	}
	private void removeOutsideCircle(Canvas canvas, RectF rect) {
		if (mRemoveOutsideCircleDynamicAngle == 360) {
			mDrawOutsideCircleIsDone = true;
			mRemoveOutsideCircleIsDone = false;
			mHideOutsideCircleBoolean = false;
			mDrawOutsideCircleDynamicAngle = 0;
			mHideOutsideCircleIsRunning = false;
			mShowOutsideCircleIsRunning = false;
			mLevel3ISRuning = false;

		}
		this.paint.setColor(CIRCLE_COLOR);
		this.paint.setStrokeWidth(mOutsideRingWidth);
		canvas.drawArc(rect, -180, mRemoveOutsideCircleDynamicAngle, false, paint);

		if (mHideOutsideCircleBoolean) {
			mRemoveOutsideCircleDynamicAngle += 2;
		}

	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
