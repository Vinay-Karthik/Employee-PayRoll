package service;

import calculator.SalaryCalculator;
import calculator.SalaryCalculatorFactory;
import config.PayrollConfig;
import decorator.DecoratorType;
import decorator.SalaryEnhancementPipeline;
import integration.EmployeeDirectory;
import integration.RepositoryEmployeeDirectory;
import model.Employee;
import model.EmployeeType;
import model.FullTimeEmployee;
import model.PartTimeEmployee;
import model.ContractorEmployee;
import model.InternEmployee;
import model.PaySlip;
import model.PayrollRequest;
import report.ReportExporter;
import repository.EmployeeRepository;
import tax.DeductionCalculator;
import tax.DeductionPolicy;
import tax.PayrollComponentFactory;
import tax.TaxCalculator;
import tax.TaxPolicy;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concrete implementation of PayrollService.
 * Coordinates employee directory, domain calculations, and report generation.
 */
public class PayrollServiceImpl implements PayrollService {
    private final EmployeeDirectory employeeDirectory;
    private final ReportExporter reportExporter;
    private final Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver;

    /**
     * Primary constructor injecting EmployeeDirectory via constructor injection (Adapter Pattern).
     * EmployeeDirectory is the single authoritative persistence abstraction (it adapts EmployeeRepository).
     */
    public PayrollServiceImpl(EmployeeDirectory employeeDirectory,
                              ReportExporter reportExporter,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver) {
        this.employeeDirectory = Objects.requireNonNull(employeeDirectory, "EmployeeDirectory must not be null");
        this.reportExporter = Objects.requireNonNull(reportExporter, "Report exporter must not be null");
        this.salaryCalculatorFactoryResolver = Objects.requireNonNull(salaryCalculatorFactoryResolver, "Factory resolver must not be null");
    }

    public PayrollServiceImpl(EmployeeDirectory employeeDirectory,
                              ReportExporter reportExporter) {
        this(employeeDirectory, reportExporter, SalaryCalculatorFactory::forType);
    }

    /**
     * Convenience constructor for backward compatibility.
     * Uses RepositoryEmployeeDirectory as default adapter for EmployeeRepository.
     */
    public PayrollServiceImpl(EmployeeRepository repository, ReportExporter reportExporter) {
        this(new RepositoryEmployeeDirectory(repository), reportExporter);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDirectory.findAllEmployees();
    }

    @Override
    public Optional<Employee> getEmployeeById(int id) {
        return employeeDirectory.findEmployeeById(id);
    }

    @Override
    public void registerEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        employeeDirectory.saveEmployee(employee);
    }

    @Override
    public boolean removeEmployee(int id) {
        return employeeDirectory.deleteEmployee(id);
    }

    @Override
    public Optional<PaySlip> processEmployeePaySlip(int id) {
        return employeeDirectory.findEmployeeById(id).map(Employee::generatePaySlip);
    }

    @Override
    public List<PaySlip> processAllPaySlips() {
        return employeeDirectory.findAllEmployees().stream()
                .map(Employee::generatePaySlip)
                .toList();
    }

    @Override
    public PaySlip processPayrollRequest(PayrollRequest request) {
        Objects.requireNonNull(request, "Payroll request must not be null");

        String effectiveFinancialYear = (request.getFinancialYear() != null && !request.getFinancialYear().isBlank())
                ? request.getFinancialYear()
                : PayrollConfig.getInstance().getFinancialYear();

        SalaryCalculatorFactory creator = salaryCalculatorFactoryResolver.apply(request.getEmployeeType());
        SalaryCalculator salaryCalculator = creator.createCalculator();
        BigDecimal gross = salaryCalculator.calculateGross(request.getSalaryComponents());

        PayrollComponentFactory componentFactory = PayrollComponentFactory.forRegime(request.getTaxRegime());
        TaxCalculator taxCalculator = componentFactory.createTaxCalculator();
        DeductionCalculator deductionCalculator = componentFactory.createDeductionCalculator();

        BigDecimal tax = taxCalculator.calculateTax(gross);
        BigDecimal deductions = deductionCalculator.calculateDeductions(gross)
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
                request.getEmployeeType().name(),
                request.getBankAccountNumber(),
                gross, tax, deductions, net,
                effectiveFinancialYear,
                request.getBonusDetails()
        );
    }

    @Override
    public String generatePayrollSummaryReport(List<PaySlip> paySlips) {
        PayrollConfig config = PayrollConfig.getInstance();
        String currency = config.getCurrency();

        String reportFinancialYear = paySlips.stream()
                .map(PaySlip::financialYear)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(config.getFinancialYear());

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
    public void exportSummaryReport(String reportContent, String destination) throws IOException {
        reportExporter.export(reportContent, destination);
    }

    @Override
    public SalaryEnhancementPipeline createSalaryEnhancementPipeline(BigDecimal baseSalary) {
        return new SalaryEnhancementPipeline(baseSalary);
    }

    /**
     * Generates a PaySlip for an employee using the SalaryEnhancementPipeline.
     * This integrates the decorator-based salary enhancement flow with the standard
     * employee payslip generation, allowing dynamic application of HRA, allowances,
     * bonuses, and deductions on top of the employee's base salary.
     *
     * @param employeeId the ID of the employee
     * @param decoratorTypes the decorator types to apply (e.g., HRA, TRANSPORT_ALLOWANCE)
     * @return Optional containing the enhanced PaySlip, or empty if employee not found
     */
    @Override
    public Optional<PaySlip> generateEnhancedPaySlip(int employeeId, DecoratorType... decoratorTypes) {
        return employeeDirectory.findEmployeeById(employeeId).map(employee -> {
            // Initialize pipeline with employee's base salary
            BigDecimal baseSalary = extractBaseSalary(employee);
            SalaryEnhancementPipeline pipeline = new SalaryEnhancementPipeline(baseSalary);

            // Apply requested decorators with default amounts
            for (DecoratorType type : decoratorTypes) {
                pipeline.applyDecorator(type);
            }

            // Build PaySlip from pipeline results
            BigDecimal gross = pipeline.getCurrentSalary();
            BigDecimal tax = calculateTaxForEmployee(employee, gross);
            BigDecimal deductions = calculateDeductionsForEmployee(employee, gross);
            BigDecimal net = gross.subtract(tax).subtract(deductions);

            // Issue 4: Validate net pay is not negative
            if (net.signum() < 0) {
                throw new IllegalStateException(
                        "Calculated net pay is negative (" + net + "). " +
                        "Gross: " + gross + ", Tax: " + tax + ", Deductions: " + deductions
                );
            }

            return new PaySlip(
                    employee.getId(),
                    employee.getName(),
                    employee.getDepartment(),
                    employee.getRoleCategory(),
                    employee.getBankAccountNumber(),
                    gross, tax, deductions, net,
                    PayrollConfig.getInstance().getFinancialYear(),
                    pipeline.getCurrentDescription()
            );
        });
    }

    /**
     * Generates a PaySlip for an employee using the SalaryEnhancementPipeline
     * with custom amounts for specific decorators.
     *
     * @param employeeId the ID of the employee
     * @param decoratorCustomAmounts map of decorator types to custom amounts
     * @return Optional containing the enhanced PaySlip, or empty if employee not found
     */
    @Override
    public Optional<PaySlip> generateEnhancedPaySlip(int employeeId, java.util.Map<DecoratorType, BigDecimal> decoratorCustomAmounts) {
        return employeeDirectory.findEmployeeById(employeeId).map(employee -> {
            BigDecimal baseSalary = extractBaseSalary(employee);
            SalaryEnhancementPipeline pipeline = new SalaryEnhancementPipeline(baseSalary);

            for (java.util.Map.Entry<DecoratorType, BigDecimal> entry : decoratorCustomAmounts.entrySet()) {
                pipeline.applyDecorator(entry.getKey(), entry.getValue());
            }

            BigDecimal gross = pipeline.getCurrentSalary();
            BigDecimal tax = calculateTaxForEmployee(employee, gross);
            BigDecimal deductions = calculateDeductionsForEmployee(employee, gross);
            BigDecimal net = gross.subtract(tax).subtract(deductions);

            // Issue 4: Validate net pay is not negative
            if (net.signum() < 0) {
                throw new IllegalStateException(
                        "Calculated net pay is negative (" + net + "). " +
                        "Gross: " + gross + ", Tax: " + tax + ", Deductions: " + deductions
                );
            }

            return new PaySlip(
                    employee.getId(),
                    employee.getName(),
                    employee.getDepartment(),
                    employee.getRoleCategory(),
                    employee.getBankAccountNumber(),
                    gross, tax, deductions, net,
                    PayrollConfig.getInstance().getFinancialYear(),
                    pipeline.getCurrentDescription()
            );
        });
    }

    /**
     * Extracts the base salary from an employee for the enhancement pipeline.
     * Uses the employee's full gross pay (including bonus, overtime, etc.) as the starting point
     * for the decorator pipeline, so that enhancements are applied on top of the complete
     * standard compensation.
     *
     * @param employee the employee to extract base salary from
     * @return the employee's standard gross pay
     * @throws IllegalArgumentException if employee type is unsupported
     */
    private BigDecimal extractBaseSalary(Employee employee) {
        // Use the employee's own calculateGrossPay() which includes all standard components:
        // - FullTimeEmployee: baseSalary + bonus
        // - PartTimeEmployee: regular pay + overtime pay
        // - ContractorEmployee: hourlyRate * hoursWorked
        // - InternEmployee: stipend
        return employee.calculateGrossPay();
    }

    /**
     * Calculates tax for an employee based on their type and the enhanced gross salary.
     * Delegates to the shared {@link TaxPolicy} for the employee's type.
     */
    private BigDecimal calculateTaxForEmployee(Employee employee, BigDecimal enhancedGross) {
        return tax.TaxPolicy.forEmployee(employee).calculateTax(enhancedGross);
    }

    /**
     * Calculates deductions for an employee based on their type and the enhanced gross salary.
     * Delegates to the shared {@link DeductionPolicy} for the employee's type.
     */
    private BigDecimal calculateDeductionsForEmployee(Employee employee, BigDecimal enhancedGross) {
        return tax.DeductionPolicy.forEmployee(employee).calculateDeductions(enhancedGross);
    }
}
