import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class contains static methods that help calculate the molecular mass of 
 * a chemical formula string (e.g., "H2O", "CH4", etc.). It does this by 
 * reading a csv file with chemical symbols and their corresponding atomic 
 * masses, and creating a HashMap with these keys and values. The HashMap is 
 * used to analyze the formula and calculate its molecular mass.
 * 
 * @author ben31w
 * @version 2021.03.05
 */
public class MolecularMass {
    
    /**
     * Given a periodic table csv file and a chemical formula string, return 
     * the molecular mass of the formula (using the atomic masses given in the 
     * csv file).
     * 
     * @param periodicTable
     *          a csv file with element symbols in the first column and atomic 
     *          masses in the next
     * @param formula
     *          a chemical formula such as "H2O" or "CH4"
     * @return the molecular mass of the formula
     * @throws FileNotFoundException 
     */
    public static double process(File periodicTable, String formula) throws FileNotFoundException {
        double molecularMass = 0.0;
        
        // Construct a HashMap to store elements and atomic masses from the file
        Map<String, Double> table = createTable(periodicTable);
        
        // Index through the formula, checking for valid chemical symbols and 
        // the subscripts after them.
        int index = 0;
        while (index < formula.length()) {
            String chemSymbol = "";
            int subscript = 1;
            
            // First if check the characters at index and index+1 form an element.
            if ( index != formula.length()-1 && 
                    table.containsKey( formula.substring(index, index+2) ) ) {
                chemSymbol = formula.substring(index, index+2);
            }
            // Otherwise check the character at index.
            else if ( table.containsKey( "" + formula.charAt(index)) ) {
                chemSymbol = "" + formula.charAt(index);
            }
            // If any characters in the formula do not form an element, throw an exception.
            else {
                throw new InvalidParameterException(formula + " is not a valid chemical formula");
            }
            
            // Get the subscript after the element and the element's atomic 
            // mass, and add their product to the total mass.
            subscript = getSubscript( formula, chemSymbol, index );
            double atomicMass = table.get(chemSymbol);
            molecularMass += atomicMass * subscript;
            
            // Adjust the index.
            ++index;
            if (chemSymbol.length() > 1) { // if the chemical symbol is length 2
                ++index;
            }
            if (subscript > 1) { // if there was a subscript after the chemical symbol
                index += ("" + subscript).length();
            }
        }
        
        return molecularMass;
    }
    
    
    /**
     * Given a "periodic table" csv file, return a HashMap representing the 
     * periodic table with elements names and keys and atomic masses as values. 
     * In order for this method to work, the csv file must contain the chemical 
     * symbols of elements in the first column and atomic masses in the second.
     * 
     * @param file
     *          a csv file with strings in the first column and doubles in the 
     *          second
     * @return a HashMap with strings (chemical symbols) as keys and doubles 
     *         (atomic masses) as values
     */
    public static Map<String, Double> createTable(File file) throws FileNotFoundException {
        Map<String, Double> periodicTable = new HashMap<>();
        
        Scanner fin = new Scanner(file); // may throw FileNotFoundException
        
        while(fin.hasNextLine()) {
            Scanner sc = new Scanner(fin.nextLine());
            sc.useDelimiter(",");
            
            while(sc.hasNext()) {
                String element = sc.next();
                double mass = sc.nextDouble();
                periodicTable.put(element, mass);
            }
            sc.close();
        }
        
        fin.close();
        
        return periodicTable;
    }
    
    
    /**
     * Returns the subscript of a given element inside a given chemical 
     * formula. This method requires a chemical formula, a chemical symbol of 
     * an element inside the formula, and the index at which the chemical 
     * symbol appears inside the formula. It returns the the numbers appearing 
     * after the chemical symbol (or 1 if no numbers are present).
     *
     * @param formula
     *          a chemical formula like "H2O" or "CH4"
     * @param chemSymbol
     *          the chemical symbol of an element inside the formula
     * @param index
     *          the index at which the chemical symbol appears inside the 
     *          formula (it is assumed that this method receives an accurate 
     *          index whenever it is called)
     * @return the subscript appearing after the chemical symbol
     */
    private static int getSubscript(String formula, String chemSymbol, int index) {
        int subscript = 1;
        String numString;
        
        // Build a string containing the numbers after the chemical symbol.
        StringBuilder sb = new StringBuilder();
        for (int i=index+chemSymbol.length(); i<formula.length(); i++) {
            char c = formula.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
            else {
                break;
            }
        }
        numString = sb.toString();
        
        // If numbers were found after the chemical symbol, adjust the subscript
        if (numString.length() > 0) {
            subscript = Integer.parseInt(numString);
        }
        
        return subscript;
    }
    
}
