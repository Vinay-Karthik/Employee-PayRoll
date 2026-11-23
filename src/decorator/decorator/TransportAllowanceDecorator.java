package decorator.decorator;

import decorator.component.SalaryComponent;
import decorator.pipeline.DecoratorType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator for Transport Allowance.
 *
 * Single Responsibility: Adds transport allowance to the salary.
 * Enforces a consistent scale of 2 with RoundingMode.HALF_UP for all monetary amounts.
 */
public class TransportAllowanceDecorator extends SalaryDecorator {
    private static final BigDecimal DEFAULT_TRANSPORT_ALLOWANCE = new BigDecimal("1600.00");
    private final BigDecimal transportAllowance;

    /**
     * Constructs Transport Allowance decorator using the default allowance ($1600.00).
     */
    public TransportAllowanceDecorator(SalaryComponent decoratedCalculator) {
        this(decoratedCalculator, DEFAULT_TRANSPORT_ALLOWANCE);
    }

    /**
     * Constructs Transport Allowance decorator with an explicit monetary amount.
     * Enforces consistent monetary scale (2 decimal places) and HALF_UP rounding.
     *
     * @param decoratedCalculator the calculator to decorate
     * @param transportAllowance explicit non-null, non-negative allowance amount
     */
    public TransportAllowanceDecorator(SalaryComponent decoratedCalculator, BigDecimal transportAllowance) {
        super(decoratedCalculator);
        if (transportAllowance == null) {
            throw new IllegalArgumentException("Transport allowance cannot be null");
        }
        if (transportAllowance.signum() < 0) {
            throw new IllegalArgumentException("Transport allowance cannot be negative: " + transportAllowance);
        }
        this.transportAllowance = transportAllowance.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public DecoratorType getDecoratorType() {
        return DecoratorType.TRANSPORT_ALLOWANCE;
    }

    @Override
    public BigDecimal calculateSalary() {
        return super.calculateSalary().add(transportAllowance).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Transport Allowance (" + transportAllowance + ")";
    }

    public BigDecimal getTransportAllowance() {
        return transportAllowance;
    }
}
