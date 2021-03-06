package meshi.energy.compositeTorsions.ramachandranSidechain;

import meshi.energy.AbstractEnergy;
import meshi.energy.EnergyCreator;
import meshi.energy.compositeTorsions.ResidueTorsionsList;
import meshi.geometry.DistanceMatrix;
import meshi.molecularElements.Protein;
import meshi.parameters.MeshiPotential;
import meshi.util.CommandList;
import meshi.util.KeyWords;

public class RamachandranSidechainEnergyCreator
	extends EnergyCreator
	implements KeyWords, MeshiPotential {

	public RamachandranSidechainEnergyCreator(double weight) {
	super(weight);
    }
    
    public RamachandranSidechainEnergyCreator() {
		super( 1.0 );
	}
	
	public AbstractEnergy createEnergyTerm(Protein protein,
			DistanceMatrix distanceMatrix, CommandList commands) {
		/* retrieve parameters */
		if (parametersList == null) {
			String rsplFileName = 
				parametersDirectory(commands)+"/"+COMPOSITE_TORSIONS_PARAMETERS;
			parametersList = new RamachandranSidechainParametersList(rsplFileName);
		}
		
		/* create residue torsions list for protein */
		ResidueTorsionsList rtl = (new ResidueTorsionsList(protein, distanceMatrix )).filterCompleteResidues();		
		
		/* return energy */
		return new RamachandranSidechainEnergy(
				rtl, distanceMatrix, (RamachandranSidechainParametersList) parametersList, weight(), "ramSide" );
	}

}
