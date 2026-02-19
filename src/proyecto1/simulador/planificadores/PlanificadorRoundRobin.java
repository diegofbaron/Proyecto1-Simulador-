package proyecto1.simulador.planificadores;

import estructuras.Lista;
import proyecto1.simulador.PCB;

/**
 * Planificador Round Robin.
 * Preemptivo por quantum - Cada proceso recibe un tiempo fijo de CPU.
 */
public class PlanificadorRoundRobin implements Planificador {
    
    private int quantum;
    
    public PlanificadorRoundRobin(int quantum) {
        this.quantum = quantum;
    }
    
    @Override
    public PCB seleccionarProceso(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        if (procesoActual != null && procesoActual.getQuantumRestante() > 0) {
            return procesoActual;
        }
        
        PCB siguiente = colaListos.getPrimero();
        if (siguiente != null) {
            siguiente.setQuantumRestante(quantum);
        }
        return siguiente;
    }
    
    @Override
    public boolean debeHacerPreempcion(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        if (procesoActual == null) return false;
        return procesoActual.getQuantumRestante() <= 0;
    }
    
    @Override
    public void ordenarCola(Lista<PCB> colaListos, int cicloActual) {
        // Round Robin no reordena - mantiene orden circular
    }
    
    @Override
    public String getNombre() {
        return "Round Robin (Q=" + quantum + ")";
    }
    
    @Override
    public boolean esPreemptivo() {
        return true;
    }
    
    public int getQuantum() { return quantum; }
    public void setQuantum(int q) { this.quantum = q; }
}
