package kz.mirinda.tictac.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;


import kz.mirinda.tictac.R;
import kz.mirinda.tictac.renderer.GLRenderer;
import kz.mirinda.tictac.renderer.examples.BrushExample;
import kz.mirinda.tictac.renderer.examples.BrushShtampExample;
import kz.mirinda.tictac.renderer.examples.FxaaExample;
import kz.mirinda.tictac.renderer.examples.ParticlesExample;

public class MyActivity extends Activity implements Button.OnClickListener, View.OnTouchListener, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = MyActivity.class.getSimpleName();
    public static Handler mHandler;
    GLSurfaceView mSurface;
    GLRenderer mRenderer;

    Button mActionButton;
    ScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       // mActionButton = (Button) findViewById(R.id.action);
        //mActionButton.setOnClickListener(this);

        mHandler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i(TAG,"Message HANDLED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                ImageView imgView =(ImageView)findViewById(R.id.trace_image);
                imgView.setImageBitmap((Bitmap)msg.obj);
                imgView.invalidate();
            }
        };

        mRenderer =new GLRenderer(this,new ParticlesExample(this));
        mRenderer.setHandler(mHandler);
        mScrollView = (ScrollView) findViewById(R.id.scroller);
        mRenderer.getExample().setScrollView(mScrollView);
        mSurface = (GLSurfaceView)findViewById(R.id.surface);
        mSurface.setEGLContextClientVersion(2);
        mSurface.setRenderer(mRenderer);
        mSurface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mSurface.setOnTouchListener(this);

        CheckBox check = (CheckBox) findViewById(R.id.checker);
        check.setOnCheckedChangeListener(this);

    }


    @Override
    public void onClick(View view) {
        mSurface.destroyDrawingCache();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "TOUCH");
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(mRenderer.getExample() instanceof FxaaExample){
            ((FxaaExample)mRenderer.getExample()).setFxaaEnabled(isChecked);
        }else if(mRenderer.getExample() instanceof BrushShtampExample){
            ((BrushShtampExample)mRenderer.getExample()).setFxaaEnabled(isChecked);
        }else if(mRenderer.getExample() instanceof  BrushExample){
            ((BrushExample)mRenderer.getExample()).setColor(isChecked? Color.argb(255,12,34,96): Color.argb(255,124,34,12));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurface.onPause();
    }
}
