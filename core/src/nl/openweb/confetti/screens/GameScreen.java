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

import static nl.openweb.confetti.GridManager.PLAYER_MOVE_DELAY;
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
    private final GameNotification gameNotification;
    private final int CONTROL_CELL_SIZE = 55;
    //private final AtomicInteger activePlayer = new AtomicInteger(0);

    public GameScreen(ConfettiGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = game.getCamera();
        this.gridRenderer = new ShapeRenderer();
        this.controlsRenderer = new ShapeRenderer();
        this.controlsImagesRenderer = new SpriteBatch();
        this.gameNotification = new GameNotification(game, "Testing", 2, 400, 280, () -> System.out.println("Closed"));
        this.stage = new Stage(game.getViewport(), batch);

        GridManager.getInstance().init(game.getCenterX(), game.getCenterY());

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
                            GameScreen.this.gameNotification.setText("Showtime!");
                            GameScreen.this.gameNotification.setDialogEvent(() -> new Thread(() -> applyAllPlayerMoves()).start());
                        } else {
                            GameScreen.this.gameNotification.setText(nextActivePlayer.getName());
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
        GameScreen.this.gameNotification.setText(playerName);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawGrid();
        drawPlayers();
        drawControls();
        drawControlImages();

        this.gameNotification.drawNotification();
    }

    private void drawPlayers() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        /*GridManager.getInstance().getPlayers().stream().filter(PlayerActor::isAlive).forEach(player -> {
            GridCell playerGridCell = GridManager.getInstance().getPlayerGridCell(player);
            if (!player.isDead()) {
                batch.draw(player.getSpriteTexture(), player.getCellDrawXPosition(playerGridCell), player.getCellDrawYPosition(playerGridCell));
            }
        });*/
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
        /*long playersAlive = GridManager.getInstance().getPlayers().stream().filter(PlayerActor::isAlive).count();

        PlayerActor activePlayer = GridManager.getInstance().getActivePlayer();
        while (activePlayer != null) {
            *//*try {
                Thread.sleep(PLAYER_MOVE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GridManager.getInstance().performPlayerMove();
            Optional deadPlayer = hitPlayer(activePlayer);

            if (deadPlayer.isPresent()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            activePlayer = GridManager.getInstance().getNextActivePlayer(true);*//*

        }*/

       /* if (playersAlive > 1) {
            for (int i = 0; i < AMOUNT_OF_MOVES; i++) {
                GridManager.getInstance().getPlayers().stream().filter(Player::isAlive).forEach(player -> {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //activePlayer.set(GridManager.getInstance().getPlayers().indexOf(player));
                            GridManager.getInstance().setActivePlayer(player);
                            Move move = player.popMove();
                            if (move != null) {
                                player.applyMove(move);
                                hitPlayer(player);
                            }
                        }
                );
            }

            this.gameNotification.setText("Next round!");
            this.gameNotification.setDialogEvent(this::startNewRound);
        } else {
            this.gameNotification.setText(GridManager.getInstance().getPlayers().stream().filter(Player::isAlive).toList().get(0).getName() + " has WON!");
        }*/
    }

    private Optional hitPlayer(PlayerActor activePlayer) {
        PlayerActor deadPlayer = null;
        for (PlayerActor player : GridManager.getInstance().getPlayers()) {
            if (player.getGridCoordinates().getColumn() == activePlayer.getGridCoordinates().getColumn() &&
                    player.getGridCoordinates().getRow() == activePlayer.getGridCoordinates().getRow() &&
                    !player.getId().equals(activePlayer.getId())) {
                player.setAlive(false);
                gameNotification.setText(player.getName() + " killed by " + activePlayer.getName());
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
        this.gameNotification.dispose();
    }
}
