package ui.command;

import model.entity.EmployeeType;
import model.factory.EmployeeFactory;
import ui.command.EmployeeFactoryRegistry;
import model.request.PayrollRequest;
import model.value.PaySlip;
import model.value.SalaryComponents;
import service.PayrollService;
import tax.regime.TaxRegime;
import ui.cli.PayrollCLI;
import ui.view.ConsoleView;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Command to process a payroll request using Builder + Factory Method + Strategy.
 */
public class PayrollRequestCommand implements MenuCommand {
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;
    private final DepartmentPrompter departmentPrompter;
    private final TaxRegimePrompter taxRegimePrompter;
    private final String defaultFinancialYear;

    public PayrollRequestCommand(PayrollService payrollService, ConsoleView view, Scanner scanner,
                                DepartmentPrompter departmentPrompter, TaxRegimePrompter taxRegimePrompter,
                                String defaultFinancialYear) {
        this.payrollService = payrollService;
        this.view = view;
        this.scanner = scanner;
        this.departmentPrompter = departmentPrompter;
        this.taxRegimePrompter = taxRegimePrompter;
        this.defaultFinancialYear = defaultFinancialYear;
    }

    @Override
    public boolean execute() {
        try {
            System.out.print("Enter Role Type (" + EmployeeFactoryRegistry.options() + "): ");
            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            EmployeeFactory factory = EmployeeFactoryRegistry.factoryFor(typeChoice);
            EmployeeType employeeType = factory.type();

            SalaryComponents components = new SalaryComponents();
            factory.fillSalaryComponents(scanner, components);

            System.out.print("Enter bonus description / details (optional, press Enter to skip): ");
            String bonusInput = scanner.nextLine().trim();

            PayrollRequest.Builder requestBuilder = new PayrollRequest.Builder()
                    .employeeType(employeeType)
                    .salaryComponents(components)
                    .bonusDetails(bonusInput.isEmpty() ? null : bonusInput);

            System.out.print("Enter Deduction Name (optional, press Enter to skip): ");
            String dedName = scanner.nextLine().trim();
            if (!dedName.isEmpty()) {
                System.out.print("Enter Deduction Amount: ");
                BigDecimal dedAmt = PayrollCLI.parseAmount(scanner.nextLine());
                requestBuilder.addDeduction(dedName, dedAmt);
            }

            System.out.print("Enter Employee ID: ");
            requestBuilder.employeeId(Integer.parseInt(scanner.nextLine().trim()));

            System.out.print("Enter Employee Name: ");
            requestBuilder.employeeName(scanner.nextLine().trim());

            requestBuilder.department(departmentPrompter.promptDepartment());

            System.out.print("Enter Bank Account Number (Enter for 'N/A'): ");
            String account = scanner.nextLine().trim();
            if (!account.isEmpty()) {
                requestBuilder.bankAccountNumber(account);
            }

            System.out.print("Enter Financial Year [" + defaultFinancialYear + "]: ");
            String fy = scanner.nextLine().trim();
            requestBuilder.financialYear(fy.isEmpty() ? defaultFinancialYear : fy);

            TaxRegime regime = taxRegimePrompter.promptTaxRegime();
            requestBuilder.taxRegime(regime);

            PayrollRequest request = requestBuilder.build();
            PaySlip slip = payrollService.processPayrollRequest(request);
            view.displayPaySlip(slip);
        } catch (NumberFormatException e) {
            view.displayMessage("Input error: Please enter valid numeric values.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.displayMessage("Validation error: " + e.getMessage());
        }
        return true;
    }
}
