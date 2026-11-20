package service;

import model.Employee;
import model.PaySlip;
import report.ReportExporter;
import repository.EmployeeRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Concrete implementation of PayrollService.
 * Coordinates repository, domain calculations, and report generation.
 */
public class PayrollServiceImpl implements PayrollService {
    private final EmployeeRepository repository;
    private final ReportExporter reportExporter;

    public PayrollServiceImpl(EmployeeRepository repository, ReportExporter reportExporter) {
        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
        this.reportExporter = Objects.requireNonNull(reportExporter, "Report exporter must not be null");
    }

    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(int id) {
        return repository.findById(id);
    }

    @Override
    public void registerEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        repository.save(employee);
    }

    @Override
    public boolean removeEmployee(int id) {
        return repository.deleteById(id);
    }

    @Override
    public Optional<PaySlip> processEmployeePaySlip(int id) {
        return repository.findById(id).map(Employee::generatePaySlip);
    }

    @Override
    public List<PaySlip> processAllPaySlips() {
        return repository.findAll().stream()
                .map(Employee::generatePaySlip)
                .toList();
    }

    @Override
    public String generatePayrollSummaryReport(List<PaySlip> paySlips) {
        double totalGross = 0.0;
        double totalTax = 0.0;
        double totalDeductions = 0.0;
        double totalNet = 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                               MONTHLY PAYROLL SUMMARY REPORT                           \n");
        sb.append("========================================================================================\n");
        sb.append(String.format("%-5s | %-16s | %-22s | %-10s | %-10s | %-10s | %-10s\n",
                "ID", "Name", "Role Category", "Gross", "Tax", "Deduction", "Net Pay"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (PaySlip slip : paySlips) {
            totalGross += slip.grossPay();
            totalTax += slip.taxWithholding();
            totalDeductions += slip.deductions();
            totalNet += slip.netPay();

            sb.append(String.format("%-5d | %-16s | %-22s | $%-9.2f | $%-9.2f | $%-9.2f | $%-9.2f\n",
                    slip.employeeId(), slip.employeeName(), slip.roleCategory(),
                    slip.grossPay(), slip.taxWithholding(), slip.deductions(), slip.netPay()));
        }

        sb.append("========================================================================================\n");
        sb.append(String.format("TOTALS: Gross: $%.2f | Tax: $%.2f | Deductions: $%.2f | Total Disbursed: $%.2f\n",
                totalGross, totalTax, totalDeductions, totalNet));
        sb.append("========================================================================================\n");

        return sb.toString();
    }

    @Override
    public void exportSummaryReport(String reportContent, String destination) throws IOException {
        reportExporter.export(reportContent, destination);
    }
}
