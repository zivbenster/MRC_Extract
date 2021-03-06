package meshi.molecularElements.residuesExtendedAtoms;
import meshi.molecularElements.AtomList;
/**
 *<pre>
 *              O
 *              |
 *     N - CA - C...n
 *     |
 *     H
 **/
public class Gly extends ResidueExtendedAtoms {
    public static final String COMMENT = "From Levitt, JMB 168:592 (1983) table 2.\n"+
	"             O\n"+
	"             |\n"+
	"    N - CA - C...n\n"+
	"    |\n"+
	"    H\n";
    public Gly(int number, int mode, double x, double y, double z) {
	this(number, getList(x, y, z), mode, ADD_ATOMS);
    }
    public Gly(int number, AtomList atomList, int mode, int addAtomsFlag) {
	super(GLY, atomList, number, mode,  addAtomsFlag);
    }
    public Gly(Gly old,int number){
         this(number,old.atoms(),old.getMode(),old.getAddAtomsFlag());
	 setSecondaryStructure(old.secondaryStructure());
	       setAccessibility(old.accessibility());
    }
    public String comment() {
	return COMMENT;
    }
}

