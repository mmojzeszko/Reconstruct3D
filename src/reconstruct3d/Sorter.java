
package reconstruct3d;


public class Sorter {
    Slice []slice;
    Slice []sorted;
    int CubeSize;
    
    void setSlices(Slice []slice){
        this.slice = slice;
    }
    
    void setCubeSize(int CubeSize){
        this.CubeSize = CubeSize;
    }
    
    Slice[] getSlices(){
        return this.slice;
    }
    
    double odlPkty(Cell c1, Cell c2){
        double odl = 0;
        
        odl = Math.sqrt((Math.pow(c2.x - c1.x, 2)) + (Math.pow(c2.y - c1.y, 2)));

        return odl;
    }
    
    void sortSeed(Seed sid){
        double min = 1000;
        int iSearch = 0;
        Cell temp;
        
        for(int i = 0 ; i < sid.edgeCells.size()-1; i++){
            min = 1000;
            
            for(int j = 1+i; j < sid.edgeCells.size(); j++){
                if(odlPkty(sid.edgeCells.get(i), sid.edgeCells.get(j)) < min){
                    iSearch = j;
                    min = odlPkty(sid.edgeCells.get(i), sid.edgeCells.get(j));
                    //System.out.println(min);
                }
                
            }      
            temp = sid.edgeCells.get(i+1);
            sid.edgeCells.set(i+1, sid.edgeCells.get(iSearch));
            sid.edgeCells.set(iSearch, temp);
        }
            
    }
    
    void processEdges(){

        
        
    }
    
}
