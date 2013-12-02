package kz.mirinda.tictac.listeners;

import kz.mirinda.tictac.listeners.OneTouchHandler;

public interface TwoTouchHandler extends OneTouchHandler {
	boolean scale(float x1, float y1,float prevX1,float prevY1,float x2, float y2,float prevX2,float prevY2);
}