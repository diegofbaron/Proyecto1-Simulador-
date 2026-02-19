package proyecto1.simulador.planificadores;

import estructuras.Lista;
import estructuras.Nodo;
import proyecto1.simulador.PCB;

/**
 * Planificador EDF (Earliest Deadline First).
 * Preemptivo - Ejecuta el proceso con deadline más cercano.
 * Algoritmo óptimo para sistemas de tiempo real.
 */
public class PlanificadorEDF implements Planificador {
    
    @Override
    public PCB seleccionarProceso(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        PCB deadlineMasCercano = encontrarDeadlineMasCercano(colaListos, cicloActual);
        
        if (procesoActual == null) {
            return deadlineMasCercano;
        }
        
        if (deadlineMasCercano != null && 
            deadlineMasCercano.getDeadline() < procesoActual.getDeadline()) {
            return deadlineMasCercano;
        }
        
        return procesoActual;
    }
    
    @Override
    public boolean debeHacerPreempcion(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        if (procesoActual == null || colaListos.esVacia()) return false;
        
        PCB deadlineMasCercano = encontrarDeadlineMasCercano(colaListos, cicloActual);
        return deadlineMasCercano != null && 
               deadlineMasCercano.getDeadline() < procesoActual.getDeadline();
    }
    
    @Override
    public void ordenarCola(Lista<PCB> colaListos, int cicloActual) {
        if (colaListos.getSize() <= 1) return;
        
        // Ordenamiento burbuja por deadline (menor deadline primero)
        boolean cambio;
        do {
            cambio = false;
            Nodo<PCB> actual = colaListos.getpFirst();
            while (actual != null && actual.getSiguiente() != null) {
                PCB p1 = actual.getContenido();
                PCB p2 = actual.getSiguiente().getContenido();
                
                if (p1.getDeadline() > p2.getDeadline()) {
                    actual.setContenido(p2);
                    actual.getSiguiente().setContenido(p1);
                    cambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (cambio);
    }
    
    private PCB encontrarDeadlineMasCercano(Lista<PCB> colaListos, int cicloActual) {
        if (colaListos.esVacia()) return null;
        
        PCB mejor = null;
        int menorDeadline = Integer.MAX_VALUE;
        
        Nodo<PCB> aux = colaListos.getpFirst();
        while (aux != null) {
            PCB p = aux.getContenido();
            if (p.getDeadline() < menorDeadline) {
                menorDeadline = p.getDeadline();
                mejor = p;
            }
            aux = aux.getSiguiente();
        }
        return mejor;
    }
    
    @Override
    public String getNombre() {
        return "EDF (Earliest Deadline First)";
    }
    
    @Override
    public boolean esPreemptivo() {
        return true;
    }
}
