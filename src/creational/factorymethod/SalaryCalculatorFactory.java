package creational.factorymethod;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Task 2: Classic GoF Factory Method Pattern - Abstract Creator.
 *
 * Defines the abstract factory method {@link #createCalculator()} which defers
 * instantiation of specific {@link SalaryCalculator} products to concrete creator
 * subclasses (e.g. {@link PermanentSalaryCalculatorFactory}, {@link ExecutiveSalaryCalculatorFactory}).
 *
 * Unlike a Simple Factory (which uses a monolithic switch statement to construct products),
 * the GoF Factory Method pattern allows extending the system with new product types by adding
 * new Creator subclasses without modifying existing code, adhering to the Open-Closed Principle (OCP).
 */
public abstract class SalaryCalculatorFactory {

    /**
     * The classic GoF Factory Method. Subclasses override this method
     * to instantiate and return their specific ConcreteProduct.
     *
     * @return a concrete SalaryCalculator instance
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
     * Keeps callers decoupled from concrete creator subclasses.
     *
     * @param type the employee type
     * @return the appropriate concrete SalaryCalculatorFactory creator
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
