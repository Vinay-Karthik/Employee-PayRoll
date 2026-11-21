package service;

import integration.EmployeeDirectory;
import model.Employee;
import model.PaySlip;
import report.ReportExporter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Facade for the monthly payroll processing workflow.
 *
 * <p>This class provides a simplified interface to the complex payroll subsystem by coordinating:
 * <ol>
 *   <li>Employee retrieval from the directory</li>
 *   <li>Salary, tax, and deduction calculation (delegated to {@link PayrollService})</li>
 *   <li>Payslip generation for all employees</li>
 *   <li>Summary report generation</li>
 *   <li>Report export to configured destination</li>
 * </ol>
 *
 * <p>Business logic remains in the domain services ({@link PayrollService}, {@link EmployeeDirectory},
 * {@link ReportExporter}). This facade only orchestrates the workflow and handles cross-cutting
 * concerns like error aggregation and transactional boundaries.
 *
 * <p>Usage example:
 * <pre>{@code
 * PayrollFacade facade = new PayrollFacade(payrollService, employeeDirectory, reportExporter);
 * facade.processMonthlyPayroll("payroll_summary_report.txt");
 * }</pre>
 */
public class PayrollFacade {

    private final PayrollService payrollService;
    private final EmployeeDirectory employeeDirectory;
    private final ReportExporter reportExporter;

    /**
     * Creates a new PayrollFacade with the required subsystem dependencies.
     *
     * @param payrollService     the payroll service for calculation and payslip generation
     * @param employeeDirectory  the employee directory for retrieving employees
     * @param reportExporter     the report exporter for outputting the summary report
     * @throws NullPointerException if any argument is null
     */
    public PayrollFacade(PayrollService payrollService,
                         EmployeeDirectory employeeDirectory,
                         ReportExporter reportExporter) {
        this.payrollService = Objects.requireNonNull(payrollService, "PayrollService must not be null");
        this.employeeDirectory = Objects.requireNonNull(employeeDirectory, "EmployeeDirectory must not be null");
        this.reportExporter = Objects.requireNonNull(reportExporter, "ReportExporter must not be null");
    }

    /**
     * Executes the complete monthly payroll processing workflow.
     *
     * <p>Workflow steps (in order):
     * <ol>
     *   <li>Retrieve all active employees from the directory</li>
     *   <li>Generate payslips for each employee (calculating salary, tax, deductions)</li>
     *   <li>Validate that all payslips were generated successfully</li>
     *   <li>Generate a summary report from the payslips</li>
     *   <li>Export the report to the specified destination</li>
     * </ol>
     *
     * <p>Failures are handled gracefully:
     * <ul>
     *   <li>Individual employee payslip failures are collected and reported without stopping the batch</li>
     *   <li>If no employees are found, the process completes with a warning (not an error)</li>
     *   <li>If report generation or export fails, a {@link PayrollProcessingException} is thrown with context</li>
     * </ul>
     *
     * @param reportDestination the file path or destination identifier for the summary report
     * @throws PayrollProcessingException if a critical failure occurs (report generation/export)
     * @throws NullPointerException       if reportDestination is null
     */
    public void processMonthlyPayroll(String reportDestination) {
        Objects.requireNonNull(reportDestination, "Report destination must not be null");

        // Step 1: Retrieve all employees
        List<Employee> employees = retrieveEmployees();
        if (employees.isEmpty()) {
            System.out.println("[PayrollFacade] WARNING: No employees found. Payroll processing completed with no payslips generated.");
            return;
        }

        System.out.printf("[PayrollFacade] Processing monthly payroll for %d employee(s)...%n", employees.size());

        // Step 2: Generate payslips for all employees
        PayrollBatchResult batchResult = generatePayslipsForAllEmployees(employees);

        // Step 3: Report on any individual failures
        if (!batchResult.failedEmployeeIds().isEmpty()) {
            System.err.printf("[PayrollFacade] WARNING: Failed to generate payslips for %d employee(s): %s%n",
                    batchResult.failedEmployeeIds().size(), batchResult.failedEmployeeIds());
        }

        if (batchResult.successfulPaySlips().isEmpty()) {
            throw new PayrollProcessingException(
                    "No payslips were generated successfully. Cannot generate summary report.",
                    batchResult.failedEmployeeIds()
            );
        }

        // Step 4: Generate summary report
        String summaryReport;
        try {
            summaryReport = payrollService.generatePayrollSummaryReport(batchResult.successfulPaySlips());
        } catch (Exception e) {
            throw new PayrollProcessingException(
                    "Failed to generate payroll summary report",
                    batchResult.failedEmployeeIds(),
                    e
            );
        }

        // Step 5: Export the report
        try {
            reportExporter.export(summaryReport, reportDestination);
            System.out.printf("[PayrollFacade] Monthly payroll completed successfully. Report exported to: %s%n", reportDestination);
        } catch (IOException e) {
            throw new PayrollProcessingException(
                    "Failed to export payroll summary report to " + reportDestination,
                    batchResult.failedEmployeeIds(),
                    e
            );
        }
    }

    /**
     * Retrieves all employees from the directory.
     *
     * @return list of all employees (empty if none found)
     * @throws PayrollProcessingException if employee retrieval fails
     */
    private List<Employee> retrieveEmployees() {
        try {
            return employeeDirectory.findAllEmployees();
        } catch (Exception e) {
            throw new PayrollProcessingException("Failed to retrieve employees from directory", List.of(), e);
        }
    }

    /**
     * Generates payslips for all employees, collecting successes and failures.
     *
     * @param employees list of employees to process
     * @return result containing successful payslips and failed employee IDs
     */
    private PayrollBatchResult generatePayslipsForAllEmployees(List<Employee> employees) {
        List<PaySlip> successfulPaySlips = new java.util.ArrayList<>();
        List<Integer> failedEmployeeIds = new java.util.ArrayList<>();

        for (Employee employee : employees) {
            try {
                Optional<PaySlip> paySlipOpt = payrollService.processEmployeePaySlip(employee.getId());
                if (paySlipOpt.isPresent()) {
                    successfulPaySlips.add(paySlipOpt.get());
                    System.out.printf("[PayrollFacade] Generated payslip for employee %d (%s)%n",
                            employee.getId(), employee.getName());
                } else {
                    failedEmployeeIds.add(employee.getId());
                    System.err.printf("[PayrollFacade] Employee %d (%s) not found in directory%n",
                            employee.getId(), employee.getName());
                }
            } catch (Exception e) {
                failedEmployeeIds.add(employee.getId());
                System.err.printf("[PayrollFacade] Failed to generate payslip for employee %d (%s): %s%n",
                        employee.getId(), employee.getName(), e.getMessage());
            }
        }

        return new PayrollBatchResult(successfulPaySlips, failedEmployeeIds);
    }

    /**
     * Immutable result of a batch payslip generation operation.
     */
    private record PayrollBatchResult(
            List<PaySlip> successfulPaySlips,
            List<Integer> failedEmployeeIds
    ) {}
}