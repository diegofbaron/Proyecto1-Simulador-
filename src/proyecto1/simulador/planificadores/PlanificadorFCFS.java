package proyecto1.simulador.planificadores;

import estructuras.Lista;
import proyecto1.simulador.PCB;

/**
 * Planificador FCFS (First Come First Served).
 * No preemptivo - El proceso se ejecuta hasta terminar o bloquearse.
 */
public class PlanificadorFCFS implements Planificador {
    
    @Override
    public PCB seleccionarProceso(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        if (procesoActual != null) {
            return procesoActual; // No hay preempción
        }
        return colaListos.getPrimero();
    }
    
    @Override
    public boolean debeHacerPreempcion(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        return false; // FCFS no es preemptivo
    }
    
    @Override
    public void ordenarCola(Lista<PCB> colaListos, int cicloActual) {
        // FCFS no reordena - mantiene orden de llegada
    }
    
    @Override
    public String getNombre() {
        return "FCFS (First Come First Served)";
    }
    
    @Override
    public boolean esPreemptivo() {
        return false;
    }
}
