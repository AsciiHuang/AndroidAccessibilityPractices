package com.ascii.androidaccessibilitypractices;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

public class BitmapUtils {

	public static Bitmap createMirrorBitmap(Bitmap oldBitmap) {
		if (oldBitmap == null) { return null; }

		final int w = oldBitmap.getWidth();
		final int h = oldBitmap.getHeight();
		final int nH = h / 2;

		final Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Runtime.getRuntime().gc();
		final Bitmap newBitmap = Bitmap.createBitmap(w, nH, Bitmap.Config.ARGB_8888);
		final Bitmap reflectBitmap = Bitmap.createBitmap(oldBitmap, 0, h - nH, w, nH, matrix, false);
		Runtime.getRuntime().gc();
		final Canvas canvas = new Canvas(newBitmap);
		final Paint paint = new Paint();
		final LinearGradient shader = new LinearGradient(0, 0, 0, nH, 0x80ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(reflectBitmap, 0, 0, null);
		canvas.drawRect(0, 0, w, nH, paint);
		Runtime.getRuntime().gc();
		return newBitmap;
	}

	public static Bitmap getResourceBitmap(Resources res, int id){
		InputStream is = res.openRawResource(id);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return getBitmap(is, null, options);
	}

	public static Bitmap getBitmap(InputStream is, Rect outPadding, BitmapFactory.Options options){
		Bitmap bitmap = BitmapFactory.decodeStream(is, outPadding, options);
		try {
			if(is != null ) is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 回收圖片所占的記憶體
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap){
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * recycle bitmap 且做gc
	 * @param bitmap
	 */
	public static void recycleBitmapWithGc(Bitmap bitmap){
		recycleBitmap(bitmap);
		System.gc();
	}

	/**
	 * recycle bitmap set
	 * @param bitmapSet
	 */
	public static void recycleBitmapSet(Set<Bitmap> bitmapSet){
		for(Bitmap bitmap : bitmapSet){
			BitmapUtils.recycleBitmap(bitmap);
		}
	}

	/**
	 * recycle bitmap map
	 * @param bitmapMap
	 */
	public static void recycleBitmapMap(Map<String, Bitmap> bitmapMap){
		for(String key : bitmapMap.keySet()){
			BitmapUtils.recycleBitmap(bitmapMap.get(key));
		}
	}

	/**
	 * 判斷bitmapTmp是否不同，如果不同就回收bitmapTmp
	 *
	 * @param bitmapTmp
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap checkRecycle(Bitmap bitmapTmp, Bitmap bitmap){
		if(bitmap == null) return null;

		if(!bitmap.equals(bitmapTmp)){
			BitmapUtils.recycleBitmap(bitmapTmp);
		}
		return bitmap;
	}

	public static void releaseMemGc(){
		if((Runtime.getRuntime().freeMemory()/Runtime.getRuntime().totalMemory()) > 0.7)
			System.gc();
	}

	public static Bitmap GetURLBitmap(URL url)
	{
		//mantis.0010435 - java.io.IOException: BufferedInputStream is closed
		InputStream isCover = null;
		try{
			//改成呼叫一個判斷方法
			releaseMemGc();

			URLConnection conn = url.openConnection();
			conn.connect();
			isCover = conn.getInputStream();
			//mantis.0010232: 專輯封面未能正常顯示，這邊看起來會發生問題是因為  streamClosed ，但是還有地方再存取或是 open 這塊 Stream，
			//所以在 decodeStream 那塊 stream 的時候就出現問題，因此想說在這邊先檢查該 stream 是不是已經被處理掉了如果是的話就不要 decodeStream
			if(isCover!=null){
				return BitmapFactory.decodeStream(isCover);
			}else{
				return null;
			}
		}catch(IOException ex){
			return null;
		}catch(Exception e){
			return null;
		}finally{
			try {
				if(isCover!=null){
					isCover.close();
					isCover =null;
				}
			} catch (IOException e) {
			}
		}
	}

}