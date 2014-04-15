package kz.mirinda.tictac.renderer.examples;

import android.content.Context;
import android.os.Handler;
import android.widget.ScrollView;

import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Created by asus on 12.04.14.
 */
public class TicTacExample implements Example{
    public static final int N = 30;
    public static final int M = 41;
    private final Context mContext ;
    private GLTexture.Scene mScreenScene;
    private int mFrameBuffer  ;
    private int mSuperBuffer;

    public TicTacExample(Context context){
        mContext = context;

    }

    @Override
    public void create() {
        mFrameBuffer = GH.genFrameBuffer();
        mSuperBuffer = GH.getCurrentBuffer();
      //  mFieldTexture = new GLTexture();
    }

    @Override
    public void change(int screenWidth, int screenHeight) {
        mScreenScene = new GLTexture.Scene(screenWidth,screenHeight);
    }

    @Override
    public void draw() {

    }

    @Override
    public void setHandler(Handler handler) {

    }

    @Override
    public void setScrollView(ScrollView scrollView) {

    }
}
