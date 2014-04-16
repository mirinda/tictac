package kz.mirinda.tictac.renderer.examples.tictac;

import android.content.Context;

import kz.mirinda.tictac.R;
import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Created by asus on 16.04.14.
 */
public class TictacBoard {
    private static final int BOARD_PIXEL_SIZE=2048;
    public static enum CELL{
        NONE
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
    public TictacBoard(int width,int height, Context context){
        mWidth = width>0?width:1;
        mHeight = height>0?height:1;
        int pixelWidth =(int)(BOARD_PIXEL_SIZE *( width > height? 1.0f : width *1.0f / height));
        int pixelHeight =(int)(BOARD_PIXEL_SIZE *( width > height? height *1.0f/width : 1.0f));
        mFieldTexture = new GLTexture(pixelWidth,pixelHeight, GH.createProgram(context, R.raw.vertex_shader,R.raw.fragment_shader));

        mBoard = new CELL[mWidth * mHeight];
        for(int i=0; i < mBoard.length; i++){
            mBoard[i]=CELL.NONE;
        }
    }



    public static interface CellDrawer{
        public void drawCell(int cellId);
    }
}
