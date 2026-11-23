package service;

import decorator.SalaryEnhancementPipeline;
import model.Employee;
import model.PaySlip;
import model.PayrollRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * High-level business contract for payroll operations.
 * Decoupled from presentation controllers, concrete data stores, and export mechanisms.
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

    /**
     * Creates a new dynamic salary enhancement pipeline for applying decorators.
     * Enforces single-use decorator constraints and deduction limits outside the UI.
     *
     * @param baseSalary initial base salary
     * @return initialized SalaryEnhancementPipeline
     */
    SalaryEnhancementPipeline createSalaryEnhancementPipeline(BigDecimal baseSalary);
}
