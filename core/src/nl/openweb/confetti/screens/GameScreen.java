package nl.openweb.confetti.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import nl.openweb.confetti.ConfettiGame;
import nl.openweb.confetti.GridManager;
import nl.openweb.confetti.database.Database;
import nl.openweb.confetti.dialog.GameNotification;
import nl.openweb.confetti.model.GridCell;
import nl.openweb.confetti.model.Move;
import nl.openweb.confetti.model.PlayerActor;

import java.util.List;
import java.util.Optional;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;
import static nl.openweb.confetti.model.Player.AMOUNT_OF_MOVES;

public class GameScreen implements Screen {
    private final Stage stage;
    private final SpriteBatch batch;
    private final ConfettiGame game;
    private final Camera camera;
    private final ShapeRenderer gridRenderer;
    private final ShapeRenderer controlsRenderer;
    private final SpriteBatch controlsImagesRenderer;
    private final int CONTROL_CELL_SIZE = 55;

    public GameScreen(ConfettiGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = game.getCamera();
        this.gridRenderer = new ShapeRenderer();
        this.controlsRenderer = new ShapeRenderer();
        this.controlsImagesRenderer = new SpriteBatch();
        this.stage = new Stage(game.getViewport(), batch);

        GridManager.getInstance().init(game.getCenterX(), game.getCenterY());
        GameNotification.getInstance().init(game, "Testing", 2, 400, 280, () -> System.out.println("Closed"));

        final List<PlayerActor> players = Database.getInstance().getPlayers();
        players.forEach(stage::addActor);

        GridManager.getInstance().setPlayers(players);
        Database.getInstance().addMoves(GridManager.getInstance().getPlayers().get(0), List.of(
                new Move(GridManager.getInstance().getPlayers().get(0).getId(), 1, 1, 0))
        );
        GridManager.getInstance().resetActivePlayer();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!GameNotification.getInstance().isVisible()) {
                    int playersAlive = GridManager.getInstance().getPlayers().stream().filter(PlayerActor::isAlive).toList().size() - 1;

                    PlayerActor currentPlayer = GridManager.getInstance().getActivePlayer();
                    if (keycode == Input.Keys.LEFT) {
                        currentPlayer.addMove(new Move(currentPlayer.getId(), currentPlayer.getMoves().size(), -1, 0));
                    }
                    if (keycode == Input.Keys.RIGHT) {
                        currentPlayer.addMove(new Move(currentPlayer.getId(), currentPlayer.getMoves().size(), 1, 0));
                    }
                    if (keycode == Input.Keys.UP) {
                        currentPlayer.addMove(new Move(currentPlayer.getId(), currentPlayer.getMoves().size(), 0, 1));
                    }
                    if (keycode == Input.Keys.DOWN) {
                        currentPlayer.addMove(new Move(currentPlayer.getId(), currentPlayer.getMoves().size(), 0, -1));
                    }
                    if (keycode == Input.Keys.BACKSPACE) {
                        currentPlayer.revertMove();
                    }
                    if (keycode == Input.Keys.SPACE) {
                        if (currentPlayer.getMoves().size() == AMOUNT_OF_MOVES) {
                            PlayerActor nextActivePlayer = GridManager.getInstance().getNextActivePlayer();
                            if (nextActivePlayer == null) {
                                GridManager.getInstance().resetActivePlayer();
                                GameNotification.getInstance().showNotification("Showtime!");
                                GameNotification.getInstance().setDialogEvent(() -> new Thread(() -> applyAllPlayerMoves()).start());
                            } else {
                                GameNotification.getInstance().showNotification(nextActivePlayer.getName());
                            }
                        }
                    }
                } else {
                    if (keycode == Input.Keys.ENTER && GameNotification.getInstance().isVisible() && GameNotification.getInstance().isWaitForKeyPress()) {
                        GameNotification.getInstance().close();
                        if (GridManager.getInstance().isGameFinished()) {
                            GameScreen.this.game.setScreen(new MainScreen(GameScreen.this.game));
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void show() {
        for (int column = 0; column < GRID_DIMENSION; column++) {
            for (int row = 0; row < GRID_DIMENSION; row++) {
                final float cellStartX = GridManager.getInstance().getGridStartX() + (column * GRID_CELL_SIZE);
                final float cellStartY = GridManager.getInstance().getGridStartY() + (row * GRID_CELL_SIZE);

                GridManager.getInstance().addCell(new GridCell(column, row, cellStartX, cellStartY));
            }
        }

        String playerName = GridManager.getInstance().getActivePlayer().getName();
        GameNotification.getInstance().showNotification(playerName);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawGrid();
        drawPlayers();
        drawControls();
        drawControlImages();

        GameNotification.getInstance().drawNotification();
    }

    private void drawPlayers() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }


    private void drawGrid() {
        gridRenderer.begin(ShapeRenderer.ShapeType.Line);
        gridRenderer.setColor(1, 1, 1, 1);
        gridRenderer.setProjectionMatrix(camera.combined);

        GridManager.getInstance()
                .getGridCells()
                .forEach(gridCell -> gridRenderer.rect(gridCell.getStartX(), gridCell.getStartY(), GRID_CELL_SIZE, GRID_CELL_SIZE));

        gridRenderer.end();
    }

    private void drawControlImages() {
        PlayerActor player = GridManager.getInstance().getActivePlayer();
        controlsImagesRenderer.begin();
        controlsImagesRenderer.setProjectionMatrix(camera.combined);
        controlsImagesRenderer.draw(player.getSpriteTexture(), 5, (AMOUNT_OF_MOVES * CONTROL_CELL_SIZE) + 15);

        TextureRegion arrow = new TextureRegion(new Texture(Gdx.files.internal("./arrow.png")));

        for (int movesMade = 0; movesMade < player.getMoves().size(); movesMade++) {
            if (player.getMoves().get(movesMade).getDeltaX() == 1) {
                controlsImagesRenderer.draw(arrow, 20, (movesMade * CONTROL_CELL_SIZE) + 20, 16, 16, 32, 32, 1, 1, 0);
            }
            if (player.getMoves().get(movesMade).getDeltaX() == -1) {
                controlsImagesRenderer.draw(arrow, 20, (movesMade * CONTROL_CELL_SIZE) + 20, 16, 16, 32, 32, 1, 1, 180);
            }
            if (player.getMoves().get(movesMade).getDeltaY() == 1) {
                controlsImagesRenderer.draw(arrow, 20, (movesMade * CONTROL_CELL_SIZE) + 20, 16, 16, 32, 32, 1, 1, 90);
            }
            if (player.getMoves().get(movesMade).getDeltaY() == -1) {
                controlsImagesRenderer.draw(arrow, 20, (movesMade * CONTROL_CELL_SIZE) + 20, 16, 16, 32, 32, 1, 1, 270);
            }
        }

        controlsImagesRenderer.end();
    }

    private void applyAllPlayerMoves() {
        GridManager.getInstance().performPlayerMove();
    }

    private Optional hitPlayer(PlayerActor activePlayer) {
        PlayerActor deadPlayer = null;
        for (PlayerActor player : GridManager.getInstance().getPlayers()) {
            if (player.getGridCoordinates().getColumn() == activePlayer.getGridCoordinates().getColumn() &&
                    player.getGridCoordinates().getRow() == activePlayer.getGridCoordinates().getRow() &&
                    !player.getId().equals(activePlayer.getId())) {
                player.setAlive(false);
                GameNotification.getInstance().showNotification(player.getName() + " killed by " + activePlayer.getName());
                deadPlayer = player;
            }
        }
        return Optional.ofNullable(deadPlayer);
    }

    private void startNewRound() {

    }

    private void drawControls() {
        controlsRenderer.begin(ShapeRenderer.ShapeType.Line);
        controlsRenderer.setColor(1, 1, 1, 1);
        controlsRenderer.setProjectionMatrix(camera.combined);

        for (int cell = 0; cell < AMOUNT_OF_MOVES; cell++) {
            controlsRenderer.rect(10, (cell * CONTROL_CELL_SIZE) + 10, CONTROL_CELL_SIZE, CONTROL_CELL_SIZE);
        }

        controlsRenderer.end();
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
        gridRenderer.dispose();
        batch.dispose();
        GameNotification.getInstance().dispose();
    }
}
