package ui.factory;

import ui.cli.PayrollCLI;
import model.entity.ContractorEmployee;
import model.entity.Employee;
import model.entity.EmployeeType;
import model.value.SalaryComponents;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Creates Contractor employees: hourly rate + billed hours.
 * For PayrollRequest components, also supports a fixed contract amount
 * when billed hours are zero.
 */
public class ContractorEmployeeFactory implements EmployeeFactory {

    @Override
    public EmployeeType type() {
        return EmployeeType.CONTRACTOR;
    }

    @Override
    public Employee create(Scanner scanner, EmployeeDetails details) {
        System.out.print("Enter Contractor Hourly Rate: ");
        BigDecimal rate = PayrollCLI.parseAmount(scanner.nextLine());
        System.out.print("Enter Billed Hours: ");
        BigDecimal hours = PayrollCLI.parseAmount(scanner.nextLine());
        return new ContractorEmployee(details.id(), details.name(), details.email(),
                details.department(), details.bankAccountNumber(), rate, hours, details.taxRegime());
    }

    @Override
    public void fillSalaryComponents(Scanner scanner, SalaryComponents components) {
        System.out.print("Enter Contractor Hourly Rate / Fixed Amount: ");
        BigDecimal amount = PayrollCLI.parseAmount(scanner.nextLine());
        System.out.print("Enter Billed Hours (or 0 for fixed contract): ");
        BigDecimal hours = PayrollCLI.parseAmount(scanner.nextLine());
        if (hours.signum() > 0) {
            components.setHourlyRate(amount);
            components.setHoursWorked(hours);
        } else {
            components.setFixedContractAmount(amount);
        }
    }
}
