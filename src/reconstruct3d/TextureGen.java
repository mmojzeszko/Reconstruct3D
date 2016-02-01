
package reconstruct3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;


public class TextureGen extends JComponent{
    
    private float texture_width = 170;  //rozmiary obrazów
    private float texture_height = 170;
    BufferedImage offImg;
    Color[] colorPalette;
    Color transp = new Color(0, 0, 0, 1);
    int t_num = 4;
    
    void setColorPalette(Color []colorPalette){
        this.colorPalette = colorPalette;
    }
    
    Color[] getColorPalette(){
        return this.colorPalette;
    }
    
    void setPrintSize(int texture_width, int texture_height){
        this.texture_width = texture_width;
        this.texture_height = texture_height;
    }
    
    public BufferedImage horizontalflip(BufferedImage img) {  //obraca obraz horyzontalnie
        int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(w, h, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);  
        g.dispose();  
        return dimg;  
    }
    
    Graphics2D createCanvas(){
        Graphics2D g2 = null;
        
        //offImg = (BufferedImage)createImage((int)texture_width, (int)texture_height);
        offImg = new BufferedImage((int)texture_width, (int)texture_height, BufferedImage.TYPE_INT_ARGB);
        
        g2 = offImg.createGraphics();
        g2.setBackground(Color.white);

        // .. set attributes ..
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        // .. clear canvas ..
        g2.clearRect(0, 0, getSize().width, getSize().height);

        return g2;
    }
    
    void initColorPalette(){
        int col_num = 1000;
        colorPalette = new Color[col_num];
        Random rand = new Random();
        
        for(int i = 0; i < col_num; i++)
            colorPalette[i] = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 255);
        
    }
    
    Color genColor(int id){
        Color gen;
        Random rand = new Random();
        
        int mn = rand.nextInt(5);
        gen = colorPalette[id];
        
        return gen;
    }
    
    void dumpTestData(Slice slice){
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter("dump/" + "logs2" + ".txt", true)));
        } catch (IOException ex) {
            Logger.getLogger(TextureGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i = 0; i < slice.sliceCells.size(); i++)
            if(slice.sliceCells.get(i).id != 0)
                writer.println(slice.sliceCells.get(i).x + " " + slice.sliceCells.get(i).y + " " + slice.sliceCells.get(i).z + " " + slice.sliceCells.get(i).id);
    }
    
    void clearOldFiles(String path){
        //int howMany = new File(path).listFiles().length;
        
        File directory = new File(path);
        File[] f = directory.listFiles();
        
        for (File file : f)
            if (file.exists())
                file.delete();
            
    }
    
    void generate(Slice slice, int i, String path){
        Graphics2D g2 = (Graphics2D)createCanvas();
        //initColorPalette();
        //ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(t_num);
        
        //for(int i = 0; i < count; i++){
            //exec.execute(new ParTexGen(slices, colorPalette, i));
        //dumpTestData(slice);
            for(int j = 0; j < slice.sliceCells.size(); j++){
                
                
//                if(slice.sliceCells.get(j).id == 0)
//                    g2.setColor(Color.white);
//                if(slice.sliceCells.get(j).id == 1)
//                    g2.setColor(Color.black);
//                else
                if(slice.sliceCells.get(j).id == 0 || slice.sliceCells.get(j) == null)
                    g2.setColor(transp);
                else
                    //g2.setColor(this.genColor(slice.sliceCells.get(j).id));
                    g2.setColor(Color.blue);
                
//                if(slice.sliceCells.get(j).color.equals(Color.white))
//                    g2.setColor(Color.white);
                
                int x = slice.sliceCells.get(j).x;
                int y = slice.sliceCells.get(j).y;
                
                g2.fillRect(x, y, 1, 1);
            }
            
            File output = new File(path + "/" + i + ".png");
            try {
                ImageIO.write(offImg, "png", output);
            } catch (IOException ex) {
                Logger.getLogger(TextureGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        //exec.shutdown();
        
//        try {
//            exec.awaitTermination(5, TimeUnit.MINUTES);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Interpolator.class.getName()).log(Level.SEVERE, null, ex);
//        }
            
    
    }
    
    void generateForAnalize(ArrayList<Cell> struct, int i, String path){
        
        offImg = null;
        Graphics2D g2 = (Graphics2D)createCanvas();
            for(int j = 0; j < struct.size(); j++){

                if(struct.get(j).id == 0 || struct.get(j) == null)
                    g2.setColor(transp);
                else
                    //g2.setColor(this.genColor(struct.get(j).id));
                    g2.setColor(Color.blue);

                
                int x = struct.get(j).x;
                int y = struct.get(j).y;
                
                g2.fillRect(x, y, 1, 1);
            }
            
            File output = new File(path + i + ".png");
            
            try {
                ImageIO.write(offImg, "png", output);
            } catch (IOException ex) {
                Logger.getLogger(TextureGen.class.getName()).log(Level.SEVERE, null, ex);
            }
            
    }
    
    void generateXaxis(ArrayList<Cell> struct, int i, String path){
        offImg = null;
        Graphics2D g2 = (Graphics2D)createCanvas();
        //System.out.println(struct.size());
            for(int j = 0; j < struct.size(); j++){

                if(struct.get(j).id == 0 || struct.get(j) == null)
                    g2.setColor(transp);
                else
                    //g2.setColor(this.genColor(struct.get(j).id));
                    g2.setColor(Color.blue);
                
                int x = struct.get(j).z;
                int y = struct.get(j).y;
                
                g2.fillRect(x, y, 1, 1);
            }
            
            File output = new File(path + "/" + i + ".png");
            try {
                ImageIO.write(offImg, "png", output);
            } catch (IOException ex) {
                Logger.getLogger(TextureGen.class.getName()).log(Level.SEVERE, null, ex);
            }
            
    }
    
}
