package net.dev.mylib.view.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 圆环图片
 * 点击图片会有变化
 * @author QD
 * 
 */
public class CircleAvatarImageView extends ImageView {

	private Bitmap image;
	private int width, height;
	private ColorMatrixColorFilter colorFilter;
	private boolean isClickable = true;

	public CircleAvatarImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CircleAvatarImageView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public CircleAvatarImageView(Context context, int width, int height) {
		super(context);
		this.width = width;
		this.height = height;
		init();
	}

	private void init() {
		if (width == 0 || height == 0) {
			width = getMeasuredWidth();
			height = getMeasuredHeight();
		}
		setOnTouchListener(onTouchListener);
	}

	/**
	 * 创造圆形图片
	 * 
	 * @return
	 */
	private Bitmap createFramedPhoto() {

		Rect dst = new Rect(0, 0, width, height);
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawOval(new RectF(0, 0, width, height), paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 绘制图片的时候，给画笔加一个灰色的蒙层
		if (colorFilter != null && isClickable)
			paint.setColorFilter(colorFilter);
		canvas.drawBitmap(image, null, dst, paint);
		return output;

	}

	@Override
	public void setClickable(boolean clickable) {
		isClickable = clickable;
		super.setClickable(clickable);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable != null) {
			image = ((BitmapDrawable) drawable).getBitmap();
		}
		if (image == null || image.isRecycled()) {
			return;
		}

		// 头像的宽高
		Paint paint = new Paint();
		Bitmap bitmap = createFramedPhoto();

		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}

	// 其中代码片段，是为了添加点击效果，点击时图片会变暗。
	public void changeLight(ImageView imageView, int brightness) {
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0,brightness,// 改变亮度
				0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
		colorFilter = new ColorMatrixColorFilter(cMatrix);
		invalidate();
	}

	public OnTouchListener onTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_CANCEL:
				changeLight((ImageView) view, 0);
				break;
			case MotionEvent.ACTION_UP:
				changeLight((ImageView) view, 0);
				break;
			case MotionEvent.ACTION_DOWN:
				changeLight((ImageView) view, -50);
				break;
			default:
				break;
			}
			return false;
		}
	};
}
