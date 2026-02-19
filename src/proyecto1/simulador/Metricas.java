package proyecto1.simulador;

import estructuras.Lista;
import estructuras.Nodo;

/**
 * Clase para calcular y almacenar métricas de rendimiento del sistema.
 */
public class Metricas {
    
    // Contadores
    private int procesosCompletados;
    private int procesosFallidosDeadline;
    private int totalProcesos;
    private int ciclosConCPUActiva;
    private int ciclosTotales;
    
    // Sumas para promedios
    private int sumaTiempoEspera;
    private int sumaTiempoRespuesta;
    private int sumaTiempoRetorno;
    
    // Historial para gráficos
    private Lista<Double> historialUsoCPU;
    private Lista<Integer> historialCiclos;
    
    public Metricas() {
        this.procesosCompletados = 0;
        this.procesosFallidosDeadline = 0;
        this.totalProcesos = 0;
        this.ciclosConCPUActiva = 0;
        this.ciclosTotales = 0;
        this.sumaTiempoEspera = 0;
        this.sumaTiempoRespuesta = 0;
        this.sumaTiempoRetorno = 0;
        this.historialUsoCPU = new Lista<>();
        this.historialCiclos = new Lista<>();
    }
    
    public void registrarCiclo(boolean cpuActiva) {
        ciclosTotales++;
        if (cpuActiva) {
            ciclosConCPUActiva++;
        }
        
        // Guardar historial cada 5 ciclos para el gráfico
        if (ciclosTotales % 5 == 0) {
            historialCiclos.insertar(ciclosTotales);
            historialUsoCPU.insertar(getUsoCPU());
        }
    }
    
    public void registrarProcesoCompletado(PCB proceso, int cicloActual) {
        procesosCompletados++;
        sumaTiempoEspera += proceso.getTiempoEspera();
        sumaTiempoRespuesta += proceso.getTiempoRespuesta();
        sumaTiempoRetorno += (cicloActual - proceso.getTiempoLlegada());
        
        if (cicloActual > proceso.getDeadline()) {
            procesosFallidosDeadline++;
        }
    }
    
    public void registrarDeadlineFallido() {
        procesosFallidosDeadline++;
    }
    
    public void registrarNuevoProceso() {
        totalProcesos++;
    }
    
    // Cálculos de métricas
    public double getUsoCPU() {
        if (ciclosTotales == 0) return 0;
        return (double) ciclosConCPUActiva / ciclosTotales * 100;
    }
    
    public double getThroughput() {
        if (ciclosTotales == 0) return 0;
        return (double) procesosCompletados / ciclosTotales * 100;
    }
    
    public double getTasaExitoMision() {
        int totalFinalizados = procesosCompletados;
        if (totalFinalizados == 0) return 100;
        return (double) (totalFinalizados - procesosFallidosDeadline) / totalFinalizados * 100;
    }
    
    public double getTiempoEsperaPromedio() {
        if (procesosCompletados == 0) return 0;
        return (double) sumaTiempoEspera / procesosCompletados;
    }
    
    public double getTiempoRespuestaPromedio() {
        if (procesosCompletados == 0) return 0;
        return (double) sumaTiempoRespuesta / procesosCompletados;
    }
    
    public double getTiempoRetornoPromedio() {
        if (procesosCompletados == 0) return 0;
        return (double) sumaTiempoRetorno / procesosCompletados;
    }
    
    public int getDeadlinesCumplidos() {
        return procesosCompletados - procesosFallidosDeadline;
    }
    
    // Getters
    public int getProcesosCompletados() { return procesosCompletados; }
    public int getProcesosFallidosDeadline() { return procesosFallidosDeadline; }
    public int getTotalProcesos() { return totalProcesos; }
    public int getCiclosTotales() { return ciclosTotales; }
    public Lista<Double> getHistorialUsoCPU() { return historialUsoCPU; }
    public Lista<Integer> getHistorialCiclos() { return historialCiclos; }
    
    public void reset() {
        procesosCompletados = 0;
        procesosFallidosDeadline = 0;
        totalProcesos = 0;
        ciclosConCPUActiva = 0;
        ciclosTotales = 0;
        sumaTiempoEspera = 0;
        sumaTiempoRespuesta = 0;
        sumaTiempoRetorno = 0;
        historialUsoCPU.limpiar();
        historialCiclos.limpiar();
    }
}
