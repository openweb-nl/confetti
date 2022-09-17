package nl.openweb.confetti.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import nl.openweb.confetti.ConfettiGame;

public class GameNotification {
    private static final int MARGIN = 10;

    private final ShapeRenderer notificationRenderer;
    private final ConfettiGame game;
    private final String text;
    private final SpriteBatch batch;
    private final float duration;
    private final int width;
    private final int height;
    private final float startX;
    private final float startY;
    private BitmapFont font;

    private float timePassed;

    public GameNotification(ConfettiGame game, String text, int duration, int width, int height) {
        this.game = game;
        this.text = text;
        this.duration = duration;
        this.width = width;
        this.height = height;
        this.startX = game.getCenterX() - (width / 2f);
        this.startY = game.getCenterY() - (height / 2f);
        this.notificationRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.createBitmapFont();
    }

    public void drawNotification() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        timePassed += deltaTime;

        if (timePassed < duration) {
            notificationRenderer.begin(ShapeRenderer.ShapeType.Filled);
            notificationRenderer.setColor(0.1f, 0.1f, .1f, 1);
            notificationRenderer.setProjectionMatrix(game.getCamera().combined);
            notificationRenderer.rect(startX, startY, width, height);
            notificationRenderer.setColor(1, 0, 0, 1);
            notificationRenderer.line(startX + MARGIN, startY + MARGIN, startX + width - MARGIN, startY + MARGIN);
            notificationRenderer.line(startX + MARGIN, startY + MARGIN, startX + MARGIN, startY + height - MARGIN);
            notificationRenderer.line(startX + MARGIN, startY + height - MARGIN, startX + width - MARGIN, startY + height - MARGIN);
            notificationRenderer.line(startX + width - MARGIN, startY + height - MARGIN, startX + width - MARGIN, startY + MARGIN);
            notificationRenderer.end();

            batch.begin();
            batch.setProjectionMatrix(game.getCamera().combined);
            font.draw(batch, "Player 1 start!", game.getCenterX(), game.getCenterY() + 16, 0f, Align.center, false);
            batch.end();
        }
    }

    private void createBitmapFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 32;
        parameter.borderWidth = 1;
        parameter.color = Color.YELLOW;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);

        font = generator.generateFont(parameter);
    }

    public void dispose() {
        notificationRenderer.dispose();
        batch.dispose();
    }
}
