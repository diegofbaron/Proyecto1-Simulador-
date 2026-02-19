/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

/**
 * Process Control Block - Bloque de Control de Proceso.
 * Contiene toda la información necesaria para gestionar un proceso.
 */
public class PCB {
    private static int contadorGlobal = 0;
    
    // Identificación
    private int id;
    private String nombre;
    
    // Registros del CPU
    private int pc;  // Program Counter (Contador de Programa)
    private int mar; // Memory Address Register (Registro de Dirección de Memoria)
    
    // Información del proceso
    private int instruccionesTotales;
    private int instruccionesEjecutadas;
    private int prioridad;           // 1 = más alta, 10 = más baja
    private int deadline;            // Ciclo límite para completar
    private int deadlineOriginal;    // Para calcular tiempo restante
    private int tiempoLlegada;       // Ciclo en que llegó al sistema
    private int periodo;             // Para tareas periódicas (0 = aperiódica)
    private Estado estado;
    
    // E/S
    private boolean tieneES;
    private int cicloES;              // En qué instrucción ocurre la E/S
    private int ciclosParaCompletarES;
    private int contadorES;
    
    // Métricas
    private int tiempoEspera;
    private int tiempoRespuesta;
    private boolean primeraEjecucion;
    private int quantumRestante;      // Para Round Robin
    
    // Constructor completo
    public PCB(String nombre, int instrucciones, int prioridad, int deadline, 
               boolean tieneES, int cicloES, int duracionES, int tiempoLlegada, int periodo) {
        this.id = ++contadorGlobal;
        this.nombre = nombre;
        this.pc = 0;
        this.mar = 0;
        this.instruccionesTotales = instrucciones;
        this.instruccionesEjecutadas = 0;
        this.prioridad = prioridad;
        this.deadline = deadline;
        this.deadlineOriginal = deadline;
        this.tiempoLlegada = tiempoLlegada;
        this.periodo = periodo;
        this.estado = Estado.NUEVO;
        this.tieneES = tieneES;
        this.cicloES = cicloES;
        this.ciclosParaCompletarES = duracionES;
        this.contadorES = 0;
        this.tiempoEspera = 0;
        this.tiempoRespuesta = -1;
        this.primeraEjecucion = true;
        this.quantumRestante = 0;
    }
    
    // Constructor simplificado (compatibilidad)
    public PCB(String nombre, int instrucciones, int prioridad, int deadline, 
               boolean tieneES, int cicloES, int duracionES) {
        this(nombre, instrucciones, prioridad, deadline, tieneES, cicloES, duracionES, 0, 0);
    }

    public void ejecutarInstruccion() { 
        this.instruccionesEjecutadas++;
        this.pc++;
        this.mar++;
    }
    
    public boolean haTerminado() { 
        return instruccionesEjecutadas >= instruccionesTotales; 
    }
    
    public boolean debeBloquearse() { 
        return tieneES && instruccionesEjecutadas == cicloES && contadorES == 0; 
    }
    
    public int getTiempoRestante() {
        return instruccionesTotales - instruccionesEjecutadas;
    }
    
    public int getTiempoRestanteDeadline(int cicloActual) {
        return deadline - cicloActual;
    }
    
    public boolean deadlineExcedido(int cicloActual) {
        return cicloActual > deadline && !haTerminado();
    }
    
    public void incrementarTiempoEspera() {
        this.tiempoEspera++;
    }
    
    public void marcarPrimeraEjecucion(int cicloActual) {
        if (primeraEjecucion) {
            this.tiempoRespuesta = cicloActual - tiempoLlegada;
            this.primeraEjecucion = false;
        }
    }
    
    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPc() { return pc; }
    public int getMar() { return mar; }
    public int getPrioridad() { return prioridad; }
    public int getInstruccionesTotales() { return instruccionesTotales; }
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    public int getDeadline() { return deadline; }
    public int getDeadlineOriginal() { return deadlineOriginal; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public int getPeriodo() { return periodo; }
    public Estado getEstado() { return estado; }
    public int getCiclosParaCompletarES() { return ciclosParaCompletarES; }
    public int getContadorES() { return contadorES; }
    public int getTiempoEspera() { return tiempoEspera; }
    public int getTiempoRespuesta() { return tiempoRespuesta; }
    public int getQuantumRestante() { return quantumRestante; }
    public boolean esPeriodica() { return periodo > 0; }
    
    // Setters
    public void setEstado(Estado e) { this.estado = e; }
    public void incrementarContadorES() { this.contadorES++; }
    public void resetContadorES() { this.contadorES = 0; }
    public void setQuantumRestante(int q) { this.quantumRestante = q; }
    public void decrementarQuantum() { this.quantumRestante--; }
    public void setPrioridad(int p) { this.prioridad = p; }
    
    @Override
    public String toString() {
        return String.format("%s [ID:%d | PC:%d | MAR:%d | Pri:%d | DL:%d | %s]", 
            nombre, id, pc, mar, prioridad, deadline, estado);
    }
    
    public String toStringDetallado() {
        return String.format("ID:%d | %s | Estado:%s | PC:%d | MAR:%d | Prioridad:%d | Deadline:%d | Restante:%d/%d", 
            id, nombre, estado, pc, mar, prioridad, deadline, getTiempoRestante(), instruccionesTotales);
    }
}
