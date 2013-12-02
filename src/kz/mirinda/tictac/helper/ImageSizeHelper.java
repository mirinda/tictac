package kz.mirinda.tictac.helper;


import android.graphics.Bitmap;

public class ImageSizeHelper {
	public static final String TAG= ImageSizeHelper.class.getSimpleName();
    public static final int MAX_TEXTURE_SIZE = 2048;

    /**
	 * Options of image: <p>
	 * {@link Options#realWidth} - width of image before download <p>
	 * {@link Options#maxTextureSize} - OGL context supported max textureSize<p>
	 * {@link Options#width} - width of image after download, may be smaller then {@link Options#realWidth} if {@link Options#realWidth} larger then {@link Options#maxTextureSize}  <p>
	 * {@link Options#textureSize} - texture size for image (<= {@link Options#maxTextureSize})<p>
	 * @author super-user
	 *
	 */
	public static class Options{
		@Override
		public String toString() {
			return "Options [width=" + width + ", height=" + height
					+ ", realWidth=" + realWidth + ", realHeight=" + realHeight
					+ ", maxTextureSize=" + maxTextureSize + ", textureSize="
					+ textureSize + "]";
		}

		public int width=0;
		public int height=0;
		public int realWidth=0;
		public int realHeight=0;
		public int maxTextureSize=0;
		public int textureSize=0;
		
		public Options(int width, int height, int realWidth, int realHeight,
				int maxTextureSize, int textureSize) {
			super();
			this.width = width;
			this.height = height;
			this.realWidth = realWidth;
			this.realHeight = realHeight;
			this.maxTextureSize = maxTextureSize;
			this.textureSize = textureSize;
		}

		public Options(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
	}
	
	/**
	 * 
	 * @param realWidth - width of image 
	 * @param realHeight - height of image
	 * @param maxTextureSize - OGL context supported max texture Size 
	 * @return evaluated {@link Options}
	 */
	public static ImageSizeHelper.Options getNormalizedOptions(int realWidth, int realHeight,int maxTextureSize){
		//J.i(TAG, "realWidth" + realWidth + "; realheighht: " + realHeight+"; maxtext: "+ maxTextureSize);
		int width = 0;
		int height = 0;
		
		if(Math.max(realWidth,realHeight) > maxTextureSize){
			if(realWidth < realHeight){
				width = (int)((realWidth / (float)realHeight) * maxTextureSize);
				height = maxTextureSize;
			}else{
				width = maxTextureSize;
				height =  (int)(((float)realHeight /realWidth ) * maxTextureSize);
			}
			return new Options(width,height,realWidth,realHeight, maxTextureSize, maxTextureSize);
		}
		
		int textureSize=2;
		while(textureSize <= maxTextureSize){
			if(Math.max(realWidth, realHeight) <= textureSize){
				if(realWidth < realHeight){
					width = (int)((realWidth / (float)realHeight) * textureSize);
					height = textureSize;
				}else{
					width = textureSize;
					height =  (int)(((float)realHeight /realWidth ) * textureSize);
				}
				return new Options(realWidth, realHeight, realWidth, realHeight, maxTextureSize, textureSize);
			}
			
			textureSize*=2;
		}
		
		//J.i(TAG, textureSize+" maxtexture:"+ maxTextureSize);
		return new Options(realWidth, realHeight);
	}
	
	/**
	 * GLTHREAD
	 * @param bmp
	 * @return
	 */
	public static ImageSizeHelper.Options getNormalizedOptions(Bitmap bmp){
		return getNormalizedOptions(bmp.getWidth(), bmp.getHeight(), MAX_TEXTURE_SIZE);
	}
}
