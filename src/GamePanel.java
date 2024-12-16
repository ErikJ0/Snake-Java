import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_WIDTH) / UNIT_SIZE;
    static final int DELAY = 75;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    final int x2[] = new int[GAME_UNITS];
    final int y2[] = new int[GAME_UNITS];

    int snakeParts = 6;
    int snakeParts2 = 6;

    int applesEaten;
    int appleX;
    int appleY;

    char direction = 'R';
    char direction2 = 'L';

    boolean running = false;
    Timer timer;
    Random random;
    JButton singleplayerButton;
    JButton multiplayerButton;
    JLabel title;
    boolean singleplayer;
    boolean end;
    boolean MM = true;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new KeyInputAdapter());
        this.setLayout(new GridBagLayout());
        MainMenu();
    }

    public void MainMenu() {
        if (MM) {
            title = new JLabel("SNAKE");
            title.setFont(new Font("Consolas", Font.PLAIN, 120));
            title.setHorizontalAlignment(JLabel.CENTER);
            title.setForeground(Color.green);

            singleplayerButton = new JButton("SinglePlayer");
            multiplayerButton = new JButton("Multiplayer");

            singleplayerButton.addActionListener(this);
            multiplayerButton.addActionListener(this);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(50, 0, 50, 0);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            add(title, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            add(singleplayerButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(multiplayerButton, gbc);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        if (e.getSource() == singleplayerButton) {
            singleplayer = true;
            MM = false;
            this.remove(singleplayerButton);
            this.remove(multiplayerButton);
            this.remove(title);
            this.repaint();
            startGame();
        } else if (e.getSource() == multiplayerButton) {
            singleplayer = false;
            MM = false;
            this.remove(singleplayerButton);
            this.remove(multiplayerButton);
            this.remove(title);
            this.repaint();
            initializeMultiplayer();
            startGame();
        }
        repaint();
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            snakeParts++;
            applesEaten++;
            newApple();
        }
        if (!singleplayer && (x2[0] == appleX) && (y2[0] == appleY)) {
            snakeParts2++;
            applesEaten++;
            newApple();
        }
    }

    public void startGame() {
        this.setFocusable(true);
        this.requestFocusInWindow();
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void initializeMultiplayer() {
        // Initialize first snake in top left corner
        for (int i = 0; i < snakeParts; i++) {
            x[i] = UNIT_SIZE * (snakeParts - i);
            y[i] = 0;
        }

        // Initialize second snake in bottom right corner
        for (int i = 0; i < snakeParts2; i++) {
            x2[i] = SCREEN_WIDTH - UNIT_SIZE * (snakeParts2 - i);
            y2[i] = SCREEN_HEIGHT - UNIT_SIZE;
        }

        direction = 'R'; // First snake moves to the right
        direction2 = 'L'; // Second snake moves to the left
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snakeParts; i++) {
                if (i == 0) {
                    g.setColor(Color.blue);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            if (!singleplayer) {
                for (int i = 0; i < snakeParts2; i++) {
                    if (i == 0) {
                        g.setColor(Color.orange);
                        g.fillRect(x2[i], y2[i], UNIT_SIZE, UNIT_SIZE);
                    } else {
                        g.setColor(Color.yellow);
                        g.fillRect(x2[i], y2[i], UNIT_SIZE, UNIT_SIZE);
                    }
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Apples Eaten: " + applesEaten,
                    (SCREEN_WIDTH - metrics.stringWidth("Apples Eaten: " + applesEaten)) / 2,
                    g.getFont().getSize());
        } else if (end) {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = snakeParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        for (int i = snakeParts2; i > 0; i--) {
            x2[i] = x2[i - 1];
            y2[i] = y2[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

        if (!singleplayer) {
            switch (direction2) {
                case 'U':
                    y2[0] = y2[0] - UNIT_SIZE;
                    break;
                case 'D':
                    y2[0] = y2[0] + UNIT_SIZE;
                    break;
                case 'L':
                    x2[0] = x2[0] - UNIT_SIZE;
                    break;
                case 'R':
                    x2[0] = x2[0] + UNIT_SIZE;
                    break;
            }
        }
    }

    public void checkCollision() {
        /*for (int i = snakeParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                end = true;
            }
        }

        if (!singleplayer) {
            for (int i = snakeParts2; i > 0; i--) {
                if ((x2[0] == x2[i]) && (y2[0] == y2[i])) {
                    running = false;
                    end = true;
                }
            }
        }

        if (x[0] < 0 || y[0] > SCREEN_HEIGHT || x[0] > SCREEN_WIDTH || y[0] < 0) {
            running = false;
            end = true;
        }
        if (!singleplayer && (x2[0] < 0 || y2[0] > SCREEN_HEIGHT || x2[0] > SCREEN_WIDTH || y2[0] < 0)) {
            running = false;
            end = true;
        }

        if (!running) {
            timer.stop();
        }*/
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics appleMetrics = getFontMetrics(g.getFont());
        g.drawString("Apples Eaten: " + applesEaten,
                (SCREEN_WIDTH - appleMetrics.stringWidth("Apples Eaten: " + applesEaten)) / 2,
                g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 100));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game OVER",
                (SCREEN_WIDTH - metrics.stringWidth("Game OVER")) / 2,
                SCREEN_WIDTH / 2);
    }

    public class KeyInputAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (singleplayer) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (direction2 != 'L') {
                            direction2 = 'R';
                        }
                        break;
                    case KeyEvent.VK_D:
                        if (direction2 != 'R') {
                            direction2 = 'L';
                            System.out.println("D");
                        }
                        break;
                    case KeyEvent.VK_W:
                        if (direction2 != 'D') {
                            direction2 = 'U';
                            System.out.println("U");
                        }
                        break;
                    case KeyEvent.VK_S:
                        if (direction2 != 'U') {
                            direction2 = 'D';
                            System.out.println("D");
                        }
                        break;
                }
            }
        }
    }
}
