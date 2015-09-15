
package reconstruct3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class ParTexGen implements Runnable{
    
    private TextureGen tex;
    Slice []slices;
    Color []colorPalette;
    int i;
    
    public ParTexGen(Slice []slices, Color []colorPalette, int i) {
        //this.tex = tex;
        tex = new TextureGen();
        tex.setColorPalette(colorPalette);
        this.slices = slices;
        this.colorPalette = colorPalette;
        this.i = i;
    }
    
    

    @Override
    public void run() {
        Graphics2D g2 = (Graphics2D)tex.createCanvas();
        
        for(int j = 0; j < slices[i].sliceCells.size(); j++){
                if(slices[i].sliceCells.get(j).id == 0)
                    g2.setColor(Color.white);
                if(slices[i].sliceCells.get(j).id == 1)
                    g2.setColor(Color.black);
                else
                    g2.setColor(tex.genColor(slices[i].sliceCells.get(j).id));
                
                int x = slices[i].sliceCells.get(j).x;
                int y = slices[i].sliceCells.get(j).y;
                
                g2.fillRect(x, y, 1, 1);
            }
            
            File output = new File("Regen/VolumeSplices/"+""+i+".png");
            try {
                ImageIO.write(tex.offImg, "png", output);
            } catch (IOException ex) {
                Logger.getLogger(TextureGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
}
