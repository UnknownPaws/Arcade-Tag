import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Obstacle extends Rectangle2D.Double {

    private Line2D topBorder, leftBorder, rightBorder, bottomBorder;
    private boolean evaderObstacle;

    public Obstacle(int xCoord, int yCoord) {
        super(xCoord*120, yCoord*78+120, 120, 78);
        topBorder = new Line2D.Double(x, y, x+120, y);
        bottomBorder = new Line2D.Double(x, y+78, x+120, y+78);
        leftBorder = new Line2D.Double(x, y+78, x, y);
        rightBorder = new Line2D.Double(x+120, y+78, x+120, y);
    }

    public Obstacle(int xCoord, int yCoord, boolean evaderObstacle) {
        this(xCoord, yCoord);
        this.evaderObstacle = evaderObstacle;
    }

    public Line2D getTopBorder() {
        return topBorder;
    }

    public Line2D getLeftBorder() {
        return leftBorder;
    }

    public Line2D getRightBorder() {
        return rightBorder;
    }

    public Line2D getBottomBorder() {
        return bottomBorder;
    }

    public String toString() {
        return !evaderObstacle ? "Obstacle" : "EvaderObstacle";
    }

    public void drawObstacle(Graphics2D g2d) {
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(this);
        if (this.evaderObstacle) g2d.setPaint(Color.GRAY);
        g2d.fill(this);
    }
}
