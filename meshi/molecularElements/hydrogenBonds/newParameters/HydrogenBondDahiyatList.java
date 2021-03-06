package meshi.molecularElements.hydrogenBonds.newParameters;

import meshi.energy.excludedVolumeImprovedDistance.EVenergyParameters;
import meshi.energy.excludedVolumeImprovedDistance.EVenergyParametersList;
import meshi.geometry.DistanceMatrix;
import meshi.molecularElements.Atom;
import meshi.molecularElements.AtomList;
import meshi.molecularElements.hydrogenBonds.AbstractHydrogenBond;
import meshi.molecularElements.hydrogenBonds.AbstractHydrogenBondList;
import meshi.parameters.AtomTypes;

/** 
 * This class describe a list of hydrogen bonds in a protein. The hydrogen bonds are described as in:
 * Dahiyat et al. Protein Sci. 1996 May;5(5):895-903. See the "DahiyatHydrogenBond" class for a much more
 * detailed description.
 * 
 * The constructor requires a parameter that points to an instance of "EVenergyParametersList". From this instance 
 * the distance dependence of the hydrogen bonds are extracted. 
 * 
 * The parameter '30' determines the refresh rate of the HB vector. Once every this number of updates, broken hydrogen 
 * bonds that are no longer in the non-bonded-list are removed from the vector. This number should be >>1 so that 
 * the refresh does not delay the updates too much, but 30 was just a thumb figure.   	
 *
 **/

public class HydrogenBondDahiyatList extends AbstractHydrogenBondList implements AtomTypes {
	
	/**
	 * This array stores pointers to the base atoms of every non-hydrogen polar atom in the protein. 
	 * The indexation is through the atom number (field)   
	 **/
    protected Atom[] baseAtom; 
	/**
	 * Since currently the hydrogen bond list is only used by the solvate energy, we use the parameters 
	 * for the hydrogen bond from this class. Later, this can be changed.   
	 **/
    protected EVenergyParametersList parameters;
    
    /**
     * In this class are the distance and angle parameters for this kind of HB
     **/
    protected DahiyatImplementationConstants angleParameters = new DahiyatHighAccuracyAngleParamaters();


    public HydrogenBondDahiyatList() {
    	throw new RuntimeException("\nERROR: without parameters the hydrogen bonds cannot be formed.\n");    	
    }

    public HydrogenBondDahiyatList(DistanceMatrix dm, AtomList atomList,EVenergyParametersList parameters) {
    	super(dm, atomList, 30 /* DEFAULT_REFRESH_VECTOR_RATE */);
    	this.parameters = parameters;
    	try {
    		update(false,0);
    	}
    	catch (Exception e) {
    		System.out.print("\nAn error occur while creating the Dahiyat hydrogen bond list.\n\n");
    		e.printStackTrace();
    		System.out.print("\n\n");
    		throw new RuntimeException("");
    	}
    }

    
		
    /**
      * This method updates the pointers in the baseAtom array. For non-polar atoms they should remain null.
      * For any polar atom (O,N or S in Cys) we calculate the base atom that participate in the definition of 
      * the hydrogen bond angle. This base atom is the attached hydrogen (if present), or the heavy atom to which 
      * the polar atom is attached (when the hydrogen is not present).      
      * 
      **/
    protected void buildSpecificStructures() {
		baseAtom = new Atom[maxAtomNum+1];
		Atom atom1,atom2;
		for (int c1=0 ; c1<atomList.size() ; c1++) {
			atom1 = atomList.atomAt(c1);
			for (int c2=0 ; c2<atom1.bonded().size() ; c2++) {
				atom2 = atom1.bonded().atomAt(c2);
				// Treating OXYGEN atoms. 
				// This is easy because the oxygens in proteins 
				// are always tied to one atom only.
				if (atom1.isOxygen)
					baseAtom[lut[atom1.number()]] = atom2;
				// Treating NITROGEN atoms.
				// First, the case of amides in glutamines and asparagines
				if (!atom2.isHydrogen && ((atom1.type==NND) || (atom1.type==QNE)))
					baseAtom[lut[atom1.number()]] = atom2;
				// Second, the case of amides without explicit H attached
				if ((atom1.type==KNZ) || (atom1.type==RNH) || (atom1.type==TRN))
					baseAtom[lut[atom1.number()]] = atom2;
				// Third , regular H attached
				if (atom2.isHydrogen && ((atom1.type==HND) || (atom1.type==HNE) || 
				    (atom1.type==RNE) || (atom1.type==WNE) ||
					(atom1.type==AN) ||
					(atom1.type==CN) ||
					(atom1.type==DN) ||
					(atom1.type==EN) ||
					(atom1.type==FN) ||
					(atom1.type==GN) ||
					(atom1.type==HN) ||
					(atom1.type==IN) ||
					(atom1.type==KN) ||
					(atom1.type==LN) ||
					(atom1.type==MN) ||
					(atom1.type==NN) ||
					(atom1.type==QN) ||
					(atom1.type==RN) ||
					(atom1.type==SN) ||
					(atom1.type==TN) ||
					(atom1.type==VN) ||
					(atom1.type==WN) ||
					(atom1.type==YN)))
					baseAtom[lut[atom1.number()]] = atom2;
				// Treating the SULFUR atoms of Cystines.
				if (atom1.type==CSG)
					baseAtom[lut[atom1.number()]] = atom2;
			}
		}
	}

	/**
	 * Creating a Dahiyat-like hydrogen-bond.
	 **/
    protected AbstractHydrogenBond createHBfromPolars(Atom atom1,Atom atom2) {
    	EVenergyParameters parameter = parameters.parameters(atom1, atom2);
		if (!parameter.hb)
			return null;

    	AtomList tmpList = new AtomList();
    	tmpList.add(atom1);
    	tmpList.add(atom2);
    	tmpList.add(baseAtom[lut[atom1.number()]]);
    	tmpList.add(baseAtom[lut[atom2.number()]]);
    	
    	return new HydrogenBondDahiyat(dm, tmpList,	 parameter.sigma ,  parameter.hbEnd ,  
    			parameter.hbEnd + angleParameters.continueAfterSigmoid(),  angleParameters.valAtp1(),  angleParameters.valAtp2(), angleParameters);

    }


}


  
