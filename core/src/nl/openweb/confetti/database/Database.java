package nl.openweb.confetti.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import nl.openweb.confetti.model.GridCell;
import nl.openweb.confetti.model.Move;
import nl.openweb.confetti.model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {
    private static Database database;
    private Connection conn = null;

    public static Database getInstance() {
        if (database == null) {
            database = new Database();
        }

        return database;
    }

    private Database() {
    }

    public void connect() {

        try {
            // db parameters
            String url = "jdbc:sqlite::memory:";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println(conn);

            System.out.println("Connection to SQLite has been established.");

            createTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTables() throws SQLException {
        Statement statement = conn.createStatement();

        boolean execute = statement.execute("CREATE TABLE PLAYERS (id VARCHAR(36) PRIMARY KEY, name VARCHAR(20), texture VARCHAR(50), position VARCHAR(5))");
        addPlayer("Blue player", "0-0", "blue-player.png");
        addPlayer("Red player", (GridCell.GRID_DIMENSION - 1) + "-0", "red-player.png");
        addPlayer("Green player", "0-" + (GridCell.GRID_DIMENSION - 1), "green-player.png");
        addPlayer("Yellow player", (GridCell.GRID_DIMENSION - 1) + "-" + (GridCell.GRID_DIMENSION - 1), "yellow-player.png");

        statement.execute("CREATE TABLE MOVES (player_id VARCHAR(36), sequence INTEGER, delta_x INTEGER, delta_y INTEGER)");
    }

    private void addPlayer(String playerName, String position, String textureFilename) throws SQLException {
        Statement player = conn.createStatement();
        String playerId = UUID.randomUUID().toString();
        player.execute("INSERT INTO PLAYERS (id, name, texture, position) VALUES ('" + playerId + "', '" + playerName + "', '" + textureFilename + "', '" + position + "')");
    }

    public void addMoves(Player player, List<Move> moves) {
        final AtomicInteger moveId = new AtomicInteger(0);
        moves.forEach(move -> {
            Statement addMovesStatement;
            try {
                addMovesStatement = conn.createStatement();
                addMovesStatement.execute("INSERT INTO MOVES (player_id, sequence, delta_x, delta_y) VALUES ('" + player.getId() + "', '" + moveId.incrementAndGet() + "', '" + move.getDeltaX() + "', '" + move.getDeltaY() + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Move> getMoves(String playerId) {
        List<Move> moves = new ArrayList<>();

        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM MOVES WHERE player_id='" + playerId + "'");
            while (resultSet.next()) {
                Move move = new Move(
                        resultSet.getString("player_id"),
                        resultSet.getInt("sequence"),
                        resultSet.getInt("delta_x"),
                        resultSet.getInt("delta_y")
                );
                moves.add(move);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return moves;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM PLAYERS");
            while (resultSet.next()) {
                String[] positions = resultSet.getString("position").split("-");
                Texture texture = new Texture(Gdx.files.internal(resultSet.getString("texture")));

                Player player = new Player(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        texture,
                        Integer.parseInt(positions[0]),
                        Integer.parseInt(positions[1])
                );

                players.add(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return players;
    }
}
