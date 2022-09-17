package nl.openweb.confetti.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import nl.openweb.confetti.ConfettiGame;
import nl.openweb.confetti.model.GridCell;
import nl.openweb.confetti.model.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final ConfettiGame game;
    private final Camera camera;
    private final ShapeRenderer gridRenderer;
    private final float gridStartX;
    private final float gridStartY;
    private final List<GridCell> gridCells;
    private final List<Player> players;

    public GameScreen(ConfettiGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = game.getCamera();
        this.gridRenderer = new ShapeRenderer();
        this.gridStartX = game.getCenterX() - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
        this.gridStartY = game.getCenterY() - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
        this.gridCells = new ArrayList<>();

        players = List.of(
                new Player("Blue player", new Texture(Gdx.files.internal("blue-player.png")), 0, 0),
                new Player("Green player", new Texture(Gdx.files.internal("green-player.png")), GRID_DIMENSION-1, 0),
                new Player("Red player", new Texture(Gdx.files.internal("red-player.png")), 0, GRID_DIMENSION-1),
                new Player("Yellow player", new Texture(Gdx.files.internal("yellow-player.png")), GRID_DIMENSION-1, GRID_DIMENSION-1)
        );
    }

    @Override
    public void show() {
        for (int column = 0; column < GRID_DIMENSION; column++) {
            for (int row = 0; row < GRID_DIMENSION; row++) {
                final float cellStartX = gridStartX + (column * GRID_CELL_SIZE);
                final float cellStartY = gridStartY + (row * GRID_CELL_SIZE);

                this.gridCells.add(new GridCell(column, row, cellStartX, cellStartY));
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawGrid();
        drawPlayers();
    }

    private void drawPlayers() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        players.forEach(player -> {
            GridCell playerGridCell = getPlayerGridCell(player);
            batch.draw(player.getSpriteTexture(), player.getCellDrawXPosition(playerGridCell), player.getCellDrawYPosition(playerGridCell));
        });
        batch.end();
    }

    private GridCell getPlayerGridCell(Player player) {
        return gridCells.stream().filter(gridCell -> gridCell.getColumn() == player.getColumn() && gridCell.getRow() == player.getRow()).findFirst().get();
    }

    private void drawGrid() {
        gridRenderer.begin(ShapeRenderer.ShapeType.Line);
        gridRenderer.setColor(1, 1, 1, 1);
        gridRenderer.setProjectionMatrix(camera.combined);

        gridCells.forEach(gridCell -> gridRenderer.rect(gridCell.getStartX(), gridCell.getStartY(), GRID_CELL_SIZE, GRID_CELL_SIZE));

        gridRenderer.setColor(0, 1, 0, 1);
        gridRenderer.rect(1, 1, game.getViewport().getWorldWidth() - 1, game.getViewport().getWorldHeight() - 1);

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
