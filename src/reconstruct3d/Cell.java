package reconstruct3d;

import java.awt.Color;


public class Cell {
    int x;
    int y;
    int z;
    int id;
    Color color;
    boolean border = false;
    
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
    
    Cell(int x, int y, int z, Color color){
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }
}
