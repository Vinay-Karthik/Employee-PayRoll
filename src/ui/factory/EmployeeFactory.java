package ui.factory;

import model.entity.Employee;
import model.entity.EmployeeType;
import model.value.SalaryComponents;

import java.util.Scanner;

/**
 * Factory for constructing a specific Employee subtype from console input.
 * Each implementation encapsulates the prompts for its employee type's
 * compensation details, keeping PayrollCLI free of per-type switch logic.
 */
public interface EmployeeFactory {

    /**
     * The employee type this factory creates.
     */
    EmployeeType type();

    /**
     * Prompts for type-specific compensation details and builds the Employee.
     */
    Employee create(Scanner scanner, EmployeeDetails details);

    /**
     * Prompts for type-specific salary components used by PayrollRequest.
     */
    void fillSalaryComponents(Scanner scanner, SalaryComponents components);
}
