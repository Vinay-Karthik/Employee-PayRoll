package decorator.pipeline;

/**
 * Type-safe enumeration for available salary decorators.
 * Eliminates magic strings and facilitates reliable tracking of enhancement components.
 */
public enum DecoratorType {
    HRA("HRA"),
    TRANSPORT_ALLOWANCE("Transport Allowance"),
    PERFORMANCE_BONUS("Performance Bonus"),
    INSURANCE_DEDUCTION("Insurance Deduction");

    private final String displayName;

    DecoratorType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
