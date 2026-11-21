package service;

import decorator.DecoratorType;
import decorator.SalaryEnhancementPipeline;
import model.Employee;
import model.PaySlip;
import model.PayrollRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * High-level business contract for payroll operations.
 * Decoupled from UI controllers and concrete data stores.
 */
public interface PayrollService {
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(int id);
    void registerEmployee(Employee employee);
    boolean removeEmployee(int id);
    Optional<PaySlip> processEmployeePaySlip(int id);
    List<PaySlip> processAllPaySlips();

    /**
     * Processes a PayrollRequest built via the Builder pattern.
     */
    PaySlip processPayrollRequest(PayrollRequest request);

    String generatePayrollSummaryReport(List<PaySlip> paySlips);
    void exportSummaryReport(String reportContent, String destination) throws IOException;

    /**
     * Creates a new dynamic salary enhancement pipeline for applying decorators.
     * Enforces single-use decorator constraints and deduction limits outside the UI.
     *
     * @param baseSalary initial base salary
     * @return initialized SalaryEnhancementPipeline
     */
    SalaryEnhancementPipeline createSalaryEnhancementPipeline(BigDecimal baseSalary);

    /**
     * Generates a PaySlip for an employee using the SalaryEnhancementPipeline.
     * Integrates the decorator-based salary enhancement flow with standard payslip generation,
     * allowing dynamic application of HRA, allowances, bonuses, and deductions.
     *
     * @param employeeId the ID of the employee
     * @param decoratorTypes the decorator types to apply (e.g., HRA, TRANSPORT_ALLOWANCE)
     * @return Optional containing the enhanced PaySlip, or empty if employee not found
     */
    Optional<PaySlip> generateEnhancedPaySlip(int employeeId, DecoratorType... decoratorTypes);

    /**
     * Generates a PaySlip for an employee using the SalaryEnhancementPipeline
     * with custom amounts for specific decorators.
     *
     * @param employeeId the ID of the employee
     * @param decoratorCustomAmounts map of decorator types to custom amounts
     * @return Optional containing the enhanced PaySlip, or empty if employee not found
     */
    Optional<PaySlip> generateEnhancedPaySlip(int employeeId, Map<DecoratorType, BigDecimal> decoratorCustomAmounts);
}
