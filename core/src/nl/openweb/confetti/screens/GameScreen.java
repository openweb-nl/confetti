package nl.openweb.confetti.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import nl.openweb.confetti.ConfettiGame;

public class GameScreen implements Screen {
	private final int GRID_DIMENSION = 5;
	private final int GRID_CELL_SIZE = 100;
	private final SpriteBatch batch;
	private final ConfettiGame game;
	private final Camera camera;
	private final ShapeRenderer gridRenderer;
	private final float gridStartX;
	private final float gridStartY;

	public GameScreen(ConfettiGame game) {
		this.game = game;
		this.batch = new SpriteBatch();
		this.camera = game.getCamera();
		this.gridRenderer = new ShapeRenderer();
		this.gridStartX = game.getCenterX() - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
		this.gridStartY = game.getCenterY() - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		drawGrid();

		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.end();
	}

	private void drawGrid() {
		gridRenderer.begin(ShapeRenderer.ShapeType.Line);
		gridRenderer.setColor(1, 1, 1, 1);
		gridRenderer.setProjectionMatrix(camera.combined);

		for (int column=0; column<GRID_DIMENSION; column++) {
			for (int row=0; row<GRID_DIMENSION; row++) {
				final float cellStartX = gridStartX + (column * GRID_CELL_SIZE);
				final float cellStartY = gridStartY + (row * GRID_CELL_SIZE);
				gridRenderer.rect(cellStartX, cellStartY, GRID_CELL_SIZE, GRID_CELL_SIZE);
			}
		}

		gridRenderer.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
