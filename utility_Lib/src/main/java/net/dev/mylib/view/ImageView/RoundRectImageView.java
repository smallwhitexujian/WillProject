package net.dev.mylib.view.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 生成带圆角的图片
 * @author xujian
 * 
 */
public class RoundRectImageView extends ImageView {
	private Bitmap bitmap;
	private int width, height;// 生成的图片的宽高
	private int layoutHeight, layoutWidth;// 图片布局所占的宽高
	private final Context context;

	public RoundRectImageView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.context = context;
	}

	public RoundRectImageView(Context context) {
		this(context, null);
	}

	/**
	 * 按照布局的大小等比例缩放图片
	 */
	private void calcBitmapSize() {
		int bitWidth = bitmap.getWidth(); // 图片本身所占的宽
		int bitHeight = bitmap.getHeight();
		layoutHeight = getLayoutParams().height - getPaddingTop()
				- getPaddingBottom();// 图片布局所占的高
		layoutWidth = getLayoutParams().width - getPaddingLeft()
				- getPaddingRight();// 图片布局所占的宽

		// 如果布局时，宽高没有指定，默认使用图片的宽高
		if (layoutHeight <= 0 || layoutWidth <= 0) {
			width = bitWidth;
			height = bitHeight;
			layoutHeight = bitHeight;
			layoutWidth = bitWidth;
			return;
		}

		float bitRadio = bitWidth / (bitHeight * 1.0f);// 图片宽高比例
		float layoutRadio = layoutWidth / (layoutHeight * 1.0f);// 布局宽高比例

		// 按照比例给图片缩放
		if (bitRadio >= layoutRadio) {
			width = layoutWidth;
			height = (int) (layoutWidth / bitRadio);
		} else {
			width = (int) (bitRadio * layoutHeight);
			height = layoutHeight;
		}

	}

	/**
	 * 创造圆形图片
	 * 
	 * @return
	 */
	public Bitmap toRoundCorner(int pixels) {
		calcBitmapSize();
		Bitmap output = Bitmap.createBitmap(layoutWidth, layoutHeight,Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xFFFFFFFF;
		Paint paint = new Paint();
		int left = 0;// 左边距
		int top = 0;// 上边距
		// 求出图片的左边、顶部内边距。将图片居中显示
		if (layoutWidth > 0 && layoutHeight > 0) {
			left = (layoutWidth - width) / 2;
			top = (layoutHeight - height) / 2;
			if (layoutWidth - width != 0)
				left += getPaddingLeft();
			if (layoutHeight - height != 0)
				top += getPaddingTop();
		}
		Rect rect = new Rect(left, top, width + left, height + top);
		RectF rectF = new RectF(rect);
		float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return output;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable != null) {
			bitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		if (bitmap == null || bitmap.isRecycled()) {
			super.onDraw(canvas);
			return;
		}
		// 画圆角图片，角度为4
		Bitmap bitmap = toRoundCorner(4);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public int px2dip(float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
