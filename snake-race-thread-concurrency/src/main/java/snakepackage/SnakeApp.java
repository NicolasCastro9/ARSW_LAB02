package snakepackage;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;

import enums.Direction;
import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private final Object lock = new Object();
    JButton inicioButton = new JButton("Iniciar");
    JButton pausarButton = new JButton("Pausar");
    JButton reanudarButton = new JButton("Reanudar");
    JButton suspenderButton = new JButton("Suspender");

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
    /**
     * Inicia el juego, creando nuevas serpientes y generando los elementos del tablero si el juego se reanuda.
     * Si el juego se estaba reanudando, se muestra el panel de estadísticas y se inician las serpientes en las celdas de inicio.
     * Si el juego se está iniciando por primera vez, se restablecen las variables de longitud de serpiente más larga y serpiente peor clasificada,
     * y se generan aleatoriamente los elementos del tablero.
     */
    private void startGame() {
        if (gameSuspended) {
            gameSuspended = false;
            statsPanel.setVisible(true);
        } else {
            // Si el juego se está iniciando por primera vez
            // Restablece las variables de longitud de serpiente más larga y serpiente peor clasificada
            longestSnakeLength = 0;
            worstSnakeLength = Integer.MAX_VALUE;
            longestSnakeLabel.setText("Serpiente más larga: ");
            worstSnakeLabel.setText("Peor serpiente: ");
            // Vuelve a generar aleatoriamente los elementos del tablero
            board.GenerateBoard();
            board.GenerateFood();
            board.GenerateBarriers();
            board.GenerateJumpPads();
            board.GenerateTurboBoosts();
            // Inicia cada serpiente en la celda de inicio y la dirección inicial
            for (int i = 0; i != MAX_THREADS; i++) {
                snakes[i] = new Snake(i + 1, spawn[i], i + 1);
                snakes[i].addObserver(board);
                thread[i] = new Thread(snakes[i]);
                thread[i].start();
            }
        }
        // Vuelve a pintar el tablero
        frame.getContentPane().getComponent(0).repaint();
        pausarButton.setEnabled(true);
        reanudarButton.setEnabled(true);
        suspenderButton.setEnabled(true);
        gamePaused = false;
    }


    public static SnakeApp getApp() {
        return app;
    }

    /**
     * Pausa el juego deteniendo la ejecución de los hilos de juego.
     * Muestra las estadísticas del juego cuando se pausa.
     */
    private void pauseGame() {
        if (!gamePaused) {
            gamePaused = true;
            for (int i = 0; i < MAX_THREADS; i++) {
                synchronized (lock) {
                    thread[i].suspend();
                }
            }
            showGameStats();
        }
    }
    /**
     * Reanuda el juego, permitiendo que los hilos de juego continúen su ejecución.
     * Solo se ejecuta si el juego está pausado.
     */
    private void resumeGame() {
        if (gamePaused) {
            gamePaused = false;
            for (int i = 0; i < MAX_THREADS; i++) {
                synchronized (lock) {
                    thread[i].resume();
                }
            }
        }
    }


    private void suspendGame() {
        gameSuspended = true;
        pauseGame();
        pausarButton.setEnabled(false);
        reanudarButton.setEnabled(false);
        suspenderButton.setEnabled(false);
        showGameStats();
    }

    /**
     * Actualiza la visualización de las estadísticas del juego.
     * Muestra la serpiente más larga que sigue viva y la primera serpiente que se ha chocado.
     */
    private void showGameStats() {
        List<Snake> aliveSnakes = new ArrayList<>();
        for (int i = 0; i < MAX_THREADS; i++) {
            if (snakes[i].isAlive()) {
                aliveSnakes.add(snakes[i]);
            }
        }
        // Actualiza la etiqueta de la serpiente más larga con su información
        if (!aliveSnakes.isEmpty()) {
            Snake longestSnake = Collections.max(aliveSnakes, (s1, s2) -> Integer.compare(s1.getBody().size(), s2.getBody().size()));
            longestSnakeLabel.setText("Serpiente más larga:"  + " [" + longestSnake.getIdt() + "]" + " tamaño " + longestSnake.getBody().size() + "/");
            if (longestSnake.getBody().size() > longestSnakeLength) {
                longestSnakeLength = longestSnake.getBody().size();
            }

        }
        // Busca la primera serpiente que se haya chocado
        Snake firstCrashedSnake = null;
        for (int i = 0; i < MAX_THREADS; i++) {
            if (!snakes[i].isAlive()) {
                firstCrashedSnake = snakes[i];
                break;
            }
        }
        if (firstCrashedSnake != null) {
            worstSnakeLabel.setText("Primera serpiente chocada: " + firstCrashedSnake.getIdt());
        }

    }
    
}
