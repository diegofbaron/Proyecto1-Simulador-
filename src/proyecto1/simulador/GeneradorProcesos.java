package proyecto1.simulador;

import estructuras.Lista;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Genera procesos aleatorios o desde archivos JSON/CSV.
 */
public class GeneradorProcesos {
    
    private static final String[] NOMBRES_TAREAS = {
        "Telemetria", "CamaraRGB", "AntenaL", "SensorTemp", 
        "Giroscopio", "PanelSolar", "EnlaceTierra", "AnalisisUV",
        "RadarAltitud", "GPS", "Magnetometro", "Acelerometro",
        "ControlTermico", "Bateria", "Propulsion", "Comunicacion",
        "NavegacionEstelar", "DetectorRadiacion", "Espectometro", "LiDAR"
    };
    
    private static final String[] NOMBRES_EMERGENCIA = {
        "AlertaMicroMeteoro", "RafagaSolar", "FalloSensor", "SobrecargaTermica",
        "PerdidaSenal", "ColisionInminente", "RadiacionAlta", "FalloBateria"
    };
    
    private Random random;
    private int contadorProcesos;
    
    public GeneradorProcesos() {
        this.random = new Random();
        this.contadorProcesos = 0;
    }
    
    /**
     * Genera un proceso aleatorio.
     */
    public PCB generarProcesoAleatorio(int cicloActual) {
        String nombre = NOMBRES_TAREAS[random.nextInt(NOMBRES_TAREAS.length)] + "_" + (++contadorProcesos);
        int instrucciones = 10 + random.nextInt(41);      // 10-50 instrucciones
        int prioridad = 1 + random.nextInt(10);           // Prioridad 1-10
        int deadline = cicloActual + 30 + random.nextInt(71); // Deadline en 30-100 ciclos
        boolean tieneES = random.nextBoolean();
        int cicloES = tieneES ? 3 + random.nextInt(instrucciones - 5) : 0;
        int duracionES = tieneES ? 2 + random.nextInt(4) : 0;
        int periodo = random.nextInt(3) == 0 ? 50 + random.nextInt(50) : 0; // 33% son periódicas
        
        return new PCB(nombre, instrucciones, prioridad, deadline, 
                      tieneES, cicloES, duracionES, cicloActual, periodo);
    }
    
    /**
     * Genera un proceso de emergencia (alta prioridad, deadline corto).
     */
    public PCB generarProcesoEmergencia(int cicloActual) {
        String nombre = NOMBRES_EMERGENCIA[random.nextInt(NOMBRES_EMERGENCIA.length)] + "_" + (++contadorProcesos);
        int instrucciones = 5 + random.nextInt(16);       // 5-20 instrucciones
        int prioridad = 1;                                 // Máxima prioridad
        int deadline = cicloActual + 10 + random.nextInt(21); // Deadline muy corto: 10-30 ciclos
        boolean tieneES = false;                           // Sin E/S para mayor urgencia
        
        return new PCB(nombre, instrucciones, prioridad, deadline, 
                      tieneES, 0, 0, cicloActual, 0);
    }
    
    /**
     * Genera múltiples procesos aleatorios.
     */
    public Lista<PCB> generarProcesosAleatorios(int cantidad, int cicloActual) {
        Lista<PCB> procesos = new Lista<>();
        for (int i = 0; i < cantidad; i++) {
            procesos.insertar(generarProcesoAleatorio(cicloActual));
        }
        return procesos;
    }
    
    /**
     * Genera el conjunto inicial de procesos de prueba.
     */
    public Lista<PCB> generarProcesosIniciales() {
        Lista<PCB> procesos = new Lista<>();
        
        // Generar 8 procesos iniciales con características diversas
        for (int i = 0; i < 8; i++) {
            String nombre = NOMBRES_TAREAS[i] + "_" + (++contadorProcesos);
            int instrucciones = 15 + (i * 3);
            int prioridad = 1 + (i % 5);
            int deadline = 50 + (i * 15);
            boolean tieneES = (i % 2 == 0);
            int cicloES = tieneES ? 5 + i : 0;
            int duracionES = tieneES ? 3 : 0;
            int periodo = (i % 3 == 0) ? 40 + (i * 10) : 0;
            
            procesos.insertar(new PCB(nombre, instrucciones, prioridad, deadline, 
                                     tieneES, cicloES, duracionES, 0, periodo));
        }
        
        return procesos;
    }
    
    /**
     * Carga procesos desde un archivo CSV.
     * Formato: nombre,instrucciones,prioridad,deadline,tieneES,cicloES,duracionES,periodo
     */
    public Lista<PCB> cargarDesdeCSV(String rutaArchivo, int cicloActual) {
        Lista<PCB> procesos = new Lista<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = br.readLine()) != null) {
                // Saltar cabecera
                if (primeraLinea) {
                    primeraLinea = false;
                    if (linea.toLowerCase().contains("nombre")) continue;
                }
                
                String[] campos = linea.split(",");
                if (campos.length >= 7) {
                    try {
                        String nombre = campos[0].trim();
                        int instrucciones = Integer.parseInt(campos[1].trim());
                        int prioridad = Integer.parseInt(campos[2].trim());
                        int deadline = cicloActual + Integer.parseInt(campos[3].trim());
                        boolean tieneES = Boolean.parseBoolean(campos[4].trim());
                        int cicloES = Integer.parseInt(campos[5].trim());
                        int duracionES = Integer.parseInt(campos[6].trim());
                        int periodo = campos.length > 7 ? Integer.parseInt(campos[7].trim()) : 0;
                        
                        procesos.insertar(new PCB(nombre + "_" + (++contadorProcesos), 
                            instrucciones, prioridad, deadline, tieneES, cicloES, duracionES, cicloActual, periodo));
                    } catch (NumberFormatException e) {
                        // Ignorar líneas con formato incorrecto
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar archivo CSV: " + e.getMessage());
        }
        
        return procesos;
    }
    
    /**
     * Carga procesos desde un archivo JSON simple.
     * Formato esperado: array de objetos con campos del PCB
     */
    public Lista<PCB> cargarDesdeJSON(String rutaArchivo, int cicloActual) {
        Lista<PCB> procesos = new Lista<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }
            
            String json = contenido.toString();
            // Parser JSON simple (sin librerías externas)
            String[] objetos = json.split("\\{");
            
            for (String obj : objetos) {
                if (obj.contains("nombre")) {
                    try {
                        String nombre = extraerValorJSON(obj, "nombre");
                        int instrucciones = Integer.parseInt(extraerValorJSON(obj, "instrucciones"));
                        int prioridad = Integer.parseInt(extraerValorJSON(obj, "prioridad"));
                        int deadline = cicloActual + Integer.parseInt(extraerValorJSON(obj, "deadline"));
                        boolean tieneES = Boolean.parseBoolean(extraerValorJSON(obj, "tieneES"));
                        int cicloES = Integer.parseInt(extraerValorJSON(obj, "cicloES"));
                        int duracionES = Integer.parseInt(extraerValorJSON(obj, "duracionES"));
                        int periodo = 0;
                        try {
                            periodo = Integer.parseInt(extraerValorJSON(obj, "periodo"));
                        } catch (Exception e) {}
                        
                        procesos.insertar(new PCB(nombre + "_" + (++contadorProcesos), 
                            instrucciones, prioridad, deadline, tieneES, cicloES, duracionES, cicloActual, periodo));
                    } catch (Exception e) {
                        // Ignorar objetos mal formados
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar archivo JSON: " + e.getMessage());
        }
        
        return procesos;
    }
    
    private String extraerValorJSON(String obj, String campo) {
        int inicio = obj.indexOf("\"" + campo + "\"");
        if (inicio == -1) return "";
        
        inicio = obj.indexOf(":", inicio) + 1;
        int fin = obj.indexOf(",", inicio);
        if (fin == -1) fin = obj.indexOf("}", inicio);
        if (fin == -1) fin = obj.length();
        
        String valor = obj.substring(inicio, fin).trim();
        // Remover comillas si las tiene
        valor = valor.replace("\"", "").replace("'", "");
        return valor;
    }
}
