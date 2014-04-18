package kz.mirinda.tictac.renderer.examples.tictac;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import kz.mirinda.tictac.R;
import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Created by asus on 16.04.14.
 */
public class TictacBoard {
    private static final int BOARD_PIXEL_SIZE=2048;
    private final int mProgramHandle;
    private final CellDrawer mDrawer;
    private final FloatBuffer mPositionBuffer;
    private final int mPixelWidth;
    private final int mPixelHeight;

    public static enum CELL{
        NONE,X,O
    }
    private final int mWidth;
    private final int mHeight;
    private final GLTexture mFieldTexture;
    private final CELL[] mBoard;

    /**
     * GL THREAD ONLY
     * @param width     board width in cells
     * @param height    board height in cells
     */
    public TictacBoard(int width,int height, Context context,CellDrawer drawer){
        mWidth = width>0?width:1;
        mHeight = height>0?height:1;
        mPixelWidth =(int)(BOARD_PIXEL_SIZE *( width > height? 1.0f : width *1.0f / height));
        mPixelHeight =(int)(BOARD_PIXEL_SIZE *( width > height? height *1.0f/width : 1.0f));
        mProgramHandle = GH.createProgram(context, R.raw.vertex_shader,R.raw.fragment_shader);
        mFieldTexture = new GLTexture(mPixelWidth,mPixelHeight, mProgramHandle);
        mPositionBuffer = GLTexture.GLPosition.calculateVertexBuffer(mPixelWidth/mWidth,mPixelHeight/mHeight);
        mDrawer = drawer;
        mBoard = new CELL[mWidth * mHeight];
        for(int i=0; i < mBoard.length; i++){
            mBoard[i]=CELL.NONE;
        }
    }

    public void drawAll() {
        GH.useProgram(mProgramHandle);
        GLTexture.Scene scene =mFieldTexture.getScene();
        int cellWidth = mPixelWidth/mWidth;
        int cellHeight = mPixelHeight/mHeight;
        for(int i=0; i < mWidth; i++){
            for(int j=0; j < mHeight; j++){
                scene.setModelMatrixIdentity();
                scene.translateModelMatrix(-mPixelWidth/2 + mWidth*(cellWidth/2),
                        +mPixelHeight/2 - (cellHeight/2));
                mDrawer.drawCell(mBoard[j*mWidth+i],
                        GLES20.glGetUniformLocation(mProgramHandle,"u_Texture"),
                        GLES20.glGetAttribLocation(mProgramHandle,"v_TexCoordinate"));
                GH.vertexPosition(mProgramHandle,mPositionBuffer);
                GH.draw();
            }
        }
    }



    public static interface CellDrawer{
        public void create();
        public void drawCell(CELL cellId, int textureUniformHandle, int textureAttribHandle);
    }

    public static class TictacDrawer implements CellDrawer{
        private final Context mContext;
        private int mTexId;
        private FloatBuffer mThirdBuffer;
        private FloatBuffer mSecondBuffer;
        private FloatBuffer mFirstBuffer;

        public TictacDrawer(Context context){
            mContext = context;

        }

        /**
         * GLTHREAD
         */
        @Override
        public void create(){
            Bitmap bmp =((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.xo)).getBitmap();
            mTexId = GH.genTexture();
            GH.texImage2D(bmp,mTexId);
            int w = bmp.getWidth();
            int  h = bmp.getHeight();
            mFirstBuffer =GLTexture.GLPosition.calculateTextureBuffer(0,0,w/2,h/2,w,h);
            mSecondBuffer = GLTexture.GLPosition.calculateTextureBuffer(w/2,0,w/2,h/2,w,h);
            mThirdBuffer= GLTexture.GLPosition.calculateTextureBuffer(0, h/2, w / 2, h / 2, w, h);
        }

        @Override
        public void drawCell(CELL cellId, int textureUniformHandle, int textureAttribHandle) {
            GH.activeTexture0(mTexId,textureUniformHandle);
            switch (cellId){
                case NONE:
                        GH.textureVertexAttrib(mFirstBuffer,textureAttribHandle);
                    break;
                case X:
                    GH.textureVertexAttrib(mSecondBuffer,textureAttribHandle);
                    break;
                case O:
                    GH.textureVertexAttrib(mThirdBuffer,textureAttribHandle);
                    break;
                default:
            }
        }
    }
}
