package kz.mirinda.tictac.renderer.examples;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ScrollView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import kz.mirinda.tictac.R;
import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.listeners.MultiTouchListener;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Created by asus on 06.03.14.
 */
public class ParticlesExample implements Example {
    private static final int POINTS_NUM = 300000;
    private static final long THREE_SEC = 3 * 1000;
    private final Context mContext;
    private ScrollView mScrollView;
    private Handler mHandler;
    private GLTexture.Scene mScreenScene;
    private int mParticleProgramHandle;
    private float[] mPoints;
    private FloatBuffer mBuffer;
    private int mVertexAttribLocation;
    private long mStartTime;

    public  ParticlesExample(Context context){
        mContext= context;
    }

    @Override
    public void create() {
        mParticleProgramHandle = GH.createProgram(mContext, R.raw.vertex_point_size_shader,R.raw.particle);
        mPoints = new float[POINTS_NUM];
        for (int i = 0; i < POINTS_NUM/3; i++) {
            mPoints[3*i]=(float)(100*Math.sin(i));
            mPoints[3*i+1]=(float)(100*Math.cos(i));
            mPoints[3*i+2]=1.0f;
        }
    }

    @Override
    public void change(int screenWidth, int screenHeight) {
        mScreenScene = new GLTexture.Scene(screenWidth,screenHeight);
        mScrollView.setOnTouchListener(new MultiTouchListener(mScreenScene,mScreenScene));

        mBuffer = ByteBuffer.allocateDirect(4*POINTS_NUM).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mBuffer.put(mPoints,0,mPoints.length);
        mBuffer.position(0);
        GH.useProgram(mParticleProgramHandle);
        GH.viewPort(mScreenScene);
        GH.uniform1f(mParticleProgramHandle,"u_PointSize",20);
        mVertexAttribLocation = GLES20.glGetAttribLocation(mParticleProgramHandle,"a_Position");
        mStartTime = SystemClock.elapsedRealtime();
        //GLES20.glPoints
    }

    @Override
    public void draw() {
        long currentTime = (SystemClock.elapsedRealtime() - mStartTime) % (THREE_SEC);
        GH.clearColor(Color.BLACK);
        // условно говоря их все нужно отрисовать
        // то есть, есть куча треугольников которые либо особого цвета, либо как то привязаны к текстуре
        // куча треугольников это массив точек, он динамически меняется с течением времени
        // раз я хочу за один раз их отрисовывать все они должны быть в координатах сцены

       for (int i = 0; i < POINTS_NUM/3; i++) {
            mPoints[3*i]=(float)(( currentTime) *Math.sin(i)+ 200*Math.sin(currentTime/100.0f));
            mPoints[3*i+1]=(float)((currentTime)*Math.cos(i) + 200*Math.cos(currentTime/100.0f));
            mPoints[3*i+2]=1.0f;
        }
        mScreenScene.setModelMatrixIdentity();
        GH.uniformMatrix(mParticleProgramHandle, mScreenScene);
        GH.uniform1f(mParticleProgramHandle,"u_PointSize",20);
        mBuffer.clear();
        //mBuffer = ByteBuffer.allocate(4*POINTS_NUM).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mBuffer.put(mPoints,0,mPoints.length);
        mBuffer.position(0);
        //GH.positionVertexAttrib();
       // mBuffer.position(0);
        GLES20.glVertexAttribPointer(mVertexAttribLocation,3,GLES20.GL_FLOAT,false,0,mBuffer);
        GLES20.glEnableVertexAttribArray(mVertexAttribLocation);
        GLES20.glDrawArrays(GLES20.GL_POINTS,0,POINTS_NUM/3);
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }
}
