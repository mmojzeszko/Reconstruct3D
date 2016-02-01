package reconstruct3d;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class OptFrame {
    
    JFrame optWind;
    Dimension def = new Dimension(500, 500);    //rozmiar okna z opcjami
    Dimension field = new Dimension(100, 30);   //rozmiar pola
    Dimension path = new Dimension(250, 30);    //rozmiar pola ścieżki z plikiem
    
    JPanel interp_panel;    //panel z polem do ilości przekrojów
    JTextField interp_text; //pole z liczbą przekrojów
    
    JPanel size_panel;      //panel rozmiar
    JTextField size_panel_x;    //rozmiar x
    JTextField size_panel_y;    //rozmiar y
    JPanel size_x_desc;     //opis pola x
    JPanel size_y_desc;     //opis pola y
    
    JPanel psize_panel;     //panel rozmiar fizyczny
    JTextField sizePx;      //rozmiar fizyczny x
    JTextField sizePy;      //rozmiar fizyczny y
    JPanel sizep_x_desc;    //opis pola x (rozmiar fizyczny)
    JPanel sizep_y_desc;    //opis pola y (rozmiar fizyczny)
    JTextField distP;       //odległość między przekrojami (pole)
    JPanel distP_pan;       //odległość między przekrojami (ramka)
    
    JPanel seedInfo_panel;  //zapisz ziarna w formie tekstowej
    JTextField seedInfoPath_field;  //ścieżka gdzie zapisać dane tekstowe
    JCheckBox saveLogs_check;   //czy zapisać dane
    
    JPanel saveDest;    //zapisz w...
    JTextField saveDest_text;   //ścieżka do zapisu obrazów przkrojów
    
    JPanel mainPan;     //główny panel
    
    JPanel but_panel;   //panel z przyciskami
    JButton save;       //przycisk zapisz
    JButton restore;    //przycisk przywróć
    
    JFileChooser chooser;   //wybór ścieżki pliku dla obrazów
    JFileChooser chooser_text;  //wybor ścieżki plików dla danych tekstowych
    JButton setPath;    //wybór ścieżki dla zapisu przekrojów(obrazów)
    JButton setPath_text;   //wybór ścieżki dla danych tekstowych
    
    Reconstruct3D rec;
    boolean isSavingLogs;

    OptFrame(Reconstruct3D rec) {
        optWind = new JFrame("Ustawienia");
        optWind.setSize(def);
        optWind.setLocationRelativeTo(null);
        
        mainPan = new JPanel();
        mainPan.setLayout(new BoxLayout(mainPan, BoxLayout.Y_AXIS));
        
        //optWind.setLayout(new BoxLayout(optWind, BoxLayout.Y_AXIS));
        optWind.add(mainPan);
        
        this.rec = rec;
        
        optWind.setVisible(true);
        initComp();
        
    }
    
    void initComp(){
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        chooser_text = new JFileChooser();
        chooser_text.setCurrentDirectory(new java.io.File("."));
        chooser_text.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        interp_panel = new JPanel();
        interp_panel.setBorder(new TitledBorder("Ilość interpolowanych przekrojów"));
        
        interp_text = new JTextField();
        interp_text.setPreferredSize(field);
        interp_text.setText("5");
        
        interp_panel.add(interp_text);
        
        //======================================================================
        
        size_panel = new JPanel();
        size_panel.setBorder(new TitledBorder("Rozmiar interpolowanej struktury"));
        
        size_x_desc = new JPanel();
        size_x_desc.setBorder(new TitledBorder("X"));
        
        size_y_desc = new JPanel();
        size_y_desc.setBorder(new TitledBorder("Y"));
        
        size_panel_x = new JTextField();
        size_panel_x.setPreferredSize(field);
        
        size_panel_y = new JTextField();
        size_panel_y.setPreferredSize(field);
        
        //======================================================================
        
        psize_panel = new JPanel();
        psize_panel.setBorder(new TitledBorder("Rozmiar fizyczny próbki"));
        
        sizep_x_desc = new JPanel();
        sizep_x_desc.setBorder(new TitledBorder("X"));
        
        sizep_y_desc = new JPanel();
        sizep_y_desc.setBorder(new TitledBorder("Y"));
        
        sizePx = new JTextField();
        sizePx.setPreferredSize(field);
        
        sizePy = new JTextField();
        sizePy.setPreferredSize(field);
        
        //======================================================================
        
        distP = new JTextField();
        distP_pan = new JPanel();
        
        distP.setPreferredSize(new Dimension(160, 30));
        distP_pan.setBorder(new TitledBorder("Odstęp między przekrojami"));
        distP_pan.add(distP);
        
        //======================================================================
        
        size_x_desc.add(size_panel_x);
        size_y_desc.add(size_panel_y);
        size_panel.add(size_x_desc);
        size_panel.add(size_y_desc);
        
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        
        sizep_x_desc.add(sizePx);
        sizep_y_desc.add(sizePy);
        psize_panel.add(sizep_x_desc);
        psize_panel.add(sizep_y_desc);
        psize_panel.add(distP_pan);
        
        seedInfo_panel = new JPanel();
        seedInfo_panel.setBorder(new TitledBorder("Zapisz dane o przekrojach w formie tekstowej"));
        seedInfoPath_field = new JTextField();
        seedInfoPath_field.setPreferredSize(path);
        seedInfoPath_field.setEnabled(false);
        saveLogs_check = new JCheckBox();
        setPath_text = new JButton("Wybierz...");
        setPath_text.setEnabled(false);
        
        seedInfo_panel.add(saveLogs_check);
        seedInfo_panel.add(seedInfoPath_field);
        seedInfo_panel.add(setPath_text);
        
        saveLogs_check.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if(saveLogs_check.isSelected()){
                    seedInfoPath_field.setEnabled(true);
                    setPath_text.setEnabled(true);
                }else{
                    seedInfoPath_field.setEnabled(false);
                    setPath_text.setEnabled(false);
                }
                
            }
        });
        
        
        
        but_panel = new JPanel();
        but_panel.setLayout(new FlowLayout());
        
        save = new JButton("Zapisz");
        restore = new JButton("Przywróć domyślne");
        
        but_panel.add(save);
        but_panel.add(restore);
        
        saveDest = new JPanel();
        saveDest.setBorder(new TitledBorder("Zapisz wyniki w..."));
        saveDest_text = new JTextField();
        saveDest_text.setPreferredSize(path);
        
        setPath = new JButton("Wybierz...");
        saveDest.add(saveDest_text);
        saveDest.add(setPath);
        
        mainPan.add(interp_panel);
        mainPan.add(size_panel);
        mainPan.add(psize_panel);
        mainPan.add(seedInfo_panel);
        mainPan.add(saveDest);
        mainPan.add(but_panel);
        load();
        buttons();
    }
    
    void buttons(){
        setPath.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //chooser.showOpenDialog(optWind);
                if(chooser.showOpenDialog(optWind) == JFileChooser.APPROVE_OPTION)
                    saveDest_text.setText("" + chooser.getSelectedFile());
            }
        });
        
        setPath_text.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(chooser_text.showOpenDialog(optWind) == JFileChooser.APPROVE_OPTION)
                    seedInfoPath_field.setText("" + chooser_text.getSelectedFile());
            }
        });
        
        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        
        restore.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                interp_text.setText("5");
                size_panel_x.setText("100");
                size_panel_y.setText("100");
                sizePx.setText("50");
                sizePy.setText("50");
                distP.setText("0.6");
                save();
            }
        });
    }
    
    void save(){
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter("settings.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OptFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        writer.println(interp_text.getText());
        writer.println(size_panel_x.getText());
        writer.println(size_panel_y.getText());
        writer.println(sizePx.getText());
        writer.println(sizePy.getText());
        writer.println(distP.getText());
        
        if(saveLogs_check.isSelected())
            writer.println("true");
        else
            writer.println("false");
        writer.println(seedInfoPath_field.getText());
        writer.println(saveDest_text.getText());
        
        writer.close();
        optWind.dispose();
        
    }
    
    void load(){
        FileInputStream fstream = null;
        
        try {
            fstream = new FileInputStream("settings.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OptFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        try {
            interp_text.setText(br.readLine().trim());
            size_panel_x.setText(br.readLine().trim());
            size_panel_y.setText(br.readLine().trim());
            sizePx.setText(br.readLine().trim());
            sizePy.setText(br.readLine().trim());
            distP.setText(br.readLine().trim());
            isSavingLogs = Boolean.parseBoolean(br.readLine().trim());
            seedInfoPath_field.setText(br.readLine().trim());
            saveDest_text.setText(br.readLine().trim());
            
            
            if(isSavingLogs)
                saveLogs_check.setSelected(true);
            else
                saveLogs_check.setSelected(false);
        } catch (IOException ex) {
            Logger.getLogger(OptFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
