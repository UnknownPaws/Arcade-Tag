import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HUD extends Rectangle2D.Double {

    public HUD() {
        super(0, 0, Main.WIDTH, 120);
    }

    public void drawUI(int player1Score, int player2Score, boolean player1IsTagger, int timer, long boostTimer, boolean musicMuted, Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fill(this);
        writeText(Integer.toString(player1Score), 30, 50, 40, Color.RED, g2d);
        writeText(Integer.toString(player2Score), 100, 50, 40, Color.BLUE, g2d);
        writeText(player1IsTagger ? "RED" : "BLUE", 600, 60, 60, player1IsTagger ? Color.RED : Color.BLUE, g2d);
        writeText(Integer.toString(30-timer), Main.WIDTH-30, 50, 40, Color.WHITE, g2d);
        writeText("Tagger:", 601, 0, 20, Color.GRAY, g2d);
        writeText("Score:", 65, 0, 20, Color.GRAY, g2d);
        writeText("Time left:", Main.WIDTH-50, 0, 20, Color.GRAY, g2d);
        g2d.setColor(Color.YELLOW);
        g2d.fill(new Rectangle2D.Double(225, 52.5, 200*((4000-(boostTimer > 4000 ? 4000 : boostTimer))/4000.0), 30));
        g2d.draw(new Rectangle2D.Double(225, 52.5, 200, 30));
        writeText("Evader boost:", 325, 0, 20, Color.GRAY, g2d);
        if (musicMuted) writeText("MUSIC MUTED", 900, 40, 30, Color.ORANGE, g2d);
        else writeText("MUSIC MUTED", 900, 40, 30, Color.BLACK, g2d);
    }

    public void writeText(String s, int x, int y, int textSize, Color color, Graphics2D g2d) {
        Font font = new Font("SansSerif", Font.PLAIN, textSize);
        Rectangle2D textBox = font.getStringBounds(s, g2d.getFontRenderContext());
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(s, (int) (x - textBox.getWidth() / 2), y+30);
    }

}
