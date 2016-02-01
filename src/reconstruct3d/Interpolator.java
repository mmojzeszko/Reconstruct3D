package reconstruct3d;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


public class Interpolator {
    Slice[] slices;
    Slice[] returnSlices;
    Slice[] interpolatedStruct; //zinterpolowana struktura
    PostSlice []managedSlice;   //obiekt postSlice, struktura danych ze zidentyfikowanymi ziarnami i należącymi do nich komórkami
    int cubeSize_x = 170; //rozmiar przekroju (szerokość i wysokość)
    int cubeSize_y = 170;
    int physSize_x = 0;
    int physSize_y = 0;
    double mulSize_z = 0;
    int interStr = 0;   //rozmiar struktury po interpolacji
    Cell[][] temporaryMap;
    int interp = 5;
    int t_num = 10;
    
    JProgressBar progress;
    
    Interpolator(Slice[] slices){
        this.slices = slices;
    }
    
    void setSize(int cubeX, int cubeY){
        cubeSize_x = cubeX;
        cubeSize_y = cubeY;
    }
    
    void setPhysicalSize(int physSize_x, int physSize_y, double mulSize_z){
        this.physSize_x = physSize_x;
        this.physSize_y = physSize_y;
        this.mulSize_z = mulSize_z;
    }
    
    void setProgress(JProgressBar progress){
        this.progress = progress;
    }
    
    double odlPkty(Cell c1, Cell c2){   //odległość między dwoma komórkami
        double odl = 0;
        
        odl = Math.sqrt((Math.pow(c2.x - c1.x, 2)) + (Math.pow(c2.y - c1.y, 2)));

        return odl;
    }
    
    Slice[] getSliceMap(int count){ //zwraca obiekt typu slice[] będący tablicą list komórek
        returnSlices = new Slice[count];
        
        for(int z = 0; z < count; z++){
            returnSlices[z] = new Slice();
            for(int i = 0; i < managedSlice[z].sliceSeeds.size(); i++){
                for(int j = 0; j < managedSlice[z].sliceSeeds.get(i).seedCells.size(); j++) //dodaj komórki ziarna...
                    returnSlices[z].sliceCells.add(managedSlice[z].sliceSeeds.get(i).seedCells.get(j));

                for(int k = 0; k < managedSlice[z].sliceSeeds.get(i).edgeCells.size(); k++) //...oraz te na krawędziach
                    returnSlices[z].sliceCells.add(managedSlice[z].sliceSeeds.get(i).edgeCells.get(k));

            }
        }
        
        return returnSlices;
    }
    
    Slice[] getFinal(){ //zwraca zrekonstruowaną strukturę
        return this.interpolatedStruct;
    }
    
    int getSize(){
        return this.interStr;
    }
    
    void manageData(int count){ //wykrywa komórki należące do poszczególnych ziaren
        managedSlice = new PostSlice[count];
        boolean found = false;
        
        ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(t_num);
        
        for(int z = 0; z < count; z++)
            managedSlice[z] = new PostSlice();
        
        for(int z = 0; z < count; z++){       
            
            exec.execute(new ParInterSeed(this, z));

        }
        exec.shutdown();
        
        try {
            exec.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Logger.getLogger(Interpolator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Zakończono układanie ziaren");
        detectSeedEdges(count);
        System.out.println("Zakończono wykrywanie krawędzi");
        sortEdges(count);
        System.out.println("Zakończono sortowanie krawędzi");
    }
    
    void manageSeeds(int count){    //przepisuje odnalezione ziarna do nowej struktury, z rozróżnieniem ziaren
    
        managedSlice = new PostSlice[count];
        
        for(int z = 0; z < count; z++){
            System.out.println(z);
            managedSlice[z] = new PostSlice();
            for(int j = 0; j < slices[z].maxId(); j++)
                managedSlice[z].sliceSeeds.add(j, new Seed());
        }
        
        for(int z = 0; z < count; z++){
            for(int j = 0; j < slices[z].sliceCells.size(); j++)
                managedSlice[z].sliceSeeds.get(slices[z].sliceCells.get(j).id).seedCells.add(slices[z].sliceCells.get(j));
        
        }

    }
    
    void clearMap(){
        for(int i = 0; i < cubeSize_x; i++)
            for(int j = 0; j < cubeSize_y; j++){
                temporaryMap[i][j] = new Cell(i, j, Color.yellow);   //!!!!!!!!!!!!!!!!!!!!!!!!!
                temporaryMap[i][j].id = -1;
            }
    }
    
    void initMap(){
        temporaryMap = new Cell[cubeSize_x][cubeSize_y];
        
        clearMap();
    }
    
    void detectSeedEdges(int count){    //wykrywa krawędzie wykrytych ziaren
        
        for(int z = 0; z < count; z++){
            for(int g = 0; g < managedSlice[z].sliceSeeds.size(); g++){ //seed iteration
                
                initMap();
                
                for(int y = 0; y < managedSlice[z].sliceSeeds.get(g).seedCells.size(); y++) //cell iteration
                    temporaryMap[managedSlice[z].sliceSeeds.get(g).seedCells.get(y).x][managedSlice[z].sliceSeeds.get(g).seedCells.get(y).y] = managedSlice[z].sliceSeeds.get(g).seedCells.get(y);
            
                for(int x = 1; x < cubeSize_x-1; x++)
                    for(int y = 1; y < cubeSize_y-1; y++){
                        if(temporaryMap[x][y].id != -1 && !temporaryMap[x][y].color.equals(Color.white)){
                            if(temporaryMap[x-1][y].id == -1 || temporaryMap[x+1][y].id == -1 || temporaryMap[x][y-1].id == -1 || temporaryMap[x][y+1].id == -1)    //jeżeli jakakolwiek z sąsiednich komórek jest oznaczona jako nienależąca do ziarna to znaczy, że bieżąca komórka należy do krawędzi
                                managedSlice[z].sliceSeeds.get(g).edgeCells.add(temporaryMap[x][y]);
                    }
                
                }
                
                //sprawdzenie brzegów struktury
                for(int x = 0; x < cubeSize_x; x++)
                    if(temporaryMap[x][0].id != -1 && !temporaryMap[x][0].color.equals(Color.white))
                        managedSlice[z].sliceSeeds.get(g).edgeCells.add(temporaryMap[x][0]);
                
                for(int y = 0; y < cubeSize_y; y++)
                    if(temporaryMap[0][y].id != -1 && !temporaryMap[0][y].color.equals(Color.white))
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
    
    void sortEdges(int count){  //przy użyciu obiektu klasy Sorter, ustawia komórki krawędzi w kolejności
        Sorter sort = new Sorter();
        
        for(int z = 0; z < count; z++){
            for(int i = 0; i < managedSlice[z].sliceSeeds.size(); i++){
                sort.sortSeed(managedSlice[z].sliceSeeds.get(i));  
            }
        }
    }
    
    void seedEdgePrep(int count, PostSlice P1, PostSlice P2){   //usuwa nadmiarowe komórki z krawędzi gdy ich liczba się nie zgadza
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
            for(int x = 0; x < cubeSize_x; x++)
                for(int y = 0; y < cubeSize_y; y++)
                    interpolatedStruct[i].sliceCells.add(new Cell(x, y, 0));
        }
    }
    
    void interpolate(int count, int interp, String dataPath, boolean isPrinting, String path, int multiply){
        this.interp = interp;
        interStr = count + (interp * (count-1));
        //interpolatedStruct = new Slice[interStr];
        double step = 0;
        PostSlice S1 = null;
        PostSlice S2 = null;
        Seed Seed1;
        Seed Seed2 = new Seed();
        Sorter sort = new Sorter();
        
        TextureGen tex = new TextureGen();
        tex.initColorPalette();
        tex.clearOldFiles(path);
        tex.setPrintSize(cubeSize_x, cubeSize_y);
        
        //initFinalStruct(interStr);
        Slice interpolated = new Slice();
        
        double wag1 = 0;
        double wag2 = 0;
        
        for(int z = 0; z < count; z++){
            interpolated = new Slice();
            for(int k = 0; k < managedSlice[z].sliceSeeds.size(); k++){
                //interpolatedStruct[z*(interp+1)] = new Slice();
                //interpolatedStruct[z*(interp+1)].sliceCells.addAll(managedSlice[z].sliceSeeds.get(k).seedCells);
                //interpolatedStruct[z*(interp+1)].sliceCells.addAll(managedSlice[z].sliceSeeds.get(k).edgeCells);
                if(managedSlice[z].sliceSeeds.get(k) != null){
                    interpolated.sliceCells.addAll(managedSlice[z].sliceSeeds.get(k).seedCells);
                    interpolated.sliceCells.addAll(managedSlice[z].sliceSeeds.get(k).edgeCells);
                    tex.generate(interpolated, z*(interp+1), path);
                }
            }
        }
        
        for(int i = 0; i < count-1; i++){
            S1 = managedSlice[i];
            S2 = managedSlice[i+1];
            
            seedEdgePrep(count, S1, S2);
            
            for(int edge = 0; edge < S1.sliceSeeds.size(); edge++){
                    Seed1 = S1.sliceSeeds.get(edge);
                    
                for(int j = 0; j < S2.sliceSeeds.size(); j++)
                    if(S2.sliceSeeds.get(j).id() == Seed1.id())
                        Seed2 = S2.sliceSeeds.get(j);
                
                //sort.checkDir(Seed1, Seed2);    //sprawdzenie kierunku sortowania
            }
        }
        
        ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(t_num);
        
        for(int i = 0; i < count-1; i++){
            final int perc = i;
            
            exec.execute(new ParCount(this, i, progress, tex, dataPath, isPrinting, path, cubeSize_x, cubeSize_y));
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    progress.setValue(perc+1);
                }
            });
            try {
                java.lang.Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Interpolator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        exec.shutdown();
        try {
            //!!!
            exec.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Logger.getLogger(Interpolator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //tu
        //gen.saveWhole();
        DataAnalyzer analize = new DataAnalyzer(physSize_x, physSize_y, mulSize_z, interStr, count, path);
        analize.setMultiply(multiply);
        //analize.analyze();
    }
    
}
