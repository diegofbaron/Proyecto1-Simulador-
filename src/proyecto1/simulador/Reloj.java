/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo que simula el reloj del sistema.
 * Cumple con el requisito: "Uso obligatorio de Hilos para el reloj del sistema".
 */
public class Reloj extends Thread {
    
    private int ciclos;
    private volatile int tiempoCiclo;
    private volatile boolean pausado;
    private SistemaOperativo so;

    public Reloj(int tiempoCicloInicial) {
        this.ciclos = 0;
        this.tiempoCiclo = tiempoCicloInicial;
        this.pausado = true;
        this.setName("Reloj-Sistema");
        this.setDaemon(true);
    }
    
    public void setSistemaOperativo(SistemaOperativo so) {
        this.so = so;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!pausado) {
                    Thread.sleep(tiempoCiclo);
                    ciclos++;
                    
                    if (so != null) {
                        so.ejecutarCiclo();
                    }
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Reloj.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void iniciar() { this.pausado = false; }
    public void pausar() { this.pausado = true; }
    public void setTiempoCiclo(int tiempoMs) { this.tiempoCiclo = tiempoMs; }
    public int getCiclos() { return ciclos; }
    public boolean isPausado() { return pausado; }
    public void resetCiclos() { this.ciclos = 0; }
}
