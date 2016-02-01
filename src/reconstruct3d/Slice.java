
package reconstruct3d;

import java.util.ArrayList;


public class Slice {
    ArrayList<Cell> sliceCells;
    int seedCount = 0;
    
    //cells stored directly into slice, no seed segregation
    Slice(){
        sliceCells = new ArrayList<>();
    }
    
    ArrayList<Cell> getSeedCells(){
        ArrayList<Cell> nonZeros = new ArrayList<>();
        
        for(int i = 0; i < sliceCells.size(); i++)
            if(sliceCells.get(i).id != 0)
                nonZeros.add(sliceCells.get(i));
        
        return nonZeros;
    }
    
    int numberofseeds(){
        int num = 0;
        ArrayList<Integer> foundIds = new ArrayList<>();
        
        
        for(int i = 0; i < sliceCells.size(); i++)
            if(!foundIds.contains(sliceCells.get(i).id))
                foundIds.add(sliceCells.get(i).id);
        
        num = foundIds.size()-1;    //-1 ponieważ tło ma id = 0
                
        return num;
    }
    
    int maxId(){
        int max = 0;
        
        for(int i = 0; i < sliceCells.size(); i++)
            if(sliceCells.get(i).id > max)
                max = sliceCells.get(i).id;
            
            
        return max;
    }
}
