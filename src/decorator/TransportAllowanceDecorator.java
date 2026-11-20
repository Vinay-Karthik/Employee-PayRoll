package decorator;

import java.math.BigDecimal;

/**
 * Concrete Decorator for Transport Allowance.
 *
 * Single Responsibility: Adds transport allowance to the salary.
 */
public class TransportAllowanceDecorator extends SalaryDecorator {
    private static final BigDecimal DEFAULT_TRANSPORT_ALLOWANCE = new BigDecimal("1600.00");
    private final BigDecimal transportAllowance;

    /**
     * Constructs Transport Allowance decorator using the default allowance ($1600.00).
     */
    public TransportAllowanceDecorator(SalaryCalculator decoratedCalculator) {
        this(decoratedCalculator, DEFAULT_TRANSPORT_ALLOWANCE);
    }

    /**
     * Constructs Transport Allowance decorator with an explicit monetary amount.
     *
     * @param decoratedCalculator the calculator to decorate
     * @param transportAllowance explicit non-null, non-negative allowance amount
     */
    public TransportAllowanceDecorator(SalaryCalculator decoratedCalculator, BigDecimal transportAllowance) {
        super(decoratedCalculator);
        if (transportAllowance == null) {
            throw new IllegalArgumentException("Transport allowance cannot be null");
        }
        if (transportAllowance.signum() < 0) {
            throw new IllegalArgumentException("Transport allowance cannot be negative: " + transportAllowance);
        }
        this.transportAllowance = transportAllowance;
    }

    @Override
    public BigDecimal calculateSalary() {
        return super.calculateSalary().add(transportAllowance);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Transport Allowance (" + transportAllowance + ")";
    }

    public BigDecimal getTransportAllowance() {
        return transportAllowance;
    }
}
