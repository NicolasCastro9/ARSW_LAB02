package edu.eci.arsw.primefinder;

public class Control extends Thread {

    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];
    private Object lock;

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];
        this.lock = new Object();

        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
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

        while (true) {
            try {
                Thread.sleep(TMILISECONDS);
                pauseThreads();
                printTotalPrimes();
                waitForEnter();
                resumeThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseThreads() {
        for (PrimeFinderThread thread : pft) {
            thread.pauseThread();
        }
    }

    private void resumeThreads() {
        for (PrimeFinderThread thread : pft) {
            thread.resumeThread();
        }
    }

    private void printTotalPrimes() {
        int totalPrimes = 0;
        for (PrimeFinderThread thread : pft) {
            totalPrimes += thread.getPrimes().size();
        }
        System.out.println("Numeros Primos encontrados Hasta ahora: " + totalPrimes);
    }

    private void waitForEnter() {
        System.out.println("Presione ENTER para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
