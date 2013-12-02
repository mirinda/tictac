package kz.mirinda.tictac.renderer;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import kz.mirinda.tictac.renderer.examples.BrushShtampExample;
import kz.mirinda.tictac.renderer.examples.Example;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 22.07.13
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    public int color = Color.RED;
    private static final String TAG = GLRenderer.class.getCanonicalName();
    Example mExample;// = new BrushShtampExample()

    public GLRenderer(Context context, Example example){
        mExample = example;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mExample.create();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        mExample.change(i, i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //Log.d(TAG,"draw");
        //GH.clearColor(color++);
        mExample.draw();
    }
    public Example getExample(){
            return mExample;
    }

    public void setHandler(Handler handle){
        mExample.setHandler(handle);
    }
}
