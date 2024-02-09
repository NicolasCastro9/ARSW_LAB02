package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {

    int a, b;

    private List<Integer> primes;
    // Objeto de bloqueo para sincronización
    private Object lock;
    // Variable booleana para controlar el estado de pausa del hilo
    private boolean paused;

    public PrimeFinderThread(int a, int b, Object lock) {
        super();
        this.primes = new LinkedList<>();
        this.a = a;
        this.b = b;
        this.lock = lock;
        this.paused = false;
    }

    @Override
    public void run() {
        for (int i = a; i < b; i++) {
            // Bloque sincronizado, si el hilo está pausado, espera hasta que se reanude
            synchronized (lock) {
                if (paused) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isPrime(i)) {
                primes.add(i);
                System.out.println(i);
            }
        }
    }

    boolean isPrime(int n) {
        boolean ans;
        if (n > 2) {
            ans = n % 2 != 0;
            for (int i = 3; ans && i * i <= n; i += 2) {
                ans = n % i != 0;
            }
        } else {
            ans = n == 2;
        }
        return ans;
    }

    public List<Integer> getPrimes() {
        return primes;
    }
    /**
     * Metodo que pausa el hilo
     */
    public void pauseThread() {
        paused = true;
    }
    /**
     * Metodo que reanuda el hilo
     */
    public void resumeThread() {
        synchronized (lock) {
            paused = false;
            lock.notify();
        }
    }
}
