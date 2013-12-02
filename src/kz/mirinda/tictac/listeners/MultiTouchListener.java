package kz.mirinda.tictac.listeners;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import kz.mirinda.tictac.helper.MathHelper;

public class MultiTouchListener implements OnTouchListener{
    public static final String TAG = MultiTouchListener.class.getSimpleName();

	
	public MultiTouchListener(Mover mover, Scaler scaler) {
		twoTouchMoveScaleHandler = new TwoTouchMoveScaleHandler(mover, scaler);
		twoTouchHandler = twoTouchMoveScaleHandler;
	}

	private static final float SCALE_EPS = 10;
	//OneTouchQueueHandler oneTouchQueueHandler = new OneTouchQueueHandler();
	OneTouchHandler oneTouchEmptyHandler = new OneTouchHandler() {
		
		@Override
		public boolean up(float x1, float y1, float prevX1, float prevY1) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean move(float x1, float y1, float prevX1, float prevY1) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean down(float x1, float y1) {
			// TODO Auto-generated method stub
			return false;
		}
	};
    TwoTouchMoveScaleHandler twoTouchMoveScaleHandler ;
	OneTouchHandler oneTouchHandler=new OneTouchHandler() {
        @Override
        public boolean up(float x1, float y1, float prevX1, float prevY1) {
            return false;
        }

        @Override
        public boolean down(float x1, float y1) {
            return false;
        }

        @Override
        public boolean move(float x1, float y1, float prevX1, float prevY1) {
            return false;
        }
    };
	TwoTouchHandler twoTouchHandler ;
	
	public void setEmpty(){
		oneTouchHandler = oneTouchEmptyHandler;
	}

	public void setOnTouchHandler(OneTouchHandler touchHandler){
		oneTouchHandler = touchHandler;
	}
	
	boolean twoPointerMode=false;
	private float mPrevX1 = 0;
	private float mPrevY1 = 0;
	private float mPrevX2 = 0;
	private float mPrevY2 = 0;
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//if(mGPUImageRenderer.getFilter() instanceof SimpleBrushFilter){
			//oneTouchHandler = oneTouchQueueHandler;
		//}else{
		//	oneTouchHandler = twoTouchMoveScaleHandler;
		//}
		//Log.e("LOGGER", "eventpointer:" + event.getPointerCount());
     //   Log.d(TAG,""+ event);
        if(event.getPointerCount()==2  ){
			//Log.e("LOGGER", "event:"+ ((event.getAction()==MotionEvent.ACTION_DOWN)?"DOWN":event.getAction()==MotionEvent.ACTION_UP?"UP":event.getAction()==MotionEvent.ACTION_MOVE?"MOVE":event.getAction()==MotionEvent.ACTION_POINTER_DOWN?"POINTER_DOWN":event.getAction()==MotionEvent.ACTION_POINTER_UP?"POINTER_UP":event.getAction()));
			
			twoPointerMode = true;
			float x1 = event.getX(0);
			float y1 = event.getY(0);
			float x2 = event.getX(1);
			float y2 = event.getY(1);
			
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					updateDoublePrev(x1, y1, x2, y2);
				break;
				case MotionEvent.ACTION_MOVE:
						float length12 = (float) MathHelper.length(new float[]{x1, y1}, new float[]{x2, y2});
						float lengthPrev12 = (float) MathHelper.length(new float[]{mPrevX1, mPrevY1}, new float[]{mPrevX2, mPrevY2}); 
						//Log.e("LOGGER", "scale:" + (length12 - lengthPrev12));	
						if(Math.abs(length12 - lengthPrev12) < SCALE_EPS){
							twoTouchHandler.move(x1, y1, mPrevX1, mPrevY1);
						}else{
							twoTouchHandler.scale(x1, y1, mPrevX1, mPrevY1, x2, y2, mPrevX2, mPrevY2);
						}
						updateDoublePrev(x1, y1, x2, y2);
				break;
				case MotionEvent.ACTION_UP:
					twoPointerMode = false;
				break;
				case MotionEvent.ACTION_POINTER_DOWN:
					//Log.e("LOGGER", "action pointer 1 down");	
					updateDoublePrev(event.getX(0), event.getY(0),event.getX(1), event.getY(1));
				break;
									
				case MotionEvent.ACTION_POINTER_2_DOWN:
					//Log.e("LOGGER", "action pointer 2 down");	
					updateDoublePrev(event.getX(0), event.getY(0),event.getX(1), event.getY(1));
				break;
				
				
				
				default:
					updateDoublePrev(event.getX(0), event.getY(0),event.getX(1), event.getY(1));
					//twoPointerMode = false;
					break;
			
			}
			
		}else if(event.getPointerCount()==1 && !twoPointerMode){
			//Log.e("LOGGER", "event:"+ ((event.getAction()==MotionEvent.ACTION_DOWN)?"DOWN":event.getAction()==MotionEvent.ACTION_UP?"UP":event.getAction()==MotionEvent.ACTION_MOVE?"MOVE":event.getAction()));
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					if(oneTouchHandler.down(event.getX(0), event.getY(0))){
						updatePrev(event.getX(0), event.getY(0));
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if(oneTouchHandler.move(event.getX(0), event.getY(0),mPrevX1,mPrevY1)){
						updatePrev(event.getX(0), event.getY(0));
					}
					break;
				case MotionEvent.ACTION_UP:
					if(oneTouchHandler.up(event.getX(0), event.getY(0),mPrevX1,mPrevY1)){
						updatePrev(event.getX(0), event.getY(0));
					}
					break;
			}
		}else{
			//Log.e("LOGGER", "event:"+ ((event.getAction()==MotionEvent.ACTION_DOWN)?"DOWN":event.getAction()==MotionEvent.ACTION_UP?"UP":event.getAction()==MotionEvent.ACTION_MOVE?"MOVE":event.getAction()==MotionEvent.ACTION_POINTER_DOWN?"POINTER_DOWN":event.getAction()==MotionEvent.ACTION_POINTER_UP?"POINTER_UP":event.getAction()));
			updatePrev(event.getX(0), event.getY(0));
			if(event.getAction()==MotionEvent.ACTION_UP){
				twoPointerMode= false;
			}
		}
		
		
		return true;
	}
	private void updatePrev(float x1, float y1) { 
		mPrevX1 = x1;
		mPrevY1 = y1;			
	}
	private void updateDoublePrev(float x1, float y1,float x2, float y2) { 
		mPrevX1 = x1;
		mPrevY1 = y1;	
		mPrevX2 = x2;
		mPrevY2 = y2;	
	}
	
	
	
}