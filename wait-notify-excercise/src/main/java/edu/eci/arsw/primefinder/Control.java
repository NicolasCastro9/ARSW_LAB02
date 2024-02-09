package edu.eci.arsw.primefinder;



public class Control extends Thread {

    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];
    // objeto de bloqueo para sincronización
    private Object lock;

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];
        // Se crea un objeto de bloqueo para sincronización
        this.lock = new Object();

        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            // Se crea un nuevo hilo PrimeFinderThread con el rango de datos correspondiente y el objeto de bloqueo
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, lock);
            pft[i] = elem;
        }
        
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, lock);
    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for (int i = 0; i < NTHREADS; i++) {
            pft[i].start();
        }
        // Bucle principal para controlar la pausa y reanudación de los hilos
        while (true) {
            try {
                // Pausa los hilos durante TMILISECONDS milisegundos
                Thread.sleep(TMILISECONDS);
                // Pausa todos los hilos PrimeFinderThread
                pauseThreads();
                // Imprime el total de números primos encontrados hasta el momento
                printTotalPrimes();
                // Espera a que se presione ENTER para continuar
                waitForEnter();
                // Reanuda todos los hilos PrimeFinderThread
                resumeThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Metodo que pausa todos los hilos.
     */
    private void pauseThreads() {
        for (PrimeFinderThread thread : pft) {
            thread.pauseThread();
        }
    }
     /**
     *  Metodo que reanuda todos los hilos.
     */
    private void resumeThreads() {
        for (PrimeFinderThread thread : pft) {
            thread.resumeThread();
        }
    }

    /**
     * Metodo que imprime el total de números primos encontrados hasta el momento por todos los hilos.
     */
    private void printTotalPrimes() {
        int totalPrimes = 0;
        for (PrimeFinderThread thread : pft) {
            totalPrimes += thread.getPrimes().size();
        }
        System.out.println("Numeros Primos encontrados Hasta ahora: " + totalPrimes);
    }
     /**
     * Metodo que esspera a que el usuario presione ENTER para continuar.
     */
    private void waitForEnter() {
        System.out.println("Presione ENTER para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
