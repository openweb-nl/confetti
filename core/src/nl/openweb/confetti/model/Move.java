package nl.openweb.confetti.model;

public class Move {
    String playerId;
    int sequence;
    int deltaX;
    int deltaY;

    public Move(String playerId, int sequence, int deltaX, int deltaY) {
        this.playerId = playerId;
        this.sequence = sequence;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }
}
