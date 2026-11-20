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
 * Refactored using SOLID Principles:
 * - Single Responsibility Principle (SRP): Acts solely as the composition root to wire together dependencies.
 * - Dependency Inversion Principle (DIP): High-level components depend on interfaces (EmployeeRepository, ReportExporter, PayrollService),
 *   and concrete implementations are injected here.
 * - Open-Closed Principle (OCP): New report exporters or repositories can be swapped in without modifying domain or UI code.
 */
public class PayrollSystem {
    private static final String DEFAULT_REPORT_FILE = "payroll_summary_report.txt";

    public static void main(String[] args) {
        // Dependency Injection & Configuration Setup
        EmployeeRepository repository = new InMemoryEmployeeRepository();
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
