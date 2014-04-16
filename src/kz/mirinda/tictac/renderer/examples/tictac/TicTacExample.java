package kz.mirinda.tictac.renderer.examples.tictac;

import android.content.Context;
import android.os.Handler;
import android.widget.ScrollView;

import kz.mirinda.tictac.gl.GH;
import kz.mirinda.tictac.renderer.engine.GLTexture;
import kz.mirinda.tictac.renderer.examples.Example;

/**
 * Created by asus on 12.04.14.
 */
public class TicTacExample implements Example {
    public static final int N = 30;
    public static final int M = 41;
    private final Context mContext ;
    private GLTexture.Scene mScreenScene;
    private int mFrameBuffer;
    private int mSuperBuffer;

    public TicTacExample(Context context, Player player1, Player player2){
        mContext = context;

    }

    @Override
    public void create() {
        mFrameBuffer = GH.genFrameBuffer();
        mSuperBuffer = GH.getCurrentBuffer();
        //mFieldTexture = //new GLTexture(2048,2048*N/M,);
    }

    @Override
    public void change(int screenWidth, int screenHeight) {
        mScreenScene = new GLTexture.Scene(screenWidth,screenHeight);
    }

    @Override
    public void draw() {
        //Что должно происходить?
        //рассматривается простейший случай границы захардкодены
        // Доска(Board) это модель данных для игры
        //кто регулирует взаимодействие игроков? Откуда они могут появиться?
        // всё что игроки должны знать:
        //1) уведомление о начале хода - игрок хватает какие-то нужные ему ресурсы
        //2) иметь возможность сделать ход в любое время если он начинает данный ход
        //3) уведомление о победе
    }

    @Override
    public void setHandler(Handler handler) {

    }

    @Override
    public void setScrollView(ScrollView scrollView) {

    }
}
