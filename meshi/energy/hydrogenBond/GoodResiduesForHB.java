package meshi.energy.hydrogenBond;


import java.util.Iterator;

import meshi.geometry.Distance;
import meshi.geometry.DistanceList;
import meshi.molecularElements.Atom;
import meshi.util.filters.Filter;

public class GoodResiduesForHB implements Filter{
    private IsHO isHO = new IsHO();
    private IsNot13 isNot13 = new IsNot13();
    private GoodSS goodSS = new GoodSS();
    private boolean firstTimeWornning = true;
    private DistanceList specialDis = null;

    public   GoodResiduesForHB(){}
    public GoodResiduesForHB(DistanceList disList){
        specialDis = disList;
    }

    public boolean accept(Object obj) {
        Distance distance = (Distance)obj;
        boolean ans =  isHO.accept(obj) && isNot13.accept(obj) && goodSS.accept(obj);
        if (!ans)
            return ans;
        boolean frozen = distance.atom1() .frozen() & distance.atom2() .frozen() ;
         if (ans & frozen){
                if(firstTimeWornning )   //TODO check this - maybe frozen atoms should be looked at ?
                {
                    System.out.println("*****************************************************************************************************\n" +
                                                               "*** NOTE: frozen atoms does not consider as GoodResiduesForHB  !!! *****\n" +
                                                               "*****************************************************************************************************");
                    firstTimeWornning = false;
                }
                return false;
            }
        if(ans & specialDis != null && !specialDis.isEmpty() ){
            Distance pair;
            Iterator specDisIter = specialDis.iterator() ;
             Atom atom1,atom2;
            while( (pair  = (Distance) specDisIter .next()) != null){
                    atom1 = pair.atom1() ;
                    int atom1Num = atom1.number();
                    atom2 = pair.atom2() ;
                    int atom2Num = atom2.number();
                    if(distance.isDistanceOf(atom1) | distance.isDistanceOf(atom2)){
                        return (distance.atom1().number() == atom1Num & distance.atom2() .number() == atom2Num) |
                                                  (distance.atom1().number() == atom2Num & distance.atom2() .number() == atom1Num);
                }
            }
        }
        return ans;

    }



    //--------------------------- internal class IsNot13 ----------------------------------

    /* this filter Accept Distance between Atoms just if there are at list 3 atoms away on the sequense,
     *  (atomes that are less then 3 atoms away on the sequense never create Hydrogen bonds with each other)
     */
    static class IsNot13 implements Filter{
        public boolean accept(Object obj) {
            Distance dis = (Distance)obj;
            if (!dis.atom1().chain().equalsIgnoreCase(dis.atom1().chain()))
                return true;
            int residueNumber1 = dis.atom1().residueNumber();
            int residueNumber2 = dis.atom2().residueNumber();
            return (!(Math.abs(residueNumber1-residueNumber2) < 3));
        }
    }


    //--------------------------- internal class GoodSS ---------------------------

    static class GoodSS implements Filter{
        public boolean accept(Object obj) {
            Distance dis = (Distance)obj;
            String atom1SS = dis.atom1().residue().secondaryStructure();
            String atom2SS = dis.atom2().residue().secondaryStructure();
            if (atom1SS.equals("COIL") || atom2SS.equals("COIL"))
                return false;
            if ((atom1SS.equals("HELIX") || atom1SS .equals("HELIX_OR_COIL")) && (atom2SS.equals("HELIX") | atom2SS .equals("HELIX_OR_COIL") ))
                {
                    int residueNumber1 = dis.atom1().residueNumber();
                    int residueNumber2 = dis.atom2().residueNumber();
                    return (Math.abs(residueNumber1-residueNumber2) == 4);
                }
//            return !(((atom1SS.equals("HELIX") || atom1SS .equals("HELIX_OR_COIL")) && (atom2SS.equals("SHEET") | atom2SS .equals("SHEET_OR_COIL") )) ||
//                    ((atom1SS.equals("SHEET") | atom1SS .equals("SHEET_OR_COIL")) && (atom2SS.equals("HELIX") | atom2SS .equals("HELIX_OR_COIL") )));
            return (atom1SS.equals("SHEET") || atom1SS .equals("SHEET_OR_COIL")) && (atom2SS.equals("SHEET") | atom2SS .equals("SHEET_OR_COIL"));
        }
    }
}
