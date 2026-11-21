package ui;

import config.PayrollConfig;
import decorator.DecoratorType;
import decorator.SalaryEnhancementPipeline;
import model.*;
import service.PayrollBatchResult;
import service.PayrollFacade;
import service.PayrollService;
import tax.TaxRegime;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller for CLI interaction.
 * Coordinates user input with PayrollFacade and ConsoleView.
 * Keeps business rules strictly outside this presentation controller.
 */
public class PayrollCLI {
    private final PayrollFacade payrollFacade;
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;
    private final String reportFilePath;

    public PayrollCLI(PayrollFacade payrollFacade, PayrollService payrollService,
                      ConsoleView view, Scanner scanner, String reportFilePath) {
        this.payrollFacade = Objects.requireNonNull(payrollFacade, "PayrollFacade must not be null");
        this.payrollService = Objects.requireNonNull(payrollService, "PayrollService must not be null");
        this.view = Objects.requireNonNull(view, "ConsoleView must not be null");
        this.scanner = Objects.requireNonNull(scanner, "Scanner must not be null");
        this.reportFilePath = Objects.requireNonNull(reportFilePath, "Report file path must not be null");
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
                case 6 -> handleTemplatePayrollRequest();
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

            System.out.print("Enter Role Type (1=Full-Time, 2=Part-Time, 3=Contractor, 4=Intern): ");
            int type = Integer.parseInt(scanner.nextLine().trim());

            Employee newEmployee = switch (type) {
                case 1 -> {
                    System.out.print("Enter Monthly Base Salary: ");
                    BigDecimal salary = parseAmount(scanner.nextLine());
                    System.out.print("Enter Monthly Bonus: ");
                    BigDecimal bonus = parseAmount(scanner.nextLine());
                    yield new FullTimeEmployee(id, name, email, dept, account, salary, bonus);
                }
                case 2 -> {
                    System.out.print("Enter Hourly Rate: ");
                    BigDecimal rate = parseAmount(scanner.nextLine());
                    System.out.print("Enter Hours Worked this month: ");
                    BigDecimal hours = parseAmount(scanner.nextLine());
                    yield new PartTimeEmployee(id, name, email, dept, account, rate, hours);
                }
                case 3 -> {
                    System.out.print("Enter Contractor Hourly Rate: ");
                    BigDecimal rate = parseAmount(scanner.nextLine());
                    System.out.print("Enter Billed Hours: ");
                    BigDecimal hours = parseAmount(scanner.nextLine());
                    yield new ContractorEmployee(id, name, email, dept, account, rate, hours);
                }
                case 4 -> {
                    System.out.print("Enter Intern Monthly Stipend: ");
                    BigDecimal stipend = parseAmount(scanner.nextLine());
                    yield new InternEmployee(id, name, email, dept, account, stipend);
                }
                default -> throw new IllegalArgumentException("Unknown employee role type: " + type);
            };

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
        PayrollBatchResult result = payrollFacade.processMonthlyPayroll(reportFilePath);

        // Display summary based on result
        view.displayMessage("\n=== Monthly Payroll Processing Result ===");
        view.displayMessage("Status: " + result.getStatus());
        view.displayMessage("Total Employees: " + result.getTotalEmployees());
        view.displayMessage("Successful Payslips: " + result.getSuccessfulCount());
        view.displayMessage("Failed Payslips: " + result.getFailedCount());

        if (result.isPartialSuccess()) {
            view.displayMessage("WARNING: Some employees failed. Failed IDs: " + result.getFailedEmployeeIds());
        } else if (result.isFailure()) {
            view.displayError("Payroll processing failed: " + result.getErrorMessage());
            if (!result.getFailedEmployeeIds().isEmpty()) {
                view.displayMessage("Failed Employee IDs: " + result.getFailedEmployeeIds());
            }
            return;
        }

        // Display the summary report if available
        if (result.getSummaryReport() != null && !result.getSummaryReport().isBlank()) {
            view.displayReport(result.getSummaryReport());
        }

        // Display export confirmation - use isReportExported() to avoid false success messages
        if (result.isReportExported()) {
            view.displayMessage("Successfully exported report to: " + result.getReportDestination());
        } else if (result.getStatus() == PayrollBatchResult.Status.SUCCESS && result.getTotalEmployees() == 0) {
            view.displayMessage("No report exported (no employees in system).");
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
     * Integrated creational-pattern flow:
     * Prototype (clone a PayrollTemplate) -> customize clone ->
     * Builder (construct PayrollRequest) -> service processes it via
     * Factory Method + Abstract Factory -> PaySlip.
     */
    private void handleTemplatePayrollRequest() {
        try {
            List<PayrollTemplate> templates = PayrollTemplateCatalog.getTemplates();
            view.displayMessage("\nAvailable payroll templates:");
            for (int i = 0; i < templates.size(); i++) {
                PayrollTemplate t = templates.get(i);
                System.out.printf("  %d. %s (%s)%n", i + 1, t.getTemplateName(), t.getEmployeeType());
            }

            System.out.print("Select template (1-" + templates.size() + "): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > templates.size()) {
                view.displayMessage("Invalid template selection.");
                return;
            }

            // Prototype pattern: clone the template so the original stays untouched
            PayrollTemplate template = templates.get(choice - 1).clone();
            view.displayMessage("Using template: " + template.getTemplateName());

            // Descriptive metadata: bonus description / justification
            System.out.print("Enter bonus description / details (Enter to keep '" + template.getBonusDetails() + "'): ");
            String bonusInput = scanner.nextLine().trim();
            if (!bonusInput.isEmpty()) {
                template.setBonusDetails(bonusInput);
            }

            // Monetary bonus is represented as BigDecimal in SalaryComponents (not parsed from description)
            if (template.getEmployeeType() == EmployeeType.EXECUTIVE
                    || template.getSalaryComponents().getBonus().signum() > 0) {
                System.out.print("Enter monetary bonus amount (Enter to keep " + template.getSalaryComponents().getBonus() + "): ");
                String bonusAmtStr = scanner.nextLine().trim();
                if (!bonusAmtStr.isEmpty()) {
                    SalaryComponents comps = template.getSalaryComponents();
                    comps.setBonus(parseAmount(bonusAmtStr));
                    template.setSalaryComponents(comps);
                }
            }

            PayrollRequest.Builder requestBuilder = new PayrollRequest.Builder()
                    .employeeType(template.getEmployeeType())
                    .salaryComponents(template.getSalaryComponents())
                    .bonusDetails(template.getBonusDetails());

            for (Map.Entry<String, BigDecimal> entry : template.getDeductions().entrySet()) {
                requestBuilder.addDeduction(entry.getKey(), entry.getValue());
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

            PayrollConfig config = PayrollConfig.getInstance();
            System.out.print("Enter Financial Year [" + config.getFinancialYear() + "]: ");
            String fy = scanner.nextLine().trim();
            requestBuilder.financialYear(fy.isEmpty() ? config.getFinancialYear() : fy);

            System.out.print("Enter Tax Regime (1=OLD, 2=NEW) [" + config.getDefaultTaxRegime() + "]: ");
            String regimeInput = scanner.nextLine().trim();
            TaxRegime regime = config.getDefaultTaxRegime();
            if (regimeInput.equals("1")) {
                regime = TaxRegime.OLD;
            } else if (regimeInput.equals("2")) {
                regime = TaxRegime.NEW;
            }
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
            SalaryEnhancementPipeline pipeline = payrollService.createSalaryEnhancementPipeline(base);
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

    private static BigDecimal parseAmount(String input) {
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: '" + input + "'");
        }
    }
}
