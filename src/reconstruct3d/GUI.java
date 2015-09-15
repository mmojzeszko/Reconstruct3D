package reconstruct3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.BasicArrowButton;


public class GUI {
    
    JFrame window;  //główne okno
    JPanel control; //panel kontrolny
    JPanel lowerButtons;    //dolne klawisze(Interpoluj, widok 3D)
    JLabel nav;
    JButton inter;  //klawisz "Interpoluj"
    JButton dim;    //klawisz "widok 3D"
    JButton img;    //wyświetlanie struktury
    JProgressBar progress;  //progress bar
    
    JMenu file; //menu "Plik"
    JMenuBar menuBar;   //całe menu
    JMenuItem load;     //podmenu "Wczytaj"
    JSlider navigate;
    
    BasicArrowButton left;  //klawisz lewo
    BasicArrowButton right; //klawisz prawo
    
    Dimension window_size = new Dimension(600, 600);    //rozmiar okna
    Reconstruct3D reconstructor;
    
    int index = 0;
    int count = 100;
    boolean done = false;
    
    GUI(Reconstruct3D reconstructor){   //konstruktor gui, tworzy okno interfejsu
        this.reconstructor = reconstructor;
        initButtons();
        
        window = new JFrame("Rekonstruktor");
        window.setSize(window_size);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());
        
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
    }
    
    void initButtons(){ //inicjalizacja klawiszy
        control = new JPanel();
        control.setLayout(new BorderLayout());
        
        lowerButtons = new JPanel();
        
        inter = new JButton("Interpoluj");
        dim = new JButton("Widok 3D");
        img = new JButton();
        
        menuBar = new JMenuBar();
        file = new JMenu("Plik");
        load = new JMenuItem("Wczytaj");
        
        file.add(load);
        
        menuBar.add(file);
        
        left = new BasicArrowButton(BasicArrowButton.LEFT);
        right = new BasicArrowButton(BasicArrowButton.RIGHT);
        arrowButtons();
        interButton();
        
        progress = new JProgressBar();
        nav = new JLabel("0/0");
        navigate = new JSlider();
        navigate.setValue(index);
        navigate.setMinimum(0);
        navigate.setMaximum(new File("Regen/VolumeSplices/").listFiles().length);
        
        navigate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e){
                index = navigate.getValue();
                img.setIcon(new ImageIcon("Regen/VolumeSplices/" + index + ".png"));
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
                img.setIcon(new ImageIcon("Regen/VolumeSplices/" + index + ".png"));
                nav.setText(index + "/" + count);
            }
        });
        
        right.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(index < count)
                    index++;
                img.setIcon(new ImageIcon("Regen/VolumeSplices/" + index + ".png"));
                nav.setText(index + "/" + count);
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
    
    void interpolationProc(){   //procedura interpolacji
        //Checkpoint 1: Załadowanie przekrojów do programu
        System.out.println("Wczytywanie obrazów...");
        Loader load = new Loader();
        load.load();
        
        System.out.println("Wczytywanie zakończone.");
        
        //Checkpoint 2: Generowanie bitmap z plików
        System.out.println("Generowanie bitmap...");
        load.generateBitmaps();
        System.out.println("Generowanie bitmap zakończone");
        
        //ustawienie zmiennych w głównej klasie programu
        reconstructor.setCount(load.getFileNum());
        reconstructor.setModelImages(load.getImageBuffer());
        
        load = new Loader();
        load = null;
        
        System.gc();
        
        progress.setMaximum(reconstructor.getcount()-1);
        progress.setValue(0);
        
        //Checkpoint 3: Konwersja do obiektu slice
        System.out.println("Konwersja danych wejściowych...");
        Translator translator = new Translator(reconstructor.getcount());
        translator.convert(reconstructor.getModelImages(), reconstructor.getcount());
        System.out.println("Konwersja zakończona.");
        
        reconstructor.setSlices(translator.getSlices());
        
        translator = null;
        System.gc();
        
        //Checkpoint 4: Sortowanie krawędzi
        
        System.out.println("Sortowanie krawędzi i przyporządkowywanie ziaren.");
        Interpolator inter = new Interpolator(reconstructor.getSlices());
        inter.manageData(reconstructor.getcount());

        inter.setProgress(progress);
        inter.interpolate(reconstructor.getcount());    //interpolacja
        //ParCount parInter = new ParCount();
        
        //Executor exec = Executors.newFixedThreadPool(4);
        
        //for(int i = 0; )
        
        System.out.println("Zakończono interpolacje");
        
        reconstructor.setSlices(inter.getFinal());
        //Checkpoint 5: Zapis zregenerowanej struktury do bitmap
        TextureGen generator = new TextureGen();
        generator.generate(reconstructor.getSlices(), inter.getSize());
        navigate.setMaximum(new File("Regen/VolumeSplices/").listFiles().length);
        img.setIcon(new ImageIcon("Regen/VolumeSplices/" + index + ".png"));
    }
    
}
