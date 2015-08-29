package reconstruct3d;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Interpolator {
    Slice[] edges;
    Slice[] slices;
    Slice[] returnSlices;
    Slice[] interpolatedStruct;
    PostSlice []managedSlice;
    int cubeSize = 100;
    int interStr = 0;
    Cell[][] temporaryMap;
    
    Interpolator(Slice[] edges, Slice[] slices){
        this.edges = edges;
        this.slices = slices;
    }
    
    double odlPkty(Cell c1, Cell c2){
        double odl = 0;
        
        odl = Math.sqrt((Math.pow(c2.x - c1.x, 2)) + (Math.pow(c2.y - c1.y, 2)));

        return odl;
    }
    
    Slice[] getSliceMap(int count){
        returnSlices = new Slice[count];
        
        for(int z = 0; z < count; z++){
            returnSlices[z] = new Slice();
            for(int i = 0; i < managedSlice[z].sliceSeeds.size(); i++){
                for(int j = 0; j < managedSlice[z].sliceSeeds.get(i).seedCells.size(); j++)
                    returnSlices[z].sliceCells.add(managedSlice[z].sliceSeeds.get(i).seedCells.get(j));

                for(int k = 0; k < managedSlice[z].sliceSeeds.get(i).edgeCells.size(); k++)
                    returnSlices[z].sliceCells.add(managedSlice[z].sliceSeeds.get(i).edgeCells.get(k));

            }
        }
        
        return returnSlices;
    }
    
    Slice[] getFinal(){
        return this.interpolatedStruct;
    }
    
    int getSize(){
        return this.interStr;
    }
    
    Seed manualCopy(Slice slices){
        Seed tmp = new Seed();
    
        for(int i = 0 ; i < slices.sliceCells.size(); i++)
            tmp.seedCells.add(slices.sliceCells.get(i));
    
        return tmp;
    }
    
    void manageData(int count){
        managedSlice = new PostSlice[count];
        boolean found = false;
        
        for(int z = 0; z < count; z++){
            managedSlice[z] = new PostSlice();
            
            //for(int j = 0; j < slices[z].numberofseeds(); j++)
                //managedSlice[z].sliceSeeds.add(j, new Seed());
            
            for(int id = 0; id < slices[z].numberofseeds()+1; id++){
                
                //for(int x = 0; x < slices[z].sliceCells.size(); x++){
                    //int id = 2;
                
                
                    Seed tmp = new Seed();
                    
                    //tmp = manualCopy(slices[z]);   //temporary add all
                    for(int i = 0; i < slices[z].sliceCells.size(); i++)
                        if(slices[z].sliceCells.get(i).id == id){
                            tmp.seedCells.add(slices[z].sliceCells.get(i));
                            found = true;
                        }
                    
//                Cell tmp = new Cell(x, y, Color.yellow);
                    if(found){
                        managedSlice[z].sliceSeeds.add(tmp);
                        found = false;
                    }//id++;
                //}
                    
            }
            //System.out.println("Iteracja: " + z);
        }
        
        for(int z = 0; z < count; z++)
            for(int i = 0; i < managedSlice[z].sliceSeeds.size(); i++)
                if(managedSlice[z].sliceSeeds.get(i).seedCells.size() < 1)
                    managedSlice[z].sliceSeeds.remove(i);
        
        detectSeedEdges(count);
        //printSlices(2, 5);
        sortEdges(count);
        //printAll(count);
    }
    
    void manageSeeds(int count){
    
        managedSlice = new PostSlice[count];
        
        for(int z = 0; z < count; z++){
            managedSlice[z] = new PostSlice();
            for(int j = 0; j < slices[z].numberofseeds(); j++)
                managedSlice[z].sliceSeeds.add(j, new Seed());
        }
        
        
        
        for(int z = 0; z < count; z++){
            for(int j = 0; j < slices[z].sliceCells.size(); j++)
                managedSlice[z].sliceSeeds.get(slices[z].sliceCells.get(j).id).seedCells.add(slices[z].sliceCells.get(j));
        
        }
        
        
        
    }
    
    Slice[] getSingle(){
        Slice []singleSeed = new Slice[2];
        
        singleSeed[0] = new Slice();
        singleSeed[1] = new Slice();
        singleSeed[0].sliceCells = managedSlice[0].sliceSeeds.get(0).seedCells;
        
        return singleSeed;
        
    }
    
    void clearMap(){
        for(int i = 0; i < cubeSize; i++)
            for(int j = 0; j < cubeSize; j++){
                temporaryMap[i][j] = new Cell(i, j, Color.yellow);   //!!!!!!!!!!!!!!!!!!!!!!!!!
                temporaryMap[i][j].id = -1;
            }
    }
    
    void initMap(){
        temporaryMap = new Cell[cubeSize][cubeSize];
        
        clearMap();
    }
    
    void detectSeedEdges(int count){
        
        for(int z = 0; z < count; z++){
            for(int g = 0; g < managedSlice[z].sliceSeeds.size(); g++){ //seed iteration
                
                initMap();
                
                for(int y = 0; y < managedSlice[z].sliceSeeds.get(g).seedCells.size(); y++) //cell iteration
                    temporaryMap[managedSlice[z].sliceSeeds.get(g).seedCells.get(y).x][managedSlice[z].sliceSeeds.get(g).seedCells.get(y).y] = managedSlice[z].sliceSeeds.get(g).seedCells.get(y);
            
                for(int x = 1; x < cubeSize-1; x++)
                    for(int y = 1; y < cubeSize-1; y++){
                        if(temporaryMap[x][y].id != -1){
                            if(temporaryMap[x-1][y].id == -1 || temporaryMap[x+1][y].id == -1 || temporaryMap[x][y-1].id == -1 || temporaryMap[x][y+1].id == -1)
                                managedSlice[z].sliceSeeds.get(g).edgeCells.add(temporaryMap[x][y]);
                    }
                
                }
                
                for(int x = 0; x < cubeSize; x++)
                    if(temporaryMap[x][0].id != -1)
                        managedSlice[z].sliceSeeds.get(g).edgeCells.add(temporaryMap[x][0]);
                
                for(int y = 0; y < cubeSize; y++)
                    if(temporaryMap[0][y].id != -1)
                        managedSlice[z].sliceSeeds.get(g).edgeCells.add(temporaryMap[0][y]);
            }
        }
    }
    
    void printWsp(){
        int count = 10;
        for(int z = 0; z < count; z++){
            System.out.println("Slice " + managedSlice[z].sliceSeeds.size() + " : " + z + ":");
            for(int i = 0; i < managedSlice[z].sliceSeeds.size(); i++)
                System.out.println(managedSlice[z].sliceSeeds.get(i).id() + "(" + managedSlice[z].sliceSeeds.get(i).seedCells.size() + ")" + " = " + managedSlice[z].sliceSeeds.get(i).W2());
        }
    
    }
    
    void printAll(int count){
        PrintWriter writer = null;
        
        //printWsp();
        for(int slice = 0; slice < count; slice++){
            for(int i = 0; i < managedSlice[slice].sliceSeeds.size(); i++){
                try {
                    writer = new PrintWriter("dump/debug/slice-" + slice + "seed-" + i + ".txt");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Interpolator.class.getName()).log(Level.SEVERE, null, ex);
                }

//                writer.println("W1: " + managedSlice[slice].sliceSeeds.get(i).W1());
//                writer.println("W2: " + managedSlice[slice].sliceSeeds.get(i).W2());
//                writer.println("W3: " + managedSlice[slice].sliceSeeds.get(i).W3());
//                writer.println("W4: " + managedSlice[slice].sliceSeeds.get(i).W4());
//                writer.println("W5: " + managedSlice[slice].sliceSeeds.get(i).W5());
//                writer.println("W6: " + managedSlice[slice].sliceSeeds.get(i).W6());
//                writer.println("W7: " + managedSlice[slice].sliceSeeds.get(i).W7());
//                writer.println("W8: " + managedSlice[slice].sliceSeeds.get(i).W8());

                writer.println("Edges");

                writer.println(managedSlice[slice].sliceSeeds.get(i).edgeCells.size());
                for(int j = 0; j < managedSlice[slice].sliceSeeds.get(i).edgeCells.size(); j++)
                    writer.println(managedSlice[slice].sliceSeeds.get(i).edgeCells.get(j).x + ":" + managedSlice[slice].sliceSeeds.get(i).edgeCells.get(j).y);

                writer.println("End Edges");
                for(int j = 0; j < managedSlice[slice].sliceSeeds.get(i).seedCells.size(); j++)
                    writer.println(managedSlice[slice].sliceSeeds.get(i).seedCells.size() + "-" + managedSlice[slice].sliceSeeds.get(i).seedCells.get(j).id + " => " + managedSlice[slice].sliceSeeds.get(i).seedCells.get(j).x + ":" + managedSlice[slice].sliceSeeds.get(i).seedCells.get(j).y);
                writer.close();
            }
        }
    }
    
    void sortEdges(int count){
        Sorter sort = new Sorter();
        
        for(int z = 0; z < count; z++){
            for(int i = 0; i < managedSlice[z].sliceSeeds.size(); i++){
                sort.sortSeed(managedSlice[z].sliceSeeds.get(i));  
            }
        }
    }
    
    void seedEdgePrep(int count, PostSlice P1, PostSlice P2){
        Seed S1;
        Seed S2 = new Seed();
        int discard = 0;
        
            for(int i = 0; i < P1.sliceSeeds.size(); i++){
                S1 = P1.sliceSeeds.get(i);
                
                for(int j = 0; j < P2.sliceSeeds.size(); j++)
                    if(P2.sliceSeeds.get(j).id() == S1.id())
                        S2 = P2.sliceSeeds.get(j);

                while(S1.edgeCells.size() != S2.edgeCells.size()){
                    if(S1.edgeCells.size() > S2.edgeCells.size()){
                        int mod = 0;
                        discard = S1.edgeCells.size() - S2.edgeCells.size();

                        if(discard != 0){
                            int usunMod = S1.edgeCells.size()/discard;

                            for(int u = 0; u < S1.edgeCells.size(); u++)
                                if((mod++)%usunMod == 0)
                                    S1.edgeCells.remove(u);
                        }

                    }
                    else{
                        int mod = 0;
                        discard = S2.edgeCells.size() - S1.edgeCells.size();
                        int u = 0;

                        if(discard != 0){
                            int usunMod = S2.edgeCells.size()/discard;

                            while(mod < S2.edgeCells.size())
                                if((mod++)%usunMod == 0){
                                    S2.edgeCells.remove(u);
                                    u++;
                                }else
                                    u++;
                        }
                    }
                }
            }
    }
    
    void initFinalStruct(int size){
        for(int i = 0; i < size; i++){
            interpolatedStruct[i] = new Slice();
            //interpolatedStruct[i].sliceCells = new ArrayList<>();
            for(int x = 0; x < cubeSize; x++)
                for(int y = 0; y < cubeSize; y++)
                    interpolatedStruct[i].sliceCells.add(new Cell(x, y, 0));
        }
    }
    
    void interpolate(int count){
        int interp = 5;
        interStr = count + (interp * (count-1));
        interpolatedStruct = new Slice[interStr];
        double step = 0;
        PostSlice S1;
        PostSlice S2;
        Seed Seed1;
        Seed Seed2 = new Seed();
        
        initFinalStruct(interStr);
        
        double wag1 = 0;
        double wag2 = 0;
        
        for(int z = 0; z < count; z++){
            for(int k = 0; k < managedSlice[z].sliceSeeds.size(); k++){
                //interpolatedStruct[z*(interp+1)] = new Slice();
                interpolatedStruct[z*(interp+1)].sliceCells.addAll(managedSlice[z].sliceSeeds.get(k).seedCells);
                interpolatedStruct[z*(interp+1)].sliceCells.addAll(managedSlice[z].sliceSeeds.get(k).edgeCells);
            }
        }
        
        for(int i = 0; i < count-1; i++){
            S1 = managedSlice[i];
            S2 = managedSlice[i+1];
            seedEdgePrep(count, S1, S2);
            
            step = 1.0/interp;
            //step = (double)Math.round((1.0/interp) * 100000) / 100000;
            //System.out.println(interp);
            for(int n = 0; n < interp; n++){
                //System.out.println(n);
                wag1 = 1 - ((double)n * step);
                wag2 = (double)n * step;
                //System.out.println(step);
                
                for(int edge = 0; edge < S1.sliceSeeds.size(); edge++){
                    Seed1 = S1.sliceSeeds.get(edge);
                    
                    for(int j = 0; j < S2.sliceSeeds.size(); j++)
                        if(S2.sliceSeeds.get(j).id() == Seed1.id())
                            Seed2 = S2.sliceSeeds.get(j);
                    
                    //System.out.println(Seed1.edgeCells.size());
                    
                    if(Seed1.edgeCells.size() > 0 && Seed2.edgeCells.size() > 0){
                        for(int finterp = 0; finterp < Seed1.edgeCells.size() && finterp < Seed2.edgeCells.size(); finterp++){
                            //System.out.println(finterp);
                            int x = (int)((Seed1.edgeCells.get(finterp).x * wag1) + (Seed2.edgeCells.get(finterp).x * wag2));
                            int y = (int)((Seed1.edgeCells.get(finterp).y * wag1) + (Seed2.edgeCells.get(finterp).y * wag2));
                            //System.out.println(wag1);

                            interpolatedStruct[i*interp+n+1+i].sliceCells.add(new Cell(x, y, Seed1.id()));
                        }
                    }
                    
                }
            }
            
            
        }
    }
    
    void interpolate2(int count){
        PostSlice P1;
        PostSlice P2;
        double match = 10000;
        double sum = match;
        int index = -1;
        boolean change = false;
        //int k = 100;
        double odleg = 2;
        
        for(int z = 0; z < count-1; z++){ //iteration for all slices
            
//            if(managedSlice[z].sliceSeeds.size() >= managedSlice[z+1].sliceSeeds.size()){
//                P1 = managedSlice[z+1];
//                P2 = managedSlice[z];
//            }
//            else{
//                P1 = managedSlice[z];
//                P2 = managedSlice[z+1];
//            }
            P1 = managedSlice[z];
            P2 = managedSlice[z+1];
            //match = 0;
            //sum = match;
            
            for(int first = 0; first < P1.sliceSeeds.size(); first++){  //Seeds
                match = 10000;
                //sum = match;
                index = -1;
                
                for(int second = 0; second < P2.sliceSeeds.size(); second++){

                    //System.out.println(odlPkty(P2.sliceSeeds.get(second).Center(), P1.sliceSeeds.get(first).Center()));
                    if(!P2.sliceSeeds.get(second).processed && odlPkty(P2.sliceSeeds.get(second).Center(), P1.sliceSeeds.get(first).Center()) < odleg){
                        sum = 0;
                        sum += Math.abs(P1.sliceSeeds.get(first).W1() - P2.sliceSeeds.get(second).W1());
                        sum += Math.abs(P1.sliceSeeds.get(first).W2() - P2.sliceSeeds.get(second).W2());
                        sum += Math.abs(P1.sliceSeeds.get(first).W3() - P2.sliceSeeds.get(second).W3());
                        sum += Math.abs(P1.sliceSeeds.get(first).W4() - P2.sliceSeeds.get(second).W4());
                        sum += Math.abs(P1.sliceSeeds.get(first).W5() - P2.sliceSeeds.get(second).W5());
                        sum += Math.abs(P1.sliceSeeds.get(first).W6() - P2.sliceSeeds.get(second).W6());
                        sum += Math.abs(P1.sliceSeeds.get(first).W7() - P2.sliceSeeds.get(second).W7());
                        sum += Math.abs(P1.sliceSeeds.get(first).W8() - P2.sliceSeeds.get(second).W8());
                        
                        if(sum < match){
                            match = sum;
                            index = second;
                            //System.out.println(index);
                            change = true;
                        }
                    }
                }

                if(change && !P2.sliceSeeds.get(index).processed){
                    P2.sliceSeeds.get(index).setId(P1.sliceSeeds.get(first).id());
                    P2.sliceSeeds.get(index).processed = true;
                    change = false;
                }
                
            }
        }
        
        
        
    }
    
}
