package kz.mirinda.tictac.renderer.examples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.ScrollView;

import java.nio.FloatBuffer;

import kz.mirinda.tictac.R;
import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.listeners.MultiTouchListener;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Simple example of working
 * Created by asus on 22.11.13.
 */
public class FxaaExample implements Example {

    private static final int TEXTURE_SIZE = 256;
    private Context mContext;

    private int[] mMap = new int[SIZE*SIZE];
    private int mWidth=SIZE;
    private int mHeight=SIZE;


    private FloatBuffer mVerticies;
    //private FloatBuffer mNormals;
    private FloatBuffer mTextures;
    private int mTexture=0;
    //float[] MVPMatrix=new float[16];
    float[] mProjectionMatrix=new float[16];
    float[] mViewMatrix=new float[16];
    float[] mModelMatrix=new float[16];
    float[] mMVPMatrix=new float[16];
    private int mProgramHandle;

    private static final String TAG = BrushShtampExample.class.getCanonicalName();
    private int mScreenWidth;
    private int mScreenHeight;
    private Bitmap mBmp;
    private float[] mMVMMatrix= new float[16];
    private float mAngle=0;
    private boolean mShouldUpdate=true;
    private int mFrameBuffer;
    private Handler mHandler;
    private int mSuperBuffer;
    private int mNewTexture;
    private int mFxaaProgramHandle;
    private GLTexture mFxaaTex;
    private int mBoundProgram;
    private GLTexture mSuperTex;
    private GLTexture mNewTex;
    private ScrollView mScrollView ;
    private boolean mFxaaEnabled;


    public FxaaExample(Context context){
        mContext = context;
    }

    public void setFxaaEnabled(boolean enabled){
        mFxaaEnabled = enabled;
    }

    public void create(){

        Log.i(TAG,"CREATE");
        //DRAW TEXTURE.
        mProgramHandle = GH.createProgram(mContext, R.raw.vertex_shader, R.raw.fragment_shader);

        // FXAA

        mFxaaProgramHandle = GH.createProgram(mContext,R.raw.vertex_shader,R.raw.fxaa);

        mBoundProgram = GH.createProgram(mContext, R.raw.vertex_shader, R.raw.gen_circle);
        mTexture =GH.genTexture();




    }
    public void change(int screenWidth, int screenHeight){
        Log.i(TAG,"CHANGE");
        Log.i(TAG,"screenWidth:"+screenWidth+"screenHeight"+ screenHeight);
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        GH.viewPort(mScreenWidth,mScreenHeight);
        GH.projectionMatrix(mProjectionMatrix,screenWidth/2.0f,screenHeight/2.0f);//mWidth/2.0f,mHeight/2.0f);
        GH.lookAt(mViewMatrix);


        mFrameBuffer =GH.genFrameBuffer();
        mSuperBuffer = GH.getCurrentBuffer();

        mNewTexture = GH.genTexture();
        GH.texImage2D(mNewTexture,2048);

        scene =  new GLTexture.Scene(screenWidth,screenHeight);
        scene.setModelMatrixIdentity();
        mSuperTex = new GLTexture(TEXTURE_SIZE,TEXTURE_SIZE, mBoundProgram);

        mFxaaTex = new GLTexture(TEXTURE_SIZE,TEXTURE_SIZE, mFxaaProgramHandle);
        mNewTex = new GLTexture(mScreenWidth,mScreenHeight,mProgramHandle);
        mNewTex.setPosition(new GLTexture.GLPosition(TEXTURE_SIZE,TEXTURE_SIZE,TEXTURE_SIZE));//256,256,256));
        mScrollView.setOnTouchListener(new MultiTouchListener(mNewTex.getScene(), mNewTex.getScene()));
    }

    GLTexture.Scene scene ;
    GLTexture resTex;


    public void draw(){

        GH.bindFrameBuffer(mFrameBuffer, mSuperTex.getTextureId());
        GH.viewPort(mSuperTex.getScene().getWidth(),mSuperTex.getScene().getHeight());
        // GH.clearColor(Color.CYAN);
        mSuperTex.genTextureContent(new GLTexture.UniformSetter() {
            @Override
            public void setUniform(int programHandle) {
            }
        });
        resTex = mSuperTex;
        GH.bindFrameBuffer(mFrameBuffer, mFxaaTex.getTextureId());
        GH.viewPort(TEXTURE_SIZE,TEXTURE_SIZE);

        //GH.clearColor(Color.GREEN);
        if(mFxaaEnabled){
            resTex=mFxaaTex;
            mFxaaTex.genTextureContent(new GLTexture.UniformSetter() {
                @Override
                public void setUniform(int programHandle) {
                    GH.activeTexture0(programHandle,mSuperTex.getTextureId(),"u_Texture");
                    GH.uniformVec2(programHandle, "u_TexCoordOffset",1.0f/TEXTURE_SIZE,1.0f/TEXTURE_SIZE);//(1.0f/(mScreenWidth)), (1.0f/(mScreenHeight)));
                }
            });
        }

        GH.bindSuperBuffer(mSuperBuffer);
        GH.viewPort(mScreenWidth,mScreenHeight);
        GH.clearColor(Color.RED);
        mNewTex.genTextureContent(new GLTexture.UniformSetter() {
            @Override
            public void setUniform(int programHandle) {
                GH.activeTexture0(programHandle,resTex.getTextureId(),"u_Texture");
            }
        });


    }


    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }
}
