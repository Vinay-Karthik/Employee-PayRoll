package service;

import model.PaySlip;
import report.ReportExporter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Facade Pattern: Simplifies payroll operations.
 *
 * <p>Provides a unified, high-level interface to coordinate the payroll subsystem:
 * generating employee payslips, compiling the summary report, and exporting it to file.
 */
public class PayrollFacade {

    private static final Logger LOGGER = Logger.getLogger(PayrollFacade.class.getName());

    private final PayrollService payrollService;
    private final ReportExporter reportExporter;

    /**
     * Creates a new PayrollFacade with the required subsystem dependencies.
     *
     * @param payrollService the payroll service for calculation and payslip generation
     * @param reportExporter the report exporter for outputting the summary report
     * @throws NullPointerException if any argument is null
     */
    public PayrollFacade(PayrollService payrollService, ReportExporter reportExporter) {
        this.payrollService = Objects.requireNonNull(payrollService, "PayrollService must not be null");
        this.reportExporter = Objects.requireNonNull(reportExporter, "ReportExporter must not be null");
    }

    /**
     * Executes the monthly payroll processing workflow:
     * <ol>
     *   <li>Retrieves and processes payslips for all active employees.</li>
     *   <li>Generates the payroll summary report.</li>
     *   <li>Exports the summary report to the specified file destination.</li>
     * </ol>
     *
     * @param reportDestination the destination path for the report file
     * @return the generated summary report, or an empty string if no employees exist
     * @throws IOException if report export fails
     */
    public String processMonthlyPayroll(String reportDestination) throws IOException {
        Objects.requireNonNull(reportDestination, "Report destination must not be null");

        List<PaySlip> paySlips = payrollService.processAllPaySlips();
        if (paySlips.isEmpty()) {
            LOGGER.info("No employees found. Payroll processing completed with no payslips generated.");
            return "";
        }

        LOGGER.info(() -> String.format("Processing monthly payroll for %d employee(s)...", paySlips.size()));
        String summaryReport = payrollService.generatePayrollSummaryReport(paySlips);

        reportExporter.export(summaryReport, reportDestination);
        LOGGER.info(() -> "Monthly payroll completed successfully. Report exported to: " + reportDestination);

        return summaryReport;
    }
}
