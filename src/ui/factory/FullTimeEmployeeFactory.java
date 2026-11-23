package ui.factory;

import ui.cli.PayrollCLI;
import model.entity.Employee;
import model.entity.EmployeeType;
import model.entity.FullTimeEmployee;
import model.value.SalaryComponents;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Creates Full-Time employees: monthly base salary + bonus.
 */
public class FullTimeEmployeeFactory implements EmployeeFactory {

    @Override
    public EmployeeType type() {
        return EmployeeType.FULL_TIME;
    }

    @Override
    public Employee create(Scanner scanner, EmployeeDetails details) {
        System.out.print("Enter Monthly Base Salary: ");
        BigDecimal salary = PayrollCLI.parseAmount(scanner.nextLine());
        System.out.print("Enter Monthly Bonus: ");
        BigDecimal bonus = PayrollCLI.parseAmount(scanner.nextLine());
        return new FullTimeEmployee(details.id(), details.name(), details.email(),
                details.department(), details.bankAccountNumber(), salary, bonus, details.taxRegime());
    }

    @Override
    public void fillSalaryComponents(Scanner scanner, SalaryComponents components) {
        System.out.print("Enter Monthly Base Salary: ");
        components.setBasic(PayrollCLI.parseAmount(scanner.nextLine()));
        System.out.print("Enter Monthly Bonus: ");
        components.setBonus(PayrollCLI.parseAmount(scanner.nextLine()));
    }
}
