/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo que simula el paso del tiempo en el sistema.
 * Cumple con el requisito: "Uso obligatorio de Hilos para el reloj".
 */
public class Reloj extends Thread {
    
    private int ciclos;           // Contador global de ciclos
    private int tiempoCiclo;      // Duración en ms de cada ciclo (configurable)
    private boolean pausado;      // Para detener la simulación
    private SistemaOperativo so;  // Referencia al sistema para avisar el "tic-tac"

    public Reloj(int tiempoCicloInicial) {
        this.ciclos = 0;
        this.tiempoCiclo = tiempoCicloInicial;
        this.pausado = true; // Inicia pausado hasta que demos "Start"
    }
    
    // Método para conectar el SO con el Reloj
    public void setSistemaOperativo(SistemaOperativo so) {
        this.so = so;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!pausado) {
                    // 1. Esperar la duración del ciclo (Simulación del tiempo)
                    Thread.sleep(tiempoCiclo);
                    
                    // 2. Incrementar reloj global
                    ciclos++;
                    // System.out.println("--- Reloj: Ciclo " + ciclos + " ---");
                    
                    // 3. Notificar al Sistema Operativo para que trabaje
                    if (so != null) {
                        so.ejecutarCiclo();
                    }
                } else {
                    // Si está pausado, esperamos un poco para no quemar CPU
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Reloj.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // --- Métodos de Control ---
    
    public void iniciar() {
        this.pausado = false;
    }
    
    public void pausar() {
        this.pausado = true;
    }
    
    // Permite cambiar la velocidad en tiempo real
    public void setTiempoCiclo(int tiempoMs) {
        this.tiempoCiclo = tiempoMs;
    }

    public int getCiclos() {
        return ciclos;
    }
}
