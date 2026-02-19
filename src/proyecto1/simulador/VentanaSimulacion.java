/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.simulador;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class VentanaSimulacion extends JFrame {
    
    // Colores del tema espacial
    private final Color COLOR_FONDO = new Color(5, 10, 20);
    private final Color COLOR_PANEL = new Color(15, 20, 35);
    private final Color COLOR_CIAN = new Color(0, 255, 240);
    private final Color COLOR_BLANCO = new Color(255, 255, 255);
    private final Color COLOR_PELIGRO = new Color(255, 60, 60);
    private final Color COLOR_EXITO = new Color(60, 255, 60);
    private final Color COLOR_ADVERTENCIA = new Color(255, 200, 60);
    
    // Panel superior - Info del sistema
    private JLabel lblClock, lblModo, lblAlgoritmo;
    private JProgressBar barRAM;
    private JLabel lblRamInfo;
    
    // Panel CPU
    private JLabel lblProcesoActual, lblPcMar, lblDeadline, lblProgreso;
    private JProgressBar barCPU;
    
    // Listas de colas
    private JList<String> listReady, listBlocked, listSuspendedReady, listSuspendedBlocked, listTerminated;
    private DefaultListModel<String> modReady, modBlocked, modSuspendedReady, modSuspendedBlocked, modTerminated;
    
    // Panel de métricas
    private JLabel lblUsoCPU, lblThroughput, lblTasaExito, lblTiempoEspera;
    private JLabel lblProcesosCompletados, lblDeadlinesFallidos;
    
    // Log de eventos
    private JTextArea txtLog;
    
    // Controles
    private JComboBox<String> cmbAlgoritmo;
    private JSpinner spinnerQuantum, spinnerVelocidad;
    private JButton btnStart, btnPausar, btnReanudar, btnReiniciar, btnEmergency, btnAgregarProceso, btnGenerar20, btnCargarArchivo;
    
    // Referencia al SO
    private SistemaOperativo sistemaOperativo;

    public VentanaSimulacion() {
        setTitle("SIMULADOR RTOS - MICROSATÉLITE DE INVESTIGACIÓN");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(COLOR_FONDO);
        
        // Panel principal con scroll
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(COLOR_FONDO);
        
        // PANEL SUPERIOR - Información del sistema
        mainPanel.add(crearPanelSuperior(), BorderLayout.NORTH);
        
        // PANEL CENTRAL - Colas y CPU
        mainPanel.add(crearPanelCentral(), BorderLayout.CENTER);
        
        // PANEL INFERIOR - Controles y log
        mainPanel.add(crearPanelInferior(), BorderLayout.SOUTH);
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // Reloj
        JPanel panelReloj = crearPanelContenedor("RELOJ DEL SISTEMA");
        lblClock = new JLabel("CICLO: 0", SwingConstants.CENTER);
        lblClock.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblClock.setForeground(COLOR_CIAN);
        panelReloj.add(lblClock, BorderLayout.CENTER);
        panel.add(panelReloj);
        
        // Modo de operación
        JPanel panelModo = crearPanelContenedor("MODO DE OPERACIÓN");
        lblModo = new JLabel("USUARIO", SwingConstants.CENTER);
        lblModo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblModo.setForeground(COLOR_EXITO);
        panelModo.add(lblModo, BorderLayout.CENTER);
        panel.add(panelModo);
        
        // Algoritmo actual
        JPanel panelAlgo = crearPanelContenedor("ALGORITMO ACTIVO");
        lblAlgoritmo = new JLabel("FCFS", SwingConstants.CENTER);
        lblAlgoritmo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblAlgoritmo.setForeground(COLOR_BLANCO);
        panelAlgo.add(lblAlgoritmo, BorderLayout.CENTER);
        panel.add(panelAlgo);
        
        // Uso de RAM
        JPanel panelRam = crearPanelContenedor("MEMORIA RAM");
        JPanel ramContent = new JPanel(new BorderLayout(5, 5));
        ramContent.setBackground(COLOR_PANEL);
        lblRamInfo = new JLabel("0/10 procesos", SwingConstants.CENTER);
        lblRamInfo.setForeground(COLOR_BLANCO);
        lblRamInfo.setFont(new Font("SansSerif", Font.BOLD, 14));
        barRAM = new JProgressBar(0, 10);
        barRAM.setStringPainted(true);
        barRAM.setForeground(COLOR_CIAN);
        barRAM.setBackground(new Color(40, 40, 50));
        ramContent.add(lblRamInfo, BorderLayout.NORTH);
        ramContent.add(barRAM, BorderLayout.CENTER);
        panelRam.add(ramContent, BorderLayout.CENTER);
        panel.add(panelRam);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Panel izquierdo - CPU y métricas
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBackground(COLOR_FONDO);
        panelIzquierdo.setPreferredSize(new Dimension(350, 0));
        
        // CPU
        JPanel panelCPU = crearPanelContenedor("PROCESADOR CENTRAL (CPU)");
        panelCPU.setPreferredSize(new Dimension(350, 200));
        JPanel cpuContent = new JPanel(new GridLayout(5, 1, 5, 5));
        cpuContent.setBackground(COLOR_PANEL);
        
        lblProcesoActual = new JLabel("IDLE - Esperando proceso", SwingConstants.CENTER);
        lblProcesoActual.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblProcesoActual.setForeground(COLOR_BLANCO);
        
        lblPcMar = new JLabel("PC: 0 | MAR: 0", SwingConstants.CENTER);
        lblPcMar.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lblPcMar.setForeground(COLOR_CIAN);
        
        barCPU = new JProgressBar(0, 100);
        barCPU.setStringPainted(true);
        barCPU.setString("0%");
        barCPU.setForeground(new Color(0, 150, 255));
        barCPU.setBackground(new Color(40, 40, 50));
        
        lblProgreso = new JLabel("Progreso: 0/0 instrucciones", SwingConstants.CENTER);
        lblProgreso.setForeground(COLOR_BLANCO);
        
        lblDeadline = new JLabel("DEADLINE: -", SwingConstants.CENTER);
        lblDeadline.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblDeadline.setForeground(COLOR_PELIGRO);
        
        cpuContent.add(lblProcesoActual);
        cpuContent.add(lblPcMar);
        cpuContent.add(barCPU);
        cpuContent.add(lblProgreso);
        cpuContent.add(lblDeadline);
        panelCPU.add(cpuContent, BorderLayout.CENTER);
        panelIzquierdo.add(panelCPU, BorderLayout.NORTH);
        
        // Métricas
        JPanel panelMetricas = crearPanelContenedor("MÉTRICAS DE RENDIMIENTO");
        JPanel metricasContent = new JPanel(new GridLayout(6, 1, 3, 3));
        metricasContent.setBackground(COLOR_PANEL);
        
        lblUsoCPU = crearLabelMetrica("Uso CPU: 0.0%");
        lblThroughput = crearLabelMetrica("Throughput: 0.0%");
        lblTasaExito = crearLabelMetrica("Tasa Éxito Misión: 100.0%");
        lblTiempoEspera = crearLabelMetrica("Tiempo Espera Prom: 0.0");
        lblProcesosCompletados = crearLabelMetrica("Completados: 0");
        lblDeadlinesFallidos = crearLabelMetrica("Deadlines Fallidos: 0");
        
        metricasContent.add(lblUsoCPU);
        metricasContent.add(lblThroughput);
        metricasContent.add(lblTasaExito);
        metricasContent.add(lblTiempoEspera);
        metricasContent.add(lblProcesosCompletados);
        metricasContent.add(lblDeadlinesFallidos);
        panelMetricas.add(metricasContent, BorderLayout.CENTER);
        panelIzquierdo.add(panelMetricas, BorderLayout.CENTER);
        
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel central - Colas
        JPanel panelColas = new JPanel(new GridLayout(2, 3, 10, 10));
        panelColas.setBackground(COLOR_FONDO);
        
        // Cola de Listos
        modReady = new DefaultListModel<>();
        listReady = configurarJList(modReady);
        panelColas.add(crearScrollConTitulo(listReady, "COLA DE LISTOS (RAM)"));
        
        // Cola de Bloqueados
        modBlocked = new DefaultListModel<>();
        listBlocked = configurarJList(modBlocked);
        panelColas.add(crearScrollConTitulo(listBlocked, "BLOQUEADOS (E/S)"));
        
        // Terminados
        modTerminated = new DefaultListModel<>();
        listTerminated = configurarJList(modTerminated);
        panelColas.add(crearScrollConTitulo(listTerminated, "COMPLETADOS"));
        
        // Suspendidos Listos
        modSuspendedReady = new DefaultListModel<>();
        listSuspendedReady = configurarJList(modSuspendedReady);
        panelColas.add(crearScrollConTitulo(listSuspendedReady, "LISTO-SUSPENDIDO (SWAP)"));
        
        // Suspendidos Bloqueados
        modSuspendedBlocked = new DefaultListModel<>();
        listSuspendedBlocked = configurarJList(modSuspendedBlocked);
        panelColas.add(crearScrollConTitulo(listSuspendedBlocked, "BLOQUEADO-SUSPENDIDO (SWAP)"));
        
        // Log de eventos
        JPanel panelLog = crearPanelContenedor("LOG DE EVENTOS");
        txtLog = new JTextArea();
        txtLog.setBackground(COLOR_PANEL);
        txtLog.setForeground(COLOR_CIAN);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBackground(COLOR_PANEL);
        scrollLog.getViewport().setBackground(COLOR_PANEL);
        panelLog.add(scrollLog, BorderLayout.CENTER);
        panelColas.add(panelLog);
        
        panel.add(panelColas, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // Selector de algoritmo
        JLabel lblAlgo = new JLabel("Algoritmo:");
        lblAlgo.setForeground(COLOR_BLANCO);
        panel.add(lblAlgo);
        
        cmbAlgoritmo = new JComboBox<>(new String[]{"FCFS", "Round Robin", "SRT", "Prioridad Estática", "EDF"});
        cmbAlgoritmo.setPreferredSize(new Dimension(140, 30));
        cmbAlgoritmo.addActionListener(e -> {
            if (sistemaOperativo != null) {
                String seleccion = (String) cmbAlgoritmo.getSelectedItem();
                sistemaOperativo.cambiarPlanificador(seleccion);
            }
        });
        panel.add(cmbAlgoritmo);
        
        // Quantum
        JLabel lblQ = new JLabel("Quantum:");
        lblQ.setForeground(COLOR_BLANCO);
        panel.add(lblQ);
        
        spinnerQuantum = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        spinnerQuantum.setPreferredSize(new Dimension(55, 30));
        spinnerQuantum.addChangeListener(e -> {
            if (sistemaOperativo != null) {
                sistemaOperativo.setQuantum((Integer) spinnerQuantum.getValue());
            }
        });
        panel.add(spinnerQuantum);
        
        // Velocidad
        JLabel lblVel = new JLabel("Velocidad (ms):");
        lblVel.setForeground(COLOR_BLANCO);
        panel.add(lblVel);
        
        spinnerVelocidad = new JSpinner(new SpinnerNumberModel(1000, 50, 5000, 50));
        spinnerVelocidad.setPreferredSize(new Dimension(75, 30));
        spinnerVelocidad.addChangeListener(e -> {
            if (sistemaOperativo != null) {
                sistemaOperativo.setVelocidad((Integer) spinnerVelocidad.getValue());
            }
        });
        panel.add(spinnerVelocidad);
        
        // Botones de control de simulación
        btnStart = new JButton("▶ INICIAR");
        estilizarBoton(btnStart, COLOR_EXITO);
        panel.add(btnStart);
        
        btnPausar = new JButton("⏸ PAUSAR");
        estilizarBoton(btnPausar, COLOR_ADVERTENCIA);
        btnPausar.setEnabled(false);
        panel.add(btnPausar);
        
        btnReanudar = new JButton("▶ REANUDAR");
        estilizarBoton(btnReanudar, COLOR_EXITO);
        btnReanudar.setEnabled(false);
        btnReanudar.setVisible(false);
        panel.add(btnReanudar);
        
        btnReiniciar = new JButton("⟳ REINICIAR");
        estilizarBoton(btnReiniciar, new Color(255, 100, 100));
        btnReiniciar.setEnabled(false);
        btnReiniciar.setVisible(false);
        panel.add(btnReiniciar);
        
        btnAgregarProceso = new JButton("+ PROCESO");
        estilizarBoton(btnAgregarProceso, COLOR_CIAN);
        panel.add(btnAgregarProceso);
        
        btnGenerar20 = new JButton("++ 20 PROC");
        estilizarBoton(btnGenerar20, COLOR_CIAN);
        panel.add(btnGenerar20);
        
        btnEmergency = new JButton("⚡ EMERGENCIA");
        estilizarBoton(btnEmergency, COLOR_PELIGRO);
        panel.add(btnEmergency);
        
        btnCargarArchivo = new JButton("📁 CARGAR");
        estilizarBoton(btnCargarArchivo, COLOR_BLANCO);
        panel.add(btnCargarArchivo);
        
        return panel;
    }
    
    // Métodos auxiliares de UI
    private JPanel crearPanelContenedor(String titulo) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(COLOR_PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(
            new LineBorder(COLOR_CIAN, 1), titulo);
        border.setTitleColor(COLOR_BLANCO);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        p.setBorder(border);
        return p;
    }
    
    private JScrollPane crearScrollConTitulo(JList<String> lista, String titulo) {
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBackground(COLOR_PANEL);
        scroll.getViewport().setBackground(COLOR_PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(
            new LineBorder(COLOR_CIAN, 1), titulo);
        border.setTitleColor(COLOR_BLANCO);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
        scroll.setBorder(border);
        return scroll;
    }
    
    private JList<String> configurarJList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setBackground(COLOR_PANEL);
        list.setForeground(COLOR_CIAN);
        list.setFont(new Font("Monospaced", Font.PLAIN, 11));
        list.setSelectionBackground(COLOR_CIAN);
        list.setSelectionForeground(Color.BLACK);
        return list;
    }
    
    private JLabel crearLabelMetrica(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(COLOR_BLANCO);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return lbl;
    }
    
    private void estilizarBoton(JButton btn, Color colorBorde) {
        btn.setBackground(new Color(20, 20, 30));
        btn.setForeground(COLOR_BLANCO);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorder(new LineBorder(colorBorde, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(115, 35));
    }
    
    // Método principal de actualización
    public void updateView(int ciclo, PCB procesoActual, String[] listos, String[] bloqueados,
                          String[] suspendidosListos, String[] suspendidosBloqueados,
                          String[] terminados, int ramUsada, int ramMax, boolean modoKernel,
                          String algoritmo, Metricas metricas, String[] log) {
        
        SwingUtilities.invokeLater(() -> {
            // Actualizar reloj
            lblClock.setText("CICLO: " + ciclo);
            
            // Actualizar modo
            if (modoKernel) {
                lblModo.setText("KERNEL (SO)");
                lblModo.setForeground(COLOR_ADVERTENCIA);
            } else {
                lblModo.setText("USUARIO");
                lblModo.setForeground(COLOR_EXITO);
            }
            
            // Actualizar algoritmo
            lblAlgoritmo.setText(algoritmo);
            
            // Actualizar RAM
            lblRamInfo.setText(ramUsada + "/" + ramMax + " procesos");
            barRAM.setMaximum(ramMax);
            barRAM.setValue(ramUsada);
            
            // Actualizar CPU
            if (procesoActual != null) {
                lblProcesoActual.setText(procesoActual.getNombre() + " [ID:" + procesoActual.getId() + "]");
                lblPcMar.setText("PC: " + procesoActual.getPc() + " | MAR: " + procesoActual.getMar());
                int progreso = (int)((double)procesoActual.getInstruccionesEjecutadas() / 
                               procesoActual.getInstruccionesTotales() * 100);
                barCPU.setValue(progreso);
                barCPU.setString(progreso + "%");
                lblProgreso.setText("Progreso: " + procesoActual.getInstruccionesEjecutadas() + 
                                   "/" + procesoActual.getInstruccionesTotales());
                
                int tiempoRestante = procesoActual.getTiempoRestanteDeadline(ciclo);
                lblDeadline.setText("DEADLINE: Ciclo " + procesoActual.getDeadline() + 
                                   " (Restan " + tiempoRestante + ")");
                if (tiempoRestante < 10) {
                    lblDeadline.setForeground(COLOR_PELIGRO);
                } else if (tiempoRestante < 20) {
                    lblDeadline.setForeground(COLOR_ADVERTENCIA);
                } else {
                    lblDeadline.setForeground(COLOR_EXITO);
                }
            } else {
                lblProcesoActual.setText("IDLE - Esperando proceso");
                lblPcMar.setText("PC: - | MAR: -");
                barCPU.setValue(0);
                barCPU.setString("0%");
                lblProgreso.setText("Progreso: -/-");
                lblDeadline.setText("DEADLINE: -");
                lblDeadline.setForeground(COLOR_BLANCO);
            }
            
            // Actualizar colas
            actualizarLista(modReady, listos, "▶ ");
            actualizarLista(modBlocked, bloqueados, "⏳ ");
            actualizarLista(modSuspendedReady, suspendidosListos, "💾 ");
            actualizarLista(modSuspendedBlocked, suspendidosBloqueados, "💾 ");
            actualizarLista(modTerminated, terminados, "✓ ");
            
            // Actualizar métricas
            if (metricas != null) {
                lblUsoCPU.setText(String.format("Uso CPU: %.1f%%", metricas.getUsoCPU()));
                lblThroughput.setText(String.format("Throughput: %.2f%%", metricas.getThroughput()));
                lblTasaExito.setText(String.format("Tasa Éxito Misión: %.1f%%", metricas.getTasaExitoMision()));
                lblTiempoEspera.setText(String.format("Tiempo Espera Prom: %.1f", metricas.getTiempoEsperaPromedio()));
                lblProcesosCompletados.setText("Completados: " + metricas.getProcesosCompletados());
                lblDeadlinesFallidos.setText("Deadlines Fallidos: " + metricas.getProcesosFallidosDeadline());
                
                // Color de tasa de éxito
                if (metricas.getTasaExitoMision() >= 90) {
                    lblTasaExito.setForeground(COLOR_EXITO);
                } else if (metricas.getTasaExitoMision() >= 70) {
                    lblTasaExito.setForeground(COLOR_ADVERTENCIA);
                } else {
                    lblTasaExito.setForeground(COLOR_PELIGRO);
                }
            }
            
            // Actualizar log
            if (log != null && log.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = log.length - 1; i >= 0 && i >= log.length - 20; i--) {
                    sb.append(log[i]).append("\n");
                }
                txtLog.setText(sb.toString());
            }
        });
    }
    
    private void actualizarLista(DefaultListModel<String> model, String[] items, String prefijo) {
        model.clear();
        if (items != null) {
            for (String item : items) {
                model.addElement(prefijo + item);
            }
        }
    }
    
    public void setSistemaOperativo(SistemaOperativo so) {
        this.sistemaOperativo = so;
    }
    
    public void setAcciones(ActionListener start, ActionListener pausar, ActionListener emergency,
                           ActionListener agregarProceso, ActionListener generar20, ActionListener cargar,
                           ActionListener reanudar, ActionListener reiniciar) {
        btnStart.addActionListener(start);
        btnPausar.addActionListener(pausar);
        btnEmergency.addActionListener(emergency);
        btnAgregarProceso.addActionListener(agregarProceso);
        btnGenerar20.addActionListener(generar20);
        btnCargarArchivo.addActionListener(cargar);
        btnReanudar.addActionListener(reanudar);
        btnReiniciar.addActionListener(reiniciar);
    }
    
    /**
     * Actualiza el estado de los botones según si la simulación está corriendo o pausada.
     * @param corriendo true si la simulación está en ejecución, false si está pausada
     */
    public void actualizarBotonesSimulacion(boolean corriendo) {
        SwingUtilities.invokeLater(() -> {
            if (corriendo) {
                // Simulación corriendo
                btnStart.setEnabled(false);
                btnStart.setVisible(false);
                btnPausar.setEnabled(true);
                btnPausar.setVisible(true);
                btnReanudar.setEnabled(false);
                btnReanudar.setVisible(false);
                btnReiniciar.setEnabled(false);
                btnReiniciar.setVisible(false);
            } else {
                // Simulación pausada - mostrar opciones de reanudar o reiniciar
                btnStart.setEnabled(false);
                btnStart.setVisible(false);
                btnPausar.setEnabled(false);
                btnPausar.setVisible(false);
                btnReanudar.setEnabled(true);
                btnReanudar.setVisible(true);
                btnReiniciar.setEnabled(true);
                btnReiniciar.setVisible(true);
            }
        });
    }

    /**
     * Muestra un diálogo informando que la simulación ha terminado.
     * El diálogo se cierra automáticamente después de 5 segundos.
     */
    public void mostrarFinSimulacion(Metricas metricas, int ciclosTotales, int procesosCompletados) {
        SwingUtilities.invokeLater(() -> {
            // Calcular deadlines cumplidos
            int deadlinesFallidos = metricas.getProcesosFallidosDeadline();
            int deadlinesCumplidos = procesosCompletados - deadlinesFallidos;
            
            // Crear diálogo personalizado
            JDialog dialogo = new JDialog(this, "Simulación Completada", false);
            dialogo.setLayout(new BorderLayout());
            dialogo.getContentPane().setBackground(COLOR_PANEL);
            
            // Panel principal
            JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
            panelContenido.setBackground(COLOR_PANEL);
            panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            
            // Título
            JLabel lblTitulo = new JLabel("✓ SIMULACIÓN COMPLETADA", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
            lblTitulo.setForeground(COLOR_EXITO);
            panelContenido.add(lblTitulo, BorderLayout.NORTH);
            
            // Métricas
            JPanel panelMetricasDialog = new JPanel(new GridLayout(6, 1, 5, 5));
            panelMetricasDialog.setBackground(COLOR_PANEL);
            panelMetricasDialog.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            
            panelMetricasDialog.add(crearLabelDialogo("📊 Ciclos totales: " + ciclosTotales));
            panelMetricasDialog.add(crearLabelDialogo("✓ Procesos completados: " + procesosCompletados));
            panelMetricasDialog.add(crearLabelDialogo("✓ Deadlines cumplidos: " + deadlinesCumplidos));
            panelMetricasDialog.add(crearLabelDialogo("✗ Deadlines fallidos: " + deadlinesFallidos));
            panelMetricasDialog.add(crearLabelDialogo("⚡ Uso promedio CPU: " + String.format("%.1f%%", metricas.getUsoCPU())));
            panelMetricasDialog.add(crearLabelDialogo("⏱ Tiempo espera promedio: " + String.format("%.2f ciclos", metricas.getTiempoEsperaPromedio())));
            
            panelContenido.add(panelMetricasDialog, BorderLayout.CENTER);
            
            // Mensaje de cierre automático
            JLabel lblCierre = new JLabel("Este mensaje se cerrará automáticamente en 5 segundos...", SwingConstants.CENTER);
            lblCierre.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblCierre.setForeground(COLOR_CIAN);
            panelContenido.add(lblCierre, BorderLayout.SOUTH);
            
            dialogo.add(panelContenido);
            dialogo.pack();
            dialogo.setLocationRelativeTo(this);
            dialogo.setResizable(false);
            
            // Timer para cerrar automáticamente después de 5 segundos
            Timer timerCierre = new Timer(5000, e -> {
                dialogo.dispose();
            });
            timerCierre.setRepeats(false);
            timerCierre.start();
            
            // Timer para actualizar el contador de segundos
            final int[] segundosRestantes = {5};
            Timer timerContador = new Timer(1000, e -> {
                segundosRestantes[0]--;
                if (segundosRestantes[0] > 0) {
                    lblCierre.setText("Este mensaje se cerrará automáticamente en " + segundosRestantes[0] + " segundos...");
                } else {
                    ((Timer) e.getSource()).stop();
                }
            });
            timerContador.start();
            
            // Mostrar diálogo
            dialogo.setVisible(true);
        });
    }
    
    /**
     * Crea un JLabel estilizado para el diálogo de fin de simulación.
     */
    private JLabel crearLabelDialogo(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(COLOR_BLANCO);
        return lbl;
    }
}