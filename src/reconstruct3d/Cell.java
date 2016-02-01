package reconstruct3d;

import java.awt.Color;


public class Cell {
    int x;
    int y;
    int z;
    int id;
    Color color;
    boolean border = false;
    boolean bg = false;
    int seed_id = 0;    //przynależność do ziarna w 2D
    
    Cell(int x, int y, Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
    
    Cell(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
    Cell(int x, int y, int z, int id){
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
    }
    
    Cell(int x, int y, int z, Color color){
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }
    
    void setSeedId(int seed_id){
        this.seed_id = seed_id;
    }
}
