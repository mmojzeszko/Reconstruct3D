/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reconstruct3d;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Mateusz
 */
public class Loader {
    int count = 0;
    File []model;   //files used for model
    BufferedImage []model_img;
    BufferedImage []temp;
    int size = 170;
    
    int getFileNum(){
        return this.count;
    }
    
    BufferedImage[] getImageBuffer(){
        return this.model_img;
    }
    
    void load(){
        count = new File("Splices/").listFiles().length;    //ile przekrojów
        System.out.println("File count: " + count);
        
        model = new File[count];
        model_img = new BufferedImage[count];
        temp = new BufferedImage[count];
        
        File directory = new File("Splices/");
        File[] f = directory.listFiles();
        
        //======================================================================
        
        //sortuje rosnąco
        Arrays.sort(f, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                try {
                    String name = f1.getName();     //usuń rozszerzenie
                    int pos = name.lastIndexOf(".");
                    if (pos > 0) {
                        name = name.substring(0, pos);
                    }
                    
                    String name2 = f2.getName();    //usuń rozszerzenie
                    int pos2 = name2.lastIndexOf(".");
                    if (pos2 > 0) {
                        name2 = name2.substring(0, pos2);
                    }
                    
                    int i1 = Integer.parseInt(name);
                    int i2 = Integer.parseInt(name2);
                    return i1 - i2;
                } catch(NumberFormatException e) {
                    throw new AssertionError(e);
                }
            }
        });

        //======================================================================
        
        int i = 0;
        
        for (File file : f) {
            if (file != null && file.getName().toLowerCase().endsWith(".png")){
                model[i] = file;
            }
            i ++;

        }
    
    }
    
    BufferedImage getScaledImage(BufferedImage srcImg, int w, int h){   //skaluje i interpoluje obraz do zadanych rozmiarów
        
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        
        return resizedImg;
    }
    
    void generateBitmaps(){ //generuje tablicę wczytanych obrazów jako obiekt BufferedImage
        for(int i = 0; i < count; i++){
            try {
                temp[i] = ImageIO.read(model[i]);
            } catch (IOException ex) {
                Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for(int i = 0; i < count; i++){
            model_img[i] = getScaledImage(temp[i], size, size);
            File output = new File(i + ".png");
            try {
                ImageIO.write(model_img[i], "png", output);
            } catch (IOException ex) {
                Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
