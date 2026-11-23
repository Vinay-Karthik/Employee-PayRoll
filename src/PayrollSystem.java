import calculator.SalaryCalculatorFactory;
import report.FileReportExporter;
import report.ReportExporter;
import repository.EmployeeRepository;
import repository.InMemoryEmployeeRepository;
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
 * - Repository Pattern (repository): InMemoryEmployeeRepository provides clean domain employee storage.
 * - Adapter Pattern (integration): ExternalHRAdapter adapts third-party ExternalHRService
 *   to EmployeeRepository as a standalone integration demonstration.
 * - Factory Method Pattern (calculator): SalaryCalculatorFactory creators for salary calculation.
 * - Strategy Pattern (tax): TaxPolicy and DeductionPolicy for category-specific tax and deduction calculation.
 * - Builder Pattern (model): Validated immutable PayrollRequest construction.
 * - Facade Pattern (service): PayrollFacade coordinates the monthly payroll workflow and owns report exporting.
 */
public class PayrollSystem {
    private static final String DEFAULT_REPORT_FILE = "payroll_summary_report.txt";
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String DEFAULT_FINANCIAL_YEAR = "FY2025-26";

    public static void main(String[] args) {
        // 1. Employee Repository Setup (Domain storage decoupled from external systems):
        EmployeeRepository employeeRepository = new InMemoryEmployeeRepository();

        // 2. Inject EmployeeRepository and configuration into PayrollService via Constructor Injection
        PayrollService payrollService = new PayrollServiceImpl(
                employeeRepository,
                SalaryCalculatorFactory::forType,
                DEFAULT_CURRENCY,
                DEFAULT_FINANCIAL_YEAR
        );

        // 3. Facade coordinates monthly payroll and owns the ReportExporter dependency
        ReportExporter reportExporter = new FileReportExporter();
        PayrollFacade payrollFacade = new PayrollFacade(payrollService, reportExporter);

        ConsoleView view = new ConsoleView(DEFAULT_CURRENCY);
        Scanner scanner = new Scanner(System.in);

        PayrollCLI cli = new PayrollCLI(payrollFacade, payrollService, view, scanner, DEFAULT_REPORT_FILE, DEFAULT_FINANCIAL_YEAR);

        // Start Application
        try {
            cli.start();
        } finally {
            scanner.close();
        }
    }
}
