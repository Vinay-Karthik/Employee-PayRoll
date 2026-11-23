package ui.command;

import service.PayrollFacade;
import ui.view.ConsoleView;

import java.io.IOException;

/**
 * Command to process full payroll and export report.
 */
public class FullPayrollCommand implements MenuCommand {
    private final PayrollFacade payrollFacade;
    private final ConsoleView view;
    private final String reportFilePath;

    public FullPayrollCommand(PayrollFacade payrollFacade, ConsoleView view, String reportFilePath) {
        this.payrollFacade = payrollFacade;
        this.view = view;
        this.reportFilePath = reportFilePath;
    }

    @Override
    public boolean execute() {
        try {
            view.displayMessage("\nProcessing monthly payroll...");
            String report = payrollFacade.processMonthlyPayroll(reportFilePath);
            if (report == null || report.isBlank()) {
                view.displayMessage("No employees found in the system. No report exported.");
                return true;
            }
            view.displayReport(report);
            view.displayMessage("Successfully exported report to: " + reportFilePath);
        } catch (IOException e) {
            view.displayError("Failed to export payroll summary report to " + reportFilePath + ": " + e.getMessage());
        } catch (RuntimeException e) {
            view.displayError("Payroll processing failed: " + e.getMessage());
        }
        return true;
    }
}
