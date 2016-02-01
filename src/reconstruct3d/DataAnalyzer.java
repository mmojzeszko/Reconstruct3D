package reconstruct3d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAnalyzer {
    
    ArrayList<Cell> cells;
    ArrayList<Cell> loadedCells;
    Slice[] toPrint;
    int SizeX = 100;
    int SizeY = 100;
    int SizeZ = 0;
    int multiply = 2;
    int interStr = 0;
    double distZ = 0.6;
    String path;

    DataAnalyzer(int x, int y, double z, int interStr, int count, String path){
        
        this.SizeX = x * multiply;
        this.SizeY = y * multiply;
        
        this.interStr = interStr;
        this.SizeZ = count;
        this.SizeZ = (int)((this.SizeZ * z) * multiply);
        //this.SizeZ = 100;
        System.out.println(SizeX);
        System.out.println(SizeY);
        System.out.println(SizeZ);
        this.path = path;
    }
    
    void setMultiply(int multiply){
    this.multiply = multiply;
    }
 
    void fill(){
        boolean found = false;
        cells = new ArrayList<>();
        
        for(int x = 0; x < SizeX; x++){
            for(int y = 0; y < SizeY; y++)
                for(int z = 0; z < SizeZ; z++){
                    for(int k = 0; k < loadedCells.size(); k++)
                        if(loadedCells.get(k).x == x && loadedCells.get(k).y == y && loadedCells.get(k).z == z){
                            cells.add(loadedCells.get(k));
                            loadedCells.remove(k);
                            found = true;
                            break;
                        }

                    if(found){
                        found = false;
                    }else
                        cells.add(new Cell(x, y, z, 0));
                }
            save(cells);
            cells.clear();
            System.out.println(x + "/" + (SizeX-1));
        }
    }
    
    void printInit(){
        toPrint = new Slice[interStr];
        
        for(int i = 0; i < interStr; i++){
            toPrint[i] = new Slice();
            //toPrint[i].sliceCells = new ArrayList<>();
        }
    }
    
    void save(ArrayList<Cell> partCell){
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter("logs" + ".txt", true)));
        } catch (IOException ex) {
            Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

        for(int z = 0; z < cells.size(); z++)
            writer.println(cells.get(z).x + " " + cells.get(z).y + " " + cells.get(z).z + " " + cells.get(z).id);
        
        writer.close();
    }
    
    void allocate(){
        printInit();
        for(int k = 0; k < loadedCells.size(); k++)
            toPrint[loadedCells.get(k).z].sliceCells.add(loadedCells.get(k));
    }
    
    void allocateXaxis(){
        printInit();
        for(int k = 0; k < loadedCells.size(); k++)
            toPrint[loadedCells.get(k).x].sliceCells.add(loadedCells.get(k));
    }

    void analyze(){
        //DataAnalyzer gen = new DataAnalyzer();
        loadedCells = new ArrayList<>();

        
        Loader loader = new Loader();
        loader.load(path);
        loader.generateBitmaps(SizeX, SizeY, true);
        
        System.out.println(loader.getFileNum());
        
        Translator tr = new Translator(interStr);
        tr.setImgSize(SizeX, SizeY);
        tr.convertForAnalize(loader.getImageBuffer(), interStr);
        
        loadedCells.addAll(tr.getSlicesArray());
        
        System.out.println("Done 1/2");
        
        //======================================================================
        
        allocate();
        
        TextureGen generator = new TextureGen();
        
        generator.initColorPalette();
        generator.setPrintSize(SizeX, SizeY);
        generator.clearOldFiles("test/");
        
        for(int z = 0; z < interStr; z++)
            generator.generateForAnalize(toPrint[z].sliceCells, z, "test/");
        //gen.fill();
        
        //======================================================================
        
        allocateXaxis();
        generator.setPrintSize(interStr, SizeY);
        generator.clearOldFiles("testX/");
        
        for(int z = 0; z < SizeX; z++)
            generator.generateXaxis(toPrint[z].sliceCells, z, "testX/");
        
        //======================================================================
        
        loader = new Loader();
        
        loader.load("testX/");
        loader.generateBitmaps(SizeZ, SizeY, true);
        
        Translator tr_fin = new Translator(SizeZ);
        tr_fin.setImgSize(SizeZ, SizeY);
        tr_fin.convertForAnalize(loader.getImageBuffer(), SizeX);
        
        loadedCells.clear();
        loadedCells.addAll(tr_fin.getSlicesArray());
        
        allocateXaxis();
        
        generator.setPrintSize(SizeX, SizeY);
        generator.clearOldFiles("test-final/");
        generator.clearOldFiles(path);
        
        for(int z = 0; z < SizeZ; z++)
            generator.generateXaxis(toPrint[z].sliceCells, z, path);
        
        File file = new File("logs.txt");
        if(file.exists())
            file.delete();
        
        fill();
        
        System.out.println("done");
    }
    
}
