package net.uofitorn.scorchedearth;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen implements Screen {

	final ScorchedEarthMain game;
	private static final String TAG = "GameScreen";
	
	float[] points = new float[Constants.POWER + 1];

	int midpoints = 1;
	
	Texture leftArrowTexture;
	Texture rightArrowTexture;
	Texture upArrowTexture;
	Texture downArrowTexture;
	Rectangle leftArrowRect;
	Rectangle rightArrowRect;
	Rectangle upArrowRect;
	Rectangle downArrowRect;

	OrthographicCamera camera;
	Tank tank1;
	float tankX;
	float tankY;
	int gunAngle = 0;
	
	boolean missileLaunched = false;
	
	
	World world;
	Box2DDebugRenderer debugRenderer;
	float scale;
	
	BodyDef bodyDef;
	Body body;
	
	BodyDef bulletBodyDef;
	Body bulletBody;
	
	
	Sprite tankSprite;
	Sprite gunSprite;
	
	public GameScreen(final ScorchedEarthMain game) {
		this.game = game;
		scale = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
		
		initStaticTextures();

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//camera = new OrthographicCamera();
		//camera.setToOrtho(false, 800, 480);
		
		camera = new OrthographicCamera(20, 20 * scale);
		
		generateTerrainPoints();
		
		world = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();
		
		float step = 20 / (float)Constants.POWER;
		Vector2[] vecs = new Vector2[4];
		BoxProp[] boxes = new BoxProp[Constants.POWER];
		for (int i = 0; i < Constants.POWER; i++) {
			//Gdx.app.debug("GameScreen", "On i: " + i);
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

		CircleShape bulletShape = new CircleShape();
		bulletShape.setRadius(0.1f);
		FixtureDef bulletFixtureDef = new FixtureDef();
		bulletFixtureDef.shape = bulletShape;
		bulletFixtureDef.isSensor = false;
		bulletFixtureDef.density = 1.0f;
		bulletFixtureDef.friction = 0.25f;
		bulletFixtureDef.restitution = 0.0f;
		bulletBodyDef = new BodyDef();
		bulletBodyDef.type = BodyType.DynamicBody;
		
		
		PolygonShape tankShape = new PolygonShape();
		//tankShape.setAsBox(1.0f, 0.5f);
		tankShape.setAsBox(2.0f / 2.0f, (2.0f * scale) / 2.0f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = tankShape;
		fixtureDef.isSensor = false;
		fixtureDef.density = 4.5f;
		fixtureDef.friction = 0.25f;
		fixtureDef.restitution = 0.0f;
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(25*step - 10, points[25]));

		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(tankSprite);
		
		fixtureDef.shape.dispose(); 
	}
	
	@Override 
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.6f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        
    	debugRenderer.render(world, camera.combined);
        
        // tell the camera to update its matrices.
        camera.update();
        
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
    		//Gdx.app.debug("2", "2. i: " + points[i]);
        }
        shapeRenderer.end();
        
        // want to convert 64 to 
        game.batch.begin();
    	
        Sprite sprite = (Sprite) body.getUserData();
        Vector2 position = body.getPosition();
        sprite.setPosition((position.x - sprite.getWidth() / 2.0f) + 0.05f, (position.y - sprite.getWidth() / 2.0f) + 0.05f);
        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
    	sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    	
        gunSprite.setPosition((position.x - sprite.getWidth() / 2.0f) + 0.05f, (position.y - sprite.getWidth() / 2.0f) + 0.70f);
    	gunSprite.setRotation(gunAngle);
    	
    	gunSprite.draw(game.batch);
    	sprite.draw(game.batch);
    	
        game.batch.draw(leftArrowTexture, leftArrowRect.x, leftArrowRect.y, leftArrowRect.width, leftArrowRect.height);
        game.batch.draw(rightArrowTexture, rightArrowRect.x, rightArrowRect.y, rightArrowRect.width, rightArrowRect.height);
        game.batch.draw(upArrowTexture, upArrowRect.x, upArrowRect.y, upArrowRect.width, upArrowRect.height);
        game.batch.draw(downArrowTexture, downArrowRect.x, downArrowRect.y, downArrowRect.width, downArrowRect.height);
        game.batch.end();
                    
        if (Gdx.input.isTouched()) {
        	Gdx.app.debug("GameScreen", "Got touch event");
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (leftArrowRect.contains(touchPos.x, touchPos.y)) {
            	body.applyLinearImpulse(-1.3f, 0, bodyDef.position.x, bodyDef.position.y, true);
            } else if (rightArrowRect.contains(touchPos.x, touchPos.y)) {
           		body.applyLinearImpulse(1.3f, 0, bodyDef.position.x, bodyDef.position.y, true);         	
            } else if (downArrowRect.contains(touchPos.x, touchPos.y)) {
            	Gdx.app.debug("GameScreen", "gunAngle: " + gunAngle);
            	if (gunAngle <= 0) {
            		gunAngle++;
            	}
        	} else if (upArrowRect.contains(touchPos.x, touchPos.y)) {
        		Gdx.app.debug("GameScreen", "gunAngle: " + gunAngle);
        		if (gunAngle > -180) {
        			gunAngle--;
        		}
        	}
        }      
        
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

    protected void generateTerrainPoints() {
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
    }
    
    protected void initStaticTextures() {
		leftArrowTexture = new Texture(Gdx.files.internal("cursor_left.png"));
		rightArrowTexture = new Texture(Gdx.files.internal("cursor_right.png"));
		upArrowTexture = new Texture(Gdx.files.internal("cursor_up.png"));
		downArrowTexture = new Texture(Gdx.files.internal("cursor_down.png"));
		tankSprite = new Sprite(new Texture(Gdx.files.internal("tanknogun.png")));
		tankSprite.setSize(2.0f, 2.0f * scale);
		tankSprite.setOrigin(tankSprite.getWidth() / 2.0f, tankSprite.getHeight() / 2.0f);
		gunSprite = new Sprite(new Texture(Gdx.files.internal("gun.png")));
		gunSprite.setSize(1.0f, 0.5f * scale);
		gunSprite.setOrigin(gunSprite.getWidth(), gunSprite.getHeight() / 2);
		leftArrowRect = new Rectangle(6, -6, 1, 1);
		rightArrowRect = new Rectangle(8, -6, 1, 1);
		upArrowRect = new Rectangle(-8, -4, 1, 1);
		downArrowRect = new Rectangle(-8, -6, 1, 1);
    }
	
}
