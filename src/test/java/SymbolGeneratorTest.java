import generator.ProbabilitiesVector;
import generator.SymbolGenerator;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SymbolGeneratorTest {

    @Test
    public void byteArrayTest () {
        byte[] ar1 = new byte[] {};
        byte[] ar2 = new byte[] {};
        byte[] ar3 = new byte[] {1,2,3};
        byte[] ar4 = new byte[] {0, +0};
        byte[] ar5 = new byte[] {-0,+0};
        HashMap<byte[], String> map = new HashMap<>();

        map.put(ar1, "1");
        map.put(ar2,"2");
        map.put(ar3,"3");
        map.put(ar4,"4");
        map.put(ar5,"5");
        System.out.println(
                map.get(ar1) + " " +
                map.get(ar2)
        );
    }

    @Test
    public void bySymbolReadTest () {
        double beginTime = System.currentTimeMillis();
        File f = new File("./readTest.txt");
        char c;
        try (FileInputStream fis = new FileInputStream(f)) {

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Time spent: " + (System.currentTimeMillis() - beginTime));
    }

    @Test
    public void allFileReadTest () {
        double beginTime = System.currentTimeMillis();
        File f = new File("./readTest.txt");

        System.out.println("Time spent: " + (System.currentTimeMillis() - beginTime));
    }

    @Test
    public void dummy () {
        int sum = 0;
        for (int i = 2; i < 200; i++) {
            sum += i;
        }
        System.out.println(sum);
    }

    @Test
    public void generatorTest () {
        try {
            SymbolGenerator generator = new SymbolGenerator("./input.txt", "./output.txt");
            System.out.println(generator);
            generator.setSize(128);
            generator.generateFile();
            System.out.println("\nResult probabilities:");
            List<ProbabilitiesVector.Probability> probabilities = generator.calcProbabilitiesInGenerateFile();
            for (ProbabilitiesVector.Probability p : probabilities) {
                System.out.println(p);
            }
            System.out.println("H(X) = " + generator.calcNonDependEntropy(probabilities));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void squareGeneratorTest () {
        try {
            SymbolGenerator generator = new SymbolGenerator("./squareInput.txt", "./squareOutput.txt");
            System.out.println(generator);
            generator.generateFile(2000000);
            List<ProbabilitiesVector.Probability> probabilities = generator.calcProbabilitiesInGenerateFile();
            for (ProbabilitiesVector.Probability p : probabilities) {
                System.out.println(p);
            }
            System.out.println("H(X) = " + generator.calcDependEntropy(probabilities));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}