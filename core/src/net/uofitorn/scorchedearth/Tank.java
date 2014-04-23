package net.uofitorn.scorchedearth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class Tank {
	float x;
	float y;
	float [] points;
	
	public Tank(float[] points) {
		this.points = points;
	}
	
	public void draw(Camera camera) {
		 ShapeRenderer shapeRenderer = new ShapeRenderer();
	     shapeRenderer.setProjectionMatrix(camera.combined);
	     shapeRenderer.begin(ShapeType.Line);
	     shapeRenderer.setColor(1, 0, 1, 1);
	     shapeRenderer.rect(x - 16,  y, 32, 16);
	     shapeRenderer.end();
	     
	}
	
	public void setX(float x) {
		this.x = x;
		float leftI = points[MathUtils.floor((x - 16) / (800 / (float)Constants.POWER))];
		float rightI = points[MathUtils.ceil((x + 16) / (800 / (float)Constants.POWER))];
		if (leftI > rightI) {
			y = leftI;
		}
		if (rightI > leftI) {
			y = rightI;
		}
		int left = MathUtils.floor((x - 16) / (800 / (float)Constants.POWER));
		int right = MathUtils.ceil((x + 16) / (800 / (float)Constants.POWER));
		float max = 0;
		for (int i = left; i <= right; i++) {
			if (points[i] > max) {
				max = points[i];
			}
		}
		y = max;
		
		float theta = MathUtils.atan2(points[right] - points[left], x - 16 - x + 16);
        theta = theta * (180 / MathUtils.PI);
		
		Gdx.app.debug("Tank", "theta is " + theta);
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
