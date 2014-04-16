package kz.mirinda.tictac.renderer.engine;

import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.security.AllPermission;

import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.listeners.Mover;
import kz.mirinda.tictac.listeners.Scaler;
import kz.mirinda.tictac.renderer.examples.BrushShtampExample;

/**
 * Created by asus on 05.10.13.
 */
public class GLTexture {
    public static final String TAG = GLTexture.class.getSimpleName();

    private int mTextureId;
    private int mTextureSize;
    private int mContentProgramHandle;
    private GLPosition mPos;
    private Scene mScene;
    public GLPosition getPosition(){
        return mPos;
    }
    public void setPosition(GLPosition position){
        mPos = position;
    }

    public GLTexture(int width, int height, int textureContentProgram){
        mContentProgramHandle = textureContentProgram;
        mTextureId = GH.genTexture();
        mTextureSize = getTextureSize(width,height);
        GH.texImage2D(mTextureId, mTextureSize);
        mPos = new GLPosition(width,height,mTextureSize);
        mScene = new Scene(width, height);
        mScene.setModelMatrixIdentity();
    }

    public void changeSize(int newWidth, int newHeight){
        mTextureSize = getTextureSize(newWidth, newHeight);
        GH.texImage2D(mTextureId,mTextureSize);
        mPos = new GLPosition(newWidth,newHeight,mTextureSize);
        mScene = new Scene(newWidth, newHeight);
        mScene.setModelMatrixIdentity();
    }

    public void genTextureContent(UniformSetter unif){
        GH.useProgram(mContentProgramHandle);
        mScene.setModelMatrixIdentity();
        unif.setUniform(mContentProgramHandle);
        GH.VertexAttrib(mContentProgramHandle, mPos);
        GH.uniformMatrix(mContentProgramHandle, mScene);
        GH.draw();
    }

    public Scene getScene() {
        return mScene;
    }

    public static interface UniformSetter{
        public void setUniform(int programHandle);
    }


    public int getTextureId() {
        return mTextureId;
    }

    public static class GLPosition{
        private FloatBuffer mTexPositions;
        private FloatBuffer mVertexPosition;
        public GLPosition(int width, int height, int textureSize){
            calculateVertexPositions(width,height);
            calculateTexturePositions(width,height,textureSize);
        }

        public void calculateTexturePositions(int imageWidth, int imageHeight, int textureSize){
            float coefW =((float)imageWidth)/textureSize;
            float coefH =((float)imageHeight)/textureSize;
            float[] buffer = new float[]{
                    coefW, coefH,
                    0,0,
                    0,coefH,
                    coefW,coefH,
                    0,0,
                    coefW,0
                    };
            mTexPositions = ByteBuffer.allocateDirect(buffer.length * BrushShtampExample.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
            //TODO do not how this
            mTexPositions.put(buffer);
            mTexPositions.position(0);
        }

        public void calculateVertexPositions(int width, int height){
            float[] buffer = new float[]{
                    width/2.0f, height/2.0f, 0.0f,
                    -width/2.0f, -height/2.0f, 0.0f,
                    -width/2.0f, height/2.0f, 0.0f,
                    width/2.0f, height/2.0f, 0.0f,
                    -width/2.0f, -height/2.0f, 0.0f,
                    width/2.0f, -height/2.0f, 0.0f
            };

            mVertexPosition = ByteBuffer.allocateDirect(buffer.length * BrushShtampExample.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
            mVertexPosition.put(buffer);
            mVertexPosition.position(0);
        }


        public FloatBuffer getVertecies() {
            return mVertexPosition;
        }

        public FloatBuffer getTextures() {
            return mTexPositions;
        }
    }
    public static class Scene implements Mover,Scaler{
        public static final String TAG = Scene.class.getSimpleName();

        public static  final String PROJECTION_MATRIX = "u_MVPMatrix";
        public static  final String VIEW_MATRIX = "u_MVMatrix";
        private  float[] mMVMMatrix = new float[16];
        private  float[] mMVPMatrix = new float[16];


        private int mWidth;
        private int mHeight;
        private  float[] mModelMatrix = new float[16];
        private float[] mLookAtMatrix = new float[16];
        private float[] mProjectionMatrix = new float[16];

        private float mCurrentX = 0;
        private float mCurrentY = 0;
        private float mScale=1.0f;

        public Scene(int width, int height){
            mWidth  = width;
            mHeight = height;
            calculateLookAt();
            calculateProjection();
            setModelMatrixIdentity();
        }

        public void setModelMatrixIdentity(){
            Matrix.setIdentityM(mModelMatrix,0);
        }

        public void calculateMVPMatrix(){
            Matrix.multiplyMM(mMVMMatrix,0,mLookAtMatrix,0,mModelMatrix,0);
            Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mMVMMatrix,0);
        }

        public void calculateLookAt(){
            GH.lookAt(mLookAtMatrix);
        }

        public void calculateProjection(){
            Matrix.orthoM(mProjectionMatrix,0,-mWidth/2.0f,mWidth/2.0f,-mHeight/2.0f,mHeight/2.0f,0.5f,-3.f);
        }

        public void rotate(int degrees){
            Matrix.rotateM(mModelMatrix,0,degrees,0,0,1);
        }

        public float[] getProjectionMatrix(){
            return mProjectionMatrix;
        }

        public float[] getLookAtMatrix(){
            return mLookAtMatrix;
        }

        public float[] getMVPMatrix(){
            return mMVPMatrix;
        }

        public float[] getMVMMatrix(){
            return mMVMMatrix;
        }

        public int getWidth(){
            return mWidth;
        }
        public int getHeight(){
            return mHeight;
        }


        public void scaleModelMatrix(float scaleX, float scaleY) {
            Matrix.scaleM(mModelMatrix,0,scaleX,scaleY,1.0f);
        }

        public void translateModelMatrix(float x, float y){
            Matrix.translateM(mModelMatrix,0,x,y,0);
        }


        @Override
        public void move(float x, float y) {

            Log.d(TAG,"x:"+x+"; y="+ y);
            mCurrentX-=x*mScale;
            mCurrentY-=y*mScale;
            GH.lookAt(mLookAtMatrix,mCurrentX,mCurrentY);
        }

        @Override
        public void scale(float scale) {
            mScale /=scale;
            GH.projectionMatrix(mProjectionMatrix,mScale*mWidth/2, mScale*mHeight/2);
        }
    }


    public static int getTextureSize(int width,int height){
        int newWidth = width;
        int newHeight = height;
        int i=-1;

        while((newWidth  ) >0 || (newHeight  ) >0 ){
            newWidth = (newWidth >>1);
            newHeight = (newHeight >>1);
            i++;
        }
        int upPowerTwo = (int)Math.pow(2,i);
        int result = upPowerTwo< Math.max(width,height)? (int)Math.pow(2,i+1):upPowerTwo;
       // Log.d(TAG,"("+width + "," + height + ") --"+ result );
        return result;
    }
}
