package generator;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * input file format for vector source file:
 * (i - amount of symbols in set)
 * (row with i symbols separated by space)
 * (i strings of probabilities also separated by space)
 * vector file example:
 * 3
 * a b c
 * 0.1
 * 0.3
 * 0.6
 * <p>
 * matrix source file example:
 * 3
 * a b c
 * 0.1 0.2 0.7
 * 0.4 0.5 0.1
 * 0.25 0.25 0.5
 * <p>
 * sum of symbol probabilities must = 1
 * assumed, that all "symbol's" is one char-length
 */
public class SymbolGenerator {

    private static final int DEFAULT_GENERATED_FILE_SIZE = 100;

    @Getter
    @Setter
    private int size = DEFAULT_GENERATED_FILE_SIZE;

    /**
     * need for matrix only
     */
    private static final List<String> symbolsFromFile = new ArrayList<>();

    private File source;
    private File result;

    private final ProbabilitiesMatrix matrix = new ProbabilitiesMatrix();
    private final ProbabilitiesVector vector = new ProbabilitiesVector();

    private int symbolAmount;

    private boolean isWorkingWithMatrix = false;

    public SymbolGenerator(String sourcePath, String resultPath) throws RuntimeException, IOException {
        if (sourcePath == null || resultPath == null) {
            throw new IllegalArgumentException("source and result path cant be null");
        }
        source = new File(sourcePath);
        result = new File(resultPath);
        boolean isWritable = result.setWritable(true);
        if (!source.canRead()) {
            throw new IllegalArgumentException("can't read this file");
        }
        if (!result.canWrite() || !isWritable) {
            throw new IllegalArgumentException("can't write to result file");
        }
        readSourceData();
    }

    private void readSourceData() throws IOException {
        Optional<List<String>> optional = readFileStrings();
        if (optional.isEmpty()) {
            return;
        }
        List<String> strings = optional.get();
        if (isSourceContainsMatrix(strings.get(0))) {
            workingWithMatrix(strings);
        } else {
            workingWithVector(strings);
        }
    }

    private Optional<List<String>> readFileStrings() throws IOException {
        List<String> strings;
        try (Stream<String> linesStream = Files.lines(source.toPath())) {
            strings = linesStream.collect(Collectors.toList());
            if (strings.size() == 0) {
                return Optional.empty();
            }
            try {
                symbolAmount = Integer.parseInt(strings.get(0));
            } catch (NumberFormatException exp) {
                throw new NumberFormatException("firs number in source file has error");
            }
            if (symbolAmount == 0) {
                return Optional.empty();
            }
            strings.remove(0);
            symbolsFromFile.clear();
            String[] allSymbols = strings.get(0).split("\\s+");
            strings.remove(0);
            symbolsFromFile.addAll(Arrays.asList(allSymbols));
            if (strings.size() != symbolAmount || strings.size() != symbolsFromFile.size()) {
                throw new RuntimeException("amount of symbols is not correspond with first line amount");
            }
        } catch (FileNotFoundException exp) {
            throw exp;
        } catch (IOException exp) {
            throw new IOException("some IO exception happened", exp);
        }
        return Optional.of(strings);
    }

    private void workingWithVector(List<String> strings) {
        isWorkingWithMatrix = false;
        vector.clear();
        boolean isAdded;
        for (int i = 0; i < strings.size(); i++) {
            isAdded = vector.addProbability(symbolsFromFile.get(i), Double.parseDouble(strings.get(i)));
            if (!isAdded) {
                throw new RuntimeException("source file contains same symbols multiple times");
            }
        }
        if (!vector.isSumEqualOne()) {
            throw new RuntimeException("vector sum of probabilities is not equal to 1");
        }
        vector.sort();
    }

    private void workingWithMatrix(List<String> strings) {
        isWorkingWithMatrix = true;
        matrix.clear();
        String[] split;
        List<Double> rowProbabilities;
        for (int strI = 0; strI < strings.size(); strI++) {
            split = strings.get(strI).split("\\s+");
            rowProbabilities = new ArrayList<>(split.length);
            for (int i = 0; i < split.length; i++) {
                rowProbabilities.add(Double.parseDouble(split[i]));
            }
            matrix.addRow(symbolsFromFile.get(strI), rowProbabilities);
        }
        if (!matrix.isSumEqualOne()) {
            throw new RuntimeException("matrix sum of each row probabilities is not equal to 1");
        }
        matrix.sort();
    }

    private boolean isSourceContainsMatrix(String str) {
        return str.split("\\s+").length > 2;
    }

    public void generateFile(int size) throws IOException {
        if (size < 0) {
            this.size = 0;
        }
        this.size = size;
        generateFile();
    }

    public void generateFile() throws IOException {
        Random rand = new Random(System.currentTimeMillis());
        try (FileWriter writer = new FileWriter(result)) {
            if (isWorkingWithMatrix) {
                generationBasedOnMatrix(rand, writer);
            } else {
                generationBasedOnVector(rand, writer);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void generationBasedOnVector(Random rand, FileWriter writer) throws IOException {
        List<ProbabilitiesVector.Probability> probabilities = vector.getProbabilities();
        outer:
        for (int i = 0; i < size; i++) {
            for (ProbabilitiesVector.Probability p : probabilities) {
                if (p.getProbability() >= rand.nextDouble()) {
                    writer.write(p.getSymbol());
                    continue outer;
                }
            }
            writer.write(probabilities.get(probabilities.size() - 1).getSymbol());
        }
    }

    private void generationBasedOnMatrix(Random rand, FileWriter writer) throws IOException {
        double val = matrix.solveInitEquation(); // todo
        //nextGaussian
        int currentRowId = rand.nextInt(symbolAmount);
        List<ProbabilitiesVector.Probability> currentRow;
        List<ProbabilitiesMatrix.MatrixRow> matrixRows = matrix.getMatrix();
        outer:
        for (int i = 0; i < size; i++) {
            currentRow = matrixRows.get(currentRowId).getRow();
            for (int j = 0; j < currentRow.size(); j++) {
                if (currentRow.get(j).getProbability() >= rand.nextDouble()) {
                    writer.write(currentRow.get(j).getSymbol());
                    currentRowId = j;
                    continue outer;
                }
            }
            writer.write(currentRow.get(currentRow.size()-1).getSymbol());
            currentRowId = currentRow.size()-1;
        }
    }

    public static List<String> getSymbolsFromFile() {
        return new ArrayList<>(symbolsFromFile);
    }

    public List<ProbabilitiesVector.Probability> calcProbabilitiesInGenerateFile() throws IOException {
        List<ProbabilitiesVector.Probability> probabilities = new ArrayList<>();
        try (Stream<String> linesStream = Files.lines(result.toPath())) {
            Optional<String> file = linesStream.findFirst();
            if (file.isEmpty()) {
                return probabilities;
            }
            char[] symbols = file.get().toCharArray();
            Map<Character, Long> charMap = new HashMap<>();
            for (char s : symbols) {
                if (charMap.get(s) == null) {
                    charMap.put(s, 1L);
                } else {
                    charMap.put(s, charMap.get(s) + 1);
                }
            }
            charMap.forEach((c, p) -> probabilities.add(
                            new ProbabilitiesVector.Probability(
                                    String.valueOf(c), ((double) p / (double) symbols.length))
            ));
        } catch (IOException e) {
            throw new IOException("error reading result file", e);
        }
        return probabilities;
    }

    public strictfp double calcNonDependEntropy (List<ProbabilitiesVector.Probability> p) {
        double current, log2 = Math.log(2), e = 0;
        for (int i = 0; i < p.size(); i++) {
            current = p.get(i).getProbability();
            e += current * (Math.log(current) / log2);
        }
        return -e;
    }

    public strictfp double calcDependEntropy (List<ProbabilitiesVector.Probability> p) {
        double e = 0;
        List<ProbabilitiesMatrix.MatrixRow> matrix = this.matrix.getMatrix();
        for (int i = 0; i < p.size(); i++) {
            for (int j = 0; j < p.size(); j++) {
                e += p.get(j).getProbability() *
                        matrix.get(j).getRow().get(i).getProbability() *
                        Math.log10(matrix.get(j).getRow().get(i).getProbability());
            }
        }
        return e;
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder("Generator. Size = ");
        str.append(size).append(" symbol amount = ").append(symbolAmount).append("\n");
        if (isWorkingWithMatrix) {
            str.append(matrix.toString());
        } else {
            str.append(vector.toString());
        }
        str.append("\n");
        return str.toString();
    }

}
