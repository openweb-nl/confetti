package nl.openweb.confetti;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import nl.openweb.confetti.screens.MainScreen;

public class ConfettiGame extends Game {
	private static final int WORLD_WIDTH = 800;
	private static final int WORLD_HEIGHT = 600;

	private float centerX;
	private float centerY;

	private Viewport viewport;
	private OrthographicCamera camera;

	@Override
	public void create () {
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

		centerX = viewport.getWorldWidth() / 2f;
		centerY = viewport.getWorldHeight() / 2f;

		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.position.set(centerX, centerY, 0);

		this.setScreen(new MainScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}

	public OrthographicCamera getCamera() {
		return this.camera;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}
}
