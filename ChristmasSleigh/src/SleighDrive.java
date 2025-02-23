import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SleighDrive extends JPanel implements Runnable {

    public static final int WIDTH_IMAGE = 1500;
    public static final int HEIGHT_IMAGE = 800;
    public static int SPEED = 0;


    private MyObject background;
    private MyObject driver;
    private MyObject surrounding;

    private boolean isDriverMovingLeft = false;
    private boolean isDriverMovingRight = false;

    private boolean isAccelerating = false;
    private boolean isDecelerating = false;


    private final ArrayList<Line> lines = new ArrayList<>();
    private final ExecutorService linesExecutorService = Executors.newSingleThreadExecutor();
    public static Future<?> renderLinesFuture;

    private JButton musicButton;
    private Clip musicClip;
    private ExecutorService musicExecutorService;
    public static Future<?> musicFuture;

    private JLabel speedLabel;
    private int displayedSpeed = 0;
    private ExecutorService speedometerExecutorService = Executors.newSingleThreadExecutor();
    public static Future<?> speedometerFuture;

    private ArrayList<MyObject> snowflakes = new ArrayList<>();
    private ExecutorService snowflakesExecutorService = Executors.newSingleThreadExecutor();
    public static Future<?> snowflakesFuture;


    public SleighDrive() {
        setPreferredSize(new Dimension(WIDTH_IMAGE, HEIGHT_IMAGE));
        setLayout(null);

        loadMusic();
        loadObjects();

        addKeyListeners();

        setFocusable(true);

        renderLines();
        new Thread(this).start();
        startSpeedometerThread();
    }


    private void addKeyListeners() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        isDriverMovingLeft = true; break;

                    case KeyEvent.VK_D:
                        isDriverMovingRight = true; break;

                    case KeyEvent.VK_W:
                        isAccelerating = true; break;

                    case KeyEvent.VK_S:
                        isDecelerating = true; break;
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        isDriverMovingLeft = false; break;

                    case KeyEvent.VK_D:
                        isDriverMovingRight = false; break;

                    case KeyEvent.VK_W:
                        isAccelerating = false; break;

                    case KeyEvent.VK_S:
                        isDecelerating = false; break;
                }
            }

        });
    }


    private void loadObjects() {
        ImageIcon backgroundIcon = new ImageIcon("Assets/mainRoad.png");
        background = new MyObject(0, 0, backgroundIcon.getImage().getScaledInstance(WIDTH_IMAGE, HEIGHT_IMAGE, Image.SCALE_SMOOTH));

        ImageIcon driverIcon = new ImageIcon("Assets/driver.png");
        driver = new MyObject(0, 0, driverIcon.getImage().getScaledInstance(WIDTH_IMAGE, HEIGHT_IMAGE, Image.SCALE_SMOOTH));

        ImageIcon surroundingIcon = new ImageIcon("Assets/surrounding.png");
        surrounding = new MyObject(0, 0, surroundingIcon.getImage().getScaledInstance(WIDTH_IMAGE, HEIGHT_IMAGE, Image.SCALE_SMOOTH));


        musicButton = new JButton();
        musicButton.setBounds(715, 635, 105, 90);
        musicButton.setBorderPainted(false);
        musicButton.setContentAreaFilled(false);


        musicButton.addActionListener(e -> {
            if (musicClip.isRunning()) {
                stopMusic();
                stopSnowing();
            } else {
                musicExecutorService = Executors.newSingleThreadExecutor();
                startMusic();
                startSnowing();
            }
            requestFocusInWindow();
        });


        this.add(musicButton);


        speedLabel = new JLabel("SPEED: ", SwingConstants.CENTER);
        speedLabel.setBounds(830, 575, 150, 80);
        speedLabel.setFont(new Font("Arial", Font.BOLD, 28));

        this.add(speedLabel);
    }


    private void loadMusic() {
        try {
            File musicFile = new File("Assets/Wham-LastChristmas.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }


    private void startMusic() {
        musicFuture = musicExecutorService.submit(() -> {
            musicClip.start();
            while(!Thread.interrupted()) ;
        });

        FutureJList.updateList();
    }

    private void stopMusic() {
        if (musicClip.isRunning())
            musicClip.stop();

        musicExecutorService.shutdownNow();

        FutureJList.updateList();
    }



    private void renderLines() {
        renderLinesFuture = linesExecutorService.submit(() -> {

            while (!Thread.currentThread().isInterrupted()) {
                if (SPEED > 0) {
                    synchronized (lines) {
                        lines.add(new Line());
                    }
                }

                try {
                    Thread.sleep(SPEED > 0 ? 2000 - SPEED : 0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        });

        FutureJList.updateList();
    }


    private void startSnowing() {
        snowflakesFuture = snowflakesExecutorService.submit(() -> {

            ImageIcon snowflakeIcon = new ImageIcon("Assets/snowflake.png");

            while (!Thread.currentThread().isInterrupted()) {
                int x = (int) (Math.random() * WIDTH_IMAGE);
                synchronized (snowflakes) {
                    snowflakes.add(new MyObject(x, 0, snowflakeIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        FutureJList.updateList();
    }

    private void stopSnowing() {
        snowflakesFuture.cancel(true);

        FutureJList.updateList();
    }


    private void startSpeedometerThread() {
        speedometerFuture = speedometerExecutorService.submit(() -> {
           while(!Thread.currentThread().isInterrupted()) {
               if (displayedSpeed != SPEED)
                   displayedSpeed = SPEED / 14;

               if (displayedSpeed > 90 && speedLabel.getForeground() != Color.RED)
                   speedLabel.setForeground(Color.RED);

               if (displayedSpeed < 90 && speedLabel.getForeground() != Color.GREEN)
                   speedLabel.setForeground(Color.GREEN);
           }
        });

        FutureJList.updateList();
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        background.draw(g, this);

        synchronized (lines) {
            for (Line line : lines)
                line.draw(g);
        }

        surrounding.draw(g, this);

        driver.draw(g, this);

        synchronized (snowflakes) {
            for (MyObject snowflake : snowflakes)
                snowflake.draw(g, this);
        }

    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            if (isDriverMovingLeft && driver.getX() > -400) {
                driver.subtractX(5);
            }

            if (isDriverMovingRight && driver.getX() < 400) {
                driver.addX(5);
            }

            musicButton.setLocation(driver.getX() + 720, driver.getY() + 630);
            speedLabel.setLocation(driver.getX() + 830, 575);


            if(isAccelerating) SPEED = Math.min(SPEED + 5, 1900);
            if(isDecelerating) SPEED = Math.max(SPEED - 5, 0);



            synchronized (lines) {
                Iterator<Line> iterator = lines.iterator();
                while (iterator.hasNext()) {
                    Line line = iterator.next();
                    line.moveDown();
                    if (line.getY() > HEIGHT_IMAGE) {
                        iterator.remove();
                    }
                }
            }

            synchronized (snowflakes) {
                Iterator<MyObject> iterator = snowflakes.iterator();
                while (iterator.hasNext()) {
                    MyObject snowflake = iterator.next();
                    snowflake.addY(5);
                    if (snowflake.getY() > HEIGHT_IMAGE) {
                        iterator.remove();
                    }
                }
            }

            speedLabel.setText(String.valueOf(displayedSpeed));


            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sleigh Drive");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SleighDrive());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


}
