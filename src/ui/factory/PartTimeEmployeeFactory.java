package ui.factory;

import ui.cli.PayrollCLI;
import model.entity.Employee;
import model.entity.EmployeeType;
import model.entity.PartTimeEmployee;
import model.value.SalaryComponents;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Creates Part-Time employees: hourly rate + hours worked.
 */
public class PartTimeEmployeeFactory implements EmployeeFactory {

    @Override
    public EmployeeType type() {
        return EmployeeType.PART_TIME;
    }

    @Override
    public Employee create(Scanner scanner, EmployeeDetails details) {
        System.out.print("Enter Hourly Rate: ");
        BigDecimal rate = PayrollCLI.parseAmount(scanner.nextLine());
        System.out.print("Enter Hours Worked this month: ");
        BigDecimal hours = PayrollCLI.parseAmount(scanner.nextLine());
        return new PartTimeEmployee(details.id(), details.name(), details.email(),
                details.department(), details.bankAccountNumber(), rate, hours, details.taxRegime());
    }

    @Override
    public void fillSalaryComponents(Scanner scanner, SalaryComponents components) {
        System.out.print("Enter Hourly Rate: ");
        components.setHourlyRate(PayrollCLI.parseAmount(scanner.nextLine()));
        System.out.print("Enter Hours Worked this month: ");
        components.setHoursWorked(PayrollCLI.parseAmount(scanner.nextLine()));
    }
}
