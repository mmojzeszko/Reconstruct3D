
package reconstruct3d;

import java.util.ArrayList;


public class Slice {
    ArrayList<Cell> sliceCells;
    int seedCount = 0;
    
    //cells stored directly into slice, no seed segregation
    Slice(){
        sliceCells = new ArrayList<>();
    }
    
    int numberofseeds(){
        int num = 0;
        
        for(int i = 0; i < sliceCells.size(); i++)
            if(sliceCells.get(i).id > num)
                num = sliceCells.get(i).id;
        
        return num;
    }
}
