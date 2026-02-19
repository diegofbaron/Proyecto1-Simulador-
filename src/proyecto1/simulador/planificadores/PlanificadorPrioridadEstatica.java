package proyecto1.simulador.planificadores;

import estructuras.Lista;
import estructuras.Nodo;
import proyecto1.simulador.PCB;

/**
 * Planificador de Prioridad Estática Preemptiva.
 * Para tareas de tiempo real - Ejecuta siempre el proceso con mayor prioridad.
 * Prioridad 1 = más alta.
 */
public class PlanificadorPrioridadEstatica implements Planificador {
    
    @Override
    public PCB seleccionarProceso(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        PCB mayorPrioridad = encontrarMayorPrioridad(colaListos);
        
        if (procesoActual == null) {
            return mayorPrioridad;
        }
        
        if (mayorPrioridad != null && 
            mayorPrioridad.getPrioridad() < procesoActual.getPrioridad()) {
            return mayorPrioridad;
        }
        
        return procesoActual;
    }
    
    @Override
    public boolean debeHacerPreempcion(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        if (procesoActual == null || colaListos.esVacia()) return false;
        
        PCB mayorPrioridad = encontrarMayorPrioridad(colaListos);
        return mayorPrioridad != null && 
               mayorPrioridad.getPrioridad() < procesoActual.getPrioridad();
    }
    
    @Override
    public void ordenarCola(Lista<PCB> colaListos, int cicloActual) {
        if (colaListos.getSize() <= 1) return;
        
        // Ordenamiento burbuja por prioridad (menor número = mayor prioridad)
        boolean cambio;
        do {
            cambio = false;
            Nodo<PCB> actual = colaListos.getpFirst();
            while (actual != null && actual.getSiguiente() != null) {
                PCB p1 = actual.getContenido();
                PCB p2 = actual.getSiguiente().getContenido();
                
                if (p1.getPrioridad() > p2.getPrioridad()) {
                    actual.setContenido(p2);
                    actual.getSiguiente().setContenido(p1);
                    cambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (cambio);
    }
    
    private PCB encontrarMayorPrioridad(Lista<PCB> colaListos) {
        if (colaListos.esVacia()) return null;
        
        PCB mejor = null;
        int mejorPrioridad = Integer.MAX_VALUE;
        
        Nodo<PCB> aux = colaListos.getpFirst();
        while (aux != null) {
            PCB p = aux.getContenido();
            if (p.getPrioridad() < mejorPrioridad) {
                mejorPrioridad = p.getPrioridad();
                mejor = p;
            }
            aux = aux.getSiguiente();
        }
        return mejor;
    }
    
    @Override
    public String getNombre() {
        return "Prioridad Estática Preemptiva";
    }
    
    @Override
    public boolean esPreemptivo() {
        return true;
    }
}
