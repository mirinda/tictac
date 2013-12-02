package kz.mirinda.tictac.listeners;

import kz.mirinda.tictac.helper.MathHelper;

public class TwoTouchMoveScaleHandler implements TwoTouchHandler {

	private Mover mMover;
	private Scaler mScaler;
	
	public TwoTouchMoveScaleHandler(Mover mover, Scaler scaler) {
		mMover = mover;
		mScaler = scaler;
	}
	
	@Override
	public boolean up(float x1, float y1, float prevX1, float prevY1) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean down(float x1, float y1) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean move(float x1, float y1, float prevX1, float prevY1) {
		//float[] xy= mGPUImageRenderer.mapToScene(x1, y1);
		//float[] xyPrev= mGPUImageRenderer.mapToScene(prevX1, prevY1);
		
		//mGPUImageRenderer.getMoveScaler().moveImage(xy[0]-xyPrev[0],-xy[1]+xyPrev[1]);
		mMover.move(x1-prevX1,-y1+prevY1);
		
		//mGPUImageView.requestRender();
		return true;
	}

	@Override
	public boolean scale(float x1, float y1, float prevX1, float prevY1,
			float x2, float y2, float prevX2, float prevY2) {
		float xy1 =(float) MathHelper.length(new float[]{x1, y1}, new float[]{x2, y2});
		float xyPrev = (float) MathHelper.length(new float[]{prevX1, prevY1},new float[]{prevX2, prevY2});
		if (prevX1 == 0 || prevX2 == 0
				|| prevY1 == 0 || prevY2 == 0)
			return false;
		float scale = xy1/xyPrev;
		//mGPUImageRenderer.getMoveScaler().changeScale(scale);
		mScaler.scale(scale);
		return true;
	}
	
}