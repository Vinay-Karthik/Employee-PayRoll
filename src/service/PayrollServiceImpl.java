package service;

import calculator.SalaryCalculator;
import calculator.SalaryCalculatorFactory;
import decorator.SalaryEnhancementPipeline;
import model.Employee;
import model.EmployeeType;
import model.PaySlip;
import model.PayrollRequest;
import repository.EmployeeRepository;
import tax.DeductionPolicy;
import tax.TaxPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concrete implementation of PayrollService.
 * Coordinates employee repository, domain calculations, and summary report formatting.
 * Report exporting is delegated exclusively to PayrollFacade.
 */
public class PayrollServiceImpl implements PayrollService {
    private final EmployeeRepository employeeRepository;
    private final Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver;
    private final String currency;
    private final String financialYear;

    /**
     * Primary constructor injecting dependencies via constructor injection.
     * Configuration parameters (currency, financialYear) are injected directly.
     */
    public PayrollServiceImpl(EmployeeRepository employeeRepository,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver,
                              String currency,
                              String financialYear) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository must not be null");
        this.salaryCalculatorFactoryResolver = Objects.requireNonNull(salaryCalculatorFactoryResolver, "Factory resolver must not be null");
        this.currency = (currency != null && !currency.isBlank()) ? currency : "USD";
        this.financialYear = (financialYear != null && !financialYear.isBlank()) ? financialYear : "FY2025-26";
    }

    public PayrollServiceImpl(EmployeeRepository employeeRepository,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver) {
        this(employeeRepository, salaryCalculatorFactoryResolver, "USD", "FY2025-26");
    }

    public PayrollServiceImpl(EmployeeRepository employeeRepository) {
        this(employeeRepository, SalaryCalculatorFactory::forType, "USD", "FY2025-26");
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(int id) {
        return employeeRepository.findById(id);
    }

    @Override
    public void registerEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        employeeRepository.save(employee);
    }

    @Override
    public boolean removeEmployee(int id) {
        return employeeRepository.deleteById(id);
    }

    @Override
    public Optional<PaySlip> processEmployeePaySlip(int id) {
        return employeeRepository.findById(id).map(Employee::generatePaySlip);
    }

    @Override
    public List<PaySlip> processAllPaySlips() {
        return employeeRepository.findAll().stream()
                .map(Employee::generatePaySlip)
                .toList();
    }

    @Override
    public PaySlip processPayrollRequest(PayrollRequest request) {
        Objects.requireNonNull(request, "Payroll request must not be null");

        String effectiveFinancialYear = (request.getFinancialYear() != null && !request.getFinancialYear().isBlank())
                ? request.getFinancialYear()
                : this.financialYear;

        SalaryCalculatorFactory creator = salaryCalculatorFactoryResolver.apply(request.getEmployeeType());
        SalaryCalculator salaryCalculator = creator.createCalculator();
        BigDecimal gross = salaryCalculator.calculateGross(request.getSalaryComponents());

        TaxPolicy taxPolicy = TaxPolicy.forEmployeeType(request.getEmployeeType());
        DeductionPolicy deductionPolicy = DeductionPolicy.forEmployeeType(request.getEmployeeType());

        // Respect the selected tax regime (OLD or NEW) from request
        BigDecimal tax = taxPolicy.calculateTax(gross, request.getTaxRegime());
        BigDecimal deductions = deductionPolicy.calculateDeductions(gross)
                .add(request.totalOptionalDeductions());
        BigDecimal net = gross.subtract(tax).subtract(deductions);

        // Issue 4: Validate net pay is not negative
        if (net.signum() < 0) {
            throw new IllegalStateException(
                    "Calculated net pay is negative (" + net + "). " +
                    "Gross: " + gross + ", Tax: " + tax + ", Deductions: " + deductions +
                    ". Deductions exceed payable salary."
            );
        }

        return new PaySlip(
                request.getEmployeeId(),
                request.getEmployeeName(),
                request.getDepartment(),
                request.getEmployeeType().getDisplayName(),
                request.getBankAccountNumber(),
                gross, tax, deductions, net,
                effectiveFinancialYear,
                request.getBonusDetails()
        );
    }

    @Override
    public String generatePayrollSummaryReport(List<PaySlip> paySlips) {
        String reportFinancialYear = paySlips.stream()
                .map(PaySlip::financialYear)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(this.financialYear);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append(String.format("               PAYROLL SUMMARY REPORT - %s (%s)               \n",
                reportFinancialYear, currency));
        sb.append("========================================================================================================\n");
        sb.append(String.format("%-5s | %-16s | %-18s | %-10s | %-12s | %-12s | %-12s | %-12s\n",
                "ID", "Name", "Role Category", "FY", "Gross", "Tax", "Deduction", "Net Pay"));
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        for (PaySlip slip : paySlips) {
            totalGross = totalGross.add(slip.grossPay());
            totalTax = totalTax.add(slip.taxWithholding());
            totalDeductions = totalDeductions.add(slip.deductions());
            totalNet = totalNet.add(slip.netPay());

            sb.append(String.format("%-5d | %-16s | %-18s | %-10s | %s %-9.2f | %s %-9.2f | %s %-9.2f | %s %-9.2f\n",
                    slip.employeeId(), slip.employeeName(), slip.roleCategory(), slip.financialYear(),
                    currency, slip.grossPay(), currency, slip.taxWithholding(),
                    currency, slip.deductions(), currency, slip.netPay()));
        }

        sb.append("========================================================================================================\n");
        sb.append(String.format("TOTALS: Gross: %s %.2f | Tax: %s %.2f | Deductions: %s %.2f | Total Disbursed: %s %.2f\n",
                currency, totalGross, currency, totalTax, currency, totalDeductions, currency, totalNet));
        sb.append("========================================================================================================\n");

        return sb.toString();
    }

    @Override
    public SalaryEnhancementPipeline createSalaryEnhancementPipeline(BigDecimal baseSalary) {
        return new SalaryEnhancementPipeline(baseSalary);
    }
}
