package reconstruct3d;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Translator {
    
    Slice[] slices;
    Slice[] slices_seeds;   //przechowywuje wykryte pojedynczo ziarna
    Slice[] edges;
    ArrayList<Cell> blacks;
    int model_size = 150;
    int img_size_x = 170;
    int img_size_y = 170;
    Cell[][][] sliceTab;
    
    ArrayList<Cell> slicesArray;
    
    Translator(int count){
        this.model_size = count;
    }
    
    void setImgSize(int img_size_x, int img_size_y){
        this.img_size_x = img_size_x;
        this.img_size_y = img_size_y;
    }
    
    void initSliceTab(){
        sliceTab = new Cell[model_size+1][model_size+1][model_size+1];
        for(int z = 0; z < model_size; z++){
            for(int y = 0; y < model_size+1; y++)
                for(int x = 0; x < model_size+1; x++)
                    sliceTab[x][y][z] = new Cell(x, y, z, Color.white);
                
        }
    }
    
    void fillSliceTab(int count){
        int x = 0;
        int y = 0;
        Color col;
        
        for(int i = 0; i < count; i++)
            for(int j = 0; j < slices[i].sliceCells.size(); j++){
                x = slices[i].sliceCells.get(j).x;
                y = slices[i].sliceCells.get(j).y;
                col = slices[i].sliceCells.get(j).color;
                sliceTab[x][y][i] = new Cell(x, y, col);
            }
    }
    
    Slice[] getSlices(){
        return this.slices;
    }
    
    ArrayList<Cell> getSlicesArray(){
        return this.slicesArray;
    }
    
    Slice[] getEdges(){
        return this.edges;
    }
    
    Cell[][][] getSliceTab(){
        return this.sliceTab;
    }
    
    void excludeBackground(int count){
        for(int z = 0; z < count; z++){
            for(int i = 0; i < slices[z].sliceCells.size(); i++)
                if(slices[z].sliceCells.get(i).color.equals(Color.white)){
                    slices[z].sliceCells.get(i).id = 0;
                    slices[z].sliceCells.get(i).color = Color.white;
                    slices[z].sliceCells.get(i).bg = true;
                }
        }
    }
    
    void convert(BufferedImage[] imgBuffer, int count){ //konwertuje dane z obrazu na listę komórek
        slices = new Slice[count];
        
        for(int z = 0; z < count; z++){
            slices[z] = new Slice();
            //System.out.println(z);
            
            for(int y = 0; y < img_size_y; y++)
                for(int x = 0; x < img_size_x; x++)
                    slices[z].sliceCells.add(new Cell(x, y, z, new Color(imgBuffer[z].getRGB(x, y)))) ;
            
        }

        System.out.println("Zakończono pierwszy etap konwersji.");
        
        detectSeeds2D(count);       
        detectSeeds(count);
        excludeBackground(count);
    }
    
    void convertForAnalize(BufferedImage[] imgBuffer, int count){ //konwertuje dane z obrazu na tablicę komórek
        //slices = new Slice[count];
        slicesArray = new ArrayList<>();
        Cell ncell;
        
        for(int z = 0; z < count; z++){
            
            for(int y = 0; y < img_size_y; y++)
                for(int x = 0; x < img_size_x; x++){
                        ncell = new Cell(x, y, z, new Color(imgBuffer[z].getRGB(x, y)));
                        if(ncell.color.equals(Color.blue)){
                            ncell.id = 1;
                            slicesArray.add(ncell);
                        }else
                            ncell.id = 0;
                        
                        
                }
        }
    }

    void detectBlacks2D(int count, int slice){
        blacks = new ArrayList<>();
        
        //for(int z = 0; z < count; z++){
            //System.out.println(z);
            //blacks[z] = new Slice();
            for (int i = 0; i < slices[slice].sliceCells.size(); i++) {
                if(slices[slice].sliceCells.get(i).color.equals(Color.black)){ 
                    blacks.add(slices[slice].sliceCells.get(i));
                }
            }
        //}
    }
    
    void detectBlacks(int count){
        blacks = new ArrayList<>();
        
        for(int z = 0; z < count; z++){
            //System.out.println(z);
            //blacks[z] = new Slice();
            for (int i = 0; i < slices[z].sliceCells.size(); i++) {
                if(slices[z].sliceCells.get(i).color.equals(Color.black)){ 
                    blacks.add(slices[z].sliceCells.get(i));
                }
            }
        }
        
    }
    
    ArrayList<Cell> near(ArrayList<Cell> black, Cell cell){
        ArrayList<Cell> nh_list = new ArrayList<>();
        
        for(Cell c: black){
            if((Math.abs(c.x - cell.x) == 1 && c.y == cell.y && c.z == cell.z) || (Math.abs(c.y - cell.y) == 1 && c.x == cell.x && c.z == cell.z) || (Math.abs(c.z - cell.z) == 1 && c.x == cell.x && c.y == cell.y)){
                nh_list.add(c);
            }
        }
        
        return nh_list;
    }
    
    ArrayList<Cell> near2D(ArrayList<Cell> black, Cell cell){
        ArrayList<Cell> nh_list = new ArrayList<>();
        
        for(Cell c: black){
            if((Math.abs(c.x - cell.x) == 1 && c.y == cell.y) || (Math.abs(c.y - cell.y) == 1 && c.x == cell.x)){
                nh_list.add(c);
            }
        }
        
        return nh_list;
    }
    
    void detectSeeds2D(int count){
        int id = 2;
        //detectBlacks(count);
        ArrayList<Cell> to_check;
        ArrayList<Cell> new_to_check;
        
        for(int z = 0; z < count; z++){
            detectBlacks2D(count, z);
            //System.out.println(z);
            while(!blacks.isEmpty()){
                blacks.get(0).id = id;
                to_check = near(blacks, blacks.get(0));
                blacks.remove(0);
                
                while(!to_check.isEmpty()){
                    new_to_check = new ArrayList<>();
                    
                    for(Cell d: to_check){
                        if(blacks.contains(d)){
                            d.id = id;
                            d.seed_id = id;
                            new_to_check.addAll(near(blacks, d));
                            int ind = blacks.indexOf(d);
                            blacks.remove(ind);
                        }
                            
                    }
                    to_check = new_to_check;
                }
                id++;
            }
                
        }
    }
    
    void detectSeeds(int count){
        int id = 2;
        detectBlacks(count);
        ArrayList<Cell> to_check;
        ArrayList<Cell> new_to_check;
        
        //for(int z = 0; z < count; z++){
            //detectBlacks2D(count, z);
            //System.out.println(z);
            while(!blacks.isEmpty()){
                blacks.get(0).id = id;
                to_check = near(blacks, blacks.get(0));
                blacks.remove(0);
                
                while(!to_check.isEmpty()){
                    new_to_check = new ArrayList<>();
                    
                    for(Cell d: to_check){
                        if(blacks.contains(d)){
                            d.id = id;
                            new_to_check.addAll(near(blacks, d));
                            int ind = blacks.indexOf(d);
                            blacks.remove(ind);
                        }
                            
                    }
                    to_check = new_to_check;
                }
                id++;
            }
                
        //}
    }
    
    void printSlices(int count){
        PrintWriter writer = null;
        
        for(int i = 0; i < count; i++){
            try {
                writer = new PrintWriter("dump/debug/" + i + ".txt");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            writer.println(slices[i].sliceCells.size());
            
            for(int j = 0; j < slices[i].sliceCells.size(); j++)
                if(slices[i].sliceCells.get(i).id != 0)
                    writer.println(slices[i].sliceCells.get(j).x + " : " + slices[i].sliceCells.get(j).y + " id: " + slices[i].sliceCells.get(j).id + "////" + slices[i].sliceCells.get(j).seed_id);
            
            writer.close();
        }
        
    }
    
}
