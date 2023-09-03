import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Stage {

    private Rectangle2D.Double[][] stage = new Rectangle2D.Double[10][10];

    public Stage(int[][] stage) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (stage[r][c] == 1) {
                    this.stage[r][c] = new Obstacle(c, r);
                }
                else if (stage[r][c] == 2) {
                    this.stage[r][c] = new SpeedBoost(c, r);
                }
                else if (stage[r][c] == 3) {
                    this.stage[r][c] = new Obstacle(c, r, true);
                }
            }
        }
    }

    public Rectangle2D.Double getObject(int row, int column) {
        return this.stage[row][column];
    }

    public void spawnBoost(int row, int column) {
        this.stage[row][column] = new SpeedBoost(column, row);
    }

    public void deleteObject(int row, int column) {
        this.stage[row][column] = null;
    }

    public void drawStage(Graphics2D g2d) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (stage[r][c] != null) {
                    if (stage[r][c].toString().equals("Obstacle") || stage[r][c].toString().equals("EvaderObstacle")) ((Obstacle) stage[r][c]).drawObstacle(g2d);
                    else if (stage[r][c].toString().equals("SpeedBoost")) ((SpeedBoost) stage[r][c]).drawSpeedBoost(g2d);
                }
            }
        }
    }
}