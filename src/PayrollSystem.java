import creational.factorymethod.SalaryCalculatorFactory;
import report.FileReportExporter;
import report.ReportExporter;
import repository.EmployeeRepository;
import repository.InMemoryEmployeeRepository;
import service.PayrollService;
import service.PayrollServiceImpl;
import structural.adapter.EmployeeDirectory;
import structural.adapter.ExternalHRAdapter;
import structural.adapter.ExternalHRService;
import structural.adapter.RepositoryEmployeeDirectory;
import ui.ConsoleView;
import ui.PayrollCLI;

import java.util.Scanner;

/**
 * Application Entry Point / Composition Root.
 *
 * Demonstrates:
 * - Structural Adapter Pattern: ExternalHRAdapter adapts third-party ExternalHRService
 *   to the EmployeeDirectory target interface, which is injected into PayrollServiceImpl.
 * - Pluggability: PayrollServiceImpl can seamlessly switch between ExternalHRAdapter
 *   and RepositoryEmployeeDirectory with ZERO changes to the payroll service.
 * - GoF Creational Patterns: Factory Method, Abstract Factory, Builder, Prototype, Singleton.
 */
public class PayrollSystem {
    private static final String DEFAULT_REPORT_FILE = "payroll_summary_report.txt";

    public static void main(String[] args) {
        // Core persistence and reporting dependencies
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        ReportExporter reportExporter = new FileReportExporter();

        // 1. Adapter Pattern Setup:
        // ExternalHRService is the Adaptee (simulating a third-party HR API)
        ExternalHRService externalHRService = new ExternalHRService();

        // ExternalHRAdapter implements the EmployeeDirectory Target interface
        EmployeeDirectory employeeDirectory = new ExternalHRAdapter(externalHRService);

        // 2. Inject EmployeeDirectory into PayrollService via Constructor Injection
        PayrollService payrollService = new PayrollServiceImpl(
                employeeDirectory,
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
