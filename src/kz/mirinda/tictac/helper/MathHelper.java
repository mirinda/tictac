package kz.mirinda.tictac.helper;

/**
 * Created by asus on 26.10.13.
 */
public class MathHelper {
    public static float length(float[] floats, float[] floats1) {
        return (float) Math.sqrt(Math.pow(floats[0] - floats1[0],2)+ Math.pow(floats[1] - floats1[1],2));
    }

    public static boolean near(float x1, float y1, float prevX1, float prevY1, float v) {
        return (float) Math.sqrt(Math.pow(x1 - prevX1,2)+ Math.pow(y1 - prevY1,2)) < v;
    }
}
