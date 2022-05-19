/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package paintbasico2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.dgs.graficos.Elipse2D;
import sm.dgs.graficos.Linea2D;
import sm.dgs.graficos.Rectangulo2D;
import sm.dgs.graficos.TrazoLibre2D;
import sm.dgs.iu.Lienzo2D.Figura;
import sm.dgs.iu.LienzoAdapter;
import sm.dgs.iu.LienzoEvent;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;

/**
 *
 * @author danielgs
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    private BufferedImage imgFuente;
    private JFileChooser chooser;

    public LookupTable cuadratica(double m) {

        double Max;

        if (m >= 128.0) {
            Max = (double) ((1.0 / 100.0) * Math.pow((float) 0.0 - m, 2));
        } else {
            Max = (double) (1.0 / 100.0) * Math.pow((float) 255.0 - m, 2);
        }
        double K = 255.0 / Max;

        byte lt[] = new byte[256];

        for (int l = 0; l < 256; l++) {
            lt[l] = (byte) (K * ((1.0 / 100.0) * Math.pow((float) l - m, 2)));
        }

        ByteLookupTable slt = new ByteLookupTable(0, lt);
        return slt;

    }

    public LookupTable trapezoide(double a, double b) {

//        double Max = (m >= 128) ? 0 : 255;
        double K = 1.0 / 255.0;

        byte lt[] = new byte[256];
        for (int l = 1; l < 256; l++) {

            if (l >= 0) {
                lt[l] = 0;
                lt[l] = (byte) (K * lt[l]);
            } else if (0 < l && l < a) {
                lt[l] = (byte) (l / a);
                lt[l] = (byte) (K * lt[l]);
            } else if (a <= l && l <= b) {
                lt[l] = (byte) 1;
                lt[l] = (byte) (K * lt[l]);
            } else if (b < l && l < 255) {
                lt[l] = (byte) (255.0 - (double) l / 255.0 - b);
                lt[l] = (byte) (K * lt[l]);
            } else if (l >= 255) {
                lt[l] = 0;
                lt[l] = (byte) (K * lt[l]);
            }
            //lt[l] = (byte) (K * (1.0 / 100.0f * Math.pow(l - (double) m, 2)));
        }

        ByteLookupTable slt = new ByteLookupTable(0, lt);
        return slt;

    }

    protected JFileChooser getFileChooser() {
        if (chooser == null) {
            chooser = new JFileChooser();
            FileFilter jpg = new FileNameExtensionFilter("JPG (.jpg)", "jpg");
            FileFilter png = new FileNameExtensionFilter("PNG (.png)", "png");
            FileFilter jpeg = new FileNameExtensionFilter("JPEG (.jpeg)", "jpeg");
            FileFilter gif = new FileNameExtensionFilter("GIF (.gif)", "gif");
            chooser.addChoosableFileFilter(jpg);
            chooser.addChoosableFileFilter(png);
            chooser.addChoosableFileFilter(jpeg);
            chooser.addChoosableFileFilter(gif);
            chooser.setFileFilter(jpg);
//            chooser.setFileFilter(png);
            chooser.setAcceptAllFileFilterUsed(false);
        }
        return chooser;
    }

    // 1) Definir la clase manejadora y sobrecargar los metodos que sean necesarios
    private class ManejadorVentanaInterna extends InternalFrameAdapter {

        @Override
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {

            VentanaInterna vi = (VentanaInterna) evt.getInternalFrame();
            Color lcolor = vi.getLienzo().getColor();
            Figura sfigura = vi.getLienzo().getFigura();

            rellenoToggleButton.setSelected(vi.getLienzo().getRelleno());
            transparenciaToggleButton.setSelected(vi.getLienzo().getTransparencia());
            alisarToggleButton.setSelected(vi.getLienzo().getAntialiasing());

            if (lcolor == Color.BLACK) {

                ComboBoxColors.setSelectedIndex(0);

            } else if (lcolor == Color.RED) {

                ComboBoxColors.setSelectedIndex(1);

            } else if (lcolor == Color.BLUE) {

                ComboBoxColors.setSelectedIndex(2);

            } else if (lcolor == Color.WHITE) {

                ComboBoxColors.setSelectedIndex(3);

            } else if (lcolor == Color.YELLOW) {

                ComboBoxColors.setSelectedIndex(4);

            } else if (lcolor == Color.GREEN) {

                ComboBoxColors.setSelectedIndex(5);

            }

            if (null != sfigura) {
                switch (sfigura) {
                    case TRAZO_LIBRE:
                        botonTrazoLibre.setSelected(true);
                        break;
                    case LINEA:
                        botonLinea.setSelected(true);
                        break;
                    case RECTANGULO:
                        botonRectan.setSelected(true);
                        break;
                    case ELIPSE:
                        botonElipse.setSelected(true);
                        break;
                    default:
                        break;
                }
            }

            ToggleButtonMover.setSelected(vi.getLienzo().getMover());

        }

    }

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
    }

    public class MiManejadorLienzo extends LienzoAdapter {

        public void shapeAdded(LienzoEvent evt) {
            labelEstado.setText("Figura " + " x " + "añadida");
        }

        public void propertyChange(LienzoEvent evt) {

            labelEstado.setText("Figura" + " x " + "seleccionada" + "Transparencia: " + evt.getfigura().getTransparencia() + " Alisar: " + evt.getfigura().getAntialiasing());

            Color lcolor = evt.getfigura().getColor();
            Shape sfigura = evt.getfigura().getTipoFigura();

            rellenoToggleButton.setSelected(evt.getfigura().getRelleno());
            transparenciaToggleButton.setSelected(evt.getfigura().getTransparencia());
            alisarToggleButton.setSelected(evt.getfigura().getAntialiasing());

            if (lcolor == Color.BLACK) {

                ComboBoxColors.setSelectedIndex(0);

            } else if (lcolor == Color.RED) {

                ComboBoxColors.setSelectedIndex(1);

            } else if (lcolor == Color.BLUE) {

                ComboBoxColors.setSelectedIndex(2);

            } else if (lcolor == Color.WHITE) {

                ComboBoxColors.setSelectedIndex(3);

            } else if (lcolor == Color.YELLOW) {

                ComboBoxColors.setSelectedIndex(4);

            } else if (lcolor == Color.GREEN) {

                ComboBoxColors.setSelectedIndex(5);

            }

            if (sfigura instanceof Linea2D) {
                botonLinea.setSelected(true);
            } else if (sfigura instanceof Rectangulo2D) {
                botonRectan.setSelected(true);
            } else if (sfigura instanceof Elipse2D) {
                botonElipse.setSelected(true);
            } else if (sfigura instanceof TrazoLibre2D) {
                botonTrazoLibre.setSelected(true);
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        figuras = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        botonNuevo = new javax.swing.JButton();
        botonAbrir = new javax.swing.JButton();
        botonGuardar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        botonTrazoLibre = new javax.swing.JToggleButton();
        botonLinea = new javax.swing.JToggleButton();
        botonRectan = new javax.swing.JToggleButton();
        botonElipse = new javax.swing.JToggleButton();
        botonCurva = new javax.swing.JToggleButton();
        ToggleButtonMover = new javax.swing.JToggleButton();
        botonSeleccionador = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jPanel5 = new javax.swing.JPanel();
        Color colores[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN};
        ComboBoxColors = new javax.swing.JComboBox<>(colores);
        jSeparator4 = new javax.swing.JToolBar.Separator();
        spinnerGrosor = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        rellenoToggleButton = new javax.swing.JToggleButton();
        transparenciaToggleButton = new javax.swing.JToggleButton();
        alisarToggleButton = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        escritorio = new javax.swing.JDesktopPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        sliderBrillo = new javax.swing.JSlider();
        sliderContraste = new javax.swing.JSlider();
        jPanel9 = new javax.swing.JPanel();
        sliderFiltroMedia = new javax.swing.JSlider();
        jPanel7 = new javax.swing.JPanel();
        ComboBoxFiltros = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        botonContraste = new javax.swing.JButton();
        bIluminar = new javax.swing.JButton();
        bOscurecer = new javax.swing.JButton();
        bCuadratica = new javax.swing.JButton();
        bTrapezoide = new javax.swing.JToggleButton();
        jPanel10 = new javax.swing.JPanel();
        slider360 = new javax.swing.JSlider();
        b90 = new javax.swing.JButton();
        b180 = new javax.swing.JButton();
        b270 = new javax.swing.JButton();
        bAumentar = new javax.swing.JButton();
        bDisminuir = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        labelEstado = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        menuNuevo = new javax.swing.JMenuItem();
        menuAbrir = new javax.swing.JMenuItem();
        menuGuardar = new javax.swing.JMenuItem();
        menuEdicion = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        menuRescaleOp = new javax.swing.JMenuItem();
        menuConvolveOp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jToolBar1.setRollover(true);

        botonNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        botonNuevo.setFocusable(false);
        botonNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonNuevo.setPreferredSize(new java.awt.Dimension(35, 35));
        botonNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonNuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(botonNuevo);

        botonAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        botonAbrir.setFocusable(false);
        botonAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAbrir.setPreferredSize(new java.awt.Dimension(35, 35));
        botonAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirActionPerformed(evt);
            }
        });
        jToolBar1.add(botonAbrir);

        botonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/guardar.png"))); // NOI18N
        botonGuardar.setFocusable(false);
        botonGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonGuardar.setPreferredSize(new java.awt.Dimension(35, 35));
        botonGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(botonGuardar);
        jToolBar1.add(jSeparator1);

        figuras.add(botonTrazoLibre);
        botonTrazoLibre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/trazo.png"))); // NOI18N
        botonTrazoLibre.setFocusable(false);
        botonTrazoLibre.setPreferredSize(new java.awt.Dimension(35, 35));
        botonTrazoLibre.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonTrazoLibre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTrazoLibreActionPerformed(evt);
            }
        });
        jToolBar1.add(botonTrazoLibre);

        figuras.add(botonLinea);
        botonLinea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/linea.png"))); // NOI18N
        botonLinea.setFocusable(false);
        botonLinea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonLinea.setPreferredSize(new java.awt.Dimension(35, 35));
        botonLinea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonLinea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLineaActionPerformed(evt);
            }
        });
        jToolBar1.add(botonLinea);

        figuras.add(botonRectan);
        botonRectan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rectangulo.png"))); // NOI18N
        botonRectan.setFocusable(false);
        botonRectan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonRectan.setPreferredSize(new java.awt.Dimension(35, 35));
        botonRectan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonRectan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRectanActionPerformed(evt);
            }
        });
        jToolBar1.add(botonRectan);

        figuras.add(botonElipse);
        botonElipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/elipse.png"))); // NOI18N
        botonElipse.setFocusable(false);
        botonElipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonElipse.setPreferredSize(new java.awt.Dimension(35, 35));
        botonElipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonElipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonElipseActionPerformed(evt);
            }
        });
        jToolBar1.add(botonElipse);

        figuras.add(botonCurva);
        botonCurva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/curva.png"))); // NOI18N
        botonCurva.setFocusable(false);
        botonCurva.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonCurva.setPreferredSize(new java.awt.Dimension(35, 35));
        botonCurva.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonCurva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCurvaActionPerformed(evt);
            }
        });
        jToolBar1.add(botonCurva);

        ToggleButtonMover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/seleccion.png"))); // NOI18N
        ToggleButtonMover.setFocusable(false);
        ToggleButtonMover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ToggleButtonMover.setPreferredSize(new java.awt.Dimension(35, 35));
        ToggleButtonMover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ToggleButtonMover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButtonMoverActionPerformed(evt);
            }
        });
        jToolBar1.add(ToggleButtonMover);

        botonSeleccionador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/9111124_select_icon (1).png"))); // NOI18N
        botonSeleccionador.setFocusable(false);
        botonSeleccionador.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonSeleccionador.setPreferredSize(new java.awt.Dimension(35, 35));
        botonSeleccionador.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonSeleccionador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSeleccionadorActionPerformed(evt);
            }
        });
        jToolBar1.add(botonSeleccionador);
        jToolBar1.add(jSeparator3);

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        ComboBoxColors.setPreferredSize(new java.awt.Dimension(35, 35));
        ComboBoxColors.setRenderer(new ColorCellRender());
        ComboBoxColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxColorsActionPerformed(evt);
            }
        });
        jPanel5.add(ComboBoxColors);

        jToolBar1.add(jPanel5);
        jToolBar1.add(jSeparator4);

        spinnerGrosor.setValue(5);
        spinnerGrosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerGrosorStateChanged(evt);
            }
        });
        jToolBar1.add(spinnerGrosor);
        jToolBar1.add(jSeparator2);

        rellenoToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rellenar.png"))); // NOI18N
        rellenoToggleButton.setFocusable(false);
        rellenoToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rellenoToggleButton.setPreferredSize(new java.awt.Dimension(35, 35));
        rellenoToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rellenoToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rellenoToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(rellenoToggleButton);

        transparenciaToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/transparencia.png"))); // NOI18N
        transparenciaToggleButton.setFocusable(false);
        transparenciaToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transparenciaToggleButton.setPreferredSize(new java.awt.Dimension(35, 35));
        transparenciaToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transparenciaToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transparenciaToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(transparenciaToggleButton);

        alisarToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/alisar.png"))); // NOI18N
        alisarToggleButton.setFocusable(false);
        alisarToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        alisarToggleButton.setPreferredSize(new java.awt.Dimension(35, 35));
        alisarToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        alisarToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alisarToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(alisarToggleButton);

        jPanel1.add(jToolBar1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1838, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
        );

        jPanel2.add(escritorio, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Brillo y contraste"));
        jPanel8.setLayout(new java.awt.GridLayout(1, 2));

        sliderBrillo.setMaximum(255);
        sliderBrillo.setMinimum(-255);
        sliderBrillo.setValue(0);
        sliderBrillo.setPreferredSize(new java.awt.Dimension(200, 35));
        sliderBrillo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBrilloStateChanged(evt);
            }
        });
        sliderBrillo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusLost(evt);
            }
        });
        jPanel8.add(sliderBrillo);

        sliderContraste.setMaximum(20);
        sliderContraste.setValue(10);
        sliderContraste.setPreferredSize(new java.awt.Dimension(200, 35));
        sliderContraste.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderContrasteStateChanged(evt);
            }
        });
        sliderContraste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderContrasteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderContrasteFocusLost(evt);
            }
        });
        jPanel8.add(sliderContraste);

        jPanel4.add(jPanel8);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Filtros"));
        jPanel9.setLayout(new java.awt.BorderLayout());

        sliderFiltroMedia.setMaximum(31);
        sliderFiltroMedia.setMinimum(1);
        sliderFiltroMedia.setValue(0);
        sliderFiltroMedia.setPreferredSize(new java.awt.Dimension(200, 35));
        sliderFiltroMedia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderFiltroMediaStateChanged(evt);
            }
        });
        sliderFiltroMedia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderFiltroMediaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderFiltroMediaFocusLost(evt);
            }
        });
        jPanel9.add(sliderFiltroMedia, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel9);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Filtros"));
        jPanel7.setLayout(new java.awt.BorderLayout());

        ComboBoxFiltros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Emborronamiento media", "Emborronamiento binomial", "Enfoque", "Relieve", "Detector de fronteras laplaciano", "Emborronamiento media5x5", "Emborronamiento media7x7" }));
        ComboBoxFiltros.setPreferredSize(new java.awt.Dimension(247, 35));
        ComboBoxFiltros.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ComboBoxFiltrosFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ComboBoxFiltrosFocusLost(evt);
            }
        });
        ComboBoxFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxFiltrosActionPerformed(evt);
            }
        });
        jPanel7.add(ComboBoxFiltros, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel7);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Transformaciones"));

        botonContraste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/contraste.png"))); // NOI18N
        botonContraste.setPreferredSize(new java.awt.Dimension(35, 35));
        botonContraste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonContrasteActionPerformed(evt);
            }
        });
        jPanel11.add(botonContraste);

        bIluminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iluminar.png"))); // NOI18N
        bIluminar.setPreferredSize(new java.awt.Dimension(35, 35));
        bIluminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIluminarActionPerformed(evt);
            }
        });
        jPanel11.add(bIluminar);

        bOscurecer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/oscurecer.png"))); // NOI18N
        bOscurecer.setPreferredSize(new java.awt.Dimension(35, 35));
        bOscurecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOscurecerActionPerformed(evt);
            }
        });
        jPanel11.add(bOscurecer);

        bCuadratica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cuadratica.png"))); // NOI18N
        bCuadratica.setPreferredSize(new java.awt.Dimension(35, 35));
        bCuadratica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCuadraticaActionPerformed(evt);
            }
        });
        jPanel11.add(bCuadratica);

        bTrapezoide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/trapezoid.png"))); // NOI18N
        bTrapezoide.setPreferredSize(new java.awt.Dimension(35, 35));
        bTrapezoide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTrapezoideActionPerformed(evt);
            }
        });
        jPanel11.add(bTrapezoide);

        jPanel4.add(jPanel11);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Rotación y escalado"));

        slider360.setMajorTickSpacing(72);
        slider360.setMaximum(360);
        slider360.setPaintTicks(true);
        slider360.setSnapToTicks(true);
        slider360.setValue(0);
        slider360.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider360StateChanged(evt);
            }
        });
        slider360.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider360FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider360FocusLost(evt);
            }
        });
        jPanel10.add(slider360);

        b90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion90.png"))); // NOI18N
        b90.setPreferredSize(new java.awt.Dimension(35, 35));
        b90.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b90ActionPerformed(evt);
            }
        });
        jPanel10.add(b90);

        b180.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion180.png"))); // NOI18N
        b180.setPreferredSize(new java.awt.Dimension(35, 35));
        b180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b180ActionPerformed(evt);
            }
        });
        jPanel10.add(b180);

        b270.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion270.png"))); // NOI18N
        b270.setPreferredSize(new java.awt.Dimension(35, 35));
        b270.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b270ActionPerformed(evt);
            }
        });
        jPanel10.add(b270);

        bAumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/aumentar.png"))); // NOI18N
        bAumentar.setPreferredSize(new java.awt.Dimension(35, 35));
        bAumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAumentarActionPerformed(evt);
            }
        });
        jPanel10.add(bAumentar);

        bDisminuir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/disminuir.png"))); // NOI18N
        bDisminuir.setPreferredSize(new java.awt.Dimension(35, 35));
        bDisminuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDisminuirActionPerformed(evt);
            }
        });
        jPanel10.add(bDisminuir);

        jPanel4.add(jPanel10);

        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        labelEstado.setText("jLabel1");
        jPanel6.add(labelEstado);

        jPanel3.add(jPanel6, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        menuArchivo.setText("Archivo");
        menuArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuArchivoActionPerformed(evt);
            }
        });

        menuNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        menuNuevo.setText("Nuevo");
        menuNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNuevoActionPerformed(evt);
            }
        });
        menuArchivo.add(menuNuevo);

        menuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        menuAbrir.setText("Abrir");
        menuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(menuAbrir);

        menuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/guardar.png"))); // NOI18N
        menuGuardar.setText("Guardar");
        menuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(menuGuardar);

        jMenuBar1.add(menuArchivo);

        menuEdicion.setText("Edición");
        jMenuBar1.add(menuEdicion);

        jMenu1.setText("Imagen");

        menuRescaleOp.setText("Reescalar imagen");
        menuRescaleOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRescaleOpActionPerformed(evt);
            }
        });
        jMenu1.add(menuRescaleOp);

        menuConvolveOp.setText("ConvolveOp");
        menuConvolveOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConvolveOpActionPerformed(evt);
            }
        });
        jMenu1.add(menuConvolveOp);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNuevoActionPerformed
        VentanaInterna vi = new VentanaInterna();
        escritorio.add(vi);
        vi.setVisible(true);

        //IMAGEN
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        vi.getLienzo().setImage(img);

        vi.addInternalFrameListener(new ManejadorVentanaInterna());
        vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
    }//GEN-LAST:event_menuNuevoActionPerformed

    private void botonTrazoLibreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTrazoLibreActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.TRAZO_LIBRE);
        }
    }//GEN-LAST:event_botonTrazoLibreActionPerformed

    private void botonLineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLineaActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.LINEA);
        }
    }//GEN-LAST:event_botonLineaActionPerformed

    private void botonRectanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRectanActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.RECTANGULO);
        }
    }//GEN-LAST:event_botonRectanActionPerformed

    private void botonElipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonElipseActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.ELIPSE);
        }
    }//GEN-LAST:event_botonElipseActionPerformed

    private void botonCurvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCurvaActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.CURVA);
        }
    }//GEN-LAST:event_botonCurvaActionPerformed

    private void spinnerGrosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerGrosorStateChanged
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {

            Integer a = (int) spinnerGrosor.getValue();

            float b = a.floatValue();

            vi.getLienzo().setStroke(new BasicStroke(b));

        }

        this.repaint();
    }//GEN-LAST:event_spinnerGrosorStateChanged

    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        JFileChooser chooser = getFileChooser();
        int resp = chooser.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                VentanaInterna vi = new VentanaInterna();
                vi.getLienzo().setImage(img);
                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);

                vi.addInternalFrameListener(new ManejadorVentanaInterna());
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_menuAbrirActionPerformed

    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage(true);
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(img, "jpg", f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_menuGuardarActionPerformed

    private void menuRescaleOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRescaleOpActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.0F, 100.0F, null);
                    rop.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_menuRescaleOpActionPerformed

    private void menuConvolveOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConvolveOpActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    float filtro[] = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);

                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_menuConvolveOpActionPerformed

    private void sliderBrilloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderBrilloFocusGained

    private void sliderBrilloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusLost
        imgFuente = null;
    }//GEN-LAST:event_sliderBrilloFocusLost

    private void sliderBrilloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBrilloStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.0F, sliderBrillo.getValue(), null);
                    rop.filter(imgFuente, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderBrilloStateChanged

    private void sliderContrasteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderContrasteFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderContrasteFocusGained

    private void sliderContrasteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderContrasteFocusLost
        imgFuente = null;
    }//GEN-LAST:event_sliderContrasteFocusLost

    private void sliderContrasteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderContrasteStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    float num = sliderContraste.getValue() * 0.1f;
                    float filtro[] = {num, num, num,
                        num, num, num,
                        num, num, num};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);

                    BufferedImage imgdest = cop.filter(imgFuente, img);

                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderContrasteStateChanged

    private void ComboBoxFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxFiltrosActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());

        switch (ComboBoxFiltros.getSelectedIndex()) {
            case 0:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_MEDIA_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 1:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 2:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 3:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 4:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_LAPLACIANA_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;

            case 5:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            float num = 1 / 25.0f;
                            float filtro[] = {num, num, num, num, num,
                                num, num, num, num, num,
                                num, num, num, num, num,
                                num, num, num, num, num,
                                num, num, num, num, num,};
                            Kernel k = new Kernel(5, 5, filtro);
                            ConvolveOp cop2 = new ConvolveOp(k);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 6:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            float num = 1 / 49.0f;
                            float filtro[] = {num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,};
                            Kernel k = new Kernel(5, 5, filtro);
                            ConvolveOp cop2 = new ConvolveOp(k);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
        }
    }//GEN-LAST:event_ComboBoxFiltrosActionPerformed

    private void ComboBoxFiltrosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ComboBoxFiltrosFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_ComboBoxFiltrosFocusGained

    private void ComboBoxFiltrosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ComboBoxFiltrosFocusLost
        imgFuente = null;
    }//GEN-LAST:event_ComboBoxFiltrosFocusLost

    private void ToggleButtonMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButtonMoverActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (ToggleButtonMover.isSelected()) {
            vi.getLienzo().setMover(true);
        } else {
            vi.getLienzo().setMover(false);
        }
    }//GEN-LAST:event_ToggleButtonMoverActionPerformed

    private void botonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirActionPerformed
        JFileChooser dlg = getFileChooser();
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                VentanaInterna vi = new VentanaInterna();
                vi.getLienzo().setImage(img);
                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_botonAbrirActionPerformed

    private void botonNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonNuevoActionPerformed
        VentanaInterna vi = new VentanaInterna();
        escritorio.add(vi);
        vi.setVisible(true);

        //IMAGEN
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        vi.getLienzo().setImage(img);

        // 2) Crear el objeto manejador (hacer el "new" de la clase anterior)
        vi.addInternalFrameListener(new ManejadorVentanaInterna());
        vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
    }//GEN-LAST:event_botonNuevoActionPerformed

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage(true);
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(img, "jpg", f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_botonGuardarActionPerformed

    private void ComboBoxColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxColorsActionPerformed

        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        switch (ComboBoxColors.getSelectedIndex()) {
            case 0:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.BLACK);
                } else {
                    vi.getLienzo().setColor(Color.BLACK);
                }

                this.repaint();

                break;

            case 1:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.RED);
                } else {
                    vi.getLienzo().setColor(Color.RED);
                }

                this.repaint();

                break;

            case 2:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.BLUE);
                } else {
                    vi.getLienzo().setColor(Color.BLUE);
                }

                this.repaint();

                break;

            case 3:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.WHITE);
                } else {
                    vi.getLienzo().setColor(Color.WHITE);
                }

                this.repaint();

                break;
            case 4:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.YELLOW);
                } else {
                    vi.getLienzo().setColor(Color.YELLOW);
                }

                this.repaint();

                break;

            case 5:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.GREEN);
                } else {
                    vi.getLienzo().setColor(Color.GREEN);
                }

                this.repaint();

                break;
        }
    }//GEN-LAST:event_ComboBoxColorsActionPerformed

    private void transparenciaToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparenciaToggleButtonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
            if (transparenciaToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setTransparencia(true);
            } else {
                vi.getLienzo().getFiguraSeleccionada().setTransparencia(false);
            }
        } else {
            if (transparenciaToggleButton.isSelected()) {
                vi.getLienzo().setTransparencia(true);
            } else {
                vi.getLienzo().setTransparencia(false);
            }
        }

        this.repaint();
    }//GEN-LAST:event_transparenciaToggleButtonActionPerformed

    private void alisarToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alisarToggleButtonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
            if (alisarToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setAntialiasing(true);
            } else {
                vi.getLienzo().getFiguraSeleccionada().setAntialiasing(false);
            }
        } else {
            if (alisarToggleButton.isSelected()) {
                vi.getLienzo().setAntialiasing(true);
            } else {
                vi.getLienzo().setAntialiasing(false);
            }
        }

        this.repaint();
    }//GEN-LAST:event_alisarToggleButtonActionPerformed

    private void rellenoToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rellenoToggleButtonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
            
            System.out.print("Relleno Figura Seleccionada");
            
            if (rellenoToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setRelleno(true);
            } else {
                vi.getLienzo().getFiguraSeleccionada().setRelleno(false);
            }
        } else {
            
            System.out.print("Relleno Figura Lienzo");
            
            if (rellenoToggleButton.isSelected()) {
                vi.getLienzo().setRelleno(true);
            } else {
                vi.getLienzo().setRelleno(false);
            }
        }

        this.repaint();
    }//GEN-LAST:event_rellenoToggleButtonActionPerformed

    private void sliderFiltroMediaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderFiltroMediaStateChanged
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    float num = 1 / (float) (sliderFiltroMedia.getValue() * sliderFiltroMedia.getValue());

                    float filtro[] = new float[sliderFiltroMedia.getValue() * sliderFiltroMedia.getValue()];

                    for (int i = 0; i < sliderFiltroMedia.getValue() * sliderFiltroMedia.getValue(); i++) {
                        filtro[i] = num;
                    }

                    Kernel k = new Kernel(sliderFiltroMedia.getValue(), sliderFiltroMedia.getValue(), filtro);
                    ConvolveOp cop2 = new ConvolveOp(k);

                    BufferedImage imgdest = cop2.filter(imgFuente, img);

                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderFiltroMediaStateChanged

    private void sliderFiltroMediaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroMediaFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderFiltroMediaFocusGained

    private void sliderFiltroMediaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroMediaFocusLost
        imgFuente = null;
        sliderFiltroMedia.setValue(0);
    }//GEN-LAST:event_sliderFiltroMediaFocusLost

    private void botonContrasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonContrasteActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_SFUNCION);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }

    }//GEN-LAST:event_botonContrasteActionPerformed

    private void bIluminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bIluminarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_ROOT);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bIluminarActionPerformed

    private void bOscurecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOscurecerActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_POWER);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bOscurecerActionPerformed

    private void bCuadraticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCuadraticaActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = cuadratica(128.0);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bCuadraticaActionPerformed

    private void slider360FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider360FocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_slider360FocusGained

    private void slider360FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider360FocusLost
        imgFuente = null;
        slider360.setValue(0);
    }//GEN-LAST:event_slider360FocusLost

    private void slider360StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider360StateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    double r = Math.toRadians(slider360.getValue());
                    Point c = new Point(imgFuente.getWidth() / 2, imgFuente.getHeight() / 2);

                    AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

                    BufferedImage imgdest = atop.filter(imgFuente, null);

                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_slider360StateChanged

    private void b90ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b90ActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {

            double r = Math.toRadians(90);
            Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
            AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
            AffineTransformOp atop;
            atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }

    }//GEN-LAST:event_b90ActionPerformed

    private void b180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b180ActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {

            double r = Math.toRadians(180);
            Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
            AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
            AffineTransformOp atop;
            atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }//GEN-LAST:event_b180ActionPerformed

    private void b270ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b270ActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {

            double r = Math.toRadians(270);
            Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
            AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
            AffineTransformOp atop;
            atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }//GEN-LAST:event_b270ActionPerformed

    private void bAumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAumentarActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {
            AffineTransform at = AffineTransform.getScaleInstance(1.25, 1.25);
            AffineTransformOp atop = new AffineTransformOp(at,
                    AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (Exception e) {
            System.err.println("Error");
        }

    }//GEN-LAST:event_bAumentarActionPerformed

    private void bDisminuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDisminuirActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {
            AffineTransform at = AffineTransform.getScaleInstance(0.75, 0.75);
            AffineTransformOp atop = new AffineTransformOp(at,
                    AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (Exception e) {
            System.err.println("Error");
        }
    }//GEN-LAST:event_bDisminuirActionPerformed

    private void bTrapezoideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTrapezoideActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = trapezoide(56, 56);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bTrapezoideActionPerformed

    private void menuArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuArchivoActionPerformed
        JFileChooser dlg = getFileChooser();
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                VentanaInterna vi = new VentanaInterna();
                vi.getLienzo().setImage(img);
                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_menuArchivoActionPerformed

    private void botonSeleccionadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSeleccionadorActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (botonSeleccionador.isSelected()) {
            vi.getLienzo().setSeleccionar(true);
        } else {
            vi.getLienzo().setSeleccionar(false);
            vi.getLienzo().setStroke(new BasicStroke(5.0f));
        }
    }//GEN-LAST:event_botonSeleccionadorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Color> ComboBoxColors;
    private javax.swing.JComboBox<String> ComboBoxFiltros;
    private javax.swing.JToggleButton ToggleButtonMover;
    private javax.swing.JToggleButton alisarToggleButton;
    private javax.swing.JButton b180;
    private javax.swing.JButton b270;
    private javax.swing.JButton b90;
    private javax.swing.JButton bAumentar;
    private javax.swing.JButton bCuadratica;
    private javax.swing.JButton bDisminuir;
    private javax.swing.JButton bIluminar;
    private javax.swing.JButton bOscurecer;
    private javax.swing.JToggleButton bTrapezoide;
    private javax.swing.JButton botonAbrir;
    private javax.swing.JButton botonContraste;
    private javax.swing.JToggleButton botonCurva;
    private javax.swing.JToggleButton botonElipse;
    private javax.swing.JButton botonGuardar;
    private javax.swing.JToggleButton botonLinea;
    private javax.swing.JButton botonNuevo;
    private javax.swing.JToggleButton botonRectan;
    private javax.swing.JToggleButton botonSeleccionador;
    private javax.swing.JToggleButton botonTrazoLibre;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.ButtonGroup figuras;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelEstado;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenuItem menuConvolveOp;
    private javax.swing.JMenu menuEdicion;
    private javax.swing.JMenuItem menuGuardar;
    private javax.swing.JMenuItem menuNuevo;
    private javax.swing.JMenuItem menuRescaleOp;
    private javax.swing.JToggleButton rellenoToggleButton;
    private javax.swing.JSlider slider360;
    private javax.swing.JSlider sliderBrillo;
    private javax.swing.JSlider sliderContraste;
    private javax.swing.JSlider sliderFiltroMedia;
    private javax.swing.JSpinner spinnerGrosor;
    private javax.swing.JToggleButton transparenciaToggleButton;
    // End of variables declaration//GEN-END:variables
}