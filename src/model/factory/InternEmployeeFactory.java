package model.factory;

import ui.cli.PayrollCLI;
import model.entity.Employee;
import model.entity.EmployeeType;
import model.entity.InternEmployee;
import model.value.SalaryComponents;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Creates Intern employees: monthly stipend.
 */
public class InternEmployeeFactory implements EmployeeFactory {

    @Override
    public EmployeeType type() {
        return EmployeeType.INTERN;
    }

    @Override
    public Employee create(Scanner scanner, EmployeeDetails details) {
        System.out.print("Enter Intern Monthly Stipend: ");
        BigDecimal stipend = PayrollCLI.parseAmount(scanner.nextLine());
        return new InternEmployee(details.id(), details.name(), details.email(),
                details.department(), details.bankAccountNumber(), stipend, details.taxRegime());
    }

    @Override
    public void fillSalaryComponents(Scanner scanner, SalaryComponents components) {
        System.out.print("Enter Intern Monthly Stipend: ");
        components.setStipend(PayrollCLI.parseAmount(scanner.nextLine()));
    }
}
