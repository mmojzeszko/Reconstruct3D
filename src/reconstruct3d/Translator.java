package reconstruct3d;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Translator {
    
    Slice[] slices;
    Slice[] edges;
    ArrayList<Cell> blacks;
    int model_size = 100;
    Cell[][][] sliceTab;
    
    void initSliceTab(){
        sliceTab = new Cell[model_size+1][model_size+1][model_size+1];
        for(int z = 0; z < model_size; z++){
            for(int y = 0; y < model_size+1; y++)
                for(int x = 0; x < model_size+1; x++)
                    sliceTab[x][y][z] = new Cell(x, y, z, Color.white);
                    //sliceTab[x][y][z].id = 0;
                
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
                sliceTab[x][y][i] = new Cell(x, y, col); //!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
    }
    
    Slice[] getSlices(){
        return this.slices;
    }
    
    Slice[] getEdges(){
        return this.edges;
    }
    
    Cell[][][] getSliceTab(){
        return this.sliceTab;
    }
    
    void convert(BufferedImage[] imgBuffer, int count){
        slices = new Slice[count];  //establish data table for slices
        
        for(int z = 0; z < count; z++){
            slices[z] = new Slice();    //initiate slices table
            
            for(int y = 0; y < model_size; y++)
                for(int x = 0; x < model_size; x++)
                    slices[z].sliceCells.add(new Cell(x, y, z, new Color(imgBuffer[z].getRGB(x, y)))) ;
                    //System.out.println(imgBuffer[z].getRGB(x, y));
            
        }
        initSliceTab();
        fillSliceTab(count);
        
        //detectEdges(count);
        detectSeeds2(count);
        
        printSlices(count);
    }
    
    void removeErr(int count){
        //int id = 2;
        
        for(int z = 0; z < model_size; z++){
            //id = 2;
            for(int y = 0; y < model_size; y++)
                for(int x = 1; x < model_size; x++){
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x+1][y][z].color))
                            sliceTab[x][y][z].id = sliceTab[x-1][y][z].id;
                        
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x-1][y][z].color))
                            sliceTab[x][y][z].id = sliceTab[x+1][y][z].id;
                }
        }
        
        for(int z = 0; z < model_size; z++){
            //id = 2;
            for(int y = 1; y < model_size; y++)
                for(int x = 0; x < model_size; x++){
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x][y+1][z].color))
                            sliceTab[x][y][z].id = sliceTab[x][y-1][z].id;
                        
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x][y-1][z].color))
                            sliceTab[x][y][z].id = sliceTab[x][y+1][z].id;
                }
            
            for(int x = 1; x < model_size; x++){
                 if(!sliceTab[x][0][z].color.equals(sliceTab[x+1][0][z].color))
                    sliceTab[x][0][z].id = sliceTab[x-1][0][z].id;
            }
            
        }
    
    }
    
    void detectBlacks(int count){
        blacks = new ArrayList<>();
        
        for(int z = 0; z < count; z++){
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
    
    void detectSeeds2(int count){
        int id = 2;
        detectBlacks(count);
        ArrayList<Cell> to_check;
        ArrayList<Cell> new_to_check;
        
        for(int z = 0; z < count; z++){
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
                
        }
    }
    
    void detectSeeds(int count){
        int id = 2;
        boolean change = false;
        boolean phased = true;
        
        for(int z = 0; z < model_size; z++){
            id = 2;
            for(int y = 0; y < model_size; y++)
                for(int x = 0; x < model_size; x++){
                    change = false;
                    if(sliceTab[x][y][z].id == 0){
                        if(sliceTab[x+1][y][z].border != true && sliceTab[x+1][y][z].id != 0){
                            sliceTab[x][y][z].id = sliceTab[x+1][y][z].id;
                            change = true;
                        }
                        
                        if(x > 0)
                            if(sliceTab[x-1][y][z].border != true && sliceTab[x-1][y][z].id != 0){
                                sliceTab[x][y][z].id = sliceTab[x-1][y][z].id;
                                change = true;
                            }
                        
                        if(sliceTab[x][y+1][z].border != true && sliceTab[x][y+1][z].id != 0){
                            sliceTab[x][y][z].id = sliceTab[x][y+1][z].id;
                            change = true;
                        }
                        
                        if(y > 0)
                            if(sliceTab[x][y-1][z].border != true && sliceTab[x][y-1][z].id != 0){
                                sliceTab[x][y][z].id = sliceTab[x][y-1][z].id;
                                change = true;
                            }
                        
                        //corner fix
                        if(x > 0 && y > 0){
                            int n = 0;
                            if(sliceTab[x-1][y][z].border == true && sliceTab[x][y-1][z].border == true){
                                if(sliceTab[x+1][y-1][z].border != true){
                                    sliceTab[x][y][z].id = sliceTab[x+1][y-1][z].id;
                                    change = true;
                                }
                                
//                                while(sliceTab[x+n][y-1][z].id == 1)
//                                    n++;
//                                
//                                sliceTab[x][y][z].id = sliceTab[x+n][y-1][z].id;
//                                change = true;
                            }
                        }
                        
                        
                    }
                    if(!change){
                        if(sliceTab[x][y][z].border != true){
                            if(phased)
                                if(!sliceTab[x][y][z].color.equals(Color.white)){
                                    sliceTab[x][y][z].id = id;
                                    id++;
                                }
                        }
                        //change = false;
                    }
                    
                    //if(sliceTab[x][y][z].id == 1)
                        //id++;
                    
                        
                }
            
            
            //for(int v = 0; v < id; v++)
            //slices[z].seedCount = id;
                
    }
        
        //wrong seed fix
        
        for(int z = 0; z < model_size; z++)
            for(int y = model_size-1; y > 0; y--)
                for(int x = model_size-1; x > 0; x--){
                    if(sliceTab[x][y][z].id != sliceTab[x-1][y][z].id && sliceTab[x][y][z].border != true && sliceTab[x-1][y][z].border != true)
                        sliceTab[x-1][y][z].id = sliceTab[x][y][z].id;
                    else
                        if(sliceTab[x][y][z].id != sliceTab[x-1][y][z].id && sliceTab[x][y][z].border == true && sliceTab[x-1][y][z].border != true)
                            sliceTab[x][y][z].id = sliceTab[x-1][y][z].id;
                }
                        
        
        //======================================================================
           
        for(int z = 0; z < model_size; z++)
            for(int x = 0; x < model_size; x++)
                for(int y = model_size-1; y >= 0; y--){
                    if(sliceTab[x][y][z].id != sliceTab[x][y+1][z].id && sliceTab[x][y][z].border != true && sliceTab[x][y+1][z].border != true)
                        sliceTab[x][y][z].id = sliceTab[x][y+1][z].id;
                    else
                        if(sliceTab[x][y][z].id != sliceTab[x][y+1][z].id && sliceTab[x][y][z].border != true && sliceTab[x][y+1][z].border == true)
                            sliceTab[x][y+1][z].id = sliceTab[x][y][z].id;
                }
        
        //======================================================================
        
        for(int z = 0; z < model_size; z++)
            for(int y = 0; y < model_size; y++)
                for(int x = 0; x < model_size-1; x++){
                    if(x > 0)
                        if(sliceTab[x][y][z].id != sliceTab[x-1][y][z].id && sliceTab[x][y][z].border != true && sliceTab[x-1][y][z].border != true)
                            sliceTab[x-1][y][z].id = sliceTab[x][y][z].id;
                    else
                    if(sliceTab[x][y][z].id != sliceTab[x+1][y][z].id && sliceTab[x][y][z].border != true && sliceTab[x+1][y][z].border != true)
                        sliceTab[x+1][y][z].id = sliceTab[x][y][z].id;
                }
        
        //======================================================================
        
//        for(int z = 0; z < model_size; z++)
//            for(int y = 1; y < model_size; y++)
//                for(int x = 1; x < model_size-1; x++){
//                    if(sliceTab[x][y][z].id == 1){
//                        
//                    }
//                }
        removeErr(count);
        
        for(int z = 0; z < count; z++)
            for(int i = 0; i < slices[z].sliceCells.size(); i++)
                slices[z].sliceCells.set(i, sliceTab[slices[z].sliceCells.get(i).x][slices[z].sliceCells.get(i).y][z]);
    }
    
    void detectEdges2(int count){
        for(int z = 0; z < model_size; z++)
            for(int y = 1; y < model_size; y++)
                for(int x = 1; x < model_size; x++){
                    if(!sliceTab[x][y][z].color.equals(sliceTab[x+1][y][z].color)){
                        sliceTab[x][y][z].border = true;
                        sliceTab[x][y][z].id = 1;
                    }else
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x-1][y][z].color)){
                            sliceTab[x][y][z].border = true;
                            sliceTab[x][y][z].id = 1;
                    }else
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x][y+1][z].color)){
                            sliceTab[x][y][z].border = true;
                            sliceTab[x][y][z].id = 1;
                    }else
                        if(!sliceTab[x][y][z].color.equals(sliceTab[x][y-1][z].color)){
                            sliceTab[x][y][z].border = true;
                            sliceTab[x][y][z].id = 1;
                    }
    
                }
    }
    
    void detectEdges(int count){
        edges = new Slice[count];
        
        for(int z = 0; z < model_size; z++){
            for(int y = 0; y < model_size; y++)
                for(int x = 0; x < model_size; x++){
                    if(!sliceTab[x][y][z].color.equals(sliceTab[x+1][y][z].color)){
                        sliceTab[x][y][z].id = 1;
                        sliceTab[x][y][z].border = true;
                    }
                    
                    if(!sliceTab[x][y][z].color.equals(sliceTab[x][y+1][z].color)){
                        sliceTab[x][y][z].id = 1;
                        sliceTab[x][y][z].border = true;
                    }
                    
                    //else
                        //sliceTab[x][y][z].id = 0;
                }
        }
        
        
        //convert back to slices
        for(int z = 0; z < count; z++){
            edges[z] = new Slice();
            for(int i = 0; i < slices[z].sliceCells.size(); i++){
                slices[z].sliceCells.set(i, sliceTab[slices[z].sliceCells.get(i).x][slices[z].sliceCells.get(i).y][z]);
                if(slices[z].sliceCells.get(i).border == true)
                    edges[z].sliceCells.add(slices[z].sliceCells.get(i));   //if edge then add to edges table
            }
        }
    }
    
    void printSlices(int count){
        PrintWriter writer = null;
        
        for(int i = 0; i < count; i++){
            try {
                writer = new PrintWriter("dump/" + i + ".txt");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            writer.println(slices[i].sliceCells.size());
            
            for(int j = 0; j < slices[i].sliceCells.size(); j++)
                writer.println(slices[i].sliceCells.get(j).x + " : " + slices[i].sliceCells.get(j).y + " id: " + slices[i].sliceCells.get(j).id);
            
            writer.close();
        }
        
    }
    
}
