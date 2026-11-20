package creational.factorymethod;

import java.util.Objects;

/**
 * Task 2: Factory Method Pattern - Creator.
 *
 * Selects the correct SalaryCalculator for an employee type.
 * Contains NO salary calculation logic itself - it only decides
 * which product to instantiate, keeping creation decoupled from use.
 */
public class SalaryCalculatorFactory {

    /**
     * Factory method: creates the appropriate calculator for the given type.
     *
     * @param type the employee salary structure type (must not be null)
     * @return a SalaryCalculator capable of handling the requested type
     * @throws IllegalArgumentException if the type is null or unsupported
     */
    public SalaryCalculator createCalculator(EmployeeType type) {
        Objects.requireNonNull(type, "Employee type must not be null");

        return switch (type) {
            case PERMANENT -> new PermanentSalaryCalculator();
            case CONTRACT -> new ContractSalaryCalculator();
            case SALES -> new SalesSalaryCalculator();
            case EXECUTIVE -> new ExecutiveSalaryCalculator();
        };
    }
}
