package kz.mirinda.tictac.renderer.examples;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Handler;
import android.util.Log;
import android.widget.ScrollView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.SynchronousQueue;

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
    public static final String TAG = BrushExample.class.getSimpleName();
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
    private int mNewColor= Color.argb(255,255,34,12);
    private boolean mUpdateColor;
    private int mVertexBuffer;
    private int mTexturePointsBuffer;

    public BrushExample(Context context){
        mContext = context;
    }

    @Override
    public void create() {


        mFrameBuffer =    GH.genFrameBuffer();
        mSuperBuffer = GH.getCurrentBuffer();
        mDrawProgram=GH.createProgram(mContext,R.raw.vertex_shader,R.raw.fragment_shader);

        final BrushHelper.BrushParameterContainer container = BrushHelper.getBrushParameters(300,0.0001f, mNewColor);
        /*for(int i = 0;i < container.ratios.length; i++){
            Log.i(TAG,container.ratios[i]+":" +container.alphas[i]);
        }*/

        mBrushStampTexture = new GLTexture(container.textureSize,container.textureSize, GH.createProgram(mContext, R.raw.vertex_shader, R.raw.brush_stamp));
        genBrushStampContent(container);

        ShtampVector vec = new ShtampVector(container.textureSize/2,10,0.3f);

        int[] buffer = new int[2];
        GLES20.glGenBuffers(2,buffer,0);
        mVertexBuffer = buffer[0];
        mTexturePointsBuffer = buffer[1];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,mVertexBuffer);
        //GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,4*3*6,mBrushStampTexture.getPosition().getVertecies(),GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vec.getVerticiesCount()*4,vec.getVerticiesBuffer(),GLES20.GL_DYNAMIC_DRAW);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,mTexturePointsBuffer);
        //GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,4*2*6,mBrushStampTexture.getPosition().getTextures(),GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vec.getTextureCount()*4,vec.getTexturesBuffer(),GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        GLES20.glEnable(GLES20.GL_BLEND);
/**/
    }

    private void genBrushStampContent(final BrushHelper.BrushParameterContainer container) {

        //Gen Brush Stamp
        GH.bindFrameBuffer(mFrameBuffer,mBrushStampTexture.getTextureId());
        GH.viewPort(mBrushStampTexture.getmScene());
        GH.clearColor(Color.BLACK);
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
        GH.viewPort(mBrushLayerTexture.getmScene());
        GH.clearColor(0);
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
            GLES20.glDisable(GLES20.GL_BLEND);
            mUpdateColor=false;
            final BrushHelper.BrushParameterContainer container = BrushHelper.getBrushParameters(300,0.001f, mNewColor);
            genBrushStampContent(container);
            GH.viewPort(mScreenScene);
            GLES20.glEnable(GLES20.GL_BLEND);
        }

        GH.clearColor(Color.GREEN);
        final  OneTouchQueueHandler.Line l;
        if((l=mQueue.poll())!=null){
            GH.bindFrameBuffer(mFrameBuffer,mBrushLayerTexture.getTextureId());
            GH.viewPort(mBrushLayerTexture.getmScene());


            //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA,GLES20.GL_ONE,GLES20.GL_ONE);

            GH.useProgram(mDrawProgram);
            mBrushLayerTexture.getmScene().setModelMatrixIdentity();
            mBrushLayerTexture.getmScene().translateModelMatrix(l.x1 - mScreenScene.getWidth() / 2.0f, -l.y1 + mScreenScene.getHeight() / 2.0f);
            GH.activeTexture0(mDrawProgram, mBrushStampTexture.getTextureId(), "u_Texture");
            GH.uniformMatrix(mDrawProgram, mBrushLayerTexture.getmScene());
           // GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,mVertexBuffer);

            GH.VertexAttrib(mDrawProgram,mVertexBuffer, mTexturePointsBuffer);
           // GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

            //GH.draw();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,60);
            int err;
            while((err=GLES20.glGetError())>0){
                Log.e(TAG,"gl Error with code:" + err);
            }
           // GH.clearColor(0);
            GH.bindSuperBuffer(mSuperBuffer);
            GH.viewPort(mScreenScene);
        }
        mScreenScene.setModelMatrixIdentity();
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA,GLES20.GL_ONE,GLES20.GL_ONE);
       // GLES20.glBlendEquationSeparate(GLES20.GL_FUNC_ADD,GLES20.GL_MAX );
       // GLES20.glDisable(GLES20.GL_BLEND);
        GH.useProgram(mDrawProgram);
        GH.activeTexture0(mDrawProgram, mBrushLayerTexture.getTextureId(), "u_Texture");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        GH.VertexAttrib(mDrawProgram,mBrushLayerTexture.getPosition());
        GH.uniformMatrix(mDrawProgram,mScreenScene);
        GH.draw();
      //  GLES20.glEnable(GLES20.GL_BLEND);
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

    public static class ShtampVector{

        private final FloatBuffer mTextureFloatBuffer;
        FloatBuffer mVertexFloatBuffer;

        public ShtampVector(int shtampRadius, int shtampCount, float shtampKoef){
            float[] vertecies = new float[shtampCount  * 3 * 6 ];
            float[] textures = new float[shtampCount   * 2 * 6 ];

            float center = 0;
            float r = shtampRadius;
            for(int i = 0; i < shtampCount; i++){
             //   Log.d(TAG,"i:"+i+"; count="+shtampCount+"; r:"+ r+ "; center:"+center);
                //   |  _ \
                vertecies[18*i]   = - r;
                vertecies[18*i+1] = r +center;
                vertecies[18*i+2] = 0;

                vertecies[18*i+3] = - r;
                vertecies[18*i+4] = - r + center;
                vertecies[18*i+5] =  0;

                vertecies[18*i+6] =  r;
                vertecies[18*i+7] =  -r+center;
                vertecies[18*i+8] =  0;

                // | _ \
                vertecies[18*i+9]  =  r;
                vertecies[18*i+10] =  -r+center;
                vertecies[18*i+11] =  0;

                vertecies[18*i+12] =  r;
                vertecies[18*i+13] =  r+center;
                vertecies[18*i+14] =  0;

                vertecies[18*i+15] =  -r;
                vertecies[18*i+16] =  r+center;
                vertecies[18*i+17] =  0;

                //
                textures[12*i]   = 0;
                textures[12*i+1] = 1;

                textures[12*i+2] = 0;
                textures[12*i+3] = 0;

                textures[12*i+4] = 1;
                textures[12*i+5] = 0;

                //
                textures[12*i+6]  = 1;
                textures[12*i+7]  = 0;

                textures[12*i+8]  = 1;
                textures[12*i+9]  = 1;

                textures[12*i+10] = 0;
                textures[12*i+11] = 1;

                center += 2*r*shtampKoef;
            }
            for(float vert: vertecies){
                Log.d(TAG,vert+"");
            }

            mVertexFloatBuffer = ByteBuffer.allocateDirect(vertecies.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            mVertexFloatBuffer.put(vertecies);
            mVertexFloatBuffer.position(0);

            mTextureFloatBuffer = ByteBuffer.allocateDirect(textures.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTextureFloatBuffer.put(textures);
            mTextureFloatBuffer.position(0);
        }

        public FloatBuffer getVerticiesBuffer(){
            return  mVertexFloatBuffer;
        }

        public FloatBuffer getTexturesBuffer(){
            return  mTextureFloatBuffer;
        }

        public  int getVerticiesCount(){
            return mVertexFloatBuffer.capacity();
        }

        public  int getTextureCount(){
            return mTextureFloatBuffer.capacity();
        }
    }
}
