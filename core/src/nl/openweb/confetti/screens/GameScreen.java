package nl.openweb.confetti.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import nl.openweb.confetti.ConfettiGame;
import nl.openweb.confetti.database.Database;
import nl.openweb.confetti.dialog.GameNotification;
import nl.openweb.confetti.model.GridCell;
import nl.openweb.confetti.model.Move;
import nl.openweb.confetti.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;
import static nl.openweb.confetti.model.Player.AMOUNT_OF_MOVES;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final ConfettiGame game;
    private final Camera camera;
    private final ShapeRenderer gridRenderer;
    private final ShapeRenderer controlsRenderer;
    private final SpriteBatch controlsImagesRenderer;
    private final float gridStartX;
    private final float gridStartY;
    private final List<GridCell> gridCells;
    private final List<Player> players;
    private final GameNotification gameNotification;
    private final int CONTROL_CELL_SIZE = 55;
    private final AtomicInteger activePlayer = new AtomicInteger(0);

    public GameScreen(ConfettiGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = game.getCamera();
        this.gridRenderer = new ShapeRenderer();
        this.controlsRenderer = new ShapeRenderer();
        this.controlsImagesRenderer = new SpriteBatch();
        this.gridStartX = game.getCenterX() - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
        this.gridStartY = game.getCenterY() - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
        this.gridCells = new ArrayList<>();
        this.gameNotification = new GameNotification(game, "Testing", 2, 400, 280, () -> System.out.println("Closed"));

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                int currentPlayerIndex = activePlayer.get();
                int playersAlive = players.stream().filter(Player::isAlive).toList().size() - 1;

                Player currentPlayer = players.get(currentPlayerIndex);
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
                        if (currentPlayerIndex == playersAlive) {
                            activePlayer.set(0);
                            GameScreen.this.gameNotification.setText("Showtime!");
                            GameScreen.this.gameNotification.setDialogEvent(() -> new Thread(() -> applyAllPlayerMoves()).start());
                        } else {
                            activePlayer.incrementAndGet();
                            String playerName = GameScreen.this.players.get(activePlayer.get()).getName();
                            GameScreen.this.gameNotification.setText(playerName);
                        }
                    }
                }
                return false;
            }
        });

        players = Database.getInstance().getPlayers();
        Database.getInstance().addMoves(players.get(0), List.of(new Move(players.get(0).getId(), 1, 1, 0)));
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

        String playerName = GameScreen.this.players.get(activePlayer.get()).getName();
        GameScreen.this.gameNotification.setText(playerName);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawGrid();
        drawPlayers();

        this.gameNotification.drawNotification();
        drawControls();
        drawControlImages();
    }

    private void drawPlayers() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        players.stream().filter(Player::isAlive).forEach(player -> {
            GridCell playerGridCell = getPlayerGridCell(player);
            if (!player.isDead()) {
                batch.draw(player.getSpriteTexture(), player.getCellDrawXPosition(playerGridCell), player.getCellDrawYPosition(playerGridCell));
            }
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

       /* gridRenderer.setColor(0, 1, 0, 1);
        gridRenderer.rect(1, 1, game.getViewport().getWorldWidth() - 1, game.getViewport().getWorldHeight() - 1);*/

        gridRenderer.end();
    }

    private void drawControlImages() {
        Player player = players.get(activePlayer.get());
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
        for (int i = 0; i < AMOUNT_OF_MOVES; i++) {
            players.forEach(player -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Apply move: " + player.getName());
                        player.applyMove(player.popMove());
                    }
            );
        }
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
        this.gameNotification.dispose();
    }
}
