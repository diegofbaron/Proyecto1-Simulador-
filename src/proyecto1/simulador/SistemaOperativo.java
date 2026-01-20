/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import estructuras.Cola;
import estructuras.Lista;
import estructuras.Nodo;
import java.util.Random;

public class SistemaOperativo {

    // --- Estructuras de Datos del Kernel ---
    private Cola<PCB> colaNuevos;          
    private Lista<PCB> colaListos;         
    private Lista<PCB> colaBloqueados;     
    private Lista<PCB> colaTerminados;     
    
    // Ejecución
    private PCB procesoEnEjecucion;        
    private Reloj reloj;                   
    private int contadorCiclos;            
    
    public SistemaOperativo() {
        // Inicializar estructuras
        this.colaNuevos = new Cola<>();
        this.colaListos = new Lista<>();
        this.colaBloqueados = new Lista<>();
        this.colaTerminados = new Lista<>();
        this.procesoEnEjecucion = null;
        this.contadorCiclos = 0;
        
        // Reloj a 1000ms (1 segundo) para poder leer la consola
        this.reloj = new Reloj(1000); 
        this.reloj.setSistemaOperativo(this); 
        
        // Carga inicial
        generarProcesosIniciales();
    }

    public void iniciarSimulacion() {
        reloj.iniciar();
        reloj.start(); 
        System.out.println(">>> Sistema Operativo Iniciado y Corriendo...");
    }

    /**
     * Ciclo principal del Kernel (Se ejecuta cada 'tic' del reloj)
     */
    public synchronized void ejecutarCiclo() {
        this.contadorCiclos++;
        System.out.println("\n--- CICLO " + contadorCiclos + " ---");

        // --- GENERADOR AUTOMÁTICO PARA PRUEBAS ---
        // Si el ciclo es múltiplo de 12, creamos un proceso nuevo
        // Esto evita que el sistema se quede "Ocioso" para siempre
        if (contadorCiclos % 12 == 0) {
            crearProcesoAleatorio();
        }
        // ------------------------------------------

        // 1. Planificador a Largo Plazo: Nuevos -> Listos
        checkColaNuevos();

        // 2. Manejar Procesos Bloqueados (I/O)
        checkColaBloqueados();

        // 3. Planificador a Corto Plazo: CPU -> Proceso
        planificarCPU();
        
        // 4. Ejecutar Proceso Actual
        ejecutarProcesoActual();
    }
    
    // --- LÓGICA DE GESTIÓN ---

    private void checkColaNuevos() {
        while (!colaNuevos.esVacia()) {
            PCB p = colaNuevos.desencolar();
            p.setEstado(Estado.LISTO);
            p.setTiempoLlegada(contadorCiclos); 
            colaListos.insertar(p);
            System.out.println("   [NUEVO -> LISTO] Entra a RAM: " + p.getNombre());
        }
    }
    
    private void checkColaBloqueados() {
        if (colaBloqueados.esVacia()) return;

        // Lista temporal para guardar los que terminan I/O
        Lista<PCB> listosParaSalir = new Lista<>();
        
        Nodo<PCB> actual = colaBloqueados.getpFirst();
        
        while (actual != null) {
            PCB p = actual.getContenido();
            p.incrementarContadorES();
            
            // ¿Ya completó su tiempo de E/S?
            if (p.getContadorES() >= p.getCiclosParaCompletarES()) {
                p.resetContadorES();
                p.setEstado(Estado.LISTO);
                listosParaSalir.insertar(p); 
            }
            actual = actual.getSiguiente();
        }
        
        // Mover los que terminaron de Bloqueados a Listos
        Nodo<PCB> aMover = listosParaSalir.getpFirst();
        while (aMover != null) {
            PCB p = aMover.getContenido();
            // Sacar de bloqueados
            colaBloqueados.eliminar(p);
            // Meter a listos
            colaListos.insertar(p);
            System.out.println("   [BLOQ -> LISTO] " + p.getNombre() + " terminó I/O.");
            aMover = aMover.getSiguiente();
        }
    }

    private void planificarCPU() {
        // Algoritmo FCFS (First-Come, First-Served)
        if (procesoEnEjecucion == null) {
            if (!colaListos.esVacia()) {
                PCB siguiente = colaListos.getpFirst().getContenido();
                colaListos.eliminar(siguiente);
                
                procesoEnEjecucion = siguiente;
                procesoEnEjecucion.setEstado(Estado.EJECUCION);
                System.out.println("   [DISPATCH] CPU asignado a: " + procesoEnEjecucion.getNombre());
            } else {
                System.out.println("   [CPU] Ocioso (Esperando procesos...)");
            }
        }
    }
    
    private void ejecutarProcesoActual() {
        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.ejecutarInstruccion();
            
            System.out.println("   [EJECUCION] " + procesoEnEjecucion.getNombre() + 
                               " | PC: " + procesoEnEjecucion.getProgramCounter() + 
                               "/" + procesoEnEjecucion.getInstruccionesTotales());

            // A. ¿Terminó?
            if (procesoEnEjecucion.haTerminado()) {
                terminarProceso(procesoEnEjecucion);
                return;
            }
            
            // B. ¿Bloqueo por E/S?
            if (procesoEnEjecucion.debeBloquearse()) {
                bloquearProceso(procesoEnEjecucion);
            }
        }
    }

    private void terminarProceso(PCB p) {
        p.setEstado(Estado.TERMINADO);
        colaTerminados.insertar(p);
        procesoEnEjecucion = null; 
        System.out.println("   [TERMINADO] Proceso " + p.getNombre() + " finalizó.");
    }
    
    private void bloquearProceso(PCB p) {
        p.setEstado(Estado.BLOQUEADO);
        colaBloqueados.insertar(p);
        procesoEnEjecucion = null; 
        System.out.println("   [EJEC -> BLOQ] Proceso " + p.getNombre() + " va a I/O.");
    }

    // --- GENERADORES ---
    
    private void generarProcesosIniciales() {
        for (int i = 0; i < 3; i++) { // Empezamos con 3
            crearProcesoAleatorio();
        }
    }

    // Método auxiliar para crear 1 proceso extra (usado al inicio y durante la ejecución)
    private void crearProcesoAleatorio() {
        Random rand = new Random();
        String nombre = "Proceso_" + contadorIdGlobal++; // Nombre simple
        int instrucciones = rand.nextInt(30) + 5;
        int prioridad = rand.nextInt(3) + 1; 
        int deadline = rand.nextInt(100) + instrucciones;
        
        boolean tieneES = rand.nextBoolean();
        int cicloES = (tieneES && instrucciones > 2) ? rand.nextInt(instrucciones - 2) + 1 : 0;
        int duracionES = tieneES ? 3 : 0; // 3 ciclos de I/O
        
        PCB nuevo = new PCB(nombre, instrucciones, prioridad, deadline, tieneES, cicloES, duracionES);
        colaNuevos.encolar(nuevo);
        // System.out.println("   >>> [GENERADOR] Nuevo proceso en cola: " + nombre);
    }
    
    // Pequeño contador para nombres únicos
    private int contadorIdGlobal = 1;
}