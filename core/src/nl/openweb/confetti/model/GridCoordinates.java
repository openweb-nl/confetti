package nl.openweb.confetti.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.openweb.confetti.GridManager;

import java.awt.geom.Point2D;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridCoordinates {
    protected int row;
    protected int column;

    public GridCoordinates applyMove(Move move) {
        column += move.getDeltaX();
        row += move.getDeltaY();
        return this;
    }

    public void setCoordinates(GridCoordinates coordinates) {
        this.column = coordinates.getColumn();
        this.row = coordinates.getRow();
    }

    public Point2D getXYCenterCoordinates() {
        return GridManager.getInstance().getCellCenterCoordinates(this);
    }
}
