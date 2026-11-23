package decorator.decorator;

import decorator.component.SalaryComponent;
import decorator.pipeline.DecoratorType;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Base Decorator (GoF Decorator Pattern).
 *
 * Implements {@link SalaryComponent} while wrapping another {@link SalaryComponent}
 * instance. Delegates all standard calls to the wrapped component, providing a base
 * for concrete enhancement decorators.
 */
public abstract class SalaryDecorator implements SalaryComponent {
    protected final SalaryComponent decoratedComponent;

    public SalaryDecorator(SalaryComponent decoratedComponent) {
        this.decoratedComponent = Objects.requireNonNull(decoratedComponent, "Decorated salary component must not be null");
    }

    public SalaryComponent getDecoratedComponent() {
        return decoratedComponent;
    }

    /**
     * Returns the DecoratorType associated with this concrete decorator.
     * Used for chain discovery and single-use constraint verification.
     */
    public abstract DecoratorType getDecoratorType();

    @Override
    public BigDecimal calculateSalary() {
        return decoratedComponent.calculateSalary();
    }

    @Override
    public BigDecimal getBaseSalary() {
        return decoratedComponent.getBaseSalary();
    }

    @Override
    public String getDescription() {
        return decoratedComponent.getDescription();
    }
}
