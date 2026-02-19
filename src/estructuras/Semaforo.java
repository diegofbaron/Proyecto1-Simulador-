package estructuras;

/**
 * Implementación de Semáforo para exclusión mutua.
 * Cumple con el requisito: "Uso de semáforos para garantizar exclusión mutua".
 */
public class Semaforo {
    private int valor;
    
    public Semaforo(int valorInicial) {
        this.valor = valorInicial;
    }
    
    public synchronized void acquire() {
        while (valor <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        valor--;
    }
    
    public synchronized void release() {
        valor++;
        notify();
    }
    
    public synchronized int getValor() {
        return valor;
    }
}
