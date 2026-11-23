package ui.cli;

import decorator.pipeline.DecoratorType;
import decorator.pipeline.SalaryEnhancementPipeline;
import model.entity.*;
import model.value.PaySlip;
import model.request.PayrollRequest;
import model.value.SalaryComponents;
import service.PayrollFacade;
import service.PayrollService;
import tax.regime.TaxRegime;
import ui.factory.EmployeeFactory;
import ui.factory.FullTimeEmployeeFactory;
import ui.factory.PartTimeEmployeeFactory;
import ui.factory.ContractorEmployeeFactory;
import ui.factory.InternEmployeeFactory;
import ui.factory.EmployeeDetails;
import ui.view.ConsoleView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller for CLI interaction.
 * Coordinates user input with PayrollFacade and ConsoleView.
 * Keeps business rules strictly outside this presentation controller.
 */
public class PayrollCLI {
    private static final List<EmployeeFactory> EMPLOYEE_FACTORIES = List.of(
            new FullTimeEmployeeFactory(),
            new PartTimeEmployeeFactory(),
            new ContractorEmployeeFactory(),
            new InternEmployeeFactory());

    private final PayrollFacade payrollFacade;
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;
    private final String reportFilePath;
    private final String defaultFinancialYear;

    public PayrollCLI(PayrollFacade payrollFacade, PayrollService payrollService,
                      ConsoleView view, Scanner scanner, String reportFilePath,
                      String defaultFinancialYear) {
        this.payrollFacade = Objects.requireNonNull(payrollFacade, "PayrollFacade must not be null");
        this.payrollService = Objects.requireNonNull(payrollService, "PayrollService must not be null");
        this.view = Objects.requireNonNull(view, "ConsoleView must not be null");
        this.scanner = Objects.requireNonNull(scanner, "Scanner must not be null");
        this.reportFilePath = Objects.requireNonNull(reportFilePath, "Report file path must not be null");
        this.defaultFinancialYear = (defaultFinancialYear != null && !defaultFinancialYear.isBlank())
                ? defaultFinancialYear : "FY2025-26";
    }

    public PayrollCLI(PayrollFacade payrollFacade, PayrollService payrollService,
                      ConsoleView view, Scanner scanner, String reportFilePath) {
        this(payrollFacade, payrollService, view, scanner, reportFilePath, "FY2025-26");
    }

    public void start() {
        view.displayWelcome();
        boolean running = true;

        while (running) {
            view.displayMenu();
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input! Please enter a number between 1 and 8.");
                continue;
            }

            switch (choice) {
                case 1 -> handleListEmployees();
                case 2 -> handleAddEmployee();
                case 3 -> handleIndividualPaySlip();
                case 4 -> handleFullPayroll();
                case 5 -> handleDeleteEmployee();
                case 6 -> handlePayrollRequest();
                case 7 -> handleDynamicSalaryEnhancement();
                case 8 -> {
                    running = false;
                    view.displayMessage("Exiting Payroll Application. Goodbye!");
                }
                default -> view.displayMessage("Invalid selection. Please choose an option from 1 to 8.");
            }
        }
    }

    private void handleListEmployees() {
        List<Employee> employees = payrollService.getAllEmployees();
        if (employees.isEmpty()) {
            view.displayMessage("No employees currently registered in the system.");
            return;
        }
        view.displayEmployees(employees);
    }

    private void handleAddEmployee() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (payrollService.getEmployeeById(id).isPresent()) {
                view.displayMessage("An employee with ID " + id + " already exists!");
                return;
            }

            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();

            String dept = promptDepartment();

            System.out.print("Enter Bank Account Number: ");
            String account = scanner.nextLine().trim();

            System.out.print("Enter Role Type (" + employeeTypeOptions() + "): ");
            int type = Integer.parseInt(scanner.nextLine().trim());
            EmployeeFactory factory = factoryFor(type);

            TaxRegime taxRegime = promptTaxRegime();

            Employee newEmployee = factory.create(scanner,
                    new EmployeeDetails(id, name, email, dept, account, taxRegime));

            payrollService.registerEmployee(newEmployee);
            view.displayMessage("Employee " + name + " (ID: " + id + ") registered successfully!");
        } catch (NumberFormatException e) {
            view.displayMessage("Input error: Please enter valid numeric values for numbers/amounts.");
        } catch (IllegalArgumentException e) {
            view.displayMessage("Validation error: " + e.getMessage());
        }
    }

    private void handleIndividualPaySlip() {
        System.out.print("Enter Employee ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Optional<PaySlip> slipOpt = payrollService.processEmployeePaySlip(id);
            if (slipOpt.isPresent()) {
                view.displayPaySlip(slipOpt.get());
            } else {
                view.displayMessage("Employee ID " + id + " not found!");
            }
        } catch (NumberFormatException e) {
            view.displayMessage("Please enter a valid numeric Employee ID.");
        }
    }

    private void handleFullPayroll() {
        try {
            view.displayMessage("\nProcessing monthly payroll...");
            String report = payrollFacade.processMonthlyPayroll(reportFilePath);
            if (report == null || report.isBlank()) {
                view.displayMessage("No employees found in the system. No report exported.");
                return;
            }
            view.displayReport(report);
            view.displayMessage("Successfully exported report to: " + reportFilePath);
        } catch (IOException e) {
            view.displayError("Failed to export payroll summary report to " + reportFilePath + ": " + e.getMessage());
        } catch (RuntimeException e) {
            view.displayError("Payroll processing failed: " + e.getMessage());
        }
    }

    private void handleDeleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean removed = payrollService.removeEmployee(id);
            if (removed) {
                view.displayMessage("Employee with ID " + id + " has been successfully removed.");
            } else {
                view.displayMessage("Employee ID " + id + " was not found!");
            }
        } catch (NumberFormatException e) {
            view.displayMessage("Please enter a valid numeric Employee ID.");
        }
    }

    /**
     * Creational-pattern flow:
     * Builder (construct PayrollRequest) -> service processes it via
     * Factory Method + Strategy -> PaySlip.
     */
    private void handlePayrollRequest() {
        try {
            System.out.print("Enter Role Type (" + employeeTypeOptions() + "): ");
            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            EmployeeFactory factory = factoryFor(typeChoice);
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
                BigDecimal dedAmt = parseAmount(scanner.nextLine());
                requestBuilder.addDeduction(dedName, dedAmt);
            }

            System.out.print("Enter Employee ID: ");
            requestBuilder.employeeId(Integer.parseInt(scanner.nextLine().trim()));

            System.out.print("Enter Employee Name: ");
            requestBuilder.employeeName(scanner.nextLine().trim());

            requestBuilder.department(promptDepartment());

            System.out.print("Enter Bank Account Number (Enter for 'N/A'): ");
            String account = scanner.nextLine().trim();
            if (!account.isEmpty()) {
                requestBuilder.bankAccountNumber(account);
            }

            System.out.print("Enter Financial Year [" + defaultFinancialYear + "]: ");
            String fy = scanner.nextLine().trim();
            requestBuilder.financialYear(fy.isEmpty() ? defaultFinancialYear : fy);

            TaxRegime regime = promptTaxRegime();
            requestBuilder.taxRegime(regime);

            PayrollRequest request = requestBuilder.build();
            PaySlip slip = payrollService.processPayrollRequest(request);
            view.displayPaySlip(slip);
        } catch (NumberFormatException e) {
            view.displayMessage("Input error: Please enter valid numeric values.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.displayMessage("Validation error: " + e.getMessage());
        }
    }

    /**
     * Interactive console flow for dynamic salary enhancements using the Decorator Pattern.
     * All business rules (duplicate prevention, deduction limits, decorator instantiation)
     * are strictly delegated to SalaryEnhancementPipeline.
     */
    private void handleDynamicSalaryEnhancement() {
        view.displayMessage("\n=== Dynamic Salary Enhancement (Decorator Pattern) ===");
        try {
            System.out.print("Enter Base Salary: ");
            BigDecimal base = parseAmount(scanner.nextLine());
            SalaryEnhancementPipeline pipeline = new SalaryEnhancementPipeline(base);
            String currency = view.getCurrency();

            boolean adding = true;
            while (adding) {
                System.out.println("\nCurrent Configuration: " + pipeline.getCurrentDescription());
                System.out.printf("Current Calculated Salary: %s %.2f%n", currency, pipeline.getCurrentSalary());
                System.out.println("\nSelect Decorator to wrap:");
                System.out.println("1. Add HRA (+20% of base)" + (pipeline.isApplied(DecoratorType.HRA) ? " [ALREADY APPLIED]" : ""));
                System.out.println("2. Add Transport Allowance (+" + currency + " 1,600.00)" + (pipeline.isApplied(DecoratorType.TRANSPORT_ALLOWANCE) ? " [ALREADY APPLIED]" : ""));
                System.out.println("3. Add Performance Bonus (+10% of base)" + (pipeline.isApplied(DecoratorType.PERFORMANCE_BONUS) ? " [ALREADY APPLIED]" : ""));
                System.out.println("4. Apply Insurance Deduction (-" + currency + " 150.00)" + (pipeline.isApplied(DecoratorType.INSURANCE_DEDUCTION) ? " [ALREADY APPLIED]" : ""));
                System.out.println("5. Custom HRA (specify amount)" + (pipeline.isApplied(DecoratorType.HRA) ? " [ALREADY APPLIED]" : ""));
                System.out.println("6. Custom Transport Allowance (specify amount)" + (pipeline.isApplied(DecoratorType.TRANSPORT_ALLOWANCE) ? " [ALREADY APPLIED]" : ""));
                System.out.println("7. Custom Performance Bonus (specify amount)" + (pipeline.isApplied(DecoratorType.PERFORMANCE_BONUS) ? " [ALREADY APPLIED]" : ""));
                System.out.println("8. Custom Insurance Deduction (specify amount)" + (pipeline.isApplied(DecoratorType.INSURANCE_DEDUCTION) ? " [ALREADY APPLIED]" : ""));
                System.out.println("9. Complete & View Enhanced Total");
                System.out.print("Enter choice (1-9): ");

                String subChoice = scanner.nextLine().trim();
                try {
                    switch (subChoice) {
                        case "1" -> pipeline.applyDecorator(DecoratorType.HRA);
                        case "2" -> pipeline.applyDecorator(DecoratorType.TRANSPORT_ALLOWANCE);
                        case "3" -> pipeline.applyDecorator(DecoratorType.PERFORMANCE_BONUS);
                        case "4" -> pipeline.applyDecorator(DecoratorType.INSURANCE_DEDUCTION);
                        case "5" -> {
                            System.out.print("Enter custom HRA amount: ");
                            pipeline.applyDecorator(DecoratorType.HRA, parseAmount(scanner.nextLine()));
                        }
                        case "6" -> {
                            System.out.print("Enter custom Transport Allowance amount: ");
                            pipeline.applyDecorator(DecoratorType.TRANSPORT_ALLOWANCE, parseAmount(scanner.nextLine()));
                        }
                        case "7" -> {
                            System.out.print("Enter custom Performance Bonus amount: ");
                            pipeline.applyDecorator(DecoratorType.PERFORMANCE_BONUS, parseAmount(scanner.nextLine()));
                        }
                        case "8" -> {
                            System.out.print("Enter custom Insurance Deduction amount: ");
                            pipeline.applyDecorator(DecoratorType.INSURANCE_DEDUCTION, parseAmount(scanner.nextLine()));
                        }
                        case "9" -> adding = false;
                        default -> System.out.println("Invalid selection. Please choose 1-9.");
                    }
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    view.displayMessage("Error: " + ex.getMessage());
                }
            }

            System.out.println("\n==============================================");
            System.out.println("         FINAL ENHANCED SALARY SUMMARY        ");
            System.out.println("==============================================");
            System.out.printf("Base Salary        : %s %.2f%n", currency, pipeline.getBaseSalary());
            System.out.println("Decorator Pipeline : " + pipeline.getCurrentDescription());
            System.out.printf("Total Enhanced Pay : %s %.2f%n", currency, pipeline.getCurrentSalary());
            System.out.println("==============================================");

        } catch (IllegalArgumentException | IllegalStateException e) {
            view.displayMessage("Error: " + e.getMessage());
        }
    }

    private static String employeeTypeOptions() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < EMPLOYEE_FACTORIES.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(i + 1).append("=").append(EMPLOYEE_FACTORIES.get(i).type().getDisplayName());
        }
        return sb.toString();
    }

    private static EmployeeFactory factoryFor(int choice) {
        if (choice < 1 || choice > EMPLOYEE_FACTORIES.size()) {
            throw new IllegalArgumentException("Unknown employee role type: " + choice);
        }
        return EMPLOYEE_FACTORIES.get(choice - 1);
    }

    private TaxRegime promptTaxRegime() {
        while (true) {
            System.out.print("Enter Tax Regime (1=OLD, 2=NEW) [NEW]: ");
            String regimeInput = scanner.nextLine().trim();
            if (regimeInput.isEmpty() || regimeInput.equals("2")) {
                return TaxRegime.NEW;
            } else if (regimeInput.equals("1")) {
                return TaxRegime.OLD;
            } else {
                view.displayMessage("Invalid input! Please enter 1 for OLD or 2 for NEW.");
            }
        }
    }

    private String promptDepartment() {
        List<String> departments = payrollService.getAllEmployees().stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .toList();

        if (!departments.isEmpty()) {
            view.displayMessage("Existing departments:");
            for (int i = 0; i < departments.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, departments.get(i));
            }
        }

        System.out.print("Select department");
        if (!departments.isEmpty()) {
            System.out.print(" (1-" + departments.size() + ")");
        }
        System.out.print(" or enter a new department name: ");

        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= departments.size()) {
                return departments.get(choice - 1);
            }
            throw new IllegalArgumentException("Invalid department selection: " + choice);
        } catch (NumberFormatException ignored) {
            // Not a number - treat as a new department name
        }

        if (input.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        return input;
    }

    public static BigDecimal parseAmount(String input) {
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: '" + input + "'");
        }
    }
}
