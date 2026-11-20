import report.FileReportExporter;
import report.ReportExporter;
import repository.CsvEmployeeRepository;
import repository.EmployeeRepository;
import service.PayrollService;
import service.PayrollServiceImpl;
import ui.ConsoleView;
import ui.PayrollCLI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Application Entry Point / Composition Root.
 *
 * Refactored using SOLID Principles:
 * - Single Responsibility Principle (SRP): Acts solely as the composition root to wire together dependencies.
 * - Dependency Inversion Principle (DIP): High-level components depend on interfaces (EmployeeRepository, ReportExporter, PayrollService),
 *   and concrete implementations are injected here.
 * - Open-Closed Principle (OCP): New report exporters or repositories can be swapped in without modifying domain or UI code.
 */
public class PayrollSystem {
    private static final String DEFAULT_DATA_FILE = "employees.csv";
    private static final String DEFAULT_REPORT_FILE = "payroll_summary_report.txt";

    public static void main(String[] args) {
        // Dependency Injection & Configuration Setup
        Path dataFilePath = Paths.get(DEFAULT_DATA_FILE);
        EmployeeRepository repository = new CsvEmployeeRepository(dataFilePath);
        ReportExporter reportExporter = new FileReportExporter();
        PayrollService payrollService = new PayrollServiceImpl(repository, reportExporter);

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
