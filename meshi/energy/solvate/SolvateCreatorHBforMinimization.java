package meshi.energy.solvate;
import meshi.energy.AbstractEnergy;
import meshi.energy.EnergyCreator;
import meshi.geometry.DistanceMatrix;
import meshi.molecularElements.Protein;
import meshi.molecularElements.hydrogenBonds.HydrogenBondDahiyatList;
import meshi.util.CommandList;
import meshi.util.KeyWords;

/**
 * The different between this creator and the "SolvateCreatorRegularHB" (see the latter main comment for a lot of details), is in the 
 * way the hydrogen-bond distance sigmoid is set. Here parameters are chosen so that the hydrogen-bond starts its drop to 0 at a 
 * donor-acceptor distance of 2.85 Ang, and is ~0 only for donor-acceptor distance of about 3.6 Ang. This is good to increase the basin of 
 * attraction of direct minimizations protocols.   
 **/

public class SolvateCreatorHBforMinimization extends EnergyCreator  implements KeyWords {

	// The different weights relevent to this term.
    private double weightSCPolarSolvate=1.0;
    private double weightSCCarbonSolvate=1.0;
    private double weightBBPolarSolvate=1.0;
    private double weightHB=1.0;

    public SolvateCreatorHBforMinimization(double weightSCPolarSolvate, double weightSCCarbonSolvate, 
    					double weightBBPolarSolvate, double weightHB) {
		super(1.0);
		this.weightSCPolarSolvate = weightSCPolarSolvate;
		this.weightSCCarbonSolvate = weightSCCarbonSolvate;
		this.weightBBPolarSolvate = weightBBPolarSolvate;
		this.weightHB = weightHB;
    }
   
    public SolvateCreatorHBforMinimization(double weight) {
		super(weight);
		this.weightSCPolarSolvate = weight;
		this.weightSCCarbonSolvate = weight;
		this.weightBBPolarSolvate = weight;
		this.weightHB = weight;
    }

    public SolvateCreatorHBforMinimization() {
		super(SOLVATE_ENERGY);
    }

    public AbstractEnergy createEnergyTerm(Protein protein, DistanceMatrix distanceMatrix, 
					  CommandList commands) {
		if (parametersList== null) {
	    	// Appanding the path to the list of parameter filenames.
	    	String[] strlist = new String[SOLVATE_MINIMIZE_HB_PARAMETERS.length];
	    	String pathname = parametersDirectory(commands).concat("/");
	    	for (int cc=0 ; cc<SOLVATE_MINIMIZE_HB_PARAMETERS.length ; cc++)
	        	strlist[cc] = pathname.concat(SOLVATE_MINIMIZE_HB_PARAMETERS[cc]);
	    	parametersList = new SolvateParametersList(strlist);
	 	}

		return new SolvateEnergy(protein.atoms(), 
					distanceMatrix, 
					(SolvateParametersList) parametersList,
					new HydrogenBondDahiyatList(distanceMatrix, protein.atoms(), (SolvateParametersList) parametersList),
				    weightSCPolarSolvate,
				    weightBBPolarSolvate,
				    weightSCCarbonSolvate,
				    weightHB);
    }

}
