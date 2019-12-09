package generator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class ProbabilitiesMatrix implements ProbabilityContainer {

    @Getter
    private final List<MatrixRow> matrix = new ArrayList<>();

    public ProbabilitiesMatrix() {
    }

    public void addRow(String symbol, List<Double> row) {
        matrix.add(new MatrixRow(symbol, row));
    }

    public void clear() {
        matrix.forEach(MatrixRow::clear);
    }

    @Override
    public void sort() {
        matrix.forEach(row -> row.row.sort(ProbabilitiesVector.Probability::compareTo));
    }

    @Override
    public boolean isSumEqualOne() {
        double sum = 0;
        for (MatrixRow row : matrix) {
            for (int i = 0; i < row.row.size(); i ++) {
                sum += row.row.get(i).getProbability();
            }
            if (sum != 1) {
                return false;
            }
            sum = 0;
        }
        return true;
    }

    public double solveInitEquation() throws RuntimeException {
        double[][] a = new double[matrix.size()][];
        double[] b = new double[matrix.size()], x = new double[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            a[i] = new double[matrix.size()];
            for (int j = 0; j < matrix.size(); j++) {
                a[i][j] = matrix.get(i).row.get(j).getProbability();
                if (i == j) {
                    a[i][j] = 1;
                }
            }
        }
        b[b.length-1] = 1;
        Gauss.method(a, b, x);
//        System.out.println("x array:");
//        for (double v : x) {
//            System.out.print(v + " ");
//        }
        return 1;
    }

    /**
     * here row - vector of probabilities, if previous was super.getSymbol()
     */
    static final class MatrixRow extends SymbolContainer {

        @Getter
        private final List<ProbabilitiesVector.Probability> row;

        public MatrixRow(String symbol, List<Double> row) {
            super(symbol);
            List<ProbabilitiesVector.Probability> probsRow = new ArrayList<>();
            for (int i = 0; i < SymbolGenerator.getSymbolsFromFile().size(); i++) {
                probsRow.add(new ProbabilitiesVector.Probability(
                        SymbolGenerator.getSymbolsFromFile().get(i), row.get(i))
                );
            }
            this.row = probsRow;
        }

        public void clear() {
            row.clear();
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder("MatrixRow: ");
            str.append(super.getSymbol()).append(" ");
            for (int i = 0; i < row.size(); i++) {
                str.append(row.get(i).getSymbol()).append(":").append(row.get(i).getProbability()).append(" ");
            }
            return str.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("generator.ProbabilitiesMatrix:\n");
        for (MatrixRow row : matrix) {
            str.append(row.toString()).append("\n");
        }
        str.append("\n");
        return str.toString();
    }

}
