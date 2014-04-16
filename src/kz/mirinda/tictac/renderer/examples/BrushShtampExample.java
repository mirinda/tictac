package kz.mirinda.tictac.renderer.examples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;

import kz.mirinda.tictac.R;

import android.os.Handler;
import android.util.Log;
import android.widget.ScrollView;

import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.helper.BrushHelper;
import kz.mirinda.tictac.listeners.MultiTouchListener;
import kz.mirinda.tictac.renderer.engine.GLTexture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 23.07.13
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class BrushShtampExample implements Example {

/**/
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


    public void setAngle(float degrees){
        mAngle = degrees;
        mShouldUpdate = true;
    }

    private void genBuffers(){
        float[] verticies = new float[mMap.length*QUAD_VERTECIES_COUNT*VERTEX_POINT_DIMENSION]; // grid.size * verticies_count * vertex_dimension
        float[] textures = new float[mMap.length*QUAD_VERTECIES_COUNT*TEXTURE_POINT_DIMENSION];
        int offset=0;
        for(int i=0; i < mMap.length; i++){
            offset = i *  QUAD_VERTECIES_COUNT*VERTEX_POINT_DIMENSION;
            verticies[offset]= (i%mHeight) - mWidth/2.0f ;
            verticies[offset+1]= i/mHeight - mHeight/2.0f ;
            verticies[offset+2]= -1.1f ;

            verticies[offset+3]= (i%mHeight)+1 - mWidth/2.0f ;
            verticies[offset+4]= i/mHeight - mHeight/2.0f ;
            verticies[offset+5]= -1.1f ;

            verticies[offset+6]= (i%mHeight)+1 - mWidth/2.0f ;
            verticies[offset+7]= i/mHeight+1 - mHeight/2.0f ;
            verticies[offset+8]= -1.1f ;

            verticies[offset+9]= (i%mHeight) - mWidth/2.0f ;
            verticies[offset+10]= i/mHeight - mHeight/2.0f ;
            verticies[offset+11]= -1.1f ;

            verticies[offset+12]= (i%mHeight) - mWidth/2.0f ;
            verticies[offset+13]= i/mHeight+1 - mHeight/2.0f ;
            verticies[offset+14]= -1.1f ;

            verticies[offset+15]= (i%mHeight)+1 - mWidth/2.0f ;
            verticies[offset+16]= i/mHeight+1 - mHeight/2.0f ;
            verticies[offset+17]= -1.1f ;

            offset = i*QUAD_VERTECIES_COUNT * TEXTURE_POINT_DIMENSION;

            textures[offset] = 0;
            textures[offset+1] = 0;

            textures[offset+2] = 0;
            textures[offset+3] = 1;

            textures[offset+4] = 1;
            textures[offset+5] = 1;

            textures[offset+6] = 0;
            textures[offset+7] = 0;

            textures[offset+8] = 1;
            textures[offset+9] = 0;

            textures[offset+10] = 1;
            textures[offset+11] = 1;
        }
        for(int i=0;i<6; i++){
            Log.d(TAG,"x="+verticies[3*i]+"; y="+verticies[3*i +1]);
        }

        mVerticies = ByteBuffer.allocateDirect( verticies.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticies.put(verticies).position(0);
        mTextures =  ByteBuffer.allocateDirect( verticies.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextures.put(textures).position(0);
    }

    private void genBuffers1(){
        //mVerticies =
    }

    public void addAngle(){
        mAngle++;
    }

    public BrushShtampExample(Context context){
        mContext = context;
        genBuffers();
    }

    public void setFxaaEnabled(boolean enabled){
        mFxaaEnabled = enabled;
    }

    public void create(){

        Log.i(TAG,"CREATE");
        //DRAW TEXTURE.
        mProgramHandle = GH.createProgram(mContext,R.raw.vertex_shader,R.raw.fragment_shader);

        // FXAA

        mFxaaProgramHandle = GH.createProgram(mContext,R.raw.vertex_shader,R.raw.fxaa);

        mBoundProgram = GH.createProgram(mContext, R.raw.vertex_shader, R.raw.brush_stamp);
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



       mBmp = BrushHelper.drawGaussianBrush(200,1.0f,Color.RED);

        container =  BrushHelper.getBrushParameters(200, 1.0f, Color.RED);
        /*Bitmap.createBitmap(2048,2048, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mBmp);
        canvas.drawColor(Color.BLUE);
        Paint paint = new Paint();
        Paint paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint.setColor(Color.RED);
        canvas.drawCircle(100,100,30,paint);
        canvas.drawLine(0,0,250,100,paint);
        canvas.drawRect(20,150,40,100,paint1);*/
        /*int[] colors = new int[256*256];
        for(int i=0; i<256*256; i++){
            colors[i] = Color.WHITE;
        }
        mBmp = Bitmap.createBitmap(colors,256,256, Bitmap.Config.ARGB_8888);*/
        //GH.bindTexture(mTexture);
        //GH.texImage2D(mBmp,mTexture);
        //mHandler.
        mHandler.obtainMessage(1, mBmp).sendToTarget();
     //   pos.calculateTexturePositions(256,256,256);
     //   pos.calculateVertexPositions(256,256);
        scene =  new GLTexture.Scene(screenWidth,screenHeight);
        scene.setModelMatrixIdentity();
        mSuperTex = new GLTexture(container.textureSize,container.textureSize, mBoundProgram);
       // Log.i(TAG,"Container: texturesize:"+container.textureSize);
        //GH.bindTexture(mSuperTex.getTextureId());
        //GH.texImage2D(mBmp,mSuperTex.getTextureId());

        mFxaaTex = new GLTexture(container.textureSize,container.textureSize, mFxaaProgramHandle);
        mNewTex = new GLTexture(mScreenWidth,mScreenHeight,mProgramHandle);
        mNewTex.setPosition(new GLTexture.GLPosition(container.textureSize,container.textureSize,container.textureSize));//256,256,256));
        mScrollView.setOnTouchListener(new MultiTouchListener(mNewTex.getmScene(), mNewTex.getmScene()));
        int texId = mFxaaTex.getTextureId();
    }

    GLTexture.GLPosition pos = new GLTexture.GLPosition(256,256,256);
    GLTexture.Scene scene ;
    GLTexture resTex;
    BrushHelper.BrushParameterContainer container; 
    

    public void draw(){

        GH.bindFrameBuffer(mFrameBuffer, mSuperTex.getTextureId());
        GH.viewPort(container.textureSize,container.textureSize);
       // GH.clearColor(Color.CYAN);
         mSuperTex.genTextureContent(new GLTexture.UniformSetter() {
            @Override
            public void setUniform(int programHandle) {
               // float[] ratios = new float[14];
               // ratios[0]=1.0f;
                GH.uniformArray(programHandle,"u_Ratios",container.ratios);
                GH.uniformArray(programHandle,"u_Alphas",container.alphas);
                GH.uniformColor(programHandle,"u_Color",container.color);
                GH.uniform1f(programHandle, "u_RealRadius",container.realRadius);
            }
        });
        resTex = mSuperTex;
       GH.bindFrameBuffer(mFrameBuffer, mFxaaTex.getTextureId());
        GH.viewPort(container.textureSize,container.textureSize);

        //GH.clearColor(Color.GREEN);
        if(mFxaaEnabled){
            resTex=mFxaaTex;
        mFxaaTex.genTextureContent(new GLTexture.UniformSetter() {
            @Override
            public void setUniform(int programHandle) {
                GH.activeTexture0(programHandle,mSuperTex.getTextureId(),"u_Texture");
                GH.uniformVec2(programHandle, "u_TexCoordOffset",1.0f/container.textureSize,1.0f/container.textureSize);//(1.0f/(mScreenWidth)), (1.0f/(mScreenHeight)));
            }
        });
        }

        GH.bindSuperBuffer(mSuperBuffer);
        GH.viewPort(mScreenWidth,mScreenHeight);
        GH.clearColor(Color.WHITE);
        //mNewTex.setScene(new GLTexture.Scene());
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
        mNewTex.genTextureContent(new GLTexture.UniformSetter() {
            @Override
            public void setUniform(int programHandle) {
                GH.activeTexture0(programHandle,resTex.getTextureId(),"u_Texture");
            }
        });
        GLES20.glDisable(GLES20.GL_BLEND);



    }

    public static void main(String[] args){
       // BrushShtampExample game = new BrushShtampExample();
        //game.genBuffers();
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }


}
