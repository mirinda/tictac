package kz.mirinda.tictac.renderer.examples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Handler;
import android.widget.ScrollView;

import java.util.concurrent.SynchronousQueue;

import javax.microedition.khronos.opengles.GL;

import kz.mirinda.tictac.R;
import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.helper.BrushHelper;
import kz.mirinda.tictac.listeners.MultiTouchListener;
import kz.mirinda.tictac.listeners.OneTouchQueueHandler;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Created by asus on 30.11.13.
 */
public class BrushExample implements Example {
    private Context mContext;
    private int mFrameBuffer;
    private int mSuperBuffer;
    private GLTexture.Scene mScreenScene;
    private GLTexture mBrushStampTexture;
    int mDrawProgram;
    private ScrollView mScrollView;
    private Handler mHandler;
    private MultiTouchListener mMultiTouchListener;
    private SynchronousQueue<OneTouchQueueHandler.Line> mQueue;
    private GLTexture mBrushLayerTexture;
    private int mNewColor= Color.CYAN;
    private boolean mUpdateColor;

    public BrushExample(Context context){
        mContext = context;
    }

    @Override
    public void create() {


        mFrameBuffer =    GH.genFrameBuffer();
        mSuperBuffer = GH.getCurrentBuffer();
        mDrawProgram=GH.createProgram(mContext,R.raw.vertex_shader,R.raw.fragment_shader);

        final BrushHelper.BrushParameterContainer container = BrushHelper.getBrushParameters(300,0.1f, mNewColor);
        mBrushStampTexture = new GLTexture(container.textureSize,container.textureSize, GH.createProgram(mContext, R.raw.vertex_shader, R.raw.brush_stamp));
        genBrushStampContent(container);

        GLES20.glEnable(GLES20.GL_BLEND);

    }

    private void genBrushStampContent(final BrushHelper.BrushParameterContainer container) {

        //Gen Brush Stamp
        GH.bindFrameBuffer(mFrameBuffer,mBrushStampTexture.getTextureId());
        GH.viewPort(mBrushStampTexture.getScene());
        GH.clearColor(0);
        mBrushStampTexture.genTextureContent(new GLTexture.UniformSetter() {
            @Override
            public void setUniform(int programHandle) {
                GH.uniformArray(programHandle,"u_Ratios",container.ratios);
                GH.uniformArray(programHandle,"u_Alphas",container.alphas);
                GH.uniformColor(programHandle, "u_Color", container.color);
                GH.uniform1f(programHandle,"u_RealRadius",container.realRadius);
            }
        });
        GH.bindSuperBuffer(mSuperBuffer);
    }

    @Override
    public void change(int screenWidth, int screenHeight) {

        mBrushLayerTexture = new GLTexture(screenWidth,screenHeight,mDrawProgram);

        GH.bindFrameBuffer(mFrameBuffer,mBrushLayerTexture.getTextureId());
        GH.viewPort(mBrushLayerTexture.getScene());
        GH.clearColor(Color.RED);
        GH.bindSuperBuffer(mSuperBuffer);

        mScreenScene = new GLTexture.Scene(screenWidth,screenHeight);
        mScrollView.setOnTouchListener(mMultiTouchListener =new MultiTouchListener(mScreenScene,mScreenScene));
        mMultiTouchListener.setOnTouchHandler(new OneTouchQueueHandler(mQueue = new SynchronousQueue<OneTouchQueueHandler.Line>()));
        GH.viewPort(screenWidth,screenHeight);


    }
    //boolean mFlag =
    @Override
    public void draw() {
        if(mUpdateColor){
            mUpdateColor=false;
            final BrushHelper.BrushParameterContainer container = BrushHelper.getBrushParameters(300,0.1f, mNewColor);
            genBrushStampContent(container);
            GH.viewPort(mScreenScene);
        }

        GH.clearColor(0);
        final  OneTouchQueueHandler.Line l;
        if((l=mQueue.poll())!=null){
            GH.bindFrameBuffer(mFrameBuffer,mBrushLayerTexture.getTextureId());
            GH.viewPort(mBrushLayerTexture.getScene());


            //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,GLES20.GL_DST_ALPHA,GLES20.GL_SRC_ALPHA,GLES20.GL_SRC_ALPHA);

            GH.useProgram(mDrawProgram);
            mBrushLayerTexture.getScene().setModelMatrixIdentity();
            mBrushLayerTexture.getScene().translateModelMatrix(l.x1 - mScreenScene.getWidth() / 2.0f, -l.y1 + mScreenScene.getHeight() / 2.0f);
            GH.activeTexture0(mDrawProgram, mBrushStampTexture.getTextureId(), "u_Texture");
            GH.uniformMatrix(mDrawProgram, mBrushLayerTexture.getScene());
            GH.VertexAttrib(mDrawProgram,mBrushStampTexture.getPosition());
            GH.draw();

           // GH.clearColor(0);
            GH.bindSuperBuffer(mSuperBuffer);
            GH.viewPort(mScreenScene);
        }
        mScreenScene.setModelMatrixIdentity();
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_DST_ALPHA,GLES20.GL_ONE,GLES20.GL_ONE);
        GH.useProgram(mDrawProgram);
        GH.activeTexture0(mDrawProgram, mBrushLayerTexture.getTextureId(), "u_Texture");
        GH.VertexAttrib(mDrawProgram,mBrushLayerTexture.getPosition());
        GH.uniformMatrix(mDrawProgram,mScreenScene);
        GH.draw();
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }

    public void setColor(int color) {
        mUpdateColor = true;
        mNewColor = color;
    }
}
