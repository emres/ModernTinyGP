package be.tmdata.ModernTinyGP;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class ModernTinyGPTest
{
    /**
     * Rigorous Test :-)
     */

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void setupFitness_WithValidFileName_ShouldReadDataFromFileAndSetVariables() {
        double delta = 0.0001;
        long randomizationSeed = -1;
        String fileName = "src/test/resources/valid_sin_data.txt";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ModernTinyGP gp = new ModernTinyGP(fileName, randomizationSeed);

        gp.setupFitness(fileName);

        assertEquals(1, ModernTinyGP.varNumber);
        assertEquals(100, ModernTinyGP.randomNumber);
        assertEquals(-5, ModernTinyGP.minRandom, delta);
        assertEquals(5, ModernTinyGP.maxRandom, delta);
        assertEquals(63, ModernTinyGP.fitnessCases);

        // Verify console output
        assertEquals("", getConsoleOutput(outContent)); // Assuming no error messages are printed
    }

    @Test
    public void calculateAndPrintStats_Test() {
        double delta = 0.0001;
        long randomizationSeed = 2;
        String fileName = "src/test/resources/valid_sin_data.txt";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ModernTinyGP gp = new ModernTinyGP(fileName, randomizationSeed);

        gp.calculateAndPrintStats(gp.fitness, gp.population, 0);

        assertEquals(-4702.205328217432, ModernTinyGP.fAveragePopulation, delta);
    }

    // Helper method to capture console output
    private String getConsoleOutput(ByteArrayOutputStream outContent) {
        return outContent.toString();
    }

}
