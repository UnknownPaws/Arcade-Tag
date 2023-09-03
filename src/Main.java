import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public static final int HEIGHT = 900;
    public static final int WIDTH = 1200;

    public Main() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT+30); //The window bar at the top accounts for 30 pixels so to make the game view HEIGHT pixels high, 30 pixels are added to the entire window's height
        this.setResizable(false);
        this.setTitle("Tag Game");

        // center the window on the viewing screen
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) screenDim.getWidth() / 2 - WIDTH / 2, (int) screenDim.getHeight() / 2 - HEIGHT / 2);

        // create a panel component and add it to the window
        this.add(new GamePanel());
    }


    public static void main(String[] args) {
        Main game = new Main();
        game.setVisible(true);
    }

}