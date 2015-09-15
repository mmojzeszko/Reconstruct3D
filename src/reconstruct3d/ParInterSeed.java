
package reconstruct3d;

public class ParInterSeed implements Runnable{

    //Slice []slices;
    //PostSlice []managedSlice;
    boolean found = false;
    int z;
    Interpolator inter;
    
    public ParInterSeed(Interpolator inter, int z) {
        this.inter = inter;
        this.z = z;
    }

    @Override
    public void run() {
        for(int id = 0; id < inter.slices[z].numberofseeds()+1; id++){

                    Seed tmp = new Seed();

                    for(int i = 0; i < inter.slices[z].sliceCells.size(); i++)
                        if(inter.slices[z].sliceCells.get(i).id == id){   //jeżeli ma szukane id
                            tmp.seedCells.add(inter.slices[z].sliceCells.get(i));
                            found = true;
                        }

                    if(found){
                        synchronized(this){
                        inter.managedSlice[z].sliceSeeds.add(tmp);    //dodaj komórkę do ziarna
                        }
                        found = false;
                        
                    }
                    
            }
    }
    
    
}
