package ui.command;

import model.entity.Employee;
import model.entity.EmployeeType;
import model.factory.*;
import model.factory.EmployeeDetails;
import service.PayrollService;
import tax.regime.TaxRegime;
import ui.cli.PayrollCLI;
import ui.view.ConsoleView;

import java.util.Scanner;

/**
 * Command to add a new employee.
 */
public class AddEmployeeCommand implements MenuCommand {
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;
    private final DepartmentPrompter departmentPrompter;
    private final TaxRegimePrompter taxRegimePrompter;

    public AddEmployeeCommand(PayrollService payrollService, ConsoleView view, Scanner scanner,
                              DepartmentPrompter departmentPrompter, TaxRegimePrompter taxRegimePrompter) {
        this.payrollService = payrollService;
        this.view = view;
        this.scanner = scanner;
        this.departmentPrompter = departmentPrompter;
        this.taxRegimePrompter = taxRegimePrompter;
    }

    @Override
    public boolean execute() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (payrollService.getEmployeeById(id).isPresent()) {
                view.displayMessage("An employee with ID " + id + " already exists!");
                return true;
            }

            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();

            String dept = departmentPrompter.promptDepartment();

            System.out.print("Enter Bank Account Number: ");
            String account = scanner.nextLine().trim();

            System.out.print("Enter Role Type (" + EmployeeFactoryRegistry.options() + "): ");
            int type = Integer.parseInt(scanner.nextLine().trim());
            EmployeeFactory factory = EmployeeFactoryRegistry.factoryFor(type);

            TaxRegime taxRegime = taxRegimePrompter.promptTaxRegime();

            Employee newEmployee = factory.create(scanner,
                    new EmployeeDetails(id, name, email, dept, account, taxRegime));

            payrollService.registerEmployee(newEmployee);
            view.displayMessage("Employee " + name + " (ID: " + id + ") registered successfully!");
        } catch (NumberFormatException e) {
            view.displayMessage("Input error: Please enter valid numeric values for numbers/amounts.");
        } catch (IllegalArgumentException e) {
            view.displayMessage("Validation error: " + e.getMessage());
        }
        return true;
    }
}
