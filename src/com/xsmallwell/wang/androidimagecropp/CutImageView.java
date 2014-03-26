package com.xsmallwell.wang.androidimagecropp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 2014-03-26 23:32
 * 
 * @author wang_xiaohao wangxiaohao13@gmail.com
 * 
 */

public class CutImageView extends View {

	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 100;

	private final int SELECT_PADDING = 20; // 选择区域边界 在长方体内部边界+mSelectPadding
											// 内就为可变化状态

	private final int SELECT_LINE = 20; // 选择区域边界 在长方体内部边界+mSelectPadding
										// 内就为可变化状态

	private final int LINE_TCRUDE = 10; // 宽度

	private final int TOP_LINE = 1;
	private final int LEFT_LINE = 2;
	private final int RIGHT_LINE = 3;
	private final int BOTTOM_LINE = 4;
	private final int CENTER_LINE = 5;
	private final int POINT_WIDTH = 60; // 指示图标大小

	private Rect mSelectRect = null; // 被选中区域
	private Rect mSrcRect = null; // 原图区域
	private Rect mDstRect = null; // 需要画区域

	private Bitmap mBitmap = null;
	private Paint mMastPaint = null; // 阴影Paint
	private Paint mLinePaint = null; // 阴影Paint

	private Bitmap pointUp;
	private Bitmap pointLeft;
	private Bitmap pointRight;
	private Bitmap pointDown;

	private boolean mSelectMod = false; // 是否符合选择模式

	private int mSelectLine = -1;
	private float yOffset = 0;

	private int mScreenWidth; // 原始宽高
	private int mScreenHeight;

	private int mOriginalWidth; // 原始宽高
	private int mOriginalHeight;

	private float scal = 1.0f; // 缩放比

	private Point mDownPoint = null; // 按下位置

	public CutImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		mMastPaint = new Paint();
		mLinePaint = new Paint();
		Resources resources = getResources();

		mMastPaint.setColor(resources.getColor(R.color.viewfinder_mask));
		mLinePaint.setColor(resources.getColor(R.color.bg_green));

		mLinePaint.setFakeBoldText(true);

	}

	// 需要被调用
	public void init(Bitmap bitmap) {
		this.mBitmap = bitmap;
		initScreen();

		initPoint();
		mOriginalWidth = bitmap.getWidth();
		mOriginalHeight = bitmap.getHeight();
		mSelectRect = new Rect(mScreenWidth / 4, mScreenHeight / 4, mScreenWidth * 3 / 4, mScreenHeight * 3 / 4);

		Log.d("wyy", " mOriginalWidth: " + mOriginalWidth + " mOriginalHeight:" + mOriginalHeight);

		calculateScalingAndOffset();

	}

	/**
	 * 计算缩放比和偏移量
	 */
	private void calculateScalingAndOffset() {
		if (null == mBitmap) {
			return;
		}

		scal = (float) mScreenWidth / (float) mOriginalWidth; // 小于 1 缩小 、 大于1 放大 一般是1.0
		mSrcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());

		float actualHeight = ((float) mBitmap.getHeight() * scal);

		yOffset = (float) ((float) mScreenHeight - actualHeight) / 2.0f;

		mDstRect = new Rect(0, (int) yOffset, (int) mScreenWidth, (int) (yOffset + actualHeight));

		Log.d("wyy", " yOffset:" + yOffset + " scal:" + scal);
		Log.d("wyy", " mDstRect:" + mDstRect);

	}

	@SuppressWarnings("deprecation")
	private void initScreen() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();

		Log.d("wyy", "initScreen w : " + mScreenWidth + " -- h : " + mScreenHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 不做处理
		if (null == mBitmap || null == mSelectRect) {
			return;
		}

		if (null != mBitmap && null != mSrcRect && null != mDstRect) {
			canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);
		}

		drawMask(canvas);
		drawSelectLine(canvas);
		drawPoint(canvas);
		// drawLine(canvas);

	}

	private void drawLine(Canvas canvas) {

		for (int i = 0; i < mScreenHeight;) {
			canvas.drawText(" " + i, 100, i, mLinePaint);
			canvas.drawLine(0, i, mScreenWidth, i, mLinePaint);
			i += 20;
		}

		for (int i = 0; i < mScreenWidth;) {
			canvas.drawText(" " + i, i, 100, mLinePaint);
			canvas.drawLine(i, 0, i, mScreenHeight, mLinePaint);
			i += 20;
		}

	}

	private void drawSelectLine(Canvas canvas) {

		canvas.drawRect(mSelectRect.left, mSelectRect.top, mSelectRect.right, mSelectRect.top + LINE_TCRUDE, mLinePaint);
		canvas.drawRect(mSelectRect.left, mSelectRect.top + LINE_TCRUDE, mSelectRect.left + LINE_TCRUDE, mSelectRect.bottom - LINE_TCRUDE, mLinePaint);
		canvas.drawRect(mSelectRect.right - LINE_TCRUDE, mSelectRect.top + LINE_TCRUDE, mSelectRect.right, mSelectRect.bottom - LINE_TCRUDE, mLinePaint);
		canvas.drawRect(mSelectRect.left, mSelectRect.bottom - LINE_TCRUDE, mSelectRect.right, mSelectRect.bottom, mLinePaint);

	}

	/**
	 * draw阴影遮罩
	 * 
	 * @param canvas
	 */
	private void drawMask(Canvas canvas) {

		if (null == mSelectRect) {
			return;
		}

		canvas.drawRect(0, 0, mScreenWidth, mSelectRect.top, mMastPaint);
		canvas.drawRect(0, mSelectRect.top, mSelectRect.left, mSelectRect.bottom, mMastPaint);
		canvas.drawRect(mSelectRect.right, mSelectRect.top, mScreenWidth, mSelectRect.bottom, mMastPaint);
		canvas.drawRect(0, mSelectRect.bottom, mScreenWidth, mScreenHeight, mMastPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			int downX = (int) event.getX(); // 相对于屏幕
			int downY = (int) event.getY();

			// float rawX = event.getRawX();
			// float rawY = event.getRawY();

			Log.d("wyy", "down_getX：" + downX + " " + downY); // 距离左上角位置

			mSelectMod = isSelectMod(new Point(downX, downY));

			if (!mSelectMod) {
				break;
			}
			// 记录按下点
			mDownPoint = new Point(downX, downY);

			getSelectLine(new Point(downX, downY)); // 计算获得需要修改的线边

			// 无标题栏，此时getRawY() 领略大于getY()
			// android:theme="@android:style/Theme.NoTitleBar"
			// 全屏方式，此时getRawY() 等于getY()
			// android:theme="@android:style/Theme.NoTitleBar.Fullscreen"

			break;
		case MotionEvent.ACTION_MOVE:

			if (!mSelectMod) {
				break;
			}
			int moveX = (int) event.getX(); // 相对于屏幕
			int moveY = (int) event.getY();

			boolean isContinue = changeSelectRect(mSelectRect, new Point(moveX, moveY));

			if (!isContinue) {
				mSelectMod = false;
			}

			mDownPoint.set(moveX, moveY);
			postInvalidate();

			break;
		case MotionEvent.ACTION_UP:
			mSelectMod = true;
			// TODO 在此可以提前截取

			break;
		}

		return true;
	}

	/**
	 * 修改坐标
	 * 
	 * @param mSelectRect2
	 * @param add
	 */
	private boolean changeSelectRect(Rect mSelectRect, Point npoint) {

		boolean isContinue = true;

		int weight = Math.abs(mSelectRect.left - mSelectRect.right);
		int height = Math.abs(mSelectRect.top - mSelectRect.bottom);

		if (weight <= MIN_WIDTH && height <= MIN_HEIGHT) {
			mSelectLine = CENTER_LINE;
		}

		switch (mSelectLine) {

		case TOP_LINE:

			if (mSelectRect.bottom - (mSelectRect.top + (npoint.y - mDownPoint.y)) > MIN_HEIGHT) {
				mSelectRect.set(mSelectRect.left, mSelectRect.top + (npoint.y - mDownPoint.y), mSelectRect.right, mSelectRect.bottom);
			}

			break;
		case LEFT_LINE:
			if (mSelectRect.right - (mSelectRect.left + (npoint.x - mDownPoint.x)) > MIN_WIDTH) {
				mSelectRect.set(mSelectRect.left + (npoint.x - mDownPoint.x), mSelectRect.top, mSelectRect.right, mSelectRect.bottom);
			}
			break;
		case RIGHT_LINE:
			if ((mSelectRect.right + (npoint.x - mDownPoint.x)) - mSelectRect.left > MIN_WIDTH) {
				mSelectRect.set(mSelectRect.left, mSelectRect.top, mSelectRect.right + (npoint.x - mDownPoint.x), mSelectRect.bottom);
			}
			break;
		case BOTTOM_LINE:
			if ((mSelectRect.bottom + (npoint.y - mDownPoint.y)) - mSelectRect.top > MIN_HEIGHT) {
				mSelectRect.set(mSelectRect.left, mSelectRect.top, mSelectRect.right, mSelectRect.bottom + (npoint.y - mDownPoint.y));
			}
			break;
		case CENTER_LINE:
			// mSelectRect.set(mSelectRect.left+(npoint.x - mDownPoint.x),
			// mSelectRect.top-(npoint.y - mDownPoint.y), mSelectRect.right,
			// mSelectRect.bottom+(npoint.y - mDownPoint.y));
			mSelectRect.offset((npoint.x - mDownPoint.x), (npoint.y - mDownPoint.y));
			break;

		default:
			break;
		}

		return isContinue;

	}

	/**
	 * @Title: drawPoint
	 * @Description:
	 * @param
	 * @return void
	 * @throws
	 */
	private void drawPoint(Canvas canvas) {

		Point up = new Point(mSelectRect.left + mSelectRect.width() / 2 - POINT_WIDTH / 2, mSelectRect.top - POINT_WIDTH / 2);
		Point dowm = new Point(mSelectRect.left + mSelectRect.width() / 2 - POINT_WIDTH / 2, mSelectRect.bottom - POINT_WIDTH / 2);
		Point left = new Point(mSelectRect.left - POINT_WIDTH / 2, mSelectRect.top + mSelectRect.height() / 2 - POINT_WIDTH / 2);
		Point right = new Point(mSelectRect.right - POINT_WIDTH / 2, mSelectRect.top + mSelectRect.height() / 2 - POINT_WIDTH / 2);

		canvas.drawBitmap(pointUp, up.x, up.y, null);
		canvas.drawBitmap(pointDown, dowm.x, dowm.y, null);
		canvas.drawBitmap(pointLeft, left.x, left.y, null);
		canvas.drawBitmap(pointRight, right.x, right.y, null);

	}

	private void getSelectLine(Point point) {

		if (isPointInnerLine(new Rect(mSelectRect.left - SELECT_LINE, mSelectRect.top - SELECT_LINE, mSelectRect.right + SELECT_LINE, mSelectRect.top + SELECT_LINE), point)) {
			// top
			mSelectLine = TOP_LINE;
		} else if (isPointInnerLine(new Rect(mSelectRect.left - SELECT_LINE, mSelectRect.top + SELECT_LINE, mSelectRect.left + SELECT_LINE, mSelectRect.bottom - SELECT_LINE), point)) {
			// left
			mSelectLine = LEFT_LINE;
		} else if (isPointInnerLine(new Rect(mSelectRect.right - SELECT_LINE, mSelectRect.top + SELECT_LINE, mSelectRect.right + SELECT_LINE, mSelectRect.bottom - SELECT_LINE), point)) {
			// right
			mSelectLine = RIGHT_LINE;
		} else if (isPointInnerLine(new Rect(mSelectRect.left - SELECT_LINE, mSelectRect.bottom - SELECT_LINE, mSelectRect.right + SELECT_LINE, mSelectRect.bottom + SELECT_LINE), point)) {
			// bottom
			mSelectLine = BOTTOM_LINE;
		} else if (isPointInnerLine(new Rect(mSelectRect.left + SELECT_LINE, mSelectRect.top + SELECT_LINE, mSelectRect.right - SELECT_LINE, mSelectRect.bottom - SELECT_LINE), point)) {
			// bottom
			mSelectLine = CENTER_LINE;
		}

		Log.d("wyy", " -getSelectLine:" + mSelectLine);
	}

	/**
	 * point是否在rect中
	 * 
	 * @param temp
	 * @param point
	 */
	private boolean isPointInnerLine(Rect temp, Point point) {
		// Log.d("wyy", "temp:" + temp + " point:" + point);
		return temp.contains(point.x, point.y);
	}

	/**
	 * 判断是否需要选择 边界值40
	 */
	private boolean isSelectMod(Point point) {

		if (null == mSelectRect) {
			return false;
		}
		Rect temp = new Rect(mSelectRect.left - SELECT_PADDING, mSelectRect.top - SELECT_PADDING, mSelectRect.right + SELECT_PADDING, mSelectRect.bottom + SELECT_PADDING);

		return temp.contains(point.x, point.y);

	}

	public Rect getSelectRect() {
		return null == mSelectRect ? null : mSelectRect;
	}

	/**
	 * 返回被选中的图片
	 * 
	 * @return
	 */
	public Bitmap getSelectBitmap() {

		Bitmap newBitmap = null;

		int left = (int) (mSelectRect.left / scal);
		int top = (int) ((mSelectRect.top - yOffset) / scal);
		int weight = (int) (mSelectRect.width() / scal);
		int height = (int) (mSelectRect.height() / scal);

		if ((left + weight) > mOriginalWidth || (top + height) > mOriginalHeight || left <= 0 || top <= 0 || weight <= 0 || height <= 0) {
			return newBitmap; // 越界
		}

		if (null != mSelectRect) {
			newBitmap = Bitmap.createBitmap(mBitmap, left, top, weight, height);
		}

		return newBitmap;
	}

	/**
	 * 初始化四个方向按钮
	 * 
	 * @Title: initPoint
	 * @Description:
	 * @param
	 * @return void
	 * @throws
	 */
	private void initPoint() {
		Resources resources = getResources();
		pointUp = BitmapFactory.decodeResource(resources, R.drawable.fangx_up);
		pointDown = BitmapFactory.decodeResource(resources, R.drawable.fangx_down);
		pointLeft = BitmapFactory.decodeResource(resources, R.drawable.fangx_left);
		pointRight = BitmapFactory.decodeResource(resources, R.drawable.fangx_right);

		// POINT_WIDTH = 20
		pointUp = Bitmap.createScaledBitmap(pointUp, POINT_WIDTH, POINT_WIDTH, false);
		pointDown = Bitmap.createScaledBitmap(pointDown, POINT_WIDTH, POINT_WIDTH, false);
		pointLeft = Bitmap.createScaledBitmap(pointLeft, POINT_WIDTH, POINT_WIDTH, false);
		pointRight = Bitmap.createScaledBitmap(pointRight, POINT_WIDTH, POINT_WIDTH, false);

	}

	public void clear() {

		if (null != mBitmap) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

}
