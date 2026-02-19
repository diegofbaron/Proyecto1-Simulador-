package proyecto1.simulador.planificadores;

import estructuras.Lista;
import proyecto1.simulador.PCB;

/**
 * Interfaz para las políticas de planificación.
 */
public interface Planificador {
    
    /**
     * Selecciona el siguiente proceso a ejecutar de la cola de listos.
     * @param colaListos Cola de procesos listos
     * @param procesoActual Proceso actualmente en ejecución (puede ser null)
     * @param cicloActual Ciclo actual del sistema
     * @return PCB del proceso seleccionado, o null si no hay ninguno
     */
    PCB seleccionarProceso(Lista<PCB> colaListos, PCB procesoActual, int cicloActual);
    
    /**
     * Determina si se debe hacer preempción del proceso actual.
     * @param colaListos Cola de procesos listos
     * @param procesoActual Proceso en ejecución
     * @param cicloActual Ciclo actual
     * @return true si debe hacer preempción
     */
    boolean debeHacerPreempcion(Lista<PCB> colaListos, PCB procesoActual, int cicloActual);
    
    /**
     * Ordena la cola de listos según el criterio del algoritmo.
     * @param colaListos Cola a ordenar
     * @param cicloActual Ciclo actual
     */
    void ordenarCola(Lista<PCB> colaListos, int cicloActual);
    
    /**
     * Obtiene el nombre del algoritmo.
     * @return Nombre del algoritmo
     */
    String getNombre();
    
    /**
     * Indica si el algoritmo es preemptivo.
     * @return true si es preemptivo
     */
    boolean esPreemptivo();
}
