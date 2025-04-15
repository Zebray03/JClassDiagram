package diagram.model.enums;

public enum Visibility {
    PRIVATE("-"),
    PROTECTED("#"),
    PACKAGE_PRIVATE("~"),
    PUBLIC("+");

    private final String symbol;

    Visibility(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}