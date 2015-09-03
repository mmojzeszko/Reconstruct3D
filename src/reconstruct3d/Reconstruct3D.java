
package reconstruct3d;

import java.awt.image.BufferedImage;


public class Reconstruct3D {
    BufferedImage []model_img;  //struktura przechowująca wczytane obrazy struktury
    Slice[] slices; //struktura przechowująca odczytane dane w formie cyfrowej
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
        
        //Checkpoint 3: Konwersja do obiektu slice
        System.out.println("Konwersja danych wejściowych...");
        Translator translator = new Translator(reconstructor.getcount());
        translator.convert(reconstructor.getModelImages(), reconstructor.getcount());
        System.out.println("Konwersja zakończona.");
        
        reconstructor.setSlices(translator.getSlices());
        
        //Checkpoint 4: Sortowanie krawędzi
        
        System.out.println("Sortowanie krawędzi i przyporządkowywanie ziaren.");
        Interpolator inter = new Interpolator(translator.getEdges(), reconstructor.getSlices());
        inter.manageData(reconstructor.getcount());

        inter.interpolate(reconstructor.getcount());    //interpolacja
        
        reconstructor.setSlices(inter.getFinal());
        //Checkpoint 5: Zapis zregenerowanej struktury do bitmap
        TextureGen generator = new TextureGen();
        generator.generate(reconstructor.getSlices(), inter.getSize());
        
        
    }
    
}
