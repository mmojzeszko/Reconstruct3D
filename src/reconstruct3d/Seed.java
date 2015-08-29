
package reconstruct3d;

import java.awt.Color;
import java.util.ArrayList;


public class Seed {
    ArrayList<Cell> seedCells;
    ArrayList<Cell> edgeCells;
    Cell center;
    boolean processed = false;
    
    int id(){
        int id = 0;
        if(this.seedCells.size() > 0)
            id = this.seedCells.get(0).id;
        
        return id;
    }
    
    void setId(int id){
        for(int i = 0; i < this.seedCells.size(); i++)
            this.seedCells.get(i).id = id;
        
        for(int i = 0; i < this.edgeCells.size(); i++)
            this.edgeCells.get(i).id = id;
    }
    
    Cell Center(){
        //double center = 0;
        double sum_x = 0;
        double sum_y = 0;
        
        for(int i = 0; i < this.edgeCells.size(); i++){
            sum_x = this.edgeCells.get(i).x;
            sum_y = this.edgeCells.get(i).y;
        }
        
        for(int i = 0; i < this.seedCells.size(); i++){
            sum_x = this.seedCells.get(i).x;
            sum_y = this.seedCells.get(i).y;
        }
        
        sum_x = sum_x/(this.edgeCells.size() + this.seedCells.size());    //center of weight
        sum_y = sum_y/(this.edgeCells.size() + this.seedCells.size());    //
        
        center = new Cell((int)sum_x, (int)sum_y, Color.yellow);
        
        
        return center;
    }
    
    double W1(){
        double w1 = 0;
        w1 = 2*Math.sqrt(this.seedCells.size()/Math.PI);
    
        return w1;
    }
    
    double W2(){
        double w2 = 0;
        w2 = this.edgeCells.size()/Math.PI;
        
        return w2;
    }
    
    double W3(){
        double w3 = 0;
        w3 = this.edgeCells.size()/(2*Math.sqrt(Math.PI*this.seedCells.size()))-1;
        
        return w3;
    }
    
    double W4(){
        double w4 = 0;
        w4 = this.seedCells.size()/(2*Math.sqrt(Math.PI*this.seedCells.size()))-1;
        
        return w4;
    }
    
    double W5(){
        double w5 = 0;
        Cell cnt = Center();
        double sum = 0;
        double sum2 = 0;
        
        for(int i = 0; i < this.edgeCells.size(); i++){
            sum += Math.sqrt(Math.pow((this.edgeCells.get(i).x - cnt.x), 2) + Math.pow((this.edgeCells.get(i).y - cnt.y), 2));
            sum2 += Math.pow((this.edgeCells.get(i).x - cnt.x), 2) + Math.pow((this.edgeCells.get(i).y - cnt.y), 2);
        }
        
        w5 = Math.sqrt(Math.pow(sum, 2)/(this.edgeCells.size() * sum2 - 1));
        
        
        return w5;
    }
    
    double W6(){
        double w6 = 0;
        Cell cnt = Center();
        double min = 10000;
        double max = 0;
        double tmp = 0;
        
        for(int i = 0; i < this.edgeCells.size(); i++){
            tmp = Math.sqrt(Math.pow((this.edgeCells.get(i).x - cnt.x), 2) + Math.pow((this.edgeCells.get(i).y - cnt.y), 2));
            
            if(tmp > max)
                max = tmp;
            
            if(tmp < min)
                min = tmp;
        }
        
        w6 = min/max;
        
        return w6;
    }
    
    double W7(){
        double w7 = 0;
        
        
        return w7;
    }
    
    double W8(){
        double w8 = 0;
        
        w8 = (2 * Math.sqrt(Math.PI*this.seedCells.size()))/this.edgeCells.size();
        
        return w8;
    }
    
    double sumAll(){
        double sum = 0;
        sum = W1() + W2() + W3() + W4() + W5() + W6() + W7() + W8();
        //sum = W4() + W5();
        
        return sum;
    }
    
    //cells stored directly into slice, no seed segregation
    Seed(){
        seedCells = new ArrayList<>();
        edgeCells = new ArrayList<>();
    }
}
