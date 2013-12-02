package kz.mirinda.tictac.listeners;

public interface OneTouchHandler{
	boolean up(float x1, float y1,float prevX1,float prevY1);
	boolean down(float x1, float y1);
	boolean move(float x1, float y1,float prevX1,float prevY1);
}