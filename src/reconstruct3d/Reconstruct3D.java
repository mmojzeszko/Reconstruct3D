
package reconstruct3d;

import java.awt.image.BufferedImage;


public class Reconstruct3D {
    BufferedImage []model_img;
    Slice[] slices;
    int count = 0;
    
    void setModelImages(BufferedImage[] model_img){
        this.model_img = model_img;
    }
    
    BufferedImage[] getModelImages(){
        return this.model_img;
    }
    
    void setCount(int count){
        this.count = count;
    }
    
    int getcount(){
        return this.count;
    }
    
    void setSlices(Slice[] slices){
        this.slices = slices;
    }
    
    Slice[] getSlices(){
        return this.slices;
    }

    public static void main(String[] args) {
        Reconstruct3D reconstructor = new Reconstruct3D();
        
        //Checkpoint 1: Load bitmaps for reconstruction
        System.out.println("Loading splices...");
        Loader load = new Loader();
        load.load();
        
        System.out.println("Loading splices. Done");
        
        //Checkpoint 2: Generate bitmaps
        System.out.println("Generating bitmaps...");
        load.generateBitmaps();
        System.out.println("Generating bitmaps. Done");
        
        //setting obtained data to data holder
        reconstructor.setCount(load.getFileNum());
        reconstructor.setModelImages(load.getImageBuffer());
        
        //Checkpoint 3: Convert bitmaps to tables(slices)
        System.out.println("Translating input...");
        Translator translator = new Translator();
        translator.convert(reconstructor.getModelImages(), reconstructor.getcount());
        System.out.println("Translating input. Done");
        
        reconstructor.setSlices(translator.getSlices());
        
        //Checkpoint 4: Sort edges
        //System.out.println("Sorting edges");
        //Sorter sort = new Sorter();
        
        System.out.println("Managing data");
        Interpolator inter = new Interpolator(translator.getEdges(), reconstructor.getSlices());
        inter.manageData(reconstructor.getcount());
        //inter.manageSeeds(reconstructor.getcount());
        //inter.seedEdgePrep(reconstructor.getcount());
        inter.interpolate(reconstructor.getcount());
        
        reconstructor.setSlices(inter.getFinal());
        //Checkpoint 4: Save test data
        TextureGen generator = new TextureGen();
        generator.generate(reconstructor.getSlices(), inter.getSize());
        
        
    }
    
}
