import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Player extends Ellipse2D.Double {

    public static final int SIZE = 50;

    private Color colour;
    private double[] speed;

    public Player(Color color, int startX, int startY) {
        super(startX,startY, SIZE, SIZE);
        this.colour = color;
        speed = new double[2];
    }

    public void drawPlayer(Graphics2D g2d, boolean isTagger) {
        g2d.setPaint(colour);
        g2d.fill(this);
        if (isTagger) g2d.setPaint(Color.BLACK);
        else g2d.setPaint(Color.WHITE);
        g2d.draw(this);
    }

    public void drawPlayer(Graphics2D g2d, int playerX, int playerY, boolean isTagger) {
        g2d.setPaint(colour);
        g2d.fill(this);
        if (isTagger) g2d.setPaint(Color.BLACK);
        else g2d.setPaint(Color.WHITE);
        g2d.draw(this);
        if (isTagger) writeText("T", playerX + 25, playerY + 12, 40, Color.WHITE, g2d);
    }

    public void writeText(String s, int x, int y, int textSize, Color color, Graphics2D g2d) {
        Font font = new Font("SansSerif", Font.PLAIN, textSize);
        Rectangle2D textBox = font.getStringBounds(s, g2d.getFontRenderContext());
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(s, (int) (x - textBox.getWidth() / 2), y+30);
    }

    public void update() {
        x += speed[0];
        y += speed[1];

    }

    public double getSpeedX() {
        return speed[0];
    }

    public double getSpeedY() {
        return speed[1];
    }

    public void setSpeed(double speedX, double speedY) {
        this.speed[0] = speedX;
        this.speed[1] = speedY;
    }

}