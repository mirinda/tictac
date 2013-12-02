package kz.mirinda.tictac.listeners;

import java.util.concurrent.SynchronousQueue;

import kz.mirinda.tictac.helper.MathHelper;

public class OneTouchQueueHandler implements OneTouchHandler {
    /**
	 * 
	 */

    SynchronousQueue<Line> mQueue;
    public static class Line{
        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public Line(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

	public OneTouchQueueHandler(SynchronousQueue<Line> queue) {
        mQueue = queue;
	}

	int counter=0;
	@Override
	public boolean up(float x1, float y1,float prevX1,float prevY1) {
		float[] xy = new float[]{x1,y1};
		float[] xyPrev = new float[]{prevX1, prevY1};
		return false;			
	}

	@Override
	public boolean down(float x1, float y1) {
		//float[] xy = mGPUImageRenderer.mapToScene(x1, y1);
		//this.photoShaderActivity.mGPUImageView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

	//	mGPUImageRenderer.getQueue().addLine(xy, xy,Queue.ACTION_DOWN);
		return true;
		
	}

	@Override
	public boolean move(float x1, float y1,float prevX1,float prevY1) {
		//SimpleBrushFilter sbf = (SimpleBrushFilter) mGPUImageRenderer.getFilter();
		//float radius = sbf.getRadius(); 
		float[] xy = new float[]{x1,y1};//mGPUImageRenderer.mapToScene(x1,y1);
		//xy = mGPUImageRenderer.mapToBitmap(event.getX(), event.getY());
		float[] xyPrev = new float[]{prevX1, prevY1};// mGPUImageRenderer.mapToScene(prevX1, prevY1);
		//float[] xyPrev =  mGPUImageRenderer.mapToBitmap(mPrevX1, mPrevY1);
		
		if(MathHelper.near(x1, y1, prevX1, prevY1, 4.0f)){
			return false; //не надо �?охран�?ть новое значение пользователь держит кур�?ор на одном ме�?те.
		}
		if(counter == 1){
	//	mGPUImageRenderer.getQueue().addLine(xyPrev, xy,Queue.ACTION_MOVE);
		counter = 0;
		}else{
			counter++;
			return false;
		}
        try{
            mQueue.put(new Line(x1,y1,prevX1,prevY1));
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
		return true; 
	}
	
}