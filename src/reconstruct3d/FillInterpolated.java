
package reconstruct3d;

import java.util.ArrayList;


public class FillInterpolated {

    Seed seed;
    Cell[] tmpPoint;
    double[] angle;
    ArrayList<Cell> sortedPoints;
    int sizeX;
    int sizeY;
    
    Cell[][] tableOfCells;
    
    boolean isPresent(ArrayList<Cell> table, Cell row){
        boolean found = false;
    
        for(int i = 0; i < table.size(); i++)
            if(table.get(i).equals(row))
                found = true;
    
        return found;
    }
    
    FillInterpolated(Seed seed, int sizeX, int sizeY) {
        this.seed = seed;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }
    
    void closeEdgesBresenham(Seed sid){
        int size = sid.edgeCells.size()-1;
        //Sorter sort = new Sorter();
        
        for(int i = 0; i < size; i++){
            //System.out.println(i);
            int x0 = sid.edgeCells.get(i).x;
            int y0 = sid.edgeCells.get(i).y;
            int x1 = sid.edgeCells.get(i+1).x;
            int y1 = sid.edgeCells.get(i+1).y;
            
            //if(sort.odlPkty(sid.edgeCells.get(i), sid.edgeCells.get(i+1)) < 5)
            //System.out.println(sid.edgeCells.get(i).seed_id + "/" + sid.edgeCells.get(i+1).seed_id);
            //if(sid.edgeCells.get(i).seed_id == sid.edgeCells.get(i+1).seed_id)
                drawLine(x0, y0, x1, y1);
        
        }
        int x0 = sid.edgeCells.get(0).x;
        int y0 = sid.edgeCells.get(0).y;
        int x1 = sid.edgeCells.get(size).x;
        int y1 = sid.edgeCells.get(size).y;
        //if(sid.edgeCells.get(0).seed_id == sid.edgeCells.get(size).seed_id)
            drawLine(x0, y0, x1, y1);
        
    }
    
    void closeEdgesBresenham(ArrayList<Cell> sid){
        int size = sid.size()-1;
        //Sorter sort = new Sorter();
        
        for(int i = 0; i < size; i++){
            //System.out.println(i);
            int x0 = sid.get(i).x;
            int y0 = sid.get(i).y;
            int x1 = sid.get(i+1).x;
            int y1 = sid.get(i+1).y;
            
            //if(sort.odlPkty(sid.edgeCells.get(i), sid.edgeCells.get(i+1)) < 5)
            //System.out.println(sid.edgeCells.get(i).seed_id + "/" + sid.edgeCells.get(i+1).seed_id);
            //if(sid.get(i).seed_id == sid.get(i+1).seed_id)
                drawLine(x0, y0, x1, y1);
        
        }
//        int x0 = sid.get(0).x;
//        int y0 = sid.get(0).y;
//        int x1 = sid.get(size).x;
//        int y1 = sid.get(size).y;
//        //if(sid.get(0).seed_id == sid.get(size).seed_id)
//            drawLine(x0, y0, x1, y1);
        
    }
    
    void closeAllToAll(ArrayList<Cell> sid){
        int size = sid.size()-1;
        //Sorter sort = new Sorter();
        
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                
                int x0 = sid.get(i).x;
                int y0 = sid.get(i).y;
                int x1 = sid.get(j+1).x;
                int y1 = sid.get(j+1).y;
                if(sid.get(i).seed_id == sid.get(j+1).seed_id)
                    drawLine(x0, y0, x1, y1);
            }
        
        }
        
    }
    
    void drawLine(int x,int y,int x2, int y2){
        int w = x2 - x ;
        int h = y2 - y ;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
        int longest = Math.abs(w) ;
        int shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;            
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {
            //putpixel(x,y,color) ;
            Cell tmp = new Cell(x, y, seed.edgeCells.get(i).z, seed.edgeCells.get(i).id);
            if(!seed.edgeCells.contains(tmp))
                seed.edgeCells.add(tmp);
            numerator += shortest ;
            if (!(numerator<longest)) {
                numerator -= longest ;
                x += dx1 ;
                y += dy1 ;
            } else {
                x += dx2 ;
                y += dy2 ;
            }
        }
    }
    
    Seed getSeed(){
        return this.seed;
    }
    
    void convertToTable(ArrayList<Cell> cells){
        tableOfCells = new Cell[sizeX][sizeY];
        
        for(int y = 0; y < sizeY; y++)
            for(int x = 0; x < sizeX; x++){
                tableOfCells[x][y] = new Cell(x, y, cells.get(0).z, 0);
                tableOfCells[x][y].border = true;
            }
        
        for(int i = 0 ; i < cells.size(); i++)
            tableOfCells[cells.get(i).x][cells.get(i).y] = cells.get(i);
    }
    
    void fill(ArrayList<Cell> cells){
        convertToTable(cells);
        
        boolean onOff = false;
        ArrayList<Cell> detected = new ArrayList<>();
        
        int minX = 0;
        int maxX = 0;
        boolean foundMin = false;
        boolean foundMax = false;
        
        for(int y = 0; y < sizeY; y++){
            foundMin = false;
            minX = 0;
            maxX = 0;
            for(int x = 0; x < sizeX; x++){
                if(tableOfCells[x][y].id != 0 && !foundMin){
                    minX = x;
                    foundMin = true;
                }
                
                if(tableOfCells[x][y].id != 0)
                    maxX = x;
                
//                if(onOff)
//                    detected.add(new Cell(x, y, cells.get(0).z, cells.get(2).id));
//                
//                if(tableOfCells[x][y].id != 0 && !onOff)
//                    onOff = true;
//                else if(tableOfCells[x][y].id == 0 && onOff)
//                    onOff = true;
//                else if(tableOfCells[x][y].id != 0 && onOff)
//                    onOff = false;
            }
            if(maxX != 0 && minX != 0)
                for(int d = minX; d < maxX; d++){
                    Cell det = new Cell(d, y, cells.get(0).z, cells.get(2).id);
                    if(!isPresent(detected, det))
                        detected.add(det);
                }
        }
        //System.out.println(detected.size());
        seed.edgeCells.addAll(detected);
    }
    
    void convexHull(){
        ArrayList<Integer> ids = new ArrayList<>();

        
        //GrahamScan grah = new GrahamScan();
        ids = seed.getSeedIds();
        //System.out.println(ids.size());
        
        for(Integer id : ids){
            if(seed.getById(id).size() > 3){
            //System.out.println(seed.getById(id).size());
                closeEdgesBresenham(GrahamScan.getConvexHull(seed.getById(id)));
                
                //fill(seed.getById(id));
            }
        }
        
        //fill(seed);
        fill(seed.edgeCells);    //good
        
    }
    

    
}
