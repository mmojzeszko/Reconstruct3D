
package reconstruct3d;

import javax.swing.JProgressBar;

public class ParCount implements Runnable{
    
    Interpolator inter;
    int index = 0;
    JProgressBar progress;

    public ParCount(Interpolator inter, int index, JProgressBar progress) {
        this.index = index;
        this.inter = inter;
        this.progress = progress;
    }

    @Override
    public void run() {
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
                wag1 = 1 - ((double)n * step);
                wag2 = (double)n * step;
                
                for(int edge = 0; edge < S1.sliceSeeds.size(); edge++){
                    Seed1 = S1.sliceSeeds.get(edge);
                    
                    for(int j = 0; j < S2.sliceSeeds.size(); j++)
                        if(S2.sliceSeeds.get(j).id() == Seed1.id())
                            Seed2 = S2.sliceSeeds.get(j);
                    
                    Sorter sort = new Sorter();
                    //sort.checkDir(Seed1, Seed2);
                    
                    if(Seed1.edgeCells.size() > 0 && Seed2.edgeCells.size() > 0){
                        for(int finterp = 0; finterp < Seed1.edgeCells.size() && finterp < Seed2.edgeCells.size(); finterp++){
                            int x = (int)((Seed1.edgeCells.get(finterp).x * wag1) + (Seed2.edgeCells.get(finterp).x * wag2));
                            int y = (int)((Seed1.edgeCells.get(finterp).y * wag1) + (Seed2.edgeCells.get(finterp).y * wag2));
                            synchronized (this){
                            inter.interpolatedStruct[index*inter.interp+n+1+index].sliceCells.add(new Cell(x, y, Seed1.id()));
                            }
                        }
                    }
                    
                }
            }
//            synchronized(this){
//                //notifyAll();
//                progress.setValue(progress.getValue()+1);
//            }

    }
    
}
