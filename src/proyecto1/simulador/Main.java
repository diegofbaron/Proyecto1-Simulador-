/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar look and feel por defecto
        }
        
        SwingUtilities.invokeLater(() -> {
            VentanaSimulacion ventana = new VentanaSimulacion();
            SistemaOperativo so = new SistemaOperativo(ventana);
            ventana.setSistemaOperativo(so);

            ventana.setAcciones(
                // Iniciar simulación
                e -> so.iniciarSimulacion(),
                
                // Pausar
                e -> so.pausarSimulacion(),
                
                // Interrupción de emergencia
                e -> so.interrupcionEmergencia(),
                
                // Agregar proceso aleatorio
                e -> so.agregarProcesoAleatorio(),
                
                // Generar 20 procesos
                e -> so.generar20Procesos(),
                
                // Cargar desde archivo
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Seleccionar archivo de procesos");
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.isDirectory() || 
                                   f.getName().toLowerCase().endsWith(".csv") ||
                                   f.getName().toLowerCase().endsWith(".json");
                        }
                        @Override
                        public String getDescription() {
                            return "Archivos CSV o JSON (*.csv, *.json)";
                        }
                    });
                    
                    int result = fileChooser.showOpenDialog(ventana);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File archivo = fileChooser.getSelectedFile();
                        String ruta = archivo.getAbsolutePath();
                        
                        if (ruta.toLowerCase().endsWith(".csv")) {
                            so.cargarProcesosDesdeCSV(ruta);
                        } else if (ruta.toLowerCase().endsWith(".json")) {
                            so.cargarProcesosDesdeJSON(ruta);
                        } else {
                            JOptionPane.showMessageDialog(ventana, 
                                "Formato no soportado. Use CSV o JSON.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                },
                
                // Reanudar simulación
                e -> so.reanudarSimulacion(),
                
                // Reiniciar simulación
                e -> {
                    int confirm = JOptionPane.showConfirmDialog(ventana,
                        "¿Está seguro de reiniciar la simulación?\nSe perderá todo el progreso actual.",
                        "Confirmar Reinicio",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        so.reiniciarSimulacion();
                    }
                }
            );

            ventana.setVisible(true);
        });
    }
}