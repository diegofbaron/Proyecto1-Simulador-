/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

public class PCB {
    // Identificación
    private static int contadorId = 1; // Autoincremental
    private int id;
    private String nombre; 

    // Estado y Registros
    private Estado estado; 
    private int programCounter; // PC
    private int mar; // MAR

    // Planificación
    private int prioridad; 
    private int deadline; // Tiempo límite absoluto (ciclo reloj)
    private int tiempoLlegada; // Momento en que entra al sistema

    // Ejecución
    private int instruccionesTotales; // Longitud del proceso
    private int instruccionesEjecutadas; // Para calcular progreso

    // Manejo de E/S (Interrupciones/Bloqueos)
    private boolean usaES; // Si el proceso requiere E/S
    private int ciclosParaGenerarES; // Cuándo ocurre la excepción
    private int ciclosParaCompletarES; // Cuánto tarda en E/S
    private int contadorES; // Contador interno mientras está bloqueado

    public PCB(String nombre, int instruccionesTotales, int prioridad, int deadline, boolean usaES, int cicloES, int duracionES) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.instruccionesTotales = instruccionesTotales;
        this.prioridad = prioridad;
        this.deadline = deadline;
        
        // Inicialización por defecto
        this.estado = Estado.NUEVO;
        this.programCounter = 0;
        this.mar = 0; 
        this.instruccionesEjecutadas = 0;
        
        // Configuración de E/S
        this.usaES = usaES;
        this.ciclosParaGenerarES = cicloES;
        this.ciclosParaCompletarES = duracionES;
        this.contadorES = 0;
    }

    // --- Métodos de Lógica del Simulador ---

    public void ejecutarInstruccion() {
        this.programCounter++;
        this.mar++;
        this.instruccionesEjecutadas++;
    }

    public boolean haTerminado() {
        return instruccionesEjecutadas >= instruccionesTotales;
    }
    
    // Método auxiliar para saber si debe bloquearse por E/S en este ciclo
    public boolean debeBloquearse() {
        return usaES && (instruccionesEjecutadas == ciclosParaGenerarES);
    }

    // --- Getters y Setters ---
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public int getProgramCounter() { return programCounter; }
    public int getMar() { return mar; }
    
    public int getPrioridad() { return prioridad; }
    public int getDeadline() { return deadline; }
    
    public int getInstruccionesTotales() { return instruccionesTotales; }
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    
    // Getters/Setters para E/S
    public int getCiclosParaCompletarES() { return ciclosParaCompletarES; }
    
    // Este es el que faltaba o daba error de nombre
    public int getContadorES() { return contadorES; } 
    public void incrementarContadorES() { this.contadorES++; }
    public void resetContadorES() { this.contadorES = 0; }
    
    // Este faltaba para marcar cuándo entra a RAM
    public void setTiempoLlegada(int t) { this.tiempoLlegada = t; }

    @Override
    public String toString() {
        return nombre + " (ID:" + id + ") [" + estado + "]";
    }
}
