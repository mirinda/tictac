package kz.mirinda.tictac.helper;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class BrushHelper {
	static final int[] BRUSH_CENTER_SIZES=new int[]{5, 42, 64, 89, 96};
    static final int[] BRUSH_CURVE_SIZES=new int[]{166, 109, 69, 40, 8};
    static final double[] SOFTNESS_SPLINE_X = new double[]{0,14,35,108,179,255,280};
    static final double[] SOFTNESS_SPLINE_Y = new double[]{0,4,25,170,242,255,280};
	private static final String TAG = BrushHelper.class.getSimpleName();

    public static float getBrushCurveSize(float hardness){   	
    	return mapArray(BRUSH_CURVE_SIZES,hardness);
    }
    private static float mapArray(int[] array, float value) {
    	float mappedVal = value *(array.length);
    	if(mappedVal >= array.length-1){
    		return array[array.length -1];
    	}
    	int left = (int)Math.floor(mappedVal);
    	float delta = mappedVal - left;
    	int right = left+1;
		return array[left] * (1-delta) + array[right] * delta;
	}
	public static float getBrushCenterSize(float hardness){
    	return mapArray(BRUSH_CENTER_SIZES, hardness);
    }
	
	public static GausianBrushDimension getGaussianBrushDimensions(float diameter,float hardness){
		float realDiameter=0;
		float solidRadius = 0;
		float blurRadius = 0;
		
		if(hardness >=  1.0f){
			realDiameter = diameter;
            solidRadius = diameter/2;
		}else{
			solidRadius = getBrushCenterSize(hardness)*diameter/200;
			blurRadius = getBrushCurveSize(hardness)*diameter / 200;
			realDiameter = (solidRadius + blurRadius)*2;
		}
		Log.e(TAG, "realDiam:"+ realDiameter+ ";solid:"+solidRadius+"; blur:"+ blurRadius);
		return new GausianBrushDimension(solidRadius, blurRadius, realDiameter);
	}

    public static BrushParameterContainer getBrushParameters(int diameter, float hardness, int color) {
        GausianBrushDimension dimensions = getGaussianBrushDimensions(diameter, hardness);
        Log.e("aikos", dimensions.toString());
        float solidRatio = dimensions.solidRadius/(dimensions.solidRadius + dimensions.blurRadius);
        float blurRatio = dimensions.blurRadius/(dimensions.solidRadius + dimensions.blurRadius);
        double[] softnessSplineX = new double[SOFTNESS_SPLINE_X.length];
        int i = 0;
        for(double x : SOFTNESS_SPLINE_X){
            softnessSplineX[i++] = solidRatio + x * blurRatio / 255.0f;
            Log.i(TAG,i+":"+softnessSplineX[i-1]);
        }

        Spline cubicSpline = new Spline(softnessSplineX, SOFTNESS_SPLINE_Y);
        float[] gradientSteps= new float[]{0, 5, 10, 25, 40, 50, 60, 100, 120, 135, 150, 175, 220, 255};
        for (i=0; i < 14; i++){
            gradientSteps[i] = solidRatio + gradientSteps[i] * blurRatio / 255;
        }

        int[] colors = new int[gradientSteps.length];
        float[] alphas = new float[gradientSteps.length];
        float[] ratios = new float[gradientSteps.length];
        float alpha =0.0f;
        i=0;
        for(float step: gradientSteps){
            Log.i(TAG,i+":"+cubicSpline.spline_value(step)+"; step:"+ step);
            alpha=1-(float)cubicSpline.spline_value(step)/255;
            colors[i]=color;
            alphas[i]=alpha<0?0.0f:alpha;
            ratios[i++]=step *(dimensions.realDiameter/2.0f);
        }
        int realDiameter = (int)dimensions.realDiameter;

        int width = ImageSizeHelper.getNormalizedOptions(realDiameter, realDiameter, ImageSizeHelper.MAX_TEXTURE_SIZE).textureSize;
        BrushParameterContainer container = new BrushParameterContainer();
        container.textureSize = width;
        container.ratios= ratios;
        container.alphas=alphas;
        container.color = color;
        container.realRadius=realDiameter / 2.0f;
        printArray(ratios, "ratios");
        printArray(alphas,"alphas");
        Log.i(TAG,"realRadius:"+container.realRadius+"");
        Log.i(TAG,"color:"+color);
        Log.i(TAG,"textureSize:"+width);
        return container;
    }

    public static class BrushParameterContainer{
        public int textureSize;
        public float[] ratios;
        public float[] alphas;
        public int color;
        public float realRadius;
    }

    public static class GausianBrushDimension{
		public float solidRadius;
		public float blurRadius;
		public float realDiameter;
		public GausianBrushDimension(float solidRadius, float blurRadius,
				float realDiameter) {
			super();
			this.solidRadius = solidRadius;
			this.blurRadius = blurRadius;
			this.realDiameter = realDiameter;
		}
		@Override
		public String toString() {
			return "solid:"+ solidRadius+";blur:" + blurRadius + ";realdiameter:" + realDiameter;
		}
		
	}
	public static Bitmap drawGaussianBrush(float diameter, float hardness, int color){
		//TODO optimize and refactor
		GausianBrushDimension dimensions = getGaussianBrushDimensions(diameter, hardness);
		Log.e("aikos", dimensions.toString());
		double solidRatio = dimensions.solidRadius/(dimensions.solidRadius + dimensions.blurRadius);
		double blurRatio = dimensions.blurRadius/(dimensions.solidRadius + dimensions.blurRadius);
		double[] softnessSplineX = new double[SOFTNESS_SPLINE_X.length];
		int i = 0;
		for(double x : SOFTNESS_SPLINE_X){
			softnessSplineX[i++] = solidRatio + x * blurRatio / 255.0;
		}
		
		Spline cubicSpline = new Spline(softnessSplineX, SOFTNESS_SPLINE_Y);
		double[] gradientSteps= new double[]{0, 5, 10, 25, 40, 50, 60, 100, 120, 135, 150, 175, 220, 255};
		for (i=0; i < 14; i++){
			gradientSteps[i] = solidRatio + gradientSteps[i] * blurRatio / 255;
		}
		//printArray(gradientSteps, "gradient Steps :");
		int[] colors = new int[gradientSteps.length];
		double[] alphas = new double[gradientSteps.length];
		double[] ratios = new double[gradientSteps.length];
		double alpha =0.0; 
		i=0;
		for(double step: gradientSteps){
			alpha=1-cubicSpline.spline_value(step)/255;
			colors[i]=color;
			alphas[i]=alpha;
			ratios[i++]=step *(dimensions.realDiameter/2.0);
		}
		int realDiameter = (int)dimensions.realDiameter;
		
		int width = ImageSizeHelper.getNormalizedOptions(realDiameter, realDiameter, ImageSizeHelper.MAX_TEXTURE_SIZE).width;
		
		int imageSize= width*width;
		int[] bitmapColors = new int[imageSize];
		//printArray(ratios,"ratios:");
		//printArray(alphas, "alphas:");
		i=0;
		for(i=0; i<width; i++){
			for(int j=0; j < width; j++){
				double length= MathHelper.length(new float[]{i,j},new float[]{width/2.0f,width/2.0f});
				if (ratios[0] > length){
					bitmapColors[i*width+j]= Color.argb(255,255,0,0);
					continue;
				}
                boolean flag=false;
				for(int k=1; k< ratios.length; k++){
					if(ratios[k] > length){
						double koef=(length-ratios[k-1])/(ratios[k]-ratios[k-1]);
						bitmapColors[i*width+j]=Color.argb((int)((alphas[k-1]*(1-koef) + alphas[k]*(koef))*255),255,  0, 0);//red with alpha
                        flag=true;
						break;
					}
				}
                if(!flag){
                    bitmapColors[i*width+j]=Color.argb(0,255,  0, 0);
                }
			}
		}
		
		Bitmap brushStamp = Bitmap.createBitmap(bitmapColors,width,width, Config.ARGB_8888);

 		return brushStamp;
		
	}
	private static void printArray(float[] ratios, String string) {
		Log.e(TAG, string);
		int i=0;
		for(double ratio: ratios){
			Log.e(TAG, "["+ i++ +"]" + ratio);
		}
		
	}
}
