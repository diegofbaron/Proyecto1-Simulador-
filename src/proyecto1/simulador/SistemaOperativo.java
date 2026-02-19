/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import estructuras.*;
import proyecto1.simulador.planificadores.*;

/**
 * Sistema Operativo de Tiempo Real para gestión de microsatélite.
 */
public class SistemaOperativo {
    
    // Interfaz gráfica
    private VentanaSimulacion ventana;
    
    // Colas de procesos
    private Lista<PCB> colaListos;
    private Lista<PCB> colaBloqueados;
    private Lista<PCB> colaSuspendidosListos;    // Listo-Suspendido (Swap)
    private Lista<PCB> colaSuspendidosBloqueados; // Bloqueado-Suspendido (Swap)
    private Lista<PCB> colaTerminados;
    
    // CPU
    private PCB procesoEnEjecucion;
    private boolean modoKernel; // true = SO ejecutando, false = proceso usuario
    
    // Planificadores
    private Planificador planificadorActual;
    private PlanificadorFCFS planificadorFCFS;
    private PlanificadorRoundRobin planificadorRR;
    private PlanificadorSRT planificadorSRT;
    private PlanificadorPrioridadEstatica planificadorPrioridad;
    private PlanificadorEDF planificadorEDF;
    
    // Reloj y ciclos
    private Reloj reloj;
    private int contadorCiclos;
    
    // Configuración de memoria
    private final int MAX_RAM = 10;
    
    // Semáforos para exclusión mutua
    private Semaforo semaforoColas;
    private Semaforo semaforoCPU;
    private Semaforo semaforoLog;
    
    // Métricas y log
    private Metricas metricas;
    private Lista<String> logEventos;
    private GeneradorProcesos generadorProcesos;
    
    // Control de interrupciones
    private volatile boolean interrupcionActiva;
    private HiloInterrupcion interrupcionActual;
    
    // Quantum para Round Robin
    private int quantum = 4;
    
    // Control de estado del reloj
    private boolean relojIniciado = false;

    public SistemaOperativo(VentanaSimulacion ventana) {
        this.ventana = ventana;
        
        // Inicializar colas
        this.colaListos = new Lista<>();
        this.colaBloqueados = new Lista<>();
        this.colaSuspendidosListos = new Lista<>();
        this.colaSuspendidosBloqueados = new Lista<>();
        this.colaTerminados = new Lista<>();
        
        this.procesoEnEjecucion = null;
        this.modoKernel = false;
        
        // Inicializar planificadores
        this.planificadorFCFS = new PlanificadorFCFS();
        this.planificadorRR = new PlanificadorRoundRobin(quantum);
        this.planificadorSRT = new PlanificadorSRT();
        this.planificadorPrioridad = new PlanificadorPrioridadEstatica();
        this.planificadorEDF = new PlanificadorEDF();
        this.planificadorActual = planificadorFCFS; // Por defecto
        
        // Inicializar reloj
        this.reloj = new Reloj(1000);
        this.reloj.setSistemaOperativo(this);
        this.contadorCiclos = 0;
        
        // Inicializar semáforos
        this.semaforoColas = new Semaforo(1);
        this.semaforoCPU = new Semaforo(1);
        this.semaforoLog = new Semaforo(1);
        
        // Inicializar métricas y generador
        this.metricas = new Metricas();
        this.logEventos = new Lista<>();
        this.generadorProcesos = new GeneradorProcesos();
        
        this.interrupcionActiva = false;
        this.interrupcionActual = null;
        
        // Generar procesos iniciales
        generarProcesosPrueba();
    }

    /**
     * Ejecuta un ciclo del sistema operativo.
     */
    public void ejecutarCiclo() {
        semaforoCPU.acquire();
        try {
            contadorCiclos++;
            modoKernel = true;
            
            // Registrar ciclo para métricas
            metricas.registrarCiclo(procesoEnEjecucion != null);
            
            // 1. Verificar deadlines
            verificarDeadlines();
            
            // 2. Manejar swap (memoria)
            manejarSwap();
            
            // 3. Verificar procesos bloqueados
            verificarBloqueados();
            
            // 4. Ordenar cola según algoritmo
            planificadorActual.ordenarCola(colaListos, contadorCiclos);
            
            // 5. Verificar preempción
            if (planificadorActual.esPreemptivo() && procesoEnEjecucion != null) {
                if (planificadorActual.debeHacerPreempcion(colaListos, procesoEnEjecucion, contadorCiclos)) {
                    registrarEvento("Preempción: " + procesoEnEjecucion.getNombre() + " desalojado");
                    procesoEnEjecucion.setEstado(Estado.LISTO);
                    colaListos.insertar(procesoEnEjecucion);
                    procesoEnEjecucion = null;
                }
            }
            
            // 6. Planificar CPU
            planificarCPU();
            
            modoKernel = false;
            
            // 7. Ejecutar instrucción del proceso actual
            if (procesoEnEjecucion != null && !interrupcionActiva) {
                procesoEnEjecucion.marcarPrimeraEjecucion(contadorCiclos);
                procesoEnEjecucion.ejecutarInstruccion();
                
                // Decrementar quantum para Round Robin
                if (planificadorActual instanceof PlanificadorRoundRobin) {
                    procesoEnEjecucion.decrementarQuantum();
                }
                
                // Verificar si terminó
                if (procesoEnEjecucion.haTerminado()) {
                    finalizarProceso(procesoEnEjecucion);
                    procesoEnEjecucion = null;
                } 
                // Verificar si debe bloquearse por E/S
                else if (procesoEnEjecucion.debeBloquearse()) {
                    registrarEvento("E/S: " + procesoEnEjecucion.getNombre() + " bloqueado");
                    procesoEnEjecucion.setEstado(Estado.BLOQUEADO);
                    colaBloqueados.insertar(procesoEnEjecucion);
                    procesoEnEjecucion = null;
                }
            }
            
            // 8. Incrementar tiempo de espera de procesos en cola
            incrementarTiemposEspera();
            
            // 9. Actualizar interfaz
            actualizarInterfaz();
            
        } finally {
            semaforoCPU.release();
        }
    }
    
    private void verificarDeadlines() {
        semaforoColas.acquire();
        try {
            // Verificar proceso en ejecución
            if (procesoEnEjecucion != null && procesoEnEjecucion.deadlineExcedido(contadorCiclos)) {
                registrarEvento("⚠ FALLO DEADLINE: " + procesoEnEjecucion.getNombre());
                metricas.registrarDeadlineFallido();
            }
            
            // Verificar cola de listos
            Nodo<PCB> aux = colaListos.getpFirst();
            while (aux != null) {
                PCB p = aux.getContenido();
                if (p.deadlineExcedido(contadorCiclos)) {
                    registrarEvento("⚠ FALLO DEADLINE (en espera): " + p.getNombre());
                    metricas.registrarDeadlineFallido();
                }
                aux = aux.getSiguiente();
            }
        } finally {
            semaforoColas.release();
        }
    }
    
    private void manejarSwap() {
        semaforoColas.acquire();
        try {
            int enRam = colaListos.getSize() + colaBloqueados.getSize() + 
                       (procesoEnEjecucion != null ? 1 : 0);

            // SWAP OUT: Si RAM llena, mover a disco (priorizando procesos con deadline lejano)
            while (enRam > MAX_RAM && !colaListos.esVacia()) {
                PCB p = encontrarProcesoParaSwapOut();
                if (p != null) {
                    p.setEstado(Estado.LISTO_SUSPENDIDO);
                    colaListos.eliminar(p);
                    colaSuspendidosListos.insertar(p);
                    registrarEvento("SWAP OUT: " + p.getNombre() + " movido a disco");
                    enRam--;
                } else {
                    break;
                }
            }

            // SWAP IN: Si hay espacio, traer del disco
            while (enRam < MAX_RAM && !colaSuspendidosListos.esVacia()) {
                PCB p = encontrarProcesoParaSwapIn();
                if (p != null) {
                    p.setEstado(Estado.LISTO);
                    colaSuspendidosListos.eliminar(p);
                    colaListos.insertar(p);
                    registrarEvento("SWAP IN: " + p.getNombre() + " traído a RAM");
                    enRam++;
                } else {
                    break;
                }
            }
        } finally {
            semaforoColas.release();
        }
    }
    
    private PCB encontrarProcesoParaSwapOut() {
        // Seleccionar proceso con deadline más lejano (menos urgente)
        if (colaListos.esVacia()) return null;
        
        PCB candidato = null;
        int mayorDeadline = -1;
        
        Nodo<PCB> aux = colaListos.getpFirst();
        while (aux != null) {
            PCB p = aux.getContenido();
            if (p.getDeadline() > mayorDeadline) {
                mayorDeadline = p.getDeadline();
                candidato = p;
            }
            aux = aux.getSiguiente();
        }
        return candidato;
    }
    
    private PCB encontrarProcesoParaSwapIn() {
        // Seleccionar proceso con deadline más cercano (más urgente)
        if (colaSuspendidosListos.esVacia()) return null;
        
        PCB candidato = null;
        int menorDeadline = Integer.MAX_VALUE;
        
        Nodo<PCB> aux = colaSuspendidosListos.getpFirst();
        while (aux != null) {
            PCB p = aux.getContenido();
            if (p.getDeadline() < menorDeadline) {
                menorDeadline = p.getDeadline();
                candidato = p;
            }
            aux = aux.getSiguiente();
        }
        return candidato;
    }

    private void verificarBloqueados() {
        semaforoColas.acquire();
        try {
            if (colaBloqueados.esVacia()) return;
            
            Lista<PCB> listosParaVolver = new Lista<>();
            Nodo<PCB> aux = colaBloqueados.getpFirst();
            
            while (aux != null) {
                PCB p = aux.getContenido();
                p.incrementarContadorES();
                if (p.getContadorES() >= p.getCiclosParaCompletarES()) {
                    listosParaVolver.insertar(p);
                }
                aux = aux.getSiguiente();
            }
            
            // Mover procesos listos
            Nodo<PCB> n = listosParaVolver.getpFirst();
            while (n != null) {
                PCB p = n.getContenido();
                p.resetContadorES();
                p.setEstado(Estado.LISTO);
                colaBloqueados.eliminar(p);
                colaListos.insertar(p);
                registrarEvento("E/S completada: " + p.getNombre() + " listo");
                n = n.getSiguiente();
            }
        } finally {
            semaforoColas.release();
        }
    }

    private void planificarCPU() {
        if (procesoEnEjecucion == null && !colaListos.esVacia()) {
            semaforoColas.acquire();
            try {
                PCB seleccionado = planificadorActual.seleccionarProceso(colaListos, null, contadorCiclos);
                if (seleccionado != null) {
                    colaListos.eliminar(seleccionado);
                    procesoEnEjecucion = seleccionado;
                    procesoEnEjecucion.setEstado(Estado.EJECUCION);
                    
                    // Asignar quantum si es Round Robin
                    if (planificadorActual instanceof PlanificadorRoundRobin) {
                        procesoEnEjecucion.setQuantumRestante(quantum);
                    }
                    
                    registrarEvento("CPU: Ejecutando " + procesoEnEjecucion.getNombre());
                }
            } finally {
                semaforoColas.release();
            }
        }
    }
    
    private void finalizarProceso(PCB proceso) {
        proceso.setEstado(Estado.TERMINADO);
        colaTerminados.insertar(proceso);
        metricas.registrarProcesoCompletado(proceso, contadorCiclos);
        
        String resultado = proceso.deadlineExcedido(contadorCiclos) ? 
            " (DEADLINE EXCEDIDO)" : " (A TIEMPO)";
        registrarEvento("✓ COMPLETADO: " + proceso.getNombre() + resultado);
    }
    
    private void incrementarTiemposEspera() {
        Nodo<PCB> aux = colaListos.getpFirst();
        while (aux != null) {
            aux.getContenido().incrementarTiempoEspera();
            aux = aux.getSiguiente();
        }
    }

    // ==================== MANEJO DE INTERRUPCIONES ====================
    
    public void manejarInterrupcion(HiloInterrupcion interrupcion) {
        semaforoCPU.acquire();
        try {
            interrupcionActiva = true;
            interrupcionActual = interrupcion;
            modoKernel = true;
            
            // Suspender proceso actual si existe
            if (procesoEnEjecucion != null) {
                registrarEvento("Proceso " + procesoEnEjecucion.getNombre() + " suspendido por interrupción");
                procesoEnEjecucion.setEstado(Estado.LISTO);
                colaListos.insertarAlInicio(procesoEnEjecucion); // Reinsertar al inicio
                procesoEnEjecucion = null;
            }
            
            actualizarInterfaz();
        } finally {
            semaforoCPU.release();
        }
    }
    
    public void finalizarInterrupcion(HiloInterrupcion interrupcion) {
        interrupcionActiva = false;
        interrupcionActual = null;
        modoKernel = false;
    }
    
    public void agregarProcesoEmergencia(PCB proceso) {
        semaforoColas.acquire();
        try {
            proceso.setEstado(Estado.LISTO);
            colaListos.insertarAlInicio(proceso); // Alta prioridad, al inicio
            metricas.registrarNuevoProceso();
            registrarEvento("🚨 EMERGENCIA: " + proceso.getNombre() + " añadido");
        } finally {
            semaforoColas.release();
        }
    }
    
    /**
     * Dispara una interrupción de emergencia con un nuevo proceso.
     */
    public void interrupcionEmergencia() {
        PCB procesoEmergencia = generadorProcesos.generarProcesoEmergencia(contadorCiclos);
        HiloInterrupcion interrupcion = new HiloInterrupcion(this, "EMERGENCIA_MISION", procesoEmergencia);
        interrupcion.start();
    }
    
    /**
     * Dispara una interrupción genérica (sin proceso nuevo).
     */
    public void interrupcionGenerica(String tipo) {
        HiloInterrupcion interrupcion = new HiloInterrupcion(this, tipo);
        interrupcion.start();
    }

    // ==================== GESTIÓN DE PROCESOS ====================
    
    public void agregarProceso(PCB proceso) {
        semaforoColas.acquire();
        try {
            proceso.setEstado(Estado.NUEVO);
            
            // Verificar si hay espacio en RAM
            int enRam = colaListos.getSize() + colaBloqueados.getSize() + 
                       (procesoEnEjecucion != null ? 1 : 0);
            
            if (enRam < MAX_RAM) {
                proceso.setEstado(Estado.LISTO);
                colaListos.insertar(proceso);
            } else {
                proceso.setEstado(Estado.LISTO_SUSPENDIDO);
                colaSuspendidosListos.insertar(proceso);
                registrarEvento("RAM llena: " + proceso.getNombre() + " enviado a SWAP");
            }
            
            metricas.registrarNuevoProceso();
        } finally {
            semaforoColas.release();
        }
    }
    
    public void agregarProcesoAleatorio() {
        PCB proceso = generadorProcesos.generarProcesoAleatorio(contadorCiclos);
        agregarProceso(proceso);
        registrarEvento("+ Nuevo proceso: " + proceso.getNombre());
    }
    
    public void generar20Procesos() {
        Lista<PCB> procesos = generadorProcesos.generarProcesosAleatorios(20, contadorCiclos);
        Nodo<PCB> aux = procesos.getpFirst();
        while (aux != null) {
            agregarProceso(aux.getContenido());
            aux = aux.getSiguiente();
        }
        registrarEvento("++ Generados 20 procesos aleatorios");
    }
    
    public void cargarProcesosDesdeCSV(String ruta) {
        Lista<PCB> procesos = generadorProcesos.cargarDesdeCSV(ruta, contadorCiclos);
        Nodo<PCB> aux = procesos.getpFirst();
        int count = 0;
        while (aux != null) {
            agregarProceso(aux.getContenido());
            count++;
            aux = aux.getSiguiente();
        }
        registrarEvento("Cargados " + count + " procesos desde CSV");
    }
    
    public void cargarProcesosDesdeJSON(String ruta) {
        Lista<PCB> procesos = generadorProcesos.cargarDesdeJSON(ruta, contadorCiclos);
        Nodo<PCB> aux = procesos.getpFirst();
        int count = 0;
        while (aux != null) {
            agregarProceso(aux.getContenido());
            count++;
            aux = aux.getSiguiente();
        }
        registrarEvento("Cargados " + count + " procesos desde JSON");
    }

    // ==================== CAMBIO DE PLANIFICADOR ====================
    
    public void cambiarPlanificador(String nombre) {
        semaforoCPU.acquire();
        try {
            switch (nombre.toUpperCase()) {
                case "FCFS":
                    planificadorActual = planificadorFCFS;
                    break;
                case "RR":
                case "ROUND ROBIN":
                    planificadorActual = planificadorRR;
                    break;
                case "SRT":
                    planificadorActual = planificadorSRT;
                    break;
                case "PRIORIDAD":
                case "PRIORIDAD ESTATICA":
                    planificadorActual = planificadorPrioridad;
                    break;
                case "EDF":
                    planificadorActual = planificadorEDF;
                    break;
            }
            registrarEvento("Algoritmo cambiado a: " + planificadorActual.getNombre());
            planificadorActual.ordenarCola(colaListos, contadorCiclos);
        } finally {
            semaforoCPU.release();
        }
    }
    
    public void setQuantum(int nuevoQuantum) {
        this.quantum = nuevoQuantum;
        planificadorRR.setQuantum(nuevoQuantum);
        registrarEvento("Quantum actualizado a: " + nuevoQuantum);
    }

    // ==================== LOG DE EVENTOS ====================
    
    public void registrarEvento(String evento) {
        semaforoLog.acquire();
        try {
            String timestamp = "[Ciclo " + contadorCiclos + "] ";
            logEventos.insertar(timestamp + evento);
            
            // Mantener solo los últimos 100 eventos
            while (logEventos.getSize() > 100) {
                logEventos.extraerPrimero();
            }
        } finally {
            semaforoLog.release();
        }
    }

    // ==================== INTERFAZ ====================
    
    private void actualizarInterfaz() {
        int ramCount = colaListos.getSize() + colaBloqueados.getSize() + 
                      (procesoEnEjecucion != null ? 1 : 0);
        
        ventana.updateView(
            contadorCiclos,
            procesoEnEjecucion,
            colaListos.toStringArray(),
            colaBloqueados.toStringArray(),
            colaSuspendidosListos.toStringArray(),
            colaSuspendidosBloqueados.toStringArray(),
            colaTerminados.toStringArray(),
            ramCount,
            MAX_RAM,
            modoKernel,
            planificadorActual.getNombre(),
            metricas,
            logEventos.toStringArray()
        );
    }

    private void generarProcesosPrueba() {
        Lista<PCB> procesosIniciales = generadorProcesos.generarProcesosIniciales();
        Nodo<PCB> aux = procesosIniciales.getpFirst();
        while (aux != null) {
            agregarProceso(aux.getContenido());
            aux = aux.getSiguiente();
        }
    }

    // ==================== CONTROL ====================
    
    public void iniciarSimulacion() {
        if (!relojIniciado) {
            reloj.start();
            relojIniciado = true;
        }
        reloj.iniciar();
        ventana.actualizarBotonesSimulacion(true); // Simulación corriendo
        registrarEvento("=== SIMULACIÓN INICIADA ===");
    }
    
    public void pausarSimulacion() {
        reloj.pausar();
        ventana.actualizarBotonesSimulacion(false); // Simulación pausada
        registrarEvento("=== SIMULACIÓN PAUSADA ===");
    }
    
    public void reanudarSimulacion() {
        reloj.iniciar();
        ventana.actualizarBotonesSimulacion(true); // Simulación corriendo
        registrarEvento("=== SIMULACIÓN REANUDADA ===");
    }
    
    public void reiniciarSimulacion() {
        // Pausar el reloj
        reloj.pausar();
        
        // Limpiar todas las colas
        semaforoColas.acquire();
        try {
            colaListos.limpiar();
            colaBloqueados.limpiar();
            colaSuspendidosListos.limpiar();
            colaSuspendidosBloqueados.limpiar();
            colaTerminados.limpiar();
        } finally {
            semaforoColas.release();
        }
        
        // Resetear CPU
        procesoEnEjecucion = null;
        modoKernel = false;
        
        // Resetear ciclos
        contadorCiclos = 0;
        reloj.resetCiclos();
        
        // Resetear métricas
        metricas.reset();
        
        // Limpiar log
        logEventos.limpiar();
        
        // Resetear interrupciones
        interrupcionActiva = false;
        interrupcionActual = null;
        
        // Generar nuevos procesos iniciales
        generadorProcesos = new GeneradorProcesos();
        generarProcesosPrueba();
        
        // Actualizar interfaz
        ventana.actualizarBotonesSimulacion(false);
        actualizarInterfaz();
        
        registrarEvento("=== SIMULACIÓN REINICIADA ===");
    }
    
    public void setVelocidad(int tiempoMs) {
        reloj.setTiempoCiclo(tiempoMs);
        registrarEvento("Velocidad cambiada a: " + tiempoMs + "ms/ciclo");
    }
    
    // Getters para la interfaz
    public Metricas getMetricas() { return metricas; }
    public int getCicloActual() { return contadorCiclos; }
    public String getPlanificadorActual() { return planificadorActual.getNombre(); }
    public boolean isModoKernel() { return modoKernel; }
}
