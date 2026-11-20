package ui;

import model.*;
import service.PayrollService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller for CLI interaction.
 * Coordinates user input with PayrollService and ConsoleView.
 */
public class PayrollCLI {
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;
    private final String reportFilePath;

    public PayrollCLI(PayrollService payrollService, ConsoleView view, Scanner scanner, String reportFilePath) {
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
                view.displayMessage("Invalid input! Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1 -> handleListEmployees();
                case 2 -> handleAddEmployee();
                case 3 -> handleIndividualPaySlip();
                case 4 -> handleFullPayroll();
                case 5 -> handleDeleteEmployee();
                case 6 -> {
                    running = false;
                    view.displayMessage("Exiting Payroll Application. Goodbye!");
                }
                default -> view.displayMessage("Invalid selection. Please choose an option from 1 to 6.");
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

            System.out.print("Enter Department: ");
            String dept = scanner.nextLine().trim();

            System.out.print("Enter Bank Account Number: ");
            String account = scanner.nextLine().trim();

            System.out.print("Enter Role Type (1=Full-Time, 2=Part-Time, 3=Contractor, 4=Intern): ");
            int type = Integer.parseInt(scanner.nextLine().trim());

            Employee newEmployee = switch (type) {
                case 1 -> {
                    System.out.print("Enter Monthly Base Salary: ");
                    double salary = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter Monthly Bonus: ");
                    double bonus = Double.parseDouble(scanner.nextLine().trim());
                    yield new FullTimeEmployee(id, name, email, dept, account, salary, bonus);
                }
                case 2 -> {
                    System.out.print("Enter Hourly Rate: ");
                    double rate = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter Hours Worked this month: ");
                    double hours = Double.parseDouble(scanner.nextLine().trim());
                    yield new PartTimeEmployee(id, name, email, dept, account, rate, hours);
                }
                case 3 -> {
                    System.out.print("Enter Contractor Hourly Rate: ");
                    double rate = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter Billed Hours: ");
                    double hours = Double.parseDouble(scanner.nextLine().trim());
                    yield new ContractorEmployee(id, name, email, dept, account, rate, hours);
                }
                case 4 -> {
                    System.out.print("Enter Intern Monthly Stipend: ");
                    double stipend = Double.parseDouble(scanner.nextLine().trim());
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
        List<PaySlip> slips = payrollService.processAllPaySlips();
        if (slips.isEmpty()) {
            view.displayMessage("No employees in the system to process payroll for!");
            return;
        }

        String report = payrollService.generatePayrollSummaryReport(slips);
        view.displayReport(report);

        try {
            payrollService.exportSummaryReport(report, reportFilePath);
            view.displayMessage("Successfully exported report to: " + reportFilePath);
        } catch (Exception e) {
            view.displayError("Failed to export report file: " + e.getMessage());
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
}
