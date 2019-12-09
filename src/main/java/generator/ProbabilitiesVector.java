package generator;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProbabilitiesVector implements ProbabilityContainer {

    @Getter
    private final List<Probability> probabilities = new ArrayList<>();

    public ProbabilitiesVector() { }

    public boolean addProbability (String symbol, double probability) {
        if (symbol != null && probability <= 1 && !isContainsSymbol(symbol)) {
            return probabilities.add(new Probability(symbol, probability));
        } else {
            throw new IllegalArgumentException(
                    "symbol can't be null and probability must be < 1 or this symbol already contains");
        }
    }

    private boolean isContainsSymbol (String symbol) {
        for (Probability p : probabilities) {
            if (p.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public void clear () {
        probabilities.clear();
    }

    @Getter
    @ToString(callSuper = true)
    public static final class Probability extends SymbolContainer implements Comparable<Probability> {
        private final double probability;

        public Probability(String symbol, double probability) {
            super(symbol);
            this.probability = probability;
        }

        @Override
        public int compareTo(Probability o) {
            return Double.compare(this.probability, o.probability);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Probability that = (Probability) o;
            return Double.compare(that.probability, probability) == 0 &&
                    Objects.equals(super.getSymbol(), that.getSymbol());
        }
    }

    @Override
    public void sort() {
        probabilities.sort(Probability::compareTo);
    }

    @Override
    public boolean isSumEqualOne() {
        double sum = 0;
        for (Probability p : probabilities) {
            sum += p.getProbability();
        }
        return sum == 1;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Probability p : probabilities) {
            str.append(p.getSymbol()).append(" ").append(p.probability).append("\n");
        }
        str.append("\n");
        return str.toString();
    }
}
