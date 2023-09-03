import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SpeedBoost extends Rectangle2D.Double {

    public SpeedBoost(int xCoord, int yCoord) {
        super(xCoord*120+60, yCoord*78+159, 30, 30);
    }

    public String toString() {
        return "SpeedBoost";
    }

    public void drawSpeedBoost(Graphics2D g2d) {
        g2d.setPaint(Color.YELLOW);
        g2d.fill(this);
        g2d.draw(this);
    }
}
