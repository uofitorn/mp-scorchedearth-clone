package net.uofitorn.scorchedearth;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen implements Screen {

	final ScorchedEarthMain game;
	private static final String TAG = "GameScreen";
	
	float[] points = new float[Constants.POWER + 1];

	int midpoints = 1;
	
	
	Texture leftArrowTexture;
	Texture rightArrowTexture;
	Rectangle leftArrowRect;
	Rectangle rightArrowRect;

	OrthographicCamera camera;
	Tank tank1;
	float tankX;
	float tankY;
	
	boolean missileLaunched = false;
	
	
	World world;
	Box2DDebugRenderer debugRenderer;
	
	public GameScreen(final ScorchedEarthMain game) {
		this.game = game;
				
		leftArrowTexture = new Texture(Gdx.files.internal("cursor_left.png"));
		rightArrowTexture = new Texture(Gdx.files.internal("cursor_right.png"));
		leftArrowRect = new Rectangle();
		leftArrowRect.x = 800 - (Constants.ARROW_WIDTH * 3);
		leftArrowRect.y = 0;
		leftArrowRect.height = Constants.ARROW_HEIGHT;
		leftArrowRect.width = Constants.ARROW_WIDTH;
		rightArrowRect = new Rectangle();
		rightArrowRect.x = 800 - Constants.ARROW_WIDTH - Constants.ARROW_WIDTH/2;
		rightArrowRect.y = 0;
		rightArrowRect.height = Constants.ARROW_HEIGHT;
		rightArrowRect.width = Constants.ARROW_WIDTH;

		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//camera = new OrthographicCamera();
		//camera.setToOrtho(false, 800, 480);
		camera = new OrthographicCamera(20,
				20 * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));
	
		float height = 3.75f;
		float displace = 4 / 4;
		float roughness = 0.75f;
		points[0] = height/2 + (MathUtils.random() * displace * 2) - displace;
		points[Constants.POWER] = height/2 + (MathUtils.random() * displace * 2) - displace;
		
		displace *= roughness;
		
		for (int i = 1; i < Constants.POWER; i *= 2) {
			for (int j = (Constants.POWER/i)/2; j < Constants.POWER; j += Constants.POWER/i) {
				points[j] = ((points[j - (Constants.POWER/i) / 2] + points[j + (Constants.POWER / i)/2]) / 2);
				points[j] += (MathUtils.random() * displace * 2) - displace;
			}
			displace *= roughness;
		}	
		/*
		tank1 = new Tank(points);
		tank1.setX(100); */
		for (int i = 0; i < Constants.POWER; i++) {
			Gdx.app.debug("1", "1. i: " + points[i]);
		}
		
		
		world = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();
		
		float step = 20 / (float)Constants.POWER;
		Vector2[] vecs = new Vector2[4];
		BoxProp[] boxes = new BoxProp[Constants.POWER];
		for (int i = 0; i < Constants.POWER; i++) {
			Gdx.app.debug("GameScreen", "On i: " + i);
			vecs[0] = new Vector2();
			vecs[0].x = (float)i * step - 10;
			vecs[0].y = -camera.viewportHeight/2;
			vecs[1] = new Vector2();
			vecs[1].x = (float)i * step + step - 10;
			vecs[1].y = -camera.viewportHeight/2;
			vecs[2] = new Vector2();
			vecs[2].x = (float)i * step + step - 10;
			vecs[2].y = points[i+1];
			vecs[3] = new Vector2();
			vecs[3].x = (float)i * step - 10;
			vecs[3].y = points[i];
			boxes[i] = new BoxProp(world, vecs);
		}

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(0.1f);
		Shape shape = circleShape;
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = false;
		fixtureDef.density = 2.5f;
		fixtureDef.friction = 0.25f;
		fixtureDef.restitution = 0.75f;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(0, 0));

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		fixtureDef.shape.dispose(); 
		
		
	}
	
	@Override 
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.6f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    	debugRenderer.render(world, camera.combined);
        
        // tell the camera to update its matrices.
        //camera.update();
        
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        
        
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(1, 1, 0, 1);
        float step = 20 / (float)Constants.POWER;
        for (int i = 0; i < Constants.POWER; i++) {
        	shapeRenderer.line((float)i * step - 10, points[i], ((float)i*step) + step - 10, points[i+1]);
    		Gdx.app.debug("2", "2. i: " + points[i]);
        }
        shapeRenderer.end();
        /*game.batch.begin();
        game.batch.draw(leftArrowTexture, 800-64-64-64, 0);
        game.batch.draw(rightArrowTexture, 800-64-32, 0);
        game.batch.end();
        tank1.draw(camera); */
              
        /*
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (leftArrowRect.contains(touchPos.x, touchPos.y)) {
            	tank1.setX(tank1.getX() - 1);
            } else if (rightArrowRect.contains(touchPos.x, touchPos.y)) {
            	tank1.setX(tank1.getX() + 1);
            }
        }      */
        
    	world.step(1 / 60f, 6, 2);
	}
	
	@Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
 
    }

	
}
