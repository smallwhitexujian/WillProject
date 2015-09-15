package net.dev.mylib.view.ImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import net.dev.mylib.R;
import net.dev.mylib.cache.imageCache.ImageCache;
import net.dev.mylib.loaderimage.ImageLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;


/**
 * ImageView extended class allowing easy downloading of remote images
 */
public class RemoteImageView extends ImageView {

	ImageCache imageCache;

	private Paint paint;
	private int roundWidth = 8;
	private int roundHeight = 8;
	private Paint paint2;

	private static final int MAX_FAIL_TIME = 5;
	private int mFails = 0;
	private String mUrl;

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		imageCache = new ImageCache(context);
		init(context, attrs);
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageCache = new ImageCache(context);
		init(context, attrs);
	}

	public RemoteImageView(Context context) {
		super(context);
		imageCache = new ImageCache(context);
		init(context, null);
	}

	public String GetUrl() {
		return mUrl;
	}

	@SuppressLint("Recycle")
	private void init(Context context, AttributeSet attrs) {

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.RoundCornerImageView);
			roundWidth = a.getDimensionPixelSize(
					R.styleable.RoundCornerImageView_roundWidth, roundWidth);
			roundHeight = a.getDimensionPixelSize(
					R.styleable.RoundCornerImageView_roundHeight, roundHeight);
		} else {
			float density = context.getResources().getDisplayMetrics().density;
			roundWidth = (int) (roundWidth * density);
			roundHeight = (int) (roundHeight * density);
		}
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

		paint2 = new Paint();
		paint2.setXfermode(null);
	}

	@Override
	public void draw(Canvas canvas) {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap);
		super.draw(canvas2);
		drawLeftUp(canvas2);
		drawRightUp(canvas2);
		drawLeftDown(canvas2);
		drawRightDown(canvas2);
		canvas.drawBitmap(bitmap, 0, 0, paint2);
		bitmap = null;
	}

	private void drawLeftUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, roundHeight);
		path.lineTo(0, 0);
		path.lineTo(roundWidth, 0);
		path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2), -90, -90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawLeftDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, getHeight() - roundHeight);
		path.lineTo(0, getHeight());
		path.lineTo(roundWidth, getHeight());
		path.arcTo(new RectF(0, getHeight() - roundHeight * 2,
				0 + roundWidth * 2, getHeight()), 90, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth() - roundWidth, getHeight());
		path.lineTo(getWidth(), getHeight());
		path.lineTo(getWidth(), getHeight() - roundHeight);
		path.arcTo(new RectF(getWidth() - roundWidth * 2, getHeight()
				- roundHeight * 2, getWidth(), getHeight()), 0, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), roundHeight);
		path.lineTo(getWidth(), 0);
		path.lineTo(getWidth() - roundWidth, 0);
		path.arcTo(new RectF(getWidth() - roundWidth * 2, 0, getWidth(),
				0 + roundHeight * 2), -90, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	public void setDefaultImage(int resId) {
		this.setImageResource(resId);
	}

	public void setImageUrl(String url) {

		if (url.indexOf("http") < 0) {
			this.setImageBitmap(getLoacalBitmap(url));
			return;
		}

		if (mUrl != null && mUrl.equals(url)) {
			mFails++;
		} else {
			mFails = 0;
			mUrl = url;
		}

		if (mFails >= MAX_FAIL_TIME)
			return;

		mUrl = url;

		if (isCached(url))
			return;

		startDownload(url);
	}
	 
	public static Bitmap getLoacalBitmap(String url) {
		
		Bitmap bm =  ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage2(url, 1000, 1000);
		
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isCached(String url) {
		Bitmap result = imageCache.getBitmap(url);
		if (result != null) {
			this.setImageBitmap(result);
			return true;
		}
		return false;
	}

	private void startDownload(String url) {
		try {
			new DownloadTask().execute(url);
		} catch (RejectedExecutionException e) {
		}
	}

	private void reDownload(String url) {
		setImageUrl(url);
	}

	class DownloadTask extends AsyncTask<String, Void, String> {

		private String imageUrl;

		@Override
		protected String doInBackground(String... params) {
			imageUrl = params[0];
			InputStream is = null;
			Bitmap bmp = null;

			try {
				URL url = new URL(imageUrl);
				is = url.openStream();
				bmp = BitmapFactory.decodeStream(is);
				if (bmp != null) {
					imageCache.put(imageUrl, bmp);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return imageUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			Bitmap bmp = imageCache.getBitmap(result);
			if (bmp != null) {
				RemoteImageView.this.setImageBitmap(bmp);
			} else {
				reDownload(imageUrl);
			}
			super.onPostExecute(result);
		}
		
	}
}
