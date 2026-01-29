/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

public class PCB {
    private String nombre;
    private int id;
    private int instruccionesTotales;
    private int instruccionesEjecutadas;
    private int prioridad;
    private int deadline;
    private Estado estado;
    
    private boolean tieneES;
    private int cicloES;
    private int ciclosParaCompletarES;
    private int contadorES;

    public PCB(String nombre, int instrucciones, int prioridad, int deadline, boolean tieneES, int cicloES, int duracionES) {
        this.nombre = nombre;
        // Extraer ID del nombre
        try { this.id = Integer.parseInt(nombre.replaceAll("[^0-9]", "")); } 
        catch (Exception e) { this.id = (int)(Math.random()*100); }
        
        this.instruccionesTotales = instrucciones;
        this.instruccionesEjecutadas = 0;
        this.prioridad = prioridad;
        this.deadline = deadline;
        this.estado = Estado.NUEVO;
        this.tieneES = tieneES;
        this.cicloES = cicloES;
        this.ciclosParaCompletarES = duracionES;
        this.contadorES = 0;
    }

    public void ejecutarInstruccion() { this.instruccionesEjecutadas++; }
    public boolean haTerminado() { return instruccionesEjecutadas >= instruccionesTotales; }
    public boolean debeBloquearse() { return tieneES && instruccionesEjecutadas == cicloES && contadorES == 0; }
    
    public String getNombre() { return nombre; }
    public int getId() { return id; }
    public int getPrioridad() { return prioridad; }
    public int getInstruccionesTotales() { return instruccionesTotales; }
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    public int getDeadline() { return deadline; }
    public int getCiclosParaCompletarES() { return ciclosParaCompletarES; }
    public int getContadorES() { return contadorES; }
    
    public void incrementarContadorES() { this.contadorES++; }
    public void resetContadorES() { this.contadorES = 0; }
    public void setEstado(Estado e) { this.estado = e; }
}
