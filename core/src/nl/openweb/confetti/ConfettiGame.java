package nl.openweb.confetti;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
		this.camera.update();

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

		centerX = viewport.getWorldWidth() / 2f;
		centerY = viewport.getWorldHeight() / 2f;

		this.setScreen(new MainScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}

	public OrthographicCamera getCamera() {
		return this.camera;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}
}
