import creational.factorymethod.SalaryCalculatorFactory;
import report.FileReportExporter;
import report.ReportExporter;
import repository.EmployeeRepository;
import repository.InMemoryEmployeeRepository;
import service.PayrollService;
import service.PayrollServiceImpl;
import ui.ConsoleView;
import ui.PayrollCLI;

import java.util.Scanner;

/**
 * Application Entry Point / Composition Root.
 *
 * Refactored using SOLID Principles and GoF Creational Design Patterns:
 * - Single Responsibility Principle (SRP): Acts solely as the composition root to wire together dependencies.
 * - Dependency Inversion Principle (DIP): High-level components depend on interfaces (EmployeeRepository, ReportExporter, PayrollService),
 *   and concrete implementations are injected here.
 * - GoF Factory Method: Demonstrates classic Creator hierarchy (SalaryCalculatorFactory subclasses) for employee salary calculation.
 * - GoF Abstract Factory: Regime-based tax and deduction component creation (PayrollComponentFactory).
 * - GoF Builder: Validated immutable request construction (PayrollRequest.Builder).
 * - GoF Prototype: Deep-copied customizable payroll templates (PayrollTemplate).
 * - GoF Singleton: Shared configuration provider (PayrollConfig).
 */
public class PayrollSystem {
    private static final String DEFAULT_REPORT_FILE = "payroll_summary_report.txt";

    public static void main(String[] args) {
        // Dependency Injection & Configuration Setup
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        ReportExporter reportExporter = new FileReportExporter();

        // Inject dependencies including GoF Factory Method creator resolver
        PayrollService payrollService = new PayrollServiceImpl(
                repository,
                reportExporter,
                SalaryCalculatorFactory::forType
        );

        ConsoleView view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        PayrollCLI cli = new PayrollCLI(payrollService, view, scanner, DEFAULT_REPORT_FILE);

        // Start Application
        try {
            cli.start();
        } finally {
            scanner.close();
        }
    }
}
