package thwack;

import java.util.HashMap;
import java.util.Map;

import thwack.collision.CollisionContext;
import thwack.model.Block;
import thwack.model.Mob;
import thwack.model.Player;
import thwack.model.Updateable;
import thwack.view.BlockRenderer;
import thwack.view.MobRenderer;
import thwack.view.PlayerRenderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ThwackGame extends ApplicationAdapter {
	
	private OrthographicCamera camera;
	
	private Map<String, Object> context = new HashMap<String, Object>();
	
	public static final String SPRITE_BATCH = "SPRITE_BATCH";
	private SpriteBatch batch;
	
	public static final String BITMAP_FONT = "BITMAP_FONT";
	private BitmapFont font;
	
	public static final String SHAPE_RENDERER = "SHAPE_RENDERER";
	private ShapeRenderer shapeRenderer;
	
	private CollisionContext collisionContext;
	
	private Array<Updateable> updateables = new Array<Updateable>();
	
	private Array<Disposable> disposables = new Array<Disposable>();
	
	private Player player;
	
	private PlayerRenderer playerRenderer;
	
	private MobRenderer mobRenderer;
	
	private Array<Mob> mobs = new Array<Mob>();
	
	private BlockRenderer blockRenderer;
	
	private Array<Block> blocks = new Array<Block>();
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		
		batch = new SpriteBatch();
		
		context.put(SPRITE_BATCH, batch);
		disposables.add(batch);

		font = new BitmapFont();
		context.put(BITMAP_FONT, font);
		disposables.add(font);
		
		shapeRenderer = new ShapeRenderer();
		context.put(SHAPE_RENDERER, shapeRenderer);
		disposables.add(shapeRenderer);

		collisionContext = new CollisionContext();
		context.put(CollisionContext.COLLISION, collisionContext);
	
		playerRenderer = new PlayerRenderer(batch, shapeRenderer);
		
		blockRenderer = new BlockRenderer(shapeRenderer);
		
		mobRenderer = new MobRenderer(batch, null);

		player = new Player();
		updateables.add(player);
		collisionContext.add(player);
		
		for (int i = 0; i < 10; i++) {
			Mob b = new Mob();
			
			do {
				b.setPosition(MathUtils.random(20.0f) - 10.0f, MathUtils.random(20.0f) - 10.0f);
			} while (b.collidesWith(player));
			
			mobs.add(b);
			collisionContext.add(b);
		}
		
		for (int i = 0; i < 10; i++) {
			Block b = new Block();
			
			do {
				b.setPosition(MathUtils.random(20.0f) - 10.0f,  MathUtils.random(20.0f) - 10.0f);
			} while (b.collidesWith(player));
			
			blocks.add(b);
			collisionContext.add(b);
		}
		
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);

		float deltaTime = Gdx.graphics.getDeltaTime();
	
		for (Updateable updateable : updateables) {
			updateable.update(deltaTime, context);
		}
		
		playerRenderer.render(player);
		
		for (Mob mob : mobs) {
			mobRenderer.render(mob);
		}
		
		for (Block b : blocks) {
			blockRenderer.render(b);
		}
	}
	
	@Override
	public void dispose() {
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		Vector3 oldpos = new Vector3(camera.position);
		camera.setToOrtho(false, width/Constants.PIXELS_PER_METER, height/Constants.PIXELS_PER_METER);
		camera.translate(oldpos.x - camera.position.x, oldpos.y - camera.position.y);
	}

}
