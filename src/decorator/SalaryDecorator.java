package decorator;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Base Decorator (GoF Decorator Pattern).
 *
 * Implements {@link SalaryCalculator} while wrapping another {@link SalaryCalculator}
 * instance. Delegates all standard calls to the wrapped component, providing a base
 * for concrete enhancement decorators.
 */
public abstract class SalaryDecorator implements SalaryCalculator {
    protected final SalaryCalculator decoratedCalculator;

    public SalaryDecorator(SalaryCalculator decoratedCalculator) {
        this.decoratedCalculator = Objects.requireNonNull(decoratedCalculator, "Decorated salary calculator must not be null");
    }

    public SalaryCalculator getDecoratedCalculator() {
        return decoratedCalculator;
    }

    /**
     * Returns the DecoratorType associated with this concrete decorator.
     * Used for chain discovery and single-use constraint verification.
     */
    public abstract DecoratorType getDecoratorType();

    @Override
    public BigDecimal calculateSalary() {
        return decoratedCalculator.calculateSalary();
    }

    @Override
    public BigDecimal getBaseSalary() {
        return decoratedCalculator.getBaseSalary();
    }

    @Override
    public String getDescription() {
        return decoratedCalculator.getDescription();
    }
}
