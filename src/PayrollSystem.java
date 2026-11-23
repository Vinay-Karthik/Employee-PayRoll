import calculator.SalaryCalculatorFactory;
import integration.EmployeeDirectory;
import integration.ExternalHRAdapter;
import integration.ExternalHRService;
import report.FileReportExporter;
import report.ReportExporter;
import service.PayrollFacade;
import service.PayrollService;
import service.PayrollServiceImpl;
import ui.ConsoleView;
import ui.PayrollCLI;

import java.util.Scanner;

/**
 * Application Entry Point / Composition Root.
 *
 * Professional Architecture & Design Patterns:
 * - Adapter Pattern (integration): ExternalHRAdapter adapts third-party ExternalHRService
 *   to the EmployeeDirectory target interface, injected into PayrollServiceImpl.
 * - Factory Method Pattern (calculator): SalaryCalculatorFactory creators for salary calculation.
 * - Strategy Pattern (tax): TaxPolicy and DeductionPolicy for category-specific tax and deduction calculation.
 * - Builder Pattern (model): Validated immutable PayrollRequest construction.
 * - Singleton Pattern (config): Shared configuration provider.
 * - Facade Pattern (service): PayrollFacade orchestrates the monthly payroll workflow.
 */
public class PayrollSystem {
    private static final String DEFAULT_REPORT_FILE = "payroll_summary_report.txt";

    public static void main(String[] args) {
        // Core reporting dependency
        ReportExporter reportExporter = new FileReportExporter();

        // 1. Adapter Pattern Setup:
        // ExternalHRService is the Adaptee (simulating a third-party HR API)
        ExternalHRService externalHRService = new ExternalHRService();

        // ExternalHRAdapter implements the EmployeeDirectory Target interface
        EmployeeDirectory employeeDirectory = new ExternalHRAdapter(externalHRService);

        // 2. Inject EmployeeDirectory into PayrollService via Constructor Injection
        PayrollService payrollService = new PayrollServiceImpl(
                employeeDirectory,
                reportExporter,
                SalaryCalculatorFactory::forType
        );

        // 3. Create Facade for orchestrating monthly payroll workflow
        PayrollFacade payrollFacade = new PayrollFacade(payrollService, reportExporter);

        ConsoleView view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        PayrollCLI cli = new PayrollCLI(payrollFacade, payrollService, view, scanner, DEFAULT_REPORT_FILE);

        // Start Application
        try {
            cli.start();
        } finally {
            scanner.close();
        }
    }
}
