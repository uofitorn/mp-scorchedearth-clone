package net.uofitorn.scorchedearth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BoxProp {
	public Body body;
	static int count = 0;
	public BoxProp(World world, Vector2[] vecs) {
		float centroidX = (vecs[0].x + vecs[1].x + vecs[2].x + vecs[3].x) / 4;
		float centroidY = (vecs[0].y + vecs[1].y + vecs[2].y + vecs[3].y) / 4;
		Vector2 centroid = new Vector2(centroidX, centroidY);
		
		Vector2[] relativeVecs = new Vector2[4];
		relativeVecs[0] = new Vector2(vecs[0].x - centroidX, vecs[0].y - centroidY);
		relativeVecs[1] = new Vector2(vecs[1].x - centroidX, vecs[1].y - centroidY);
		relativeVecs[2] = new Vector2(vecs[2].x - centroidX, vecs[2].y - centroidY);
		relativeVecs[3] = new Vector2(vecs[3].x - centroidX, vecs[3].y - centroidY);
		
		
		//initialize body 
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(centroid);
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		this.body = world.createBody(bodyDef);
	    
	    //initialize shape
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape boxShape = new PolygonShape();
		//boxShape.setAsBox(this.width / 2, this.height / 2);		
		boxShape.set(relativeVecs);
		fixtureDef.shape=boxShape;
		fixtureDef.restitution=0.4f; //positively bouncy!
	    this.body.createFixture(fixtureDef);
	    //boxShape.dispose();
	}
}
