
package reconstruct3d;

import java.awt.Color;
import java.awt.image.BufferedImage;


public class ParTranslate implements Runnable{  //klasa używana przy równoległej konwersji

    BufferedImage []imgBuffer;
    Slice []slices;
    int img_size;
    int z;
    
    public ParTranslate(BufferedImage[] imgBuffer, Slice []slices, int img_size, int z) {
        this.imgBuffer = imgBuffer;
        this.slices = slices;
        this.img_size = img_size;
        this.z = z;
    }

    @Override
    public void run() {
        for(int y = 0; y < img_size; y++)
                for(int x = 0; x < img_size; x++)
                    slices[z].sliceCells.add(new Cell(x, y, z, new Color(imgBuffer[z].getRGB(x, y))));
    }
    
}
