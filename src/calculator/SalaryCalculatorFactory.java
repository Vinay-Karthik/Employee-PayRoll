package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * GoF Factory Method Pattern: Abstract Creator.
 *
 * Defines the abstract factory method {@link #createCalculator()} which defers
 * instantiation of specific {@link SalaryCalculator} products to concrete creator subclasses.
 */
public abstract class SalaryCalculatorFactory {

    /**
     * Classic GoF Factory Method. Subclasses override this method
     * to instantiate and return their specific ConcreteProduct.
     */
    public abstract SalaryCalculator createCalculator();

    /**
     * Creator operation: calculates gross salary using the product
     * instantiated by the factory method.
     */
    public BigDecimal calculateGross(SalaryComponents components) {
        SalaryCalculator calculator = createCalculator();
        return calculator.calculateGross(components);
    }

    /**
     * Returns the employee type supported by this creator.
     */
    public abstract EmployeeType getSupportedType();

    /**
     * Selects the appropriate concrete Creator for the given EmployeeType.
     */
    public static SalaryCalculatorFactory forType(EmployeeType type) {
        Objects.requireNonNull(type, "Employee type must not be null");
        return switch (type) {
            case PERMANENT -> new PermanentSalaryCalculatorFactory();
            case CONTRACT -> new ContractSalaryCalculatorFactory();
            case SALES -> new SalesSalaryCalculatorFactory();
            case EXECUTIVE -> new ExecutiveSalaryCalculatorFactory();
        };
    }

    /**
     * Convenience method to obtain a calculator product directly for a given type.
     */
    public static SalaryCalculator create(EmployeeType type) {
        return forType(type).createCalculator();
    }
}
