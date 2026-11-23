package calculator;

import model.EmployeeType;

import java.util.Objects;

/**
 * GoF Factory Method Pattern: Abstract Creator.
 *
 * Defines the abstract factory method {@link #createCalculator()} which defers
 * instantiation of specific {@link SalaryCalculator} products to concrete creator subclasses
 * for the unified employee classifications (Full-Time, Part-Time, Contractor, Intern).
 */
public abstract class SalaryCalculatorFactory {

    /**
     * Classic GoF Factory Method. Subclasses override this method
     * to instantiate and return their specific ConcreteProduct.
     */
    public abstract SalaryCalculator createCalculator();

    /**
     * Selects the appropriate concrete Creator for the given EmployeeType.
     */
    public static SalaryCalculatorFactory forType(EmployeeType type) {
        Objects.requireNonNull(type, "Employee type must not be null");
        return switch (type) {
            case FULL_TIME -> new FullTimeSalaryCalculatorFactory();
            case PART_TIME -> new PartTimeSalaryCalculatorFactory();
            case CONTRACTOR -> new ContractorSalaryCalculatorFactory();
            case INTERN -> new InternSalaryCalculatorFactory();
        };
    }
}
