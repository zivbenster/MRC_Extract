package meshi.energy.hydrogenBondsPairs;

import meshi.energy.hydrogenBond.HB_AtomAttribute;
import meshi.geometry.Distance;
import meshi.molecularElements.Atom;
import meshi.molecularElements.AtomList;

public class PairOfHydrogenBondsElements {

    //---------------------------------- data ------------------------------------------

    /*
     * The hydroegn residue number in HOelement1 <= the hydroegn residue number in HOelement2  
     */
    protected Distance HOelement1;
	protected Distance HOelement2;	
	
    /*
     * seqDistance means the gap between 2 atoms.
     */
    protected int seqDistance1,seqDistance2;
    protected int ooSeqDistance,hhSeqDistance,ohSeqDistance,hoSeqDistance;

    /*
     * any gap bigger then MAX_INER_SEQ_DISTANCE is treated as gap = MAX_INER_SEQ_DISTANCE
     * [we don't care the loop size in betta sheets for now]
     */
    public final int MAX_INER_SEQ_DISTANCE = 10;
    /*
     * we determind that 2 HBpairs are close by using MAX_SEQ_PAIRS_DISTANCE.
     */
    public final int MAX_SEQ_PAIRS_DISTANCE = 5;
    /*
     * determinded by creterya that can be found in the method:
     *    public  void set (Distance _HOelement1,Distance _HOelement2)
     * protected field from efficiency aspects     
     */
    protected boolean lookAtThisPair;	    
    private AtomList atoms;
    private static final int HB_PAIR_CAPACITY = 4; //should be 6 if we want also Nytrogens



    private double pairValue = 0;
    public void setPairValue(double pairValue) {
        this.pairValue = pairValue;
    }

    public final int seqDistance1(){return seqDistance1;}
    public final int seqDistance2(){return seqDistance2;}
    public final int hoSeqDistance(){return hoSeqDistance;}
    public final int hhSeqDistance(){return hhSeqDistance;}
    public final int ooSeqDistance(){return ooSeqDistance;}
    public final int ohSeqDistance(){return ohSeqDistance;}
	    
    public final Distance HOelement1(){return HOelement1;}
    public final Distance HOelement2(){return HOelement2;}
    public final AtomList atoms(){return atoms;}

    //data for the set method - global from efficiency aspects
	protected Atom oAtom1, hAtom1, oAtom2, hAtom2;

    public boolean isHelixPair() {
        return helixPair;
    }

    public boolean isBetaPair() {
        return betaPair;
    }

    public boolean isOneHelix() {
        return oneHelix;
    }

    public boolean isOneBetta() {
        return oneBetta;
    }

    private boolean helixPair,betaPair, oneHelix = false, oneBetta = false;


    //-------------------------------------- constructors ---------------------------------
    
    PairOfHydrogenBondsElements(){
        atoms = new AtomList(HB_PAIR_CAPACITY);
    }

    /*
     * copy constructor
     */
    PairOfHydrogenBondsElements(PairOfHydrogenBondsElements pair)
    {
        betaPair = pair.betaPair;
        helixPair = pair.helixPair;
        oneBetta =pair.oneBetta ;
        oneHelix =pair.oneHelix ;
        this.HOelement1 = pair.HOelement1;
        this.HOelement2 = pair.HOelement2;
                         	
	    this.seqDistance1 = pair.seqDistance1;
        this.seqDistance2 = pair.seqDistance2;            
            	    
	    this.ooSeqDistance = pair.ooSeqDistance;
        this.hhSeqDistance = pair.hhSeqDistance;
        this.ohSeqDistance = pair.ohSeqDistance;
        this.hoSeqDistance = pair.hoSeqDistance;

        this.lookAtThisPair = pair.lookAtThisPair;
        hAtom1 = pair.hAtom1;
        hAtom2 = pair.hAtom2;
        oAtom1 = pair.oAtom1;
        oAtom2 = pair.oAtom2;
        atoms = new AtomList(HB_PAIR_CAPACITY);
        if(lookAtThisPair){
            atoms.fastAdd(hAtom1);
            atoms.fastAdd(hAtom2);
            atoms.fastAdd(oAtom1);
            atoms.fastAdd(oAtom2);
        }
        else
            throw new RuntimeException("This is wird: copy of bad pair "+pair);
    }
	
    PairOfHydrogenBondsElements(Distance _HOelement1, Distance _HOelement2){                
        set(_HOelement1, _HOelement2);                     	        
    }

    //--------------------------------------------- methods ------------------------------------------
	    
    public  void set (Distance _HOelement1,Distance _HOelement2){
       if (_HOelement1 == null)
            System.out.println("PairOfHydrogenBondsElements: _HOelement1 == null");
        try{
            // all the inner vars should be out side ???

        
        int oAtom1ResidueNumber, hAtom1ResidueNumber, oAtom2ResidueNumber, hAtom2ResidueNumber;
        int realSeqDistance1,realSeqDistance2;
        int realOOSeqDistance,realHHSeqDistance,realOHSeqDistance,realHOSeqDistance;
        boolean secondaryStructure=true; //if both the residues are predicted to be with the same SS and in good positions (in helix) or true if we are in coil...

        if(hAtom(_HOelement1).residueNumber() <  hAtom(_HOelement2).residueNumber()){
            HOelement1 = _HOelement1;
            HOelement2 = _HOelement2;
        }
        else{
            HOelement1 = _HOelement2;
            HOelement2 = _HOelement1;
        }
        //atomPair1 = HOelement1.atomPair(); 
        //atomPair2 = HOelement2.atomPair(); 

        HB_AtomAttribute atom11Attribute =
            (HB_AtomAttribute) HOelement1.atom1().getAttribute(HB_AtomAttribute.key);
        if (atom11Attribute.isH) {
            hAtom1 = HOelement1.atom1();
            oAtom1 = HOelement1.atom2();
        }
        else {
            hAtom1 = HOelement1.atom2();
            oAtom1 = HOelement1.atom1();
        }

        HB_AtomAttribute atom21Attribute =
            (HB_AtomAttribute) HOelement2.atom1().getAttribute(HB_AtomAttribute.key);
        if (atom21Attribute.isH) {
            hAtom2 = HOelement2.atom1();
            oAtom2 = HOelement2.atom2();
        }
        else {
            hAtom2 = HOelement2.atom2();
            oAtom2 = HOelement2.atom1();
        }
          
    //    if(oAtom1 == oAtom2 || hAtom1 == hAtom2){
    //        lookAtThisPair =false;                //TODO check it: if this code is running then paterns like(11,11,1,0,-6,6) won't be punished and won't be reworded. !!
   //         return;
   //     }
        //  if(hAtom1 == hAtom2){                            //TODO
        //     lookAtThisPair =false;
		//      return;
        //  }
        hAtom1ResidueNumber = hAtom1.residueNumber();
        hAtom2ResidueNumber = hAtom2.residueNumber();	        
        oAtom1ResidueNumber = oAtom1.residueNumber();
        oAtom2ResidueNumber = oAtom2.residueNumber();
        realSeqDistance1 = oAtom1ResidueNumber - hAtom1ResidueNumber;
        realSeqDistance2 = oAtom2ResidueNumber - hAtom2ResidueNumber;

	        	        
        if(realSeqDistance1 > MAX_INER_SEQ_DISTANCE)
            seqDistance1 = MAX_INER_SEQ_DISTANCE+1;
        else
	        if(realSeqDistance1 < -1*MAX_INER_SEQ_DISTANCE)
	            seqDistance1 = -1*(MAX_INER_SEQ_DISTANCE+1);
            else 
                seqDistance1 = realSeqDistance1;
                
        if(realSeqDistance2 > MAX_INER_SEQ_DISTANCE)
            seqDistance2 = MAX_INER_SEQ_DISTANCE+1;
        else
	        if(realSeqDistance2 < -1*MAX_INER_SEQ_DISTANCE)
	            seqDistance2 = -1*(MAX_INER_SEQ_DISTANCE+1);
            else
                seqDistance2 = realSeqDistance2;
        if(seqDistance2 == -2)
            throw new RuntimeException("this is wird ! "+this );
        realHHSeqDistance = hAtom2ResidueNumber - hAtom1ResidueNumber; 
        realOOSeqDistance = oAtom2ResidueNumber - oAtom1ResidueNumber;
        realHOSeqDistance = hAtom2ResidueNumber - oAtom1ResidueNumber;
        realOHSeqDistance = oAtom2ResidueNumber - hAtom1ResidueNumber;
	        
        int counter =0;	        
        if(realOOSeqDistance > MAX_SEQ_PAIRS_DISTANCE){
            counter++;
            ooSeqDistance = MAX_SEQ_PAIRS_DISTANCE+1;
        }
        else
	        if(realOOSeqDistance < -1*MAX_SEQ_PAIRS_DISTANCE){
	            counter++;
	            ooSeqDistance = -1*(MAX_SEQ_PAIRS_DISTANCE+1);
	        }
            else
                ooSeqDistance = realOOSeqDistance;    
	        
        if(realHHSeqDistance > MAX_SEQ_PAIRS_DISTANCE){
            counter++;
            hhSeqDistance = MAX_SEQ_PAIRS_DISTANCE+1;
        }
        else
	        if(realHHSeqDistance < -1*MAX_SEQ_PAIRS_DISTANCE){
	            counter++;
	            hhSeqDistance = -1*(MAX_SEQ_PAIRS_DISTANCE+1);
	        }
            else
                hhSeqDistance = realHHSeqDistance;
                
        if(realOHSeqDistance > MAX_SEQ_PAIRS_DISTANCE){
            counter++;
            ohSeqDistance = MAX_SEQ_PAIRS_DISTANCE+1;
        }
        else
	        if(realOHSeqDistance < -1*MAX_SEQ_PAIRS_DISTANCE){
	            counter++;
	            ohSeqDistance = -1*(MAX_SEQ_PAIRS_DISTANCE+1);
	        }
            else
                ohSeqDistance = realOHSeqDistance;
                
        if(realHOSeqDistance > MAX_SEQ_PAIRS_DISTANCE+1){
            counter++;
            hoSeqDistance = MAX_SEQ_PAIRS_DISTANCE+1;
        }
        else
	        if(realHOSeqDistance < -1*(MAX_SEQ_PAIRS_DISTANCE+1)){
	            counter++;
	            hoSeqDistance = -1*(MAX_SEQ_PAIRS_DISTANCE+1);
	        }        
            else
                hoSeqDistance = realHOSeqDistance;
                
        if (counter >  2) {lookAtThisPair = false;            ///if 3 or 4 out of hh,oo,ho,oh are too large then the 2 pairs are not near each other pn the sequence.
            return;}
                
        boolean structure1, structure2;
        structure1 = helixPair(HOelement1);
        structure2 = helixPair(HOelement2);                
        if (structure1 && structure2) helixPair = true;
        else {
            helixPair = false;
            oneHelix = structure1 || structure2;
        }
                
        structure1 = sheetPair(HOelement1);
        structure2 = sheetPair(HOelement2);
        if (structure1 && structure2)
                betaPair = true;
        else {
            betaPair = false;
            oneBetta = structure1 || structure2;
        }

        secondaryStructure = !(oneHelix && oneBetta);//false means that it's Helix-Sheet pairs
	            
        if(helixPair )
            lookAtThisPair = (realSeqDistance1 == -4 && realSeqDistance2 == -4
                              && counter <= 1);
        else
            //lookAtThisPair = betaPair;
            lookAtThisPair = secondaryStructure;
                
        if(lookAtThisPair) {
            //System.out.println("PairOf....:in adding atoms");
            ////            nAtom1 = hAtom1.residue().amideN();
            ////            nAtom2 = hAtom2.residue().amideN();
            atoms = new AtomList(HB_PAIR_CAPACITY);
	    	atoms.fastAdd(hAtom1);
	    	atoms.fastAdd(hAtom2);
	    	atoms.fastAdd(oAtom1);
	    	atoms.fastAdd(oAtom2);
            ////atoms.fastAdd(nAtom1);
	    	////atoms.fastAdd(nAtom2);
          }
        }
         catch(NullPointerException e){
                  System .out.println();
        }
    }
	    
	    
    public Atom hAtom(Distance atomPair){

        HB_AtomAttribute atom1Attribute =
            (HB_AtomAttribute) atomPair.atom1().getAttribute(HB_AtomAttribute.key);
             HB_AtomAttribute atom2Attribute =
            (HB_AtomAttribute) atomPair.atom2().getAttribute(HB_AtomAttribute.key);
        if (atom1Attribute.isH)
            return atomPair.atom1();
        else if (atom2Attribute.isH)
            return atomPair.atom2();
        else throw new RuntimeException("This pair does not has hydrogen "+atomPair);
    }
	    
    public Atom oAtom(Distance atomPair){
        HB_AtomAttribute atom1Attribute =
            (HB_AtomAttribute) atomPair.atom1().getAttribute(HB_AtomAttribute.key);
        HB_AtomAttribute atom2Attribute =
            (HB_AtomAttribute) atomPair.atom2().getAttribute(HB_AtomAttribute.key);
        if (atom1Attribute.isO)
            return atomPair.atom1();
        else if (atom2Attribute.isO)
            return atomPair.atom2();
        else throw new RuntimeException("This pair does not has oxygen "+atomPair);
    }
	    
    public boolean helixPair(Distance atomPair){
        String atom1SS = atomPair.atom1().residue().secondaryStructure();
        String atom2SS = atomPair.atom2().residue().secondaryStructure();
        return(atom1SS.equals("HELIX") || atom1SS .equals("HELIX_OR_COIL")) && (atom2SS.equals("HELIX") | atom2SS .equals("HELIX_OR_COIL") );
    }

    public boolean sheetPair(Distance atomPair){
        String atom1SS = atomPair.atom1().residue().secondaryStructure();
        String atom2SS = atomPair.atom1().residue().secondaryStructure();
        return (atom1SS.equals("SHEET") || atom1SS .equals("SHEET_OR_COIL")) && (atom2SS.equals("SHEET") | atom2SS .equals("SHEET_OR_COIL"));
    }
	    
    public boolean helixSheetPair(Distance atomPair){
        String atom1SS = atomPair.atom1().residue().secondaryStructure();
        String atom2SS = atomPair.atom1().residue().secondaryStructure();
        return ((atom1SS.equals("HELIX") || atom1SS .equals("HELIX_OR_COIL")) && (atom2SS.equals("SHEET")  | atom2SS .equals("SHEET_OR_COIL"))) ||
                ((atom1SS.equals("SHEET") || atom1SS .equals("SHEET_OR_COIL")) && (atom2SS.equals("HELIX")  | atom2SS .equals("HELIX_OR_COIL")));
    }
	    
    public String toString(){
        return "PairOfHydrogenBondsElements: "+hAtom1.residueNumber()+" "+oAtom1.residueNumber()+
            " "+hAtom2.residueNumber()+" "+oAtom2.residueNumber()+" ( "+seqDistance1+","+seqDistance2+
            ","+hhSeqDistance+","+ooSeqDistance+","+hoSeqDistance+","+ohSeqDistance+" )"+":"+helixPair +","+betaPair+", value: "+pairValue;
        //                return "";
    }
}
