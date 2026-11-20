package creational.factorymethod;

import java.math.BigDecimal;

/**
 * Task 2: Factory Method Pattern - Product interface.
 * Each concrete calculator encapsulates the salary calculation
 * logic for one employee salary structure.
 */
public interface SalaryCalculator {

    /**
     * Computes the gross salary for the given salary components.
     */
    BigDecimal calculateGross(SalaryComponents components);

    /**
     * Returns the employee type this calculator supports.
     */
    EmployeeType getSupportedType();
}
