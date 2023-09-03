import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Objects;

public class GamePanel extends JPanel {

    private Image background = null;
    private boolean musicMuted;
    private Player player1, player2;
    private HUD HUD;
    private boolean player1IsTagger;
    private int player1Score, player2Score;
    private long roundStart;
    private boolean evaderBoost;
    private long boostStart;
    private long boostSpawn;
    private double speedValue1, speedValue2;
    private Stage currentStage;
    private int[][] currentMap;



    public GamePanel() {
        JOptionPane.showMessageDialog(null, "Make sure you play this game in two!\n" +
                        "Red Player - WASD Keys | Blue Player - Arrow Keys\n\n" +
                        "Every time the tagger tags the evader, the tagger scores a point, but if the evader does not get tagged\n" +
                        "for 30 seconds (time left is shown at the top right of the screen) then the evader gets a point.\n" +
                        "The Tagger is shown with a big T and at the top of the screen. The score is shown at the top left of the\n" +
                        "screen in the respective colours. There are yellow squares around the field that only the evader can pick\n" +
                        "up which gives the evader a 4 second boost making them quicker than the tagger. This is important for the evader\n" +
                        "as normally the tagger is faster than the evader. The light grey obstacles are obstacles that only the tagger\n" +
                        "can vault over and the evader can not go over them allowing the tagger to take shortcuts.\n\n" +
                        "Skip a level at any time using Shift + P, this will not grant any player points and will reset the time.\n" +
                        "Mute/Unmute the background music by pressing M",
                "Tag Instructions", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Objects.requireNonNull(getClass().getResource("logo.png"))));
        player1 = new Player(Color.RED, (int) (Main.WIDTH*0.25), (int) (Main.HEIGHT*0.25));
        player2 = new Player(Color.BLUE, (int) (Main.WIDTH*0.75), (int) (Main.HEIGHT*0.75));
        player1IsTagger = Math.random() < 0.5;
        System.out.println("Tagger is " + (player1IsTagger ? "red" : "blue"));
        roundStart = System.currentTimeMillis();
        boostSpawn = System.currentTimeMillis();

        try {
            StdAudio.playInBackground("backgroundMusic.wav");
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not find background music file", "Error", JOptionPane.ERROR_MESSAGE);
        }

        HUD = new HUD();
        currentMap = new int[10][10];
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                currentMap[r][c] = Stages.STAGE_ONE[r][c];
            }
        }
        currentStage = new Stage(Stages.STAGE_ONE);
        randomSpawnBoost();

        try {
            background = ImageIO.read(Objects.requireNonNull(getClass().getResource("grass.jpg")));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There is no background image file that can be found", "Error", JOptionPane.ERROR_MESSAGE);
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    player2.setSpeed(player2.getSpeedX(), speedValue2);
                } if (e.getKeyCode() == KeyEvent.VK_UP) {
                    player2.setSpeed(player2.getSpeedX(), -speedValue2);
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    player2.setSpeed(-speedValue2, player2.getSpeedY());
                } if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    player2.setSpeed(speedValue2, player2.getSpeedY());
                }


                if (e.getKeyCode() == KeyEvent.VK_S) {
                    player1.setSpeed(player1.getSpeedX(), speedValue1);
                } if (e.getKeyCode() == KeyEvent.VK_W) {
                    player1.setSpeed(player1.getSpeedX(), -speedValue1);
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    player1.setSpeed(-speedValue1, player1.getSpeedY());
                } if (e.getKeyCode() == KeyEvent.VK_D) {
                    player1.setSpeed(speedValue1, player1.getSpeedY());
                }

                repaint();
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP)
                    player2.setSpeed(player2.getSpeedX(), 0);
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
                    player2.setSpeed(0, player2.getSpeedY());

                if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_W)
                    player1.setSpeed(player1.getSpeedX(), 0);
                if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D)
                    player1.setSpeed(0, player1.getSpeedY());

                repaint();
            }

            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'P') {
                    newRound();
                }
                else if (e.getKeyChar() == 'm') {
                    musicMuted = !musicMuted;
                    if (musicMuted) StdAudio.stopInBackground();
                    else StdAudio.playInBackground("backgroundMusic.wav");
                }
            }
        });

        Timer timer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                repaint();
            }
        });
        timer.start();
        this.setFocusable(true);

        Timer musicTimer = new Timer(32500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!musicMuted) {
                    try {
                        StdAudio.playInBackground("backgroundMusic.wav");
                    }
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(null, "Could not find background music file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        musicTimer.start();
    }



    private void updateGame() {
        Line2D leftBorder, rightBorder, topBorder, bottomBorder;
        leftBorder = new Line2D.Double(0, 0, 0, Main.HEIGHT);
        rightBorder = new Line2D.Double(Main.WIDTH, 0, Main.WIDTH, Main.HEIGHT);
        topBorder = new Line2D.Double(0, 120, Main.WIDTH, 120);
        bottomBorder = new Line2D.Double(0, Main.HEIGHT, Main.WIDTH, Main.HEIGHT);

        //The diagonal speed will be faster because the horizontal and vertical velocity vector will form a triangle where the velocity vector is the hypotenuse which will be higher than speedValue
        if (!player1IsTagger && evaderBoost){
            speedValue1 = 1.4;
            player1.setSpeed(player1.getSpeedX()<0 ? speedValue1*-1 : (player1.getSpeedX()==0 ? 0 : speedValue1), player1.getSpeedY()<0 ? speedValue1*-1 : (player1.getSpeedY()==0 ? 0 : speedValue1));
        }
        else if (player1IsTagger) {
            speedValue1 = 1.2;
        }
        else {
            speedValue1 = 1;
            player1.setSpeed(player1.getSpeedX()<0 ? speedValue1*-1 : (player1.getSpeedX()==0 ? 0 : speedValue1), player1.getSpeedY()<0 ? speedValue1*-1 : (player1.getSpeedY()==0 ? 0 : speedValue1));

        }

        if (player1IsTagger && evaderBoost) {
            speedValue2 = 1.4;
            player2.setSpeed(player2.getSpeedX()<0 ? speedValue2*-1 : (player2.getSpeedX()==0 ? 0 : speedValue2), player2.getSpeedY()<0 ? speedValue2*-1 : (player2.getSpeedY()==0 ? 0 : speedValue2));

        }
        else if (!player1IsTagger) {
            speedValue2 = 1.2;
        }
        else {
            speedValue2 = 1;
            player2.setSpeed(player2.getSpeedX()<0 ? speedValue2*-1 : (player2.getSpeedX()==0 ? 0 : speedValue2), player2.getSpeedY()<0 ? speedValue2*-1 : (player2.getSpeedY()==0 ? 0 : speedValue2));
        }

        checkBorderCollisions(player1, leftBorder, rightBorder, topBorder, bottomBorder);
        checkBorderCollisions(player2, leftBorder, rightBorder, topBorder, bottomBorder);
        checkRunnerWin();
        checkPlayerCollisions();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Rectangle2D.Double obs = currentStage.getObject(r, c);
                if (obs != null) {
                    if (obs.toString().equals("Obstacle")) {
                        checkObstacleCollisions(player1, (Obstacle) obs);
                        checkObstacleCollisions(player2, (Obstacle) obs);
                    }
                    else if (obs.toString().equals("SpeedBoost")) {
                        checkSpeedBoostCollisions((SpeedBoost) obs, r, c);
                    }
                    else if (obs.toString().equals("EvaderObstacle")) {
                        checkObstacleCollisions(player1IsTagger ? player2 : player1, (Obstacle) obs);
                    }
                }
            }
        }

        evaderBoost = System.currentTimeMillis()-boostStart < 4000;
        if (System.currentTimeMillis()-boostSpawn > 8000) {
            boostSpawn = System.currentTimeMillis();
            randomSpawnBoost();
        }

        player1.update();
        player2.update();

    }


    private void checkBorderCollisions(Player player, Line2D leftBorder, Line2D rightBorder, Line2D topBorder, Line2D bottomBorder) {
        if (bottomBorder.intersects(player.getBounds2D())) {
            player.y = bottomBorder.getY1()-Player.SIZE;
        }
        else if (topBorder.intersects(player.getBounds2D())) {
            player.y = topBorder.getY1();
        }
        if (leftBorder.intersects(player.getBounds2D())) {
            player.x = leftBorder.getX1();
        }
        else if(rightBorder.intersects(player.getBounds2D())) {
            player.x = rightBorder.getX1()-Player.SIZE;
        }
    }

    private void checkObstacleCollisions(Player player, Obstacle obs) {
        while (obs.getBottomBorder().intersects(player.getBounds2D()) && player.y > obs.getBottomBorder().getY1()-5) {
            player.y = obs.getBottomBorder().getY1()+1;
        }
        while (obs.getTopBorder().intersects(player.getBounds2D()) && player.y + Player.SIZE < obs.getTopBorder().getY1()+5) {
            player.y = obs.getTopBorder().getY1()-Player.SIZE-1;
        }
        while (obs.getLeftBorder().intersects(player.getBounds2D()) && player.x + Player.SIZE < obs.getLeftBorder().getX1()+5) {
            player.x = obs.getLeftBorder().getX1()-Player.SIZE-1;
        }
        while (obs.getRightBorder().intersects(player.getBounds2D()) && player.x > obs.getRightBorder().getX1()-5) {
            player.x = obs.getRightBorder().getX1()+1;
        }
    }

    private void checkSpeedBoostCollisions(SpeedBoost spdBst, int row, int column) {
        if ((player1.intersects(spdBst) && !player1IsTagger) || (player2.intersects(spdBst) && player1IsTagger)) {
            evaderBoost = true;
            boostStart = System.currentTimeMillis();
            currentStage.deleteObject(row, column);
            try {
                StdAudio.playInBackground("speedBoost.wav");
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "There is no speed boost sound effect", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void checkRunnerWin() {
        if (roundTime() >= 30) {
            player1Score += player1IsTagger ? 0 : 1;
            player2Score += !player1IsTagger ? 0 : 1;
            try {
                StdAudio.playInBackground("evaderWin.wav");
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "There is no speed boost sound effect", "Error", JOptionPane.ERROR_MESSAGE);
            }
            newRound();
        }
    }


    private void checkPlayerCollisions() {
        if (player1.intersects(player2.getBounds2D())) {
            player1Score += player1IsTagger ? 1 : 0;
            player2Score += !player1IsTagger ? 1 : 0;
            try {
                StdAudio.playInBackground("taggerWin.wav");
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "There is no speed boost sound effect", "Error", JOptionPane.ERROR_MESSAGE);
            }
            newRound();
        }
    }


    private void randomSpawnBoost() {
        int count = 0;
        for (int[] row : currentMap) {
            for (int element : row) {
                if (element == 0) count++;
            }
        }

        int[] randCoords = {(int) (Math.random() * 10), (int) (Math.random() * 10)};
        if (count > 0) {
            while(currentMap[randCoords[0]][randCoords[1]] != 0) {
                randCoords[0] = (int) (Math.random() * 10);
                randCoords[1] = (int) (Math.random() * 10);
            }
        }
        else return;
        currentMap[randCoords[0]][randCoords[1]] = 2;
        currentStage.spawnBoost(randCoords[0], randCoords[1]);
    }


    private void newRound() {
        boostStart = 0;
        evaderBoost = false;
        System.out.println(player1Score + " : " + player2Score);
        player1IsTagger = !player1IsTagger;
        System.out.println("Tagger is " + (player1IsTagger ? "red" : "blue"));
        resetTime();
        boostSpawn = System.currentTimeMillis();
        int[][] nextStage = Stages.randomStage();
        currentStage = new Stage(nextStage);
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                currentMap[r][c] = nextStage[r][c];
            }
        }

        int startPos1 = (int) (Math.random()*4)+1;
        int startPos2 = startPos1;
        while(startPos1 == startPos2) startPos2 = (int) (Math.random()*4)+1;

        switch (startPos1) {
            case 2:
                player1 = new Player(Color.RED, (int) (Main.WIDTH*0.25), (int) (Main.HEIGHT*0.25));
                break;
            case 3:
                player1 = new Player(Color.RED, (int) (Main.WIDTH*0.25), (int) (Main.HEIGHT*0.75));
                break;
            case 4:
                player1 = new Player(Color.RED, (int) (Main.WIDTH*0.75), (int) (Main.HEIGHT*0.75));
                break;
            default:
                player1 = new Player(Color.RED, (int) (Main.WIDTH*0.75), (int) (Main.HEIGHT*0.25));
                break;
        }

        switch (startPos2) {
            case 2:
                player2 = new Player(Color.BLUE, (int) (Main.WIDTH*0.25), (int) (Main.HEIGHT*0.25));
                break;
            case 3:
                player2 = new Player(Color.BLUE, (int) (Main.WIDTH*0.25), (int) (Main.HEIGHT*0.75));
                break;
            case 4:
                player2 = new Player(Color.BLUE, (int) (Main.WIDTH*0.75), (int) (Main.HEIGHT*0.75));
                break;
            default:
                player2 = new Player(Color.BLUE, (int) (Main.WIDTH*0.75), (int) (Main.HEIGHT*0.25));
                break;
        }
        randomSpawnBoost();
    }


    public int roundTime() {
        return (int) ((System.currentTimeMillis()-roundStart)/1000);
    }


    public void resetTime() {
        roundStart = System.currentTimeMillis();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawBackground(g2d);

        currentStage.drawStage(g2d);
        HUD.drawUI(player1Score, player2Score, player1IsTagger, roundTime(), System.currentTimeMillis()-boostStart, musicMuted, g2d);
        player1.drawPlayer(g2d, (int) player1.x, (int) player1.y, player1IsTagger);
        player2.drawPlayer(g2d, (int) player2.x, (int) player2.y, !player1IsTagger);
    }


    public void drawBackground(Graphics2D g2d) {
        g2d.drawImage(background, 0, 0, Main.WIDTH, Main.HEIGHT, null);
    }
}