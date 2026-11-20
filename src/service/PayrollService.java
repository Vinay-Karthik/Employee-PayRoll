package service;

import creational.builder.PayrollRequest;
import model.Employee;
import model.PaySlip;

import java.io.IOException;
import java.util.List;
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
     * Uses the Factory Method (salary calculator) and Abstract Factory
     * (tax regime components) creational patterns internally.
     */
    PaySlip processPayrollRequest(PayrollRequest request);

    String generatePayrollSummaryReport(List<PaySlip> paySlips);
    void exportSummaryReport(String reportContent, String destination) throws IOException;
}
