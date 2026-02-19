package proyecto1.simulador.planificadores;

import estructuras.Lista;
import estructuras.Nodo;
import proyecto1.simulador.PCB;

/**
 * Planificador SRT (Shortest Remaining Time).
 * Preemptivo - Ejecuta el proceso con menor tiempo restante.
 */
public class PlanificadorSRT implements Planificador {
    
    @Override
    public PCB seleccionarProceso(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        PCB mejorCandidato = encontrarMenorTiempoRestante(colaListos);
        
        if (procesoActual == null) {
            return mejorCandidato;
        }
        
        if (mejorCandidato != null && 
            mejorCandidato.getTiempoRestante() < procesoActual.getTiempoRestante()) {
            return mejorCandidato;
        }
        
        return procesoActual;
    }
    
    @Override
    public boolean debeHacerPreempcion(Lista<PCB> colaListos, PCB procesoActual, int cicloActual) {
        if (procesoActual == null || colaListos.esVacia()) return false;
        
        PCB mejorCandidato = encontrarMenorTiempoRestante(colaListos);
        return mejorCandidato != null && 
               mejorCandidato.getTiempoRestante() < procesoActual.getTiempoRestante();
    }
    
    @Override
    public void ordenarCola(Lista<PCB> colaListos, int cicloActual) {
        if (colaListos.getSize() <= 1) return;
        
        // Ordenamiento burbuja por tiempo restante
        boolean cambio;
        do {
            cambio = false;
            Nodo<PCB> actual = colaListos.getpFirst();
            while (actual != null && actual.getSiguiente() != null) {
                PCB p1 = actual.getContenido();
                PCB p2 = actual.getSiguiente().getContenido();
                
                if (p1.getTiempoRestante() > p2.getTiempoRestante()) {
                    actual.setContenido(p2);
                    actual.getSiguiente().setContenido(p1);
                    cambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (cambio);
    }
    
    private PCB encontrarMenorTiempoRestante(Lista<PCB> colaListos) {
        if (colaListos.esVacia()) return null;
        
        PCB mejor = null;
        int menorTiempo = Integer.MAX_VALUE;
        
        Nodo<PCB> aux = colaListos.getpFirst();
        while (aux != null) {
            PCB p = aux.getContenido();
            if (p.getTiempoRestante() < menorTiempo) {
                menorTiempo = p.getTiempoRestante();
                mejor = p;
            }
            aux = aux.getSiguiente();
        }
        return mejor;
    }
    
    @Override
    public String getNombre() {
        return "SRT (Shortest Remaining Time)";
    }
    
    @Override
    public boolean esPreemptivo() {
        return true;
    }
}
