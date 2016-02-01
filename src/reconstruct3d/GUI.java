package reconstruct3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;


public class GUI {
    
    JFrame window;  //główne okno
    JFrame console; //okno konsoli
    JPanel control; //panel kontrolny
    JPanel lowerButtons;    //dolne klawisze(Interpoluj, widok 3D)
    JLabel nav;
    JButton inter;  //klawisz "Interpoluj"
    JButton dim;    //klawisz "widok 3D"
    JButton img;    //wyświetlanie struktury
    JProgressBar progress;  //progress bar
    
    JTextArea logs; //pole tekstowe z logami
    
    JMenu file; //menu "Plik"
    JMenu options; //menu "Opcje"
    JMenuBar menuBar;   //całe menu
    JMenuItem load;     //podmenu "Wczytaj"
    JMenuItem settings; //podmenu "Ustawienia"
    JSlider navigate;
    JFileChooser chooser;   //wybór struktur do rekonstrukcji
    
    BasicArrowButton left;  //klawisz lewo
    BasicArrowButton right; //klawisz prawo
    
    Dimension window_size = new Dimension(600, 600);    //rozmiar okna
    Dimension console_size = new Dimension(250, 600);
    Reconstruct3D reconstructor;
    
    int index = 0;
    int count = 0;
    boolean done = false;
    boolean mouse_progress = false;
    boolean isPrinting;
    
    int size_x = 0;
    int size_y = 0;
    
    int sizep_x = 0;
    int sizep_y = 0;
    double sizep_z = 0;
    int multiply = 2;
    
    int interp = 0;
    String path;    //ścieżka gdzie zapisać obrazy
    String textData_path;   //ścieżka dla danych tekstowych
    String reconstruct_path;    //ścieżka dla wyboru struktury
    
    GUI(Reconstruct3D reconstructor){   //konstruktor gui, tworzy okno interfejsu
        loadSettings();
        this.reconstructor = reconstructor;
        initButtons();
        
        window = new JFrame("Rekonstruktor");
        window.setSize(window_size);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());
        
        console = new JFrame("Konsola");
        console.setSize(console_size);
        //console.setLocationRelativeTo(window);
        console.setLocation(window.getLocation().x+window.getWidth(), window.getLocation().y);
        
        logs = new JTextArea();
        console.add(logs);
        logs.setEditable(false);
        
        window.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                console.setLocation(window.getLocation().x+window.getWidth(), window.getLocation().y);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                
            }
        });
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        lowerButtons.add(inter);
        lowerButtons.add(dim);
        lowerButtons.add(nav);
        
        control.add(lowerButtons, BorderLayout.CENTER);
        control.add(menuBar, BorderLayout.NORTH);
        control.add(progress, BorderLayout.SOUTH);
        
        window.add(control, BorderLayout.NORTH);
        window.add(left, BorderLayout.WEST);
        window.add(right, BorderLayout.EAST);
        window.add(img, BorderLayout.CENTER);
        window.add(navigate, BorderLayout.SOUTH);
        
        window.setVisible(true);
        console.setVisible(true);
    }
    
    void loadSettings(){
        FileInputStream fstream = null;
        
 
        try {
            fstream = new FileInputStream("settings.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        try {
            interp = Integer.parseInt(br.readLine().trim());
            size_x = Integer.parseInt(br.readLine().trim());
            size_y = Integer.parseInt(br.readLine().trim());
            sizep_x = Integer.parseInt(br.readLine().trim());
            sizep_y = Integer.parseInt(br.readLine().trim());
            sizep_z = Double.parseDouble(br.readLine().trim());
            isPrinting = Boolean.parseBoolean(br.readLine().trim());
            textData_path = br.readLine().trim();
            path = br.readLine().trim();
            
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    void initButtons(){ //inicjalizacja klawiszy
        control = new JPanel();
        control.setLayout(new BorderLayout());
        
        lowerButtons = new JPanel();
        
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        inter = new JButton("Interpoluj");
        dim = new JButton("Widok 3D");
        img = new JButton();
        img.setBackground(Color.yellow);
        
        menuBar = new JMenuBar();
        file = new JMenu("Plik");
        load = new JMenuItem("Wczytaj");
        
        options = new JMenu("Ustawienia");
        settings = new JMenuItem("Opcje");
        
        file.add(load);
        options.add(settings);
        
        menuBar.add(file);
        menuBar.add(options);
        
        left = new BasicArrowButton(BasicArrowButton.LEFT);
        right = new BasicArrowButton(BasicArrowButton.RIGHT);
        arrowButtons();
        interButton();
        dimButton();
        optionMenuBut();
        selectMenuBut();
        
        progress = new JProgressBar();
        nav = new JLabel("0/0");
        navigate = new JSlider();
        navigate.setValue(index);
        navigate.setMinimum(0);
        //navigate.setMaximum(new File("" + path + "/").listFiles().length);
        
        navigate.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                index = navigate.getValue();
                img.setIcon(new ImageIcon("" + path + "/" + index + ".png"));
                nav.setText(index + "/" + count);
            }
        });
    }
    
    void arrowButtons(){    //funkcjonalność klawiszy lewo prawo
        count = new File("Regen/VolumeSplices/").listFiles().length;
        left.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(index > 0)
                    index--;
                img.setIcon(new ImageIcon("" + path + "/" + index + ".png"));
                nav.setText(index + "/" + count);
                navigate.setValue(index);
            }
        });
        
        right.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(index < count)
                    index++;
                img.setIcon(new ImageIcon("" + path + "/" + index + ".png"));
                nav.setText(index + "/" + count);
                navigate.setValue(index);
            }
        });
    
    }
    
    void dimButton(){
        dim.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Screen mainWind = new Screen(new File("" + path + "/").listFiles().length, sizep_x*multiply, sizep_y*multiply, (float)(count*sizep_z));
                mainWind.setUp();
            }
        });
    }
    
    void interButton(){ //klawisz "Interpoluj"
        inter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final SwingWorker worker;
                //interpolationProc();
                worker = new SwingWorker() {
                    
                    @Override
                    protected Object doInBackground() throws Exception {    //interpolacja przebiega w tle by nie blokować gui
                        interpolationProc();
                        return null;
                    }
                };
                        //interpolationProc();
//                worker.addPropertyChangeListener(new PropertyChangeListener() {
//
//                    @Override
//                    public void propertyChange(PropertyChangeEvent evt) {
//                        if (evt.getPropertyName().equalsIgnoreCase("progress")) {
//                    int prog = worker.getProgress();
//                    if (prog == 0) {
//                        progress.setIndeterminate(true);
//                    } else {
//                        progress.setIndeterminate(false);
//                        progress.setValue(prog);
//                    }
//                }
//                    }
//                });
                
                worker.execute();
                

            }
        });
    }
    
    void optionMenuBut(){
        settings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OptFrame opt = new OptFrame(reconstructor);
            }
        });
    }
    
    void selectMenuBut(){
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION)
                    reconstruct_path = "" + chooser.getSelectedFile();
            }
        });
    }
    
    void interpolationProc(){   //procedura interpolacji
        //Checkpoint 1: Załadowanie przekrojów do programu
        //loadSettings(); //załaduj ustawienia
        //System.out.println("Wczytywanie obrazów...");
        logs.setText(logs.getText() + "Wczytywanie obrazów..." + "\n");
        Loader load = new Loader();
        load.load(reconstruct_path);
        
        //System.out.println("Wczytywanie zakończone.");
        logs.setText(logs.getText() + "Wczytywanie obrazów zakończone." + "\n");
        
        //Checkpoint 2: Generowanie bitmap z plików
        //System.out.println("Generowanie bitmap...");
        logs.setText(logs.getText() + "Generowanie bitmap..." + "\n");
        load.generateBitmaps(size_x, size_y, false);
        //System.out.println("Generowanie bitmap zakończone");
        logs.setText(logs.getText() + "Generowanie bitmap zakończone." + "\n");
        
        //ustawienie zmiennych w głównej klasie programu
        reconstructor.setCount(load.getFileNum());
        reconstructor.setModelImages(load.getImageBuffer());
        
        load = null;
        
        System.gc();
        
        progress.setMaximum(reconstructor.getcount()-1);
        progress.setValue(0);
        
        //Checkpoint 3: Konwersja do obiektu slice
        //System.out.println("Konwersja danych wejściowych...");
        logs.setText(logs.getText() + "Konwersja danych wejściowych..." + "\n");
        Translator translator = new Translator(reconstructor.getcount());
        translator.setImgSize(size_x, size_y);
        translator.convert(reconstructor.getModelImages(), reconstructor.getcount());
        logs.setText(logs.getText() + "Konwersja zakończona." + "\n");
        //System.out.println("Konwersja zakończona.");
        
        reconstructor.setSlices(translator.getSlices());
        
        //translator = null;
        //System.gc();
        
        //Checkpoint 4: Sortowanie krawędzi
        
        //System.out.println("Sortowanie krawędzi i przyporządkowywanie ziaren.");
        logs.setText(logs.getText() + "Przygotowywanie do interpolacji..." + "\n");
        Interpolator inter = new Interpolator(reconstructor.getSlices());
        inter.setSize(size_x, size_y);
        inter.setPhysicalSize(sizep_x, sizep_y, sizep_z);
        inter.manageData(reconstructor.getcount());

        logs.setText(logs.getText() + "Interpolacja..." + "\n");
        inter.setProgress(progress);
        inter.interpolate(reconstructor.getcount(), interp, textData_path, isPrinting, path, multiply);    //interpolacja
        //ParCount parInter = new ParCount();
        
        //Executor exec = Executors.newFixedThreadPool(4);
        
        //for(int i = 0; )
        
        //System.out.println("Zakończono interpolacje");
        logs.setText(logs.getText() + "Zakończono interpolację." + "\n");
        logs.setText(logs.getText() + "===================================" + "\n");
        
        //reconstructor.setSlices(inter.getFinal());
        //Checkpoint 5: Zapis zregenerowanej struktury do bitmap
        //TextureGen generator = new TextureGen();
        //generator.generate(reconstructor.getSlices(), inter.getSize());
        navigate.setMaximum(new File("" + path + "/").listFiles().length);
        img.setIcon(new ImageIcon("" + path + "/" + "0" + ".png"));  //ustaw pierwszy
        navigate.setValue(0);
    }
    
}
