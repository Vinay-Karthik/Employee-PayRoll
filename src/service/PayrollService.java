package service;

import model.entity.Employee;
import model.value.PaySlip;
import model.request.PayrollRequest;

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
}
