
package reconstruct3d;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JProgressBar;

public class ParCount implements Runnable{
    
    Interpolator inter;
    int index = 0;
    JProgressBar progress;
    //int cubeSize;
    TextureGen tex;
    Color []colorPalette;
    
    String dataPath;    //gdzie zapisać dane tekstowe
    String path;    //gdzie zapisać obrazy
    boolean isPrinting;
    
    int sizeX;
    int sizeY;
    


    public ParCount(Interpolator inter, int index, JProgressBar progress, TextureGen tex, String dataPath, boolean isPrinting, String path, int sizeX, int sizeY) {
        this.index = index;
        this.inter = inter;
        this.progress = progress;
        this.dataPath = dataPath;
        this.isPrinting = isPrinting;
        this.path = path;
        //this.cubeSize = cubeSize;
        //this.colorPalette = colorPalette;
        this.tex = tex;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        //this.tex.setColorPalette(colorPalette);
    }
    
    void fillBG(Slice interpolated, int z){
        for(int x = 0; x < sizeX; x++)
            for(int y = 0; y < sizeY; y++){
                Cell c = new Cell(x, y, z, 0);
                
                for(int k = 0; k < interpolated.sliceCells.size(); k++)
                    if(interpolated.sliceCells.get(k).x != c.x && interpolated.sliceCells.get(k).y != c.y)
                        interpolated.sliceCells.add(c);
            }
    }
    
    boolean isPresent(ArrayList<Cell> table, Cell row){
        boolean found = false;
    
        for(int i = 0; i < table.size(); i++)
            if(table.get(i).equals(row))
                found = true;
    
        return found;
    }

    @Override
    public void run() {
        Slice interpolated = new Slice();
        Seed inter_seed = new Seed();
        Sorter sort = new Sorter();
        int z_index = 0;
        //tex = new TextureGen();
        //tex.setColorPalette(colorPalette);
        
        double step = 0;
            PostSlice S1 = inter.managedSlice[index];
            PostSlice S2 = inter.managedSlice[index+1];
            
            Seed Seed1 = new Seed();
            Seed Seed2 = new Seed();
            
            double wag1 = 0;
            double wag2 = 0;
            //seedEdgePrep(count, S1, S2);
            
            step = 1.0/inter.interp;
            for(int n = 0; n < inter.interp; n++){
                interpolated = new Slice();
                wag1 = 1 - ((double)n * step);
                wag2 = (double)n * step;
                
                for(int edge = 0; edge < S1.sliceSeeds.size(); edge++){
                    //inter_seed = new Seed();
                    Seed1 = S1.sliceSeeds.get(edge);
                    
                    for(int j = 0; j < S2.sliceSeeds.size(); j++)
                        if(S2.sliceSeeds.get(j).id() == Seed1.id())
                            Seed2 = S2.sliceSeeds.get(j);
                    
                    //Sorter sort = new Sorter();
                    sort.checkDir(Seed1, Seed2);    //sprawdzenie kolejności sortowania
                    //inter_seed = new Seed();
                    
                    if(Seed1.edgeCells.size() < Seed2.edgeCells.size()){    //zamień ziarna by to z większą ilościa komórek było pierwsze
                        Seed tmp = new Seed();
                        
                        tmp = Seed1;
                        Seed1 = Seed2;
                        Seed2 = tmp;
                    }else{
                        int seedId = 0;
                        if(Seed1.edgeCells.size() > 0 && Seed2.edgeCells.size() > 0){
                            double wsp = Seed1.edgeCells.size() / Seed2.edgeCells.size();   //współczynnik stosunku rozmiarów
                            double incr = Math.floor(wsp);
                            int secondSeed = 0;
                            double indexer = 0;
                            inter_seed = new Seed();
                            
                            
                            for(int finterp = 0; finterp < Seed1.edgeCells.size(); finterp++){
                                
                                
                                if(indexer > 1){
                                    indexer = 0;
                                    if(secondSeed < Seed2.edgeCells.size()-1)
                                        secondSeed++;
                                }

                                int x = (int)((Seed1.edgeCells.get(finterp).x * wag1) + (Seed2.edgeCells.get(secondSeed).x * wag2));
                                int y = (int)((Seed1.edgeCells.get(finterp).y * wag1) + (Seed2.edgeCells.get(secondSeed).y * wag2));
                                
                                int z = index*inter.interp+n+1+index;
                                Cell newCell = new Cell(x, y, z, Seed1.id());

                                if(wag1 > wag2)
                                    seedId = Seed1.edgeCells.get(finterp).seed_id;
                                else
                                    seedId = Seed2.edgeCells.get(secondSeed).seed_id;
                                
                                newCell.setSeedId(seedId);
                                //if(!inter_seed.edgeCells.contains(newCell))
                                //if(!isPresent(interpolated.sliceCells, newCell))
                                inter_seed.edgeCells.add(newCell);
                                //inter_seed.setSeedId(seedId);
                                //interpolated.sliceCells.addAll(inter_seed.edgeCells);
    //                            synchronized (this){
    //                            inter.interpolatedStruct[index*inter.interp+n+1+index].sliceCells.add(new Cell(x, y, Seed1.id()));
    //                            }
                                indexer += incr;
                                //seedId++;
                            }
                            
                            
                            sort.sortSeed(inter_seed);
                            FillInterpolated filler = new FillInterpolated(inter_seed, sizeX, sizeY); //obiekt klasy filler, służy do zamknięcia krawędzi i wypełnienia ziaren
                            //filler.closeEdges();    //zamyka krawędzie ziaren
                            //filler.closeEdgesNH();
                            //interpolated.sliceCells.addAll(filler.detectConst(Seed1, Seed2).seedCells);
                            //filler.closeEdgesBresenham(inter_seed);
                            //filler.closeSeed();
                            //filler.Graham();
                            //if(inter_seed.edgeCells.size() > 3)
                            filler.convexHull();
                            //filler.connectAll();

                            interpolated.sliceCells.addAll(inter_seed.edgeCells);
                            inter_seed = new Seed();
                            //tex.generate(interpolated, index*inter.interp+n+1+index);
                        }
                    }
                    
                    //tex.generate(interpolated, index*inter.interp+n+1+index);
                }
                synchronized(tex){
                    tex.generate(interpolated, index*inter.interp+n+1+index, path);
                }
                //fillBG(interpolated, z_index);

            }
//            synchronized(this){
//                //notifyAll();
//                progress.setValue(progress.getValue()+1);
//            }

    }
    
}
