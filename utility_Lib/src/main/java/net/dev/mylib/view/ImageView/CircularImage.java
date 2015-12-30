package net.dev.mylib.view.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import net.dev.mylib.R;
import net.dev.mylib.cache.imageCache.ImageCache;
import net.dev.mylib.loaderimage.ImageFileLoader;

import java.util.concurrent.Future;

/**
 * @author xujian
 * 圆形头像
 */
public class CircularImage extends MaskedImage {
    private ImageFileLoader imageFileLoader=ImageFileLoader.getInstance();
	private Future<?> future;

	public CircularImage(Context paramContext) {
		super(paramContext);
	}

	public CircularImage(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CircularImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public Bitmap createMask() {
		int i = getWidth();
		int j = getHeight();
		Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
		Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint(1);
		localPaint.setColor(-16777216);
		float f1 = getWidth();
		float f2 = getHeight();
		RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
		localCanvas.drawOval(localRectF, localPaint);
		return localBitmap;
	}
    public void setImageUrl(String url) {
		if(url==null){
			return;
		}
		Bitmap bitmap= ImageCache.getInstance().getCache(url);
		if(bitmap!=null){
			setImageBitmap(bitmap);
		}else {
			if(future!=null){
				future.cancel(true);
			}
			future=imageFileLoader.execute(url, 400, 400, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						setImageBitmap(bitmap);
					}else{
                        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_micro);
                        setImageBitmap(mBitmap);

                    }
				}
			});
		}
    }

}
