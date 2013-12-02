package kz.mirinda.tictac.renderer.examples;

import android.os.Handler;
import android.widget.ScrollView;

/**
 * Created by asus on 22.11.13.
 */
public interface Example {
    public static final int SIZE=2;
    public static final int QUAD_VERTECIES_COUNT=6;
    public static final int VERTEX_POINT_DIMENSION=3;
    public static final int TEXTURE_POINT_DIMENSION=2;
    public static final int BYTES_PER_FLOAT=4;

    public void create();
    public void change(int screenWidth, int screenHeight);
    public void draw();
    public void setHandler(Handler handler);
    public void setScrollView(ScrollView scrollView);

    }
