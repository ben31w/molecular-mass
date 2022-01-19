import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.TempDirectory;

class MolecularMassTest {
    
    @Test
    @ExtendWith(TempDirectory.class)
    void test_create_table(@TempDir Path folder) throws FileNotFoundException {
        Map<String, Double> actual;
        
        // Create a file with a few chemical symbols and atomic masses.
        File file = folder.resolve( "short_table.csv" ).toFile();
        PrintWriter fout = new PrintWriter(file);
        fout.println("H,1.008");
        fout.println("Ce,140.1161");
        fout.println("Og,294");
        fout.close();
        
        assertTrue( file.exists(), "Output file does not exist" );
        
        actual = MolecularMass.createTable(file);
        assertTrue( actual.containsKey("H"),  "Table does not contain H" );
        assertTrue( actual.containsKey("Ce"), "Table does not contain Ce" );
        assertTrue( actual.containsKey("Og"), "Table does not contain Og" );
        
        assertEquals( 1.008, actual.get("H"), "H has incorrect atomic mass" );
        assertEquals( 140.1161, actual.get("Ce"), "Ce has incorrect atomic mass" );
        assertEquals( 294.0, actual.get("Og"), "Og has incorrect atomic mass" );
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    void test_proccess_invalid_symbol(@TempDir Path folder) throws FileNotFoundException {      
        // Create a temp file
        File file = folder.resolve( "short_table.csv" ).toFile();
        PrintWriter fout = new PrintWriter(file);
        fout.println("H,1.008");
        fout.println("C,12.011");
        fout.close();
        
        String invalidFormula = "C-H4";
        
        Throwable t = assertThrows( InvalidParameterException.class, 
                () -> MolecularMass.process(file, invalidFormula) );
        assertEquals( invalidFormula + " is not a valid chemical formula", t.getMessage() );
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    void test_process_no_subscripts(@TempDir Path folder) throws FileNotFoundException {
        double actual;
        double expected;
        double delta;
        
        // Create a temporary file
        File file = folder.resolve( "short_table.csv" ).toFile();
        PrintWriter fout = new PrintWriter(file);
        fout.println("C,12.011");
        fout.println("O,15.999");
        fout.println("N,14.007");
        fout.println("Na,22.98976928");
        fout.println("C,12.011");
        fout.println("Cl,35.45");
        fout.close();
        
        actual = MolecularMass.process( file, "CO" );
        expected = 28.01;
        delta = 0.000001;
        assertEquals( expected, actual, delta, 
                "expected value " + expected + " != actual value " + actual );
        
        actual = MolecularMass.process( file, "NaCl" );
        expected = 58.43976928;
        delta = 0.000001;
        assertEquals( expected, actual, delta, 
                "expected value " + expected + " != actual value " + actual );
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    void test_process_single_digit_subscripts(@TempDir Path folder) 
            throws FileNotFoundException {
        double actual;
        double expected;
        double delta;
        
        // Create a temporary file
        File file = folder.resolve( "short_table.csv" ).toFile();
        PrintWriter fout = new PrintWriter(file);
        fout.println("H,1.008");
        fout.println("O,15.999");
        fout.println("Ac,227");
        fout.println("O,15.999");
        fout.close();
        
        actual = MolecularMass.process( file, "H2O" );
        expected = 18.015;
        delta = 0.000001;
        assertEquals( expected, actual, delta, 
                "expected value " + expected + " != actual value " + actual );
        
        actual = MolecularMass.process( file, "Ac2O3" );
        expected = 501.997;
        delta = 0.000001;
        assertEquals( expected, actual, delta, 
                "expected value " + expected + " != actual value " + actual );
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    void test_process_double_digit_subscripts(@TempDir Path folder) 
            throws FileNotFoundException {
        double actual;
        double expected;
        double delta;
        
        // Create a temporary file
        File file = folder.resolve( "short_table.csv" ).toFile();
        PrintWriter fout = new PrintWriter(file);
        fout.println("H,1.008");
        fout.println("C,12.011");
        fout.println("O,15.999");
        fout.close();
        
        actual = MolecularMass.process( file, "C6H12O6" );
        expected = 180.156;
        delta = 0.000001;
        assertEquals( expected, actual, delta, 
                "expected value " + expected + " != actual value " + actual );      
    }
    
}
