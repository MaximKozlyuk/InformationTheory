package generator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class SymbolContainer {

    private final String symbol;

    public SymbolContainer(String symbol) {
        this.symbol = symbol;
    }

}
