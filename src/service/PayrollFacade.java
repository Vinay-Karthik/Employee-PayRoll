package service;

import model.Employee;
import model.PaySlip;
import report.ReportExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Facade for the monthly payroll processing workflow.
 *
 * <p>This class provides a simplified interface to the complex payroll subsystem by coordinating:
 * <ol>
 *   <li>Employee retrieval from the directory (via PayrollService)</li>
 *   <li>Salary, tax, and deduction calculation (delegated to {@link PayrollService})</li>
 *   <li>Payslip generation for all employees</li>
 *   <li>Summary report generation</li>
 *   <li>Report export to configured destination</li>
 * </ol>
 *
 * <p>Business logic remains in the domain services ({@link PayrollService}, {@link EmployeeDirectory},
 * {@link ReportExporter}). This facade only orchestrates the workflow and handles cross-cutting
 * concerns like error aggregation.
 *
 * <p>Usage example:
 * <pre>{@code
 * PayrollFacade facade = new PayrollFacade(payrollService, reportExporter);
 * PayrollBatchResult result = facade.processMonthlyPayroll("payroll_summary_report.txt");
 * // Presentation layer displays result
 * }</pre>
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
     * Executes the complete monthly payroll processing workflow.
     *
     * <p>Workflow steps (in order):
     * <ol>
     *   <li>Retrieve all active employees from the directory (via PayrollService)</li>
     *   <li>Generate payslips for each employee (calculating salary, tax, deductions)</li>
     *   <li>Generate a summary report from the successful payslips</li>
     *   <li>Export the report to the specified destination</li>
     * </ol>
     *
     * <p>Failures are handled gracefully:
     * <ul>
     *   <li>Individual employee payslip failures are collected without stopping the batch</li>
     *   <li>If no employees are found, returns a SUCCESS result with zero counts (no export)</li>
     *   <li>If no payslips are generated successfully, returns FAILURE status</li>
     *   <li>If report generation or export fails, returns FAILURE status with error details</li>
     *   <li>Employee retrieval failures are captured and returned as FAILURE result</li>
     * </ul>
     *
     * @param reportDestination the file path or destination identifier for the summary report
     * @return structured result containing status, counts, payslips, report, and any errors
     * @throws NullPointerException if reportDestination is null
     */
    public PayrollBatchResult processMonthlyPayroll(String reportDestination) {
        Objects.requireNonNull(reportDestination, "Report destination must not be null");

        // Step 1: Retrieve all employees (with failure handling)
        List<Employee> employees;
        try {
            employees = retrieveEmployees();
        } catch (PayrollProcessingException e) {
            LOGGER.log(Level.SEVERE, "Employee retrieval failed: " + e.getMessage(), e);
            return PayrollBatchResult.builder()
                    .status(PayrollBatchResult.Status.FAILURE)
                    .totalEmployees(0)
                    .successfulCount(0)
                    .failedCount(0)
                    .successfulPaySlips(List.of())
                    .failedEmployeeIds(List.of())
                    .errorMessage("Employee retrieval failed: " + e.getMessage())
                    // No report destination set since no export was attempted
                    .build();
        }

        if (employees.isEmpty()) {
            LOGGER.info("No employees found. Payroll processing completed with no payslips generated.");
            return PayrollBatchResult.builder()
                    .status(PayrollBatchResult.Status.SUCCESS)
                    .totalEmployees(0)
                    .successfulCount(0)
                    .failedCount(0)
                    .successfulPaySlips(List.of())
                    .failedEmployeeIds(List.of())
                    .summaryReport("")
                    // No report destination set - no export was attempted
                    .build();
        }

        LOGGER.info(() -> String.format("Processing monthly payroll for %d employee(s)...", employees.size()));

        // Step 2: Generate payslips for all employees
        PayslipGenerationResult generationResult = generatePayslipsForAllEmployees(employees);

        // Step 3: Handle case where no payslips were generated
        if (generationResult.successfulPaySlips().isEmpty()) {
            String errorMsg = "No payslips were generated successfully. Cannot generate summary report.";
            LOGGER.severe(() -> errorMsg + " Failed IDs: " + generationResult.failedEmployeeIds());
            return PayrollBatchResult.builder()
                    .status(PayrollBatchResult.Status.FAILURE)
                    .totalEmployees(employees.size())
                    .successfulCount(0)
                    .failedCount(generationResult.failedEmployeeIds().size())
                    .successfulPaySlips(List.of())
                    .failedEmployeeIds(generationResult.failedEmployeeIds())
                    .errorMessage(errorMsg)
                    // No report destination set
                    .build();
        }

        // Step 4: Generate summary report
        String summaryReport;
        try {
            summaryReport = payrollService.generatePayrollSummaryReport(generationResult.successfulPaySlips());
        } catch (RuntimeException e) {
            String errorMsg = "Failed to generate payroll summary report: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMsg, e);
            return PayrollBatchResult.builder()
                    .status(PayrollBatchResult.Status.FAILURE)
                    .totalEmployees(employees.size())
                    .successfulCount(generationResult.successfulPaySlips().size())
                    .failedCount(generationResult.failedEmployeeIds().size())
                    .successfulPaySlips(generationResult.successfulPaySlips())
                    .failedEmployeeIds(generationResult.failedEmployeeIds())
                    .errorMessage(errorMsg)
                    .reportDestination(reportDestination)
                    .build();
        }

        // Step 5: Export the report
        try {
            reportExporter.export(summaryReport, reportDestination);
            LOGGER.info(() -> "Monthly payroll completed successfully. Report exported to: " + reportDestination);
        } catch (IOException e) {
            String errorMsg = "Failed to export payroll summary report to " + reportDestination + ": " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMsg, e);
            return PayrollBatchResult.builder()
                    .status(PayrollBatchResult.Status.FAILURE)
                    .totalEmployees(employees.size())
                    .successfulCount(generationResult.successfulPaySlips().size())
                    .failedCount(generationResult.failedEmployeeIds().size())
                    .successfulPaySlips(generationResult.successfulPaySlips())
                    .failedEmployeeIds(generationResult.failedEmployeeIds())
                    .summaryReport(summaryReport)
                    .errorMessage(errorMsg)
                    .reportDestination(reportDestination)
                    .build();
        }

        // Determine final status
        PayrollBatchResult.Status finalStatus;
        if (generationResult.failedEmployeeIds().isEmpty()) {
            finalStatus = PayrollBatchResult.Status.SUCCESS;
        } else {
            finalStatus = PayrollBatchResult.Status.PARTIAL_SUCCESS;
        }

        return PayrollBatchResult.builder()
                .status(finalStatus)
                .totalEmployees(employees.size())
                .successfulCount(generationResult.successfulPaySlips().size())
                .failedCount(generationResult.failedEmployeeIds().size())
                .successfulPaySlips(generationResult.successfulPaySlips())
                .failedEmployeeIds(generationResult.failedEmployeeIds())
                .summaryReport(summaryReport)
                .reportDestination(reportDestination)
                .build();
    }

    /**
     * Retrieves all employees from the PayrollService.
     * Validates that the returned collection is non-null and contains no null entries.
     *
     * @return list of all employees (empty if none found)
     * @throws PayrollProcessingException if employee retrieval fails
     */
    private List<Employee> retrieveEmployees() {
        try {
            List<Employee> employees = payrollService.getAllEmployees();

            // Validate non-null contract
            if (employees == null) {
                throw new PayrollProcessingException(
                        "PayrollService.getAllEmployees() returned null instead of empty list",
                        List.of()
                );
            }

            // Validate no null entries
            for (int i = 0; i < employees.size(); i++) {
                if (employees.get(i) == null) {
                    throw new PayrollProcessingException(
                            "PayrollService.getAllEmployees() returned list containing null at index " + i,
                            List.of()
                    );
                }
            }

            return employees;
        } catch (PayrollProcessingException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new PayrollProcessingException(
                    "Failed to retrieve employees: " + e.getMessage(),
                    List.of(),
                    e
            );
        }
    }

    /**
     * Generates payslips for all employees, collecting successes and failures.
     *
     * @param employees list of employees to process
     * @return result containing successful payslips and failed employee IDs
     */
    private PayslipGenerationResult generatePayslipsForAllEmployees(List<Employee> employees) {
        List<PaySlip> successfulPaySlips = new ArrayList<>();
        List<Integer> failedEmployeeIds = new ArrayList<>();

        for (Employee employee : employees) {
            try {
                Optional<PaySlip> paySlipOpt = payrollService.processEmployeePaySlip(employee.getId());
                if (paySlipOpt.isPresent()) {
                    successfulPaySlips.add(paySlipOpt.get());
                    LOGGER.fine(() -> String.format("Generated payslip for employee %d (%s)",
                            employee.getId(), employee.getName()));
                } else {
                    failedEmployeeIds.add(employee.getId());
                    LOGGER.warning(() -> String.format("Employee %d (%s) not found in directory",
                            employee.getId(), employee.getName()));
                }
            } catch (RuntimeException e) {
                failedEmployeeIds.add(employee.getId());
                LOGGER.log(Level.WARNING, String.format("Failed to generate payslip for employee %d (%s): %s",
                        employee.getId(), employee.getName(), e.getMessage()), e);
            }
        }

        return new PayslipGenerationResult(successfulPaySlips, failedEmployeeIds);
    }

    /**
     * Internal record for payslip generation step result.
     */
    private record PayslipGenerationResult(
            List<PaySlip> successfulPaySlips,
            List<Integer> failedEmployeeIds
    ) {}
}