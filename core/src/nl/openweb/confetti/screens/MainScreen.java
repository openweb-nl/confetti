package nl.openweb.confetti.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import nl.openweb.confetti.ConfettiGame;

public class MainScreen implements Screen {
	private final SpriteBatch batch;
	private final Texture title;
	private final Music music;
	private final OrthographicCamera camera;
	private final ConfettiGame game;
	private final ShapeRenderer debugShapeRenderer;

	public MainScreen(ConfettiGame game) {
		this.camera = game.getCamera();
		this.camera.update();
		this.game = game;
		this.batch = new SpriteBatch();
		this.title = new Texture(Gdx.files.internal("title.png"));
		this.music = Gdx.audio.newMusic((Gdx.files.internal("music.mp3")));
		this.debugShapeRenderer = new ShapeRenderer();

		Gdx.input.setInputProcessor(new InputAdapter(){
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.ENTER) {
					MainScreen.this.game.setScreen(new GameScreen(MainScreen.this.game));
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void show() {
		this.music.play();
		this.music.setLooping(true);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(title, game.getCenterX() - (title.getWidth() / 2f), 420);
		batch.end();

		drawDebugLines();
	}

	@Override
	public void resize(int width, int height) {
		game.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		this.music.stop();
	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.music.dispose();
	}

	private void drawDebugLines() {
		debugShapeRenderer.setProjectionMatrix(camera.combined);
		debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		debugShapeRenderer.setColor(0, 1, 0, 1);
		debugShapeRenderer.rect(0, 0, 800, 600);
		debugShapeRenderer.end();
	}
}
