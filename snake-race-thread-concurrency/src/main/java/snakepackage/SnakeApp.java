package snakepackage;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];
    private boolean gamePaused = false;
    private boolean gameSuspended = false;
    private JPanel statsPanel;
    private JLabel longestSnakeLabel;
    private JLabel worstSnakeLabel;
    private int longestSnakeLength = 0;
    private int worstSnakeLength = Integer.MAX_VALUE;

    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton inicioButton = new JButton("Iniciar");
        JButton pausarButton = new JButton("Pausar");
        JButton reanudarButton = new JButton("Reanudar");
        JButton suspenderButton = new JButton("Suspender");
        // Agregar acciones a los botones
        inicioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        pausarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }
        });
        
        reanudarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
        
        suspenderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suspendGame();
            }
        });
        JPanel actionsBPabel=new JPanel();
        actionsBPabel.setLayout(new FlowLayout());
        actionsBPabel.add(inicioButton);
        actionsBPabel.add(pausarButton);
        actionsBPabel.add(reanudarButton);
        actionsBPabel.add(suspenderButton);
        board = new Board();
        statsPanel = new JPanel(new FlowLayout());
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        statsPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));
        longestSnakeLabel = new JLabel("Serpiente más larga: ");
        worstSnakeLabel = new JLabel("Peor serpiente: ");
        statsPanel.add(longestSnakeLabel);
        statsPanel.add(worstSnakeLabel);
        
        frame.add(controlPanel, BorderLayout.SOUTH);
         frame.add(statsPanel, BorderLayout.NORTH);
        frame.add(board,BorderLayout.CENTER);
        
        
        frame.add(actionsBPabel,BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {
        
        
        
        for (int i = 0; i != MAX_THREADS; i++) {
            
            snakes[i] = new Snake(i + 1, spawn[i], i + 1);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            thread[i].start();
        }

        frame.setVisible(true);

            
        while (true) {
            int x = 0;
            for (int i = 0; i != MAX_THREADS; i++) {
                if (snakes[i].isSnakeEnd() == true) {
                    x++;
                }
            }
            if (x == MAX_THREADS) {
                break;
            }
        }


        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("["+i+"] :"+thread[i].getState());
        }
        

    }
    private void startGame() {
        if (gameSuspended) {
            gameSuspended = false;
            statsPanel.setVisible(true);
            for (int i = 0; i < MAX_THREADS; i++) {
                thread[i].resume();
            }
        } else {
            longestSnakeLength = 0;
            worstSnakeLength = Integer.MAX_VALUE;
            longestSnakeLabel.setText("Serpiente más larga: ");
            worstSnakeLabel.setText("Peor serpiente: ");
            for (int i = 0; i != MAX_THREADS; i++) {
                snakes[i] = new Snake(i + 1, spawn[i], i + 1);
                snakes[i].addObserver(board);
                thread[i] = new Thread(snakes[i]);
                thread[i].start();
            }
        }
        gamePaused = false;
    }


    public static SnakeApp getApp() {
        return app;
    }
    private void pauseGame() {
        if (!gamePaused) {
            gamePaused = true;
            for (int i = 0; i < MAX_THREADS; i++) {
                thread[i].suspend();
            }
            showGameStats();
        }
    }

    private void resumeGame() {
        if (gamePaused) {
            gamePaused = false;
            for (int i = 0; i < MAX_THREADS; i++) {
                thread[i].resume();
            }
        }
    }

    private void suspendGame() {
        gameSuspended = true;
        statsPanel.setVisible(true);
        for (int i = 0; i < MAX_THREADS; i++) {
            thread[i].suspend();
        }
    showGameStats();
    }

    private void showGameStats() {
        List<Snake> aliveSnakes = new ArrayList<>();
        for (int i = 0; i < MAX_THREADS; i++) {
            if (snakes[i].isAlive()) {
                aliveSnakes.add(snakes[i]);
            }
        }
        if (!aliveSnakes.isEmpty()) {
            Snake longestSnake = Collections.max(aliveSnakes, (s1, s2) -> Integer.compare(s1.getBody().size(), s2.getBody().size()));
            longestSnakeLabel.setText("Serpiente más larga: " + longestSnake.getBody().size());
            if (longestSnake.getBody().size() > longestSnakeLength) {
                longestSnakeLength = longestSnake.getBody().size();
            }
    
            Snake worstSnake = Collections.min(aliveSnakes, (s1, s2) -> Integer.compare(s1.getDeathTime(), s2.getDeathTime()));
            worstSnakeLabel.setText("Peor serpiente: " + worstSnake.getIdt() + " (muerta en " + worstSnake.getDeathTime() + " movimientos)");
            if (worstSnake.getDeathTime() < worstSnakeLength) {
                worstSnakeLength = worstSnake.getDeathTime();
            }
        }
    }
    

}
